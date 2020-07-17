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
import com.googlecode.paradox.planner.nodes.ValueNode;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Types;

/**
 * Unit test for {@link InNode} class.
 *
 * @version 1.2
 * @since 1.6.0
 */
public class InNodeTest {

    /**
     * Test for {@link BetweenNode#toString()} method.
     */
    @Test
    public void testToString() {
        final FieldNode first = new FieldNode("table", "first", null);
        final InNode node = new InNode(first, null);
        node.addField(new ValueNode("test", null, Types.VARCHAR));
        node.addField(new ValueNode("1", null, Types.NUMERIC));
        Assert.assertEquals("Invalid node value.", "table.first IN ('test', 1)", node.toString());
    }
}
