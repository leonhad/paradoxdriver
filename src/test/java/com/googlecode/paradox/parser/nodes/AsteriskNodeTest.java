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

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link AsteriskNode}.
 *
 * @version 1.2
 * @since 1.5.0
 */
public class AsteriskNodeTest {

    /**
     * Test for instance.
     */
    @Test
    public void testInstance() {
        final AsteriskNode node = new AsteriskNode(null, null);
        Assert.assertEquals("Test for node name", "ASTERISK", node.getName());
    }

    /**
     * Test toString method.
     */
    @Test
    public void testToString() {
        AsteriskNode node = new AsteriskNode(null, null);
        Assert.assertEquals("Invalid value", "*", node.toString());

        node = new AsteriskNode("t", null);
        Assert.assertEquals("Invalid value", "t.*", node.toString());
    }
}
