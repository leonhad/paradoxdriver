/*
 * ParadoxDataFile.java
 *
 * 03/12/2009
 * Copyright (C) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.metadata;

import java.io.File;
import java.util.List;

import com.googlecode.paradox.utils.StringUtils;

/**
 * Defines the paradox default file structure.
 *
 * @author Leonardo Alves da Costa
 * @since 1.0
 * @version 1.2
 */
public abstract class ParadoxDataFile {

    /**
     * The field with auto increment value.
     */
    protected int autoIncrementValue;

    /**
     * Block file size.
     */
    protected byte blockSize;

    /**
     * File count in this file.
     */
    protected int fieldCount;

    /**
     * Fields in this file.
     */
    protected List<ParadoxField> fields;

    /**
     * Java file used to read the database.
     */
    private final File file;

    /**
     * The first block in the file.
     */
    protected int firstBlock;

    /**
     * The first free block in the file.
     */
    protected int firstFreeBlock;

    /**
     * Size of file header.
     */
    protected int headerSize;

    /**
     * The last block in the file.
     */
    protected int lastBlock;

    /**
     * The file name.
     */
    private String name;

    /**
     * Primary key field count.
     */
    protected int primaryFieldCount;

    /**
     * Size of one record.
     */
    protected int recordSize;

    /**
     * Referential integrity used in index files.
     */
    protected byte referentialIntegrity;

    /**
     * Total rows in this file.
     */
    protected int rowCount;

    /**
     * Total blocks in this file.
     */
    protected int totalBlocks;

    /**
     * The file type.
     */
    protected byte type = -1;

    /**
     * Used blocks in this file.
     */
    protected int usedBlocks;

    /**
     * Version ID of this file.
     */
    protected byte versionId;

    /**
     * If this file has an write protected lock.
     */
    protected byte writeProtected;

    /**
     * Creates a new instance.
     *
     * @param file
     *            the database {@link File}.
     * @param name
     *            the file name.
     */
    public ParadoxDataFile(final File file, final String name) {
        this.file = file;
        this.name = StringUtils.removeDb(name);
    }

    /**
     * Finds a field by name.
     *
     * @param name
     *            the field name.
     * @return the {@link ParadoxField}.
     */
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

    /**
     * Gets the increment value.
     *
     * @return the increment value.
     */
    public int getAutoIncrementValue() {
        return autoIncrementValue;
    }

