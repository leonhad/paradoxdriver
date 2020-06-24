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
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxFieldType;
import com.googlecode.paradox.results.TypeName;
import com.googlecode.paradox.utils.Constants;
import com.googlecode.paradox.utils.Utils;
import org.junit.*;

import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;

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
    private static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/";

    /**
     * The database connection.
     */
    private ParadoxConnection conn;

    /**
     * Register the database driver.
     *
     * @throws Exception in case of failures.
     */
    @BeforeClass
    public static void setUp() throws Exception {
        Class.forName(Driver.class.getName());
    }

    /**
     * Close the test connection.
     *
     * @throws Exception in case of failures.
     */
    @After
    public void closeConnection() throws Exception {
        if (this.conn != null) {
            this.conn.close();
        }
    }

    /**
     * Connect to the test database.
     *
     * @throws Exception in case of failures.
     */
    @Before
    public void connect() throws Exception {
        this.conn = (ParadoxConnection) DriverManager.getConnection(
                ParadoxResultSetMetaDataTest.CONNECTION_STRING + "db");
    }

    /**
     * Test for column metadata.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testColumn() throws SQLException {
        final Column column = new Column(new ParadoxField(conn));
        column.getField().setType(ParadoxFieldType.INTEGER.getType());
        column.getField().setSize(255);
        column.setName("name");
        column.setPrecision(2);
        column.getField().setTable(new ParadoxTable(null, "table", conn));
        column.setAutoIncrement(false);
        column.setCurrency(false);
        column.setWritable(false);
        column.setNullable(false);
        column.setReadOnly(true);
        column.setSearchable(true);
        column.setSigned(true);
        final ParadoxResultSetMetaData metaData = new ParadoxResultSetMetaData(this.conn,
                Collections.singletonList(column));
        Assert.assertEquals("Testing for column size.", 1, metaData.getColumnCount());
        Assert.assertEquals("Testing for class name.", TypeName.INTEGER.getClassName(), metaData.getColumnClassName(1));
        Assert.assertEquals("Testing for catalog name.", "test-classes", metaData.getCatalogName(1));
        Assert.assertEquals("Testing for schema name.", "db", metaData.getSchemaName(1));
        Assert.assertEquals("Testing for column display size.", Constants.MAX_STRING_SIZE,
                metaData.getColumnDisplaySize(1));
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
        Assert.assertTrue("Testing for read only.", metaData.isReadOnly(1));
        Assert.assertTrue("Testing for searchable.", metaData.isSearchable(1));
        Assert.assertTrue("Testing for sign.", metaData.isSigned(1));

        Assert.assertEquals("Testing for nullable.", ResultSetMetaData.columnNoNulls, metaData.isNullable(1));
    }

    /**
     * Test for instance.
     */
    @Test
    public void testInstance() {
        final ParadoxResultSetMetaData metaData =
                new ParadoxResultSetMetaData(this.conn, Collections.emptyList());
        Assert.assertEquals("Testing for column size.", 0, metaData.getColumnCount());
    }

    /**
     * Test for invalid column with high value.
     *
     * @throws SQLException in case of errors.
     */
    @Test(expected = SQLException.class)
    public void testInvalidColumnHighValue() throws SQLException {
        final ParadoxResultSetMetaData metaData =
                new ParadoxResultSetMetaData(this.conn, Collections.emptyList());
        metaData.getColumnName(5);
    }

    /**
     * Test for invalid column with low value.
     *
     * @throws SQLException in case of errors.
     */
    @Test(expected = SQLException.class)
    public void testInvalidColumnLowValue() throws SQLException {
        final ParadoxResultSetMetaData metaData =
                new ParadoxResultSetMetaData(this.conn, Collections.emptyList());
        metaData.getColumnName(0);
    }

    /**
     * Test for the {@link Utils#isWrapperFor(java.sql.Wrapper, Class)}.
     */
    @Test
    public void testIsWrapFor() {
        final ParadoxResultSetMetaData metaData =
                new ParadoxResultSetMetaData(this.conn, Collections.emptyList());
        Assert.assertTrue("Invalid value.", metaData.isWrapperFor(ParadoxResultSetMetaData.class));
    }

    /**
     * Test for null column metadata.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testNullColumn() throws SQLException {
        final Column column = new Column(new ParadoxField(conn));
        column.setName("name");
        column.setNullable(true);
        final ParadoxResultSetMetaData metaData =
                new ParadoxResultSetMetaData(this.conn, Collections.singletonList(column));
        Assert.assertEquals("Testing for nullable.",
                ResultSetMetaData.columnNullable, metaData.isNullable(1));
    }

    /**
     * Test for unwrap.
     *
     * @throws Exception in case of failures.
     */
    @Test
    public void testUnwrap() throws Exception {
        final ParadoxResultSetMetaData metaData =
                new ParadoxResultSetMetaData(this.conn, Collections.emptyList());
        Assert.assertNotNull("Invalid value.", metaData.unwrap(ParadoxResultSetMetaData.class));
    }
}
