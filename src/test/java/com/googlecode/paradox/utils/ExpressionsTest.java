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

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for {@link Expressions}.
 *
 * @since 1.1
 */
class ExpressionsTest {

    /**
     * Test for equals.
     */
    @Test
    void testEquals() {
        assertTrue(Expressions.accept(Locale.ENGLISH, "TABLE", "table", false, Constants.ESCAPE_CHAR));
    }

    /**
     * Test for an extra value.
     */
    @Test
    void testExtra() {
        assertFalse(Expressions.accept(Locale.ENGLISH, "TEST.QBE~", "%.QBE", false, Constants.ESCAPE_CHAR));
    }

    /**
     * Test for like.
     */
    @Test
    void testLikes() {
        assertTrue(Expressions.accept(Locale.ENGLISH, "TABLE", "TA%", false, Constants.ESCAPE_CHAR));
    }

    /**
     * Test for mixed values.
     */
    @Test
    void testMix() {
        assertTrue(Expressions.accept(Locale.ENGLISH, "TEST.X02", "%.X__", false, Constants.ESCAPE_CHAR));
    }

    /**
     * Test for not equals.
     */
    @Test
    void testNotEquals() {
        assertFalse(Expressions.accept(Locale.ENGLISH, "TABLE", "table", true, Constants.ESCAPE_CHAR));
    }

    /**
     * Test for sanity.
     */
    @Test
    void testSanity() {
        assertTrue(TestUtil.assertSanity(Expressions.class));
    }

    /**
     * Test for unique keyword.
     */
    @Test
    void testUnique() {
        assertTrue(Expressions.accept(Locale.ENGLISH, "TABLE", "TAB_E", false, Constants.ESCAPE_CHAR));
    }

    /**
     * Test for escaped %.
     */
    @Test
    void testEscapedChar() {
        assertTrue(Expressions.accept(Locale.ENGLISH, "%a%", "\\%a\\%", true, Constants.ESCAPE_CHAR));
    }

    /**
     * Test for unescaped %.
     */
    @Test
    void testUnescapedChar() {
        assertTrue(Expressions.accept(Locale.ENGLISH, "k, Jersey City, Newark", "k%", true, Constants.ESCAPE_CHAR));
    }
}
