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

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.metadata.Field;
import com.googlecode.paradox.utils.Utils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Optional;

/**
 * Defines the paradox default file structure.
 *
 * @version 1.6
 * @since 1.0
 */
@SuppressWarnings({"java:S1448", "java:S1820"})
public class ParadoxDataFile {

    /**
     * Java file used to read the database.
     */
    protected final File file;

    /**
     * Fields in this file.
     */
    protected Field[] fields;

    /**
     * Block file size.
     */
    protected byte blockSize;

    /**
     * The file name.
     */
    protected String name;

    /**
     * The field with auto increment value.
     */
    private int autoIncrementValue;

    /**
     * Language sort order.
     */
    private byte sortOrder;

    /**
     * File charset.
     */
    protected Charset charset;

    /**
     * Original code page.
     */
    private int codePage;

    /**
     * File count in this file.
     */
    private int fieldCount;

    /**
     * Table header change count.
     */
    private byte headerChangeCount;

    /**
     * Block change count;
     */
    private byte blockChangeCount;

    /**
     * Fields index order.
     */
    private short[] fieldsOrder;

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
     * If this file has a write protected lock.
     */
    private boolean writeProtected;

    /**
     * Encrypted data.
     */
    private long encryptedData;

    /**
     * Field order ID.
     */
    private String sortOrderID;

    /**
     * Connection information.
     */
    protected final ConnectionInfo connectionInfo;

    /**
     * Creates a new instance.
     *
     * @param file           the database {@link File}.
     * @param connectionInfo the connection information
     */
    public ParadoxDataFile(final File file, final ConnectionInfo connectionInfo) {
        this.file = file;
        this.name = Utils.removeSuffix(file.getName(), "DB");
        this.connectionInfo = connectionInfo;
        this.charset = Optional.ofNullable(connectionInfo).map(ConnectionInfo::getCharset).orElse(null);
    }

    /**
     * Finds a field by name.
     *
     * @param name the field name.
     * @return the {@link Field}.
     */
    public Field findField(final String name) {
        for (final Field field : this.fields) {
            if (field.getName().equalsIgnoreCase(name)) {
                return field;
            }
        }

        return null;
    }

    /**
     * Gets the sorter order id.
     *
     * @return the sorter order id.
     */
    public String getSortOrderID() {
        return this.sortOrderID;
    }

    /**
     * Sets the sort order ID.
     *
     * @param sortOrderID the sort order ID to set.
     */
    public void setSortOrderID(final String sortOrderID) {
        this.sortOrderID = sortOrderID;
    }

    /**
     * Gets the schema name.
     *
     * @return the schema name.
     */
    public String getSchemaName() {
        return file.getParentFile().getName();
    }

    /**
     * Gets the increment value.
     *
     * @return the increment value.
     */
    public int getAutoIncrementValue() {
        return this.autoIncrementValue;
    }

    /**
     * Sets the auto increment value.
     *
     * @param autoIncrementValue the auto increment value.
     */
    public void setAutoIncrementValue(final int autoIncrementValue) {
        this.autoIncrementValue = autoIncrementValue;
    }

