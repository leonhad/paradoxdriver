/*
 * ParadoxDataFile.java 03/12/2009 Copyright (C) 2009 Leonardo Alves da Costa This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.metadata;

import com.googlecode.paradox.utils.Utils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
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
     * The field with auto increment value.
     */
    private int autoIncrementValue;
    /**
     * Block file size.
     */
    private byte blockSize;
    /**
     * File charset.
     */
    private Charset charset = Charset.forName("Cp437");
    /**
     * File count in this file.
     */
    private int fieldCount;
    /**
     * Fields index order.
     */
    private List<Short> fieldsOrder;
    /**
     * Java file used to read the database.
     */
    private final File file;
    /**
     * The first block in the file.
     */
    private int firstBlock;
    /**
     * The first free block in the file.
     */
    private int firstFreeBlock;
    /**
     * Size of file header.
     */
    private int headerSize;
    /**
     * The last block in the file.
     */
    private int lastBlock;
    /**
     * The file name.
     */
    private String name;
    /**
     * Primary key field count.
     */
    private int primaryFieldCount;
    /**
     * Size of one record.
     */
    private int recordSize;
    /**
     * Referential integrity used in index files.
     */
    private byte referentialIntegrity;
    /**
     * Total rows in this file.
     */
    private int rowCount;
    /**
     * Total blocks in this file.
     */
    private int totalBlocks;
    /**
     * The file type.
     */
    private byte type = -1;
    /**
     * Used blocks in this file.
     */
    private int usedBlocks;
    /**
     * Version ID of this file.
     */
    private byte versionId;

    /**
     * If this file has an write protected lock.
     */
    private byte writeProtected;

    /**
     * Fields in this file.
     */
    protected List<ParadoxField> fields = new ArrayList<>();

    /**
     * Creates a new instance.
     *
     * @param file the database {@link File}.
     * @param name the file name.
     */
    protected ParadoxDataFile(final File file, final String name) {
        this.file = file;
        this.name = Utils.removeDb(name);
    }

    /**
     * Finds a field by name.
     *
     * @param name the field name.
     * @return the {@link ParadoxField}.
     */
    public final ParadoxField findField(final String name) {
        for (final ParadoxField field : this.fields) {
            if (field.getName().equalsIgnoreCase(name)) {
                return field;
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
        return this.autoIncrementValue;
    }

    /**
     * Gets the block size.
     *
     * @return the block size.
     */
    public final byte getBlockSize() {
        return this.blockSize;
    }

    /**
     * Gets the charset.
     *
     * @return the charset.
     */
    public Charset getCharset() {
        return this.charset;
    }

    /**
     * Gets the field count.
     *
     * @return the field count.
     */
    public final int getFieldCount() {
        return this.fieldCount;
    }

    /**
     * Gets the field list.
     *
     * @return the field list.
     */
    public final List<ParadoxField> getFields() {
        return this.fields;
    }

    /**
     * Gets the file orders.
     *
     * @return the file orders.
     */
    public List<Short> getFieldsOrder() {
        return Collections.unmodifiableList(this.fieldsOrder);
    }

    /**
     * Gets the java {@link File}.
     *
     * @return the java {@link File}.
     */
    public final File getFile() {
        return this.file;
    }

    /**
     * Gets the first block reference.
     *
     * @return the first block reference.
     */
    public final int getFirstBlock() {
        return this.firstBlock;
    }

    /**
     * Gets the first free block reference.
     *
     * @return the first free block reference.
     */
    public final int getFirstFreeBlock() {
        return this.firstFreeBlock;
    }

    /**
     * Gets the header size.
     *
     * @return the header size.
     */
    public final int getHeaderSize() {
        return this.headerSize;
    }

    /**
     * Gets the last block reference.
     *
     * @return the last block reference.
     */
    public final int getLastBlock() {
        return this.lastBlock;
    }

    /**
     * Gets the data files name.
     *
     * @return the data files name.
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Gets the primary key field count.
     *
     * @return the primary key field count.
     */
    public final int getPrimaryFieldCount() {
        return this.primaryFieldCount;
    }

    /**
     * Gets the record size.
     *
     * @return the record size.
     */
    public final int getRecordSize() {
        return this.recordSize;
    }

    /**
     * Gets the referential integrity reference.
     *
     * @return the referential integrity reference.
     */
    public final byte getReferentialIntegrity() {
        return this.referentialIntegrity;
    }

    /**
     * Gets the row count.
     *
     * @return the row count.
     */
    public final int getRowCount() {
        return this.rowCount;
    }

    /**
     * Gets the file total blocks.
     *
     * @return the file total blocks.
     */
    public final int getTotalBlocks() {
        return this.totalBlocks;
    }

    /**
     * Gets the file type.
     *
     * @return the file type.
     */
    public final byte getType() {
        return this.type;
    }

    /**
     * Gets the used blocks.
     *
     * @return the used blocks.
     */
    public final int getUsedBlocks() {
        return this.usedBlocks;
    }

    /**
     * Gets the file version ID.
     *
     * @return the file version ID.
     */
    public final byte getVersionId() {
        return this.versionId;
    }

    /**
     * Gets the write protected value.
     *
     * @return the write protected value.
     */
    public final byte getWriteProtected() {
        return this.writeProtected;
    }

    /**
     * Sets the auto increment value.
     *
     * @param autoIncrementValue the auto increment value.
     */
    public final void setAutoIncrementValue(final int autoIncrementValue) {
        this.autoIncrementValue = autoIncrementValue;
    }

    /**
     * Sets the block size.
     *
     * @param blockSize the block size.
     */
    public final void setBlockSize(final byte blockSize) {
        this.blockSize = blockSize;
    }

    /**
     * Sets the charset.
     *
     * @param charset the charset to set.
     */
    public void setCharset(final Charset charset) {
        this.charset = charset;
    }

    /**
     * Sets the field count.
     *
     * @param fieldCount the field count.
     */
    public final void setFieldCount(final int fieldCount) {
        this.fieldCount = fieldCount;
    }

    /**
     * Sets the field list.
     *
     * @param fields the field list.
     */
    public final void setFields(final List<ParadoxField> fields) {
        this.fields = fields;
    }

    /**
     * Sets the fields order.
     *
     * @param fieldsOrder the fields order to set.
     */
    public void setFieldsOrder(final List<Short> fieldsOrder) {
        this.fieldsOrder = Collections.unmodifiableList(fieldsOrder);
    }

    /**
     * Sets the first block.
     *
     * @param firstBlock the first block.
     */
    public final void setFirstBlock(final int firstBlock) {
        this.firstBlock = firstBlock;
    }

    /**
     * Sets the first free block.
     *
     * @param firstFreeBlock the first free block.
     */
    public final void setFirstFreeBlock(final int firstFreeBlock) {
        this.firstFreeBlock = firstFreeBlock;
    }

    /**
     * Sets the header size.
     *
     * @param headerSize the header size.
     */
    public final void setHeaderSize(final int headerSize) {
        this.headerSize = headerSize;
    }

    /**
     * Sets the last block.
     *
     * @param lastBlock the last block.
     */
    public final void setLastBlock(final int lastBlock) {
        this.lastBlock = lastBlock;
    }

    /**
     * Sets the file name.
     *
     * @param name the file name.
     */
    public final void setName(final String name) {
        this.name = name;
    }

    /**
     * Sets the primary key field count.
     *
     * @param primaryFieldCount the primary key field count.
     */
    public final void setPrimaryFieldCount(final int primaryFieldCount) {
        this.primaryFieldCount = primaryFieldCount;
    }

    /**
     * Set the record size.
     *
     * @param recordSize the record size.
     */
    public final void setRecordSize(final int recordSize) {
        this.recordSize = recordSize;
    }

    /**
     * Set the referential integrity value.
     *
     * @param referentialIntegrity the referential integrity value.
     */
    public final void setReferentialIntegrity(final byte referentialIntegrity) {
        this.referentialIntegrity = referentialIntegrity;
    }

    /**
     * Sets the row count.
     *
     * @param rowCount the row count.
     */
    public final void setRowCount(final int rowCount) {
        this.rowCount = rowCount;
    }

    /**
     * Sets the total blocks.
     *
     * @param totalBlocks the total blocks.
     */
    public final void setTotalBlocks(final int totalBlocks) {
        this.totalBlocks = totalBlocks;
    }

    /**
     * Sets the file type.
     *
     * @param type the file type.
     */
    public final void setType(final byte type) {
        this.type = type;
    }

    /**
     * Sets the used blocks.
     *
     * @param usedBlocks the used blocks.
     */
    public final void setUsedBlocks(final int usedBlocks) {
        this.usedBlocks = usedBlocks;
    }

    /**
     * Sets the file version ID.
     *
     * @param versionId the file version ID.
     */
    public final void setVersionId(final byte versionId) {
        this.versionId = versionId;
    }

    /**
     * Sets the write protected value.
     *
     * @param writeProtected the write protected value.
     */
    public final void setWriteProtected(final byte writeProtected) {
        this.writeProtected = writeProtected;
    }
}
