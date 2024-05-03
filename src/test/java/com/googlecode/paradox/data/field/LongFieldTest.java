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
import com.googlecode.paradox.results.ParadoxType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for {@link LongField} class.
 *
 * @since 1.3
 */
class LongFieldTest {

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
     * Test for invalid match.
     */
    @Test
    void testInvalidMatch() {
        final LongField field = new LongField();
        assertFalse(field.match(ParadoxType.NULL));
    }

    /**
     * Test for parse method.
     */
    @Test
    void testParse() {
        final LongField field = new LongField();

        // Test positive values
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{(byte) 0x80, (byte) 0x00, (byte) 0x01, (byte) 0x00});
        Object value = field.parse(null, buffer, null);
        assertEquals(256L, value);

        // Test negative values
        buffer = ByteBuffer.wrap(new byte[]{(byte) 0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0x00});
        value = field.parse(null, buffer, null);
        assertEquals(-256L, value);
    }

    /**
     * Test for valid match.
     */
    @Test
    void testValidMatch() {
        final LongField field = new LongField();
        assertTrue(field.match(ParadoxType.LONG));
    }

    /**
     * Test for LONG reading.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testReadLong() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT \"Time\" FROM fields.time")) {
            assertTrue(rs.next());
            assertEquals("01:00:01", rs.getTime("Time").toString());

            assertTrue(rs.next());
            assertNull(rs.getTime("Time"));

            assertTrue(rs.next());
            assertEquals("03:00:03", rs.getTime("Time").toString());

            assertFalse(rs.next());
        }
    }
}
