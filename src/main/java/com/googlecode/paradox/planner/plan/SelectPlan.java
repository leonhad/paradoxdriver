/*
 * SelectPlan.java
 *
 * 03/14/2009
 * Copyright (C) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.planner.plan;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.data.TableData;
import com.googlecode.paradox.data.table.value.FieldValue;
import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.metadata.ParadoxTable;
import com.googlecode.paradox.planner.nodes.PlanTableNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.utils.SQLStates;

/**
 * Creates a SELECT plan for execution.
 *
 * @author Leonardo Alves da Costa
 * @since 1.1
 * @version 1.1
 */
public class SelectPlan implements Plan {

    private final List<Column> columns = new ArrayList<>();
    private final List<PlanTableNode> tables = new ArrayList<>();
    private List<List<FieldValue>> values = null;

    /**
     * Creates a SELECT plan.
     *
     * @param conn the Paradox connection.
     */
    public SelectPlan(final ParadoxConnection conn) {
    }

    /**
     * Add column from select list.
     *
     * @param name
     *            column name.
     * @throws SQLException
     *             search column exception.
     */
    public void addColumn(String name) throws SQLException {
        final ParadoxField field = findField(name);
        if (field == null) {
            throw new SQLException(String.format("Invalid column name: '%s'", name), SQLStates.INVALID_COLUMN);
        }
        columns.add(field.getColumn());
    }

    /**
     * Associate all columns from a table.
     *
     * @param table the table to scan.
     * @throws SQLException in case of wrong SQL type.
     */
    public void addColumnFromTable(final ParadoxTable table) throws SQLException {
        for (final ParadoxField field : table.getFields()) {
            columns.add(field.getColumn());
        }
    }

    /**
     * Adds a table to this plan.
     *
     * @param table the table.
     */
    public void addTable(final PlanTableNode table) {
        tables.add(table);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() throws SQLException {
        if (columns.isEmpty() || tables.isEmpty()) {
            return;
        }

        for (final Column column : columns) {
            for (final PlanTableNode table : tables) {
                final ParadoxTable pTable = table.getTable();
                if (column.getTableName().equalsIgnoreCase(pTable.getName())) {
                    final ParadoxField field = pTable.findField(column.getName());
                    if (field == null) {
                        throw new SQLException("Column '" + column.getName() + "' not found in table '" + pTable.getName(), SQLStates.INVALID_FIELD_VALUE);
                    }
                    // load table data
                    final List<List<FieldValue>> tableData = TableData.loadData(pTable, pTable.getFields());
                    // search column index
                    if (field.getOrderNum() > tableData.size() || field.getOrderNum() < 1) {
                        throw new SQLException("Invalid column position", SQLStates.INVALID_FIELD_VALUE);
                    }
                    if (values == null) {
                        values = new ArrayList<>();
                    }
                    final int p = field.getOrderNum() - 1;
                    List<FieldValue> resultRow;
                    for (int j = 0; j < tableData.size(); j++) {
                        if (j == values.size()) {
                            resultRow = new ArrayList<>();
                            values.add(resultRow);
                        } else {
                            resultRow = values.get(j);
                        }
                        resultRow.add(tableData.get(j).get(p));
                    }
                }
            }
        }
    }

    private ParadoxField findField(String name) throws SQLException {
        final List<ParadoxField> fields = new ArrayList<>(1);
        String prefix = null;
        final int p = name.indexOf('.');
        if (p > -1) {
            prefix = name.substring(0, p - 1);
            name = name.substring(p);
        }
        /*
         * Find column in table list
         *
         * select a.id from table a - true select id from table - true select id
         * from table1, table2 - exception (if id exists in table1 and table2)
         */
        for (final PlanTableNode table : tables) {
            if (table.getTable() == null) {
                throw new SQLException("Empty table", SQLStates.INVALID_TABLE);
            }

            if (prefix != null && table.getAlias() != null && !prefix.equalsIgnoreCase(table.getAlias())) {
                continue;
            }

            for (final ParadoxField field : table.getTable().getFields()) {
                if (field.getName().equalsIgnoreCase(name)) {
                    fields.add(field);
                    // Unique column in table
                    break;
                }
            }
        }
        if (!fields.isEmpty()) {
            if (fields.size() > 1) {
                throw new SQLException("Column '" + name + "' ambiguously defined", SQLStates.COLUMN_AMBIQUOUS);
            } else {
                return fields.get(0);
            }
        }

        return null;
    }

    /**
     * Gets the columns in SELECT statement.
     *
     * @return the columns in SELECT statement.
     */
    public List<Column> getColumns() {
        return columns;
    }

    /**
     * Gets the tables in this plan.
     *
     * @return the tables in this plan.
     */
    public List<PlanTableNode> getTables() {
        return tables;
    }

    /**
     * Values from tables in column order.
     *
     * @return array of array of values/ Can be null (empty result set);
     */
    public List<List<FieldValue>> getValues() {
        return values;
    }
}