    /**
     * Gets the block size.
     *
     * @return the block size.
     */
    public byte getBlockSize() {
        return blockSize;
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
     * Gets the field list.
     *
     * @return the field list.
     */
    public List<ParadoxField> getFields() {
        return fields;
    }

    /**
     * Gets the java {@link File}.
     *
     * @return the java {@link File}.
     */
    public File getFile() {
        return file;
    }

    /**
     * Gets the first block reference.
     *
     * @return the first block reference.
     */
    public int getFirstBlock() {
        return firstBlock;
    }

    /**
     * Gets the first free block reference.
     *
     * @return the first free block reference.
     */
    public int getFirstFreeBlock() {
        return firstFreeBlock;
    }

    /**
     * Gets the header size.
     *
     * @return the header size.
     */
    public int getHeaderSize() {
        return headerSize;
    }

    /**
     * Gets the last block reference.
     *
     * @return the last block reference.
     */
    public int getLastBlock() {
        return lastBlock;
    }

    /**
     * Gets the data files name.
     *
     * @return the data files name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the primary key field count.
     *
     * @return the primary key field count.
     */
    public int getPrimaryFieldCount() {
        return primaryFieldCount;
    }

    /**
     * Gets the record size.
     *
     * @return the record size.
     */
    public int getRecordSize() {
        return recordSize;
    }

    /**
     * Gets the referential integrity reference.
     *
     * @return the referential integrity reference.
     */
    public byte getReferentialIntegrity() {
        return referentialIntegrity;
    }

    /**
     * Gets the row count.
     *
     * @return the row count.
     */
    public int getRowCount() {
        return rowCount;
    }

    /**
     * Gets the file total blocks.
     *
     * @return the file total blocks.
     */
    public int getTotalBlocks() {
        return totalBlocks;
    }

    /**
     * Gets the file type.
     *
     * @return the file type.
     */
    public byte getType() {
        return type;
    }

    /**
     * Gets the used blocks.
     *
     * @return the used blocks.
     */
    public int getUsedBlocks() {
        return usedBlocks;
    }

    /**
     * Gets the file version ID.
     *
     * @return the file version ID.
     */
    public byte getVersionId() {
        return versionId;
    }

    /**
     * Gets the write protected value.
     *
     * @return the write protected value.
     */
    public byte getWriteProtected() {
        return writeProtected;
    }

    /**
     * Get the integrity check.
     *
     * @return if this file is valid.
     */
    public abstract boolean isValid();

    /**
     * Sets the auto increment value.
     *
     * @param autoIncrementValue
     *            the auto increment value.
     */
    public void setAutoIncrementValue(final int autoIncrementValue) {
        this.autoIncrementValue = autoIncrementValue;
    }

    /**
     * Sets the block size.
     *
     * @param blockSize
     *            the block size.
     */
    public void setBlockSize(final byte blockSize) {
        this.blockSize = blockSize;
    }

    /**
     * Sets the field count.
     *
     * @param fieldCount
     *            the field count.
     */
    public void setFieldCount(final int fieldCount) {
        this.fieldCount = fieldCount;
    }

    /**
     * Sets the field list.
     *
     * @param fields
     *            the field list.
     */
    public void setFields(final List<ParadoxField> fields) {
        this.fields = fields;
    }

    /**
     * Sets the first block.
     *
     * @param firstBlock
     *            the first block.
     */
    public void setFirstBlock(final int firstBlock) {
        this.firstBlock = firstBlock;
    }

    /**
     * Sets the first free block.
     *
     * @param firstFreeBlock
     *            the first free block.
     */
    public void setFirstFreeBlock(final int firstFreeBlock) {
        this.firstFreeBlock = firstFreeBlock;
    }

    /**
     * Sets the header size.
     *
     * @param headerSize
     *            the header size.
     */
    public void setHeaderSize(final int headerSize) {
        this.headerSize = headerSize;
    }

    /**
     * Sets the last block.
     *
     * @param lastBlock
     *            the last block.
     */
    public void setLastBlock(final int lastBlock) {
        this.lastBlock = lastBlock;
    }

    /**
     * Sets the file name.
     *
     * @param name
     *            the file name.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Sets the primary key field count.
     *
     * @param primaryFieldCount
     *            the primary key field count.
     */
    public void setPrimaryFieldCount(final int primaryFieldCount) {
        this.primaryFieldCount = primaryFieldCount;
    }

    /**
     * Set the record size.
     *
     * @param recordSize
     *            the record size.
     */
    public void setRecordSize(final int recordSize) {
        this.recordSize = recordSize;
    }

    /**
     * Set the referential integrity value.
     *
     * @param referentialIntegrity
     *            the referential integrity value.
     */
    public void setReferentialIntegrity(final byte referentialIntegrity) {
        this.referentialIntegrity = referentialIntegrity;
    }

    /**
     * Sets the row count.
     *
     * @param rowCount
     *            the row count.
     */
    public void setRowCount(final int rowCount) {
        this.rowCount = rowCount;
    }

    /**
     * Sets the total blocks.
     *
     * @param totalBlocks
     *            the total blocks.
     */
    public void setTotalBlocks(final int totalBlocks) {
        this.totalBlocks = totalBlocks;
    }

    /**
     * Sets the file type.
     *
     * @param type
     *            the file type.
     */
    public void setType(final byte type) {
        this.type = type;
    }

    /**
     * Sets the used blocks.
     *
     * @param usedBlocks
     *            the used blocks.
     */
    public void setUsedBlocks(final int usedBlocks) {
        this.usedBlocks = usedBlocks;
    }

    /**
     * Sets the file version ID.
     *
     * @param versionId
     *            the file version ID.
     */
    public void setVersionId(final byte versionId) {
        this.versionId = versionId;
    }

    /**
     * Sets the write protected value.
     *
     * @param writeProtected
     *            the write protected value.
     */
    public void setWriteProtected(final byte writeProtected) {
        this.writeProtected = writeProtected;
    }
}
