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

import com.googlecode.paradox.data.ParadoxBuffer;
import com.googlecode.paradox.data.table.value.FieldValue;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;

/**
 * Unit test for {@link LongField} class.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.3
 */
public class LongFieldTest {
    /**
     * Test for invalid match.
     */
    @Test
    public void testInvalidMatch() {
        final LongField field = new LongField();
        Assert.assertFalse("Invalid field type.", field.match(0));
    }

    /**
     * Test for parse method.
     *
     * @throws SQLException in case of parse errors.
     */
    @Test
    public void testParse() throws SQLException {
        final LongField field = new LongField();

        // Test positive values
        ParadoxBuffer buffer = new ParadoxBuffer(new byte[]{(byte) 0x80, (byte) 0x00, (byte) 0x01, (byte) 0x00});
        FieldValue value = field.parse(null, buffer, null);
        Assert.assertEquals("Invalid number value.", 256L, value.getNumber());

        // Test negative values
        buffer = new ParadoxBuffer(new byte[]{(byte) 0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0x00});
        value = field.parse(null, buffer, null);
        Assert.assertEquals("Invalid number value.", -256L, value.getNumber());
    }

    /**
     * Test for valid match.
     */
    @Test
    public void testValidMatch() {
        final LongField field = new LongField();
        Assert.assertTrue("Invalid field type.", field.match(4));
    }
}
