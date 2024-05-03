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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for Paradox Prepared Statement.
 *
 * @since 1.6.0
 */
class ParadoxPreparedStatementTest {

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
    @BeforeAll
    static void setUp() throws Exception {
        Class.forName(Driver.class.getName());
    }

    /**
     * Close the test connection.
     *
     * @throws Exception in case of failures.
     */
    @AfterEach
    void closeConnection() throws Exception {
        if (this.conn != null) {
            this.conn.close();
        }
    }

    /**
     * Connect to the test database.
     *
     * @throws Exception in case of failures.
     */
    @BeforeEach
    void connect() throws Exception {
        this.conn = DriverManager.getConnection(CONNECTION_STRING);
    }

    /**
     * Test for simple prepared statement.
     */
    @Test
    void testAbsoluteEmpty() throws SQLException {
        try (final PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM db.DECIMAL");
             final ResultSet rs = preparedStatement.executeQuery()) {
            assertTrue(rs.next());
        }
    }

    /**
     * Test for prepared statement with parameters.
     */
    @Test
    void testWithParameters() throws SQLException {
        try (final PreparedStatement preparedStatement = conn.prepareStatement("select * from geog.tblAC ac " +
                " where ac.State = ? and ? = ac.AreaCode")) {
            preparedStatement.setString(1, "NJ");
            preparedStatement.setInt(2, 201);

            try (final ResultSet rs = preparedStatement.executeQuery()) {
                assertTrue(rs.next());

                assertEquals("201", rs.getString("AreaCode"));
                assertEquals("NJ", rs.getString("State"));
                assertNull(rs.getObject("Effective"));

                assertFalse(rs.next());
            }
        }
    }

    /**
     * Test for set scale.
     */
    @Test
    void testSetScale() throws SQLException {
        try (final PreparedStatement preparedStatement = conn.prepareStatement("select ?")) {
            preparedStatement.setObject(1, 12.012345, Types.NUMERIC, 2);

            try (final ResultSet rs = preparedStatement.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(12.01D, rs.getDouble(1), 0.0001D);
                assertFalse(rs.next());
            }
        }
    }

    /**
     * Test for set length with reader.
     */
    @Test
    void testSetLengthWithReader() throws SQLException {
        final StringReader reader = new StringReader("test");

        try (final PreparedStatement preparedStatement = conn.prepareStatement("select ?")) {
            preparedStatement.setObject(1, reader, Types.VARCHAR, 2);

            try (final ResultSet rs = preparedStatement.executeQuery()) {
                assertTrue(rs.next());
                assertEquals("te", rs.getString(1));
                assertFalse(rs.next());
            }
        }
    }

    /**
     * Test for set length with inputStream.
     */
    @Test
    void testSetLengthWithInputStream() throws SQLException {
        final ByteArrayInputStream is = new ByteArrayInputStream("test".getBytes(StandardCharsets.UTF_8));

        try (final PreparedStatement preparedStatement = conn.prepareStatement("select ?")) {
            preparedStatement.setObject(1, is, Types.VARCHAR, 2);

            try (final ResultSet rs = preparedStatement.executeQuery()) {
                assertTrue(rs.next());
                assertEquals("te", rs.getString(1));
                assertFalse(rs.next());
            }
        }
    }
}
