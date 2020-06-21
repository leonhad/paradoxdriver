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
 * If the file is a directory.
 *
 * @author Leonardo Costa
 * @version 1.0
 * @since 1.4
 */
public class DirectoryFilter implements FileFilter {

    private final String pattern;

    private final ParadoxConnection connection;

    /**
     * Creates a new instance.
     *
     * @param connection the Paradox connection.
     * @param pattern    the directory pattern.
     */
    public DirectoryFilter(final ParadoxConnection connection, final String pattern) {
        this.connection = connection;
        this.pattern = pattern;
    }

    /**
     * Creates a new instance.
     *
     * @param connection the Paradox connection.
     */
    public DirectoryFilter(final ParadoxConnection connection) {
        this(connection, null);
    }

    @Override
    public boolean accept(final File file) {
        boolean expression = true;
        if (pattern != null) {
            expression = Expressions.accept(connection, file.getName(), pattern, false);
        }
        return expression && file != null && file.isDirectory();
    }
}
