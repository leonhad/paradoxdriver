package com.googlecode.paradox.metadata;

import java.io.File;
import java.util.List;

import com.googlecode.paradox.utils.StringUtils;

/**
 * Defines the paradox default file structure.
 *
 * @author Leonardo Alves da Costa
 * @since 03/12/2009
 * @version 1.2
 */
public abstract class ParadoxDataFile {

    protected int recordSize;
    protected int headerSize;
    protected byte type = -1;
    protected byte blockSize;
    protected int rowCount;
    protected int usedBlocks;
    protected int totalBlocks;
    protected int firstBlock;
    protected int lastBlock;
    protected int fieldCount;
    protected int primaryFieldCount;
    protected byte writeProtected;
    protected byte versionId;
    protected int autoIncrementValue;
    protected int firstFreeBlock;
    protected byte referencialIntegrity;
    protected List<ParadoxField> fields;

    private final File file;
    private String name;

    public ParadoxDataFile(final File file, final String name) {
        this.file = file;
        this.name = StringUtils.removeDb(name);
    }

    /**
     * Get the integrity check.
     *
     * @return if this file is valid.
     */
    public abstract boolean isValid();

    public ParadoxField findField(final String name) {
        if (fields != null) {
            for (final ParadoxField field : fields) {
                if (field.getName().equalsIgnoreCase(name)) {
                    return field;
                }
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public File getFile() {
        return file;
    }

    public int getRecordSize() {
        return recordSize;
    }

    public void setRecordSize(final int recordSize) {
        this.recordSize = recordSize;
    }

    public int getHeaderSize() {
        return headerSize;
    }

    public void setHeaderSize(final int headerSize) {
        this.headerSize = headerSize;
    }

    public byte getType() {
        return type;
    }

    public void setType(final byte type) {
        this.type = type;
    }

    public byte getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(final byte blockSize) {
        this.blockSize = blockSize;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(final int rowCount) {
        this.rowCount = rowCount;
    }

    public int getUsedBlocks() {
        return usedBlocks;
    }

    public void setUsedBlocks(final int usedBlocks) {
        this.usedBlocks = usedBlocks;
    }

    public int getTotalBlocks() {
        return totalBlocks;
    }

    public void setTotalBlocks(final int totalBlocks) {
        this.totalBlocks = totalBlocks;
    }

    public int getFirstBlock() {
        return firstBlock;
    }

    public void setFirstBlock(final int firstBlock) {
        this.firstBlock = firstBlock;
    }

    public int getLastBlock() {
        return lastBlock;
    }

    public void setLastBlock(final int lastBlock) {
        this.lastBlock = lastBlock;
    }

    public int getFieldCount() {
        return fieldCount;
    }

    public void setFieldCount(final int fieldCount) {
        this.fieldCount = fieldCount;
    }

    public int getPrimaryFieldCount() {
        return primaryFieldCount;
    }

    public void setPrimaryFieldCount(final int primaryFieldCount) {
        this.primaryFieldCount = primaryFieldCount;
    }

    public byte getWriteProtected() {
        return writeProtected;
    }

    public void setWriteProtected(final byte writeProtected) {
        this.writeProtected = writeProtected;
    }

    public byte getVersionId() {
        return versionId;
    }

    public void setVersionId(final byte versionId) {
        this.versionId = versionId;
    }

    public int getAutoIncrementValue() {
        return autoIncrementValue;
    }

    public void setAutoIncrementValue(final int autoIncrementValue) {
        this.autoIncrementValue = autoIncrementValue;
    }

    public int getFirstFreeBlock() {
        return firstFreeBlock;
    }

    public void setFirstFreeBlock(final int firstFreeBlock) {
        this.firstFreeBlock = firstFreeBlock;
    }

    public byte getReferencialIntegrity() {
        return referencialIntegrity;
    }

    public void setReferencialIntegrity(final byte referencialIntegrity) {
        this.referencialIntegrity = referencialIntegrity;
    }

    public List<ParadoxField> getFields() {
        return fields;
    }

    public void setFields(final List<ParadoxField> fields) {
        this.fields = fields;
    }
}
