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
package com.googlecode.paradox.metadata.paradox;

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.data.IndexData;
import com.googlecode.paradox.data.PrimaryKeyData;
import com.googlecode.paradox.data.TableData;
import com.googlecode.paradox.data.filefilters.TableFilter;
import com.googlecode.paradox.exceptions.ParadoxDataException;
import com.googlecode.paradox.metadata.Field;
import com.googlecode.paradox.metadata.Index;
import com.googlecode.paradox.metadata.Table;
import com.googlecode.paradox.metadata.TableType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;

/**
 * Stores a table data file.
 *
 * @version 1.9
 * @since 1.0
 */
public final class ParadoxTable extends ParadoxDataFile implements Table {

    /**
     * Table primary key.
     */
    private ParadoxPK primaryKeyIndex;

    /**
     * Creates a new instance.
     *
     * @param file           table references file.
     * @param name           table name.
     * @param connectionInfo the connection information.
     */
    public ParadoxTable(final File file, final String name, final ConnectionInfo connectionInfo) {
        super(file, name, connectionInfo);
    }

    /**
     * Gets the associated blob file (MB).
     *
     * @return the associated blob file (MB).
     * @throws SQLException in case of failures.
     */
    public FileInputStream openBlobs() throws SQLException {
        final File[] fileList = file.getParentFile().listFiles(new TableFilter(connectionInfo.getLocale(), name, "mb"));
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
            throw new ParadoxDataException(ParadoxDataException.Error.ERROR_OPENING_BLOB_FILE, e);
        }
    }

    @Override
    public List<Object[]> load(final Field[] fields) throws SQLException {
        return TableData.loadData(this, fields);
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

    @Override
    public Index getPrimaryKeyIndex() throws SQLException {
        if (primaryKeyIndex == null) {
            this.primaryKeyIndex = PrimaryKeyData.getPrimaryKey(file.getParentFile(), this, connectionInfo);
        }

        return primaryKeyIndex;
    }

    public Field[] getPrimaryKeys() {
        final Field[] ret = new Field[this.getPrimaryFieldCount()];
        if (this.getPrimaryFieldCount() > 0) {
            System.arraycopy(this.getFields(), 0, ret, 0, this.getPrimaryFieldCount());
        }

        return ret;
    }

    @Override
    public Index[] getIndexes() throws SQLException {
        final List<Index> indexes = IndexData.listIndexes(file.getParentFile(), name, this.connectionInfo);
        final Index index = getPrimaryKeyIndex();
        if (index != null) {
            indexes.add(index);
        }

        return indexes.toArray(new Index[0]);
    }

    @Override
    public boolean isWriteProtected() {
        return true;
    }

    @Override
    public TableType type() {
        return TableType.TABLE;
    }
}
