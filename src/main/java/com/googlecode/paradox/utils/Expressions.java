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
        final int expressionLimit = exp.length;
        int index = 0;
        int loop;

        for (loop = 0; loop < criterion.length && index < expressionLimit; loop++) {
            final char c = criterion[loop];

            if (c == '_') {
                // Accept any char, but only one.
                index++;
            } else if (c == '%') {
                // Has more chars?
                if (loop + 1 >= criterion.length) {
                    // If not, the % gets any more chars in list.
                    // Sets the expression index to the end.
                    index = expressionLimit + 1;
                } else {
                    final char next = criterion[loop + 1];
                    index = nextIndex(exp, index, next);
                }
            } else {
                if (c != exp[index]) {
                    break;
                }

                ++index;
            }
        }

        return index >= expressionLimit && loop == criterion.length;
    }

    private static int nextIndex(final char[] expression, final int currentIndex, final char next) {
        int index = currentIndex;
        while (index < expression.length && next != expression[index]) {
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
