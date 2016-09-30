/*
 * SecondaryIndexFilter.java
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
 * Paradox secondary key file filter (Index Key).
 *
 * @author Leonardo Alves da Costa
 * @since 1.0
 * @version 1.0
 */
public class SecondaryIndexFilter implements FileFilter {

    /**
     * The index name.
     */
    private final String indexName;

    /**
     * Create a new instance.
     */
    public SecondaryIndexFilter() {
        indexName = null;
    }

    /**
     * Create a new instance.
     * 
     * @param indexName
     *            the index name.
     */
    public SecondaryIndexFilter(final String indexName) {
        this.indexName = indexName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(final File pathname) {
        final String name = pathname.getName();

        if (Expressions.accept(name, "%.X??")) {
            return indexName == null || Expressions.accept(name, indexName);
        }
        return false;
    }

}
