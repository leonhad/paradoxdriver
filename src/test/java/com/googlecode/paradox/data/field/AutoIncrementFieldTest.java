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
 * Unit test for {@link AutoIncrementField} class.
 *
 * @since 1.3
 */
class AutoIncrementFieldTest {

    /**
     * Test for invalid match.
     */
    @Test
    void testInvalidMatch() {
        final AutoIncrementField field = new AutoIncrementField();
        assertFalse(field.match(ParadoxType.NULL));
    }

    /**
     * Test for parse method.
     */
    @Test
    void testParse() {
        final AutoIncrementField field = new AutoIncrementField();
        final ByteBuffer buffer = ByteBuffer.wrap(new byte[]{0, 0, 1, 0});
        final Object value = field.parse(null, buffer, null);
        assertEquals(256, value);
    }

    /**
     * Test for valid match.
     */
    @Test
    void testValidMatch() {
        final AutoIncrementField field = new AutoIncrementField();
        assertTrue(field.match(ParadoxType.AUTO_INCREMENT));
    }
}
