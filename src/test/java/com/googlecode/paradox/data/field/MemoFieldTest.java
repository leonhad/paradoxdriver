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

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for MEMO field.
 *
 * @since 1.5.0
 */
class MemoFieldTest {

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
     * Test for memo reading.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testReadMemo() throws SQLException {
        try (Statement stmt = this.conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT Id, MEMO FROM fields.memo")) {
            assertTrue(rs.next());
            assertEquals(1, rs.getInt("Id"));
            assertEquals(555, rs.getString("MEMO").length());

            assertTrue(rs.next());
            assertEquals(2, rs.getInt("Id"));
            assertEquals("01234567890\n", rs.getString("MEMO"));

            assertFalse(rs.next());
        }
    }

    /**
     * Test for formatted memo reading.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testReadFormattedMemo() throws SQLException {
        try (Statement stmt = this.conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT Id, FMEMO FROM fields.fmemo")) {
            assertTrue(rs.next());
            assertEquals(1, rs.getInt("Id"));
            assertEquals(169, rs.getString("FMEMO").length());

            assertTrue(rs.next());
            assertEquals(2, rs.getInt("Id"));
            assertEquals(726, rs.getString("FMEMO").length());

            assertFalse(rs.next());
        }
    }
}
