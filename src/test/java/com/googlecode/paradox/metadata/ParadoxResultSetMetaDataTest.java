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
import com.googlecode.paradox.utils.Utils;
import org.junit.*;

import java.sql.*;
import java.util.Collections;

/**
 * Unit test for {@link ParadoxResultSetMetaData} class.
 *
 * @version 1.1
 * @since 1.3
 */
public class ParadoxResultSetMetaDataTest {

    /**
     * The connection string used by tests.
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
        this.conn = (ParadoxConnection) DriverManager.getConnection(ParadoxResultSetMetaDataTest.CONNECTION_STRING + "db");
    }

    /**
     * Test for instance.
     */
    @Test
    public void testInstance() {
        final ParadoxResultSetMetaData metaData = new ParadoxResultSetMetaData(this.conn.getConnectionInfo(), Collections.emptyList());
        Assert.assertEquals("Testing for column size.", 0, metaData.getColumnCount());
    }

    /**
     * Test for invalid column with high value.
     *
     * @throws SQLException in case of errors.
     */
    @Test(expected = SQLException.class)
    public void testInvalidColumnHighValue() throws SQLException {
        final ParadoxResultSetMetaData metaData = new ParadoxResultSetMetaData(this.conn.getConnectionInfo(), Collections.emptyList());
        metaData.getColumnName(5);
    }

    /**
     * Test for invalid column with low value.
     *
     * @throws SQLException in case of errors.
     */
    @Test(expected = SQLException.class)
    public void testInvalidColumnLowValue() throws SQLException {
        final ParadoxResultSetMetaData metaData = new ParadoxResultSetMetaData(this.conn.getConnectionInfo(), Collections.emptyList());
        metaData.getColumnName(0);
    }

    /**
     * Test for the {@link Utils#isWrapperFor(java.sql.Wrapper, Class)}.
     */
    @Test
    public void testIsWrapFor() {
        final ParadoxResultSetMetaData metaData = new ParadoxResultSetMetaData(this.conn.getConnectionInfo(), Collections.emptyList());
        Assert.assertTrue("Invalid value.", metaData.isWrapperFor(ParadoxResultSetMetaData.class));
    }

    /**
     * Test for unwrap.
     *
     * @throws Exception in case of failures.
     */
    @Test
    public void testUnwrap() throws Exception {
        final ParadoxResultSetMetaData metaData = new ParadoxResultSetMetaData(this.conn.getConnectionInfo(), Collections.emptyList());
        Assert.assertNotNull("Invalid value.", metaData.unwrap(ParadoxResultSetMetaData.class));
    }

    /**
     * Test for the precision and scale for decimals.
     *
     * @throws Exception in case of failures.
     */
    @Test
    public void testPrecisionScaleDecimal() throws Exception {
        try (Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery("SELECT * FROM db.DECIMAL")) {
            if (rs.next()) {
                ResultSetMetaData metaData = rs.getMetaData();
                Assert.assertEquals("Invalid precision.", 15, metaData.getPrecision(1));
                Assert.assertEquals("Invalid scale.", 6, metaData.getScale(1));
            } else {
                Assert.fail("No catalog selected.");
            }
        }
    }

    /**
     * Test for the precision and scale for BCD.
     *
     * @throws Exception in case of failures.
     */
    @Test
    public void testPrecisionScaleBcd() throws Exception {
        try (Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery("SELECT * FROM fields.bcd")) {
            if (rs.next()) {
                ResultSetMetaData metaData = rs.getMetaData();
                Assert.assertEquals("Invalid precision.", 2, metaData.getPrecision(1));
                Assert.assertEquals("Invalid scale.", 0, metaData.getScale(1));
                Assert.assertEquals("Invalid column display size.", 2, metaData.getColumnDisplaySize(1));
            } else {
                Assert.fail("No catalog selected.");
            }
        }
    }
}
