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

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

/**
 * Unit test for {@link DateUtils}.
 *
 * @version 1.0
 * @since 1.3
 */
public class DateUtilsTest {

    /**
     * Test the Gregorian do SDN conversion.
     */
    @Test
    public void testGregorianToSdn() {
        Assert.assertEquals("Invalid date.", 2457566, DateUtils.gregorianToSdn(2016, 6, 26));
    }

    /**
     * Check for invalid date.
     */
    @Test
    public void testGregorianToSdnDateInvalid() {
        Assert.assertEquals("Invalid date.", 0, DateUtils.gregorianToSdn(2016, 6, 0));
        Assert.assertEquals("Invalid date.", 0, DateUtils.gregorianToSdn(2016, 6, 32));
    }

    /**
     * Check for invalid month.
     */
    @Test
    public void testGregorianToSdnMonthInvalid() {
        Assert.assertEquals("Invalid date.", 0, DateUtils.gregorianToSdn(2016, 0, 26));
        Assert.assertEquals("Invalid date.", 0, DateUtils.gregorianToSdn(2016, 13, 26));
    }

    /**
     * Test negative year.
     */
    @Test
    public void testGregorianToSdnNegativeYearLimit() {
        Assert.assertEquals("Invalid date.", 1721060, DateUtils.gregorianToSdn(-1, 1, 1));
        Assert.assertEquals("Invalid date.", 1721426, DateUtils.gregorianToSdn(1, 1, 1));
    }

    /**
     * Check for invalid year.
     */
    @Test
    public void testGregorianToSdnYearInvalid() {
        Assert.assertEquals("Invalid date.", 0, DateUtils.gregorianToSdn(0, 6, 26));
        Assert.assertEquals("Invalid date.", 0, DateUtils.gregorianToSdn(-5000, 6, 26));
    }

    /**
     * Test for year limits.
     */
    @Test
    public void testGregorianToSdnYearLimit() {
        Assert.assertEquals("Invalid date.", 0, DateUtils.gregorianToSdn(-4714, 11, 24));
        Assert.assertEquals("Invalid date.", 0, DateUtils.gregorianToSdn(-4714, 10, 25));
        Assert.assertEquals("Invalid date.", 1, DateUtils.gregorianToSdn(-4714, 11, 25));
        Assert.assertEquals("Invalid date.", 30, DateUtils.gregorianToSdn(-4714, 12, 24));
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
