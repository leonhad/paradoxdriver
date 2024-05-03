/*
 * Copyright (c) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.data;

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.data.filefilters.TableFilter;
import com.googlecode.paradox.exceptions.DataError;
import com.googlecode.paradox.exceptions.ParadoxDataException;
import com.googlecode.paradox.metadata.Field;
import com.googlecode.paradox.metadata.Table;
import com.googlecode.paradox.metadata.paradox.ParadoxTable;

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
     * Gets all tables within a pattern.
     *
     * @param schema         the schema directory.
     * @param pattern        the pattern.
     * @param connectionInfo the connection information.
     * @return the tables filtered.
     */
    public static List<Table> listTables(final File schema, final String pattern,
                                         final ConnectionInfo connectionInfo) {
        final List<Table> tables = new ArrayList<>();
        final File[] fileList = schema.listFiles(new TableFilter(connectionInfo.getLocale(), pattern));

        if (fileList != null) {
            Arrays.sort(fileList);
            for (final File file : fileList) {
                try {
                    final ParadoxTable table = loadHeader(file, connectionInfo);
                    Arrays.stream(table.getFields()).forEach(field -> field.setTable(table));
                    table.loadIndexes();
                    table.loadValidations();
                    tables.add(table);
                } catch (final SQLException e) {
                    e.printStackTrace();
                    connectionInfo.addWarning(e);
                }
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
    public static List<Object[]> loadData(final ParadoxTable table, final Field[] fields) throws SQLException {

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
            throw new ParadoxDataException(DataError.ERROR_LOADING_DATA, e);
        }
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
    private static Object[] readRow(final ParadoxTable table, final Field[] fields, final ByteBuffer buffer)
            throws SQLException {
        final Object[] row = new Object[fields.length];

        for (final Field field : table.getFields()) {
            // Field filter
            final int index = search(fields, field);
            if (index != -1) {
                row[index] = ParadoxFieldFactory.parse(table, buffer, field);
            } else {
                int size = field.getRealSize();
                buffer.position(buffer.position() + size);
            }
        }

        return row;
    }

    private static int search(final Field[] values, Object find) {
        for (int i = 0; i < values.length; i++) {
            if (Objects.equals(values[i], find)) {
                return i;
            }
        }

        return -1;
    }
}
