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
package com.googlecode.paradox;

import com.googlecode.paradox.utils.Constants;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for {@link Driver}.
 *
 * @since 1.3
 */
class DriverTest {

    /**
     * Test for JDBC compliance.
     */
    @Test
    void testCompliance() {
        final Driver driver = new Driver();
        assertFalse(driver.jdbcCompliant());
    }

    /**
     * Test for invalid connection.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testInvalidConnection() throws SQLException {
        final Driver driver = new Driver();
        assertNull(driver.connect(null, new Properties()));
    }

    /**
     * Test an invalid URL.
     */
    @Test
    void testInvalidURL() {
        final Driver driver = new Driver();
        assertFalse(driver.acceptsURL("jdbc:mysql:/path"));
    }

    /**
     * Test for driver logger.
     */
    @Test
    void testLogger() {
        final Driver driver = new Driver();
        assertNotNull(driver.getParentLogger());
    }

    /**
     * Test for the Major Version.
     */
    @Test
    void testMajorVersion() {
        final Driver driver = new Driver();
        assertEquals(Constants.MAJOR_VERSION, driver.getMajorVersion());
    }

    /**
     * Test for the Minor Version.
     */
    @Test
    void testMinorVersion() {
        final Driver driver = new Driver();
        assertEquals(Constants.MINOR_VERSION, driver.getMinorVersion());
    }

    /**
     * Test for null properties.
     */
    @Test
    void testNullProperty() {
        final Driver driver = new Driver();
        assertEquals(7, driver.getPropertyInfo(null, null).length);
    }

    /**
     * Test for null property info.
     */
    @Test
    void testNullPropertyInfo() {
        final Driver driver = new Driver();
        assertEquals(7, driver.getPropertyInfo("jdbc:paradox:target/test-classes/", null).length);
    }

    /**
     * Test for property info.
     */
    @Test
    void testPropertyInfo() {
        final Driver driver = new Driver();
        final DriverPropertyInfo[] info = driver.getPropertyInfo("jdbc:paradox:target/test-classes/", new Properties());
        assertEquals(7, info.length);
        assertEquals(ConnectionInfo.BCD_ROUNDING_KEY, info[0].name);
        assertEquals(ConnectionInfo.CHARSET_KEY, info[1].name);
        assertEquals(ConnectionInfo.ENABLE_CATALOG_KEY, info[2].name);
        assertEquals(ConnectionInfo.LOCALE_KEY, info[3].name);
        assertEquals("password", info[4].name);
        assertEquals(ConnectionInfo.TIMEZONE_KEY, info[5].name);
        assertEquals(ConnectionInfo.USER_KEY, info[6].name);
    }

    /**
     * Test for a valid connection.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testValidConnection() throws SQLException {
        final Driver driver = new Driver();
        try (Connection c = driver.connect("jdbc:paradox:target/test-classes/", new Properties())) {
            assertNotNull(c);
            assertTrue(c.isValid(0));
        }
    }

    /**
     * Test a valid URL.
     */
    @Test
    void testValidURL() {
        final Driver driver = new Driver();
        assertTrue(driver.acceptsURL("jdbc:paradox:/path"));
    }
}
