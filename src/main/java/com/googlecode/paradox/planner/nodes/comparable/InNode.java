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

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.parser.ScannerPosition;
import com.googlecode.paradox.planner.FieldValueUtils;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.planner.nodes.ValueNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.rowset.ValuesComparator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores the IN node.
 *
 * @version 1.2
 * @since 1.6.0
 */
public final class InNode extends AbstractComparableNode {

    /**
     * The list of values in expression.
     */
    private final List<ValueNode> values = new ArrayList<>();

    /**
     * Create a new instance.
     *
     * @param field    the first node.
     * @param position the current Scanner position.
     */
    public InNode(final FieldNode field, final ScannerPosition position) {
        super("in", field, null, position);
    }

    /**
     * Add a new value to this expression.
     *
     * @param value the new value to add.
     */
    public void addField(final ValueNode value) {
        values.add(value);
    }

    /**
     * Gets the value list in this node.
     *
     * @return the value list in this node.
     */
    public List<ValueNode> getValues() {
        return values;
    }

    @Override
    public boolean evaluate(final ParadoxConnection connection, final Object[] row, final Object[] parameters,
                            final ParadoxType[] parameterTypes, final List<Column> columnsLoaded) throws SQLException {
        final Object value1 = FieldValueUtils.getValue(connection, row, field, parameters, parameterTypes,
                columnsLoaded);

        for (final ValueNode value : values) {
            final Object value2 = value.getName();
            if (ValuesComparator.equals(value1, value2)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(field.toString());
        builder.append(" IN (");

        for (int i = 0; i < values.size(); i++) {
            if (i != 0) {
                builder.append(", ");
            }

            builder.append(values.get(i).toString());
        }

        builder.append(")");
        return builder.toString();
    }
}
