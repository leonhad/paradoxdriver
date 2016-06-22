package com.googlecode.paradox.utils.filefilters;

import java.io.File;
import java.io.FileFilter;

import com.googlecode.paradox.utils.Expressions;

/**
 * Paradox Table Filters
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 14/03/2009
 */
public class TableFilter implements FileFilter {

    private String tableName;
    private String extension;

    public TableFilter() {
        tableName = null;
        extension = "db";
    }

    public TableFilter(final String tableName) {
        this();
        this.tableName = tableName;
    }

    public TableFilter(final String tableName, final String extension) {
        this(tableName);
        this.extension = extension;
    }

    @Override
    public boolean accept(final File pathname) {
        final String name = pathname.getName();

        if (tableName != null) {
            return Expressions.accept(name, tableName + "." + extension, false);
        }
        return Expressions.accept(name, "%." + extension, false);
    }

}
