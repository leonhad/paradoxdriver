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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for {@link BooleanField} class.
 *
 * @since 1.3
 */
class BooleanFieldTest {

    /**
     * Test for invalid match.
     */
    @Test
    void testInvalidMatch() {
        final BooleanField field = new BooleanField();
        assertFalse(field.match(ParadoxType.NULL));
    }

    /**
     * Test for parse false value.
     */
    @Test
    void testParseFalse() {
        final BooleanField field = new BooleanField();
        final ByteBuffer buffer = ByteBuffer.wrap(new byte[]{-128});
        final Boolean value = field.parse(null, buffer, null);
        assertFalse(value);
    }

    /**
     * Test for parse null value.
     */
    @Test
    void testParseNull() {
        final BooleanField field = new BooleanField();
        final ByteBuffer buffer = ByteBuffer.wrap(new byte[]{0});
        final Boolean value = field.parse(null, buffer, null);
        assertNull(value);
    }

    /**
     * Test for parse true value.
     */
    @Test
    void testParseTrue() {
        final BooleanField field = new BooleanField();
        final ByteBuffer buffer = ByteBuffer.wrap(new byte[]{-127});
        final Boolean value = field.parse(null, buffer, null);
        assertTrue(value);
    }

    /**
     * Test for valid match.
     */
    @Test
    void testValidMatch() {
        final BooleanField field = new BooleanField();
        assertTrue(field.match(ParadoxType.BOOLEAN));
    }
}
