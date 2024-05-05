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
package com.googlecode.paradox.data.field;

import com.googlecode.paradox.data.EncryptedData;
import com.googlecode.paradox.data.FieldParser;
import com.googlecode.paradox.exceptions.DataError;
import com.googlecode.paradox.exceptions.ParadoxDataException;
import com.googlecode.paradox.metadata.Field;
import com.googlecode.paradox.metadata.paradox.ParadoxTable;
import com.googlecode.paradox.results.ParadoxType;

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
 * @version 1.8
 * @since 1.5.0
 */
public abstract class AbstractLobField implements FieldParser {

    /**
     * Lob offset size.
     */
    public static final int HEAD_SIZE = 3;
    /**
     * General log header size.
     */
    public static final int BLOB_HEADER_SIZE = 9;
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
    /**
     * The graph specific header size.
     */
    private static final int GRAPH_HEADER_SIZE = 17;

    /**
     * Default blob precision.
     */
    public static final int LEADER_SIZE_PADDING = 10;

    private static ByteBuffer readBlock(final FileChannel channel, final int size, final ParadoxTable table)
            throws IOException {
        // Calculate the block size.
        final long pos = channel.position();
        final long offset = pos & 0xFFFFFF00L;
        int blockSize = (int) (size + pos - offset);
        if ((blockSize & 0xFF) > 0) {
            blockSize = ((blockSize >> 0x08) + 1) << 0x08;
        }

        // Read the block data
        ByteBuffer buffer = ByteBuffer.allocate(blockSize);
        channel.position(offset);
        channel.read(buffer);
        channel.position(pos + size);

        // Handle encryption.
        if (table.isEncrypted()) {
            byte[] b = buffer.array();
            EncryptedData.decryptMBBlock(b, table.getEncryptedData(), blockSize);
        }

        buffer.flip();

        // recalculate offset.
        int bufferOffset = (int) (pos - offset);
        if (bufferOffset > 0) {
            buffer.position(bufferOffset);
            buffer = buffer.slice();
            buffer.limit(size);
        } else {
            buffer.limit(size);
        }

        return buffer;
    }

    /**
     * Gets the LOB value.
     *
     * @param table  the associated table.
     * @param buffer the buffer.
     * @return the LOB value.
     * @throws ParadoxDataException in case of failures.
     */
    protected abstract Object getValue(final ParadoxTable table, final ByteBuffer buffer) throws ParadoxDataException;

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object parse(final ParadoxTable table, final ByteBuffer buffer, final Field field)
            throws SQLException {
        int leader = field.getRealSize() - LEADER_SIZE_PADDING;

        final ByteBuffer value = ByteBuffer.allocate(leader);

        System.arraycopy(buffer.array(), buffer.position(), value.array(), 0, leader);
        buffer.position(buffer.position() + leader);

        buffer.order(ByteOrder.LITTLE_ENDIAN);
        value.position(leader);

        // All fields are 9, only graphics is 17.
        int headerSize = BLOB_HEADER_SIZE;

        // Graphic field?
        if (field.getType() == ParadoxType.GRAPHIC) {
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
            final long offset = beginIndex & 0xFFFFFF00L;
            channel.position(offset);

            ByteBuffer head = readBlock(channel, HEAD_SIZE, table);
            head.order(ByteOrder.LITTLE_ENDIAN);

            byte type = head.get();
            head.getShort();

            final long index = beginIndex & 0xFF;
            return processBlobByBlockType(table, headerSize, size, channel, offset, type, index);
        } catch (final IOException ex) {
            throw new ParadoxDataException(DataError.ERROR_LOADING_DATA, ex);
        }
    }

    private Object processBlobByBlockType(final ParadoxTable table, final int headerSize, final int size,
                                          final FileChannel channel, final long offset, final byte type,
                                          final long index) throws SQLException, IOException {
        switch (type) {
            case 0x0:
                throw new ParadoxDataException(DataError.BLOB_READ_HEAD_BLOCK);
            case 0x1:
                throw new ParadoxDataException(DataError.BLOB_READ_FREE_BLOCK);
            case FREE_BLOCK:
                throw new ParadoxDataException(DataError.BLOB_INVALID_HEADER);
            case SINGLE_BLOCK:
                return parseSingleBlock(table, index, size, headerSize, channel);
            case SUB_BLOCK:
                return parseSubBlock(table, index, offset, size, headerSize, channel);
            default:
                throw new ParadoxDataException(DataError.BLOB_INVALID_HEADER_TYPE);
        }
    }

    private Object parseSubBlock(final ParadoxTable table, final long index, final long offset, final int size,
                                 final int headerSize, final FileChannel channel) throws IOException, SQLException {
        channel.position(channel.position() + headerSize);

        channel.position(offset + 0x0CL + index * 0x05L);
        final ByteBuffer head = readBlock(channel, 5, table);
        head.order(ByteOrder.LITTLE_ENDIAN);

        // Data offset divided by 16.
        final int blockOffset = head.get() & 0xFF;
        // Data length divided by 16 (rounded up).
        int dataLength = head.get() & 0xFF;
        head.getShort();
        // This is reset to 1 by a table restructure.
        // Data length modulo 16.
        final int modulo = head.get() & 0xFF;

        if (size != (dataLength - 1) * 0x10 + modulo) {
            throw new ParadoxDataException(DataError.BLOB_INVALID_DECLARED_SIZE);
        }

        channel.position(offset + blockOffset * 0x10);
        final ByteBuffer blocks = readBlock(channel, size, table);

        return getValue(table, blocks);
    }

    private Object parseSingleBlock(ParadoxTable table, long index, int size, int headerSize, final FileChannel channel)
            throws SQLException, IOException {
        if (index != 0xFF) {
            throw new ParadoxDataException(DataError.BLOB_SINGLE_BLOCK_INVALID_INDEX);
        }
        // Read the remaining 6 bytes from the header.
        final ByteBuffer head = readBlock(channel, headerSize - HEAD_SIZE, table);
        head.order(ByteOrder.LITTLE_ENDIAN);

        int internalSize = head.getInt();
        if (size != internalSize) {
            throw new ParadoxDataException(DataError.BLOB_INVALID_DECLARED_SIZE);
        }

        final ByteBuffer blocks = readBlock(channel, size, table);
        return getValue(table, blocks);
    }
}
