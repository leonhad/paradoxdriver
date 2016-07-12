/*
 * ORNodeTest.java
 *
 * 07/11/2016
 * Copyright (C) 2016 Leonardo Alves da Costa
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
package com.googlecode.paradox.parser.nodes.conditional;

import org.junit.Assert;
import org.junit.Test;

import com.googlecode.paradox.parser.nodes.SQLNode;

/**
 * Unit test for {@link ORNode} class.
 * 
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.3
 */
public class ORNodeTest {

    /**
     * Test for childhood.
     */
    @Test
    public void testChildhood() {
        final SQLNode node = new SQLNode("node");
        final ORNode and = new ORNode(node);
        Assert.assertEquals(1, and.getChildren().size());
        Assert.assertEquals(node, and.getChildren().iterator().next());
    }

    /**
     * Test for name.
     */
    @Test
    public void testName() {
        final ORNode node = new ORNode(null);
        Assert.assertEquals("OR", node.getName());
    }
}
