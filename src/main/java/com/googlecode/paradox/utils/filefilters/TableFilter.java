/*
 * TableFilter.java 03/12/2009 Copyright (C) 2009 Leonardo Alves da Costa This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.utils.filefilters;

import com.googlecode.paradox.utils.Expressions;
import java.io.File;
import java.io.FileFilter;

/**
 * Paradox Table filters.
 *
 * @author Leonardo Alves da Costa
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
    
    /**
     * Create a new instance.
     */
    public TableFilter() {
        this(null, "db");
    }
    
    /**
     * Create a new instance.
     *
     * @param tableName
     *            the table name.
     */
    public TableFilter(final String tableName) {
        this(tableName, "db");
    }
    
    /**
     * Create a new instance.
     *
     * @param tableName
     *            the table name.
     * @param extension
     *            the table extension.
     */
    public TableFilter(final String tableName, final String extension) {
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
            return Expressions.accept(name, this.tableName + "." + this.extension, false);
        }
        return Expressions.accept(name, "%." + this.extension, false);
    }
    
}
