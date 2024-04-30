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

import com.googlecode.paradox.parser.SQLParser;
import com.googlecode.paradox.parser.nodes.SelectNode;
import com.googlecode.paradox.parser.nodes.StatementNode;
import com.googlecode.paradox.planner.nodes.ValueNode;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;

/**
 * Unit test for {@link LikeNode} class.
 *
 * @version 1.0
 * @since 1.3
 */
public class LikeNodeTest {

    /**
     * Test for not as a value.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testNullAsValue() throws SQLException {
        final SQLParser parser = new SQLParser("SELECT A FROM db.B WHERE A LIKE 't&%' escape '&'");
        final StatementNode tree = parser.parse();

        final SelectNode select = (SelectNode) tree;

        Assert.assertTrue("Invalid condition value.", select.getCondition() instanceof LikeNode);
        final LikeNode node = (LikeNode) select.getCondition();
        Assert.assertEquals("Invalid field name.", "A", node.getField().getName());
        Assert.assertTrue("Invalid field value.", node.getLast() instanceof ValueNode);

        Assert.assertEquals("Invalid field value.", "t&%", node.getLast().getName());
        Assert.assertEquals("Invalid escape char", '&', node.getEscape());
    }
}
