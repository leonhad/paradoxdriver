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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.data.table.value;

import com.googlecode.paradox.metadata.BlobTable;

/**
 * Describe blob information from database file.
 *
 * @author Andre Mikhaylov
 * @since 1.2
 * @version 1.0
 */
public class BlobDescriptor {

    private BlobTable file = null;
    private long length = 0;
    private short modificator = 0;
    private long offset = 0;

    public BlobDescriptor(final BlobTable file) {
        setFile(file);
    }

    public BlobTable getFile() {
        return file;
    }

    public long getLength() {
        return length;
    }

    public short getModificator() {
        return modificator;
    }

    public long getOffset() {
        return offset;
    }

    public void setFile(final BlobTable file) {
        this.file = file;
    }

    public void setLength(final long length) {
        this.length = length;
    }

    public void setModificator(final short modificator) {
        this.modificator = modificator;
    }

    public void setOffset(final long offset) {
        this.offset = offset;
    }

}
