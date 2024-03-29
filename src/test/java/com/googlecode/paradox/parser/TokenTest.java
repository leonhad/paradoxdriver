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
package com.googlecode.paradox.parser;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link Token} class.
 *
 * @version 1.0
 * @since 1.3
 */
public class TokenTest {

    /**
     * Test for instance.
     */
    @Test
    public void testInstance() {
        final Token token = new Token(TokenType.AND, "value", new ScannerPosition());
        Assert.assertEquals("Invalid token type.", TokenType.AND, token.getType());
        Assert.assertEquals("Invalid token value.", "value", token.getValue());
    }

    /**
     * Test for {@link Token#toString()} method.
     */
    @Test
    public void testToString() {
        final Token token = new Token(TokenType.AND, "value", new ScannerPosition());
        Assert.assertEquals("Invalid token value.", "AND = value", token.toString());
    }
}
