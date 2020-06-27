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

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.results.ParadoxFieldType;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Unit test for {@link ParadoxField} class.
 *
 * @author Leonardo Costa
 * @version 1.1
 * @since 1.3
 */
public class ParadoxFieldTest {
    /**
     * The connection string used in this tests.
     */
    public static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/";

    private static ParadoxConnection conn;

    /**
     * Register the database driver.
     *
     * @throws SQLException in case of failures.
     */
    @BeforeClass
    public static void setUp() throws SQLException {
        new Driver();
        conn = (ParadoxConnection) DriverManager.getConnection(CONNECTION_STRING + "date");
    }

    @AfterClass
    public static void tearDown() throws SQLException {
        conn.close();
    }

    /**
     * Test for auto increment.
     */
    @Test
    public void testAutoIncrement() {
        final ParadoxField field = new ParadoxField(conn, ParadoxFieldType.AUTO_INCREMENT.getType());
        Assert.assertTrue("Invalid autoincrement type.", field.isAutoIncrement());
    }

    /**
     * Test for default order.
     */
    @Test
    public void testDefaultOrder() {
        final ParadoxField field = new ParadoxField(conn, ParadoxFieldType.VARCHAR.getType());
        Assert.assertEquals("Invalid field order.", 1, field.getOrderNum());
    }

    /**
     * Test for empty alias.
     */
    @Test
    public void testEmptyAlias() {
        final ParadoxField field = new ParadoxField(conn, ParadoxFieldType.VARCHAR.getType());
        field.setName("Field");
        Assert.assertEquals("Invalid field alias.", "Field", field.getAlias());
    }

    /**
     * Test for {@link ParadoxField#equals(Object)} method.
     */
    @Test
    public void testEquals() {
        final ParadoxField first = new ParadoxField(conn, ParadoxFieldType.VARCHAR.getType());
        first.setName("Field");
        final ParadoxField last = new ParadoxField(conn, ParadoxFieldType.VARCHAR.getType());
        last.setName("Field");
        Assert.assertEquals("Invalid equals result.", last, first);
    }

    /**
     * Test for {@link ParadoxField#equals(Object)} method with a different
     * class.
     */
    @Test
    public void testEqualsDifferentClass() {
        final ParadoxField first = new ParadoxField(conn, ParadoxFieldType.VARCHAR.getType());
        first.setName("Field");
        Assert.assertNotEquals("Invalid equals result.", "String", first);
    }

    /**
     * Test for {@link ParadoxField#equals(Object)} method with null value.
     */
    @Test
    public void testEqualsNull() {
        final ParadoxField field = new ParadoxField(conn, ParadoxFieldType.VARCHAR.getType());
        field.setName("Field");
        Assert.assertNotEquals("Invalid equals result.", null, field);
    }

    /**
     * Test for getters and setters.
     */
    @Test
    public void testGettersAndSetters() {
        final ParadoxField field = new ParadoxField(conn, ParadoxFieldType.VARCHAR.getType());
        field.setAlias("alias");
        field.setChecked(false);
        field.setExpression("expression");
        field.setJoinName("joinName");
        field.setName("name");
        field.setTableName("tableName");
        field.setTable(null);

        Assert.assertEquals("Invalid field alias.", "alias", field.getAlias());
        Assert.assertFalse("Field is not checked.", field.isChecked());
        Assert.assertEquals("Invalid field expression.", "expression", field.getExpression());
        Assert.assertEquals("Invalid field join name.", "joinName", field.getJoinName());
        Assert.assertEquals("Invalid field name.", "name", field.getName());
        Assert.assertEquals("Invalid field table name.", "tableName", field.getTableName());
        Assert.assertNull("Invalid table", field.getTable());
    }

    /**
     * Test for {@link ParadoxField#hashCode()} method.
     */
    @Test
    public void testHashCode() {
        final ParadoxField field = new ParadoxField(conn, ParadoxFieldType.VARCHAR.getType());
        field.setName("Field");
        Assert.assertNotEquals("Invalid hash code.", 0, field.hashCode());
    }

    /**
     * Test for not auto increment.
     */
    @Test
    public void testNotAutoIncrement() {
        final ParadoxField field = new ParadoxField(conn, (byte) 1);
        Assert.assertFalse("Invalido autoincrement type.", field.isAutoIncrement());
    }

    /**
     * Test for {@link ParadoxField#equals(Object)} method different values.
     */
    @Test
    public void testNotEquals() {
        final ParadoxField first = new ParadoxField(conn, ParadoxFieldType.VARCHAR.getType());
        first.setName("Field");
        final ParadoxField last = new ParadoxField(conn, ParadoxFieldType.VARCHAR.getType());
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
     * Test for {@link ParadoxField#getSize()} and {@link ParadoxField#setSize(int)} method.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testSize() throws SQLException {
        // Not CLOB or BLOB type
        final ParadoxField field = new ParadoxField(conn, (byte) 0x1);
        field.setSize(10);
        Assert.assertEquals("Field size invalid.", 10, field.getSize());
    }

    /**
     * Test for {@link ParadoxField#toString()} method.
     */
    @Test
    public void testToString() {
        final ParadoxField first = new ParadoxField(conn, ParadoxFieldType.VARCHAR.getType());
        first.setName("Field");
        Assert.assertEquals("Invalid field name.", "Field", first.toString());
    }
}
