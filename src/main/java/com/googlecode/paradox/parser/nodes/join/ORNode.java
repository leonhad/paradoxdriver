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
package com.googlecode.paradox.parser.nodes.join;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.parser.ValuesComparator;
import com.googlecode.paradox.parser.nodes.AbstractConditionalNode;
import com.googlecode.paradox.parser.nodes.SQLNode;

/**
 * Store the OR node.
 *
 * @author Leonardo Costa
 * @version 1.3
 * @since 1.1
 */
public class ORNode extends AbstractJoinNode {

    /**
     * Create a new instance.
     *
     * @param connection the Paradox connection.
     * @param child      the child node.
     */
    public ORNode(final ParadoxConnection connection, final SQLNode child) {
        super(connection, "OR", child);
    }

    @Override
    public boolean evaluate(final Object[] row, final ValuesComparator comparator) {
        for (final SQLNode node : childhood) {
            final AbstractConditionalNode comparisonNode = (AbstractConditionalNode) node;
            if (comparisonNode.evaluate(row, comparator)) {
                return true;
            }
        }
        return false;
    }
}
