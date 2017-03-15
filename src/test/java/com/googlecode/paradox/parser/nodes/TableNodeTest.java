/*
 * TableNodeTest.java 07/17/2009 Copyright (C) 2009 Leonardo Alves da Costa This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.parser.nodes;

import com.googlecode.paradox.parser.nodes.comparisons.EqualsNode;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link TableNode} class.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.3
 */
public class TableNodeTest {
    
    /**
     * Test for instance.
     */
    @Test
    public void testInstance() {
        final TableNode node = new TableNode("table.db", "alias");
        Assert.assertEquals("Testing for name.", "table", node.getName());
        Assert.assertEquals("Testing for alias", "alias", node.getAlias());
    }
    
    /**
     * Test for joins.
     */
    @Test
    public void testJoin() {
        final TableNode node = new TableNode("table.db", "alias");
        Assert.assertEquals("Testing for initial join size.", 0, node.getJoins().size());
        node.addJoin(new JoinNode());
        Assert.assertEquals("Testing for changed join size.", 1, node.getJoins().size());
    }
    
    /**
     * Test for {@link TableNode#toString()} method.
     */
    @Test
    public void testToString() {
        final TableNode node = new TableNode("table.db", "alias");
        final JoinNode join = new JoinNode();
        join.setTableName("table2");
        
        final FieldNode fieldA = new FieldNode(null, "a", null);
        final FieldNode fieldB = new FieldNode(null, "b", null);
        
        final ArrayList<SQLNode> list = new ArrayList<>();
        list.add(new EqualsNode(fieldA, fieldB));
        join.setConditions(list);
        
        node.addJoin(join);
        Assert.assertEquals("Testing for toString().", "table AS alias CROSS JOIN table2 ON a = b ", node.toString());
    }
}
