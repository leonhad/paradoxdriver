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

import org.junit.*;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;

/**
 * Unit test for Paradox Prepared Statement.
 *
 * @version 1.1
 * @since 1.6.0
 */
public class ParadoxPreparedStatementTest {

    /**
     * The connection string used in this tests.
     */
    private static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/db";

    /**
     * The database connection.
     */
    private Connection conn;

    /**
     * Register the database driver.
     *
     * @throws Exception in case of failures.
     */
    @BeforeClass
    public static void setUp() throws Exception {
        Class.forName(Driver.class.getName());
    }

    /**
     * Close the test connection.
     *
     * @throws Exception in case of failures.
     */
    @After
    public void closeConnection() throws Exception {
        if (this.conn != null) {
            this.conn.close();
        }
    }

    /**
     * Connect to the test database.
     *
     * @throws Exception in case of failures.
     */
    @Before
    public void connect() throws Exception {
        this.conn = DriverManager.getConnection(CONNECTION_STRING);
    }

    /**
     * Test for simple prepared statement.
     */
    @Test
    public void testAbsoluteEmpty() throws SQLException {
        try (final PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM db.DECIMAL");
             final ResultSet rs = preparedStatement.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());
        }
    }

    /**
     * Test for prepared statement with parameters.
     */
    @Test
    public void testWithParameters() throws SQLException {
        try (final PreparedStatement preparedStatement = conn.prepareStatement("select * from geog.tblAC ac " +
                " where ac.State = ? and ? = ac.AreaCode")) {
            preparedStatement.setString(1, "NJ");
            preparedStatement.setInt(2, 201);

            try (final ResultSet rs = preparedStatement.executeQuery()) {
                Assert.assertTrue("Invalid result set state", rs.next());

                Assert.assertEquals("Invalid value", "201", rs.getString("AreaCode"));
                Assert.assertEquals("Invalid value", "NJ", rs.getString("State"));
                Assert.assertNull("Invalid value", rs.getObject("Effective"));

                Assert.assertFalse("Invalid result set state", rs.next());
            }
        }
    }

    /**
     * Test for set scale.
     */
    @Test
    public void testSetScale() throws SQLException {
        try (final PreparedStatement preparedStatement = conn.prepareStatement("select ?")) {
            preparedStatement.setObject(1, 12.012345, Types.NUMERIC, 2);

            try (final ResultSet rs = preparedStatement.executeQuery()) {
                Assert.assertTrue("Invalid result set state", rs.next());
                Assert.assertEquals("Invalid value", 12.01D, rs.getDouble(1), 0.0001D);
                Assert.assertFalse("Invalid result set state", rs.next());
            }
        }
    }

    /**
     * Test for set length with reader.
     */
    @Test
    public void testSetLengthWithReader() throws SQLException {
        final StringReader reader = new StringReader("test");

        try (final PreparedStatement preparedStatement = conn.prepareStatement("select ?")) {
            preparedStatement.setObject(1, reader, Types.VARCHAR, 2);

            try (final ResultSet rs = preparedStatement.executeQuery()) {
                Assert.assertTrue("Invalid result set state", rs.next());
                Assert.assertEquals("Invalid value", "te", rs.getString(1));
                Assert.assertFalse("Invalid result set state", rs.next());
            }
        }
    }

    /**
     * Test for set length with inputStream.
     */
    @Test
    public void testSetLengthWithInputStream() throws SQLException {
        final ByteArrayInputStream is = new ByteArrayInputStream("test".getBytes(StandardCharsets.UTF_8));

        try (final PreparedStatement preparedStatement = conn.prepareStatement("select ?")) {
            preparedStatement.setObject(1, is, Types.VARCHAR, 2);

            try (final ResultSet rs = preparedStatement.executeQuery()) {
                Assert.assertTrue("Invalid result set state", rs.next());
                Assert.assertEquals("Invalid value", "te", rs.getString(1));
                Assert.assertFalse("Invalid result set state", rs.next());
            }
        }
    }
}