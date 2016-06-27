/*
 * IndexData.java
 *
 * 03/14/2009
 * Copyright (C) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.data;

import static java.nio.ByteBuffer.allocate;
import static java.nio.charset.Charset.forName;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.metadata.ParadoxIndex;
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
 * @since 1.0
 * @version 1.1
 */
public final class IndexData {
    
    /**
     * Utility class.
     */
    private IndexData() {
        // Utility class.
    }

    /**
     * List the indexes in a database file.
     *
     * @param conn
     *            the Paradox connection.
     * @param tableName
     *            the table name.
     * @return a list of {@link ParadoxIndex}.
     * @throws SQLException
     *             in case of reading failures.
     */
    public static List<ParadoxIndex> listIndexes(final ParadoxConnection conn, final String tableName)
            throws SQLException {
        final ArrayList<ParadoxIndex> indexes = new ArrayList<>();
        final String indexNamePattern = tableName.substring(0, tableName.lastIndexOf('.')) + ".X??";
        final File[] fileList = conn.getDir().listFiles(new SecondaryIndexFilter(indexNamePattern));
        if (fileList != null) {
            for (final File file : fileList) {
                final ParadoxIndex index;
                try {
                    index = loadIndexHeader(file);
                } catch (final IOException ex) {
                    throw new SQLException("Error loading Paradox index.", ex);
                }
                if (index.isValid()) {
                    indexes.add(index);
                }
            }
        }
        return indexes;
    }

    /**
     * Loads the database file header.
     *
     * @param file
     *            the database {@link File}.
     * @return the {@link ParadoxIndex} reference.
     * @throws IOException
     *             if case of I/O exceptions.
     * @throws SQLException
     *             in case of database errors.
     */
    private static ParadoxIndex loadIndexHeader(final File file) throws IOException, SQLException {
        final ByteBuffer buffer = allocate(2048);

        buffer.order(ByteOrder.LITTLE_ENDIAN);
        FileChannel channel = null;
        final FileInputStream fs = new FileInputStream(file);
        final ParadoxIndex index = new ParadoxIndex(file, file.getName());

        try {
            channel = fs.getChannel();
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

            if (index.getVersionId() > 4) {
                // Set the charset.
                buffer.position(0x6A);
                index.setCharset(forName("cp" + buffer.getShort()));

                buffer.position(0x78);
            } else {
                buffer.position(0x58);
            }

            final ArrayList<ParadoxField> fields = new ArrayList<>();
            for (int loop = 0; loop < index.getFieldCount(); loop++) {
                final ParadoxField field = new ParadoxField(loop + 1);
                field.setType(buffer.get());
                field.setSize(buffer.get());
                fields.add(field);
            }

            if (index.getVersionId() > 4) {
                if (index.getVersionId() == 0xC) {
                    buffer.position(0x78 + 261 + 4 + 6 * fields.size());
                } else {
                    buffer.position(0x78 + 83 + 6 * fields.size());
                }
            } else {
                buffer.position(0x58 + 83 + 6 * fields.size());
            }

            for (int loop = 0; loop < index.getFieldCount(); loop++) {
                final ByteBuffer name = allocate(261);

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

            // Sort Order ID
            final ByteBuffer sortOrderID = allocate(26);
            while (true) {
                final byte c = buffer.get();
                if (c == 0) {
                    break;
                }
                sortOrderID.put(c);
            }
            sortOrderID.flip();
            index.setSortOrderID(index.getCharset().decode(sortOrderID).toString());

            // Index name
            final ByteBuffer name = allocate(26);
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

        } finally {
            if (channel != null) {
                channel.close();
            }
            fs.close();
        }
        return index;
    }
}
