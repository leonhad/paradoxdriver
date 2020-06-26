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
package com.googlecode.paradox.results;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.metadata.ParadoxField;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Unit test for {@link Column} class.
 *
 * @author Leonardo Costa
 * @version 1.1
 * @since 1.3
 */
public class ColumnTest {

    /**
     * The connection string used in this tests.
     */
    public static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/db";

    private static ParadoxConnection conn;

    /**
     * Register the database driver.
     *
     * @throws SQLException in case of failures.
     */
    @BeforeClass
    public static void setUp() throws SQLException {
        new Driver();
        conn = (ParadoxConnection) DriverManager.getConnection(CONNECTION_STRING);
    }

    @AfterClass
    public static void tearDown() throws SQLException {
        conn.close();
    }

    /**
     * Test for auto increment.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testAutoincrement() throws SQLException {
        final Column column = new Column(new ParadoxField(conn, ParadoxFieldType.AUTO_INCREMENT.getType()));
        Assert.assertTrue("Invalid field type.", column.isAutoIncrement());

        final Column column2 = new Column("NAME", ParadoxFieldType.INTEGER.getSQLType());
        Assert.assertFalse("Invalid field type.", column2.isAutoIncrement());
    }

    /**
     * Test for currency.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testCurrency() throws SQLException {
        final Column column = new Column(new ParadoxField(conn, ParadoxFieldType.CURRENCY.getType()));
        Assert.assertTrue("Invalid field type.", column.isCurrency());

        final Column column2 = new Column("NAME", ParadoxFieldType.INTEGER.getSQLType());
        Assert.assertFalse("Invalid field type.", column2.isCurrency());
    }

    /**
     * Test field.
     */
    @Test
    public void testField() throws SQLException {
        final ParadoxField field = new ParadoxField(conn, ParadoxFieldType.INTEGER.getType());
        final Column column = new Column(field);
        Assert.assertEquals("Invalid field.", field, column.getField());
    }

    /**
     * Test for index.
     */
    @Test
    public void testIndex() throws SQLException {
        final Column column = new Column(new ParadoxField(conn, ParadoxFieldType.VARCHAR.getType()));
        column.setIndex(1);
        Assert.assertEquals("Invalid field type.", 1, column.getIndex());
    }

    /**
     * Test instance with field.
     */
    @Test
    public void testInstanceWithFields() throws SQLException {
        final ParadoxField field = new ParadoxField(conn, ParadoxFieldType.INTEGER.getType());
        field.setName("field");
        final Column column = new Column(field);
        Assert.assertEquals("Invalid field.", field, column.getField());
        Assert.assertEquals("Invalid field name.", "field", column.getName());
        Assert.assertEquals("Invalid field type.", ParadoxFieldType.INTEGER.getSQLType(), column.getType());
    }

    /**
     * Test instance with values.
     */
    @Test
    public void testInstanceWithValues() {
        final Column column = new Column("field", ParadoxFieldType.INTEGER.getSQLType());
        Assert.assertEquals("Invalid field name.", "field", column.getName());
        Assert.assertEquals("Invalid field type.", ParadoxFieldType.INTEGER.getSQLType(), column.getType());
    }

    /**
     * Test for name.
     */
    @Test
    public void testName() {
        final Column column = new Column("name", ParadoxFieldType.VARCHAR.getType());
        Assert.assertEquals("Invalid field name.", "name", column.getName());
    }

    /**
     * Test for nullable.
     *
     * @throws SQLException in case of failues.
     */
    @Test
    public void testNullable() throws SQLException {
        final Column column = new Column("NAME", ParadoxFieldType.INTEGER.getSQLType());
        Assert.assertTrue("Invalid field nullable.", column.isNullable());

        final Column column2 = new Column(new ParadoxField(conn, ParadoxFieldType.AUTO_INCREMENT.getType()));
        Assert.assertFalse("Invalid field nullable.", column2.isNullable());
    }

    /**
     * Test for read only.
     */
    @Test
    public void testReadOnly() throws SQLException {
        final Column column = new Column(new ParadoxField(conn, ParadoxFieldType.VARCHAR.getType()));
        Assert.assertTrue("Invalid field readonly.", column.isReadOnly());
    }

    /**
     * Test for scale.
     */
    @Test
    public void testScale() throws SQLException {
        final Column column = new Column(new ParadoxField(conn, ParadoxFieldType.VARCHAR.getType()));
        column.setPrecision(1);
        Assert.assertEquals("Invalid field scale.", 1, column.getScale());
    }

    /**
     * Test for searchable.
     */
    @Test
    public void testSearchable() throws SQLException {
        final Column column = new Column(new ParadoxField(conn, ParadoxFieldType.VARCHAR.getType()));
        Assert.assertTrue("Invalid field searchable.", column.isSearchable());

        final Column column2 = new Column("NAME", ParadoxFieldType.BLOB.getSQLType());
        Assert.assertFalse("Invalid field searchable.", column2.isSearchable());

        final Column column3 = new Column("NAME", ParadoxFieldType.GRAPHIC.getSQLType());
        Assert.assertFalse("Invalid field searchable.", column3.isSearchable());
    }

    /**
     * Test for signed.
     */
    @Test
    public void testSigned() {
        final Column column = new Column("NAME", ParadoxFieldType.NUMBER.getSQLType());
        Assert.assertTrue("Invalid field signed.", column.isSigned());

        final Column column2 = new Column("NAME", ParadoxFieldType.BLOB.getSQLType());
        Assert.assertFalse("Invalid field signed.", column2.isSigned());
    }

    /**
     * Test for table name.
     */
    @Test
    public void testTableName() throws SQLException {
        final Column column = new Column(new ParadoxField(conn, ParadoxFieldType.VARCHAR.getType()));
        column.setTableName("name");
        Assert.assertEquals("Invalid field table name.", "name", column.getTableName());
    }

    /**
     * Test for type.
     */
    @Test
    public void testType() throws SQLException {
        final Column column = new Column(new ParadoxField(conn, ParadoxFieldType.VARCHAR.getType()));
        Assert.assertEquals("Invalid field type.", ParadoxFieldType.VARCHAR.getSQLType(), column.getType());
    }

    /**
     * Test for type name.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testTypeName() throws SQLException {
        Assert.assertEquals("Invalid field type name.", TypeName.BOOLEAN.getName(),
                Column.getTypeName(TypeName.BOOLEAN.getSQLType()));
    }

    /**
     * Test for writable.
     */
    @Test
    public void testWritable() throws SQLException {
        final Column column = new Column(new ParadoxField(conn, ParadoxFieldType.VARCHAR.getType()));
        Assert.assertFalse("Invalid field writable.", column.isWritable());
    }
}
