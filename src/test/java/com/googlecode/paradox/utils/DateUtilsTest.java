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
package com.googlecode.paradox.utils;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for {@link DateUtils}.
 *
 * @since 1.3
 */
class DateUtilsTest {

    /**
     * The connection string used in  tests.
     */
    private static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/";

    /**
     * The connection.
     */
    private static ParadoxConnection conn;

    /**
     * Register the database driver.
     *
     * @throws SQLException in case of failures.
     */
    @BeforeAll
    static void setUp() throws SQLException {
        new Driver();
        conn = (ParadoxConnection) DriverManager.getConnection(CONNECTION_STRING + "fields");
    }

    @AfterAll
    static void tearDown() throws SQLException {
        conn.close();
    }

    /**
     * Test the Gregorian do SDN conversion.
     */
    @Test
    void testGregorianToSdn() {
        assertEquals(2457566, DateUtils.gregorianToSdn(2016, 6, 26, conn.getConnectionInfo()));
    }

    /**
     * Check for invalid date.
     */
    @Test
    void testGregorianToSdnDateInvalid() {
        assertEquals(0, DateUtils.gregorianToSdn(2016, 6, 0, conn.getConnectionInfo()));
        assertEquals(0, DateUtils.gregorianToSdn(2016, 6, 32, conn.getConnectionInfo()));
    }

    /**
     * Check for invalid month.
     */
    @Test
    void testGregorianToSdnMonthInvalid() {
        assertEquals(0, DateUtils.gregorianToSdn(2016, 0, 26, conn.getConnectionInfo()));
        assertEquals(0, DateUtils.gregorianToSdn(2016, 13, 26, conn.getConnectionInfo()));
    }

    /**
     * Test negative year.
     */
    @Test
    void testGregorianToSdnNegativeYearLimit() {
        assertEquals(1721060, DateUtils.gregorianToSdn(-1, 1, 1, conn.getConnectionInfo()));
        assertEquals(1721426, DateUtils.gregorianToSdn(1, 1, 1, conn.getConnectionInfo()));
    }

    /**
     * Check for invalid year.
     */
    @Test
    void testGregorianToSdnYearInvalid() {
        assertEquals(0, DateUtils.gregorianToSdn(0, 6, 26, conn.getConnectionInfo()));
        assertEquals(0, DateUtils.gregorianToSdn(-5000, 6, 26, conn.getConnectionInfo()));
    }

    /**
     * Test for year limits.
     */
    @Test
    void testGregorianToSdnYearLimit() {
        assertEquals(0, DateUtils.gregorianToSdn(-4714, 11, 24, conn.getConnectionInfo()));
        assertEquals(0, DateUtils.gregorianToSdn(-4714, 10, 25, conn.getConnectionInfo()));
        assertEquals(1, DateUtils.gregorianToSdn(-4714, 11, 25, conn.getConnectionInfo()));
        assertEquals(30, DateUtils.gregorianToSdn(-4714, 12, 24, conn.getConnectionInfo()));
    }

    /**
     * Test utility class.
     */
    @Test
    void testSanity() {
        assertTrue(TestUtil.assertSanity(DateUtils.class));
    }

    /**
     * Test low SDN.
     */
    @Test
    void testSdnToGregorian() {
        final Date date = DateUtils.sdnToGregorian(1);
        assertNotNull(date);
        assertEquals("4715-11-25", date.toString());
    }

    /**
     * Test invalid SDN.
     */
    @Test
    void testSdnToGregorianInvalid() {
        assertNull(DateUtils.sdnToGregorian(0));
        assertNull(DateUtils.sdnToGregorian(-1));
    }
}
