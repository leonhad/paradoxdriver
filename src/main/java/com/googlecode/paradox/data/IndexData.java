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
import com.googlecode.paradox.metadata.ParadoxDataFile;
import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.metadata.ParadoxIndex;
import com.googlecode.paradox.utils.Constants;
import com.googlecode.paradox.utils.Utils;
import com.googlecode.paradox.utils.filefilters.SecondaryIndexFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads index data files.
 *
 * @author Leonardo Alves da Costa
 * @version 1.1
 * @since 1.0
 */
public final class IndexData extends ParadoxData {

    /**
     * Utility class.
     */
    private IndexData() {
        super();
    }

    /**
     * List the indexes in a database file.
     *
     * @param currentSchema the current schema file.
     * @param tableName     the table name.
     * @param connection    the database connection.
     * @return a list of {@link ParadoxIndex}.
     * @throws SQLException in case of reading failures.
     */
    public static List<ParadoxIndex> listIndexes(final File currentSchema, final String tableName,
                                                 final ParadoxConnection connection) throws SQLException {
        final ArrayList<ParadoxIndex> indexes = new ArrayList<>();
        final String indexNamePattern = Utils.removeDb(tableName) + ".X??";
        final File[] fileList = currentSchema.listFiles(new SecondaryIndexFilter(indexNamePattern));
        if (fileList != null) {
            for (final File file : fileList) {
                try {
                    final ParadoxIndex index = IndexData.loadIndexHeader(file, connection);
                    indexes.add(index);
                } catch (final IOException ex) {
                    throw new SQLException("Error loading Paradox index.", ex);
                }
            }
        }
        return indexes;
    }

    /**
     * Loads the database file header.
     *
     * @param file       the database {@link File}.
     * @param connection the database connection.
     * @return the {@link ParadoxIndex} reference.
     * @throws IOException  if case of I/O exceptions.
     * @throws SQLException in case of database errors.
     */
    private static ParadoxIndex loadIndexHeader(final File file, final ParadoxConnection connection) throws IOException,
            SQLException {
        final ParadoxBuffer buffer = new ParadoxBuffer(Constants.MAX_BUFFER_SIZE);

        buffer.order(ByteOrder.LITTLE_ENDIAN);

        final ParadoxIndex index = new ParadoxIndex(file, file.getName(), connection);

        try (final FileInputStream fs = new FileInputStream(file); FileChannel channel = fs.getChannel()) {
            buffer.read(channel);
            buffer.flip();

            index.setRecordSize(buffer.getShort());
            index.setHeaderSize(buffer.getShort());
            index.setType(buffer.get());
            index.setBlockSize(buffer.get());
            index.setRowCount(buffer.getInt());
            index.setUsedBlocks(buffer.getShort());
            index.setTotalBlocks(buffer.getShort());
            index.setFirstBlock(buffer.getShort());
            index.setLastBlock(buffer.getShort());

            buffer.position(0x21);
            index.setFieldCount(buffer.getShort());
            index.setPrimaryFieldCount(buffer.getShort());

            buffer.position(0x38);
            index.setWriteProtected(buffer.get());
            index.setVersionId(buffer.get());

            buffer.position(0x49);
            index.setAutoIncrementValue(buffer.getInt());
            index.setFirstFreeBlock(buffer.getShort());

            buffer.position(0x55);
            index.setReferentialIntegrity(buffer.get());

            parseVersionID(buffer, index);

            parseFields(buffer, index);

            IndexData.parseSortID(buffer, index);
            IndexData.parseIndexName(buffer, index);
        }
        return index;
    }

    /**
     * Parse the index names.
     *
     * @param buffer the buffer to parse.
     * @param index  the paradox index.
     */
    private static void parseIndexName(final ParadoxBuffer buffer, final ParadoxIndex index) {
        final ByteBuffer name = ByteBuffer.allocate(26);
        while (true) {
            final byte c = buffer.get();
            if (c == 0) {
                break;
            }
            name.put(c);
        }

        name.flip();
        final String tempName = index.getCharset().decode(name).toString();
        if (tempName.length() != 0) {
            index.setName(tempName);
        }
    }

    /**
     * Parse fields in index header.
     *
     * @param buffer the buffer to parse.
     * @param index  the paradox index.
     * @throws SQLException in case of parse errors.
     */
    private static void parseFields(final ParadoxBuffer buffer, final ParadoxDataFile index) throws SQLException {
        final ArrayList<ParadoxField> fields = new ArrayList<>();
        for (int loop = 0; loop < index.getFieldCount(); loop++) {
            final ParadoxField field = new ParadoxField(loop + 1);
            field.setType(buffer.get());
            field.setSize((int) buffer.get());
            fields.add(field);
        }

        if (index.getVersionId() > 4) {
            if (index.getVersionId() == 0xC) {
                buffer.position(0x78 + 261 + 4 + (6 * fields.size()));
            } else {
                buffer.position(0x78 + 83 + (6 * fields.size()));
            }
        } else {
            buffer.position(0x58 + 83 + (6 * fields.size()));
        }

        for (int loop = 0; loop < index.getFieldCount(); loop++) {
            final ByteBuffer name = ByteBuffer.allocate(261);

            while (true) {
                final byte c = buffer.get();
                if (c == 0) {
                    break;
                }
                name.put(c);
            }
            name.flip();
            fields.get(loop).setName(index.getCharset().decode(name).toString());
        }
        index.setFields(fields);

        final ArrayList<Short> fieldsOrder = new ArrayList<>();
        for (int loop = 0; loop < index.getFieldCount(); loop++) {
            fieldsOrder.add(buffer.getShort());
        }
        index.setFieldsOrder(fieldsOrder);
    }

    /**
     * Parse the sort order ID.
     *
     * @param buffer the buffer to parse.
     * @param index  the paradox index.
     */
    private static void parseSortID(final ParadoxBuffer buffer, final ParadoxIndex index) {
        final ByteBuffer sortOrderID = ByteBuffer.allocate(26);
        while (true) {
            final byte c = buffer.get();
            if (c == 0) {
                break;
            }
            sortOrderID.put(c);
        }

        sortOrderID.flip();
        index.setSortOrderID(index.getCharset().decode(sortOrderID).toString());
    }

}
