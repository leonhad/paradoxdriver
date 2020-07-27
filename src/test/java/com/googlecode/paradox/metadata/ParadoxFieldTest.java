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
package com.googlecode.paradox.metadata;

import com.googlecode.paradox.results.ParadoxType;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link ParadoxField} class.
 *
 * @version 1.4
 * @since 1.3
 */
public class ParadoxFieldTest {

    /**
     * Test for auto increment.
     */
    @Test
    public void testAutoIncrement() {
        final ParadoxField field = new ParadoxField(ParadoxType.AUTO_INCREMENT);
        Assert.assertTrue("Invalid autoincrement type.", field.isAutoIncrement());
    }

    /**
     * Test for default order.
     */
    @Test
    public void testDefaultOrder() {
        final ParadoxField field = new ParadoxField(ParadoxType.VARCHAR);
        Assert.assertEquals("Invalid field order.", 1, field.getOrderNum());
    }

    /**
     * Test for empty alias.
     */
    @Test
    public void testEmptyAlias() {
        final ParadoxField field = new ParadoxField(ParadoxType.VARCHAR);
        field.setName("Field");
        Assert.assertEquals("Invalid field alias.", "Field", field.getAlias());
    }

    /**
     * Test for {@link ParadoxField#equals(Object)} method.
     */
    @Test
    public void testEquals() {
        final ParadoxField first = new ParadoxField(ParadoxType.VARCHAR);
        first.setName("Field");
        final ParadoxField last = new ParadoxField(ParadoxType.VARCHAR);
        last.setName("Field");
        Assert.assertEquals("Invalid equals result.", last, first);
    }

    /**
     * Test for getters and setters.
     */
    @Test
    public void testGettersAndSetters() {
        final ParadoxField field = new ParadoxField(ParadoxType.VARCHAR);
        field.setAlias("alias");
        field.setChecked(false);
        field.setExpression("expression");
        field.setJoinName("joinName");
        field.setName("name");
        field.setTable(null);

        Assert.assertEquals("Invalid field alias.", "alias", field.getAlias());
        Assert.assertFalse("Field is not checked.", field.isChecked());
        Assert.assertEquals("Invalid field expression.", "expression", field.getExpression());
        Assert.assertEquals("Invalid field join name.", "joinName", field.getJoinName());
        Assert.assertEquals("Invalid field name.", "name", field.getName());
        Assert.assertNull("Invalid table", field.getTable());
    }

    /**
     * Test for {@link ParadoxField#hashCode()} method.
     */
    @Test
    public void testHashCode() {
        final ParadoxField field = new ParadoxField(ParadoxType.VARCHAR);
        field.setName("Field");
        Assert.assertNotEquals("Invalid hash code.", 0, field.hashCode());
    }

    /**
     * Test for not auto increment.
     */
    @Test
    public void testNotAutoIncrement() {
        final ParadoxField field = new ParadoxField(ParadoxType.VARCHAR);
        Assert.assertFalse("Invalid autoincrement type.", field.isAutoIncrement());
    }

    /**
     * Test for {@link ParadoxField#equals(Object)} method different values.
     */
    @Test
    public void testNotEquals() {
        final ParadoxField first = new ParadoxField(ParadoxType.VARCHAR);
        first.setName("Field");
        final ParadoxField last = new ParadoxField(ParadoxType.VARCHAR);
        last.setName("Field 2");
        Assert.assertNotEquals("Invalid field.", first, last);

        first.setName(null);
        last.setName("Field 2");
        Assert.assertNotEquals("Invalid field.", first, last);

        first.setName("Field");
        last.setName(null);
        Assert.assertNotEquals("Invalid field.", first, last);

        first.setName(null);
        last.setName(null);
        Assert.assertEquals("Invalid field.", first, last);
    }

    /**
     * Test for {@link ParadoxField#getSize()} and {@link ParadoxField#setSize(int)}
     * method.
     */
    @Test
    public void testSize() {
        // Not CLOB or BLOB type
        final ParadoxField field = new ParadoxField(ParadoxType.VARCHAR);
        field.setSize(10);
        Assert.assertEquals("Field size invalid.", 10, field.getSize());
    }

    /**
     * Test for {@link ParadoxField#toString()} method.
     */
    @Test
    public void testToString() {
        final ParadoxField first = new ParadoxField(ParadoxType.VARCHAR);
        first.setName("Field");
        Assert.assertEquals("Invalid field name.", "Field", first.toString());
    }
}
