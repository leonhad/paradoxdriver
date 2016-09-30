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

import com.googlecode.paradox.utils.Utils;

import java.io.File;
import java.util.List;

/**
 * Defines the paradox default file structure.
 *
 * @author Leonardo Alves da Costa
 * @version 1.2
 * @since 1.0
 */
public class ParadoxDataFile {

    /**
     * Java file used to read the database.
     */
    private final File file;
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
     * The file name.
     */
    private String name;

    /**
     * Create a new instance.
     */
    private ParadoxDataFile() {
        // Not used.
    }

    /**
     * Creates a new instance.
     *
     * @param file
     *         the database {@link File}.
     * @param name
     *         the file name.
     */
    public ParadoxDataFile(final File file, final String name) {
        this.file = file;
        this.name = Utils.removeDb(name);
    }

    /**
     * Finds a field by name.
     *
     * @param name
     *         the field name.
     * @return the {@link ParadoxField}.
     */
    public final ParadoxField findField(final String name) {
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
    public final int getAutoIncrementValue() {
        return autoIncrementValue;
    }

    /**
     * Sets the auto increment value.
     *
     * @param autoIncrementValue
     *         the auto increment value.
     */
    public final void setAutoIncrementValue(final int autoIncrementValue) {
        this.autoIncrementValue = autoIncrementValue;
    }

    /**
     * Gets the block size.
     *
     * @return the block size.
     */
    public final byte getBlockSize() {
        return blockSize;
    }

    /**
     * Sets the block size.
     *
     * @param blockSize
     *         the block size.
     */
    public final void setBlockSize(final byte blockSize) {
        this.blockSize = blockSize;
    }

    /**
     * Gets the field count.
     *
     * @return the field count.
     */
    public final int getFieldCount() {
        return fieldCount;
    }

    /**
     * Sets the field count.
     *
     * @param fieldCount
     *         the field count.
     */
    public final void setFieldCount(final int fieldCount) {
        this.fieldCount = fieldCount;
    }

    /**
     * Gets the field list.
     *
     * @return the field list.
     */
    public final List<ParadoxField> getFields() {
        return fields;
    }

    /**
     * Sets the field list.
     *
     * @param fields
     *         the field list.
     */
    public final void setFields(final List<ParadoxField> fields) {
        this.fields = fields;
    }

    /**
     * Gets the java {@link File}.
     *
     * @return the java {@link File}.
     */
    public final File getFile() {
        return file;
    }

    /**
     * Gets the first block reference.
     *
     * @return the first block reference.
     */
    public final int getFirstBlock() {
        return firstBlock;
    }

    /**
     * Sets the first block.
     *
     * @param firstBlock
     *         the first block.
     */
    public final void setFirstBlock(final int firstBlock) {
        this.firstBlock = firstBlock;
    }

    /**
     * Gets the first free block reference.
     *
     * @return the first free block reference.
     */
    public final int getFirstFreeBlock() {
        return firstFreeBlock;
    }

    /**
     * Sets the first free block.
     *
     * @param firstFreeBlock
     *         the first free block.
     */
    public final void setFirstFreeBlock(final int firstFreeBlock) {
        this.firstFreeBlock = firstFreeBlock;
    }

    /**
     * Gets the header size.
     *
     * @return the header size.
     */
    public final int getHeaderSize() {
        return headerSize;
    }

    /**
     * Sets the header size.
     *
     * @param headerSize
     *         the header size.
     */
    public final void setHeaderSize(final int headerSize) {
        this.headerSize = headerSize;
    }

    /**
     * Gets the last block reference.
     *
     * @return the last block reference.
     */
    public final int getLastBlock() {
        return lastBlock;
    }

    /**
     * Sets the last block.
     *
     * @param lastBlock
     *         the last block.
     */
    public final void setLastBlock(final int lastBlock) {
        this.lastBlock = lastBlock;
    }

    /**
     * Gets the data files name.
     *
     * @return the data files name.
     */
    public final String getName() {
        return name;
    }

    /**
     * Sets the file name.
     *
     * @param name
     *         the file name.
     */
    public final void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets the primary key field count.
     *
     * @return the primary key field count.
     */
    public final int getPrimaryFieldCount() {
        return primaryFieldCount;
    }

    /**
     * Sets the primary key field count.
     *
     * @param primaryFieldCount
     *         the primary key field count.
     */
    public final void setPrimaryFieldCount(final int primaryFieldCount) {
        this.primaryFieldCount = primaryFieldCount;
    }

    /**
     * Gets the record size.
     *
     * @return the record size.
     */
    public final int getRecordSize() {
        return recordSize;
    }

    /**
     * Set the record size.
     *
     * @param recordSize
     *         the record size.
     */
    public final void setRecordSize(final int recordSize) {
        this.recordSize = recordSize;
    }

    /**
     * Gets the referential integrity reference.
     *
     * @return the referential integrity reference.
     */
    public final byte getReferentialIntegrity() {
        return referentialIntegrity;
    }

    /**
     * Set the referential integrity value.
     *
     * @param referentialIntegrity
     *         the referential integrity value.
     */
    public final void setReferentialIntegrity(final byte referentialIntegrity) {
        this.referentialIntegrity = referentialIntegrity;
    }

    /**
     * Gets the row count.
     *
     * @return the row count.
     */
    public final int getRowCount() {
        return rowCount;
    }

    /**
     * Sets the row count.
     *
     * @param rowCount
     *         the row count.
     */
    public final void setRowCount(final int rowCount) {
        this.rowCount = rowCount;
    }

    /**
     * Gets the file total blocks.
     *
     * @return the file total blocks.
     */
    public final int getTotalBlocks() {
        return totalBlocks;
    }

    /**
     * Sets the total blocks.
     *
     * @param totalBlocks
     *         the total blocks.
     */
    public final void setTotalBlocks(final int totalBlocks) {
        this.totalBlocks = totalBlocks;
    }

    /**
     * Gets the file type.
     *
     * @return the file type.
     */
    public final byte getType() {
        return type;
    }

    /**
     * Sets the file type.
     *
     * @param type
     *         the file type.
     */
    public final void setType(final byte type) {
        this.type = type;
    }

    /**
     * Gets the used blocks.
     *
     * @return the used blocks.
     */
    public final int getUsedBlocks() {
        return usedBlocks;
    }

    /**
     * Sets the used blocks.
     *
     * @param usedBlocks
     *         the used blocks.
     */
    public final void setUsedBlocks(final int usedBlocks) {
        this.usedBlocks = usedBlocks;
    }

    /**
     * Gets the file version ID.
     *
     * @return the file version ID.
     */
    public final byte getVersionId() {
        return versionId;
    }

    /**
     * Sets the file version ID.
     *
     * @param versionId
     *         the file version ID.
     */
    public final void setVersionId(final byte versionId) {
        this.versionId = versionId;
    }

    /**
     * Gets the write protected value.
     *
     * @return the write protected value.
     */
    public final byte getWriteProtected() {
        return writeProtected;
    }

    /**
     * Sets the write protected value.
     *
     * @param writeProtected
     *         the write protected value.
     */
    public final void setWriteProtected(final byte writeProtected) {
        this.writeProtected = writeProtected;
    }
}
