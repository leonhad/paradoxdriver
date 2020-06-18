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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.*;

public class MainTest {

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
    public void testAcq() throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:paradox:/Users/leonardo");
             final Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Acq") ) {
            while(rs.next()) {
                System.out.println(rs.getString(1));
            }
        }
    }

    /**
     * Test the data and time.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testDateTime() throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING + "date");
             final Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT \"DATE\", \"TIME\" FROM DATE7")) {

            Assert.assertTrue("Invalid row state.", rs.next());
            Assert.assertEquals("Invalid time.", "10:00:00", rs.getTime("TIME").toString());
            Assert.assertEquals("Invalid date.", "2018-01-01", rs.getDate("DATE").toString());

            Assert.assertTrue("Invalid row state.", rs.next());
            Assert.assertEquals("Invalid time.", "10:30:00", rs.getTime("TIME").toString());
            Assert.assertEquals("Invalid date.", "2018-02-01", rs.getDate("DATE").toString());

            Assert.assertTrue("Invalid row state.", rs.next());
            Assert.assertEquals("Invalid time.", "09:25:25", rs.getTime("TIME").toString());
            Assert.assertEquals("Invalid date.", "2018-01-02", rs.getDate("DATE").toString());

            Assert.assertTrue("Invalid row state.", rs.next());
            Assert.assertEquals("Invalid time.", "10:00:00", rs.getTime("TIME").toString());
            Assert.assertNull("Invalid date.", rs.getDate("DATE"));

            Assert.assertTrue("Invalid row state.", rs.next());
            Assert.assertNull("Invalid time.", rs.getTime("TIME"));
            Assert.assertEquals("Invalid date.", "2018-01-01", rs.getDate("DATE").toString());

            Assert.assertFalse("Invalid row state.", rs.next());
        }
    }
}
