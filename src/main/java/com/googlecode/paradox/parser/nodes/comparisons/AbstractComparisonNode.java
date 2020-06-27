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
import com.googlecode.paradox.metadata.ParadoxTable;
import com.googlecode.paradox.parser.nodes.FieldNode;
import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.planner.nodes.PlanTableNode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Stores a abstract comparision node.
 *
 * @author Leonardo Alves da Costa
 * @version 1.1
 * @since 1.1
 */
public abstract class AbstractComparisonNode extends SQLNode {

    /**
     * The field node.
     */
    protected final FieldNode field;

    /**
     * Creates a new instance.
     *
     * @param connection the Paradox connection.
     * @param name       the condition name.
     */
    public AbstractComparisonNode(final ParadoxConnection connection, final String name, final FieldNode field) {
        super(connection, name);
        this.field = field;
    }

    public AbstractComparisonNode(final ParadoxConnection connection, final String name) {
        this(connection, name, null);
    }

    public boolean evaluate(final List<FieldValue> row, final List<PlanTableNode> tables) {
        return false;
    }

    protected Object getValue(final List<FieldValue> row, final FieldNode field, final List<PlanTableNode> tables) {
        final String tableName = tables.stream()
                .filter(t -> t.getAlias().equals(field.getTableName()))
                .map(PlanTableNode::getTable).map(ParadoxTable::getName)
                .findFirst().orElse(field.getTableName());

        FieldValue ret = null;
        if (tableName == null) {
            ret = row.stream().filter(v -> v.getField().getName().equalsIgnoreCase(field.getName()))
                    .findFirst().orElse(null);
        } else {
            ret = row.stream().filter(v -> v.getField().getTable().getName().equalsIgnoreCase(tableName)
                    && v.getField().getName().equalsIgnoreCase(field.getName())).findFirst().orElse(null);
        }

        // FIXME type converter

        if (ret != null) {
            if (ret.getValue() != null) {
                return ret.getValue().toString();
            }
            return null;
        }

        return field.getName();
    }

    @Override
    public Set<FieldNode> getClausuleFields() {
        final Set<FieldNode> set = new HashSet<>();
        set.add(field);
        set.addAll(super.getClausuleFields());
        return set;
    }

    /**
     * Get the field.
     *
     * @return the field.
     */
    public FieldNode getField() {
        return field;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        return field.toString();
    }
}
