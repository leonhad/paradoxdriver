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
import com.googlecode.paradox.metadata.ParadoxTable;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Unit test for {@link Column} class.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
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
     */
    @Test
    public void testAutoincrement() {
        final Column column = new Column(new ParadoxField(conn));
        column.setAutoIncrement(true);
        Assert.assertTrue("Invalid field type.", column.isAutoIncrement());
    }

    /**
     * Test for auto increment type.
     */
    @Test
    public void testAutoIncrementType() {
        final Column column = new Column(new ParadoxField(conn));
        column.setType(ParadoxFieldType.AUTO_INCREMENT.getType());
        Assert.assertEquals("Invalid field value.", 9d, column.getPrecision(), 0);
        Assert.assertTrue("Invalid field type.", column.isAutoIncrement());
    }

    /**
     * Test for currency.
     */
    @Test
    public void testCurrency() {
        final Column column = new Column(new ParadoxField(conn));
        column.setCurrency(true);
        Assert.assertTrue("Invalid field type.", column.isCurrency());
    }

    /**
     * Test for double type.
     */
    @Test
    public void testDoubleType() {
        final Column column = new Column(new ParadoxField(conn));
        column.setType(ParadoxFieldType.CURRENCY.getType());
        Assert.assertEquals("Invalid field value.", 9d, column.getPrecision(), 0);
        Assert.assertTrue("Invalid field type.", column.isCurrency());
    }

    /**
     * Test field.
     */
    @Test
    public void testField() {
        final ParadoxField field = new ParadoxField(conn);
        field.setType(ParadoxFieldType.INTEGER.getType());
        field.setName("field");
        final Column column = new Column(new ParadoxField(conn));
        column.setField(field);
        Assert.assertEquals("Invalid field.", field, column.getField());
    }

    /**
     * Test for index.
     */
    @Test
    public void testIndex() {
        final Column column = new Column(new ParadoxField(conn));
        column.setIndex(1);
        Assert.assertEquals("Invalid field type.", 1, column.getIndex());
    }

    /**
     * Test instance with field.
     */
    @Test
    public void testInstanceWithFields() {
        final ParadoxField field = new ParadoxField(conn);
        field.setType(ParadoxFieldType.INTEGER.getType());
        field.setName("field");
        final Column column = new Column(field);
        Assert.assertEquals("Invalid field.", field, column.getField());
        Assert.assertEquals("Invalid field name.", "field", column.getName());
        Assert.assertEquals("Invalid field type.", ParadoxFieldType.INTEGER.getType(), column.getType());
    }

    /**
     * Test instance with values.
     */
    @Test
    public void testInstanceWithValues() {
        final Column column = new Column("field", ParadoxFieldType.INTEGER.getType());
        Assert.assertEquals("Invalid field name.", "field", column.getName());
        Assert.assertEquals("Invalid field type.", ParadoxFieldType.INTEGER.getType(), column.getType());
    }

    /**
     * Test for name.
     */
    @Test
    public void testName() {
        final Column column = new Column(new ParadoxField(conn));
        column.setName("name");
        Assert.assertEquals("Invalid field name.", "name", column.getName());
    }

    /**
     * Test for nullable.
     */
    @Test
    public void testNullable() {
        final Column column = new Column(new ParadoxField(conn));
        column.setNullable(true);
        Assert.assertTrue("Invalid field nullable.", column.isNullable());
    }

    /**
     * Test for numeric type.
     */
    @Test
    public void testNumericType() {
        final Column column = new Column(new ParadoxField(conn));
        column.setType(ParadoxFieldType.NUMBER.getType());
        Assert.assertEquals("Invalid field value.", 2d, column.getScale(), 0);
    }

    /**
     * Test for precision.
     */
    @Test
    public void testPrecision() {
        final Column column = new Column(new ParadoxField(conn));
        column.setPrecision(1);
        Assert.assertEquals("Invalid field precision.", 1, column.getPrecision());
    }

    /**
     * Test for read only.
     */
    @Test
    public void testReadOnly() {
        final Column column = new Column(new ParadoxField(conn));
        column.setReadOnly(true);
        Assert.assertTrue("Invalid field readonly.", column.isReadOnly());
    }

    /**
     * Test for scale.
     */
    @Test
    public void testScale() {
        final Column column = new Column(new ParadoxField(conn));
        column.setScale(1);
        Assert.assertEquals("Invalid field scale.", 1, column.getScale());
    }

    /**
     * Test for searchable.
     */
    @Test
    public void testSearchable() {
        final Column column = new Column(new ParadoxField(conn));
        column.setSearchable(true);
        Assert.assertTrue("Invalid field searchable.", column.isSearchable());
    }

    /**
     * Test for signed.
     */
    @Test
    public void testSigned() {
        final Column column = new Column(new ParadoxField(conn));
        column.setSigned(true);
        Assert.assertTrue("Invalid field signed.", column.isSigned());
    }

    /**
     * Test for table name.
     */
    @Test
    public void testTableName() {
        final Column column = new Column(new ParadoxField(conn));
        column.setTable(new ParadoxTable(null, "name", conn));
        Assert.assertEquals("Invalid field table name.", "name", column.getTableName());
    }

    /**
     * Test for type.
     */
    @Test
    public void testType() {
        final Column column = new Column(new ParadoxField(conn));
        column.setType(1);
        Assert.assertEquals("Invalid field type.", 1, column.getType());
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
    public void testWritable() {
        final Column column = new Column(new ParadoxField(conn));
        column.setWritable(true);
        Assert.assertTrue("Invalid field writable.", column.isWritable());
    }
}
