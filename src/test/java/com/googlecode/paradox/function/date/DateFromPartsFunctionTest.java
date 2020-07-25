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

package com.googlecode.paradox.function.date;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import org.junit.*;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Unit test for {@link DateFromPartsFunction}.
 *
 * @version 1.0
 * @since 1.6.0
 */
public class DateFromPartsFunctionTest {

    /**
     * The connection string used in this tests.
     */
    private static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/";

    /**
     * The database connection.
     */
    private ParadoxConnection conn;

    /**
     * Creates a new instance.
     */
    public DateFromPartsFunctionTest() {
        super();
    }

    /**
     * Register the database driver.
     */
    @BeforeClass
    public static void initClass() {
        new Driver();
    }

    /**
     * Close the test connection.
     *
     * @throws SQLException in case of failures.
     */
    @After
    public void closeConnection() throws SQLException {
        if (this.conn != null) {
            this.conn.close();
        }
    }

    /**
     * Connect to the test database.
     *
     * @throws SQLException in case of failures.
     */
    @Before
    @SuppressWarnings("java:S2115")
    public void connect() throws SQLException {
        this.conn = (ParadoxConnection) DriverManager.getConnection(CONNECTION_STRING + "db");
    }

    /**
     * Test function.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testFunction() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("SELECT DATEFROMPARTS(2018, 10, 31)");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertNotNull("Null date value", rs.getDate(1));
            Assert.assertEquals("Invalid date", "2018-10-31", rs.getDate(1).toString());
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test for invalid date.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testInvalid() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("SELECT DATEFROMPARTS(2018, 2, 31)");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertNotNull("Null date value", rs.getDate(1));
            Assert.assertEquals("Invalid date", "2018-03-03", rs.getDate(1).toString());
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test null values.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testNull() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("SELECT DATEFROMPARTS(2018, 10, null)");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertNull("Invalid null date value", rs.getDate(1));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }
}