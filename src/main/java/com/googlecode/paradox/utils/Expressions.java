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

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class to parse SQL expressions.
 *
 * @version 1.0
 * @since 1.1
 */
public final class Expressions {

    /**
     * The class logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Expressions.class.getName());

    /**
     * Utility class.
     */
    private Expressions() {
        // Utility class.
    }

    /**
     * Test for an expression.
     *
     * @param conn       the Paradox connection.
     * @param expression the expression to test for.
     * @param criteria   the criteria to use.
     * @return true if the expression is valid.
     */
    public static boolean accept(final ParadoxConnection conn, final String expression, final String criteria) {
        return Expressions.accept(conn, expression, criteria, false);
    }

    /**
     * Test for an expression.
     *
     * @param conn          the Paradox connection.
     * @param expression    the expression to test for.
     * @param criteria      the criteria to use.
     * @param caseSensitive true if this validation processes is case sensitive.
     * @return true if the expression is valid.
     */
    public static boolean accept(final ParadoxConnection conn, final String expression, final String criteria,
                                 final boolean caseSensitive) {
        try {
            Expressions.acceptExpression(conn, expression, criteria, caseSensitive);
        } catch (final SQLException e) {
            Expressions.LOGGER.log(Level.FINER, e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * Test for an expression.
     *
     * @param conn          the Paradox connection.
     * @param expression    the expression to test for.
     * @param criteria      the criteria to use.
     * @param caseSensitive true if this validation processes is case sensitive.
     * @throws SQLException in case of invalid expression.
     */
    private static void acceptExpression(final ParadoxConnection conn, final String expression, final String criteria
            , final boolean caseSensitive)
            throws SQLException {
        final char[] criterion = Expressions.getCharArrayWithCase(conn, criteria, caseSensitive);
        final char[] exp = Expressions.getCharArrayWithCase(conn, expression, caseSensitive);
        final int limit = exp.length - 1;
        int index = 0;

        for (int loop = 0; loop < criterion.length; loop++) {
            if (index > limit) {
                throw new SQLException();
            }
            final char c = criterion[loop];

            if (c == '?') {
                index++;
            } else if (c == '%') {
                // Has others chars
                if ((loop + 1) < criterion.length) {
                    final char next = criterion[loop + 1];
                    index = Expressions.fixIndex(exp, limit, index, next);
                    Expressions.checkBounds(exp, limit, index, next);
                } else {
                    return;
                }
            } else {
                Expressions.checkIndexBoundaries(exp, index, c);
                ++index;
            }
        }
        if (index <= limit) {
            throw new SQLException();
        }
    }

    /**
     * Check for expressions limit boundaries.
     *
     * @param exp   the expression to check.
     * @param limit the value limits.
     * @param index the current index.
     * @param next  the next char in criteria.
     * @throws SQLException in case of invalid expression.
     */
    private static void checkBounds(final char[] exp, final int limit, final int index, final char next)
            throws SQLException {
        if ((index > limit) || (next != exp[index])) {
            throw new SQLException();
        }
    }

    /**
     * Check for index boundaries.
     *
     * @param exp   the expression to check.
     * @param index the current index.
     * @param c     the current char in expression.
     * @throws SQLException in case of invalid expression.
     */
    private static void checkIndexBoundaries(final char[] exp, final int index, final char c) throws SQLException {
        if (c != exp[index]) {
            throw new SQLException();
        }
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
