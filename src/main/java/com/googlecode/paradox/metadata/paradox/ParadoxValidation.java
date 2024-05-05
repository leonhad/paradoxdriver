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

package com.googlecode.paradox.metadata.paradox;

import com.googlecode.paradox.data.ValidationField;

/**
 * Stores a validation data file.
 *
 * @since 1.6.2
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
     * the table change count.
     */
    private byte tableChangeCount;

    /**
     * File count in this file.
     */
    private int fieldCount;

    /**
     * The footer offset.
     */
    private int footerOffset;

    /**
     * The referential integrity offset.
     */
    private int referentialIntegrityOffset;

    /**
     * The validation field list.
     */
    private ValidationField[] fields;

    /**
     * The referential integrity list.
     */
    private ParadoxReferentialIntegrity[] referentialIntegrity;

    /**
     * The original table name.
     */
    private String originalTableName;

    /**
     * Gets the validation version ID.
     *
     * @return the validation version ID.
     */
    public byte getVersionId() {
        return versionId;
    }

    /**
     * Sets the validation version ID.
     *
     * @param versionId the validation version ID.
     */
    public void setVersionId(byte versionId) {
        this.versionId = versionId;
    }

    /**
     * Gets the validation count.
     *
     * @return the validation count.
     */
    public byte getCount() {
        return count;
    }

    /**
     * Sets the validation count.
     *
     * @param count the validation count.
     */
    public void setCount(byte count) {
        this.count = count;
    }

    /**
     * Gets the field count.
     *
     * @return the field count.
     */
    public int getFieldCount() {
        return fieldCount;
    }

    /**
     * Sets the field count.
     *
     * @param fieldCount the field count.
     */
    public void setFieldCount(int fieldCount) {
        this.fieldCount = fieldCount;
    }

    /**
     * Gets the original table name.
     *
     * @return the original table name.
     */
    public String getOriginalTableName() {
        return originalTableName;
    }

    /**
     * Sets the original table name.
     *
     * @param originalTableName the original table name.
     */
    public void setOriginalTableName(String originalTableName) {
        this.originalTableName = originalTableName;
    }

    /**
     * Gets the validation field list.
     *
     * @return the validation field list.
     */
    public ValidationField[] getFields() {
        return fields;
    }

    /**
     * Sets the validation field list.
     *
     * @param fields the validation field list.
     */
    public void setFields(ValidationField[] fields) {
        this.fields = fields;
    }

    /**
     * Gets the footer offset.
     *
     * @return the footer offset.
     */
    public int getFooterOffset() {
        return footerOffset;
    }

    /**
     * Sets the footer offset.
     *
     * @param footerOffset the footer offset.
     */
    public void setFooterOffset(int footerOffset) {
        this.footerOffset = footerOffset;
    }

    /**
     * Gets the table change count.
     *
     * @return the table change count.
     */
    public byte getTableChangeCount() {
        return tableChangeCount;
    }

    /**
     * Sets the table change count.
     *
     * @param tableChangeCount the table change count.
     */
    public void setTableChangeCount(byte tableChangeCount) {
        this.tableChangeCount = tableChangeCount;
    }

    /**
     * Gets the referential integrity offset.
     *
     * @return the referential integrity offset.
     */
    public int getReferentialIntegrityOffset() {
        return referentialIntegrityOffset;
    }

    /**
     * Sets the referential integrity offset.
     *
     * @param referentialIntegrityOffset the referential integrity offset.
     */
    public void setReferentialIntegrityOffset(int referentialIntegrityOffset) {
        this.referentialIntegrityOffset = referentialIntegrityOffset;
    }

    /**
     * Gets the referential integrity list.
     *
     * @return the referential integrity list.
     */
    public ParadoxReferentialIntegrity[] getReferentialIntegrity() {
        return referentialIntegrity;
    }

    /**
     * Sets the referential integrity list.
     *
     * @param referentialIntegrity the referential integrity list.
     */
    public void setReferentialIntegrity(ParadoxReferentialIntegrity[] referentialIntegrity) {
        this.referentialIntegrity = referentialIntegrity;
    }
}
