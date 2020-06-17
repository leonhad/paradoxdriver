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

import com.googlecode.paradox.data.table.value.FieldValue;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link IntegerField} class.
 *
 * @author Leonardo Alves da Costa
 * @since 1.3
 * @version 1.0
 */
public class IntegerFieldTest {
    /**
     * Test for invalid match.
     */
    @Test
    public void testInvalidMatch() {
        final IntegerField field = new IntegerField();
        Assert.assertFalse(field.match(0));
    }
    
    /**
     * Test for parse method.
     *
     * @throws SQLException
     *             in case of parse errors.
     */
    @Test
    public void testParse() throws SQLException {
        final IntegerField field = new IntegerField();
        
        // Test positive numbers
        ByteBuffer buffer = ByteBuffer.wrap(new byte[] { (byte) 0x87, (byte) 0xE1 });
        FieldValue value = field.parse(null, buffer, null);
        Assert.assertEquals(2017, value.getNumber());
        
        // Test negative numbers
        buffer = ByteBuffer.wrap(new byte[] { (byte) 0x40, (byte) 0x59 });
        value = field.parse(null, buffer, null);
        Assert.assertEquals(-16295, value.getNumber());
    }
    
    /**
     * Test for valid match.
     */
    @Test
    public void testValidMatch() {
        final IntegerField field = new IntegerField();
        Assert.assertTrue(field.match(3));
    }
}
