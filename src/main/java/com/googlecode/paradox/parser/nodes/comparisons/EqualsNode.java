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
package com.googlecode.paradox.parser.nodes.comparisons;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.data.table.value.FieldValue;
import com.googlecode.paradox.parser.nodes.FieldNode;
import com.googlecode.paradox.planner.nodes.PlanTableNode;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Stores the equals node.
 *
 * @author Leonardo Alves da Costa
 * @version 1.1
 * @since 1.1
 */
public final class EqualsNode extends AbstractComparisonNode {

    /**
     * The last node.
     */
    private final FieldNode last;

    /**
     * Create a new instance.
     *
     * @param connection the Paradox connection.
     * @param field      the first node.
     * @param last       the last node.
     */
    public EqualsNode(final ParadoxConnection connection, final FieldNode field, final FieldNode last) {
        super(connection, "=", field);
        this.last = last;
    }

    public FieldNode getLast() {
        return last;
    }

    @Override
    public Set<FieldNode> getClauseFields() {
        final Set<FieldNode> nodes = super.getClauseFields();
        nodes.add(last);
        return nodes;
    }

    @Override
    public String toString() {
        return String.format("%s = %s", field, last);
    }

    @Override
    public void setFieldIndexes(final List<FieldValue> row, final List<PlanTableNode> tables) {
        super.setFieldIndexes(row, tables);
        getIndex(last, row, tables);
    }

    @Override
    public boolean evaluate(final List<FieldValue> row, final List<PlanTableNode> tables) {
        final Object value1 = getValue(row, field);
        final Object value2 = getValue(row, last);
        return Objects.equals(value1, value2);
    }
}
