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
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for {@link LikeNode} class.
 *
 * @since 1.3
 */
class LikeNodeTest {

    /**
     * Test for not as a value.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testNullAsValue() throws SQLException {
        final SQLParser parser = new SQLParser("SELECT A FROM db.B WHERE A LIKE 't&%' escape '&'");
        final StatementNode tree = parser.parse();

        final SelectNode select = (SelectNode) tree;

        assertInstanceOf(LikeNode.class, select.getCondition());
        final LikeNode node = (LikeNode) select.getCondition();
        assertEquals("A", node.getField().getName());
        assertInstanceOf(ValueNode.class, node.getLast());

        assertEquals("t&%", node.getLast().getName());
        assertEquals('&', node.getEscape());
    }
}
