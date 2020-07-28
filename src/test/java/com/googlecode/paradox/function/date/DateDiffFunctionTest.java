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
public class DateDiffFunctionTest {

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
    public DateDiffFunctionTest() {
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
     * Test milliseconds.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testMilliseconds() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement(
                "SELECT DATEDIFF(MILLISECOND, '2017-08-25', '2017-08-24')");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertEquals("Invalid date", -86_400_000, rs.getInt(1));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test seconds.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testSeconds() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement(
                "SELECT DATEDIFF(SECOND, '2017-08-25', '2011-08-25')");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertEquals("Invalid date", -189_388_800, rs.getInt(1));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test minute.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testMinute() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement(
                "SELECT DATEDIFF(MINUTE, '2017-08-25', '2011-08-25')");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertEquals("Invalid date", -3_156_480, rs.getInt(1));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test hour.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testHour() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement(
                "SELECT DATEDIFF(HOUR, '2017-08-25', '2011-08-25')");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertEquals("Invalid date", -52_608, rs.getInt(1));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test days.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testDays() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement(
                "SELECT DATEDIFF(day, '2017-08-25', '2011-08-25')");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertEquals("Invalid date", -2_192, rs.getInt(1));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test day of the year.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testDayOfYear() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement(
                "SELECT DATEDIFF(DAYOFYEAR, '2017-08-25', '2011-08-25')");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertEquals("Invalid date", -2_192, rs.getInt(1));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test month.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testMonth() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement(
                "SELECT DATEDIFF(MONTH, '2017-08-25', '2011-08-25')");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertEquals("Invalid date", -72, rs.getInt(1));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test year.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testYear() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement(
                "SELECT DATEDIFF(YEAR, '2017-08-25', '2011-08-25')");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertEquals("Invalid date", -6, rs.getInt(1));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test weeks.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testWeeks() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement(
                "SELECT DATEDIFF(WEEK, '2017-08-25', '2011-08-25')");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertEquals("Invalid date", -313, rs.getInt(1));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test quarters.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testQarters() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement(
                "SELECT DATEDIFF(QUARTER, '2017-08-25', '2011-08-25')");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertEquals("Invalid date", -24, rs.getInt(1));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }
}