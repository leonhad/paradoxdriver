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
package com.googlecode.paradox.parser.nodes;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.exceptions.ParadoxException;
import com.googlecode.paradox.metadata.ParadoxTable;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.planner.nodes.PlanTableNode;
import com.googlecode.paradox.planner.nodes.ValueNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.rowset.ValuesComparator;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Stores a abstract comparable node.
 *
 * @version 1.5
 * @since 1.1
 */
public abstract class AbstractConditionalNode extends SQLNode {

    /**
     * The field node.
     */
    protected final FieldNode field;

    /**
     * Creates a new instance.
     *
     * @param connection the Paradox connection.
     * @param name       the condition name.
     * @param field      the field node to compare.
     */
    public AbstractConditionalNode(final ParadoxConnection connection, final String name, final FieldNode field) {
        super(connection, name);
        this.field = field;
    }

    public AbstractConditionalNode(final ParadoxConnection connection, final String name) {
        this(connection, name, null);
    }

    public abstract boolean evaluate(final Object[] row, final ValuesComparator comparator);

    public void setFieldIndexes(final List<Column> columns, final List<PlanTableNode> tables) throws SQLException {
        getIndex(field, columns, tables);
    }

    protected void getIndex(final FieldNode field, final List<Column> columns, final List<PlanTableNode> tables)
            throws SQLException {

        // Do not set indexes in value nodes.
        if (field == null || field instanceof ValueNode) {
            return;
        }

        final String tableName = tables.stream()
                .filter(t -> t.getAlias().equalsIgnoreCase(field.getTableName()))
                .map(PlanTableNode::getTable).map(ParadoxTable::getName)
                .findFirst().orElse(field.getTableName());

        int index = -1;
        for (int i = 0; i < columns.size(); i++) {
            final Column column = columns.get(i);

            // Invalid table name.
            if (tableName != null && !tableName.equalsIgnoreCase(column.getField().getTable().getName())) {
                continue;
            }

            if (column.getField().getName().equalsIgnoreCase(field.getName())) {
                if (index != -1) {
                    throw new ParadoxException(ParadoxException.Error.COLUMN_AMBIGUOUS_DEFINED, field.toString());
                }
                index = i;
            }
        }

        if (index == -1) {
            throw new ParadoxException(ParadoxException.Error.INVALID_COLUMN, field.toString());
        }

        field.setIndex(index);
    }

    protected Object getValue(final Object[] row, final FieldNode field) {
        if (field.getIndex() == -1) {
            // Not a table field.
            return field.getName();
        }

        return row[field.getIndex()];
    }

    @Override
    public Set<FieldNode> getClauseFields() {
        final Set<FieldNode> set = new HashSet<>();
        if (field != null) {
            set.add(field);
        }
        set.addAll(super.getClauseFields());
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
