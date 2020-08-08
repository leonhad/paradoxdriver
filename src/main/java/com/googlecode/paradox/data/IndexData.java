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

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.data.filefilters.SecondaryIndexFilter;
import com.googlecode.paradox.exceptions.ParadoxDataException;
import com.googlecode.paradox.metadata.Index;
import com.googlecode.paradox.metadata.paradox.ParadoxDataFile;
import com.googlecode.paradox.metadata.paradox.ParadoxField;
import com.googlecode.paradox.metadata.paradox.ParadoxIndex;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.utils.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Reads index data files.
 *
 * @version 1.6
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
     * @param currentSchema  the current schema file.
     * @param tableName      the table name.
     * @param connectionInfo the connection information.
     * @return a list of {@link ParadoxIndex}.
     * @throws SQLException in case of reading failures.
     */
    public static Index[] listIndexes(final File currentSchema, final String tableName,
                                      final ConnectionInfo connectionInfo) throws SQLException {
        final ArrayList<ParadoxIndex> indexes = new ArrayList<>();
        String indexNamePattern = tableName + ".X__";
        File[] fileList = currentSchema.listFiles(new SecondaryIndexFilter(connectionInfo.getLocale(),
                indexNamePattern));

        if (fileList != null) {
            for (final File file : fileList) {
                try {
                    final ParadoxIndex index = IndexData.loadIndexHeader(file, connectionInfo);
                    indexes.add(index);
                } catch (final IOException e) {
                    throw new ParadoxDataException(ParadoxDataException.Error.ERROR_LOADING_DATA, e);
                }
            }
        }

        // FIXME review the filter and loading.
        indexNamePattern = tableName + ".Y__";
        fileList = currentSchema.listFiles(new SecondaryIndexFilter(connectionInfo.getLocale(), indexNamePattern));

        if (fileList != null) {
            for (final File file : fileList) {
                try {
                    final ParadoxIndex index = IndexData.loadIndexHeader(file, connectionInfo);
                    indexes.add(index);
                } catch (final IOException e) {
                    throw new ParadoxDataException(ParadoxDataException.Error.ERROR_LOADING_DATA, e);
                }
            }
        }

        return indexes.toArray(new Index[0]);
    }

    /**
     * Loads the database file header.
     *
     * @param file           the database {@link File}.
     * @param connectionInfo the connection information.
     * @return the {@link ParadoxIndex} reference.
     * @throws IOException if case of I/O exceptions.
     */
    private static ParadoxIndex loadIndexHeader(final File file, final ConnectionInfo connectionInfo)
            throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(Constants.MAX_BUFFER_SIZE);

        // FIXME fix Y__ index metadata

        buffer.order(ByteOrder.LITTLE_ENDIAN);

        final ParadoxIndex index = new ParadoxIndex(file, file.getName(), connectionInfo);

        try (final FileInputStream fs = new FileInputStream(file); FileChannel channel = fs.getChannel()) {
            channel.read(buffer);
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
    private static void parseIndexName(final ByteBuffer buffer, final ParadoxIndex index) {
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
     */
    private static void parseFields(final ByteBuffer buffer, final ParadoxDataFile index) {
        final ParadoxField[] fields = new ParadoxField[index.getFieldCount()];
        for (int loop = 0; loop < index.getFieldCount(); loop++) {
            final ParadoxField field = new ParadoxField(ParadoxType.valueOfVendor(buffer.get()), loop + 1);
            field.setSize(buffer.get());
            fields[loop] = field;
        }

        if (index.getVersionId() > Constants.PARADOX_VERSION_4) {
            if (index.getVersionId() == 0xC) {
                buffer.position(0x78 + 261 + 4 + (6 * fields.length));
            } else {
                buffer.position(0x78 + 83 + (6 * fields.length));
            }
        } else {
            buffer.position(0x58 + 83 + (6 * fields.length));
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
            fields[loop].setName(index.getCharset().decode(name).toString());
        }
        index.setFields(fields);

        final short[] fieldsOrder = new short[index.getFieldCount()];
        for (int loop = 0; loop < index.getFieldCount(); loop++) {
            fieldsOrder[loop] = buffer.getShort();
        }

        index.setFieldsOrder(fieldsOrder);
    }

    /**
     * Parse the sort order ID.
     *
     * @param buffer the buffer to parse.
     * @param index  the paradox index.
     */
    private static void parseSortID(final ByteBuffer buffer, final ParadoxIndex index) {
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
