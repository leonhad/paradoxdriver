/*
 * SQLNodeTest.java
 *
 * 07/02/2016
 * Copyright (C) 2016 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.parser.nodes;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link SQLNode} class.
 * 
 * @author Leonardo Alves da Costa
 * @since 1.3
 * @version 1.0
 */
public class SQLNodeTest {

    /**
     * Test for alias.
     */
    @Test
    public void testAlias() {
        final JoinNode node = new JoinNode();
        node.setAlias("alias");
        Assert.assertEquals("alias", node.getAlias());
    }

    /**
     * Test conditions.
     */
    @Test
    public void testConditions() {
        final ArrayList<SQLNode> list = new ArrayList<>();
        final SQLNode node = new JoinNode();
        node.setChildren(list);
        Assert.assertEquals(list, node.getChildren());
    }

}
