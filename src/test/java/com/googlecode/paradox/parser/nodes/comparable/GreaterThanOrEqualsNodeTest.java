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
package com.googlecode.paradox.parser.nodes.comparable;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.parser.nodes.FieldNode;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Unit test for {@link GreaterThanOrEqualsNode} class.
 *
 * @author Leonardo Costa
 * @version 1.0
 * @since 1.6.0
 */
public class GreaterThanOrEqualsNodeTest {
    /**
     * The connection string used in this tests.
     */
    public static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/db";

    private static ParadoxConnection conn;

    /**
     * Register the database driver.
     *
     * @throws SQLException in case of failures.
     */
    @BeforeClass
    public static void setUp() throws SQLException {
        new Driver();
        conn = (ParadoxConnection) DriverManager.getConnection(CONNECTION_STRING);
    }

    @AfterClass
    public static void tearDown() throws SQLException {
        conn.close();
    }

    /**
     * Test for {@link BetweenNode#toString()} method.
     */
    @Test
    public void testToString() {
        final FieldNode first = new FieldNode(conn, "table", "first", "first");
        final FieldNode last = new FieldNode(conn, "table", "last", "last");
        final GreaterThanOrEqualsNode node = new GreaterThanOrEqualsNode(conn, first, last);
        Assert.assertEquals("Invalid node value.", "table.first >= table.last", node.toString());
    }

    /**
     * Test for {@link ResultSet} execution.
     *
     * @throws Exception in case of failures.
     */
    @Test
    public void testResultSet() throws Exception {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT B FROM fields.bcd where B >= -1")) {
            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid value.", 1, rs.getInt("B"));
            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid value", -1, rs.getInt("B"));
            Assert.assertFalse("Invalid ResultSet state.", rs.next());
        }
    }
}