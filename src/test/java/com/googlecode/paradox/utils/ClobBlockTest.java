/*
 * ClobBlockTest.java
 *
 * 06/29/2009
 * Copyright (C) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link ClobBlock} class.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.3
 */
public class ClobBlockTest {

    /**
     * Test for getters.
     */
    @Test
    public void testGetters() {
        final ClobBlock clob = new ClobBlock(2, 1, (short) 0);
        Assert.assertEquals("Invalid clob number.", 2, clob.getNum());
        Assert.assertEquals("Invalid clob type.", 1, clob.getType());
        Assert.assertEquals("Invalid clob offset.", 0, clob.getOffset());
        Assert.assertNull("Invalid clob value.", clob.getValue());
    }
}
