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
import com.googlecode.paradox.parser.nodes.comparisons.NotEqualsNode;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Unit test for {@link SelectNode}.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.3
 */
public class SelectNodeTest {

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
     * Test for condition list.
     */
    @Test
    public void testConditionList() {
        final SelectNode node = new SelectNode(conn);
        Assert.assertEquals("List not empty.", 0, node.getConditions().size());
        node.setConditions(new ArrayList<>());
        Assert.assertEquals("Invalid node size.", 0, node.getConditions().size());
    }

    /**
     * Test for distinct flag.
     */
    @Test
    public void testDistinctFlag() {
        final SelectNode node = new SelectNode(conn);
        Assert.assertFalse("Invalid node value.", node.isDistinct());
        node.setDistinct(true);
        Assert.assertTrue("Invalid node value.", node.isDistinct());
    }

    /**
     * Test for fields.
     */
    @Test
    public void testFields() {
        final SelectNode node = new SelectNode(conn);
        final FieldNode field = new FieldNode(conn, "table", "field", null);
        Assert.assertEquals("Invalid node size.", 0, node.getFields().size());
        node.addField(field);
        Assert.assertEquals("Invalid node size.", 1, node.getFields().size());
    }

    /**
     * Test for group by.
     */
    @Test
    public void testGroupBy() {
        final SelectNode node = new SelectNode(conn);
        final IdentifierNode identifier = new IdentifierNode(conn, "Node");
        Assert.assertEquals("Invalid node size.", 0, node.getGroups().size());
        node.addGroupBy(identifier);
        Assert.assertEquals("Invalid node size.", 1, node.getGroups().size());
    }

    /**
     * Test for order by.
     */
    @Test
    public void testOrderBy() {
        final SelectNode node = new SelectNode(conn);
        final IdentifierNode identifier = new IdentifierNode(conn, "Node");
        Assert.assertEquals("Invalid node size.", 0, node.getOrder().size());
        node.addOrderBy(identifier);
        Assert.assertEquals("Invalid node size.", 1, node.getOrder().size());
    }

    /**
     * Test for tables.
     */
    @Test
    public void testTables() {
        final SelectNode node = new SelectNode(conn);
        final TableNode table = new TableNode(conn, null, "table", null);
        Assert.assertEquals("Invalid node size.", 0, node.getTables().size());
        node.addTable(table);
        Assert.assertEquals("Invalid node size.", 1, node.getTables().size());
    }

    /**
     * Test for {@link SelectNode#toString()} method.
     */
    @Test
    public void testToString() {
        final SelectNode node = new SelectNode(conn);
        node.addField(new FieldNode(conn, "t", "field", "f"));
        node.addField(new FieldNode(conn, "b", "field2", "f2"));
        node.addTable(new TableNode(conn, null, "table1", "t"));
        node.addTable(new TableNode(conn, null, "table2", "b"));
        node.addGroupBy(new IdentifierNode(conn, "f1"));
        node.addGroupBy(new IdentifierNode(conn, "f2"));
        node.addOrderBy(new IdentifierNode(conn, "f"));
        node.addOrderBy(new IdentifierNode(conn, "f2"));

        final ArrayList<SQLNode> conditions = new ArrayList<>();
        conditions.add(new EqualsNode(conn, new FieldNode(conn, "t", "field", null), new FieldNode(conn, "t", "field2"
                , null)));
        conditions.add(new NotEqualsNode(conn, new FieldNode(conn, "t", "field", null), new FieldNode(conn, "t",
                "field2", null)));
        node.setConditions(conditions);

        Assert.assertEquals("Invalid node value.",
                "SELECT t.field AS f, b.field2 AS f2 FROM table1 AS t, table2 AS b " +
                        "WHERE t.field = t.field2 t.field <> t.field2 GROUP BY f1, f2 ORDER BY f, f2",
                node.toString());
    }

    /**
     * Test for {@link SelectNode#toString()} method with empty where.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testToStringEmptyWhere() {
        final SelectNode node = new SelectNode(conn);
        node.addField(new FieldNode(conn, "t", "field", "f"));
        node.addField(new FieldNode(conn, "b", "field2", "f2"));
        node.setConditions(Collections.EMPTY_LIST);
        Assert.assertEquals("Invalid node value.", "SELECT t.field AS f, b.field2 AS f2", node.toString());
    }

    /**
     * Test for {@link SelectNode#toString()} method with fields.
     */
    @Test
    public void testToStringFields() {
        final SelectNode node = new SelectNode(conn);
        node.addField(new FieldNode(conn, "t", "field", "f"));
        node.addField(new FieldNode(conn, "b", "field2", "f2"));
        Assert.assertEquals("Invalid node value.", "SELECT t.field AS f, b.field2 AS f2", node.toString());
    }
}
