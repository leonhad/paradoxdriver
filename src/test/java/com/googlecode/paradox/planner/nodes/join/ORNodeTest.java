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
package com.googlecode.paradox.planner.nodes.join;

import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.parser.nodes.StatementNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for {@link ORNode} class.
 *
 * @since 1.3
 */
class ORNodeTest {

    /**
     * Test for childhood.
     */
    @Test
    void testChildhood() {
        final SQLNode node = new StatementNode(null, null);
        final ORNode and = new ORNode(node, null);
        assertEquals(1, and.getChildren().size());
        assertEquals(node, and.getChildren().iterator().next());
    }

    /**
     * Test for name.
     */
    @Test
    void testName() {
        final ORNode node = new ORNode(null, null);
        assertEquals("OR", node.getName());
    }
}
