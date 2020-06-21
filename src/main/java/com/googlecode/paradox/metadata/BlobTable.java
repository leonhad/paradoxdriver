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
package com.googlecode.paradox.metadata;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.utils.*;
import com.googlecode.paradox.utils.filefilters.TableFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Read from LOB file of PARADOX format.
 *
 * @author Leonardo Alves da Costa
 * @author Andre Mikhaylov
 * @version 1.1
 * @since 1.2
 */
public final class BlobTable extends ParadoxDataFile {

    /**
     * Free block value.
     */
    private static final int FREE_BLOCK = 4;

    /**
     * Default header block size.
     */
    private static final long HEADER_BLOCK_SIZE = 0x1000;

    /**
     * Single block value.
     */
    private static final int SINGLE_BLOCK = 2;

    /**
     * Sub block value.
     */
    private static final int SUB_BLOCK = 3;

    /**
     * Block cache.
     */
    private final IBlockCache cache;

    /**
     * Channel to read of.
     */
    private FileChannel channel;

    /**
     * This LOB {@link InputStream}.
     */
    private FileInputStream fs;

    /**
     * If this LOB is already parsed.
     */
    private boolean parsed;

    /**
     * Creates a new instance.
     *
     * @param file       the file to read of.
     * @param name       the LOBs name.
     * @param connection the database connection.
     */
    BlobTable(final File file, final String name, final ParadoxConnection connection) {
        super(file, Utils.removeMB(name), connection);
        this.cache = new AllBlockCache();
        this.parsed = false;
        this.fields = Collections.emptyList();
    }

    /**
     * Close this LOB reference.
     *
     * @throws SQLException in case of I/O errors.
     */
    public void close() throws SQLException {
        try {
            this.channel.close();
            this.fs.close();
        } catch (final IOException ex) {
            throw new SQLException(ex.getMessage(), SQLStates.LOAD_DATA.getValue(), ex);
        }
    }

    /**
     * If this block is already parsed.
     *
     * @return true if this block is already parsed.
     */
    public boolean isParsed() {
        return this.parsed;
    }

    /**
     * Read length bytes from offset position in MB file.
     *
     * @param pOffset offset of the blob's data block in the MB file and an
     *                index value.
     * @return the data values.
     * @throws SQLException in case of parse errors.
     */
    public byte[] read(final long pOffset) throws SQLException {
        if (!this.parsed) {
            this.open();
            this.parse();
        }
        return this.getData(BlockOffset.fromRawLong(pOffset));
    }

    /**
     * Gets the data from LOB file.
     *
     * @param offset offset to read of.
     * @return the data array.
     * @throws SQLException in case of reading failures.
     */
    private byte[] getData(final BlockOffset offset) throws SQLException {
        ClobBlock block = this.cache.get(offset);
        if (block != null) {
            return block.getValue();
        }

        block = this.readBlock(offset);
        if (block == null) {
            throw new SQLException("Block " + offset + " not found. Invalid mb file", SQLStates.LOAD_DATA.getValue());
        }
        return block.getValue();
    }

    /**
     * Open this LOB to reading.
     *
     * @throws SQLException in case of failures.
     */
    private void open() throws SQLException {
        try {
            final File blobFile = this.openBlob();
            this.fs = new FileInputStream(blobFile);
            this.channel = this.fs.getChannel();
        } catch (final IOException ex) {
            throw new SQLException(ex.getMessage(), SQLStates.LOAD_DATA.getValue(), ex);
        }
    }

    /**
     * Open this LOB to reading.
     *
     * @return the {@link File} to read of.
     * @throws SQLException in case of failures.
     */
    private File openBlob() throws SQLException {
        final String name = Utils.removeDB(this.getFile().getName());
        final File[] fileList = this.getFile().getParentFile().listFiles(new TableFilter(connection, name, "mb"));
        if ((fileList == null) || (fileList.length == 0)) {
            throw new SQLException(String.format("Blob file not found for table '%s'", name),
                    SQLStates.LOAD_DATA.getValue());
        }
        if (fileList.length > 1) {
            throw new SQLException(String.format("Many blob files for table '%s'", name),
                    SQLStates.LOAD_DATA.getValue());
        }
        return fileList[0];
    }

    /**
     * Parse this LOB file.
     *
     * @throws SQLException in case of reading failures.
     */
    private void parse() throws SQLException {
        try {
            // First block - always 4k bytes
            this.channel.position(0);
            final ByteBuffer buffer = ByteBuffer.allocate(1);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.clear();
            this.channel.read(buffer);
            buffer.flip();
            final byte headerType = buffer.get();
            if (headerType != 0) {
                throw new SQLException("Invalid blob format for '" + this.getName() + "'",
                        SQLStates.LOAD_DATA.getValue());
            }
            // No read header (while not necessary)
            this.channel.position(BlobTable.HEADER_BLOCK_SIZE);
            this.parsed = true;
        } catch (final IOException ex) {
            throw new SQLException(ex.getMessage(), SQLStates.LOAD_DATA.getValue(), ex);
        }
    }

    /**
     * Parses free blocks.
     *
     * @param startBlockAddress the start block address.
     * @param blockSize         the block size.
     * @throws IOException in case of reading errors.
     */
    private void parseFreeBlock(final long startBlockAddress, final int blockSize) throws IOException {
        this.channel.position(startBlockAddress + (blockSize * BlobTable.HEADER_BLOCK_SIZE));
    }

