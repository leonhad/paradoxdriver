/*
 * ANDNode.java
 *
 * 03/12/2009
 * Copyright (C) 2009 Leonardo Alves da Costa
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

import com.googlecode.paradox.parser.nodes.SQLNode;

import java.util.Collections;

/**
 * Stores the AND node.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.1
 */
public class ANDNode extends SQLNode {

    /**
     * Create a new instance.
     *
     * @param child
     *         the child node.
     */
    public ANDNode(final SQLNode child) {
        super("AND");
        setChildhood(Collections.singletonList(child));
    }

}
