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
package com.googlecode.paradox.planner.nodes.join;

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.parser.ScannerPosition;
import com.googlecode.paradox.parser.nodes.AbstractConditionalNode;
import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;

import java.sql.SQLException;
import java.util.List;

/**
 * Store the OR node.
 *
 * @version 1.8
 * @since 1.1
 */
public class ORNode extends AbstractJoinNode {

    /**
     * Create a new instance.
     *
     * @param child    the child node.
     * @param position the current Scanner position.
     */
    public ORNode(final SQLNode child, final ScannerPosition position) {
        super("OR", child, position);
    }

    @Override
    public boolean evaluate(final ConnectionInfo connectionInfo, final Object[] row, final Object[] parameters,
                            final ParadoxType[] parameterTypes, final List<Column> columnsLoaded) throws SQLException {
        for (final SQLNode node : children) {
            final AbstractConditionalNode conditionalNode = (AbstractConditionalNode) node;
            if (conditionalNode.evaluate(connectionInfo, row, parameters, parameterTypes, columnsLoaded)) {
                return true;
            }
        }
        return false;
    }
}
