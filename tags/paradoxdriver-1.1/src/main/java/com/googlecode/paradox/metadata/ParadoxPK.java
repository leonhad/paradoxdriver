package com.googlecode.paradox.metadata;

/**
 *
 * @author 72330554168
 */
public class ParadoxPK {
    private String name;
    private int recordSize;
    private int headerSize;
    private byte type = -1;
    private byte blockSize;
    private int rowCount;
    private int usedBlocks;
    private int totalBlocks;
    private int firstBlock;
    private int lastBlock;
    private int indexFieldNumber;
    private byte writeProtected;
    private byte versionId;

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof ParadoxPK) {
            return getName().equals(((ParadoxPK) obj).getName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    /**
     * If this table is valid
     *
     * @return true if this table is valid
     */
    public boolean isValid() {
        return type == 1;
    }


    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the recordSize
     */
    public int getRecordSize() {
        return recordSize;
    }

    /**
     * @param recordSize the recordSize to set
     */
    public void setRecordSize(int recordSize) {
        this.recordSize = recordSize;
    }

    /**
     * @return the headerSize
     */
    public int getHeaderSize() {
        return headerSize;
    }

    /**
     * @param headerSize the headerSize to set
     */
    public void setHeaderSize(int headerSize) {
        this.headerSize = headerSize;
    }

    /**
     * @return the type
     */
    public byte getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(byte type) {
        this.type = type;
    }

    /**
     * @return the blockSize
     */
    public byte getBlockSize() {
        return blockSize;
    }

    /**
     * @param blockSize the blockSize to set
     */
    public void setBlockSize(byte blockSize) {
        this.blockSize = blockSize;
    }

    /**
     * @return the rowCount
     */
    public int getRowCount() {
        return rowCount;
    }

    /**
     * @param rowCount the rowCount to set
     */
    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    /**
     * @return the usedBlocks
     */
    public int getUsedBlocks() {
        return usedBlocks;
    }

    /**
     * @param usedBlocks the usedBlocks to set
     */
    public void setUsedBlocks(int usedBlocks) {
        this.usedBlocks = usedBlocks;
    }

    /**
     * @return the totalBlocks
     */
    public int getTotalBlocks() {
        return totalBlocks;
    }

    /**
     * @param totalBlocks the totalBlocks to set
     */
    public void setTotalBlocks(int totalBlocks) {
        this.totalBlocks = totalBlocks;
    }

    /**
     * @return the firstBlock
     */
    public int getFirstBlock() {
        return firstBlock;
    }

    /**
     * @param firstBlock the firstBlock to set
     */
    public void setFirstBlock(int firstBlock) {
        this.firstBlock = firstBlock;
    }

    /**
     * @return the lastBlock
     */
    public int getLastBlock() {
        return lastBlock;
    }

    /**
     * @param lastBlock the lastBlock to set
     */
    public void setLastBlock(int lastBlock) {
        this.lastBlock = lastBlock;
    }

    /**
     * @return the indexFieldNumber
     */
    public int getIndexFieldNumber() {
        return indexFieldNumber;
    }

    /**
     * @param indexFieldNumber the indexFieldNumber to set
     */
    public void setIndexFieldNumber(int indexFieldNumber) {
        this.indexFieldNumber = indexFieldNumber;
    }

    /**
     * @return the writeProtected
     */
    public byte getWriteProtected() {
        return writeProtected;
    }

    /**
     * @param writeProtected the writeProtected to set
     */
    public void setWriteProtected(byte writeProtected) {
        this.writeProtected = writeProtected;
    }

    /**
     * @return the versionId
     */
    public byte getVersionId() {
        return versionId;
    }

    /**
     * @param versionId the versionId to set
     */
    public void setVersionId(byte versionId) {
        this.versionId = versionId;
    }

}
