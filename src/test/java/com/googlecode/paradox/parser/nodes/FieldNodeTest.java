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
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link FieldNode}
 *
 * @version 1.2
 * @since 1.3
 */
public class FieldNodeTest {

    /**
     * Test for {@link FieldNode#toString()} method.
     */
    @Test
    public void testToString() {
        final FieldNode node = new FieldNode("table", "field", null);
        node.setAlias("alias");
        Assert.assertEquals("Invalid node value.", "table.field AS alias", node.toString());
    }

    /**
     * Test for {@link FieldNode#toString()} method with null alias.
     */
    @Test
    public void testToStringWithNullAlias() {
        final FieldNode node = new FieldNode("table", "field", null);
        Assert.assertEquals("Invalid node value.", "table.field", node.toString());
    }

    /**
     * Test {@link FieldNode#toString()} with null table.
     */
    @Test
    public void testToStringWithNullTable() {
        final FieldNode node = new FieldNode(null, "field", null);
        Assert.assertEquals("Invalid node value.", "field", node.toString());
    }

    /**
     * Test for {@link FieldNode#toString()} method without alias.
     */
    @Test
    public void testToStringWithoutAlias() {
        final FieldNode node = new FieldNode("table", "field", null);
        Assert.assertEquals("Invalid node value.", "table.field", node.toString());
    }
}
