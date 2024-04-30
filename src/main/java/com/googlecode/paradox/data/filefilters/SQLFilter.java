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
 * SQL file filter.
 *
 * @since 1.6.0
 */
public final class SQLFilter implements FileFilter {

    /**
     * The file extension.
     */
    private final String extension;

    /**
     * Locale to search.
     */
    private final Locale locale;

    /**
     * Create a new instance.
     *
     * @param locale the locale to use.
     */
    public SQLFilter(final Locale locale) {
        this.locale = locale;
        this.extension = "sql";
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean accept(final File pathname) {
        final String name = pathname.getName();
        return Expressions.accept(locale, name, "%." + this.extension, false, Constants.ESCAPE_CHAR) && pathname.isFile();
    }

}
