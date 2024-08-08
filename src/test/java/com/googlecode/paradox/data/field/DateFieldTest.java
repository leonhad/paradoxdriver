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

import com.googlecode.paradox.results.ParadoxType;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for {@link DateField} class.
 *
 * @since 1.3
 */
class DateFieldTest {

    /**
     * Test for invalid match.
     */
    @Test
    void testInvalidMatch() {
        final DateField field = new DateField();
        assertFalse(field.match(ParadoxType.NULL));
    }

    /**
     * Test for parse method.
     */
    @Test
    void testParse() {
        final DateField field = new DateField();
        final ByteBuffer buffer = ByteBuffer.wrap(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x8D, (byte) 0x40});
        final Date value = field.parse(null, buffer, null);
        assertEquals("0100-01-01", value.toString());
    }

    /**
     * Test for parse method.
     */
    @Test
    void testParse2() {
        final DateField field = new DateField();
        final ByteBuffer buffer = ByteBuffer.wrap(new byte[]{(byte) 0x00, (byte) 0x0B, (byte) 0x1E, (byte) 0xCF});
        final Date value = field.parse(null, buffer, null);
        assertEquals("1996-05-04", value.toString());
    }

    /**
     * Test for valid match.
     */
    @Test
    void testValidMatch() {
        final DateField field = new DateField();
        assertTrue(field.match(ParadoxType.DATE));
    }
}
