package com.googlecode.paradox.metadata;

import com.googlecode.paradox.utils.StringUtils;

import java.io.File;
import java.util.List;

/**
 * Defines the paradox table default structure
 *
 * @author Leonardo Alves da Costa
 * @since 03/12/2009
 * @version 1.1
 */
public abstract class AbstractTable {

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

    public AbstractTable(final File file, final String name) {
        this.file = file;
        this.name = StringUtils.removeDb(name);
    }
    
    public abstract boolean isValid();

    public ParadoxField findField(String name) {
        if (fields != null) {
            for (ParadoxField field : fields) {
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

    public void setRecordSize(int recordSize) {
        this.recordSize = recordSize;
    }

    public int getHeaderSize() {
        return headerSize;
    }

    public void setHeaderSize(int headerSize) {
        this.headerSize = headerSize;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(byte blockSize) {
        this.blockSize = blockSize;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getUsedBlocks() {
        return usedBlocks;
    }

    public void setUsedBlocks(int usedBlocks) {
        this.usedBlocks = usedBlocks;
    }

    public int getTotalBlocks() {
        return totalBlocks;
    }

    public void setTotalBlocks(int totalBlocks) {
        this.totalBlocks = totalBlocks;
    }

    public int getFirstBlock() {
        return firstBlock;
    }

    public void setFirstBlock(int firstBlock) {
        this.firstBlock = firstBlock;
    }

    public int getLastBlock() {
        return lastBlock;
    }

    public void setLastBlock(int lastBlock) {
        this.lastBlock = lastBlock;
    }

    public int getFieldCount() {
        return fieldCount;
    }

    public void setFieldCount(int fieldCount) {
        this.fieldCount = fieldCount;
    }

    public int getPrimaryFieldCount() {
        return primaryFieldCount;
    }

    public void setPrimaryFieldCount(int primaryFieldCount) {
        this.primaryFieldCount = primaryFieldCount;
    }

    public byte getWriteProtected() {
        return writeProtected;
    }

    public void setWriteProtected(byte writeProtected) {
        this.writeProtected = writeProtected;
    }

    public byte getVersionId() {
        return versionId;
    }

    public void setVersionId(byte versionId) {
        this.versionId = versionId;
    }

    public int getAutoIncrementValue() {
        return autoIncrementValue;
    }

    public void setAutoIncrementValue(int autoIncrementValue) {
        this.autoIncrementValue = autoIncrementValue;
    }

    public int getFirstFreeBlock() {
        return firstFreeBlock;
    }

    public void setFirstFreeBlock(int firstFreeBlock) {
        this.firstFreeBlock = firstFreeBlock;
    }

    public byte getReferencialIntegrity() {
        return referencialIntegrity;
    }

    public void setReferencialIntegrity(byte referencialIntegrity) {
        this.referencialIntegrity = referencialIntegrity;
    }

    public List<ParadoxField> getFields() {
        return fields;
    }

    public void setFields(List<ParadoxField> fields) {
        this.fields = fields;
    }
}
