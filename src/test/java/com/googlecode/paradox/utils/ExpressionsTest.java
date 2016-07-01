/*
 * ExpressionsTest.java
 *
 * 03/12/2009
 * Copyright (C) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import static com.googlecode.paradox.utils.Expressions.accept;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

/**
 * Unit test for {@link Expressions}.
 * 
 * @author Leonardo Alves da Costa
 * @since 1.1
 * @version 1.1
 */
public class ExpressionsTest {

    /**
     * Test for equals.
     */
    @Test
    public void testEquals() {
        assertTrue(accept("TABLE", "table", false));
    }

    /**
     * Test for an extra value.
     */
    @Test
    public void testExtra() {
        assertFalse(accept("TESTE.QBE~", "%.QBE"));
    }

    /**
     * Test for like.
     */
    @Test
    public void testLikes() {
        assertTrue(accept("TABLE", "TA%"));
    }

    /**
     * Test for mixed values.
     */
    @Test
    public void testMix() {
        assertTrue(accept("TESTE.X02", "%.X??"));
    }

    /**
     * Test for not equals.
     */
    @Test
    public void testNotEquals() {
        assertFalse(accept("TABLE", "table", true));
    }

    /**
     * Test for sanity.
     * 
     * @throws IllegalAccessException
     *             in case of errors.
     * @throws InstantiationException
     *             in case of errors.
     * @throws InvocationTargetException
     *             in case of errors.
     * @throws NoSuchMethodException
     *             in case of errors.
     */
    @Test
    public void testSanity() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        TestUtil.assertUtilityClassWellDefined(Expressions.class);
    }

    /**
     * Test for unique keyword.
     */
    @Test
    public void testUnique() {
        assertTrue(accept("TABLE", "TAB?E"));
    }
}