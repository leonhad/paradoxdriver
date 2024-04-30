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
import org.junit.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.*;
import java.util.Properties;

/**
 * Unit test for {@link ParadoxClob} class.
 *
 * @version 1.4
 * @since 1.3
 */
public class ParadoxClobTest {

    /**
     * Connection string used in tests.
     */
    public static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/";

    /**
     * The database connection.
     */
    private ParadoxConnection conn;

    /**
     * Register the driver.
     *
     * @throws ClassNotFoundException in case of connection errors.
     */
    @BeforeClass
    public static void setUp() throws ClassNotFoundException {
        Class.forName(Driver.class.getName());
    }

    /**
     * Used to close the test connection.
     *
     * @throws SQLException in case closing of errors.
     */
    @After
    public void closeConnection() throws SQLException {
        if (this.conn != null) {
            this.conn.close();
        }
    }

    /**
     * Connect to test database.
     *
     * @throws SQLException in case of connection errors.
     */
    @Before
    public void connect() throws SQLException {
        this.conn = (ParadoxConnection) DriverManager.getConnection(CONNECTION_STRING + "db");
    }

    /**
     * Test for {@link Clob#getAsciiStream()} method.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testAsciiStream() throws SQLException, IOException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Comments FROM CUSTOMER")) {
            Assert.assertTrue("First record not exists", rs.next());

            final Clob clob = rs.getClob("comments");
            Assert.assertNotNull("First comment is null", rs.getClob("comments"));

            final BufferedReader reader = new BufferedReader(new InputStreamReader(clob.getAsciiStream()));
            String line = reader.readLine();

            Assert.assertEquals("Testing for input stream value.", "Small comment (less 100 symbols)",
                    line);
        }
    }

    /**
     * Test for {@link Clob#getCharacterStream()} method.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testCharacterStream() throws SQLException, IOException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Comments FROM CUSTOMER")) {
            Assert.assertTrue("First record not exists", rs.next());

            final Clob clob = rs.getClob("comments");
            Assert.assertNotNull("First comment is null", rs.getClob("comments"));

            final BufferedReader reader = new BufferedReader(clob.getCharacterStream());
            Assert.assertEquals("Testing for input stream value.", "Small comment (less 100 symbols)",
                    reader.readLine());
        }
    }

    /**
     * Test for {@link Clob#getCharacterStream(long, long)} with high position.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testCharacterStreamWithHighPosition() throws SQLException, IOException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Comments FROM CUSTOMER")) {
            Assert.assertTrue("First record not exists", rs.next());

            final Clob clob = rs.getClob("comments");
            Assert.assertEquals("Invalid clob size", 32, clob.length());
            Assert.assertNotNull("First comment is null", rs.getClob("comments"));

            try (final Reader reader = clob.getCharacterStream(100, 3)) {
                Assert.assertEquals("Invalid clob size", -1, reader.read());
            }
        }
    }

    /**
     * Test for {@link Clob#getCharacterStream(long, long)} with invalid length.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testCharacterStreamWithInvalidLength() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Comments FROM CUSTOMER")) {
            Assert.assertTrue("First record not exists", rs.next());

            final Clob clob = rs.getClob("comments");
            Assert.assertNotNull("First comment is null", rs.getClob("comments"));

            Assert.assertThrows("Invalid clob", SQLException.class, () -> clob.getCharacterStream(1, -1));
        }
    }

    /**
     * Test for {@link Clob#getCharacterStream(long, long)} with long length.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testCharacterStreamWithLongLength() throws SQLException, IOException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Comments FROM CUSTOMER")) {
            Assert.assertTrue("First record not exists", rs.next());

            final Clob clob = rs.getClob("comments");
            Assert.assertNotNull("First comment is null", rs.getClob("comments"));

            try (final Reader reader = clob.getCharacterStream(1, 100)) {
                Assert.assertNotNull("Invalid value", reader);
            }
        }
    }

    /**
     * Test for {@link Clob#getCharacterStream(long, long)} with low position.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testCharacterStreamWithLowPosition() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Comments FROM CUSTOMER")) {
            Assert.assertTrue("First record not exists", rs.next());

            final Clob clob = rs.getClob("comments");
            Assert.assertNotNull("First comment is null", rs.getClob("comments"));

            Assert.assertThrows("Invalid clob", SQLException.class, () -> clob.getCharacterStream(0, 3));
        }
    }

    /**
     * Test for {@link Clob#getCharacterStream(long, long)} method.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testCharacterStreamWithParameters() throws SQLException, IOException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Comments FROM CUSTOMER")) {
            Assert.assertTrue("First record not exists", rs.next());

            final Clob clob = rs.getClob("comments");
            Assert.assertNotNull("First comment is null", rs.getClob("comments"));

            final BufferedReader reader = new BufferedReader(clob.getCharacterStream(1, 3));
            Assert.assertEquals("Testing for input stream value.", "Sma", reader.readLine());
        }
    }

    /**
     * Test for {@link ResultSet#getClob(String)} method.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testReadBlob() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Comments FROM CUSTOMER")) {
            Assert.assertTrue("First record not exists", rs.next());
            Assert.assertNotNull("First comment is null", rs.getClob("comments"));
            Assert.assertEquals("Fields are not equals.", rs.getClob(1), rs.getClob("comments"));
            Assert.assertTrue("Second record not exists", rs.next());
            Assert.assertNotNull("Second comment is null", rs.getClob("comments"));
            Assert.assertEquals("2 row: Medium comment (about 500 symbols)", 518, rs.getClob("comments").length());
            Assert.assertTrue("Third record not exists", rs.next());
            Assert.assertNotNull("Third comment is null", rs.getClob("comments"));
            Assert.assertEquals("3 row: Medium comment (318 symbols)", 318, rs.getClob("comments").length());
            Assert.assertTrue("Fourth record not exists", rs.next());
            Assert.assertNotNull("Fourth comment is null", rs.getClob("comments"));
            Assert.assertEquals("4 row: Big comment (56864 symbols)", 56864, rs.getClob("comments").length());
            Assert.assertTrue("Five record not exists", rs.next());
            Assert.assertNotNull("Five comment is null", rs.getClob("comments"));
            Assert.assertEquals("5 row: Small comment (415 symbols)", 426, rs.getClob("comments").length());
        }
    }

    /**
     * Test for CLOB with cp1251 charset.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testReadBlob1251() throws SQLException {
        Properties properties = new Properties();
        properties.put("charset", "cp1251");

        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING + "db", properties);
             final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery("SELECT Note FROM NOTE1251 WHERE Id=2")) {

            Assert.assertTrue("Nation locale: record not exists", rs.next());
            final Clob c = rs.getClob("note");
            final String expected = "Удивительное устройство USB-флешки Kingston DataTraveler";
            final String real = c.getSubString(1, (int) c.length());
            Assert.assertEquals("Unexpected cp1251 text", expected, real);
        }
    }

    /**
     * Test for {@link Clob#getSubString(long, int)} method.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testSubString() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Comments FROM CUSTOMER")) {
            Assert.assertTrue("First record not exists", rs.next());

            final Clob clob = rs.getClob("comments");
            Assert.assertNotNull("First comment is null", rs.getClob("comments"));

            Assert.assertEquals("Testing for input stream value.", "Sma", clob.getSubString(1, 3));
        }
    }

    /**
     * Test for {@link Clob#getSubString(long, int)} method with high length.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testSubStringWithHighLength() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Comments FROM CUSTOMER")) {
            Assert.assertTrue("First record not exists", rs.next());

            final Clob clob = rs.getClob("comments");
            Assert.assertNotNull("First comment is null", rs.getClob("comments"));

            Assert.assertEquals("Invalid value", "Small comment (less 100 symbols)",
                    clob.getSubString(1, 100));
        }
    }

    /**
     * Test for {@link Clob#getSubString(long, int)} method with high position.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testSubStringWithHighPos() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Comments FROM CUSTOMER")) {
            Assert.assertTrue("First record not exists", rs.next());

            final Clob clob = rs.getClob("comments");
            Assert.assertNotNull("First comment is null", rs.getClob("comments"));

            Assert.assertTrue("Invalid clob string", clob.getSubString(100, 3).isEmpty());
        }
    }

    /**
     * Test for {@link Clob#getSubString(long, int)} method with invalid length.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testSubStringWithInvalidLength() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Comments FROM CUSTOMER")) {
            Assert.assertTrue("First record not exists", rs.next());

            final Clob clob = rs.getClob("comments");
            Assert.assertNotNull("First comment is null", rs.getClob("comments"));

            Assert.assertThrows("Invalid clob", SQLException.class, () -> clob.getSubString(1, -1));
        }
    }

    /**
     * Test for {@link Clob#getSubString(long, int)} method with low position.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testSubStringWithLowPos() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Comments FROM CUSTOMER")) {
            Assert.assertTrue("First record not exists", rs.next());

            final Clob clob = rs.getClob("comments");
            Assert.assertNotNull("First comment is null", rs.getClob("comments"));

            Assert.assertThrows("Invalid clob", SQLException.class, () -> clob.getSubString(0, 3));
        }
    }

    /**
     * Test for {@link Clob#truncate(long)} method.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testTruncate() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Comments FROM CUSTOMER")) {
            Assert.assertTrue("First record not exists", rs.next());

            final Clob clob = rs.getClob("comments");
            Assert.assertNotNull("First comment is null", rs.getClob("comments"));

            clob.truncate(3);
            Assert.assertEquals("Testing for truncate.", 3, clob.length());
        }
    }

    /**
     * Test for {@link Clob#truncate(long)} method with high value.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testTruncateHighValue() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Comments FROM CUSTOMER")) {
            Assert.assertTrue("First record not exists", rs.next());

            final Clob clob = rs.getClob("comments");
            Assert.assertNotNull("First comment is null", rs.getClob("comments"));

            final long value = clob.length();
            Assert.assertThrows("Invalid clob", SQLException.class, () -> clob.truncate(value + 100));
        }
    }

    /**
     * Test for {@link Clob#truncate(long)} method with zero size.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testTruncateWithZeroSize() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Comments FROM CUSTOMER")) {
            Assert.assertTrue("First record not exists", rs.next());

            final Clob clob = rs.getClob("comments");
            Assert.assertNotNull("First comment is null", rs.getClob("comments"));

            clob.truncate(0);
            Assert.assertEquals("Testing for truncate.", 0, clob.length());
        }
    }

    /**
     * Test for clob reading.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testClobReading() throws SQLException, IOException {
        try (final PreparedStatement stmt =
                     conn.prepareStatement("SELECT Comments FROM CUSTOMER WHERE CustNo = ?")) {
            stmt.setInt(1, 4);

            try (final ResultSet rs = stmt.executeQuery()) {
                Assert.assertTrue("First record not exists", rs.next());

                final Clob clob = rs.getClob("comments");
                final int size = 56864;
                Assert.assertEquals("Invalid clob size", size, clob.length());

                // String methods
                Assert.assertEquals("Invalid clob string size", size, rs.getString("Comments").length());
                Assert.assertEquals("Invalid clob string size", size, clob.getSubString(1, size).length());

                try (final Reader reader = clob.getCharacterStream(1, size)) {
                    char[] buffer = new char[size];
                    Assert.assertEquals("Invalid clob size", size, reader.read(buffer));
                }
            }
        }
    }
}
