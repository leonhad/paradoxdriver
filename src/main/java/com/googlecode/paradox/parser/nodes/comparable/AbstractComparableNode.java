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
package com.googlecode.paradox.parser.nodes.comparable;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.data.table.value.FieldValue;
import com.googlecode.paradox.metadata.ParadoxTable;
import com.googlecode.paradox.parser.nodes.FieldNode;
import com.googlecode.paradox.parser.nodes.NumberNode;
import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.parser.nodes.StringNode;
import com.googlecode.paradox.planner.nodes.PlanTableNode;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Stores a abstract comparable node.
 *
 * @author Leonardo Costa
 * @version 1.2
 * @since 1.1
 */
public abstract class AbstractComparableNode extends SQLNode {

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
    public AbstractComparableNode(final ParadoxConnection connection, final String name, final FieldNode field) {
        super(connection, name);
        this.field = field;
    }

    public AbstractComparableNode(final ParadoxConnection connection, final String name) {
        this(connection, name, null);
    }

    public abstract boolean evaluate(final FieldValue[] row, final ValuesComparator comparator);

    public void setFieldIndexes(final List<FieldValue> row, final List<PlanTableNode> tables) throws SQLException {
        getIndex(field, row, tables);
    }

    protected void getIndex(final FieldNode field, final List<FieldValue> row, final List<PlanTableNode> tables)
            throws SQLException {
        final String tableName = tables.stream()
                .filter(t -> t.getAlias().equals(field.getTableName()))
                .map(PlanTableNode::getTable).map(ParadoxTable::getName)
                .findFirst().orElse(field.getTableName());

        if (field instanceof StringNode || field instanceof NumberNode) {
            // Do not set indexes in value nodes.
            return;
        }

        int index = -1;
        for (int i = 0; i < row.size(); i++) {
            final FieldValue value = row.get(i);

            // Invalid table name.
            if (tableName != null && !tableName.equals(value.getField().getTable().getName())) {
                continue;
            }

            if (value.getField().getName().equalsIgnoreCase(field.getName())) {
                if (index != -1) {
                    throw new SQLException("Column " + field + " ambiguous defined.");
                }
                index = i;
            }
        }

        if (index == -1) {
            throw new SQLException("Field " + field + " not found.");
        }

        field.setIndex(index);
    }

    protected Object getValue(final FieldValue[] row, final FieldNode field) {
        if (field.getIndex() == -1) {
            // Not a table field.
            return field.getName();
        }

        return row[field.getIndex()].getValue();
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
