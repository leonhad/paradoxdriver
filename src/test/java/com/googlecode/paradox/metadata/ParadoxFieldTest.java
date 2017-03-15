/*
 * ParadoxFieldTest.java 06/29/2009 Copyright (C) 2009 Leonardo Alves da Costa This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.metadata;

import java.sql.SQLException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link ParadoxField} class.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.3
 */
public class ParadoxFieldTest {
    
    /**
     * Test for auto increment.
     */
    @Test
    public void testAutoIncrement() {
        final ParadoxField field = new ParadoxField();
        field.setType((byte) 0x16);
        Assert.assertTrue(field.isAutoIncrement());
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
     * Test for empty alias.
     */
    @Test
    public void testEmptyAlias() {
        final ParadoxField field = new ParadoxField();
        field.setName("Field");
        Assert.assertEquals("Field", field.getAlias());
    }
    
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
     * Test for {@link ParadoxField#equals(Object)} method with a different
     * class.
     */
    @Test
    public void testEqualsDifferentClass() {
        final ParadoxField first = new ParadoxField();
        first.setName("Field");
        Assert.assertFalse(first.equals("String"));
    }
    
    /**
     * Test for {@link ParadoxField#equals(Object)} method with null value.
     */
    @Test
    public void testEqualsNull() {
        final ParadoxField field = new ParadoxField();
        field.setName("Field");
        Assert.assertFalse(field.equals(null));
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
        field.setJoinName("joinName");
        field.setName("name");
        field.setTableName("tableName");
        field.setTable(null);
        
        Assert.assertEquals("alias", field.getAlias());
        Assert.assertFalse("Field is not checked.", field.isChecked());
        Assert.assertEquals("expression", field.getExpression());
        Assert.assertEquals("joinName", field.getJoinName());
        Assert.assertEquals("name", field.getName());
        Assert.assertEquals("tableName", field.getTableName());
        Assert.assertEquals(null, field.getTable());
    }
    
    /**
     * Test for {@link ParadoxField#hashCode()} method.
     */
    @Test
    public void testHashCode() {
        final ParadoxField field = new ParadoxField();
        field.setName("Field");
        Assert.assertEquals((7 * 17) + "Field".hashCode(), field.hashCode());
    }
    
    /**
     * Test for {@link ParadoxField#hashCode()} method variant.
     */
    @Test
    public void testHashCodeVariant() {
        final ParadoxField field = new ParadoxField();
        field.setName(null);
        Assert.assertEquals(7 * 17, field.hashCode());
    }
    
    /**
     * Test for not auto increment.
     */
    @Test
    public void testNotAutoIncrement() {
        final ParadoxField field = new ParadoxField();
        field.setType((byte) 1);
        Assert.assertFalse(field.isAutoIncrement());
    }
    
    /**
     * Test for {@link ParadoxField#equals(Object)} method different values.
     */
    @Test
    public void testNotEquals() {
        final ParadoxField first = new ParadoxField();
        first.setName("Field");
        final ParadoxField last = new ParadoxField();
        last.setName("Field 2");
        Assert.assertFalse(first.equals(last));
        
        first.setName(null);
        last.setName("Field 2");
        Assert.assertFalse(first.equals(last));
        
        first.setName("Field");
        last.setName(null);
        Assert.assertFalse(first.equals(last));
        
        first.setName(null);
        last.setName(null);
        Assert.assertTrue(first.equals(last));
    }
    
    /**
     * Test for {@link ParadoxField#getSize()} and
     * {@link ParadoxField#setSize(int)} method.
     *
     * @throws SQLException
     *             in case of errors.
     */
    @Test
    public void testSize() throws SQLException {
        final ParadoxField field = new ParadoxField();
        // Not CLOB or BLOB type
        field.setType((byte) 0x1);
        field.setSize(10);
        Assert.assertEquals("Field size invalid.", 10, field.getSize());
    }
    
    /**
     * Test for {@link ParadoxField#toString()} method.
     *
     * @throws SQLException
     *             in case of errors.
     */
    @Test
    public void testSizeClob() throws SQLException {
        final ParadoxField field = new ParadoxField();
        
        field.setType((byte) 0xC);
        field.setSize(20);
        Assert.assertEquals("Field size invalid.", 10, field.getSize());
        
        field.setType((byte) 0xD);
        field.setSize(20);
        Assert.assertEquals("Field size invalid.", 10, field.getSize());
        
        field.setType((byte) 0xF);
        field.setSize(20);
        Assert.assertEquals("Field size invalid.", 10, field.getSize());
        
        field.setType((byte) 0x18);
        field.setSize(20);
        Assert.assertEquals("Field size invalid.", 10, field.getSize());
        
        // Not changed by type
        field.setType((byte) 0xF);
        field.setSize(20);
        Assert.assertEquals("Physical field size invalid.", 20, field.getPhysicsSize());
        
        field.setType((byte) 0x1);
        field.setSize(20);
        Assert.assertEquals("Physical field size invalid.", 20, field.getPhysicsSize());
    }
    
    /**
     * Test for {@link ParadoxField#toString()} method.
     */
    @Test
    public void testToString() {
        final ParadoxField first = new ParadoxField();
        first.setName("Field");
        Assert.assertEquals("Field", first.toString());
    }
}
