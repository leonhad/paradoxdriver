package com.googlecode.paradox.utils.filefilters;

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

    /**
     * Creates a new instance.
     *
     * @param pattern the directory pattern.
     */
    public DirectoryFilter(String pattern) {
        this.pattern = pattern;
    }

    /**
     * Creates a new instance.
     */
    public DirectoryFilter() {
        this(null);
    }

    @Override
    public boolean accept(final File file) {
        boolean expression = true;
        if (pattern != null) {
            expression = Expressions.accept(file.getName(), pattern, false);
        }
        return expression && file != null && file.isDirectory();
    }
}
