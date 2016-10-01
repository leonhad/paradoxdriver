/*
 * ViewFilter.java
 *
 * 03/12/2009
 * Copyright (C) 2009 Leonardo Alves da Costa
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
package com.googlecode.paradox.utils.filefilters;

import com.googlecode.paradox.utils.Expressions;

import java.io.File;
import java.io.FileFilter;

/**
 * Paradox view filter.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.0
 */
public final class ViewFilter implements FileFilter {

    /**
     * The view name.
     */
    private final String viewName;

    /**
     * Create a new instance;
     */
    ViewFilter() {
        this(null);
    }

    /**
     * Create a new instance.
     *
     * @param viewName
     *         the view name.
     */
    public ViewFilter(final String viewName) {
        this.viewName = viewName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(final File pathname) {
        final String name = pathname.getName();

        return Expressions.accept(name, "%.QBE") && (viewName == null || Expressions.accept(name, viewName));
    }

}
