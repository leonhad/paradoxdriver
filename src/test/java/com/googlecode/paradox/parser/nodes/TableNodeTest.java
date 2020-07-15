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
package com.googlecode.paradox.parser.nodes;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.planner.nodes.comparable.EqualsNode;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Unit test for {@link TableNode} class.
 *
 * @version 1.3
 * @since 1.3
 */
public class TableNodeTest {

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
     * Test for instance.
     */
    @Test
    public void testInstance() {
        final TableNode node = new TableNode(conn, null, "table.db", "alias", null);
        Assert.assertEquals("Invalid table name.", "table", node.getName());
        Assert.assertEquals("Invalid table alias.", "alias", node.getAlias());
    }

    /**
     * Test for {@link TableNode#toString()} method.
     */
    @Test
    public void testToString() {
        final JoinNode join = new JoinNode(conn, null, "table.db", "alias", JoinType.INNER, null);
        final FieldNode fieldA = new FieldNode(conn, null, "a", null, null);
        final FieldNode fieldB = new FieldNode(conn, null, "b", null, null);

        join.setCondition(new EqualsNode(conn, fieldA, fieldB, null));

        Assert.assertEquals("Invalid JoinNode for toString().", "INNER JOIN table AS alias ON a = b ",
                join.toString());
    }
}
