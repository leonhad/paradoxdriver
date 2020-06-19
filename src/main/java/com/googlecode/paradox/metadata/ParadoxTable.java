/*
 * Copyright (C) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.metadata;

import com.googlecode.paradox.ParadoxConnection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores a table data file.
 *
 * @author Leonardo Alves da Costa
 * @version 1.2
 * @since 1.0
 */
public final class ParadoxTable extends ParadoxDataFile {

    /**
     * The blob instance used to read the file.
     */
    private BlobTable blobFile;

    /**
     * Creates a new instance.
     *
     * @param file       table references file.
     * @param name       table name.
     * @param connection the database connection.
     */
    public ParadoxTable(final File file, final String name, final ParadoxConnection connection) {
        super(file, name, connection);
    }

    /**
     * Gets the blob table.
     *
     * @return the blob table.
     */
    public BlobTable getBlobTable() {
        if (this.blobFile == null) {
            this.blobFile = new BlobTable(this.getFile(), this.getName(), getConnection());
        }
        return this.blobFile;
    }

    /**
     * Return the block size in bytes.
     *
     * @return the block size in bytes.
     */
    public int getBlockSizeBytes() {
        // The blockSize is always in KiB.
        return this.blockSize * 1_024;
    }

    /**
     * Gets the table name.
     *
     * @return the table name.
     */
    @Override
    public String toString() {
        return this.getName();
    }

    /**
     * Gets the primary keys list.
     *
     * @return the primary keys list.
     */
    List<ParadoxField> getPrimaryKeys() {
        final ArrayList<ParadoxField> ret = new ArrayList<>();
        for (int loop = 0; loop < this.getPrimaryFieldCount(); loop++) {
            ret.add(this.getFields().get(loop));
        }
        return ret;
    }
}
