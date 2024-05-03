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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for {@link FieldNode}
 *
 * @since 1.3
 */
class FieldNodeTest {

    /**
     * Test for {@link FieldNode#toString()} method.
     */
    @Test
    void testToString() {
        final FieldNode node = new FieldNode("table", "field", null);
        node.setAlias("alias");
        assertEquals("table.field AS alias", node.toString());
    }

    /**
     * Test for {@link FieldNode#toString()} method with null alias.
     */
    @Test
    void testToStringWithNullAlias() {
        final FieldNode node = new FieldNode("table", "field", null);
        assertEquals("table.field", node.toString());
    }

    /**
     * Test {@link FieldNode#toString()} with null table.
     */
    @Test
    void testToStringWithNullTable() {
        final FieldNode node = new FieldNode(null, "field", null);
        assertEquals("field", node.toString());
    }

    /**
     * Test for {@link FieldNode#toString()} method without alias.
     */
    @Test
    void testToStringWithoutAlias() {
        final FieldNode node = new FieldNode("table", "field", null);
        assertEquals("table.field", node.toString());
    }
}
