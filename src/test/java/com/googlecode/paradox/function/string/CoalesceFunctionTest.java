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

package com.googlecode.paradox.function.string;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.exceptions.ParadoxSyntaxErrorException;
import org.junit.*;

import java.sql.*;

/**
 * Unit test for coalesce function.
 */
public class CoalesceFunctionTest {

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
    public CoalesceFunctionTest() {
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
     * Test for coalesce.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testCoalesce() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement(
                "select coalesce(b, a) ret from fields.bcd");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertEquals("Invalid value", 1, rs.getInt("ret"));
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertEquals("Invalid value", -1, rs.getInt("ret"));
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertEquals("Invalid value", 0, rs.getInt("ret"));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test for coalesce with 3 parameters.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testCoalesceThreeParameters() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement(
                "select coalesce(1, null, 1) ret");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertEquals("Invalid value", 1, rs.getInt("ret"));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test for coalesce with parameters.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testCoalesceWithParameters() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select coalesce(?, 1)")) {
            stmt.setNull(1, Types.NUMERIC);
            try (final ResultSet rs = stmt.executeQuery()) {
                Assert.assertTrue("Invalid result set state", rs.next());
                Assert.assertEquals("Invalid value", 1, rs.getInt(1));
                Assert.assertFalse("Invalid result set state", rs.next());
            }
        }
    }

    /**
     * Test for invalid types.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testInvalidTypes() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement(
                "select coalesce(1, 'value')")) {
            Assert.assertThrows("Invalid result set", ParadoxSyntaxErrorException.class, stmt::executeQuery);
        }
    }
}