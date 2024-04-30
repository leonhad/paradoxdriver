/*
 * Copyright (c) 2009 Leonardo Alves da Costa
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
 * Unit test for {@link IsDateFunction}.
 *
 * @version 1.0
 * @since 1.6.0
 */
public class IsDateFunctionTest {

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
    public IsDateFunctionTest() {
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
     * Test for IS DATE function.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testIsDate() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select ISDATE('2020-01-01') ");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());

            Assert.assertEquals("Invalid value", 1, rs.getInt(1));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test for null value.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testNull() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select ISDATE(null) ");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());

            Assert.assertEquals("Invalid value", 0, rs.getInt(1));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test for numeric value.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testNumeric() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select ISDATE(1) ");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());

            Assert.assertEquals("Invalid value", 0, rs.getInt(1));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test for string value.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testString() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select ISDATE('a') ");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());

            Assert.assertEquals("Invalid value", 0, rs.getInt(1));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }
}
