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

package com.googlecode.paradox.data;

import com.googlecode.paradox.Driver;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.*;

public class EncryptedDataTest {

    /**
     * The connection string used in this tests.
     */
    public static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/";

    /**
     * Register the database driver.
     *
     * @throws ClassNotFoundException in case of failures.
     */
    @BeforeClass
    public static void setUp() throws ClassNotFoundException {
        Class.forName(Driver.class.getName());
    }

    @Test
    public void testExcrypted() throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING + "encrypt");
             final Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM encrypted")) {

            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid id value", 1, rs.getInt("Id"));
            Assert.assertEquals("Invalid id value", "Value 1", rs.getString("Text"));

            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid id value", 2, rs.getInt("Id"));
            Assert.assertEquals("Invalid id value", "Value 2", rs.getString("Text"));

            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid id value", 3, rs.getInt("Id"));
            Assert.assertEquals("Invalid id value", "Value 3", rs.getString("Text"));

            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid id value", 4, rs.getInt("Id"));
            Assert.assertEquals("Invalid id value", "Last one", rs.getString("Text"));

            Assert.assertFalse("Invalid ResultSet state.", rs.next());
        }
    }

    @Test
    public void testExcrypted35() throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING + "encrypt");
             final Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM encrypt.encrypted35")) {

            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid id value", 1.0D, rs.getDouble("A"), 0.0001D);
            Assert.assertEquals("Invalid id value", "Test 1", rs.getString("B"));

            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid id value", 2.0D, rs.getInt("A"), 0.0001D);
            Assert.assertEquals("Invalid id value", "Test2", rs.getString("B"));

            Assert.assertFalse("Invalid ResultSet state.", rs.next());
        }
    }
}