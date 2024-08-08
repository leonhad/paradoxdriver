/*
 * Copyright (c) 2009 Leonardo Alves da Costa
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
import com.googlecode.paradox.planner.sorting.OrderType;
import com.googlecode.paradox.results.ParadoxType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for {@link SelectNode}.
 *
 * @since 1.3
 */
class SelectNodeTest {

    /**
     * Test for condition.
     */
    @Test
    void testCondition() {
        final SelectNode node = new SelectNode(null);
        assertNull(node.getCondition());
    }

    /**
     * Test for distinct flag.
     */
    @Test
    void testDistinctFlag() {
        final SelectNode node = new SelectNode(null);
        assertFalse(node.isDistinct());
        node.setDistinct(true);
        assertTrue(node.isDistinct());
    }

    /**
     * Test for group by.
     */
    @Test
    void testGroupBy() {
        final SelectNode node = new SelectNode(null);
        final FieldNode identifier = new FieldNode(null, "Node", null);
        assertEquals(0, node.getGroups().size());
        node.addGroupBy(identifier);
        assertEquals(1, node.getGroups().size());
    }

    /**
     * Test for order by.
     */
    @Test
    void testOrderBy() {
        final SelectNode node = new SelectNode(null);
        final ValueNode value = new ValueNode("1", null, ParadoxType.NUMBER);
        assertEquals(0, node.getOrder().size());
        node.addOrderBy(value, OrderType.ASC);
        assertEquals(1, node.getOrder().size());
    }

    /**
     * Test for tables.
     */
    @Test
    void testTables() {
        final SelectNode node = new SelectNode(null);
        final TableNode table = new TableNode(null, "table", null, null);
        assertEquals(0, node.getTables().size());
        node.addTable(table);
        assertEquals(1, node.getTables().size());
    }
}
