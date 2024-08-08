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

package com.googlecode.paradox.function.string;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.exceptions.ParadoxSyntaxErrorException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for {@link TrimFunction}.
 *
 * @since 1.6.0
 */
class TrimFunctionTest {

    /**
     * The connection string used in  tests.
     */
    private static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/";

    /**
     * The database connection.
     */
    private ParadoxConnection conn;

    /**
     * Creates a new instance.
     */
    public TrimFunctionTest() {
        super();
    }

    /**
     * Register the database driver.
     */
    @BeforeAll
    static void initClass() {
        new Driver();
    }

    /**
     * Close the test connection.
     *
     * @throws SQLException in case of failures.
     */
    @AfterEach
    void closeConnection() throws SQLException {
        if (this.conn != null) {
            this.conn.close();
        }
    }

    /**
     * Connect to the test database.
     *
     * @throws SQLException in case of failures.
     */
    @BeforeEach
    void connect() throws SQLException {
        this.conn = (ParadoxConnection) DriverManager.getConnection(CONNECTION_STRING + "db");
    }

    /**
     * Test for trim function.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testTrim() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select trim(' 3   ') ");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());

            assertEquals("3", rs.getString(1));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for trim with from function.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testTrimFrom() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select trim('1' from '1  3   ') ");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());

            assertEquals("  3   ", rs.getString(1));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for trim both from function.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testTrimBoth() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select trim(BOTH '1' from '1  3   ') ");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());

            assertEquals("  3   ", rs.getString(1));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for trim leading from function.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testTrimLeading() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select trim(LEADING '1' from '1  3   1') ");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());

            assertEquals("  3   1", rs.getString(1));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for trim trailing from function.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testTrimTrailing() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select trim(TRAILING '1' from '1  3   1') ");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());

            assertEquals("1  3   ", rs.getString(1));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for trim with spaces.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testTrimSpaces() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select trim(both ' ' from '   3   1') ");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());

            assertEquals("3   1", rs.getString(1));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for trim with invalid size count.
     */
    @Test
    void testTrimInvalidCount() {
        assertThrows(ParadoxSyntaxErrorException.class, () -> this.conn.prepareStatement("select trim(1 from ' ' from '   3   1') "));
    }

    /**
     * Test for trim with invalid type.
     */
    @Test
    void testTrimInvalid() {
        assertThrows(ParadoxSyntaxErrorException.class, () -> this.conn.prepareStatement("select trim(INVALID ' ' from '   3   1') "));
    }
}
