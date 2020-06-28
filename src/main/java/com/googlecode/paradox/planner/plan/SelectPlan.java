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
package com.googlecode.paradox.planner.plan;

import com.googlecode.paradox.data.TableData;
import com.googlecode.paradox.data.table.value.FieldValue;
import com.googlecode.paradox.metadata.ParadoxDataFile;
import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.parser.nodes.FieldNode;
import com.googlecode.paradox.parser.nodes.comparisons.AbstractComparisonNode;
import com.googlecode.paradox.planner.nodes.PlanTableNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.utils.SQLStates;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Creates a SELECT plan for execution.
 *
 * @author Leonardo Alves da Costa
 * @version 1.2
 * @since 1.1
 */
public final class SelectPlan implements Plan {

    /**
     * The columns in this plan.
     */
    private final List<Column> columns = new ArrayList<>();

    /**
     * The tables in this plan.
     */
    private final List<PlanTableNode> tables = new ArrayList<>();

    /**
     * The data values.
     */
    private final List<FieldValue[]> values = new ArrayList<>();

    /**
     * The conditions to filter values
     */
    private final AbstractComparisonNode condition;

    /**
     * Creates a new instance.
     */
    SelectPlan() {
        this(null);
    }

    /**
     * Creates a SELECT plan with conditions.
     *
     * @param condition the conditions to filter results
     */
    public SelectPlan(final AbstractComparisonNode condition) {
        this.condition = condition;
    }

    /**
     * Add column from select list.
     *
     * @param node SQL node with column attributes.
     * @throws SQLException search column exception.
     */
    public void addColumn(final FieldNode node) throws SQLException {
        List<ParadoxField> fields = Collections.emptyList();

        for (final PlanTableNode table : this.tables) {
            if (node.getTableName() == null || table.isThis(node.getTableName())) {
                fields = table.getTable().getFields().stream()
                        .filter(f -> f.getName().equalsIgnoreCase(node.getName()))
                        .collect(Collectors.toList());
                if (!fields.isEmpty()) {
                    break;
                }
            }
        }

        if (fields.isEmpty()) {
            String fieldName = node.getName();
            if (!node.getName().equalsIgnoreCase(node.getAlias())) {
                fieldName = node.getAlias() + "." + node.getName();
            }
            throw new SQLException(String.format("Invalid column name: '%s'", fieldName),
                    SQLStates.INVALID_COLUMN.getValue());
        } else if (fields.size() > 1) {
            throw new SQLException(String.format("Column '%s' ambiguous defined.", node.getName()),
                    SQLStates.INVALID_COLUMN.getValue());
        }

        this.columns.add(fields.stream().map(Column::new).findFirst().orElse(null));
    }

    /**
     * Associate all columns from a table.
     *
     * @param table the table to scan.
     */
    public void addColumnFromTable(final ParadoxDataFile table) {
        for (final ParadoxField field : table.getFields()) {
            this.columns.add(new Column(field));
        }
    }

    /**
     * Associate all columns from a list of tables.
     *
     * @param tables the table list to scan.
     */
    public void addColumnFromTables(final Iterable<PlanTableNode> tables) {
        for (final PlanTableNode table : tables) {
            addColumnFromTable(table.getTable());
        }
    }

