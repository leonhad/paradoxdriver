/*
 * ParadoxTable.java
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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.Charset.forName;

/**
 * Stores a table data file.
 *
 * @author Leonardo Alves da Costa
 * @version 1.2
 * @since 1.0
 */
public class ParadoxTable extends ParadoxDataFile {

    /**
     * The blob instance used to read the file.
     */
    private BlobTable blobFile = null;

    /**
     * Table charset.
     */
    private Charset charset = forName("Cp437");

    /**
     * Fields order in file.
     */
    private List<Short> fieldsOrder;

    /**
     * Creates a new instance.
     *
     * @param file
     *         table references file.
     * @param name
     *         table name.
     */
    public ParadoxTable(final File file, final String name) {
        super(file, name);
    }

    /**
     * Gets the blob table.
     *
     * @return the blob table.
     */
    public BlobTable getBlobTable() {
        if (blobFile == null) {
            blobFile = new BlobTable(getFile(), getName());
        }
        return blobFile;
    }

    /**
     * Return the block size in bytes.
     *
     * @return the block size in bytes.
     */
    public int getBlockSizeBytes() {
        // The blockSize is always in KiB
        return getBlockSize() * 1024;
    }

    /**
     * @return the charset.
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * Sets the charset.
     *
     * @param charset
     *         the charset to set.
     */
    public void setCharset(final Charset charset) {
        this.charset = charset;
    }

    /**
     * @return the fieldsOrder.
     */
    public List<Short> getFieldsOrder() {
        return fieldsOrder;
    }

    /**
     * Sets the file order.
     *
     * @param fieldsOrder
     *         the fieldsOrder to set.
     */
    public void setFieldsOrder(final List<Short> fieldsOrder) {
        this.fieldsOrder = fieldsOrder;
    }

    /**
     * Gets the primary keys list.
     *
     * @return the primary keys list.
     */
    public List<ParadoxField> getPrimaryKeys() {
        final ArrayList<ParadoxField> ret = new ArrayList<>();
        for (int loop = 0; loop < getPrimaryFieldCount(); loop++) {
            ret.add(getFields().get(loop));
        }
        return ret;
    }

    /**
     * Gets the table name.
     *
     * @return the table name.
     */
    @Override
    public String toString() {
        return getName();
    }
}
