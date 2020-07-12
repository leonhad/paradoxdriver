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
package com.googlecode.paradox.data.filefilters;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.utils.Constants;
import com.googlecode.paradox.utils.Expressions;

import java.io.File;
import java.io.FileFilter;

/**
 * Paradox primary key file filter.
 *
 * @version 1.1
 * @since 1.0
 */
public final class PrimaryKeyFilter implements FileFilter {

    /**
     * The primary key name.
     */
    private final String pkName;

    private final ParadoxConnection connection;

    /**
     * Create a new instance.
     *
     * @param connection the Paradox connection.
     */
    public PrimaryKeyFilter(final ParadoxConnection connection) {
        this(connection, null);
    }

    /**
     * Create a new instance.
     *
     * @param connection the Paradox connection.
     * @param pkName     the primary key name.
     */
    public PrimaryKeyFilter(final ParadoxConnection connection, final String pkName) {
        this.connection = connection;
        this.pkName = pkName;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean accept(final File pathname) {
        final String name = pathname.getName();

        return Expressions.accept(connection, name, "%.PX", false, Constants.ESCAPE_CHAR)
                && ((this.pkName == null) || Expressions.accept(connection, name, this.pkName, false,
                Constants.ESCAPE_CHAR));
    }
}
