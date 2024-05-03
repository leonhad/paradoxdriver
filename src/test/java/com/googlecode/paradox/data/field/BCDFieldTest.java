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
package com.googlecode.paradox.data.field;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for BCD field.
 *
 * @since 1.5.0
 */
class BCDFieldTest {

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
     */
    @BeforeAll
    static void setUp() {
        new Driver();
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
        this.conn = (ParadoxConnection) DriverManager.getConnection(CONNECTION_STRING + "fields");
    }

    /**
     * Test for BCD reading.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testReadBCD() throws SQLException {
        try (Statement stmt = this.conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT A, B, C FROM fields.bcd")) {
            assertTrue(rs.next());
            assertEquals(1.23D, rs.getDouble("A"), 0.001D);
            assertEquals(1.0D, rs.getDouble("B"), 0.001D);
            assertEquals(0.123D, rs.getDouble("C"), 0.001D);

            assertTrue(rs.next());
            assertEquals(-1.23D, rs.getDouble("A"), 0.001D);
            assertEquals(-1.0D, rs.getDouble("B"), 0.001D);
            assertEquals(-0.123D, rs.getDouble("C"), 0.001D);

            assertTrue(rs.next());
            assertEquals(0.0D, rs.getDouble("A"), 0.001D);
            assertNull(rs.getObject("B"));
            assertEquals(0.9999D, rs.getDouble("C"), 0.001D);

            assertFalse(rs.next());
        }
    }

    /**
     * Test for BCD reading with skipping.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testReadBCDSkipping() throws SQLException {
        try (Statement stmt = this.conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT C FROM fields.bcd")) {
            assertTrue(rs.next());
            assertEquals(0.123D, rs.getDouble("C"), 0.001D);

            assertTrue(rs.next());
            assertEquals(-0.123D, rs.getDouble("C"), 0.001D);

            assertTrue(rs.next());
            assertEquals(0.9999D, rs.getDouble("C"), 0.001D);

            assertFalse(rs.next());
        }
    }

    /**
     * Test for BCD metadata.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testBCDMetadata() throws SQLException {
        try (final Statement stmt = this.conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT C FROM fields.bcd")) {

            final ResultSetMetaData metaData = rs.getMetaData();
            assertEquals("BCD", metaData.getColumnTypeName(1));
        }

        DatabaseMetaData databaseMetaData = conn.getMetaData();
        try (ResultSet rs = databaseMetaData.getColumns(null, "fields", "bcd", "%")) {
            while (rs.next()) {
                assertEquals("BCD", rs.getString("TYPE_NAME"));
            }
        }
    }

    /**
     * Test for BCD equals.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testEquals() throws SQLException {
        try (Statement stmt = this.conn.createStatement(); ResultSet rs = stmt.executeQuery("select c from fields.bcd where c = 0.999900000000000011810001400000141541")) {
            assertTrue(rs.next());
            assertEquals(1.0D, rs.getDouble("c"), 0.001D);
            assertFalse(rs.next());
        }
    }
}
