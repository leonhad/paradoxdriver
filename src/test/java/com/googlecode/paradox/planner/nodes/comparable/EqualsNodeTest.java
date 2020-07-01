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
import java.sql.SQLException;

/**
 * Unit test for {@link EqualsNode} class.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.3
 */
public class EqualsNodeTest {
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
        final EqualsNode node = new EqualsNode(conn, first, last);
        Assert.assertEquals("Invalid node value.", "table.first = table.last", node.toString());
    }
}
