package com.googlecode.paradox.metadata;

import com.googlecode.paradox.utils.*;
import com.googlecode.paradox.utils.filefilters.TableFilter;

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

/**
 * Read from clob/blob file of PARADOX format.
 *
 * Created by Andre on 11.12.2014.
 */
public class BlobTable extends AbstractTable {

    private static final int HEADER_BLOCK_SIZE = 0x1000;
    private static final short SINGLE_BLOCK = 2;
    private static final short SUB_BLOCK = 3;
    private static final short FREE_BLOCK = 4;

    private boolean isParsed;
    private boolean isEnd;
    private FileInputStream fs;
    private FileChannel channel = null;
    private final IBlockCache cache;
    private int numBlock = 0;

    public BlobTable(final File file, final String name) {
        super(file, StringUtils.removeMb(name));
        this.cache = new AllBlockCache();
        isEnd = isParsed = false;
        fields = Collections.emptyList();
    }

    @Override
    public boolean isValid() {
        return false;
    }

    /**
     * Read length bytes from offset position in mb file.
     *
     * @param pOffset offset of the blob's data block in the MB file and an
     * index value.
     * @return the data values.
     * @throws SQLException in case of parse errors.
     */
    public byte[] read(long pOffset) throws SQLException {
        short offset = (short) (pOffset & 0xFF);

//        if (offset == 0xFF) {
//            blockType = SINGLE_BLOCK;
//        } else {
//            blockType = SUB_BLOCK;
//        }

        int blockNum = getBlockNum(pOffset);
        if (!isParsed) {
            open();
            parse();
        }
        return getData(blockNum, offset);
    }

    public boolean isParsed() {
        return isEnd;
    }

    public void close() throws SQLException {
        try {
            fs.close();
        } catch (IOException ex) {
            throw new SQLException(ex.getMessage(), SQLStates.LOAD_DATA, ex);
        }
    }

    private void open() throws SQLException {
        try {
            File blobFile = openBlob();
            fs = new FileInputStream(blobFile);
            channel = fs.getChannel();
        } catch (IOException ex) {
            throw new SQLException(ex.getMessage(), SQLStates.LOAD_DATA, ex);
        }
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
            byte headerType = buffer.get();
            if (headerType != 0) {
                throw new SQLException("Invalid blob format for '" + getName() + "'", SQLStates.LOAD_DATA);
            }
            // No read header (while not necessary)
            channel.position(HEADER_BLOCK_SIZE);
            numBlock++;
            isParsed = true;
        } catch (IOException ex) {
            throw new SQLException(ex.getMessage(), SQLStates.LOAD_DATA, ex);
        }
    }

    private File openBlob() throws SQLException {
        String name = StringUtils.removeDb(getFile().getName());
        final File[] fileList = getFile().getParentFile().listFiles(new TableFilter(name, "mb"));
        if (fileList == null || fileList.length == 0) {
            throw new SQLException(String.format("Blob file not found for table '%s'", name), SQLStates.LOAD_DATA);
        }
        if (fileList.length > 1) {
            throw new SQLException(String.format("Many blob files for table '%s'", name), SQLStates.LOAD_DATA);
        }
        return fileList[0];
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
    private int getBlockNum(long offset) {
        int idx = (int) (offset & 0x0000FF00) >> 8;
        return (idx & 0x0F) * 0xF + (idx & 0xF0) >> 4;
    }

    private byte[] getData(int blockNum, short offset) throws SQLException {
        ClobBlock block = cache.get(blockNum, offset);
        if (block != null) {
            return block.getValue();
        }
        try {
            block = readBlock(blockNum, offset);
        } catch (Exception x) {
            throw new SQLException("Read clob error", SQLStates.LOAD_DATA, x);
        }
        if (block == null) {
            throw new SQLException("Block " + blockNum + " not found. Invalid mb file", SQLStates.LOAD_DATA);
        }
        return block.getValue();
    }

    private ClobBlock readBlock(int blockNum, short offset) throws SQLException {
        List<ClobBlock> nextBlocks = new ArrayList<ClobBlock>(1);
        while (readNextBlock(nextBlocks)) {
            cache.add(nextBlocks);
            ClobBlock next = (cache.get(blockNum, offset));
            if (next != null) {
                return next;
            }
        }
        isEnd = true;
        return null;
    }

    private boolean readNextBlock(List<ClobBlock> blocks) throws SQLException {
        try {
            if (channel.position() == channel.size()) {
                return false;
            }
            long startBlockAddress = channel.position();
            ByteBuffer header = ByteBuffer.allocate(3);
            header.order(ByteOrder.LITTLE_ENDIAN);
            header.clear();
            channel.read(header);
            header.flip();
            byte headerType = header.get();
            short blockSize = header.getShort();

            byte[] values;
            if (headerType == SINGLE_BLOCK) {
                ByteBuffer sblockHead = ByteBuffer.allocate(6);
                sblockHead.order(ByteOrder.LITTLE_ENDIAN);
                sblockHead.clear();
                channel.read(sblockHead);
                sblockHead.flip();
                int blobLength = sblockHead.getInt();
                // Modificator
                sblockHead.getShort();

                ByteBuffer sblockData = ByteBuffer.allocate(blobLength);
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
                //There are nine more bytes in the header. I have no idea
                // what they contain.
                channel.position(channel.position() + 9);
                short n = 0; // 0 - this is header block
                while (n < 64) {
                    ByteBuffer blockPointer = ByteBuffer.allocate(5);
                    blockPointer.order(ByteOrder.LITTLE_ENDIAN);
                    blockPointer.clear();
                    channel.read(blockPointer);
                    blockPointer.flip();
                    short offset = (short) (blockPointer.get() * 0x10); // Data offset divided by 16
                    int ln = blockPointer.get() * 0x10; //Data length divided by 16 (rounded up)
                    int mdf = blockPointer.getShort(); // Modification number from blob header
                    //This is reset to 1 by a table restructure.
                    int mdl = blockPointer.get(); // Data length modulo 16.
                    // If offset is zero, then the blob was deleted and
                    // the space has been reused for another blob
                    if (offset != 0) {
                        long position = channel.position();
                        long start = offset + startBlockAddress;
                        ln = ln - 0x10 + mdl;
                        ByteBuffer sblockData = ByteBuffer.allocate(ln);
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
                throw new SQLException("Unsupported CLOB block type: " + headerType, SQLStates.CLOB_READ);
            }
        } catch (IOException ex) {
            throw new SQLException(ex.getMessage(), SQLStates.LOAD_DATA, ex);
        }
        return true;
    }
}
