/*
 * TokenTypeTest.java 07/03/2009 Copyright (C) 2009 Leonardo Alves da Costa This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.parser;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link TokenType} class.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.3
 */
public class TokenTypeTest {

    /**
     * Used to test the conditional breaks.
     */
    private static final TokenType[] CONDITIONAL_BREAKS = { TokenType.ORDER, TokenType.HAVING, TokenType.RPAREN,
            TokenType.LEFT, TokenType.RIGHT, TokenType.OUTER, TokenType.INNER, TokenType.JOIN };

    /**
     * Used to test the operator values.
     */
    private static final TokenType[] OPERATORS = { TokenType.AND, TokenType.OR, TokenType.XOR };

    /**
     * Test for conditional breaks.
     */
    @Test
    public void testConditionalBreaks() {
        for (final TokenType token : TokenType.values()) {
            final boolean test = isConditionalBreak(token);
            Assert.assertEquals(test, TokenType.isConditionalBreak(token));
        }
    }

    /**
     * Test for null conditional break.
     */
    @Test
    public void testNullConditional() {
        Assert.assertFalse(TokenType.isConditionalBreak(null));
    }

    /**
     * Test for null operator.
     */
    @Test
    public void testNullOperators() {
        Assert.assertFalse("Invalid operator.", TokenType.isOperator(null));
    }

    /**
     * Test for operators.
     */
    @Test
    public void testOperators() {
        for (final TokenType token : TokenType.values()) {
            final boolean test = isOperator(token);
            Assert.assertEquals("Operators not the same.", test, TokenType.isOperator(token));
        }
    }

    /**
     * Test the get token by nonexistent value.
     */
    @Test
    public void testTokenByNonexistentValue() {
        Assert.assertNull(TokenType.get("invalid"));
    }

    /**
     * Test the get token by value.
     */
    @Test
    public void testTokenByValue() {
        Assert.assertEquals(TokenType.PLUS, TokenType.get("+"));
    }

    /**
     * Test for a conditional break.
     *
     * @param tokenType
     *            the token to test.
     * @return true if the token is a conditional break;
     */
    private boolean isConditionalBreak(final TokenType tokenType) {
        for (final TokenType token : TokenTypeTest.CONDITIONAL_BREAKS) {
            if (token.equals(tokenType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Test for operator types.
     *
     * @param tokenType
     *            the type to test of.
     * @return true if this token is an operator.
     */
    private boolean isOperator(final TokenType tokenType) {
        for (final TokenType token : TokenTypeTest.OPERATORS) {
            if (token.equals(tokenType)) {
                return true;
            }
        }
        return false;
    }
}
