/*
 * BetweenNodeTest.java 06/29/2009 Copyright (C) 2009 Leonardo Alves da Costa This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.parser.nodes.comparisons;

import com.googlecode.paradox.parser.nodes.FieldNode;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link BetweenNode} class.
 *
 * @author Leonardo Alves da Costa
 * @since 1.3
 * @version 1.0
 */
public class BetweenNodeTest {

    /**
     * Test the field node.
     */
    @Test
    public void testField() {
        final FieldNode field = new FieldNode("table", "field", "alias");
        final BetweenNode node = new BetweenNode(field, null, null);
        Assert.assertEquals(field, node.getField());
    }

    /**
     * Test for {@link BetweenNode#toString()} method.
     */
    @Test
    public void testToString() {
        final FieldNode field = new FieldNode("table", "field", "field");
        final FieldNode first = new FieldNode("table", "first", "first");
        final FieldNode last = new FieldNode("table", "last", "last");
        final BetweenNode node = new BetweenNode(field, first, last);
        Assert.assertEquals("table.field BETWEEN table.first AND table.last", node.toString());
    }
}
