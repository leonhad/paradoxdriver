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
 * Unit test for {@link IsDateFunction}.
 *
 * @since 1.6.0
 */
class IsDateFunctionTest {

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
    public IsDateFunctionTest() {
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
     * Test for IS DATE function.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testIsDate() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select ISDATE('2020-01-01') ");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());

            assertEquals(1, rs.getInt(1));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for null value.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testNull() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select ISDATE(null) ");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());

            assertEquals(0, rs.getInt(1));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for numeric value.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testNumeric() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select ISDATE(1) ");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());

            assertEquals(0, rs.getInt(1));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for string value.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testString() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select ISDATE('a') ");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());

            assertEquals(0, rs.getInt(1));
            assertFalse(rs.next());
        }
    }
}
