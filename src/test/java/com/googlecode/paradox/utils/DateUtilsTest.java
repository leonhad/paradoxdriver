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
package com.googlecode.paradox.utils;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;

/**
 * Unit test for {@link DateUtils}.
 *
 * @version 1.0
 * @since 1.3
 */
public class DateUtilsTest {

    /**
     * The connection string used in this tests.
     */
    public static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/";

    /**
     * The connection.
     */
    private static ParadoxConnection conn;

    /**
     * Register the database driver.
     *
     * @throws SQLException in case of failures.
     */
    @BeforeClass
    public static void setUp() throws SQLException {
        new Driver();
        conn = (ParadoxConnection) DriverManager.getConnection(CONNECTION_STRING + "fields");
    }

    @AfterClass
    public static void tearDown() throws SQLException {
        conn.close();
    }

    /**
     * Test the Gregorian do SDN conversion.
     */
    @Test
    public void testGregorianToSdn() {
        Assert.assertEquals("Invalid date.", 2457566, DateUtils.gregorianToSdn(2016, 6, 26, conn.getConnectionInfo()));
    }

    /**
     * Check for invalid date.
     */
    @Test
    public void testGregorianToSdnDateInvalid() {
        Assert.assertEquals("Invalid date.", 0, DateUtils.gregorianToSdn(2016, 6, 0, conn.getConnectionInfo()));
        Assert.assertEquals("Invalid date.", 0, DateUtils.gregorianToSdn(2016, 6, 32, conn.getConnectionInfo()));
    }

    /**
     * Check for invalid month.
     */
    @Test
    public void testGregorianToSdnMonthInvalid() {
        Assert.assertEquals("Invalid date.", 0, DateUtils.gregorianToSdn(2016, 0, 26, conn.getConnectionInfo()));
        Assert.assertEquals("Invalid date.", 0, DateUtils.gregorianToSdn(2016, 13, 26, conn.getConnectionInfo()));
    }

    /**
     * Test negative year.
     */
    @Test
    public void testGregorianToSdnNegativeYearLimit() {
        Assert.assertEquals("Invalid date.", 1721060, DateUtils.gregorianToSdn(-1, 1, 1, conn.getConnectionInfo()));
        Assert.assertEquals("Invalid date.", 1721426, DateUtils.gregorianToSdn(1, 1, 1, conn.getConnectionInfo()));
    }

    /**
     * Check for invalid year.
     */
    @Test
    public void testGregorianToSdnYearInvalid() {
        Assert.assertEquals("Invalid date.", 0, DateUtils.gregorianToSdn(0, 6, 26, conn.getConnectionInfo()));
        Assert.assertEquals("Invalid date.", 0, DateUtils.gregorianToSdn(-5000, 6, 26, conn.getConnectionInfo()));
    }

    /**
     * Test for year limits.
     */
    @Test
    public void testGregorianToSdnYearLimit() {
        Assert.assertEquals("Invalid date.", 0, DateUtils.gregorianToSdn(-4714, 11, 24, conn.getConnectionInfo()));
        Assert.assertEquals("Invalid date.", 0, DateUtils.gregorianToSdn(-4714, 10, 25, conn.getConnectionInfo()));
        Assert.assertEquals("Invalid date.", 1, DateUtils.gregorianToSdn(-4714, 11, 25, conn.getConnectionInfo()));
        Assert.assertEquals("Invalid date.", 30, DateUtils.gregorianToSdn(-4714, 12, 24, conn.getConnectionInfo()));
    }

    /**
     * Test utility class.
     */
    @Test
    public void testSanity() {
        Assert.assertTrue("Utility class in wrong format.", TestUtil.assertSanity(DateUtils.class));
    }

    /**
     * Test low SDN.
     */
    @Test
    public void testSdnToGregorian() {
        final Date date = DateUtils.sdnToGregorian(1);
        Assert.assertNotNull("Null date.", date);
        Assert.assertEquals("Invalid date.", "4715-11-25", date.toString());
    }

    /**
     * Test invalid SDN.
     */
    @Test
    public void testSdnToGregorianInvalid() {
        Assert.assertNull("Date is not null.", DateUtils.sdnToGregorian(0));
        Assert.assertNull("Date is not null.", DateUtils.sdnToGregorian(-1));
    }
}
