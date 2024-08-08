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
package com.googlecode.paradox.planner.nodes.join;

import com.googlecode.paradox.parser.ScannerPosition;
import com.googlecode.paradox.parser.nodes.AbstractConditionalNode;
import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.planner.nodes.PlanTableNode;
import com.googlecode.paradox.results.Column;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * Join node common code.
 *
 * @version 1.3
 * @since 1.6.0
 */
public abstract class AbstractJoinNode extends AbstractConditionalNode {

    /**
     * Create a new instance.
     *
     * @param fieldName the field name.
     * @param child     the child node.
     * @param position  the current scanner position.
     */
    protected AbstractJoinNode(final String fieldName, final SQLNode child, final ScannerPosition position) {
        super(fieldName, position);
        if (child != null) {
            this.children.add(child);
        }
    }

    @Override
    public final void setFieldIndexes(final List<Column> columns, final List<PlanTableNode> tables)
            throws SQLException {
        for (final SQLNode node : children) {
            ((AbstractConditionalNode) node).setFieldIndexes(columns, tables);
        }
    }

    @Override
    public final Set<FieldNode> getClauseFields() {
        final Set<FieldNode> nodes = super.getClauseFields();
        for (final SQLNode node : children) {
            nodes.addAll(node.getClauseFields());
        }
        return nodes;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (final SQLNode node : children) {
            if (first) {
                builder.append(" (");
                first = false;
            } else {
                builder.append(" ");
                builder.append(this.name);
                builder.append(" ");
            }

            builder.append(node.toString());
        }

        builder.append(") ");
        return builder.toString();
    }
}
