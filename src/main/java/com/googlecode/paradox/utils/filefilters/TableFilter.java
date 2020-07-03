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
package com.googlecode.paradox.utils.filefilters;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.utils.Expressions;

import java.io.File;
import java.io.FileFilter;

/**
 * Paradox Table filters.
 *
 * @version 1.0
 * @since 1.0
 */
public final class TableFilter implements FileFilter {

    /**
     * The file extension.
     */
    private final String extension;

    /**
     * The table name.
     */
    private final String tableName;

    private final ParadoxConnection connection;

    /**
     * Create a new instance.
     *
     * @param connection the Paradox connection.
     */
    public TableFilter(final ParadoxConnection connection) {
        this(connection, null, "db");
    }

    /**
     * Create a new instance.
     *
     * @param connection the Paradox connection.
     * @param tableName  the table name.
     */
    public TableFilter(final ParadoxConnection connection, final String tableName) {
        this(connection, tableName, "db");
    }

    /**
     * Create a new instance.
     *
     * @param connection the Paradox connection.
     * @param tableName  the table name.
     * @param extension  the table extension.
     */
    public TableFilter(final ParadoxConnection connection, final String tableName, final String extension) {
        this.connection = connection;
        this.tableName = tableName;
        this.extension = extension;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean accept(final File pathname) {
        final String name = pathname.getName();

        if (this.tableName != null) {
            return Expressions.accept(connection, name,
                    this.tableName + "." + this.extension, false) && pathname.isFile();
        }
        return Expressions.accept(connection, name, "%." + this.extension, false) && pathname.isFile();
    }

}
