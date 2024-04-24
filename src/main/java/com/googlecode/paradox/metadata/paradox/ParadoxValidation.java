package com.googlecode.paradox.metadata.paradox;

/**
 * Stores a validation data file.
 *
 * @since 1.6.1
 */
public class ParadoxValidation {

    /**
     * Version ID of this file.
     */
    private byte versionId;

    /**
     * Validation count.
     */
    private byte count;

    /**
     * File count in this file.
     */
    private int fieldCount;

    private String[] fieldNames;

    private String originalTableName;

    public byte getVersionId() {
        return versionId;
    }

    public void setVersionId(byte versionId) {
        this.versionId = versionId;
    }

    public byte getCount() {
        return count;
    }

    public void setCount(byte count) {
        this.count = count;
    }

    public int getFieldCount() {
        return fieldCount;
    }

    public void setFieldCount(int fieldCount) {
        this.fieldCount = fieldCount;
    }

    public String[] getFieldNames() {
        return fieldNames;
    }

    public void setFieldNames(String[] fieldNames) {
        this.fieldNames = fieldNames;
    }

    public String getOriginalTableName() {
        return originalTableName;
    }

    public void setOriginalTableName(String originalTableName) {
        this.originalTableName = originalTableName;
    }
}
