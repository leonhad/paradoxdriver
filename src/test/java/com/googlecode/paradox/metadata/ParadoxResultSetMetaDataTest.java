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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Arrays;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.integration.MainTest;

/**
 * Unit test for {@link ParadoxResultSetMetaData} class.
 * 
 * @author Leonardo Alves da Costa
 * @since 1.3
 * @version 1.0
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
     *             in case of failures.
     */
    @BeforeClass
    public static void setUp() throws Exception {
        Class.forName(Driver.class.getName());
    }

    /**
     * Close the test conneciton.
     * 
     * @throws Exception
     *             in case of failures.
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
     *             in case of failures.
     */
    @Before
    public void connect() throws Exception {
        conn = DriverManager.getConnection(MainTest.CONNECTION_STRING + "db");
    }

    /**
     * Test for the catalog metadata.
     * 
     * @throws Exception
     *             in case of failures.
     */
    @Test
    public void testCatalog() throws Exception {
        ResultSet rs = null;

        final DatabaseMetaData meta = conn.getMetaData();
        try {
            rs = meta.getCatalogs();
            if (rs.next()) {
                Assert.assertEquals("db", rs.getString("TABLE_CAT"));
            } else {
                Assert.fail("No catalog selected.");
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    /**
     * Test for the index info metadata.
     * 
     * @throws Exception
     *             in case of failures.
     */
    @Test
    public void testIndexInfo() throws Exception {
        ResultSet rs = null;

        try {
            final String[] names = new String[2];
            final DatabaseMetaData meta = conn.getMetaData();

            rs = meta.getIndexInfo("db", "APP", "customer.db", true, true);
            Assert.assertTrue(rs.next());
            names[0] = rs.getString("INDEX_NAME");
            Assert.assertTrue(rs.next());
            names[1] = rs.getString("INDEX_NAME");
            Assert.assertTrue(rs.next());

            Arrays.sort(names);
            Assert.assertEquals("CUSTOMER.PX", names[0]);
            Assert.assertEquals("CUSTOMER.X06", names[1]);

            while (rs.next()) {
                Assert.assertEquals("db", rs.getString("TABLE_CAT"));
                Assert.assertEquals("APP", rs.getString("TABLE_SCHEM"));
                Assert.assertEquals(null, rs.getString("TABLE_NAME"));
                Assert.assertEquals("false", rs.getString("NON_UNIQUE"));
                Assert.assertEquals("db", rs.getString("INDEX_QUALIFIER"));
                Assert.assertEquals("CUSTOMER.X06", rs.getString("INDEX_NAME"));
                Assert.assertEquals("2", rs.getString("TYPE"));
                Assert.assertEquals("0", rs.getString("ORDINAL_POSITION"));
                Assert.assertEquals("City", rs.getString("COLUMN_NAME"));
                Assert.assertEquals("A", rs.getString("ASC_OR_DESC"));
                Assert.assertEquals("0", rs.getString("CARDINALITY"));
                Assert.assertEquals("0", rs.getString("PAGES"));
                Assert.assertEquals(null, rs.getString("FILTER_CONDITION"));
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    /**
     * Test for primary key metadata.
     * 
     * @throws Exception
     *             in case of failures.
     */
    @Test
    public void testPrimaryKey() throws Exception {
        ResultSet rs = null;

        try {
            final DatabaseMetaData meta = conn.getMetaData();

            rs = meta.getPrimaryKeys("db", "APP", "CUSTOMER.db");
            Assert.assertTrue(rs.next());
            Assert.assertEquals("db", rs.getString("TABLE_CAT"));
            Assert.assertEquals("APP", rs.getString("TABLE_SCHEM"));
            Assert.assertEquals("CUSTOMER", rs.getString("TABLE_NAME"));
            Assert.assertEquals("CustNo", rs.getString("COLUMN_NAME"));
            Assert.assertEquals("0", rs.getString("KEY_SEQ"));
            Assert.assertEquals("CustNo", rs.getString("PK_NAME"));
            Assert.assertFalse(rs.next());
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }
}
