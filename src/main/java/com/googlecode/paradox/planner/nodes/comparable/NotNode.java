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
package com.googlecode.paradox.planner.nodes.comparable;

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.exceptions.ParadoxSyntaxErrorException;
import com.googlecode.paradox.exceptions.SyntaxError;
import com.googlecode.paradox.parser.ScannerPosition;
import com.googlecode.paradox.parser.nodes.AbstractConditionalNode;
import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.planner.nodes.PlanTableNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * Stores the not node.
 *
 * @version 1.11
 * @since 1.1
 */
public final class NotNode extends AbstractComparableNode {

    /**
     * Create a new instance.
     *
     * @param position the current Scanner position.
     */
    public NotNode(final ScannerPosition position) {
        super("NOT", null, null, position);
    }

    @Override
    public Set<FieldNode> getClauseFields() {
        final Set<FieldNode> nodes = super.getClauseFields();
        for (final SQLNode node : children) {
            nodes.addAll(node.getClauseFields());
        }

        return nodes;
    }

    @Override
    public void setFieldIndexes(final List<Column> columns, final List<PlanTableNode> tables)
            throws SQLException {
        for (final SQLNode node : children) {
            ((AbstractConditionalNode) node).setFieldIndexes(columns, tables);
        }
    }

    @Override
    public boolean evaluate(final ConnectionInfo connectionInfo, final Object[] row, final Object[] parameters,
                            final ParadoxType[] parameterTypes, final List<Column> columnsLoaded) throws SQLException {
        if (!children.isEmpty()) {
            return !((AbstractConditionalNode) children.get(0)).evaluate(connectionInfo, row, parameters,
                    parameterTypes, columnsLoaded);
        }

        // Should never happens.
        throw new ParadoxSyntaxErrorException(SyntaxError.INVALID_SELECT_STATEMENT);
    }

    @Override
    public String toString() {
        if (!children.isEmpty()) {
            final SQLNode child = children.get(0);
            return String.format("%s %s", name, child.toString());
        }

        return name;
    }
}
