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

import com.googlecode.paradox.exceptions.ParadoxConnectionException;
import com.googlecode.paradox.exceptions.ParadoxNotSupportedException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for connection.
 *
 * @since 1.6.0
 */
class ParadoxConnectionTest {

    /**
     * The connection string used in tests.
     */
    private static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/db";

    /**
     * Register the database driver.
     */
    @BeforeAll
    static void setUp() {
        new Driver();
    }

    /**
     * Test for connection.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testConnect() throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            assertNotNull(conn);
        }
    }

    /**
     * Test for clear warnings.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testClearWarnings() throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            conn.clearWarnings();
            assertNull(conn.getWarnings());
        }
    }

    /**
     * Test for invalid directory.
     */
    @Test
    void testInvalidDirectory() {
        assertThrows(ParadoxConnectionException.class, () -> DriverManager.getConnection(CONNECTION_STRING + "/invalid"));
    }

    /**
     * Test for charset changing.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testCharsetChanging() throws SQLException {
        String charsetName = StandardCharsets.UTF_8.name();

        final Properties info = new Properties();
        info.put("charset", charsetName);

        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING, info)) {
            assertEquals(charsetName, ((ParadoxConnection) conn).getConnectionInfo().getCharset().displayName());
        }
    }

    /**
     * Test for default charset.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testDefaultCharset() throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            assertNull(((ParadoxConnection) conn).getConnectionInfo().getCharset());
        }
    }

    /**
     * Test for default locale.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testDefaultLocale() throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            assertEquals("en", ((ParadoxConnection) conn).getConnectionInfo().getLocale().getLanguage());
        }
    }

    /**
     * Test for locale changing.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testLocaleChanging() throws SQLException {
        Locale locale = new Locale.Builder().setLanguage("pt").setRegion("BR").build();

        final Properties info = new Properties();
        info.put("locale", locale.toLanguageTag());

        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING, info)) {
            assertEquals(locale, ((ParadoxConnection) conn).getConnectionInfo().getLocale());
        }
    }

    /**
     * Test for schema switch.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testSchemaSwitch() throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            assertEquals("db", conn.getSchema());

            conn.setSchema("fields");
            assertEquals("fields", conn.getSchema());
        }
    }

    /**
     * Test for schema switch with catalog enabled.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testSchemaSwitchWithCatalog() throws SQLException {
        Properties info = new Properties();
        info.put(ConnectionInfo.ENABLE_CATALOG_KEY, "true");

        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING, info)) {
            assertEquals("db", conn.getSchema());

            conn.setSchema("fields");
            assertEquals("fields", conn.getSchema());
        }
    }

    /**
     * Test for default BCD rounding.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testDefaultBCDRounding() throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            assertTrue(((ParadoxConnection) conn).getConnectionInfo().isBcdRounding());
        }
    }

    /**
     * Test for BCD rounding changing.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testBCDRoundingChanging() throws SQLException {
        final Properties info = new Properties();
        info.put("bcd_rounding", "false");

        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING, info)) {
            assertFalse(((ParadoxConnection) conn).getConnectionInfo().isBcdRounding());
        }
    }

    /**
     * Test for create array of.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testCreateArray() throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            assertThrows(ParadoxNotSupportedException.class, () -> conn.createArrayOf(null, null));
        }
    }

    /**
     * Test for create NCLOB.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testCreateNClob() throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            assertThrows(ParadoxNotSupportedException.class, conn::createNClob);
        }
    }

    /**
     * Test for create SQL XML.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testCreateSQLXML() throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            assertThrows(ParadoxNotSupportedException.class, conn::createSQLXML);
        }
    }

    /**
     * Test for create Struct.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testCreateStruct() throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            assertThrows(ParadoxNotSupportedException.class, () -> conn.createStruct(null, null));
        }
    }

    /**
     * Test for create BLOB.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testCreateBlob() throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            assertNotNull(conn.createBlob());
        }
    }

    /**
     * Test for create CLOB.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testCreateClob() throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            assertNotNull(conn.createClob());
        }
    }

    /**
     * Test for isClosed.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    @SuppressWarnings("java:S2095")
    void testIsClosed() throws SQLException {
        Connection conn = DriverManager.getConnection(CONNECTION_STRING);
        assertFalse(conn.isClosed());
        conn.close();
        assertTrue(conn.isClosed());
    }

    /**
     * Test for connection in root.
     */
    @Test
    @SuppressWarnings("java:S2095")
    void testRoot() {
        assertThrows(ParadoxConnectionException.class, () -> DriverManager.getConnection("jdbc:paradox:/"));
    }
}
