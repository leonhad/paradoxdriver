package com.googlecode.paradox.metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.googlecode.paradox.utils.AllBlockCache;
import com.googlecode.paradox.utils.ClobBlock;
import com.googlecode.paradox.utils.IBlockCache;
import com.googlecode.paradox.utils.SQLStates;
import com.googlecode.paradox.utils.StringUtils;
import com.googlecode.paradox.utils.filefilters.TableFilter;

/**
 * Read from clob/blob file of PARADOX format.
 *
 * Created by Andre on 11.12.2014.
 */
public class BlobTable extends ParadoxDataFile {

    private static final short FREE_BLOCK = 4;
    private static final int HEADER_BLOCK_SIZE = 0x1000;
    private static final short SINGLE_BLOCK = 2;
    private static final short SUB_BLOCK = 3;

    private final IBlockCache cache;
    private FileChannel channel = null;
    private FileInputStream fs;
    private boolean isEnd;
    private boolean isParsed;
    private int numBlock = 0;

    public BlobTable(final File file, final String name) {
        super(file, StringUtils.removeMb(name));
        cache = new AllBlockCache();
        isEnd = isParsed = false;
        fields = Collections.emptyList();
    }

    public void close() throws SQLException {
        try {
            fs.close();
        } catch (final IOException ex) {
            throw new SQLException(ex.getMessage(), SQLStates.LOAD_DATA, ex);
        }
    }

    /**
     * Calculate block type.
     *
     * We'll refer to the first four bytes after the leader as MB_Offset.
     * MB_Offset is used to locate the blob data.
     *
     * If MB_Offset = 0 then the entire blob is contained in the leader. Take
     * the low-order byte from MB_Offset and call it MB_Index. Change the
     * low-order byte of MB_Offset to zero. If MB_Index is FFh, then MB_Offset
     * contains the offset of a type 02 (SINGLE_BLOCK) block in the MB file.
     *
     * Otherwise, MB_Offset contains the offset of a type 03 (SUB_BLOCK) block
     * in the MB file. MB_Index contains the index of an entry in the Blob
     * Pointer Array in the type 03 block.
     *
     * @param offset
     * @return
     */
    private int getBlockNum(final long offset) {
        final int idx = (int) (offset & 0x0000FF00) >> 8;
        return (idx & 0x0F) * 0xF + (idx & 0xF0) >> 4;
    }

    private byte[] getData(final int blockNum, final short offset) throws SQLException {
        ClobBlock block = cache.get(blockNum, offset);
        if (block != null) {
            return block.getValue();
        }
        try {
            block = readBlock(blockNum, offset);
        } catch (final Exception x) {
            throw new SQLException("Read clob error", SQLStates.LOAD_DATA, x);
        }
        if (block == null) {
            throw new SQLException("Block " + blockNum + " not found. Invalid mb file", SQLStates.LOAD_DATA);
        }
        return block.getValue();
    }

