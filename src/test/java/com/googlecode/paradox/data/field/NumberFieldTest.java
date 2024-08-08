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
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for {@link NumberField} class.
 *
 * @version 1.3
 * @since 1.3
 */
class NumberFieldTest {

    /**
     * Test for decimal values.
     *
     * @throws Exception in case of failures.
     */
    @Test
    void testDecimalValues() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:paradox:target/test-classes/db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM \"DECIMAL\"")) {

            assertTrue(rs.next());
        }
    }

    /**
     * Test for invalid match.
     */
    @Test
    void testInvalidMatch() {
        final NumberField field = new NumberField();
        assertFalse(field.match(ParadoxType.NULL));
    }

    /**
     * Test for parse method.
     */
    @Test
    void testParse() {
        final NumberField fieldParser = new NumberField();
        Field field = new Field();
        final ByteBuffer buffer = ByteBuffer.wrap(new byte[]{(byte) 0xC0, (byte) 0x59, (byte) 0x20, 0, 0, 0, 0, 0});
        final Double value = fieldParser.parse(null, buffer, field);
        assertNotNull(value);
        assertEquals(100.5d, value, 0);
    }

    /**
     * Test for valid match.
     */
    @Test
    void testValidMatch() {
        final NumberField field = new NumberField();
        assertTrue(field.match(ParadoxType.NUMBER));
    }
}
