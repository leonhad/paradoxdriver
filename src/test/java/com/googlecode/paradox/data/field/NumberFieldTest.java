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

import java.sql.*;

/**
 * Unit test for {@link NumberField} class.
 *
 * @author Leonardo Alves da Costa
 * @version 1.1
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
        Assert.assertFalse("Invalid number value.", field.match(0));
    }

    /**
     * Test for parse method.
     *
     * @throws SQLException in case of parse errors.
     */
    @Test
    public void testParse() throws SQLException {
        final NumberField field = new NumberField();
        final ParadoxBuffer buffer = new ParadoxBuffer(new byte[]{
                (byte) 0xC0, (byte) 0x59, (byte) 0x20, 0, 0, 0, 0, 0}
        );
        final FieldValue value = field.parse(null, buffer, null);
        Assert.assertEquals("Different values.", 100.5d, value.getNumber().doubleValue(), 0);
    }

    /**
     * Test for valid match.
     */
    @Test
    public void testValidMatch() {
        final NumberField field = new NumberField();
        Assert.assertTrue("Invalid field type.", field.match(5));
        Assert.assertTrue("Invalid field type.", field.match(6));
    }
}
