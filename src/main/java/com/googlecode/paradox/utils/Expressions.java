/*
 * Copyright (C) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.utils;

import com.googlecode.paradox.ParadoxConnection;

/**
 * Utility class to parse SQL expressions.
 *
 * @version 1.2
 * @since 1.1
 */
public final class Expressions {

    /**
     * Utility class.
     */
    private Expressions() {
        // Utility class.
    }

    /**
     * Test for an expression.
     *
     * @param conn          the Paradox connection.
     * @param expression    the expression to test for.
     * @param criteria      the criteria to use.
     * @param caseSensitive true if this validation processes is case sensitive.
     * @return <code>true</code> if the expression is valid.
     */
    public static boolean accept(final ParadoxConnection conn, final String expression, final String criteria,
                                 final boolean caseSensitive) {
        final char[] criterion = getCharArrayWithCase(conn, criteria, caseSensitive);
        final char[] exp = getCharArrayWithCase(conn, expression, caseSensitive);
        final int limit = exp.length - 1;
        int index = 0;

        for (int loop = 0; loop < criterion.length; loop++) {
            if (index > limit) {
                return false;
            }
            final char c = criterion[loop];

            if (c == '_') {
                index++;
            } else if (c == '%') {
                // Has more chars.
                if ((loop + 1) < criterion.length) {
                    final char next = criterion[loop + 1];
                    index = Expressions.fixIndex(exp, limit, index, next);

                    if ((index > limit) || (next != exp[index])) {
                        return false;
                    }
                } else {
                    return true;
                }
            } else {
                if (c != exp[index]) {
                    return false;
                }

                ++index;
            }
        }

        return index > limit;
    }

    /**
     * Fix index based on % position.
     *
     * @param exp    the expression to test.
     * @param limit  the expression max size.
     * @param offset the offset to start of.
     * @param next   the next char on criteria.
     * @return new index.
     */
    private static int fixIndex(final char[] exp, final int limit, final int offset, final char next) {
        int index = offset;
        while ((index <= limit) && (next != exp[index])) {
            index++;
        }

        return index;
    }

    /**
     * Gets a char array with case option.
     *
     * @param conn          the Paradox connection.
     * @param str           the string to convert.
     * @param caseSensitive if use case sensitive option.
     * @return the char array.
     */
    private static char[] getCharArrayWithCase(final ParadoxConnection conn, final String str,
                                               final boolean caseSensitive) {
        if (caseSensitive) {
            return str.toCharArray();
        }

        return str.toUpperCase(conn.getLocale()).toCharArray();
    }
}
