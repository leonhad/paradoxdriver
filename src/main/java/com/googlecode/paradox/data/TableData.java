/*
 * TableData.java 03/14/2009 Copyright (C) 2009 Leonardo Alves da Costa This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.data;

import com.googlecode.paradox.data.table.value.FieldValue;
import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.metadata.ParadoxTable;
import com.googlecode.paradox.utils.SQLStates;
import com.googlecode.paradox.utils.Utils;
import com.googlecode.paradox.utils.filefilters.TableFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility class for loading table files.
 *
 * @author Leonardo Alves da Costa
 * @version 1.1
 * @since 1.0
 */
public final class TableData extends AbstractParadoxData {

    /**
     * Utility class.
     */
    private TableData() {
        super();
    }

    /**
     * List all database tables.
     *
     * @param currentSchema the current schema file.
     * @return all {@link ParadoxTable}.
     * @throws SQLException in case of failures.
     */
    public static List<ParadoxTable> listTables(final File currentSchema) throws SQLException {
        final ArrayList<ParadoxTable> tables = new ArrayList<>();
        final File[] fileList = currentSchema.listFiles(new TableFilter());
        if (fileList != null) {
            for (final File file : fileList) {
                final ParadoxTable table = TableData.loadTableHeader(file);
                tables.add(table);
            }
        }
        return tables;
    }

    /**
     * Gets all tables within a pattern.
     *
     * @param pattern the pattern.
     * @param schema  the schema directory.
     * @return the tables filtered.
     * @throws SQLException in case of failures.
     */
    public static List<ParadoxTable> listTables(final String pattern, final File schema) throws SQLException {
        final List<ParadoxTable> tables = new ArrayList<>();
        final File[] fileList = schema.listFiles(new TableFilter(Utils.removeDb(pattern)));
        if (fileList != null) {
            for (final File file : fileList) {
                final ParadoxTable table = TableData.loadTableHeader(file);
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
    public static List<List<FieldValue>> loadData(final ParadoxTable table,
            final Collection<ParadoxField> fields) throws SQLException {
        final List<List<FieldValue>> ret = new ArrayList<>();

        final int blockSize = table.getBlockSizeBytes();
        final int recordSize = table.getRecordSize();
        final int headerSize = table.getHeaderSize();
        final ByteBuffer buffer = ByteBuffer.allocate(blockSize);

        try (FileInputStream fs = new FileInputStream(table.getFile()); FileChannel channel = fs.getChannel()) {
            if (table.getUsedBlocks() == 0) {
                return ret;
            }
            long nextBlock = table.getFirstBlock();
            do {
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                channel.position(headerSize + ((nextBlock - 1) * blockSize));

                buffer.clear();
                channel.read(buffer);
                buffer.flip();

                nextBlock = buffer.getShort();
                // The block number.
                buffer.getShort();

                final int addDataSize = buffer.getShort() & 0xFFFF;
                final int rowsInBlock = (addDataSize / recordSize) + 1;

                buffer.order(ByteOrder.BIG_ENDIAN);

                for (int loop = 0; loop < rowsInBlock; loop++) {
                    ret.add(TableData.readRow(table, fields, buffer));
                }
            } while (nextBlock != 0);
        } catch (final IOException e) {
            throw new SQLException(e.getMessage(), SQLStates.INVALID_IO.getValue(), e);
        }
        return ret;
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
        if (table.getVersionId() > 4) {
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
     * @param file the {@link File} to read.
     * @return the {@link ParadoxTable}.
     * @throws SQLException in case of reading errors.
     */
    private static ParadoxTable loadTableHeader(final File file) throws SQLException {
        final ParadoxTable table = new ParadoxTable(file, file.getName());
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        try (FileInputStream fs = new FileInputStream(file); FileChannel channel = fs.getChannel()) {
            channel.read(buffer);
            buffer.flip();

            table.setRecordSize(buffer.getShort());
            table.setHeaderSize(buffer.getShort());
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

            buffer.position(0x38);
            table.setWriteProtected(buffer.get());
            table.setVersionId(buffer.get());

            buffer.position(0x49);
            table.setAutoIncrementValue(buffer.getInt());
            table.setFirstFreeBlock(buffer.getShort());

            buffer.position(0x55);
            table.setReferentialIntegrity(buffer.get());

            AbstractParadoxData.parseVersionID(buffer, table);

            final List<ParadoxField> fields = TableData.parseTableFields(table, buffer);

            // Restart the buffer with all table header
            channel.position(0);
            buffer = ByteBuffer.allocate(table.getHeaderSize());
            channel.read(buffer);

            TableData.fixTablePositionByVersion(table, buffer, fields.size());

            TableData.parseTableFieldsName(table, buffer, fields);

            TableData.parseTableFieldsOrder(table, buffer);
        } catch (final IOException e) {
            throw new SQLException(e.getMessage(), SQLStates.INVALID_IO.getValue(), e);
        }
        return table;
    }

    /**
     * Read fields attributes.
     *
     * @param table  the Paradox table.
     * @param buffer the buffer to read of.
     * @return the Paradox field list.
     * @throws SQLException in case of parse errors.
     */
    private static List<ParadoxField> parseTableFields(final ParadoxTable table, final ByteBuffer buffer)
    throws SQLException {
        final List<ParadoxField> fields = new ArrayList<>();
        for (int loop = 0; loop < table.getFieldCount(); loop++) {
            final ParadoxField field = new ParadoxField(loop + 1);
            field.setType(buffer.get());
            field.setSize(buffer.get() & 0xff);
            field.setTableName(table.getName());
            field.setTable(table);
            fields.add(field);
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
            final List<ParadoxField> fields) {
        for (int loop = 0; loop < table.getFieldCount(); loop++) {
            final ByteBuffer name = ByteBuffer.allocate(261);

            while (true) {
                final byte c = buffer.get();
                if (c == 0) {
                    break;
                }
                name.put(c);
            }
            name.flip();
            fields.get(loop).setName(table.getCharset().decode(name).toString());
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
        final List<Short> fieldsOrder = new ArrayList<>();
        for (int loop = 0; loop < table.getFieldCount(); loop++) {
            fieldsOrder.add(buffer.getShort());
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
    private static List<FieldValue> readRow(final ParadoxTable table, final Collection<ParadoxField> fields,
            final ByteBuffer buffer) throws SQLException {
        final List<FieldValue> row = new ArrayList<>();

        for (final ParadoxField field : table.getFields()) {
            final FieldValue fieldValue = FieldFactory.parse(table, buffer, field);

            // Field filter
            if (fields.contains(field) && (fieldValue != null)) {
                fieldValue.setField(field);
                row.add(fieldValue);
            }
        }
        return row;
    }
}
