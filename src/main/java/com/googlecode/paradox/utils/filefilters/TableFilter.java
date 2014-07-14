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

    private final String tableName;

    public TableFilter() {
        tableName = null;
    }

    public TableFilter(final String tableName) {
        this.tableName = tableName;
    }

    @Override
    public boolean accept(final File pathname) {
        final String name = pathname.getName();

        if (Expressions.accept(name, "%.DB")) {
            if (tableName != null) {
                return Expressions.accept(name, tableName);
            }
            return true;
        }
        return false;
    }

}
