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

/**
 * Parses memo fields.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.3
 */
public final class MemoField implements FieldParser {
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
        return type == ParadoxFieldType.MEMO.getType()
                || type == ParadoxFieldType.FORMATTED_MEMO.getType();
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
        int index = (int) beginIndex & 0xFF;
        long offset = beginIndex & 0xFFFFFF00;

        int size = buffer.getInt();
        int modifier = buffer.getShort();

        // All fields are 9, only graphics is 17.
        int hsize = 9;

        // Graphic field?
        if (false) {
            size -= 8;
            hsize = 17;
        }

        buffer.order(ByteOrder.BIG_ENDIAN);
        if (size <= 0) {
            return NULL;
        } else if (size <= leader) {
            value.flip();
            value.limit(size);
            final String strValue = table.getCharset().decode(value).toString();
            return new FieldValue(strValue, ParadoxFieldType.MEMO.getSQLType());
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
            head.getShort();

            switch (type) {
                case 0x0:
                    throw new SQLException("Trying to read a head lob block.");
                case 0x1:
                    throw new SQLException("Trying to read a free lob block.");
                case FREE_BLOCK:
                    throw new SQLException("Invalid MB header.");
                case SINGLE_BLOCK: {
                    if (index != 0xFF) {
                        throw new SQLException("Offset points to a single blob block but index field is not 0xFF.");
                    }
                    // Read the remaining 6 bytes from the header.
                    head = ByteBuffer.allocate(hsize - 3);
                    head.order(ByteOrder.LITTLE_ENDIAN);
                    channel.read(head);
                    head.flip();

                    int internalSize = head.getInt();

                    if (size != internalSize) {
                        throw new SQLException(String.format("Blob does not have expected size (%d != %d).", size,
                                internalSize));
                    }

                    ByteBuffer blocks = ByteBuffer.allocate(size);
                    channel.read(blocks);
                    blocks.flip();

                    final String strValue = table.getCharset().decode(blocks).toString();
                    return new FieldValue(strValue, ParadoxFieldType.MEMO.getSQLType());
                }
                case SUB_BLOCK: {
                    // The remaining header bytes.
                    channel.position(channel.position() + hsize);

                    channel.position(offset + 12 + index * 5);
                    head = ByteBuffer.allocate(5);
                    channel.read(head);
                    head.order(ByteOrder.LITTLE_ENDIAN);
                    head.flip();

                    // Data offset divided by 16.
                    final int blockOffset = head.get() & 0xFF;
                    // Data length divided by 16 (rounded up).
                    int dataLength = head.get() & 0xFF;
                    head.getShort();
                    // This is reset to 1 by a table restructure.
                    // Data length modulo 16.
                    final int modulo = head.get() & 0xFF;

                    if (size != (dataLength - 1) * 0x10 + modulo) {
                        throw new SQLException(String.format("Blob does not have expected size (%d != %d).", size,
                                (dataLength - 1) * 0x10 + modulo));
                    }

                    ByteBuffer blocks = ByteBuffer.allocate(size);
                    channel.position(offset + blockOffset * 0x10);
                    channel.read(blocks);
                    blocks.flip();

                    final String strValue = table.getCharset().decode(blocks).toString();
                    return new FieldValue(strValue, ParadoxFieldType.MEMO.getSQLType());
                }
                default:
                    throw new SQLException("Invalid BLOB header type " + type);
            }
        } catch (final IOException ex) {
            throw new SQLException(ex);
        }
    }
}
