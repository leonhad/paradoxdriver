/*
 * Copyright (C) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.data;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.data.filefilters.TableFilter;
import com.googlecode.paradox.exceptions.ParadoxDataException;
import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.metadata.ParadoxTable;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.utils.Constants;
import com.googlecode.paradox.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.*;

/**
 * Utility class for loading table files.
 *
 * @version 1.5
 * @since 1.0
 */
public final class TableData extends ParadoxData {

    /**
     * Utility class.
     */
    private TableData() {
        super();
    }

    /**
     * List all database tables.
     *
     * @param schema     the current schema name.
     * @param connection the database connection.
     * @return all {@link ParadoxTable}.
     * @throws SQLException in case of failures.
     */
    public static List<ParadoxTable> listTables(final String schema, final ParadoxConnection connection) throws
            SQLException {
        final ArrayList<ParadoxTable> tables = new ArrayList<>();
        final File currentSchema = new File(connection.getCurrentSchema().getParent(), schema);

        final File[] fileList = currentSchema.listFiles(new TableFilter(connection.getLocale()));
        if (fileList != null) {
            for (final File file : fileList) {
                final ParadoxTable table = TableData.loadTableHeader(file, connection);
                tables.add(table);
            }
        }
        return tables;
    }

    /**
     * Gets all tables within a pattern.
     *
     * @param schema     the schema directory.
     * @param pattern    the pattern.
     * @param connection the database connection.
     * @return the tables filtered.
     * @throws SQLException in case of failures.
     */
    public static List<ParadoxTable> listTables(final File schema, final String pattern,
                                                final ParadoxConnection connection) throws SQLException {
        final List<ParadoxTable> tables = new ArrayList<>();
        final File[] fileList = schema.listFiles
                (new TableFilter(connection.getLocale(), Utils.removeSuffix(pattern, "DB")));

        if (fileList != null) {
            Arrays.sort(fileList);
            for (final File file : fileList) {
                final ParadoxTable table = TableData.loadTableHeader(file, connection);
                tables.add(table);
            }
        }
        return tables;
    }

    /**
     * Load the table data from file.
     *
     * @param table  the table to read.
     * @param fields the fields to read.
     * @return the row values.
     * @throws SQLException in case of failures.
     */
    public static List<Object[]> loadData(final ParadoxTable table, final ParadoxField[] fields)
            throws SQLException {

        final int blockSize = table.getBlockSizeBytes();
        final int recordSize = table.getRecordSize();
        final int headerSize = table.getHeaderSize();

        try (final FileInputStream fs = new FileInputStream(table.getFile());
             final FileChannel channel = fs.getChannel()) {
            if (table.getUsedBlocks() == 0) {
                return Collections.emptyList();
            }

            final List<Object[]> ret = new ArrayList<>(table.getRowCount());
            long nextBlock = table.getFirstBlock();

            final ByteBuffer buffer = ByteBuffer.allocate(blockSize);
            do {
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                long position = headerSize + ((nextBlock - 1) * blockSize);
                channel.position(position);

                buffer.clear();
                channel.read(buffer);
                checkDBEncryption(buffer, table, blockSize, nextBlock);
                buffer.flip();

                nextBlock = buffer.getShort() & 0xFFFF;

                // The block number.
                buffer.getShort();

                final int addDataSize = buffer.getShort();
                final int rowsInBlock = (addDataSize / recordSize) + 1;

                buffer.order(ByteOrder.BIG_ENDIAN);

                for (int loop = 0; loop < rowsInBlock; loop++) {
                    ret.add(TableData.readRow(table, fields, buffer));
                }
            } while (nextBlock != 0);

            return ret;
        } catch (final IOException e) {
            throw new ParadoxDataException(ParadoxDataException.Error.ERROR_LOADING_DATA, e);
        }
    }

    /**
     * Fix the buffer position based on file version ID.
     *
     * @param table      the Paradox table.
     * @param buffer     the buffer to fix.
     * @param fieldsSize the field list.
     */
    private static void fixTablePositionByVersion(final ParadoxTable table, final ByteBuffer buffer,
                                                  final int fieldsSize) {
        if (table.getVersionId() > Constants.PARADOX_VERSION_4) {
            if (table.getVersionId() == 0xC) {
                buffer.position(0x78 + 261 + 4 + (6 * fieldsSize));
            } else {
                buffer.position(0x78 + 83 + (6 * fieldsSize));
            }
        } else {
            buffer.position(0x58 + 83 + (6 * fieldsSize));
        }
    }

