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
package com.googlecode.paradox.metadata;

import com.googlecode.paradox.metadata.paradox.ParadoxField;
import com.googlecode.paradox.results.ParadoxType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for {@link ParadoxField} class.
 *
 * @since 1.3
 */
class ParadoxFieldTest {

    /**
     * Test for auto increment.
     */
    @Test
    void testAutoIncrement() {
        final ParadoxField field = new ParadoxField(ParadoxType.AUTO_INCREMENT);
        assertTrue(field.isAutoIncrement());
    }

    /**
     * Test for default order.
     */
    @Test
    void testDefaultOrder() {
        final ParadoxField field = new ParadoxField(ParadoxType.VARCHAR);
        assertEquals(1, field.getOrderNum());
    }

    /**
     * Test for empty alias.
     */
    @Test
    void testEmptyAlias() {
        final ParadoxField field = new ParadoxField(ParadoxType.VARCHAR);
        field.setName("Field");
        assertEquals("Field", field.getAlias());
    }

    /**
     * Test for {@link ParadoxField#equals(Object)} method.
     */
    @Test
    void testEquals() {
        final ParadoxField first = new ParadoxField(ParadoxType.VARCHAR);
        first.setName("Field");
        final ParadoxField last = new ParadoxField(ParadoxType.VARCHAR);
        last.setName("Field");
        assertEquals(last, first);
    }

    /**
     * Test for getters and setters.
     */
    @Test
    void testGettersAndSetters() {
        final ParadoxField field = new ParadoxField(ParadoxType.VARCHAR);
        field.setAlias("alias");
        field.setName("name");
        field.setTable(null);

        assertEquals("alias", field.getAlias());
        assertEquals("name", field.getName());
        assertNull(field.getTable());
    }

    /**
     * Test for {@link ParadoxField#hashCode()} method.
     */
    @Test
    void testHashCode() {
        final ParadoxField field = new ParadoxField(ParadoxType.VARCHAR);
        field.setName("Field");
        assertNotEquals(0, field.hashCode());
    }

    /**
     * Test for not auto increment.
     */
    @Test
    void testNotAutoIncrement() {
        final ParadoxField field = new ParadoxField(ParadoxType.VARCHAR);
        assertFalse(field.isAutoIncrement());
    }

    /**
     * Test for {@link ParadoxField#equals(Object)} method different values.
     */
    @Test
    void testNotEquals() {
        final ParadoxField first = new ParadoxField(ParadoxType.VARCHAR);
        first.setName("Field");
        final ParadoxField last = new ParadoxField(ParadoxType.VARCHAR);
        last.setName("Field 2");
        assertNotEquals(first, last);

        first.setName(null);
        last.setName("Field 2");
        assertNotEquals(first, last);

        first.setName("Field");
        last.setName(null);
        assertNotEquals(first, last);

        first.setName(null);
        last.setName(null);
        assertEquals(first, last);
    }

    /**
     * Test for {@link ParadoxField#getSize()} and {@link ParadoxField#setSize(int)}
     * method.
     */
    @Test
    void testSize() {
        // Not CLOB or BLOB type
        final ParadoxField field = new ParadoxField(ParadoxType.VARCHAR);
        field.setSize(10);
        assertEquals(10, field.getSize());
    }

    /**
     * Test for {@link ParadoxField#toString()} method.
     */
    @Test
    void testToString() {
        final ParadoxField first = new ParadoxField(ParadoxType.VARCHAR);
        first.setName("Field");
        assertEquals("Field", first.toString());
    }
}
