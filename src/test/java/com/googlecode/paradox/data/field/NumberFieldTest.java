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
package com.googlecode.paradox.data.field;

import com.googlecode.paradox.metadata.Field;
import com.googlecode.paradox.results.ParadoxType;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Unit test for {@link NumberField} class.
 *
 * @version 1.3
 * @since 1.3
 */
public class NumberFieldTest {

    /**
     * Test for decimal values.
     *
     * @throws Exception in case of failures.
     */
    @Test
    public void testDecimalValues() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:paradox:target/test-classes/db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM \"DECIMAL\"")) {

            Assert.assertTrue("First record:", rs.next());
        }
    }

    /**
     * Test for invalid match.
     */
    @Test
    public void testInvalidMatch() {
        final NumberField field = new NumberField();
        Assert.assertFalse("Invalid number value.", field.match(ParadoxType.NULL));
    }

    /**
     * Test for parse method.
     */
    @Test
    public void testParse() {
        final NumberField fieldParser = new NumberField();
        Field field = new Field();
        final ByteBuffer buffer = ByteBuffer.wrap(new byte[]{(byte) 0xC0, (byte) 0x59, (byte) 0x20, 0, 0, 0, 0, 0});
        final Double value = fieldParser.parse(null, buffer, field);
        Assert.assertNotNull("Invalid value.", value);
        Assert.assertEquals("Different values.", 100.5d, value, 0);
    }

    /**
     * Test for valid match.
     */
    @Test
    public void testValidMatch() {
        final NumberField field = new NumberField();
        Assert.assertTrue("Invalid field type.", field.match(ParadoxType.NUMBER));
    }
}
