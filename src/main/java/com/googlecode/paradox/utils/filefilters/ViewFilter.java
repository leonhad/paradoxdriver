package com.googlecode.paradox.utils.filefilters;

import java.io.File;
import java.io.FileFilter;

import com.googlecode.paradox.utils.Expressions;

/**
 * Paradox View Filter
 * 
 * @author Leonardo Alves da Costa
 * @since 04/12/2009
 * @version 1.0
 */
public class ViewFilter  implements FileFilter {
    private final String viewName;

    public ViewFilter() {
        viewName = null;
    }

    public ViewFilter(final String viewName) {
        this.viewName = viewName;
    }

    @Override
    public boolean accept(final File pathname) {
        final String name = pathname.getName();

        if (Expressions.accept(name, "%.QBE")) {
            if (viewName != null) {
                return Expressions.accept(name, viewName);
            }
            return true;
        }
        return false;
    }

}
