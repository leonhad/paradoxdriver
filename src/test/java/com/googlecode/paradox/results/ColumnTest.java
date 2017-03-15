/*
 * ColumnTest.java 07/11/2016 Copyright (C) 2016 Leonardo Alves da Costa This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.results;

import com.googlecode.paradox.metadata.ParadoxField;
import java.sql.SQLException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link Column} class.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.3
 */
public class ColumnTest {

    /**
     * Test for auto increment.
     */
    @Test
    public void testAutoincrement() {
        final Column column = new Column();
        column.setAutoIncrement(true);
        Assert.assertTrue(column.isAutoIncrement());
    }

    /**
     * Test for auto increment type.
     */
    @Test
    public void testAutoIncrementType() {
        final Column column = new Column();
        column.setType(ParadoxFieldType.AUTO_INCREMENT.getType());
        Assert.assertEquals(9d, column.getPrecision(), 0);
        Assert.assertTrue(column.isAutoIncrement());
    }

    /**
     * Test for currency.
     */
    @Test
    public void testCurrency() {
        final Column column = new Column();
        column.setCurrency(true);
        Assert.assertTrue(column.isCurrency());
    }

    /**
     * Test for double type.
     */
    @Test
    public void testDoubleType() {
        final Column column = new Column();
        column.setType(ParadoxFieldType.DOUBLE.getType());
        Assert.assertEquals(9d, column.getPrecision(), 0);
        Assert.assertTrue(column.isCurrency());
    }

    /**
     * Test field.
     */
    @Test
    public void testField() {
        final ParadoxField field = new ParadoxField();
        field.setType(ParadoxFieldType.INTEGER.getType());
        field.setName("field");
        final Column column = new Column();
        column.setField(field);
        Assert.assertEquals(field, column.getField());
    }

    /**
     * Test for index.
     */
    @Test
    public void testIndex() {
        final Column column = new Column();
        column.setIndex(1);
        Assert.assertEquals(1, column.getIndex());
    }

    /**
     * Test instance with field.
     */
    @Test
    public void testInstanceWithFields() {
        final ParadoxField field = new ParadoxField();
        field.setType(ParadoxFieldType.INTEGER.getType());
        field.setName("field");
        final Column column = new Column(field);
        Assert.assertEquals(field, column.getField());
        Assert.assertEquals("field", column.getName());
        Assert.assertEquals(ParadoxFieldType.INTEGER.getType(), column.getType());
    }

    /**
     * Test instance with values.
     */
    @Test
    public void testInstanceWithValues() {
        final Column column = new Column("field", ParadoxFieldType.INTEGER.getType());
        Assert.assertEquals("field", column.getName());
        Assert.assertEquals(ParadoxFieldType.INTEGER.getType(), column.getType());
    }

    /**
     * Test for max size.
     */
    @Test
    public void testMaxSize() {
        final Column column = new Column();
        column.setMaxSize(1);
        Assert.assertEquals(1, column.getMaxSize());
    }

    /**
     * Test for name.
     */
    @Test
    public void testName() {
        final Column column = new Column();
        column.setName("name");
        Assert.assertEquals("name", column.getName());
    }

    /**
     * Test for nullable.
     */
    @Test
    public void testNullable() {
        final Column column = new Column();
        column.setNullable(true);
        Assert.assertTrue(column.isNullable());
    }

    /**
     * Test for numeric type.
     */
    @Test
    public void testNumericType() {
        final Column column = new Column();
        column.setType(ParadoxFieldType.NUMERIC.getType());
        Assert.assertEquals(2d, column.getScale(), 0);
    }

    /**
     * Test for precision.
     */
    @Test
    public void testPrecision() {
        final Column column = new Column();
        column.setPrecision(1);
        Assert.assertEquals(1, column.getPrecision());
    }

    /**
     * Test for read only.
     */
    @Test
    public void testReadOnly() {
        final Column column = new Column();
        column.setReadOnly(true);
        Assert.assertTrue(column.isReadOnly());
    }

    /**
     * Test for scale.
     */
    @Test
    public void testScale() {
        final Column column = new Column();
        column.setScale(1);
        Assert.assertEquals(1, column.getScale());
    }

    /**
     * Test for searchable.
     */
    @Test
    public void testSearchable() {
        final Column column = new Column();
        column.setSearchable(true);
        Assert.assertTrue(column.isSearchable());
    }

    /**
     * Test for signed.
     */
    @Test
    public void testSigned() {
        final Column column = new Column();
        column.setSigned(true);
        Assert.assertTrue(column.isSigned());
    }

    /**
     * Test for table name.
     */
    @Test
    public void testTableName() {
        final Column column = new Column();
        column.setTableName("name");
        Assert.assertEquals("name", column.getTableName());
    }

    /**
     * Test for type.
     */
    @Test
    public void testType() {
        final Column column = new Column();
        column.setType(1);
        Assert.assertEquals(1, column.getType());
    }

    /**
     * Test for type name.
     *
     * @throws SQLException
     *             in case of errors.
     */
    @Test
    public void testTypeName() throws SQLException {
        Assert.assertEquals(TypeName.BOOLEAN.getName(), Column.getTypeName(TypeName.BOOLEAN.getSQLType()));
    }

    /**
     * Test for writable.
     */
    @Test
    public void testWritable() {
        final Column column = new Column();
        column.setWritable(true);
        Assert.assertTrue(column.isWritable());
    }
}
