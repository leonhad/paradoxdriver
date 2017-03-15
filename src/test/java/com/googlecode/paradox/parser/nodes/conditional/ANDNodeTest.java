/*
 * ANDNodeTest.java 07/11/2016 Copyright (C) 2016 Leonardo Alves da Costa This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.parser.nodes.conditional;

import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.parser.nodes.StatementNode;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link ANDNode} class.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.3
 */
public class ANDNodeTest {
    
    /**
     * Test for childhood.
     */
    @Test
    public void testChildhood() {
        final SQLNode node = new StatementNode(null);
        final ANDNode and = new ANDNode(node);
        Assert.assertEquals(1, and.getChildhood().size());
        Assert.assertEquals(node, and.getChildhood().iterator().next());
    }
    
    /**
     * Test for name.
     */
    @Test
    public void testName() {
        final ANDNode node = new ANDNode(null);
        Assert.assertEquals("AND", node.getName());
    }
}
