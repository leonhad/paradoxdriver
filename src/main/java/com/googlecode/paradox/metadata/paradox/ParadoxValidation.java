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

    private byte tableChangeCount;

    /**
     * File count in this file.
     */
    private int fieldCount;

    private int footerOffset;

    private int referentialIntegrityOffset;

    private ValidationField[] fields;

    private ParadoxReferentialIntegrity[] referentialIntegrity;

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

    public String getOriginalTableName() {
        return originalTableName;
    }

    public void setOriginalTableName(String originalTableName) {
        this.originalTableName = originalTableName;
    }

    public ValidationField[] getFields() {
        return fields;
    }

    public void setFields(ValidationField[] fields) {
        this.fields = fields;
    }

    public int getFooterOffset() {
        return footerOffset;
    }

    public void setFooterOffset(int footerOffset) {
        this.footerOffset = footerOffset;
    }

    public byte getTableChangeCount() {
        return tableChangeCount;
    }

    public void setTableChangeCount(byte tableChangeCount) {
        this.tableChangeCount = tableChangeCount;
    }

    public int getReferentialIntegrityOffset() {
        return referentialIntegrityOffset;
    }

    public void setReferentialIntegrityOffset(int referentialIntegrityOffset) {
        this.referentialIntegrityOffset = referentialIntegrityOffset;
    }

    public ParadoxReferentialIntegrity[] getReferentialIntegrity() {
        return referentialIntegrity;
    }

    public void setReferentialIntegrity(ParadoxReferentialIntegrity[] referentialIntegrity) {
        this.referentialIntegrity = referentialIntegrity;
    }
}
