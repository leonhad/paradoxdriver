/*
 * Copyright (c) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.utils;

import java.util.Locale;

/**
 * Utility class to parse SQL expressions.
 *
 * @version 1.4
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
     * @param locale        the locale to use.
     * @param expression    the expression to test for.
     * @param criteria      the criteria to use.
     * @param caseSensitive true if this validation processes is case sensitive.
     * @param escape        the escape char.
     * @return <code>true</code> if the expression is valid.
     */
    public static boolean accept(final Locale locale, final String expression, final String criteria,
                                 final boolean caseSensitive, final char escape) {
        if ("%".equals(criteria) || criteria == null) {
            return true;
        }

        final char[] criterion = getCharArrayWithCase(locale, criteria, caseSensitive);
        final char[] exp = getCharArrayWithCase(locale, expression, caseSensitive);
        int index = 0;
        int criterionIndex;

        for (criterionIndex = 0; criterionIndex < criterion.length && index < exp.length; criterionIndex++) {
            char c = criterion[criterionIndex];

            if (c == '_') {
                // Accept any char, but only one.
                index++;
            } else if (c == '%') {
                index = getIndex(criterion, exp, index, criterionIndex);
            } else {
                // Is it an escaped char?
                if (isEscapedChar(criterionIndex, criterion, escape)) {
                    criterionIndex++;
                    c = criterion[criterionIndex];
                }

                if (c != exp[index]) {
                    break;
                }

                ++index;
            }
        }

        return index >= exp.length && criterionIndex == criterion.length;
    }

    /**
     * Gets the next % valid index.
     *
     * @param criterion       the criterion with current %.
     * @param expression      the expression to test.
     * @param expressionIndex the current expression index.
     * @param criterionIndex  the criterion index.
     * @return the next valid index based on % criterion.
     */
    private static int getIndex(final char[] criterion, final char[] expression, final int expressionIndex,
                                final int criterionIndex) {
        int index = expressionIndex;
        // Has more chars?
        if (criterionIndex + 1 >= criterion.length) {
            // If not, the % gets any more chars in list.
            // Sets the expression index to the end.
            index = expression.length + 1;
        } else {
            final char next = criterion[criterionIndex + 1];
            index = nextIndex(expression, index, next);
        }
        return index;
    }

    /**
     * Test for LIKE escape characters.
     *
     * @param currentIndex the current criterion index.
     * @param criterion    the criterion to look at.
     * @param escape       the escape char.
     * @return <code>true</code> if the current char is an escaped character.
     */
    private static boolean isEscapedChar(final int currentIndex, final char[] criterion, final char escape) {
        boolean ret = false;

        if (criterion[currentIndex] == escape && currentIndex + 1 < criterion.length) {
            final char next = criterion[currentIndex + 1];

            switch (next) {
                case '%':
                case '\\':
                case '_':
                    ret = true;
                    break;

                default:
                    ret = false;
                    break;
            }
        }

        return ret;
    }

    /**
     * Gets a next valid index based on % criteria char.
     *
     * @param expression   the expression to test.
     * @param currentIndex the current index on expression testing.
     * @param next         the next criteria char.
     * @return the index on expression when characters match.
     */
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
     * @param locale        the locale to use.
     * @param str           the string to convert.
     * @param caseSensitive if use case sensitive option.
     * @return the char array.
     */
    private static char[] getCharArrayWithCase(final Locale locale, final String str, final boolean caseSensitive) {
        if (str == null) {
            return new char[0];
        }

        if (caseSensitive) {
            return str.toCharArray();
        }

        return str.toUpperCase(locale).toCharArray();
    }
}
