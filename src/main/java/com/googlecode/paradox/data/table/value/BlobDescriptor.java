/*
 * BlobDescriptor.java
 *
 * 12/22/2014
 * Copyright (C) 2014 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.data.table.value;

import com.googlecode.paradox.metadata.BlobTable;

/**
 * Describe the blob information from database file.
 *
 * @author Leonardo Alves da Costa
 * @author Andre Mikhaylov
 * @since 1.2
 * @version 1.1
 */
public class BlobDescriptor {
    
    /**
     * The blob table.
     */
    private BlobTable file = null;

    /**
     * The blob length.
     */
    private long length = 0;

    /**
     * Blob modifier.
     */
    private short modifier = 0;
    
    /**
     * Blob file offset.
     */
    private long offset = 0;

    /**
     * Creates a new instance.
     *
     * @param file
     *            the blob file reference.
     */
    public BlobDescriptor(final BlobTable file) {
        setFile(file);
    }

    /**
     * Gets the blob file.
     *
     * @return the blob file.
     */
    public BlobTable getFile() {
        return file;
    }

    /**
     * Gets the blob length.
     *
     * @return the blob length.
     */
    public long getLength() {
        return length;
    }

    /**
     * Gets the blob modifier.
     *
     * @return the blob modifier.
     */
    public short getModifier() {
        return modifier;
    }

    /**
     * Gets the blob offset.
     *
     * @return the blob offset.
     */
    public long getOffset() {
        return offset;
    }

    /**
     * Sets the blob table file.
     *
     * @param file
     *            the blob table file.
     */
    public void setFile(final BlobTable file) {
        this.file = file;
    }

    /**
     * Sets the blob length.
     *
     * @param length
     *            the blob length.
     */
    public void setLength(final long length) {
        this.length = length;
    }

    /**
     * Sets the blob modifier.
     * 
     * @param modifier
     *            the blob modifier.
     */
    public void setModifier(final short modifier) {
        this.modifier = modifier;
    }

    /**
     * Sets the blob offset.
     *
     * @param offset
     *            the blob offset.
     */
    public void setOffset(final long offset) {
        this.offset = offset;
    }
}
