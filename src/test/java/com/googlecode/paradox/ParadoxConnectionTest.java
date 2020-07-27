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
package com.googlecode.paradox;

import com.googlecode.paradox.exceptions.ParadoxConnectionException;
import com.googlecode.paradox.exceptions.ParadoxNotSupportedException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Properties;

/**
 * Unit test for connection.
 *
 * @version 1.1
 * @since 1.6.0
 */
@SuppressWarnings({"java:S2115", "java:S1192"})
public class ParadoxConnectionTest {

    /**
     * The connection string used in this tests.
     */
    public static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/db";

    /**
     * Register the database driver.
     */
    @BeforeClass
    @SuppressWarnings("java:S2115")
    public static void setUp() {
        new Driver();
    }

    /**
     * Test for connection.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testConnect() throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            Assert.assertNotNull("Invalid connection", conn);
        }
    }

    /**
     * Test for clear warnings.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testClearWarnings() throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            conn.clearWarnings();
            Assert.assertNull("Invalid warnings", conn.getWarnings());
        }
    }

    /**
     * Test for invalid directory.
     */
    @Test
    public void testInvalidDirectory() {
        Assert.assertThrows("Invalid directory", ParadoxConnectionException.class,
                () -> DriverManager.getConnection(CONNECTION_STRING + "/invalid"));
    }

    /**
     * Test for charset changing.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testCharsetChanging() throws SQLException {
        String charsetName = "UTF-8";

        final Properties info = new Properties();
        info.put("charset", charsetName);

        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING, info)) {
            Assert.assertEquals("Invalid charset", charsetName,
                    ((ParadoxConnection) conn).getConnectionInfo().getCharset().displayName());
        }
    }

    /**
     * Test for default charset.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testDefaultCharset() throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            Assert.assertNull("Invalid charset", ((ParadoxConnection) conn).getConnectionInfo().getCharset());
        }
    }

    /**
     * Test for default locale.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testDefaultLocale() throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            Assert.assertEquals("Invalid charset", "en",
                    ((ParadoxConnection) conn).getConnectionInfo().getLocale().getLanguage());
        }
    }

    /**
     * Test for locale changing.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testLocaleChanging() throws SQLException {
        Locale locale = new Locale("pt", "BR");

        final Properties info = new Properties();
        info.put("locale", locale.toLanguageTag());

        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING, info)) {
            Assert.assertEquals("Invalid locale", locale,
                    ((ParadoxConnection) conn).getConnectionInfo().getLocale());
        }
    }

    /**
     * Test for default BCD rounding.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testDefaultBCDRounding() throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            Assert.assertTrue("Invalid BCD rounding", ((ParadoxConnection) conn).getConnectionInfo().isBcdRounding());
        }
    }

    /**
     * Test for BCD rounding changing.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testBCDRoundingChanging() throws SQLException {
        final Properties info = new Properties();
        info.put("bcd_rounding", "false");

        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING, info)) {
            Assert.assertFalse("Invalid BCD rounding", ((ParadoxConnection) conn).getConnectionInfo().isBcdRounding());
        }
    }

    /**
     * Test for create array of.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testCreateArray() throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            Assert.assertThrows("Invalid create array", ParadoxNotSupportedException.class,
                    () -> conn.createArrayOf(null, null));
        }
    }

    /**
     * Test for create NCLOB.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testCreateNClob() throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            Assert.assertThrows("Invalid create NCLOB", ParadoxNotSupportedException.class,
                    conn::createNClob);
        }
    }

    /**
     * Test for create SQL XML.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testCreateSQLXML() throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            Assert.assertThrows("Invalid create SQL XML", ParadoxNotSupportedException.class,
                    conn::createSQLXML);
        }
    }

    /**
     * Test for create Struct.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testCreateStruct() throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            Assert.assertThrows("Invalid create SQL XML", ParadoxNotSupportedException.class,
                    () -> conn.createStruct(null, null));
        }
    }

    /**
     * Test for create BLOB.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testCreateBlob() throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            Assert.assertNotNull("Invalid created BLOB", conn.createBlob());
        }
    }

    /**
     * Test for create CLOB.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testCreateClob() throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            Assert.assertNotNull("Invalid created CLOB", conn.createClob());
        }
    }

    /**
     * Test for isClosed.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    @SuppressWarnings("java:S2095")
    public void testIsClosed() throws SQLException {
        Connection conn = DriverManager.getConnection(CONNECTION_STRING);
        Assert.assertFalse("Invalid connection state", conn.isClosed());
        conn.close();
        Assert.assertTrue("Invalid connection state", conn.isClosed());
    }
}