    public boolean isParsed() {
        return isEnd;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    private void open() throws SQLException {
        try {
            final File blobFile = openBlob();
            fs = new FileInputStream(blobFile);
            channel = fs.getChannel();
        } catch (final IOException ex) {
            throw new SQLException(ex.getMessage(), SQLStates.LOAD_DATA, ex);
        }
    }

    private File openBlob() throws SQLException {
        final String name = StringUtils.removeDb(getFile().getName());
        final File[] fileList = getFile().getParentFile().listFiles(new TableFilter(name, "mb"));
        if (fileList == null || fileList.length == 0) {
            throw new SQLException(String.format("Blob file not found for table '%s'", name), SQLStates.LOAD_DATA);
        }
        if (fileList.length > 1) {
            throw new SQLException(String.format("Many blob files for table '%s'", name), SQLStates.LOAD_DATA);
        }
        return fileList[0];
    }

    private void parse() throws SQLException {
        try {
            // First block - always 4k bytes
            channel.position(0);
            final ByteBuffer buffer = ByteBuffer.allocate(1);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.clear();
            channel.read(buffer);
            buffer.flip();
            final byte headerType = buffer.get();
            if (headerType != 0) {
                throw new SQLException("Invalid blob format for '" + getName() + "'", SQLStates.LOAD_DATA);
            }
            // No read header (while not necessary)
            channel.position(HEADER_BLOCK_SIZE);
            numBlock++;
            isParsed = true;
        } catch (final IOException ex) {
            throw new SQLException(ex.getMessage(), SQLStates.LOAD_DATA, ex);
        }
    }

    /**
     * Read length bytes from offset position in mb file.
     *
     * @param pOffset
     *            offset of the blob's data block in the MB file and an index
     *            value.
     * @return the data values.
     * @throws SQLException
     *             in case of parse errors.
     */
    public byte[] read(final long pOffset) throws SQLException {
        final short offset = (short) (pOffset & 0xFF);

        final int blockNum = getBlockNum(pOffset);
        if (!isParsed) {
            open();
            parse();
        }
        return getData(blockNum, offset);
    }

    private ClobBlock readBlock(final int blockNum, final short offset) throws SQLException {
        final List<ClobBlock> nextBlocks = new ArrayList<>(1);
        while (readNextBlock(nextBlocks)) {
            cache.add(nextBlocks);
            final ClobBlock next = cache.get(blockNum, offset);
            if (next != null) {
                return next;
            }
        }
        isEnd = true;
        return null;
    }

    private boolean readNextBlock(final List<ClobBlock> blocks) throws SQLException {
        try {
            if (channel.position() == channel.size()) {
                return false;
            }
            final long startBlockAddress = channel.position();
            final ByteBuffer header = ByteBuffer.allocate(3);
            header.order(ByteOrder.LITTLE_ENDIAN);
            header.clear();
            channel.read(header);
            header.flip();
            final byte headerType = header.get();
            final short blockSize = header.getShort();

            byte[] values;
            if (headerType == SINGLE_BLOCK) {
                final ByteBuffer sblockHead = ByteBuffer.allocate(6);
                sblockHead.order(ByteOrder.LITTLE_ENDIAN);
                sblockHead.clear();
                channel.read(sblockHead);
                sblockHead.flip();
                final int blobLength = sblockHead.getInt();
                // Modificator
                sblockHead.getShort();

                final ByteBuffer sblockData = ByteBuffer.allocate(blobLength);
                sblockData.order(ByteOrder.LITTLE_ENDIAN);
                sblockData.clear();
                channel.read(sblockData);
                sblockData.flip();
                values = new byte[blobLength];
                sblockData.get(values);
                blocks.add(new ClobBlock(numBlock, headerType, (short) 0xFF, values));
                numBlock++;
                channel.position(startBlockAddress + blockSize * HEADER_BLOCK_SIZE);
            } else if (headerType == SUB_BLOCK) {
                // There are nine more bytes in the header. I have no idea
                // what they contain.
                channel.position(channel.position() + 9);
                short n = 0; // 0 - this is header block
                while (n < 64) {
                    final ByteBuffer blockPointer = ByteBuffer.allocate(5);
                    blockPointer.order(ByteOrder.LITTLE_ENDIAN);
                    blockPointer.clear();
                    channel.read(blockPointer);
                    blockPointer.flip();
                    final short offset = (short) (blockPointer.get() * 0x10); // Data
                    // offset
                    // divided
                    // by
                    // 16
                    int ln = blockPointer.get() * 0x10; // Data length divided
                    // by 16 (rounded up)
                    blockPointer.getShort();
                    // This is reset to 1 by a table restructure.
                    final int mdl = blockPointer.get(); // Data length modulo
                    // 16.
                    // If offset is zero, then the blob was deleted and
                    // the space has been reused for another blob
                    if (offset != 0) {
                        final long position = channel.position();
                        final long start = offset + startBlockAddress;
                        ln = ln - 0x10 + mdl;
                        final ByteBuffer sblockData = ByteBuffer.allocate(ln);
                        sblockData.order(ByteOrder.LITTLE_ENDIAN);
                        sblockData.clear();
                        channel.position(start);
                        channel.read(sblockData);
                        sblockData.flip();
                        values = new byte[ln];
                        sblockData.get(values);

                        blocks.add(new ClobBlock(numBlock, headerType, n, values));
                        channel.position(position);
                    }
                    n++;
                }
                channel.position(startBlockAddress + HEADER_BLOCK_SIZE);
                numBlock++;
            } else if (headerType == FREE_BLOCK) {
                blocks.add(new ClobBlock(numBlock, headerType, (short) 0));
                channel.position(startBlockAddress + blockSize * HEADER_BLOCK_SIZE);
                numBlock++;
            } else {
                throw new SQLException("Unsupported CLOB block type: " + headerType, SQLStates.TYPE_NOT_FOUND);
            }
        } catch (final IOException ex) {
            throw new SQLException(ex.getMessage(), SQLStates.LOAD_DATA, ex);
        }
        return true;
    }
}
