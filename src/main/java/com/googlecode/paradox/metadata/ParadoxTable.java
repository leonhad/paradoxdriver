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
import com.googlecode.paradox.exceptions.ParadoxDataException;
import com.googlecode.paradox.utils.filefilters.TableFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;

/**
 * Stores a table data file.
 *
 * @version 1.6
 * @since 1.0
 */
public final class ParadoxTable extends ParadoxDataFile {

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

    public FileInputStream openBlobs() throws SQLException {
        final File[] fileList = file.getParentFile().listFiles(new TableFilter(connection,
                name, "mb"));
        if ((fileList == null) || (fileList.length == 0)) {
            throw new ParadoxDataException(ParadoxDataException.Error.BLOB_FILE_NOT_FOUND);
        }
        if (fileList.length > 1) {
            throw new ParadoxDataException(ParadoxDataException.Error.TOO_MANY_BLOB_FILES);
        }
        File blobFile = fileList[0];
        try {
            return new FileInputStream(blobFile);
        } catch (final FileNotFoundException e) {
            throw new ParadoxDataException(ParadoxDataException.Error.ERROR_OPENING_BLOB_FILE);
        }
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
    public ParadoxField[] getPrimaryKeys() {
        final ParadoxField[] ret = new ParadoxField[this.getPrimaryFieldCount()];
        if (this.getPrimaryFieldCount() >= 0) {
            System.arraycopy(this.getFields(), 0, ret, 0, this.getPrimaryFieldCount());
        }

        return ret;
    }
}
