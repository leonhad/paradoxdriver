/*
 * DriverTest.java
 *
 * 06/29/2016
 * Copyright (C) 2016 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox;

import com.googlecode.paradox.utils.Constants;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;

/**
 * Unit test for {@link Driver}.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.3
 */
public final class DriverTest {

    /**
     * Test for JDBC compliance.
     */
    @Test
    public void testCompliance() {
        final Driver driver = new Driver();
        Assert.assertFalse("Driver not compliant.", driver.jdbcCompliant());
    }

    /**
     * Test for invalid connection.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testInvalidConnection() throws SQLException {
        final Driver driver = new Driver();
        Assert.assertNull("Connection can't be valid.", driver.connect(null, null));
    }

    /**
     * Test an invalid URL.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testInvalidURL() throws SQLException {
        final Driver driver = new Driver();
        Assert.assertFalse(driver.acceptsURL("jdbc:mysql:/path"));
    }

    /**
     * Test for driver logger.
     *
     * @throws SQLFeatureNotSupportedException
     *         in case of errors.
     */
    @Test
    public void testLogger() throws SQLFeatureNotSupportedException {
        final Driver driver = new Driver();
        Assert.assertNotNull(driver.getParentLogger());
    }

    /**
     * Test for the Major Version.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testMajorVersion() throws SQLException {
        final Driver driver = new Driver();
        Assert.assertEquals(Constants.MAJOR_VERSION, driver.getMajorVersion());
    }

    /**
     * Test for the Minor Version.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testMinorVersion() throws SQLException {
        final Driver driver = new Driver();
        Assert.assertEquals(Constants.MINOR_VERSION, driver.getMinorVersion());
    }

    /**
     * Test for null properties.
     *
     * @throws SQLException
     *         if there is no errors.
     */
    @Test(expected = NullPointerException.class)
    public void testNullProperty() throws SQLException {
        final Driver driver = new Driver();
        Assert.assertEquals(0, driver.getPropertyInfo(null, null).length);
    }

    /**
     * Test for null property info.
     *
     * @throws SQLException
     *         if there is no errors.
     */
    @Test(expected = NullPointerException.class)
    public void testNullPropertyInfo() throws SQLException {
        final Driver driver = new Driver();
        driver.getPropertyInfo("jdbc:paradox:target/test-classes/", null);
    }

    /**
     * Test for property info.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testPropertyInfo() throws SQLException {
        final Driver driver = new Driver();
        final DriverPropertyInfo[] info = driver.getPropertyInfo("jdbc:paradox:target/test-classes/", new Properties());
        Assert.assertEquals(2, info.length);
        Assert.assertEquals("DBNAME", info[0].name);
        Assert.assertEquals("password", info[1].name);
    }

    /**
     * Test for properties with invalid URL.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test(expected = NullPointerException.class)
    public void testPropertyInfoInvalidURL() throws SQLException {
        final Driver driver = new Driver();
        driver.getPropertyInfo("jdbc:paradox:/path", null);
    }

    /**
     * Test for a valid connection.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testValidConnection() throws SQLException {
        final Driver driver = new Driver();
        try (Connection c = driver.connect("jdbc:paradox:target/test-classes/", null)) {
            Assert.assertNotNull("Connection is null.", c);
            Assert.assertNotNull("Connection is not valid.", c.isValid(0));
        }
    }

    /**
     * Test a valid URL.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testValidURL() throws SQLException {
        final Driver driver = new Driver();
        Assert.assertTrue("Driver connection is invalid.", driver.acceptsURL("jdbc:paradox:/path"));
    }
}
