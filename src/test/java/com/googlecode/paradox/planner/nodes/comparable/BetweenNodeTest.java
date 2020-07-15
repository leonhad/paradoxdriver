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
package com.googlecode.paradox.planner.nodes.comparable;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.planner.nodes.FieldNode;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Unit test for {@link BetweenNode} class.
 *
 * @version 1.3
 * @since 1.3
 */
@SuppressWarnings({"java:S109", "java:S1192"})
public class BetweenNodeTest {

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
    @SuppressWarnings("java:S2115")
    public static void setUp() throws SQLException {
        new Driver();
        conn = (ParadoxConnection) DriverManager.getConnection(CONNECTION_STRING);
    }

    @AfterClass
    public static void tearDown() throws SQLException {
        conn.close();
    }

    /**
     * Test the field node.
     */
    @Test
    public void testField() {
        final FieldNode field = new FieldNode(conn, "table", "field", "alias", null);
        final FieldNode first = new FieldNode(conn, "table", "first", "first", null);
        final FieldNode last = new FieldNode(conn, "table", "last", "last", null);
        final BetweenNode node = new BetweenNode(conn, field, first, last, null);
        Assert.assertEquals("Invalid field value",
                "table.field AS alias BETWEEN table.first AND table.last", node.toString());
    }

    /**
     * Test for {@link BetweenNode#toString()} method.
     */
    @Test
    public void testToString() {
        final FieldNode field = new FieldNode(conn, "table", "field", "field", null);
        final FieldNode first = new FieldNode(conn, "table", "first", "first", null);
        final FieldNode last = new FieldNode(conn, "table", "last", "last", null);
        final BetweenNode node = new BetweenNode(conn, field, first, last, null);
        Assert.assertEquals("Invalid node values", "table.field BETWEEN table.first AND table.last", node.toString());
    }

    /**
     * Test for between condition.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testInsensitiveLike() throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("select \"date\" from fields.date7 " +
                     "where \"date\" between '2018-01-02' and '2018-01-02'")) {

            Assert.assertTrue("Invalid ResultSet state", rs.next());
            Assert.assertEquals("Invalid value", "2018-01-02", rs.getString("date"));
            Assert.assertFalse("Invalid ResultSet state", rs.next());
        }
    }
}