    /**
     * Adds a table to this plan.
     *
     * @param table the table.
     */
    public void addTable(final PlanTableNode table) {
        this.tables.add(table);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void execute() throws SQLException {
        if (this.columns.isEmpty() || this.tables.isEmpty()) {
            return;
        }

        final List<List<List<FieldValue>>> rawData = new ArrayList<>();
        for (final PlanTableNode table : this.tables) {
            // From columns
            final Set<Column> columnsToLoad =
                    this.columns.stream().filter(c -> c.isThis(table.getTable()))
                            .collect(Collectors.toSet());

            if (condition != null) {
                final Set<FieldNode> fields = this.condition.getClauseFields();
                fields.forEach((FieldNode node) -> {
                    if (table.isThis(node.getTableName())) {
                        table.getTable().getFields().stream()
                                .filter(f -> f.getName().equalsIgnoreCase(node.getName()))
                                .map(Column::new).forEach(columnsToLoad::add);
                    }
                });
            }

            if (!columnsToLoad.isEmpty()) {
                final List<List<FieldValue>> tableData = TableData.loadData(table.getTable(),
                        columnsToLoad.stream().map(Column::getField).collect(Collectors.toList()));

                rawData.add(tableData);
            }
        }

        if (rawData.isEmpty() || rawData.stream().allMatch(List::isEmpty)) {
            return;
        }

        final List<FieldValue> firstLine = new ArrayList<>(1);
        for (List<List<FieldValue>> tableValues : rawData) {
            firstLine.addAll(tableValues.get(0));
        }

        if (this.condition != null) {
            this.condition.setFieldIndexes(firstLine, tables);
        }

        // Find column indexes.
        final int[] mapColumns = new int[this.columns.size()];
        for (int i = 0; i < this.columns.size(); i++) {
            Column column = this.columns.get(i);
            for (int loop = 0; loop < firstLine.size(); loop++) {
                if (firstLine.get(loop).getField().equals(column.getField())) {
                    mapColumns[i] = loop;
                    break;
                }
            }
        }

        final FieldValue[] row = new FieldValue[firstLine.size()];
        filter(rawData, 0, row, 0, mapColumns);
    }

    private void filter(final List<List<List<FieldValue>>> tables, final int tableIndex, final FieldValue[] row,
                      final int rowIndex,   final int[] mapColumns) {

        List<List<FieldValue>> rowValues = tables.get(tableIndex);
        for (final List<FieldValue> tableRow : rowValues) {
            // Fill row.
            for (int loop = 0; loop < tableRow.size(); loop++) {
                row[rowIndex + loop] = tableRow.get(loop);
            }

            // Last table?
            if (tableIndex + 1 == tables.size()) {
                // Filter joins

                if (condition != null && !condition.evaluate(row, this.tables)) {
                    continue;
                }

                final FieldValue[] finalRow = new FieldValue[mapColumns.length];
                for (int i = 0; i < mapColumns.length; i++) {
                    int index = mapColumns[i];
                    finalRow[i] = row[index];
                }
                this.values.add(finalRow);
            } else {
                // There is more tables.
                filter(tables, tableIndex + 1, row, rowIndex + tableRow.size(), mapColumns);
            }
        }
    }
/*
        // Find indexes.
        final int[] mapColumns = new int[this.columns.size()];
        final List<FieldValue> firstLine = rawData.get(0);
        for (int i = 0; i < this.columns.size(); i++) {
            Column column = this.columns.get(i);
            for (int loop = 0; loop < firstLine.size(); loop++) {
                if (firstLine.get(loop).getField().equals(column.getField())) {
                    mapColumns[i] = loop;
                    break;
                }
            }
        }

        // Filter joins
        for (final List<FieldValue> row : rawData) {
            if (condition != null && !condition.evaluate(row, this.tables)) {
                continue;
            }

            final FieldValue[] finalRow = new FieldValue[mapColumns.length];
            for (int i = 0; i < mapColumns.length; i++) {
                int index = mapColumns[i];
                finalRow[i] = row.get(index);
            }
            this.values.add(finalRow);
        }
    }*/

    /**
     * Gets the columns in SELECT statement.
     *
     * @return the columns in SELECT statement.
     */
    public List<Column> getColumns() {
        return this.columns;
    }

    /**
     * Gets the tables in this plan.
     *
     * @return the tables in this plan.
     */
    public List<PlanTableNode> getTables() {
        return this.tables;
    }

    /**
     * Values from tables in column order.
     *
     * @return array of array of values / Can be null (empty result set);
     */
    public List<FieldValue[]> getValues() {
        return this.values;
    }
}
