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
package com.googlecode.paradox.parser;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link TokenType} class.
 *
 * @version 1.2
 * @since 1.3
 */
public class TokenTypeTest {

    /**
     * Test for null conditional break.
     */
    @Test
    public void testNullConditional() {
        Assert.assertFalse("Invalid token type.", TokenType.isConditionalBreak(null));
    }

    /**
     * Test for null operator.
     */
    @Test
    public void testNullOperators() {
        Assert.assertFalse("Invalid operator.", TokenType.isOperator(null));
    }

    /**
     * Test the get token by nonexistent value.
     */
    @Test
    public void testTokenByNonexistentValue() {
        Assert.assertNull("Invalid token type.", TokenType.get("invalid"));
    }

    /**
     * Test the get token by value.
     */
    @Test
    public void testTokenByValue() {
        Assert.assertEquals("Invalid token type.", TokenType.PLUS, TokenType.get("+"));
    }
}
