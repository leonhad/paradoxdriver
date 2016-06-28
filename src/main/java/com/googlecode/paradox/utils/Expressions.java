/*
 * Expressions.java
 *
 * 03/12/2009
 * Copyright (C) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.utils;

/**
 * Utility class to parse SQL expressions.
 * 
 * @author Leonardo Alves da Costa
 * @since 1.1
 * @version 1.0
 */
public class Expressions {

    /**
     * Utility class.
     */
    private Expressions() {
        // Utility class.
    }

    /**
     * Test for an expression.
     * 
     * @param expression
     *            the expression to test for.
     * @param criteria
     *            the criteria to use.
     * @return true if the expression is valid.
     */
    public static boolean accept(final String expression, final String criteria) {
        return accept(expression, criteria, false);
    }

    /**
     * Test for an expression.
     * 
     * @param expression
     *            the expression to test for.
     * @param criteria
     *            the criteria to use.
     * @param caseSensitive
     *            true if this validation processes is case sensitive.
     * @return true if the expression is valid.
     */
    public static boolean accept(final String expression, final String criteria, final boolean caseSensitive) {
        final char[] crit = caseSensitive ? criteria.toCharArray() : criteria.toUpperCase().toCharArray();
        final char[] exp = caseSensitive ? expression.toCharArray() : expression.toUpperCase().toCharArray();
        final int limit = exp.length - 1;
        int index = 0;

        for (int loop = 0; loop < crit.length; loop++) {
            if (index > limit) {
                return false;
            }
            final char c = crit[loop];

            switch (c) {
            case '?':
                index++;
                break;
            case '%':
                // Has others chars
                if (loop + 1 < crit.length) {
                    final char next = crit[loop + 1];
                    while (index <= limit && next != exp[index]) {
                        index++;
                    }
                    if (index > limit || next != exp[index]) {
                        return false;
                    }
                } else {
                    return true;
                }
                break;
            default:
                if (c == exp[index]) {
                    index++;
                } else {
                    return false;
                }
                break;
            }
        }
        return index > limit;
    }
}
