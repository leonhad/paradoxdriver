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
package com.googlecode.paradox.data;

import com.googlecode.paradox.Driver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for encrypted data.
 *
 * @since 1.5.0
 */
class EncryptedDataTest {

    /**
     * The connection string used in  tests.
     */
    private static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/";

    /**
     * Register the database driver.
     *
     * @throws ClassNotFoundException in case of failures.
     */
    @BeforeAll
    static void setUp() throws ClassNotFoundException {
        Class.forName(Driver.class.getName());
    }

    @Test
    void testEncrypted() throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING + "encrypt");
             final Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM encrypt.encrypted")) {

            assertTrue(rs.next());
            assertEquals(1, rs.getInt("Id"));
            assertEquals("Value 1", rs.getString("Text"));

            assertTrue(rs.next());
            assertEquals(2, rs.getInt("Id"));
            assertEquals("Value 2", rs.getString("Text"));

            assertTrue(rs.next());
            assertEquals(3, rs.getInt("Id"));
            assertEquals("Value 3", rs.getString("Text"));

            assertTrue(rs.next());
            assertEquals(4, rs.getInt("Id"));
            assertEquals("Last one", rs.getString("Text"));

            assertFalse(rs.next());
        }
    }

    @Test
    void testEncrypted35() throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING + "encrypt");
             final Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM encrypt.encrypted35")) {

            assertTrue(rs.next());
            assertEquals(1.0D, rs.getDouble("A"), 0.0001D);
            assertEquals("Test 1", rs.getString("B"));

            assertTrue(rs.next());
            assertEquals(2.0D, rs.getInt("A"), 0.0001D);
            assertEquals("Test2", rs.getString("B"));

            assertFalse(rs.next());
        }
    }

    /**
     * Test for encrypted blob.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testEncryptedBlob() throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING + "encrypt");
             final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery("select TEXT from encrypt.encryptedmemo")) {
            assertTrue(rs.next());
            assertNotNull(rs.getBlob("TEXT"));
            assertTrue(rs.next());
            assertNotNull(rs.getBlob("TEXT"));
            assertFalse(rs.next());
        }
    }
}