    /**
     * Gets the table header from a file.
     *
     * @param file       the {@link File} to read.
     * @param connection the database connection.
     * @return the {@link ParadoxTable}.
     * @throws SQLException in case of reading errors.
     */
    private static ParadoxTable loadTableHeader(final File file, final ParadoxConnection connection) throws
            SQLException {
        final ParadoxTable table = new ParadoxTable(file, file.getName(), connection);

        ByteBuffer buffer = ByteBuffer.allocate(Constants.MAX_BUFFER_SIZE);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        try (FileInputStream fs = new FileInputStream(file); FileChannel channel = fs.getChannel()) {
            channel.read(buffer);
            buffer.flip();

            table.setRecordSize(buffer.getShort() & 0xFFFF);
            table.setHeaderSize(buffer.getShort() & 0xFFFF);
            table.setType(buffer.get());
            table.setBlockSize(buffer.get());
            table.setRowCount(buffer.getInt());
            table.setUsedBlocks(buffer.getShort());
            table.setTotalBlocks(buffer.getShort());
            table.setFirstBlock(buffer.getShort());
            table.setLastBlock(buffer.getShort());

            buffer.position(0x21);
            table.setFieldCount(buffer.getShort());
            table.setPrimaryFieldCount(buffer.getShort());

            // Check for encrypted file.
            buffer.position(0x25);
            long value = buffer.getInt();

            buffer.position(0x38);
            table.setWriteProtected(buffer.get());
            table.setVersionId(buffer.get());

            // Paradox version 4.x and up.
            if (value == 0xFF00_FF00 && table.getVersionId() > Constants.PARADOX_VERSION_4) {
                buffer.position(0x5c);
                value = buffer.getInt();
            }

            table.setEncryptedData(value);

            buffer.position(0x49);
            table.setAutoIncrementValue(buffer.getInt());
            table.setFirstFreeBlock(buffer.getShort());

            buffer.position(0x55);
            table.setReferentialIntegrity(buffer.get());

            parseVersionID(buffer, table);

            final ParadoxField[] fields = TableData.parseTableFields(table, buffer);

            // Restart the buffer with all table header
            channel.position(0);
            buffer = ByteBuffer.allocate(table.getHeaderSize());
            channel.read(buffer);

            TableData.fixTablePositionByVersion(table, buffer, fields.length);
            TableData.parseTableFieldsName(table, buffer, fields);
            TableData.parseTableFieldsOrder(table, buffer);
        } catch (final IOException e) {
            throw new ParadoxDataException(ParadoxDataException.Error.ERROR_LOADING_DATA, e);
        }

        return table;
    }

    /**
     * Read fields attributes.
     *
     * @param table  the Paradox table.
     * @param buffer the buffer to read of.
     * @return the Paradox field list.
     */
    private static ParadoxField[] parseTableFields(final ParadoxTable table, final ByteBuffer buffer) {
        final ParadoxField[] fields = new ParadoxField[table.getFieldCount()];
        for (int loop = 0; loop < table.getFieldCount(); loop++) {
            final ParadoxField field = new ParadoxField(table.getConnection(),
                    ParadoxType.valueOfVendor(buffer.get()), loop + 1);
            field.setSize(buffer.get() & 0xFF);
            field.setTable(table);
            field.setTable(table);
            fields[loop] = field;
        }

        return fields;
    }

    /**
     * Parse the Paradox fields name.
     *
     * @param table  the Paradox table.
     * @param buffer the buffer to read of.
     * @param fields the field list.
     */
    private static void parseTableFieldsName(final ParadoxTable table, final ByteBuffer buffer,
                                             final ParadoxField[] fields) {
        final ByteBuffer name = ByteBuffer.allocate(261);
        for (int loop = 0; loop < table.getFieldCount(); loop++) {
            name.clear();

            while (true) {
                final byte c = buffer.get();
                if (c == 0) {
                    break;
                }
                name.put(c);
            }
            name.flip();
            fields[loop].setName(table.getCharset().decode(name).toString());
        }

        table.setFields(fields);
    }

    /**
     * Parse the fields order.
     *
     * @param table  the Paradox table.
     * @param buffer the buffer to read of.
     */
    private static void parseTableFieldsOrder(final ParadoxTable table, final ByteBuffer buffer) {
        final short[] fieldsOrder = new short[table.getFieldCount()];

        for (int loop = 0; loop < table.getFieldCount(); loop++) {
            fieldsOrder[loop] = buffer.getShort();
        }

        table.setFieldsOrder(fieldsOrder);
    }

    /**
     * Read a entire row.
     *
     * @param table  the table to read of.
     * @param fields the fields to read.
     * @param buffer the buffer to read of.
     * @return the row.
     * @throws SQLException in case of parse errors.
     */
    private static Object[] readRow(final ParadoxTable table, final ParadoxField[] fields,
                                    final ByteBuffer buffer) throws SQLException {
        final Object[] row = new Object[fields.length];

        for (final ParadoxField field : table.getFields()) {
            // Field filter
            final int index = search(fields, field);
            if (index != -1) {
                row[index] = FieldFactory.parse(table, buffer, field);
            } else {
                buffer.position(buffer.position() + field.getSize());
            }
        }

        return row;
    }

    private static int search(final Object[] values, Object find) {
        for (int i = 0; i < values.length; i++) {
            if (Objects.equals(values[i], find)) {
                return i;
            }
        }

        return -1;
    }
}
