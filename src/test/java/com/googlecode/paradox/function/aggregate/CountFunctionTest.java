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

package com.googlecode.paradox.function.aggregate;

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
 * Unit test for {@link CountFunction}.
 *
 * @since 1.6.0
 */
class CountFunctionTest {

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
    public CountFunctionTest() {
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
     * Test for count function.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testCount() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select count(1) from fields.date7");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());
            assertEquals(5, rs.getInt(1));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for count with null values.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testCountNull() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select count(\"DATE\") from fields.date7");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());
            assertEquals(4, rs.getInt(1));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for count with asterisk.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testCountAsterisk() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select count(*) from fields.date7");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());
            assertEquals(5, rs.getInt(1));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for count with group by.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testCountWithGroupBy() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select count(State), AC from db.AREACODES group by AC");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());
            assertNotEquals(0, rs.getInt(1));
        }
    }

    /**
     * Test for count with group by not in SELECT.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testCountWithGroupByNotInSelect() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("SELECT count(*) FROM AREACODES group by State");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());
            assertNotEquals(0, rs.getInt(1));
            assertTrue(rs.next());
            assertNotEquals(0, rs.getInt(1));
        }
    }

    /**
     * Test for count with only null values.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testCountWithNullValues() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select count(LONG) from fields.long where LONG is null");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());
            assertEquals(0, rs.getInt(1));
            assertFalse(rs.wasNull());
            assertFalse(rs.next());
        }
    }

    /**
     * Test for count with group by null values.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testCountWithGroupByNullValues() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("SELECT count(*), null FROM AREACODES group by null");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());
            assertEquals(370, rs.getInt(1));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for count inside a function.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testCountInsideFunction() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("SELECT upper(count(*)) FROM AREACODES");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());
            assertEquals(370, rs.getInt(1));
            assertFalse(rs.next());
        }
    }
}
