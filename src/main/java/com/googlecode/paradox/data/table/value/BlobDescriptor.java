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
 * @version 1.1
 * @since 1.2
 */
class BlobDescriptor {

    /**
     * The blob table.
     */
    private BlobTable file;

    /**
     * Blob file offset.
     */
    private long offset;

    /**
     * Creates a new instance.
     *
     * @param file
     *         the blob file reference.
     */
    BlobDescriptor(final BlobTable file) {
        setFile(file);
    }

    /**
     * Gets the blob file.
     *
     * @return the blob file.
     */
    public final BlobTable getFile() {
        return file;
    }

    /**
     * Sets the blob table file.
     *
     * @param file
     *         the blob table file.
     */
    public final void setFile(final BlobTable file) {
        this.file = file;
    }

    /**
     * Gets the blob offset.
     *
     * @return the blob offset.
     */
    public final long getOffset() {
        return offset;
    }

    /**
     * Sets the blob offset.
     *
     * @param offset
     *         the blob offset.
     */
    public final void setOffset(final long offset) {
        this.offset = offset;
    }
}
