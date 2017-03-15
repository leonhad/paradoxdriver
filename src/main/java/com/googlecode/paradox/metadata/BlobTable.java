/*
 * BlobTable.java 12/22/2014 Copyright (C) 2014 Leonardo Alves da Costa This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.metadata;

import com.googlecode.paradox.utils.AllBlockCache;
import com.googlecode.paradox.utils.ClobBlock;
import com.googlecode.paradox.utils.IBlockCache;
import com.googlecode.paradox.utils.SQLStates;
import com.googlecode.paradox.utils.Utils;
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
     * Number of LOB blocks.
     */
    private int numBlock;
    
    /**
     * If this LOB is already parsed.
     */
    private boolean parsed;
    
    /**
     * Creates a new instance.
     *
     * @param file
     *            the file to read of.
     * @param name
     *            the LOBs name.
     */
    BlobTable(final File file, final String name) {
        super(file, Utils.removeMb(name));
        this.cache = new AllBlockCache();
        this.parsed = false;
        this.fields = Collections.emptyList();
    }
    
    /**
     * Calculate block type. We'll refer to the first four bytes after the
     * leader as MB_Offset. MB_Offset is used to locate the blob data. If
     * MB_Offset = 0 then the entire blob is contained in the leader. Take the
     * low-order byte from MB_Offset and call it MB_Index. Change the low-order
     * byte of MB_Offset to zero. If MB_Index is FFh, then MB_Offset contains
     * the offset of a type 02 (SINGLE_BLOCK) block in the MB file. Otherwise,
     * MB_Offset contains the offset of a type 03 (SUB_BLOCK) block in the MB
     * file. MB_Index contains the index of an entry in the Blob Pointer Array
     * in the type 03 block.
     *
     * @param offset
     *            offset to read on.
     * @return number of blocks read.
     */
    private static int getBlockNum(final long offset) {
        final int idx = (int) (offset & 0xFF00) >> 8;
        return (((idx & 0x0F) * 0xF) + (idx & 0xF0)) >> 4;
    }
    
    /**
     * Close this LOB reference.
     *
     * @throws SQLException
     *             in case of I/O errors.
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
     * @param pOffset
     *            offset of the blob's data block in the MB file and an index
     *            value.
     * @return the data values.
     * @throws SQLException
     *             in case of parse errors.
     */
    public byte[] read(final long pOffset) throws SQLException {
        final long offset = pOffset & 0xFF;
        
        final int blockNum = BlobTable.getBlockNum(pOffset);
        if (!this.parsed) {
            this.open();
            this.parse();
        }
        return this.getData(blockNum, (int) offset);
    }
    
    /**
     * Gets the data from LOB file.
     *
     * @param blockNum
     *            block reference.
     * @param offset
     *            offset to read of.
     * @return the data array.
     * @throws SQLException
     *             in case of reading failures.
     */
    private byte[] getData(final int blockNum, final int offset) throws SQLException {
        ClobBlock block = this.cache.get(blockNum, offset);
        if (block != null) {
            return block.getValue();
        }
        
        block = this.readBlock(blockNum, offset);
        if (block == null) {
            throw new SQLException("Block " + blockNum + " not found. Invalid mb file", SQLStates.LOAD_DATA.getValue());
        }
        return block.getValue();
    }
    
    /**
     * Open this LOB to reading.
     *
     * @throws SQLException
     *             in case of failures.
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
     * @throws SQLException
     *             in case of failures.
     */
    private File openBlob() throws SQLException {
        final String name = Utils.removeDb(this.getFile().getName());
        final File[] fileList = this.getFile().getParentFile().listFiles(new TableFilter(name, "mb"));
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
     * @throws SQLException
     *             in case of reading failures.
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
            this.numBlock++;
            this.parsed = true;
        } catch (final IOException ex) {
            throw new SQLException(ex.getMessage(), SQLStates.LOAD_DATA.getValue(), ex);
        }
    }
    
    /**
     * Parses free blocks.
     *
     * @param blocks
     *            the list of CLOB blocks.
     * @param startBlockAddress
     *            the start block address.
     * @param headerType
     *            the header type.
     * @param blockSize
     *            the block size.
     * @throws IOException
     *             in case of reading errors.
     */
    private void parseFreeBlock(final List<ClobBlock> blocks, final long startBlockAddress, final byte headerType,
            final int blockSize) throws IOException {
        blocks.add(new ClobBlock(this.numBlock, headerType, 0));
        this.channel.position(startBlockAddress + (blockSize * BlobTable.HEADER_BLOCK_SIZE));
        this.numBlock++;
    }
    
    /**
     * Parses a single block.
     *
     * @param blocks
     *            the CLOB block list.
     * @param startBlockAddress
     *            the start block address.
     * @param headerType
     *            the header type.
     * @param blockSize
     *            the block size.
     * @throws IOException
     *             in case of reading errors.
     */
    private void parseSingleBlock(final List<ClobBlock> blocks, final long startBlockAddress, final byte headerType,
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
        blocks.add(new ClobBlock(this.numBlock, headerType, 0xFF, values));
        this.numBlock++;
        this.channel.position(startBlockAddress + (blockSize * BlobTable.HEADER_BLOCK_SIZE));
    }
    
    /**
     * Parses a sub block.
     *
     * @param blocks
     *            the CLOB block list.
     * @param startBlockAddress
     *            the start block address.
     * @param headerType
     *            the header type.
     * @throws IOException
     *             in case of reading errors.
     */
    private void parseSubBlock(final List<ClobBlock> blocks, final long startBlockAddress, final byte headerType)
            throws IOException {
        // There are nine more bytes in the header. I have no idea
        // what they contain.
        this.channel.position(this.channel.position() + 9);
        // 0 - this is header block
        int n = 0;
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
                
                blocks.add(new ClobBlock(this.numBlock, headerType, n, values));
                this.channel.position(position);
            }
            n++;
        }
        this.channel.position(startBlockAddress + BlobTable.HEADER_BLOCK_SIZE);
        this.numBlock++;
    }
    
    /**
     * Read a single block.
     *
     * @param blockNum
     *            block reference to read.
     * @param offset
     *            offset of the blob's data block in the MB file and an index
     *            value.
     * @return the CLOB block.
     * @throws SQLException
     *             in case of parse errors.
     */
    private ClobBlock readBlock(final int blockNum, final int offset) throws SQLException {
        final List<ClobBlock> nextBlocks = new ArrayList<>(1);
        while (this.readNextBlock(nextBlocks)) {
            this.cache.add(nextBlocks);
            final ClobBlock next = this.cache.get(blockNum, offset);
            if (next != null) {
                return next;
            }
        }
        return null;
    }
    
    /**
     * Read the next block in the list.
     *
     * @param blocks
     *            blocks reference.
     * @return true if has no errors.
     * @throws SQLException
     *             in case of parse errors.
     */
    private boolean readNextBlock(final List<ClobBlock> blocks) throws SQLException {
        try {
            if (this.channel.position() == this.channel.size()) {
                return false;
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
                this.parseSingleBlock(blocks, startBlockAddress, headerType, blockSize);
            } else if (headerType == BlobTable.SUB_BLOCK) {
                this.parseSubBlock(blocks, startBlockAddress, headerType);
            } else if (headerType == BlobTable.FREE_BLOCK) {
                this.parseFreeBlock(blocks, startBlockAddress, headerType, blockSize);
            } else {
                throw new SQLException("Unsupported CLOB block type: " + headerType,
                        SQLStates.TYPE_NOT_FOUND.getValue());
            }
        } catch (final IOException ex) {
            throw new SQLException(ex.getMessage(), SQLStates.LOAD_DATA.getValue(), ex);
        }
        return true;
    }
}
