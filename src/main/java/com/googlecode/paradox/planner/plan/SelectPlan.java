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

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.data.TableData;
import com.googlecode.paradox.metadata.ParadoxDataFile;
import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.parser.nodes.AbstractConditionalNode;
import com.googlecode.paradox.parser.nodes.FieldNode;
import com.googlecode.paradox.planner.ValuesComparator;
import com.googlecode.paradox.planner.nodes.PlanTableNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.utils.SQLStates;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Creates a SELECT plan for execution.
 *
 * @author Leonardo Alves da Costa
 * @version 1.3
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
    private List<Object[]> values;

    /**
     * The conditions to filter values
     */
    private final AbstractConditionalNode condition;

    /**
     * Creates a SELECT plan with conditions.
     *
     * @param condition the conditions to filter results
     */
    public SelectPlan(final AbstractConditionalNode condition) {
        this.condition = condition;
    }

    private static void getConditionalFields(final PlanTableNode table, final Set<Column> columnsToLoad,
                                             final AbstractConditionalNode condition) {
        if (condition != null) {
            final Set<FieldNode> fields = condition.getClauseFields();
            fields.forEach((FieldNode node) -> {
                if (table.isThis(node.getTableName())) {
                    Arrays.stream(table.getTable().getFields())
                            .filter(f -> f.getName().equalsIgnoreCase(node.getName()))
                            .map(Column::new).forEach(columnsToLoad::add);
                }
            });
        }
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
     * Add column from select list.
     *
     * @param node SQL node with column attributes.
     * @throws SQLException search column exception.
     */
    public void addColumn(final FieldNode node) throws SQLException {
        List<ParadoxField> fields = Collections.emptyList();

        for (final PlanTableNode table : this.tables) {
            if (node.getTableName() == null || table.isThis(node.getTableName())) {
                fields = Arrays.stream(table.getTable().getFields())
                        .filter(f -> f.getName().equalsIgnoreCase(node.getName()))
                        .collect(Collectors.toList());
                if (!fields.isEmpty()) {
                    break;
                }
            }
        }

        if (fields.isEmpty()) {
            throw new SQLException(String.format("Invalid column name: '%s'", node.toString()),
                    SQLStates.INVALID_COLUMN.getValue());
        } else if (fields.size() > 1) {
            throw new SQLException(String.format("Column '%s' ambiguous defined.", node.toString()),
                    SQLStates.INVALID_COLUMN.getValue());
        }

        fields.stream().map(Column::new).findFirst().ifPresent((Column c) -> {
            c.setName(node.getAlias());
            this.columns.add(c);
        });
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void execute(final ParadoxConnection connection) throws SQLException {
        if (this.columns.isEmpty() || this.tables.isEmpty()) {
            return;
        }

        final List<Column> columnsLoaded = new ArrayList<>();
        final List<List<Object[]>> rawData = new ArrayList<>();
        for (final PlanTableNode table : this.tables) {
            // From columns in SELECT clause.
            final Set<Column> columnsToLoad =
                    this.columns.stream().filter(c -> c.isThis(table.getTable()))
                            .collect(Collectors.toSet());

            // Fields from WHERE clause.
            getConditionalFields(table, columnsToLoad, this.condition);

            // Get fields from other tables join.
            for (final PlanTableNode tableToField : this.tables) {
                getConditionalFields(table, columnsToLoad, tableToField.getConditionalJoin());
            }

            // If there is a column to load.
            if (!columnsToLoad.isEmpty()) {
                columnsLoaded.addAll(columnsToLoad);

                final List<Object[]> tableData = TableData.loadData(table.getTable(),
                        columnsToLoad.stream().map(Column::getField).toArray(ParadoxField[]::new));

                rawData.add(tableData);
            }
        }

        // Stop here if there is no value to process.
        if (rawData.isEmpty() || rawData.stream().allMatch(List::isEmpty)) {
            return;
        }

        setIndexes(columnsLoaded);

        // Find column indexes.
        final int[] mapColumns = mapColumnIndexes(columnsLoaded);
        final Object[] row = new Object[columnsLoaded.size()];
        final ValuesComparator comparator = new ValuesComparator(connection);

        this.values = new ArrayList<>(rawData.get(0).size());
        filter(rawData, 0, row, 0, mapColumns, comparator);
    }

    private void setIndexes(List<Column> columns) throws SQLException {
        // Set conditional indexes.
        if (this.condition != null) {
            this.condition.setFieldIndexes(columns, tables);
        }

        // Set table join indexes.
        for (final PlanTableNode table : this.tables) {
            if (table.getConditionalJoin() != null) {
                table.getConditionalJoin().setFieldIndexes(columns, tables);
            }
        }
    }

    private int[] mapColumnIndexes(final List<Column> loadedColumns) {
        final int[] mapColumns = new int[this.columns.size()];
        for (int i = 0; i < this.columns.size(); i++) {
            final Column column = this.columns.get(i);
            for (int loop = 0; loop < loadedColumns.size(); loop++) {
                if (loadedColumns.get(loop).getField().equals(column.getField())) {
                    mapColumns[i] = loop;
                    break;
                }
            }
        }

        return mapColumns;
    }

    private void filter(final List<List<Object[]>> tables, final int tableIndex, final Object[] row,
                        final int rowIndex, final int[] mapColumns, final ValuesComparator comparator) {

        List<Object[]> rowValues = tables.get(tableIndex);
        mainLoop:
        for (final Object[] tableRow : rowValues) {
            // Fill row.
            System.arraycopy(tableRow, 0, row, rowIndex, tableRow.length);

            // Last table?
            if (tableIndex + 1 == tables.size()) {
                // Filter FROM joins.
                for (final PlanTableNode table : this.tables) {
                    if (table.getConditionalJoin() != null && !table.getConditionalJoin().evaluate(row, comparator)) {
                        // FIXME move to table load. Here is not possible to do LEFT or RIGHT join.
                        continue mainLoop;
                    }
                }

                // Filter WHERE joins.
                if (condition != null && !condition.evaluate(row, comparator)) {
                    continue;
                }

                final Object[] finalRow = new Object[mapColumns.length];
                for (int i = 0; i < mapColumns.length; i++) {
                    int index = mapColumns[i];
                    finalRow[i] = row[index];
                }
                this.values.add(finalRow);
            } else {
                // There is more tables.
                filter(tables, tableIndex + 1, row, rowIndex + tableRow.length, mapColumns, comparator);
            }
        }
    }

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
    public List<Object[]> getValues() {
        return this.values;
    }
}
