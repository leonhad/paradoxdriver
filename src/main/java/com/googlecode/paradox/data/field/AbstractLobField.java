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
import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.metadata.ParadoxTable;
import com.googlecode.paradox.results.ParadoxFieldType;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Parses LOB fields.
 *
 * @version 1.1
 * @since 1.5.0
 */
public abstract class AbstractLobField implements FieldParser {

    public static final int HEAD_SIZE = 3;
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
    public static final int GRAPH_HEADER_SIZE = 17;
    public static final int BLOB_HEADER_SIZE = 9;

    protected abstract Object getValue(final ParadoxTable table, final ByteBuffer value);

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object parse(final ParadoxTable table, final ByteBuffer buffer, final ParadoxField field)
            throws SQLException {
        final ByteBuffer value = ByteBuffer.allocate(field.getRealSize());

        System.arraycopy(buffer.array(), buffer.position(), value.array(), 0, field.getRealSize());
        buffer.position(buffer.position() + field.getRealSize());

        buffer.order(ByteOrder.LITTLE_ENDIAN);
        int leader = field.getRealSize();
        value.position(leader);

        // All fields are 9, only graphics is 17.
        int headerSize = BLOB_HEADER_SIZE;

        // Graphic field?
        if (field.getType() == ParadoxFieldType.GRAPHIC.getType()) {
            headerSize = GRAPH_HEADER_SIZE;
        }

        long beginIndex = buffer.getInt();
        final int size = buffer.getInt();
        buffer.getShort();
        buffer.order(ByteOrder.BIG_ENDIAN);
        if (size <= 0) {
            return null;
        } else if (size <= leader) {
            byte[] currentValue = Arrays.copyOf(value.array(), size);
            return getValue(table, ByteBuffer.wrap(currentValue));
        }

        try (final FileInputStream fs = table.openBlobs(); final FileChannel channel = fs.getChannel()) {
            final long offset = beginIndex & 0xFFFFFF00;
            channel.position(offset);

            ByteBuffer head = ByteBuffer.allocate(HEAD_SIZE);
            head.order(ByteOrder.LITTLE_ENDIAN);
            channel.read(head);
            head.flip();
            byte type = head.get();
            head.getShort();

            final int index = (int) beginIndex & 0xFF;
            return processBlobByBlockType(table, headerSize, size, channel, offset, type, index);
        } catch (final IOException ex) {
            throw new SQLException(ex);
        }
    }

    private Object processBlobByBlockType(ParadoxTable table, int headerSize, int size, FileChannel channel,
                                          long offset, byte type, int index) throws SQLException, IOException {
        switch (type) {
            case 0x0:
                throw new SQLException("Trying to read a head lob block.");
            case 0x1:
                throw new SQLException("Trying to read a free lob block.");
            case FREE_BLOCK:
                throw new SQLException("Invalid MB header.");
            case SINGLE_BLOCK:
                return parseSingleBlock(table, index, size, headerSize, channel);
            case SUB_BLOCK:
                return parseSubBlock(table, index, offset, size, headerSize, channel);
            default:
                throw new SQLException("Invalid BLOB header type " + type);
        }
    }

    private Object parseSubBlock(final ParadoxTable table, final int index, final long offset, final int size,
                                 final int headerSize, final FileChannel channel) throws IOException, SQLException {
        channel.position(channel.position() + headerSize);

        channel.position(offset + 12 + index * 5);
        final ByteBuffer head = ByteBuffer.allocate(5);
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

        final ByteBuffer blocks = ByteBuffer.allocate(size);
        channel.position(offset + blockOffset * 0x10);
        channel.read(blocks);
        blocks.flip();

        return getValue(table, blocks);
    }

    private Object parseSingleBlock(ParadoxTable table, int index, int size, int headerSize, FileChannel channel)
            throws SQLException, IOException {
        if (index != 0xFF) {
            throw new SQLException("Offset points to a single blob block but index field is not 0xFF.");
        }
        // Read the remaining 6 bytes from the header.
        final ByteBuffer head = ByteBuffer.allocate(headerSize - HEAD_SIZE);
        head.order(ByteOrder.LITTLE_ENDIAN);
        channel.read(head);
        head.flip();

        int internalSize = head.getInt();
        if (size != internalSize) {
            throw new SQLException(String.format("Blob does not have expected size (%d != %d).", size,
                    internalSize));
        }

        final ByteBuffer blocks = ByteBuffer.allocate(size);
        channel.read(blocks);
        blocks.flip();

        return getValue(table, blocks);
    }
}
