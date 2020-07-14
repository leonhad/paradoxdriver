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
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.planner.nodes.ValueNode;
import com.googlecode.paradox.rowset.ValuesComparator;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores the IN node.
 *
 * @version 1.0
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
     * @param connection the Paradox connection.
     * @param field      the first node.
     */
    public InNode(final ParadoxConnection connection, final FieldNode field) {
        super(connection, "in", field, null);
    }

    /**
     * Add a new value to this expression.
     *
     * @param value the new value to add.
     */
    public void addField(final ValueNode value) {
        values.add(value);
    }

    @Override
    public boolean evaluate(final Object[] row, final Object[] parameters) {
        final Object value1 = getValue(row, field, parameters);

        for (final ValueNode value : values) {
            final Object value2 = value.getName();
            if (ValuesComparator.equals(value1, value2)) {
                return true;
            }
        }

        return false;
    }
}
