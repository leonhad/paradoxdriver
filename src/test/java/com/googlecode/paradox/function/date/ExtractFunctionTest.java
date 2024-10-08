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
 * Unit test for {@link ExtractFunction}.
 *
 * @since 1.6.0
 */
class ExtractFunctionTest {

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
    public ExtractFunctionTest() {
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
     * Test for milliseconds.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testMilliseconds() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select EXTRACT(MILLISECOND FROM '2017-06-15 01:02:03.091') ");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());

            assertEquals(91, rs.getInt(1));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for seconds.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testSeconds() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select EXTRACT(SECOND FROM '2017-06-15 01:02:03.091') ");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());

            assertEquals(3, rs.getInt(1));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for minutes.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testMinutes() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select EXTRACT(MINUTE FROM '2017-06-15 01:02:03.091') ");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());

            assertEquals(2, rs.getInt(1));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for hours.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testHours() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select EXTRACT(HOUR FROM '2017-06-15 13:02:03.091') ");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());

            assertEquals(13, rs.getInt(1));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for hours with time.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testHoursTime() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select EXTRACT(HOUR FROM '13:02:03') ");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());

            assertEquals(13, rs.getInt(1));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for day with date.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testDay() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select EXTRACT(DAY FROM '2017-06-15') ");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());

            assertEquals(15, rs.getInt(1));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for day with timestamp.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testDayTimestamp() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select EXTRACT(DAY FROM '2017-06-15 13:02:03.091') ");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());

            assertEquals(15, rs.getInt(1));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for month with timestamp.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testMonth() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select EXTRACT(MONTH FROM '2017-06-15') ");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());

            assertEquals(6, rs.getInt(1));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for year with timestamp.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testYear() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select EXTRACT(YEAR FROM '2017-06-15') ");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());

            assertEquals(2017, rs.getInt(1));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for week with timestamp.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testWeek() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select EXTRACT(WEEK FROM '2017-06-15') ");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());

            assertEquals(24, rs.getInt(1));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for quarter with timestamp.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testQuarter() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select EXTRACT(QUARTER FROM '2017-06-15') ");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());

            assertEquals(2, rs.getInt(1));
            assertFalse(rs.next());
        }
    }
}
