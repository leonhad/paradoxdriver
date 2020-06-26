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
package com.googlecode.paradox.data.field;

import com.googlecode.paradox.data.FieldParser;
import com.googlecode.paradox.data.table.value.FieldValue;
import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.metadata.ParadoxTable;
import com.googlecode.paradox.results.ParadoxFieldType;
import com.googlecode.paradox.utils.SQLStates;
import com.googlecode.paradox.utils.filefilters.TableFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Parses blob fields.
 *
 * @author Leonardo Alves da Costa
 * @author Michael Berry
 * @version 1.0
 * @since 1.3
 */
public final class BlobField implements FieldParser {

    /**
     * Free block value.
     */
    private static final int FREE_BLOCK = 4;

    /**
     * Single block value.
     */
    private static final int SINGLE_BLOCK = 2;

    /**
     * Sub block value.
     */
    private static final int SUB_BLOCK = 3;

    private static final FieldValue NULL = new FieldValue(ParadoxFieldType.MEMO.getSQLType());

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean match(final int type) {
        return type == ParadoxFieldType.BLOB.getType()
                || type == ParadoxFieldType.OLE.getType()
                || type == ParadoxFieldType.GRAPHIC.getType();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public FieldValue parse(final ParadoxTable table, final ByteBuffer buffer, final ParadoxField field)
            throws SQLException {
        final ByteBuffer value = ByteBuffer.allocate(field.getSize());
        for (int chars = 0; chars < field.getSize(); chars++) {
            value.put(buffer.get());
        }

        value.flip();

        buffer.order(ByteOrder.LITTLE_ENDIAN);
        int leader = field.getSize();
        if (leader > 0) {
            // Field size defined in DB file.
            value.position(leader);
        }

        long beginIndex = buffer.getInt();
        // Index.
        long offset = beginIndex & 0xFFFFFF00;

        int size = buffer.getInt();
        buffer.getShort();

        // All fields are 9, only graphics is 17.
        int hsize = 9;

        int blobSize = size;
        // Graphic field?
        if (false) {
            blobSize -= 8;
            hsize = 17;
        }

        buffer.order(ByteOrder.BIG_ENDIAN);
        if (size <= 0) {
            return NULL;
        } else if (size <= leader) {
            byte[] currentValue = Arrays.copyOf(value.array(), size);
            return new FieldValue(currentValue, ParadoxFieldType.BLOB.getSQLType());
        }

        // Find blob file.
        final File[] fileList = table.getFile().getParentFile().listFiles(new TableFilter(field.getConnection(),
                table.getName(), "mb"));
        if ((fileList == null) || (fileList.length == 0)) {
            throw new SQLException(String.format("Blob file not found for table '%s'", table.getName()),
                    SQLStates.LOAD_DATA.getValue());
        }
        if (fileList.length > 1) {
            throw new SQLException(String.format("Many blob files for table '%s'", table.getName()),
                    SQLStates.LOAD_DATA.getValue());
        }
        File blobFile = fileList[0];
        try (final FileInputStream fs = new FileInputStream(blobFile);
             final FileChannel channel = fs.getChannel()) {

            channel.position(offset);

            ByteBuffer head = ByteBuffer.allocate(3);
            head.order(ByteOrder.LITTLE_ENDIAN);
            channel.read(head);
            head.flip();
            byte type = head.get();
            final int blockSize = head.getShort() & 0xFFFF;

            switch (type) {
                case 0x0:
                    throw new SQLException("Trying to read a head lob block.");
                case 0x1:
                    throw new SQLException("Trying to read a free lob block.");
                case FREE_BLOCK:
                    throw new SQLException("Invalid MB header.");
                case SINGLE_BLOCK: {
                    final ByteBuffer blockHead = ByteBuffer.allocate(hsize - 3);
                    blockHead.order(ByteOrder.LITTLE_ENDIAN);
                    blockHead.clear();
                    channel.read(blockHead);
                    blockHead.flip();
                    final int blobLength = blockHead.getInt();
                    // Modifier.
                    blockHead.getShort();

                    final ByteBuffer blockData = ByteBuffer.allocate(blobLength);
                    blockData.order(ByteOrder.LITTLE_ENDIAN);
                    blockData.clear();
                    channel.read(blockData);

                    return new FieldValue(blockData.array(), ParadoxFieldType.BLOB.getSQLType());
                }
                case SUB_BLOCK: {
                    // Nine extra bytes here for remaining header.

                    channel.position(channel.position() + hsize);

                    ArrayList<Byte> blocks = new ArrayList<>(blobSize);
                    int n = 0;
                    while (n < 64) {
                        head = ByteBuffer.allocate(5);
                        channel.read(head);
                        head.order(ByteOrder.LITTLE_ENDIAN);
                        head.flip();

                        // Data offset divided by 16.
                        final int blockOffset = (head.get() & 0xFF) * 0x10;
                        // Data length divided by 16 (rounded up).
                        int ln = head.get() * 0x10;
                        head.getShort();
                        // This is reset to 1 by a table restructure.
                        // Data length modulo 16.
                        final int mdl = head.get();

                        if (blockOffset != 0) {
                            final long position = channel.position();
                            final long start = blockOffset + offset;
                            ln = (ln - 0x10) + mdl;
                            final ByteBuffer blockData = ByteBuffer.allocate(ln);
                            blockData.order(ByteOrder.LITTLE_ENDIAN);
                            blockData.clear();
                            channel.position(start);
                            channel.read(blockData);
                            blockData.flip();
                            final byte[] values = new byte[ln];
                            blockData.get(values);

                            for (final byte b : values) {
                                blocks.add(b);
                            }
                            channel.position(position);
                        }
                        n++;
                    }

                    byte[] bytes = new byte[blocks.size()];
                    for (int i = 0; i < blocks.size(); i++) {
                        bytes[i] = blocks.get(i);
                    }

                    return new FieldValue(bytes, ParadoxFieldType.BLOB.getSQLType());
                }
                default:
                    throw new SQLException("Invalid BLOB header type " + type);
            }
        } catch (final IOException ex) {
            throw new SQLException(ex);
        }
    }
}
