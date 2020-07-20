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

import com.googlecode.paradox.planner.nodes.ValueNode;
import com.googlecode.paradox.planner.sorting.OrderType;
import com.googlecode.paradox.results.ParadoxType;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link SelectNode}.
 *
 * @version 1.5
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
        final ValueNode value = new ValueNode("1", null, ParadoxType.NUMBER);
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
}
