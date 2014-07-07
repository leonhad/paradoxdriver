package org.paradox.metadata;

import java.io.File;
import java.nio.charset.Charset;
import static java.nio.charset.Charset.forName;
import java.util.ArrayList;

/**
 *
 * @author 72330554168
 */
public class ParadoxIndex extends AbstractTable {

    private int recordSize;
    private int headerSize;
    private byte type = -1;
    private byte blockSize;
    private int rowCount;
    private int usedBlocks;
    private int totalBlocks;
    private int firstBlock;
    private int lastBlock;
    private int fieldCount;
    private int primaryFieldCount;
    private byte writeProtected;
    private byte versionId;
    private int autoIncrementValue;
    private int firstFreeBlock;
    private byte referencialIntegrity;
    private String sortOrderID;
    private String fatherName;
    private ArrayList<ParadoxField> fields;
    private ArrayList<Short> fieldsOrder;
    private Charset charset = forName("Cp437");

    public ParadoxIndex(final File file, final String name) {
        super(file, name);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof ParadoxTable) {
            return getName().equals(((AbstractTable) obj).getName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    public ArrayList<ParadoxField> getPrimaryKeys() {
        final ArrayList<ParadoxField> ret = new ArrayList<ParadoxField>();
        for (int loop = 0; loop < primaryFieldCount; loop++) {
            ret.add(fields.get(loop));
        }
        return ret;
    }

    public String getOrder() {
        switch (referencialIntegrity) {
            case 0:
            case 1:
            case 0x20:
            case 0x21:
                return "A";
            case 0x10:
            case 0x11:
            case 0x30:
                return "D";
            default:
                return "A";
        }
    }

    /**
     * If this table is valid
     *
     * @return true if this table is valid
     */
    @Override
    public boolean isValid() {
        switch (type) {
            case 3:
            case 5:
            case 6:
            case 8:
                return true;
            default:
                return false;
        }
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
    public void setRecordSize(final int recordSize) {
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
    public void setHeaderSize(final int headerSize) {
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
    public void setType(final byte type) {
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
    public void setBlockSize(final byte blockSize) {
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
    public void setRowCount(final int rowCount) {
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
    public void setUsedBlocks(final int usedBlocks) {
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
    public void setTotalBlocks(final int totalBlocks) {
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
    public void setFirstBlock(final int firstBlock) {
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
    public void setLastBlock(final int lastBlock) {
        this.lastBlock = lastBlock;
    }

    /**
     * @return the fieldCount
     */
    public int getFieldCount() {
        return fieldCount;
    }

    /**
     * @param fieldCount the fieldCount to set
     */
    public void setFieldCount(final int fieldCount) {
        this.fieldCount = fieldCount;
    }

    /**
     * @return the primaryFieldCount
     */
    public int getPrimaryFieldCount() {
        return primaryFieldCount;
    }

    /**
     * @param primaryFieldCount the primaryFieldCount to set
     */
    public void setPrimaryFieldCount(final int primaryFieldCount) {
        this.primaryFieldCount = primaryFieldCount;
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
    public void setWriteProtected(final byte writeProtected) {
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
    public void setVersionId(final byte versionId) {
        this.versionId = versionId;
    }

    /**
     * @return the autoIncrementValue
     */
    public int getAutoIncrementValue() {
        return autoIncrementValue;
    }

    /**
     * @param autoIncrementValue the autoIncrementValue to set
     */
    public void setAutoIncrementValue(final int autoIncrementValue) {
        this.autoIncrementValue = autoIncrementValue;
    }

    /**
     * @return the firstFreeBlock
     */
    public int getFirstFreeBlock() {
        return firstFreeBlock;
    }

    /**
     * @param firstFreeBlock the firstFreeBlock to set
     */
    public void setFirstFreeBlock(final int firstFreeBlock) {
        this.firstFreeBlock = firstFreeBlock;
    }

    /**
     * @return the referencialIntegrity
     */
    public byte getReferencialIntegrity() {
        return referencialIntegrity;
    }

    /**
     * @param referencialIntegrity the referencialIntegrity to set
     */
    public void setReferencialIntegrity(final byte referencialIntegrity) {
        this.referencialIntegrity = referencialIntegrity;
    }

    /**
     * @return the fields
     */
    @Override
    public ArrayList<ParadoxField> getFields() {
        return fields;
    }

    /**
     * @return the fieldsOrder
     */
    public ArrayList<Short> getFieldsOrder() {
        return fieldsOrder;
    }

    /**
     * @return the charset
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * @param charset the charset to set
     */
    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    /**
     * @return the sortOrderID
     */
    public String getSortOrderID() {
        return sortOrderID;
    }

    /**
     * @param sortOrderID the sortOrderID to set
     */
    public void setSortOrderID(final String sortOrderID) {
        this.sortOrderID = sortOrderID;
    }

    /**
     * @return the fatherName
     */
    public String getFatherName() {
        return fatherName;
    }

    /**
     * @param fatherName the fatherName to set
     */
    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    /**
     * @param fields the fields to set
     */
    public void setFields(ArrayList<ParadoxField> fields) {
        this.fields = fields;
    }

    /**
     * @param fieldsOrder the fieldsOrder to set
     */
    public void setFieldsOrder(ArrayList<Short> fieldsOrder) {
        this.fieldsOrder = fieldsOrder;
    }
}
