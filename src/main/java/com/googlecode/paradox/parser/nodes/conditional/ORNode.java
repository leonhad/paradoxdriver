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
package com.googlecode.paradox.parser.nodes.conditional;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.data.table.value.FieldValue;
import com.googlecode.paradox.parser.nodes.FieldNode;
import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.parser.nodes.comparable.AbstractComparableNode;
import com.googlecode.paradox.parser.nodes.comparable.ValuesComparator;
import com.googlecode.paradox.planner.nodes.PlanTableNode;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * Store the OR node.
 *
 * @author Leonardo Costa
 * @version 1.1
 * @since 1.1
 */
public class ORNode extends AbstractComparableNode {

    /**
     * Create a new instance.
     *
     * @param connection the Paradox connection.
     * @param child      the child node.
     */
    public ORNode(final ParadoxConnection connection, final SQLNode child) {
        super(connection, "OR");
        this.childhood.add(child);
    }

    @Override
    public boolean evaluate(final FieldValue[] row, final ValuesComparator comparator) {
        for (final SQLNode node : childhood) {
            final AbstractComparableNode comparisonNode = (AbstractComparableNode) node;
            if (comparisonNode.evaluate(row, comparator)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setFieldIndexes(final List<FieldValue> row, final List<PlanTableNode> tables) throws SQLException {
        for (final SQLNode node : childhood) {
            ((AbstractComparableNode) node).setFieldIndexes(row, tables);
        }
    }

    @Override
    public Set<FieldNode> getClauseFields() {
        final Set<FieldNode> nodes = super.getClauseFields();
        for (final SQLNode node : childhood) {
            nodes.addAll(node.getClauseFields());
        }
        return nodes;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (final SQLNode node : childhood) {
            if (first) {
                first = false;
            } else {
                builder.append(" ");
                builder.append(this.name);
                builder.append(" ");
            }
            builder.append(node.toString());
        }
        return builder.toString();
    }
}
