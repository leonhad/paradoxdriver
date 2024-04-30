/*
 * Copyright (c) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.googlecode.paradox.data.filefilters;

import com.googlecode.paradox.utils.Constants;
import com.googlecode.paradox.utils.Expressions;

import java.io.File;
import java.io.FileFilter;
import java.util.Locale;

/**
 * If the file is a directory.
 *
 * @version 1.1
 * @since 1.4
 */
public class DirectoryFilter implements FileFilter {

    private final String pattern;

    private final Locale locale;

    /**
     * Creates a new instance.
     *
     * @param locale  the locale to use.
     * @param pattern the directory pattern.
     */
    public DirectoryFilter(final Locale locale, final String pattern) {
        this.locale = locale;
        this.pattern = pattern;
    }

    /**
     * Creates a new instance.
     *
     * @param locale the locale to use.
     */
    public DirectoryFilter(final Locale locale) {
        this(locale, null);
    }

    @Override
    public boolean accept(final File file) {
        boolean expression = true;
        if (pattern != null) {
            expression = Expressions.accept(locale, file.getName(), pattern, false, Constants.ESCAPE_CHAR);
        }
        return expression && file != null && file.isDirectory();
    }
}
