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
package com.googlecode.paradox.data.field;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.exceptions.ParadoxException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.*;

/**
 * Unit test for blob fields.
 *
 * @version 1.0
 * @since 1.6.0
 */
@SuppressWarnings({"java:S109", "java:S1192"})
public class BlobFieldTest {

    /**
     * The connection string used in this tests.
     */
    public static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/";

    /**
     * The database connection.
     */
    private static ParadoxConnection conn;

    /**
     * Register the database driver.
     *
     * @throws SQLException in case of failures.
     */
    @BeforeClass
    @SuppressWarnings("java:S2115")
    public static void setUp() throws SQLException {
        new Driver();
        conn = (ParadoxConnection) DriverManager.getConnection(CONNECTION_STRING + "fields");
    }

    /**
     * Close the database connection.
     *
     * @throws SQLException in case of failures.
     */
    @AfterClass
    public static void tearDown() throws SQLException {
        conn.close();
    }

    /**
     * Test for blob loading.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testBlobLoading() throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("select Graphic from fields.graphic where Id = 2")) {
            int size = 9626;
            Assert.assertTrue("Invalid ResultSet state", rs.next());

            final Blob blob = rs.getBlob("Graphic");
            Assert.assertNotNull("Invalid blob value", blob);

            final Blob newBlob = conn.createBlob();
            final boolean equals = blob.equals(newBlob);
            Assert.assertFalse("Invalid blob equals", equals);
            Assert.assertNotEquals("Invalid hash code", 0, blob.hashCode());

            Assert.assertEquals("Invalid value", size, blob.length());
            Assert.assertEquals("Invalid value", size, blob.getBytes(1, size).length);

            try (InputStream is = blob.getBinaryStream()) {
                byte[] buffer = new byte[size];
                Assert.assertEquals("Invalid stream size", size, is.read(buffer));
                Assert.assertEquals("Invalid stream state", 0, is.available());
                Assert.assertArrayEquals("Invalid binary stream", buffer, blob.getBytes(1, size));
            } catch (final IOException e) {
                throw new SQLException(e);
            }

            int forcedSize = 10;
            try (InputStream is = blob.getBinaryStream(1, forcedSize)) {
                byte[] buffer = new byte[forcedSize];
                Assert.assertEquals("Invalid stream size", forcedSize, is.read(buffer));
                Assert.assertEquals("Invalid stream state", 0, is.available());
                Assert.assertArrayEquals("Invalid binary stream", buffer, blob.getBytes(1, forcedSize));
            } catch (final IOException e) {
                throw new SQLException(e);
            }

            blob.truncate(forcedSize);
            Assert.assertEquals("Invalid stream size", forcedSize, blob.length());

            blob.truncate(0);
            Assert.assertEquals("Invalid stream size", 0, blob.length());

            Assert.assertThrows("Invalid truncate blob", ParadoxException.class,
                    () -> blob.truncate(1));

            blob.free();
            Assert.assertEquals("Invalid value", 0, blob.length());

            Assert.assertFalse("Invalid ResultSet state", rs.next());
        }
    }

    /**
     * Test for big blob.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testBigBlob() throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("select Graphic from fields.graphic where Id = 3")) {
            int size = 2074686;
            Assert.assertTrue("Invalid ResultSet state", rs.next());

            final Blob blob = rs.getBlob("Graphic");
            Assert.assertNotNull("Invalid blob value", blob);

            final Blob newBlob = conn.createBlob();
            final boolean equals = blob.equals(newBlob);
            Assert.assertFalse("Invalid blob equals", equals);
            Assert.assertNotEquals("Invalid hash code", 0, blob.hashCode());

            Assert.assertEquals("Invalid value", size, blob.length());
            Assert.assertEquals("Invalid value", size, blob.getBytes(1, size).length);

            try (InputStream is = blob.getBinaryStream()) {
                byte[] buffer = new byte[size];
                Assert.assertEquals("Invalid stream size", size, is.read(buffer));
                Assert.assertEquals("Invalid stream state", 0, is.available());
                Assert.assertArrayEquals("Invalid binary stream", buffer, blob.getBytes(1, size));
            } catch (final IOException e) {
                throw new SQLException(e);
            }

            int forcedSize = 10;
            try (InputStream is = blob.getBinaryStream(1, forcedSize)) {
                byte[] buffer = new byte[forcedSize];
                Assert.assertEquals("Invalid stream size", forcedSize, is.read(buffer));
                Assert.assertEquals("Invalid stream state", 0, is.available());
                Assert.assertArrayEquals("Invalid binary stream", buffer, blob.getBytes(1, forcedSize));
            } catch (final IOException e) {
                throw new SQLException(e);
            }

            blob.truncate(forcedSize);
            Assert.assertEquals("Invalid stream size", forcedSize, blob.length());

            blob.truncate(0);
            Assert.assertEquals("Invalid stream size", 0, blob.length());

            Assert.assertThrows("Invalid truncate blob", ParadoxException.class,
                    () -> blob.truncate(1));

            blob.free();
            Assert.assertEquals("Invalid value", 0, blob.length());

            Assert.assertFalse("Invalid ResultSet state", rs.next());
        }
    }

    /**
     * Test for blob searching.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testBlobSearching() throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("select Graphic from fields.graphic where Id = 2")) {
            int size = 9626;
            Assert.assertTrue("Invalid ResultSet state", rs.next());

            final Blob blob = rs.getBlob("Graphic");
            Assert.assertEquals("Invalid blob value", size, blob.length());

            byte[] pattern = new byte[]{37, 0, 0, 0};
            Assert.assertEquals("Invalid blob value", 4, blob.position(pattern, 1));
            Assert.assertEquals("Invalid blob value", 4, blob.position(pattern, 4));

            final Blob created = conn.createBlob();
            created.setBytes(1, pattern);
            Assert.assertEquals("Invalid blob value", 4, blob.position(pattern, 1));

            Assert.assertEquals("Invalid blob value", 1, blob.position(blob, 1));
            Assert.assertEquals("Invalid blob value", -1, blob.position(blob, 2));

            Assert.assertThrows("Invalid position", SQLException.class, () -> blob.position(blob, 0));

            Assert.assertFalse("Invalid ResultSet state", rs.next());
        }
    }

    /**
     * Test for blob output stream.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testBlobOutputStream() throws SQLException, IOException {
        final Blob blob = conn.createBlob();

        byte[] pattern = new byte[]{1, 2, 3, 4};
        try (OutputStream out = blob.setBinaryStream(1)) {
            out.write(pattern);
        }

        Assert.assertArrayEquals("Invalid values written", pattern, blob.getBytes(1, (int) blob.length()));
    }
}
