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
package com.googlecode.paradox.results;

import com.googlecode.paradox.metadata.paradox.ParadoxField;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for {@link Column} class.
 *
 * @since 1.3
 */
class ColumnTest {

    /**
     * Test for auto increment.
     */
    @Test
    void testAutoincrement() {
        final Column column = new Column(new ParadoxField(ParadoxType.AUTO_INCREMENT));
        assertTrue(column.isAutoIncrement());

        final Column column2 = new Column("NAME", ParadoxType.INTEGER);
        assertFalse(column2.isAutoIncrement());
    }

    /**
     * Test for currency.
     */
    @Test
    void testCurrency() {
        final Column column = new Column(new ParadoxField(ParadoxType.CURRENCY));
        assertTrue(column.isCurrency());

        final Column column2 = new Column("NAME", ParadoxType.INTEGER);
        assertFalse(column2.isCurrency());
    }

    /**
     * Test field.
     */
    @Test
    void testField() {
        final ParadoxField field = new ParadoxField(ParadoxType.INTEGER);
        final Column column = new Column(field);
        assertEquals(field, column.getField());
    }

    /**
     * Test for index.
     */
    @Test
    void testIndex() {
        final Column column = new Column(new ParadoxField(ParadoxType.VARCHAR));
        column.setIndex(1);
        assertEquals(1, column.getIndex());
    }

    /**
     * Test instance with field.
     */
    @Test
    void testInstanceWithFields() {
        final ParadoxField field = new ParadoxField(ParadoxType.INTEGER);
        field.setName("field");
        final Column column = new Column(field);
        assertEquals(field, column.getField());
        assertEquals("field", column.getName());
        assertEquals(ParadoxType.INTEGER, column.getType());
    }

    /**
     * Test instance with values.
     */
    @Test
    void testInstanceWithValues() {
        final Column column = new Column("field", ParadoxType.INTEGER);
        assertEquals("field", column.getName());
        assertEquals(ParadoxType.INTEGER, column.getType());
    }

    /**
     * Test for name.
     */
    @Test
    void testName() {
        final Column column = new Column("name", ParadoxType.VARCHAR);
        assertEquals("name", column.getName());
    }

    /**
     * Test for nullable.
     */
    @Test
    void testNullable() {
        final Column column = new Column("NAME", ParadoxType.INTEGER);
        assertTrue(column.isNullable());
    }

    /**
     * Test for read only.
     */
    @Test
    void testReadOnly() {
        final Column column = new Column(new ParadoxField(ParadoxType.VARCHAR));
        assertFalse(column.isReadOnly());
    }

    /**
     * Test for scale.
     */
    @Test
    void testScale() {
        final Column column = new Column(new ParadoxField(ParadoxType.VARCHAR));
        assertEquals(0, column.getScale());
    }

    /**
     * Test for searchable.
     */
    @Test
    void testSearchable() {
        final Column column = new Column(new ParadoxField(ParadoxType.VARCHAR));
        assertTrue(column.isSearchable());

        final Column column2 = new Column("NAME", ParadoxType.NULL);
        assertFalse(column2.isSearchable());
    }

    /**
     * Test for signed.
     */
    @Test
    void testSigned() {
        final Column column = new Column("NAME", ParadoxType.NUMBER);
        assertTrue(column.isSigned());

        final Column column2 = new Column("NAME", ParadoxType.BLOB);
        assertFalse(column2.isSigned());
    }

    /**
     * Test for type.
     */
    @Test
    void testType() {
        final Column column = new Column(new ParadoxField(ParadoxType.VARCHAR));
        assertEquals(ParadoxType.VARCHAR, column.getType());
    }

    /**
     * Test for writable.
     */
    @Test
    void testWritable() {
        final Column column = new Column(new ParadoxField(ParadoxType.VARCHAR));
        assertTrue(column.isWritable());
    }
}
