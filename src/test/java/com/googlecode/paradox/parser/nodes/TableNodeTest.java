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
import com.googlecode.paradox.planner.nodes.comparable.EqualsNode;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link TableNode} class.
 *
 * @version 1.3
 * @since 1.3
 */
public class TableNodeTest {

    /**
     * Test for instance.
     */
    @Test
    public void testInstance() {
        final TableNode node = new TableNode(null, "table.db", "alias", null);
        Assert.assertEquals("Invalid table name.", "table", node.getName());
        Assert.assertEquals("Invalid table alias.", "alias", node.getAlias());
    }

    /**
     * Test for {@link TableNode#toString()} method.
     */
    @Test
    public void testToString() {
        final JoinNode join = new JoinNode(null, "table.db", "alias", JoinType.INNER, null);
        final FieldNode fieldA = new FieldNode(null, "a", null, null);
        final FieldNode fieldB = new FieldNode(null, "b", null, null);

        join.setCondition(new EqualsNode(fieldA, fieldB, null));

        Assert.assertEquals("Invalid JoinNode for toString().", "INNER JOIN table AS alias ON a = b ",
                join.toString());
    }
}
