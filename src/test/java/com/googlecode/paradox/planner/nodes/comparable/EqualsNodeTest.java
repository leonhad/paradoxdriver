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

import com.googlecode.paradox.planner.nodes.FieldNode;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link EqualsNode} class.
 *
 * @version 1.2
 * @since 1.3
 */
public class EqualsNodeTest {

    /**
     * Test for {@link BetweenNode#toString()} method.
     */
    @Test
    public void testToString() {
        final FieldNode first = new FieldNode("table", "first", "first", null);
        final FieldNode last = new FieldNode("table", "last", "last", null);
        final EqualsNode node = new EqualsNode(first, last, null);
        Assert.assertEquals("Invalid node value.", "table.first = table.last", node.toString());
    }
}
