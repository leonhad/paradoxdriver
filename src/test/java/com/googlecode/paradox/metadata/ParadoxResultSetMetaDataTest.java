/*
 * ParadoxResultSetMetaDataTest.java
 *
 * 06/30/2016
 * Copyright (C) 2016 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.metadata;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxFieldType;
import com.googlecode.paradox.results.TypeName;
import com.googlecode.paradox.utils.Utils;
import org.junit.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Unit test for {@link ParadoxResultSetMetaData} class.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.3
 */
public class ParadoxResultSetMetaDataTest {
    /**
     * The connection string used in this tests.
     */
    public static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/";

    /**
     * The database connection.
     */
    private Connection conn;

    /**
     * Register the database driver.
     *
     * @throws Exception
     *         in case of failures.
     */
    @BeforeClass
    public static void setUp() throws Exception {
        Class.forName(Driver.class.getName());
    }

    /**
     * Close the test connection.
     *
     * @throws Exception
     *         in case of failures.
     */
    @After
    public void closeConnection() throws Exception {
        if (conn != null) {
            conn.close();
        }
    }

    /**
     * Connect to the test database.
     *
     * @throws Exception
     *         in case of failures.
     */
    @Before
    public void connect() throws Exception {
        conn = DriverManager.getConnection(CONNECTION_STRING + "db");
    }

    /**
     * Test for instance.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testInstance() throws SQLException {
        ParadoxResultSetMetaData metaData = new ParadoxResultSetMetaData((ParadoxConnection) conn,
                Collections.<Column>emptyList());
        Assert.assertEquals("Testing for column size.", 0, metaData.getColumnCount());
    }

    /**
     * Test for invalid column with low value.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test(expected = SQLException.class)
    public void testInvalidColumnLowValue() throws SQLException {
        ParadoxResultSetMetaData metaData = new ParadoxResultSetMetaData((ParadoxConnection) conn,
                Collections.<Column>emptyList());
        metaData.getColumnName(0);
    }

    /**
     * Test for invalid column with hight value.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test(expected = SQLException.class)
    public void testInvalidColumnHightValue() throws SQLException {
        ParadoxResultSetMetaData metaData = new ParadoxResultSetMetaData((ParadoxConnection) conn,
                Collections.<Column>emptyList());
        metaData.getColumnName(5);
    }

    /**
     * Test for column metadata.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testColumn() throws SQLException {
        Column column = new Column();
        column.setName("name");
        column.setMaxSize(255);
        column.setType(ParadoxFieldType.INTEGER.getSQLType());
        column.setPrecision(0);
        column.setTableName("table");
        column.setAutoIncrement(false);
        column.setCurrency(false);
        column.setWritable(false);
        column.setNullable(false);
        column.setReadOnly(true);
        column.setSearchable(true);
        column.setSigned(true);
        column.setScale(2);
        List<Column> columns = Arrays.asList(column);
        ParadoxResultSetMetaData metaData = new ParadoxResultSetMetaData((ParadoxConnection) conn, columns);
        Assert.assertEquals("Testing for column size.", 1, metaData.getColumnCount());
        Assert.assertEquals("Testing for class name.", TypeName.INTEGER.getClassName(), metaData.getColumnClassName(1));
        Assert.assertEquals("Testing for catalog name.", "db", metaData.getCatalogName(1));
        Assert.assertEquals("Testing for schema name.", "APP", metaData.getSchemaName(1));
        Assert.assertEquals("Testing for column display size.", 255, metaData.getColumnDisplaySize(1));
        Assert.assertEquals("Testing for column label.", "name", metaData.getColumnLabel(1));
        Assert.assertEquals("Testing for column name.", "name", metaData.getColumnName(1));
        Assert.assertEquals("Testing for column type.", ParadoxFieldType.INTEGER.getSQLType(),
                metaData.getColumnType(1));
        Assert.assertEquals("Testing for column type name.", TypeName.INTEGER.getName(), metaData.getColumnTypeName(1));
        Assert.assertEquals("Testing for column precision.", 0, metaData.getPrecision(1));
        Assert.assertEquals("Testing for column scale.", 2, metaData.getScale(1));
        Assert.assertEquals("Testing for table name.", "table", metaData.getTableName(1));
        Assert.assertFalse("Testing for auto increment value.", metaData.isAutoIncrement(1));
        Assert.assertFalse("Testing for case sensitivity.", metaData.isCaseSensitive(1));
        Assert.assertFalse("Testing for currency.", metaData.isCurrency(1));
        Assert.assertFalse("Testing for writable.", metaData.isWritable(1));
        Assert.assertFalse("Testing for definitely writable.", metaData.isDefinitelyWritable(1));
        Assert.assertEquals("Testing for nullable.", ResultSetMetaData.columnNoNulls, metaData.isNullable(1));
        Assert.assertTrue("Testing for read only.", metaData.isReadOnly(1));
        Assert.assertTrue("Testing for searchable.", metaData.isSearchable(1));
        Assert.assertTrue("Testing for sign.", metaData.isSigned(1));
    }

    /**
     * Test for the {@link Utils#isWrapperFor(java.sql.Wrapper, Class)}.
     *
     * @throws Exception
     *         in case of failures.
     */
    @Test
    public void testIsWrapFor() throws Exception {
        ParadoxResultSetMetaData metaData = new ParadoxResultSetMetaData((ParadoxConnection) conn,
                Collections.<Column>emptyList());
        Assert.assertTrue(metaData.isWrapperFor(ParadoxResultSetMetaData.class));
    }

    /**
     * Test for unwrap.
     *
     * @throws Exception
     *         in case of failures.
     */
    @Test
    public void testUnwrap() throws Exception {
        ParadoxResultSetMetaData metaData = new ParadoxResultSetMetaData((ParadoxConnection) conn,
                Collections.<Column>emptyList());
        Assert.assertNotNull(metaData.unwrap(ParadoxResultSetMetaData.class));
    }
}
