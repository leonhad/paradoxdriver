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
package com.googlecode.paradox.planner.nodes.comparable;

import com.googlecode.paradox.planner.nodes.FieldNode;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link NotEqualsNode} class.
 *
 * @version 1.3
 * @since 1.3
 */
public class NotEqualsNodeTest {

    /**
     * Test for {@link BetweenNode#toString()} method.
     */
    @Test
    public void testToString() {
        final FieldNode first = new FieldNode("table", "first", null);
        final FieldNode last = new FieldNode("table", "last", null);
        final NotEqualsNode node = new NotEqualsNode(first, last, null);
        Assert.assertEquals("Invalid node value.", "table.first <> table.last", node.toString());
    }
}
