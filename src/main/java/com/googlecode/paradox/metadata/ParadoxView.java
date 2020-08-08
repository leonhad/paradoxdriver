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

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.exceptions.ParadoxNotSupportedException;
import com.googlecode.paradox.metadata.paradox.ParadoxDataFile;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

/**
 * Stores the paradox view data.
 *
 * @version 1.3
 * @since 1.0
 */
public final class ParadoxView extends ParadoxDataFile implements Table {

    /**
     * Stores the field list sort.
     */
    private Field[] fieldsSort;

    /**
     * Creates a new instance.
     *
     * @param file           the file to read of.
     * @param name           the view name.
     * @param connectionInfo the connection information.
     */
    public ParadoxView(final File file, final String name, final ConnectionInfo connectionInfo) {
        super(file, name, connectionInfo);
    }

    /**
     * Gets the fields sort.
     *
     * @return the fieldsSort the fields sort.
     */
    public Field[] getFieldsSort() {
        return this.fieldsSort;
    }

    /**
     * Sets the fields sort.
     *
     * @param fieldsSort the fields sort to set.
     */
    public void setFieldsSort(final Field[] fieldsSort) {
        this.fieldsSort = fieldsSort;
    }

    @Override
    public boolean isWriteProtected() {
        return false;
    }

    @Override
    public List<Object[]> load(Field[] fields) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public Field[] getPrimaryKeys() {
        return new Field[0];
    }

    /**
     * No indexes for views.
     *
     * @return a empty array.
     */
    @Override
    public Index[] getIndexes() {
        return new Index[0];
    }

    @Override
    public TableType type() {
        return TableType.VIEW;
    }
}
