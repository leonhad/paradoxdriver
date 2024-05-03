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
package com.googlecode.paradox.rowset;

import com.googlecode.paradox.ParadoxConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.*;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for {@link ParadoxClob} class.
 *
 * @since 1.3
 */
class ParadoxClobTest {

    /**
     * Connection string used in tests.
     */
    private static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/";

    /**
     * The database connection.
     */
    private ParadoxConnection conn;

    /**
     * Register the driver.
     *
     * @throws ClassNotFoundException in case of connection errors.
     */
    @BeforeAll
    static void setUp() throws ClassNotFoundException {
        Class.forName(Driver.class.getName());
    }

    /**
     * Used to close the test connection.
     *
     * @throws SQLException in case closing of errors.
     */
    @AfterEach
    void closeConnection() throws SQLException {
        if (this.conn != null) {
            this.conn.close();
        }
    }

    /**
     * Connect to test database.
     *
     * @throws SQLException in case of connection errors.
     */
    @BeforeEach
    void connect() throws SQLException {
        this.conn = (ParadoxConnection) DriverManager.getConnection(CONNECTION_STRING + "db");
    }

    /**
     * Test for {@link Clob#getAsciiStream()} method.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testAsciiStream() throws SQLException, IOException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Comments FROM CUSTOMER")) {
            assertTrue(rs.next());

            final Clob clob = rs.getClob("comments");
            assertNotNull(rs.getClob("comments"));

            final BufferedReader reader = new BufferedReader(new InputStreamReader(clob.getAsciiStream()));
            String line = reader.readLine();

            assertEquals("Small comment (less 100 symbols)", line);
        }
    }

    /**
     * Test for {@link Clob#getCharacterStream()} method.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testCharacterStream() throws SQLException, IOException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Comments FROM CUSTOMER")) {
            assertTrue(rs.next());

            final Clob clob = rs.getClob("comments");
            assertNotNull(rs.getClob("comments"));

            final BufferedReader reader = new BufferedReader(clob.getCharacterStream());
            assertEquals("Small comment (less 100 symbols)", reader.readLine());
        }
    }

    /**
     * Test for {@link Clob#getCharacterStream(long, long)} with high position.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testCharacterStreamWithHighPosition() throws SQLException, IOException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Comments FROM CUSTOMER")) {
            assertTrue(rs.next());

            final Clob clob = rs.getClob("comments");
            assertEquals(32, clob.length());
            assertNotNull(rs.getClob("comments"));

            try (final Reader reader = clob.getCharacterStream(100, 3)) {
                assertEquals(-1, reader.read());
            }
        }
    }

    /**
     * Test for {@link Clob#getCharacterStream(long, long)} with invalid length.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testCharacterStreamWithInvalidLength() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Comments FROM CUSTOMER")) {
            assertTrue(rs.next());

            final Clob clob = rs.getClob("comments");
            assertNotNull(rs.getClob("comments"));

            assertThrows(SQLException.class, () -> clob.getCharacterStream(1, -1));
        }
    }

    /**
     * Test for {@link Clob#getCharacterStream(long, long)} with long length.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testCharacterStreamWithLongLength() throws SQLException, IOException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Comments FROM CUSTOMER")) {
            assertTrue(rs.next());

            final Clob clob = rs.getClob("comments");
            assertNotNull(rs.getClob("comments"));

            try (final Reader reader = clob.getCharacterStream(1, 100)) {
                assertNotNull(reader);
            }
        }
    }

    /**
     * Test for {@link Clob#getCharacterStream(long, long)} with low position.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testCharacterStreamWithLowPosition() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Comments FROM CUSTOMER")) {
            assertTrue(rs.next());

            final Clob clob = rs.getClob("comments");
            assertNotNull(rs.getClob("comments"));

            assertThrows(SQLException.class, () -> clob.getCharacterStream(0, 3));
        }
    }

    /**
     * Test for {@link Clob#getCharacterStream(long, long)} method.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testCharacterStreamWithParameters() throws SQLException, IOException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Comments FROM CUSTOMER")) {
            assertTrue(rs.next());

            final Clob clob = rs.getClob("comments");
            assertNotNull(rs.getClob("comments"));

            final BufferedReader reader = new BufferedReader(clob.getCharacterStream(1, 3));
            assertEquals("Sma", reader.readLine());
        }
    }

    /**
     * Test for {@link ResultSet#getClob(String)} method.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testReadBlob() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Comments FROM CUSTOMER")) {
            assertTrue(rs.next());
            assertNotNull(rs.getClob("comments"));
            assertEquals(rs.getClob(1), rs.getClob("comments"));
            assertTrue(rs.next());
            assertNotNull(rs.getClob("comments"));
            assertEquals(518, rs.getClob("comments").length());
            assertTrue(rs.next());
            assertNotNull(rs.getClob("comments"));
            assertEquals(318, rs.getClob("comments").length());
            assertTrue(rs.next());
            assertNotNull(rs.getClob("comments"));
            assertEquals(56864, rs.getClob("comments").length());
            assertTrue(rs.next());
            assertNotNull(rs.getClob("comments"));
            assertEquals(426, rs.getClob("comments").length());
        }
    }

    /**
     * Test for CLOB with cp1251 charset.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testReadBlob1251() throws SQLException {
        Properties properties = new Properties();
        properties.put("charset", "cp1251");

        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING + "db", properties);
             final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery("SELECT Note FROM NOTE1251 WHERE Id=2")) {

            assertTrue(rs.next());
            final Clob c = rs.getClob("note");
            final String expected = "Удивительное устройство USB-флешки Kingston DataTraveler";
            final String real = c.getSubString(1, (int) c.length());
            assertEquals(expected, real);
        }
    }

    /**
     * Test for {@link Clob#getSubString(long, int)} method.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testSubString() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Comments FROM CUSTOMER")) {
            assertTrue(rs.next());

            final Clob clob = rs.getClob("comments");
            assertNotNull(rs.getClob("comments"));

            assertEquals("Sma", clob.getSubString(1, 3));
        }
    }

    /**
     * Test for {@link Clob#getSubString(long, int)} method with high length.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testSubStringWithHighLength() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Comments FROM CUSTOMER")) {
            assertTrue(rs.next());

            final Clob clob = rs.getClob("comments");
            assertNotNull(rs.getClob("comments"));

            assertEquals("Small comment (less 100 symbols)", clob.getSubString(1, 100));
        }
    }

    /**
     * Test for {@link Clob#getSubString(long, int)} method with high position.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testSubStringWithHighPos() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Comments FROM CUSTOMER")) {
            assertTrue(rs.next());

            final Clob clob = rs.getClob("comments");
            assertNotNull(rs.getClob("comments"));

            assertTrue(clob.getSubString(100, 3).isEmpty());
        }
    }

    /**
     * Test for {@link Clob#getSubString(long, int)} method with invalid length.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testSubStringWithInvalidLength() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Comments FROM CUSTOMER")) {
            assertTrue(rs.next());

            final Clob clob = rs.getClob("comments");
            assertNotNull(rs.getClob("comments"));

            assertThrows(SQLException.class, () -> clob.getSubString(1, -1));
        }
    }

    /**
     * Test for {@link Clob#getSubString(long, int)} method with low position.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testSubStringWithLowPos() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Comments FROM CUSTOMER")) {
            assertTrue(rs.next());

            final Clob clob = rs.getClob("comments");
            assertNotNull(rs.getClob("comments"));

            assertThrows(SQLException.class, () -> clob.getSubString(0, 3));
        }
    }

    /**
     * Test for {@link Clob#truncate(long)} method.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testTruncate() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Comments FROM CUSTOMER")) {
            assertTrue(rs.next());

            final Clob clob = rs.getClob("comments");
            assertNotNull(rs.getClob("comments"));

            clob.truncate(3);
            assertEquals(3, clob.length());
        }
    }

    /**
     * Test for {@link Clob#truncate(long)} method with high value.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testTruncateHighValue() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Comments FROM CUSTOMER")) {
            assertTrue(rs.next());

            final Clob clob = rs.getClob("comments");
            assertNotNull(rs.getClob("comments"));

            final long value = clob.length();
            assertThrows(SQLException.class, () -> clob.truncate(value + 100));
        }
    }

    /**
     * Test for {@link Clob#truncate(long)} method with zero size.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testTruncateWithZeroSize() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Comments FROM CUSTOMER")) {
            assertTrue(rs.next());

            final Clob clob = rs.getClob("comments");
            assertNotNull(rs.getClob("comments"));

            clob.truncate(0);
            assertEquals(0, clob.length());
        }
    }

    /**
     * Test for clob reading.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testClobReading() throws SQLException, IOException {
        try (final PreparedStatement stmt =
                     conn.prepareStatement("SELECT Comments FROM CUSTOMER WHERE CustNo = ?")) {
            stmt.setInt(1, 4);

            try (final ResultSet rs = stmt.executeQuery()) {
                assertTrue(rs.next());

                final Clob clob = rs.getClob("comments");
                final int size = 56864;
                assertEquals(size, clob.length());

                // String methods
                assertEquals(size, rs.getString("Comments").length());
                assertEquals(size, clob.getSubString(1, size).length());

                try (final Reader reader = clob.getCharacterStream(1, size)) {
                    char[] buffer = new char[size];
                    assertEquals(size, reader.read(buffer));
                }
            }
        }
    }
}
