/*
 * ParadoxResultSetTest.java
 *
 * 07/21/2016
 * Copyright (C) 2016 Leonardo Alves da Costa
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
package com.googlecode.paradox;

import com.googlecode.paradox.data.table.value.FieldValue;
import com.googlecode.paradox.results.Column;
import org.junit.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

/**
 * Unit test for {@link ParadoxResultSet} class.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.3
 */
public class ParadoxResultSetTest {
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
     * Close the test conneciton.
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
     * Test for {@link ResultSet} execution.
     *
     * @throws Exception
     *         in case of failures.
     */
    @Test
    public void testResultSet() throws Exception {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT AC as 'ACode', State, CITIES FROM AREACODES")) {
            Assert.assertTrue("No First row", rs.next());
            Assert.assertEquals("Testing for column 'AC'.", "201", rs.getString("ac"));
            Assert.assertEquals("Testing for column 'State'.", "NJ", rs.getString("State"));
            Assert.assertEquals("Testing for column 'Cities'.", "Hackensack, Jersey City (201/551 overlay)",
                    rs.getString("Cities"));
        }
    }

    /**
     * Test for first result.
     *
     * @throws Exception
     *         in case of failures.
     */
    @Test
    public void testFirstResult() throws Exception {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT AC as 'ACode', State, CITIES FROM AREACODES")) {
            Assert.assertTrue("No first row", rs.next());
            String firstValue = rs.getString("ac");
            Assert.assertTrue("No first row", rs.next());
            Assert.assertNotEquals("Rows with same value.", firstValue, rs.getString("ac"));
            Assert.assertTrue("Not in first row.", rs.first());
            Assert.assertEquals("Rows with different values.", firstValue, rs.getString("ac"));
        }
    }

    /**
     * Test for first result.
     *
     * @throws Exception
     *         in case of failures.
     */
    @Test
    public void testNoFirstResult() throws Exception {
        ParadoxConnection paradoxConnection = (ParadoxConnection) conn;
        try (ParadoxResultSet rs = new ParadoxResultSet(paradoxConnection, new ParadoxStatement(paradoxConnection),
                Collections.<List<FieldValue>>emptyList(), Collections.<Column>emptyList())) {

            Assert.assertFalse("There is one first row", rs.next());
            Assert.assertFalse("There is one first row", rs.first());
        }
    }
}