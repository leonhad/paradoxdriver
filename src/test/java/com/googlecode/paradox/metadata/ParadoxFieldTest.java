/*
 * ParadoxFieldTest.java
 *
 * 06/29/2009
 * Copyright (C) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.metadata;

import java.sql.SQLException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link ParadoxField} class.
 *
 * @author Leonardo Alves da Costa
 * @since 1.3
 * @version 1.0
 */
public class ParadoxFieldTest {
    
    /**
     * Test for {@link ParadoxField#equals(Object)} method.
     */
    @Test
    public void testEquals() {
        final ParadoxField first = new ParadoxField();
        first.setName("Field");
        final ParadoxField last = new ParadoxField();
        last.setName("Field");
        Assert.assertTrue(first.equals(last));
    }

    /**
     * Test for {@link ParadoxField#getSize()} method.
     */
    @Test
    public void testSizeClob() {
        final ParadoxField first = new ParadoxField();
        first.setName("Field");
        final ParadoxField last = new ParadoxField();
        last.setName("Field");
        Assert.assertTrue(first.equals(last));
    }
    
    /**
     * Test for {@link ParadoxField#toString()} method.
     *
     * @throws SQLException
     *             in case of errors.
     */
    @Test
    public void testToString() throws SQLException {
        final ParadoxField field = new ParadoxField();

        field.setType((byte) 0xC);
        field.setSize((short) 20);
        Assert.assertEquals(10, field.getSize());

        field.setType((byte) 0xD);
        field.setSize((short) 20);
        Assert.assertEquals(10, field.getSize());

        field.setType((byte) 0xF);
        field.setSize((short) 20);
        Assert.assertEquals(10, field.getSize());

        field.setType((byte) 0x18);
        field.setSize((short) 20);
        Assert.assertEquals(10, field.getSize());
    }

    /**
     * Test for {@link ParadoxField#getSize()} and
     * {@link ParadoxField#setSize(short)} method.
     *
     * @throws SQLException
     *             in case of errors.
     */
    @Test
    public void testSize() throws SQLException {
        final ParadoxField field = new ParadoxField();
        // Not CLOB or BLOB type
        field.setType((byte) 0x1);
        field.setSize((short) 10);
        Assert.assertEquals(10, field.getSize());
    }
    
    /**
     * Test for getters and setters.
     */
    @Test
    public void testGettersAndSetters() {
        final ParadoxField field = new ParadoxField();
        field.setAlias("alias");
        field.setChecked(false);
        field.setExpression("expression");
        field.setJoinName("joinname");
        field.setName("name");
        field.setTableName("tablename");
        field.setTable(null);

        Assert.assertEquals("alias", field.getAlias());
        Assert.assertEquals(false, field.isChecked());
        Assert.assertEquals("expression", field.getExpression());
        Assert.assertEquals("joinname", field.getJoinName());
        Assert.assertEquals("name", field.getName());
        Assert.assertEquals("tablename", field.getTableName());
        Assert.assertEquals(null, field.getTable());
    }
    
    /**
     * Test for {@link ParadoxField#hashCode()} method.
     */
    @Test
    public void testHashCode() {
        final ParadoxField field = new ParadoxField();
        field.setName("Field");
        Assert.assertEquals(67875153, field.hashCode());
    }

    /**
     * Test for default order.
     */
    @Test
    public void testDefaultOrder() {
        final ParadoxField field = new ParadoxField();
        Assert.assertEquals(1, field.getOrderNum());
    }
    
    /**
     * Test for not auto increment.
     */
    @Test
    public void testNotAutoIncrement() {
        final ParadoxField first = new ParadoxField(1);
        Assert.assertFalse(first.isAutoIncrement());
    }

    /**
     * Test for auto increment.
     */
    @Test
    public void testAutoIncrement() {
        final ParadoxField first = new ParadoxField(0x16);
        Assert.assertFalse(first.isAutoIncrement());
    }
}
