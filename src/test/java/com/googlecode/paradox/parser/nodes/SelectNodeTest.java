/*
 * SelectNodeTest.java
 *
 * 03/12/2009
 * Copyright (C) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.parser.nodes;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import com.googlecode.paradox.parser.nodes.comparisons.EqualsNode;
import com.googlecode.paradox.parser.nodes.comparisons.NotEqualsNode;

/**
 * Unit test for {@link SelectNode}.
 * 
 * @author Leonardo Alves da Costa
 * @since 1.3
 * @version 1.0
 */
public class SelectNodeTest {

    /**
     * Test for condition list.
     */
    @Test
    public void testConditionList() {
        final SelectNode node = new SelectNode();
        Assert.assertNull(node.getConditions());
        node.setConditions(new ArrayList<SQLNode>());
        Assert.assertEquals(0, node.getConditions().size());
    }

    /**
     * Test for distinct flag.
     */
    @Test
    public void testDistinctFlag() {
        final SelectNode node = new SelectNode();
        Assert.assertFalse(node.isDistinct());
        node.setDistinct(true);
        Assert.assertTrue(node.isDistinct());
    }

    /**
     * Test for fields.
     */
    @Test
    public void testFields() {
        final SelectNode node = new SelectNode();
        final FieldNode field = new FieldNode("table", "field", null);
        Assert.assertEquals(0, node.getFields().size());
        node.addField(field);
        Assert.assertEquals(1, node.getFields().size());
    }

    /**
     * Test for group by.
     */
    @Test
    public void testGroupBy() {
        final SelectNode node = new SelectNode();
        final IdentifierNode identifier = new IdentifierNode("Node");
        Assert.assertEquals(0, node.getGroups().size());
        node.addGroupBy(identifier);
        Assert.assertEquals(1, node.getGroups().size());
    }

    /**
     * Test for order by.
     */
    @Test
    public void testOrderBy() {
        final SelectNode node = new SelectNode();
        final IdentifierNode identifier = new IdentifierNode("Node");
        Assert.assertEquals(0, node.getOrder().size());
        node.addOrderBy(identifier);
        Assert.assertEquals(1, node.getOrder().size());
    }

    /**
     * Test for tables.
     */
    @Test
    public void testTables() {
        final SelectNode node = new SelectNode();
        final TableNode table = new TableNode("table", null);
        Assert.assertEquals(0, node.getTables().size());
        node.addTable(table);
        Assert.assertEquals(1, node.getTables().size());
    }

    /**
     * Test for {@link SelectNode#toString()} method.
     */
    @Test
    public void testToString() {
        final SelectNode node = new SelectNode();
        node.addField(new FieldNode("t", "field", "f"));
        node.addField(new FieldNode("b", "field2", "f2"));
        node.addTable(new TableNode("table1", "t"));
        node.addTable(new TableNode("table2", "b"));
        node.addGroupBy(new IdentifierNode("f1"));
        node.addGroupBy(new IdentifierNode("f2"));
        node.addOrderBy(new IdentifierNode("f"));
        node.addOrderBy(new IdentifierNode("f2"));

        final ArrayList<SQLNode> conditions = new ArrayList<>();
        conditions.add(new EqualsNode(new FieldNode("t", "field", null), new FieldNode("t", "field2", null)));
        conditions.add(new NotEqualsNode(new FieldNode("t", "field", null), new FieldNode("t", "field2", null)));
        node.setConditions(conditions);

        Assert.assertEquals("SELECT t.field AS f, b.field2 AS f2 FROM table1 AS t, table2 AS b WHERE t.field = t.field2 t.field <> t.field2 GROUP BY f1, f2 ORDER BY f, f2", node.toString());
    }

    /**
     * Test for {@link SelectNode#toString()} method with fields.
     */
    @Test
    public void testToStringFields() {
        final SelectNode node = new SelectNode();
        node.addField(new FieldNode("t", "field", "f"));
        node.addField(new FieldNode("b", "field2", "f2"));
        Assert.assertEquals("SELECT t.field AS f, b.field2 AS f2", node.toString());
    }
}
