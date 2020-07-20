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

import com.googlecode.paradox.results.ParadoxType;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * Unit test for {@link IntegerField} class.
 *
 * @version 1.3
 * @since 1.3
 */
public class IntegerFieldTest {
    /**
     * Test for invalid match.
     */
    @Test
    public void testInvalidMatch() {
        final IntegerField field = new IntegerField();
        Assert.assertFalse("Invalid field value.", field.match(ParadoxType.NULL));
    }

    /**
     * Test for parse method.
     */
    @Test
    public void testParse() {
        final IntegerField field = new IntegerField();

        // Test positive numbers
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{(byte) 0x87, (byte) 0xE1});
        Object value = field.parse(null, buffer, null);
        Assert.assertEquals("Invalid number value.", 2017, value);

        // Test negative numbers
        buffer = ByteBuffer.wrap(new byte[]{(byte) 0x40, (byte) 0x59});
        value = field.parse(null, buffer, null);
        Assert.assertEquals("Invalid number value.", -16295, value);
    }

    /**
     * Test for valid match.
     */
    @Test
    public void testValidMatch() {
        final IntegerField field = new IntegerField();
        Assert.assertTrue("Invalid field type.", field.match(ParadoxType.INTEGER));
    }
}
