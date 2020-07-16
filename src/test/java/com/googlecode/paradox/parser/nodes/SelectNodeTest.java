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

import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.planner.nodes.ValueNode;
import com.googlecode.paradox.planner.nodes.comparable.EqualsNode;
import com.googlecode.paradox.planner.nodes.comparable.NotEqualsNode;
import com.googlecode.paradox.planner.nodes.join.ANDNode;
import com.googlecode.paradox.planner.sorting.OrderType;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Types;

/**
 * Unit test for {@link SelectNode}.
 *
 * @version 1.3
 * @since 1.3
 */
@SuppressWarnings({"java:S109", "java:S1192"})
public class SelectNodeTest {

    /**
     * Test for condition.
     */
    @Test
    public void testCondition() {
        final SelectNode node = new SelectNode(null);
        Assert.assertNull("List not empty.", node.getCondition());
    }

    /**
     * Test for distinct flag.
     */
    @Test
    public void testDistinctFlag() {
        final SelectNode node = new SelectNode(null);
        Assert.assertFalse("Invalid node value.", node.isDistinct());
        node.setDistinct(true);
        Assert.assertTrue("Invalid node value.", node.isDistinct());
    }

    /**
     * Test for fields.
     */
    @Test
    public void testFields() {
        final SelectNode node = new SelectNode(null);
        final FieldNode field = new FieldNode("table", "field", null, null);
        Assert.assertEquals("Invalid node size.", 0, node.getFields().size());
        node.addField(field);
        Assert.assertEquals("Invalid node size.", 1, node.getFields().size());
    }

    /**
     * Test for group by.
     */
    @Test
    public void testGroupBy() {
        final SelectNode node = new SelectNode(null);
        final IdentifierNode identifier = new IdentifierNode("Node", null);
        Assert.assertEquals("Invalid node size.", 0, node.getGroups().size());
        node.addGroupBy(identifier);
        Assert.assertEquals("Invalid node size.", 1, node.getGroups().size());
    }

    /**
     * Test for order by.
     */
    @Test
    public void testOrderBy() {
        final SelectNode node = new SelectNode(null);
        final ValueNode value = new ValueNode("1", null, null, Types.NUMERIC);
        Assert.assertEquals("Invalid node size.", 0, node.getOrder().size());
        node.addOrderBy(value, OrderType.ASC);
        Assert.assertEquals("Invalid node size.", 1, node.getOrder().size());
    }

    /**
     * Test for tables.
     */
    @Test
    public void testTables() {
        final SelectNode node = new SelectNode(null);
        final TableNode table = new TableNode(null, "table", null, null);
        Assert.assertEquals("Invalid node size.", 0, node.getTables().size());
        node.addTable(table);
        Assert.assertEquals("Invalid node size.", 1, node.getTables().size());
    }

    /**
     * Test for {@link SelectNode#toString()} method.
     */
    @Test
    public void testToString() {
        final SelectNode node = new SelectNode(null);
        node.addField(new FieldNode("t", "field", "f", null));
        node.addField(new FieldNode("b", "field2", "f2", null));
        node.addTable(new TableNode(null, "table1", "t", null));
        node.addTable(new TableNode(null, "table2", "b", null));
        node.addGroupBy(new IdentifierNode("f1", null));
        node.addGroupBy(new IdentifierNode("f2", null));
        node.addOrderBy(new ValueNode("f", null, null, Types.INTEGER), OrderType.ASC);
        node.addOrderBy(new ValueNode("f2", null, null, Types.INTEGER), OrderType.ASC);

        ANDNode andNode = new ANDNode(null, null);
        andNode.addChild(new EqualsNode(
                new FieldNode("t", "field", null, null),
                new FieldNode("t", "field2", null, null), null));
        andNode.addChild(new NotEqualsNode(
                new FieldNode("t", "field", null, null),
                new FieldNode("t", "field2", null, null), null));
        node.setCondition(andNode);

        Assert.assertEquals("Invalid node value.",
                "SELECT t.field AS f, b.field2 AS f2 FROM table1 AS t, table2 AS b " +
                        "WHERE t.field = t.field2 AND t.field <> t.field2 GROUP BY f1, f2 ORDER BY f, f2",
                node.toString());
    }

    /**
     * Test for {@link SelectNode#toString()} method with empty where.
     */
    @Test
    public void testToStringEmptyWhere() {
        final SelectNode node = new SelectNode(null);
        node.addField(new FieldNode("t", "field", "f", null));
        node.addField(new FieldNode("b", "field2", "f2", null));
        node.setCondition(null);
        Assert.assertEquals("Invalid node value.", "SELECT t.field AS f, b.field2 AS f2", node.toString());
    }

    /**
     * Test for {@link SelectNode#toString()} method with fields.
     */
    @Test
    public void testToStringFields() {
        final SelectNode node = new SelectNode(null);
        node.addField(new FieldNode("t", "field", "f", null));
        node.addField(new FieldNode("b", "field2", "f2", null));
        Assert.assertEquals("Invalid node value.", "SELECT t.field AS f, b.field2 AS f2", node.toString());
    }
}
