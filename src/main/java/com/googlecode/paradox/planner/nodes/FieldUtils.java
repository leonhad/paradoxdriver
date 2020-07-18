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
package com.googlecode.paradox.planner.nodes;

import com.googlecode.paradox.exceptions.ParadoxException;
import com.googlecode.paradox.metadata.ParadoxTable;
import com.googlecode.paradox.results.Column;

import java.sql.SQLException;
import java.util.List;

public final class FieldUtils {

    private FieldUtils() {
        // Not used.
    }

    public static void getIndex(final FieldNode field, final List<Column> columns, final List<PlanTableNode> tables)
            throws SQLException {

        // Do not set indexes in value or parameter nodes.
        if (field == null || field instanceof ValueNode || field instanceof ParameterNode) {
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

    public static Object getValue(final Object[] row, final FieldNode field, final Object[] parameters) {
        Object ret;
        if (field instanceof ParameterNode) {
            ret = ((ParameterNode) field).getValue(parameters);
        } else if (field.getIndex() == -1) {
            // Not a table field.
            ret = field.getName();
        } else {
            ret = row[field.getIndex()];
        }

        return ret;
    }

}
