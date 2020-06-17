/*
 * Copyright (C) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.googlecode.paradox.planner.nodes;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link PlanTableNode} class.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.3
 */
public class PlanTableNodeTest {
    
    /**
     * Test for instance sanity.
     */
    @Test
    public void testInstance() {
        final PlanTableNode node = new PlanTableNode();
        node.setAlias("alias");
        node.setTable(null);
        
        Assert.assertEquals("Alias not equals.", "alias", node.getAlias());
        Assert.assertNull("Table not equals.", node.getTable());
    }
}
