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
import com.googlecode.paradox.parser.nodes.comparisons.EqualsNode;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit test {@link JoinNode} class.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.3
 */
public class JoinNodeTest {

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
     * Test conditions.
     */
    @Test
    public void testConditions() {
        final ArrayList<SQLNode> list = new ArrayList<>();
        final JoinNode node = new JoinNode(conn);
        node.setConditions(list);
        Assert.assertEquals(list, node.getConditions());
    }

    /**
     * Test for new instance.
     */
    @Test
    public void testInstance() {
        final JoinNode node = new JoinNode(conn);
        Assert.assertEquals("JOIN", node.getName());
    }

    /**
     * Test join type.
     */
    @Test
    public void testJoinType() {
        final JoinNode node = new JoinNode(conn);
        node.setType(JoinType.LEFT);
        Assert.assertEquals(JoinType.LEFT, node.getType());
    }

    /**
     * Test for table name.
     */
    @Test
    public void testName() {
        final JoinNode node = new JoinNode(conn);
        node.setTableName("name");
        Assert.assertEquals("name", node.getTableName());
    }

    /**
     * Test for {@link JoinNode#toString()} method.
     */
    @Test
    public void testToString() {
        final JoinNode node = new JoinNode(conn);
        node.setTableName("table");
        node.setAlias("alias");
        Assert.assertEquals("CROSS JOIN table AS alias", node.toString());
    }

    /**
     * Test for {@link JoinNode#toString()} method with conditions.
     */
    @Test
    public void testToStringWithConditions() {
        final JoinNode node = new JoinNode(conn);
        node.setTableName("table");
        final List<SQLNode> list = new ArrayList<>();
        list.add(new EqualsNode(conn, new FieldNode(conn, null, "a", null), new FieldNode(conn, null, "b", null)));
        node.setConditions(list);
        Assert.assertEquals("CROSS JOIN table ON a = b ", node.toString());
    }

    /**
     * Test for {@link JoinNode#toString()} method with no alias.
     */
    @Test
    public void testToStringWithoutAlias() {
        final JoinNode node = new JoinNode(conn);
        node.setTableName("table");
        node.setAlias("table");
        Assert.assertEquals("CROSS JOIN table", node.toString());
    }
}