    /**
     * Sets the block size.
     *
     * @param blockSize the block size.
     */
    public void setBlockSize(final byte blockSize) {
        this.blockSize = blockSize;
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
     * Sets the charset.
     *
     * @param charset the charset to set.
     */
    public void setCharset(final Charset charset) {
        this.charset = charset;
    }

    /**
     * Gets the field count.
     *
     * @return the field count.
     */
    public int getFieldCount() {
        return this.fieldCount;
    }

    /**
     * Sets the field count.
     *
     * @param fieldCount the field count.
     */
    public void setFieldCount(final int fieldCount) {
        this.fieldCount = fieldCount;
    }

    /**
     * Gets the field list.
     *
     * @return the field list.
     */
    public Field[] getFields() {
        return this.fields;
    }

    /**
     * Sets the field list.
     *
     * @param fields the field list.
     */
    public void setFields(final Field[] fields) {
        this.fields = fields;
    }

    /**
     * Gets the file orders.
     *
     * @return the file orders.
     */
    public short[] getFieldsOrder() {
        return this.fieldsOrder;
    }

    /**
     * Sets the fields order.
     *
     * @param fieldsOrder the fields order to set.
     */
    public void setFieldsOrder(final short[] fieldsOrder) {
        this.fieldsOrder = fieldsOrder;
    }

    /**
     * Gets the java {@link File}.
     *
     * @return the java {@link File}.
     */
    public File getFile() {
        return this.file;
    }

    /**
     * Gets the first block reference.
     *
     * @return the first block reference.
     */
    public int getFirstBlock() {
        return this.firstBlock;
    }

    /**
     * Sets the first block.
     *
     * @param firstBlock the first block.
     */
    public void setFirstBlock(final int firstBlock) {
        this.firstBlock = firstBlock;
    }

    /**
     * Gets the first free block reference.
     *
     * @return the first free block reference.
     */
    public int getFirstFreeBlock() {
        return this.firstFreeBlock;
    }

    /**
     * Sets the first free block.
     *
     * @param firstFreeBlock the first free block.
     */
    public void setFirstFreeBlock(final int firstFreeBlock) {
        this.firstFreeBlock = firstFreeBlock;
    }

    /**
     * Gets the header size.
     *
     * @return the header size.
     */
    public int getHeaderSize() {
        return this.headerSize;
    }

    /**
     * Sets the header size.
     *
     * @param headerSize the header size.
     */
    public void setHeaderSize(final int headerSize) {
        this.headerSize = headerSize;
    }

    /**
     * Gets the last block reference.
     *
     * @return the last block reference.
     */
    public int getLastBlock() {
        return this.lastBlock;
    }

    /**
     * Sets the last block.
     *
     * @param lastBlock the last block.
     */
    public void setLastBlock(final int lastBlock) {
        this.lastBlock = lastBlock;
    }

    /**
     * Gets the data files name.
     *
     * @return the data files name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the file name.
     *
     * @param name the file name.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets the primary key field count.
     *
     * @return the primary key field count.
     */
    public int getPrimaryFieldCount() {
        return this.primaryFieldCount;
    }

    /**
     * Sets the primary key field count.
     *
     * @param primaryFieldCount the primary key field count.
     */
    public void setPrimaryFieldCount(final int primaryFieldCount) {
        this.primaryFieldCount = primaryFieldCount;
    }

    /**
     * Gets the record size.
     *
     * @return the record size.
     */
    public int getRecordSize() {
        return this.recordSize;
    }

    /**
     * Set the record size.
     *
     * @param recordSize the record size.
     */
    public void setRecordSize(final int recordSize) {
        this.recordSize = recordSize;
    }

    /**
     * Gets the referential integrity reference.
     *
     * @return the referential integrity reference.
     */
    public byte getReferentialIntegrity() {
        return this.referentialIntegrity;
    }

    /**
     * Set the referential integrity value.
     *
     * @param referentialIntegrity the referential integrity value.
     */
    public void setReferentialIntegrity(final byte referentialIntegrity) {
        this.referentialIntegrity = referentialIntegrity;
    }

    /**
     * Gets the row count.
     *
     * @return the row count.
     */
    public int getRowCount() {
        return this.rowCount;
    }

    /**
     * Sets the row count.
     *
     * @param rowCount the row count.
     */
    public void setRowCount(final int rowCount) {
        this.rowCount = rowCount;
    }

    /**
     * Gets the file total blocks.
     *
     * @return the file total blocks.
     */
    public int getTotalBlocks() {
        return this.totalBlocks;
    }

    /**
     * Sets the total blocks.
     *
     * @param totalBlocks the total blocks.
     */
    public void setTotalBlocks(final int totalBlocks) {
        this.totalBlocks = totalBlocks;
    }

    /**
     * Gets the file type.
     *
     * @return the file type.
     */
    public byte getType() {
        return this.type;
    }

    /**
     * Sets the file type.
     *
     * @param type the file type.
     */
    public void setType(final byte type) {
        this.type = type;
    }

    /**
     * Gets the used blocks.
     *
     * @return the used blocks.
     */
    public int getUsedBlocks() {
        return this.usedBlocks;
    }

    /**
     * Sets the used blocks.
     *
     * @param usedBlocks the used blocks.
     */
    public void setUsedBlocks(final int usedBlocks) {
        this.usedBlocks = usedBlocks;
    }

    /**
     * Gets the file version ID.
     *
     * @return the file version ID.
     */
    public byte getVersionId() {
        return this.versionId;
    }

    /**
     * Sets the file version ID.
     *
     * @param versionId the file version ID.
     */
    public void setVersionId(final byte versionId) {
        this.versionId = versionId;
    }

    /**
     * Gets the write protected value.
     *
     * @return the write protected value.
     */
    public boolean isWriteProtected() {
        return this.writeProtected;
    }

    /**
     * Sets the write protected value.
     *
     * @param writeProtected the write protected value.
     */
    public void setWriteProtected(final boolean writeProtected) {
        this.writeProtected = writeProtected;
    }

    /**
     * Gets if this file is encrypted.
     *
     * @return <code>true</code> if this file is encrypted.
     */
    public boolean isEncrypted() {
        return encryptedData != 0;
    }

    /**
     * Gets the encryption data.
     *
     * @return the encryption data.
     */
    public long getEncryptedData() {
        return encryptedData;
    }

    /**
     * Sets   the encryption data.
     *
     * @param encryptedData the encryption data.
     */
    public void setEncryptedData(long encryptedData) {
        this.encryptedData = encryptedData;
    }

    /**
     * Gets the connection information.
     *
     * @return the connection information.
     */
    public ConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    /**
     * Gets the sort order.
     *
     * @return the sort order.
     */
    public byte getSortOrder() {
        return sortOrder;
    }

    /**
     * Sets the sort order.
     *
     * @param sortOrder the sort order.
     */
    public void setSortOrder(byte sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * Gets the original code page.
     *
     * @return the original code page.
     */
    public int getCodePage() {
        return codePage;
    }

    /**
     * Sets the original code page.
     *
     * @param codePage the original code page.
     */
    public void setCodePage(int codePage) {
        this.codePage = codePage;
    }

    /**
     * Gets the header change count.
     *
     * @return the header change count.
     */
    public byte getHeaderChangeCount() {
        return headerChangeCount;
    }

    /**
     * Sets the header change count.
     *
     * @param headerChangeCount the header change count.
     */
    public void setHeaderChangeCount(byte headerChangeCount) {
        this.headerChangeCount = headerChangeCount;
    }

    /**
     * Gets the block change count.
     *
     * @return the block change count.
     */
    public byte getBlockChangeCount() {
        return blockChangeCount;
    }

    /**
     * Sets the block change count.
     *
     * @param blockChangeCount the block change count.
     */
    public void setBlockChangeCount(byte blockChangeCount) {
        this.blockChangeCount = blockChangeCount;
    }
}