    /**
     * Parses a single block.
     *
     * @param startBlockAddress the start block address.
     * @param headerType        the header type.
     * @param blockSize         the block size.
     * @throws IOException in case of reading errors.
     */
    private ClobBlock parseSingleBlock(final long startBlockAddress, final byte headerType,
                                       final int blockSize) throws IOException {
        final ByteBuffer blockHead = ByteBuffer.allocate(6);
        blockHead.order(ByteOrder.LITTLE_ENDIAN);
        blockHead.clear();
        this.channel.read(blockHead);
        blockHead.flip();
        final int blobLength = blockHead.getInt();
        // Modifier.
        blockHead.getShort();

        final ByteBuffer blockData = ByteBuffer.allocate(blobLength);
        blockData.order(ByteOrder.LITTLE_ENDIAN);
        blockData.clear();
        this.channel.read(blockData);
        blockData.flip();
        final byte[] values = new byte[blobLength];
        blockData.get(values);
        this.channel.position(startBlockAddress + (blockSize * BlobTable.HEADER_BLOCK_SIZE));
        return new ClobBlock(headerType, new BlockOffset(startBlockAddress, 0), values);
    }

    /**
     * Parses a sub block.
     *
     * @param startBlockAddress the start block address.
     * @param headerType        the header type.
     * @throws IOException in case of reading errors.
     */
    private List<ClobBlock> parseSubBlock(final long startBlockAddress, final byte headerType)
            throws IOException {
        // There are nine more bytes in the header. I have no idea
        // what they contain.
        this.channel.position(this.channel.position() + 9);
        // 0 - this is header block
        int n = 0;
        List<ClobBlock> ret = new ArrayList<>();
        while (n < 64) {
            final ByteBuffer blockPointer = ByteBuffer.allocate(5);
            blockPointer.order(ByteOrder.LITTLE_ENDIAN);
            blockPointer.clear();
            this.channel.read(blockPointer);
            blockPointer.flip();
            // Data offset divided by 16.
            final int offset = blockPointer.get() * 0x10;
            // Data length divided by 16 (rounded up).
            int ln = blockPointer.get() * 0x10;
            blockPointer.getShort();
            // This is reset to 1 by a table restructure.
            // Data length modulo 16.
            final int mdl = blockPointer.get();
            // If offset is zero, then the blob was deleted and
            // the space has been reused for another blob.
            if (offset != 0) {
                final long position = this.channel.position();
                final long start = offset + startBlockAddress;
                ln = (ln - 0x10) + mdl;
                final ByteBuffer blockData = ByteBuffer.allocate(ln);
                blockData.order(ByteOrder.LITTLE_ENDIAN);
                blockData.clear();
                this.channel.position(start);
                this.channel.read(blockData);
                blockData.flip();
                final byte[] values = new byte[ln];
                blockData.get(values);

                ret.add(new ClobBlock(headerType, new BlockOffset(startBlockAddress, n), values));
                this.channel.position(position);
            }
            n++;
        }
        this.channel.position(startBlockAddress + BlobTable.HEADER_BLOCK_SIZE);
        return ret;
    }

    /**
     * Read a single block.
     *
     * @param offset offset of the blob's data block in the MB file and an index
     *               value.
     * @return the CLOB block.
     * @throws SQLException in case of parse errors.
     */
    private ClobBlock readBlock(final BlockOffset offset) throws SQLException {
        while (true) {
            final List<ClobBlock> nextBlocks = this.readNextBlock();
            this.cache.add(nextBlocks);
            final ClobBlock next = this.cache.get(offset);
            if (next != null) {
                return next;
            }
        }
    }

    /**
     * Read the next block in the list.
     *
     * @return true if has no errors.
     * @throws SQLException in case of parse errors.
     */
    private List<ClobBlock> readNextBlock() throws SQLException {
        final List<ClobBlock> blocks = new ArrayList<>();
        try {
            if (this.channel.position() == this.channel.size()) {
                return Collections.emptyList();
            }
            final long startBlockAddress = this.channel.position();
            final ByteBuffer header = ByteBuffer.allocate(3);
            header.order(ByteOrder.LITTLE_ENDIAN);
            header.clear();
            this.channel.read(header);
            header.flip();
            final byte headerType = header.get();
            final int blockSize = header.getShort();

            if (headerType == BlobTable.SINGLE_BLOCK) {
                blocks.add(this.parseSingleBlock(startBlockAddress, headerType, blockSize));
            } else if (headerType == BlobTable.SUB_BLOCK) {
                blocks.addAll(this.parseSubBlock(startBlockAddress, headerType));
            } else if (headerType == BlobTable.FREE_BLOCK) {
                this.parseFreeBlock(startBlockAddress, blockSize);
            } else {
                throw new SQLException("Unsupported CLOB block type: " + headerType,
                        SQLStates.TYPE_NOT_FOUND.getValue());
            }
        } catch (final IOException ex) {
            throw new SQLException(ex.getMessage(), SQLStates.LOAD_DATA.getValue(), ex);
        }
        return blocks;
    }
}
