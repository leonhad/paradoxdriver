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
package com.googlecode.paradox.data.field;

import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * Unit test for {@link BooleanField} class.
 *
 * @author Leonardo Costa
 * @version 1.2
 * @since 1.3
 */
public class BooleanFieldTest {
    /**
     * Test for invalid match.
     */
    @Test
    public void testInvalidMatch() {
        final BooleanField field = new BooleanField();
        Assert.assertFalse("Invalid field value.", field.match(0));
    }

    /**
     * Test for parse false value.
     */
    @Test
    public void testParseFalse() {
        final BooleanField field = new BooleanField();
        final ByteBuffer buffer = ByteBuffer.wrap(new byte[]{-128});
        final Boolean value = field.parse(null, buffer, null);
        Assert.assertFalse("Invalid boolean value.", value);
    }

    /**
     * Test for parse null value.
     */
    @Test
    public void testParseNull() {
        final BooleanField field = new BooleanField();
        final ByteBuffer buffer = ByteBuffer.wrap(new byte[]{0});
        final Boolean value = field.parse(null, buffer, null);
        Assert.assertNull("Invalid boolean value.", value);
    }

    /**
     * Test for parse true value.
     */
    @Test
    public void testParseTrue() {
        final BooleanField field = new BooleanField();
        final ByteBuffer buffer = ByteBuffer.wrap(new byte[]{-127});
        final Boolean value = field.parse(null, buffer, null);
        Assert.assertTrue("Invalid boolean value.", value);
    }

    /**
     * Test for valid match.
     */
    @Test
    public void testValidMatch() {
        final BooleanField field = new BooleanField();
        Assert.assertTrue("Invalid field type.", field.match(9));
    }
}
