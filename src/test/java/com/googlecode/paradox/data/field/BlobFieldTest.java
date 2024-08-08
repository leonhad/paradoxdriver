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
import com.googlecode.paradox.exceptions.ParadoxException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for blob fields.
 *
 * @since 1.6.0
 */
class BlobFieldTest {

    /**
     * The connection string used in  tests.
     */
    private static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/";

    /**
     * The database connection.
     */
    private static ParadoxConnection conn;

    /**
     * Register the database driver.
     *
     * @throws SQLException in case of failures.
     */
    @BeforeAll
    static void setUp() throws SQLException {
        new Driver();
        conn = (ParadoxConnection) DriverManager.getConnection(CONNECTION_STRING + "fields");
    }

    /**
     * Close the database connection.
     *
     * @throws SQLException in case of failures.
     */
    @AfterAll
    static void tearDown() throws SQLException {
        conn.close();
    }

    /**
     * Test for blob loading.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testBlobLoading() throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("select Graphic from fields.graphic where Id = 2")) {
            int size = 9626;
            assertTrue(rs.next());

            final Blob blob = rs.getBlob("Graphic");
            assertNotNull(blob);

            final Blob newBlob = conn.createBlob();
            final boolean equals = blob.equals(newBlob);
            assertFalse(equals);
            assertNotEquals(0, blob.hashCode());

            assertEquals(size, blob.length());
            assertEquals(size, blob.getBytes(1, size).length);

            try (InputStream is = blob.getBinaryStream()) {
                byte[] buffer = new byte[size];
                assertEquals(size, is.read(buffer));
                assertEquals(0, is.available());
                assertArrayEquals(buffer, blob.getBytes(1, size));
            } catch (final IOException e) {
                throw new SQLException(e);
            }

            int forcedSize = 10;
            try (InputStream is = blob.getBinaryStream(1, forcedSize)) {
                byte[] buffer = new byte[forcedSize];
                assertEquals(forcedSize, is.read(buffer));
                assertEquals(0, is.available());
                assertArrayEquals(buffer, blob.getBytes(1, forcedSize));
            } catch (final IOException e) {
                throw new SQLException(e);
            }

            blob.truncate(forcedSize);
            assertEquals(forcedSize, blob.length());

            blob.truncate(0);
            assertEquals(0, blob.length());

            assertThrows(ParadoxException.class, () -> blob.truncate(1));

            blob.free();
            assertEquals(0, blob.length());

            assertFalse(rs.next());
        }
    }

    /**
     * Test for big blob.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testBigBlob() throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("select Graphic from fields.graphic where Id = 3")) {
            int size = 2074686;
            assertTrue(rs.next());

            final Blob blob = rs.getBlob("Graphic");
            assertNotNull(blob);

            final Blob newBlob = conn.createBlob();
            final boolean equals = blob.equals(newBlob);
            assertFalse(equals);
            assertNotEquals(0, blob.hashCode());

            assertEquals(size, blob.length());
            assertEquals(size, blob.getBytes(1, size).length);

            try (InputStream is = blob.getBinaryStream()) {
                byte[] buffer = new byte[size];
                assertEquals(size, is.read(buffer));
                assertEquals(0, is.available());
                assertArrayEquals(buffer, blob.getBytes(1, size));
            } catch (final IOException e) {
                throw new SQLException(e);
            }

            int forcedSize = 10;
            try (InputStream is = blob.getBinaryStream(1, forcedSize)) {
                byte[] buffer = new byte[forcedSize];
                assertEquals(forcedSize, is.read(buffer));
                assertEquals(0, is.available());
                assertArrayEquals(buffer, blob.getBytes(1, forcedSize));
            } catch (final IOException e) {
                throw new SQLException(e);
            }

            blob.truncate(forcedSize);
            assertEquals(forcedSize, blob.length());

            blob.truncate(0);
            assertEquals(0, blob.length());

            assertThrows(ParadoxException.class, () -> blob.truncate(1));

            blob.free();
            assertEquals(0, blob.length());

            assertFalse(rs.next());
        }
    }

    /**
     * Test for blob searching.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testBlobSearching() throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("select Graphic from fields.graphic where Id = 2")) {
            int size = 9626;
            assertTrue(rs.next());

            final Blob blob = rs.getBlob("Graphic");
            assertEquals(size, blob.length());

            byte[] pattern = new byte[]{37, 0, 0, 0};
            assertEquals(4, blob.position(pattern, 1));
            assertEquals(4, blob.position(pattern, 4));

            final Blob created = conn.createBlob();
            created.setBytes(1, pattern);
            assertEquals(4, blob.position(pattern, 1));

            assertEquals(1, blob.position(blob, 1));
            assertEquals(-1, blob.position(blob, 2));

            assertThrows(SQLException.class, () -> blob.position(blob, 0));

            assertFalse(rs.next());
        }
    }

    /**
     * Test for blob output stream.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testBlobOutputStream() throws SQLException, IOException {
        final Blob blob = conn.createBlob();

        byte[] pattern = new byte[]{1, 2, 3, 4};
        try (OutputStream out = blob.setBinaryStream(1)) {
            out.write(pattern);
        }

        assertArrayEquals(pattern, blob.getBytes(1, (int) blob.length()));
    }
}
