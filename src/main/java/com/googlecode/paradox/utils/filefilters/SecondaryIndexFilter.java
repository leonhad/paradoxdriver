package com.googlecode.paradox.utils.filefilters;

import java.io.File;
import java.io.FileFilter;

import com.googlecode.paradox.utils.Expressions;

/**
 *
 * @author 72330554168
 */
public class SecondaryIndexFilter implements FileFilter {

    private final String indexName;

    public SecondaryIndexFilter() {
        indexName = null;
    }

    public SecondaryIndexFilter(final String indexName) {
        this.indexName = indexName;
    }

    @Override
    public boolean accept(final File pathname) {
        final String name = pathname.getName();

        if (Expressions.accept(name, "%.X??")) {
            if (indexName != null) {
                return Expressions.accept(name, indexName);
            }
            return true;
        }
        return false;
    }

}
