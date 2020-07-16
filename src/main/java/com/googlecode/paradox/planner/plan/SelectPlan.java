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
import com.googlecode.paradox.exceptions.ParadoxException;
import com.googlecode.paradox.metadata.ParadoxDataFile;
import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.metadata.ParadoxTable;
import com.googlecode.paradox.parser.nodes.AbstractConditionalNode;
import com.googlecode.paradox.parser.nodes.JoinType;
import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.planner.nodes.PlanTableNode;
import com.googlecode.paradox.planner.nodes.ValueNode;
import com.googlecode.paradox.planner.nodes.join.ANDNode;
import com.googlecode.paradox.planner.nodes.join.AbstractJoinNode;
import com.googlecode.paradox.planner.nodes.join.ORNode;
import com.googlecode.paradox.planner.sorting.OrderByComparator;
import com.googlecode.paradox.planner.sorting.OrderType;
import com.googlecode.paradox.results.Column;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Creates a SELECT plan for execution.
 *
 * @version 1.9
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
    private final List<Object[]> values = new ArrayList<>(50);

    /**
     * If this result needs to be distinct.
     */
    private final boolean distinct;
    /**
     * The conditions to filter values
     */
    private AbstractConditionalNode condition;
    /**
     * If this statement was cancelled.
     */
    private boolean cancelled;
    /**
     * Order by fields.
     */
    private final List<Column> orderByFields = new ArrayList<>();
    /**
     * Order type.
     */
    private final List<OrderType> orderTypes = new ArrayList<>();

    /**
     * Creates a SELECT plan with conditions.
     *
     * @param condition the conditions to filter results.
     * @param distinct  if this SELECT uses DISTINCT.
     */
    public SelectPlan(final AbstractConditionalNode condition, final boolean distinct) {
        this.condition = condition;
        this.distinct = distinct;
    }

    private static AbstractConditionalNode reduce(final AbstractConditionalNode node) {
        AbstractConditionalNode ret = node;

        // It is an AND and OR node?
        if (node instanceof AbstractJoinNode) {
            final List<SQLNode> children = node.getChildren();

            // Reduce all children.
            for (int loop = 0; loop < children.size(); loop++) {
                children.set(loop, reduce((AbstractConditionalNode) children.get(loop)));
            }

            // Reduce only AND and OR nodes.
            while (ret instanceof AbstractJoinNode && ret.getChildren().size() <= 1) {
                if (ret.getChildren().isEmpty()) {
                    ret = null;
                } else {
                    ret = (AbstractConditionalNode) ret.getChildren().get(0);
                }
            }
        }

        return ret;
    }

    private static void addAndClause(final PlanTableNode table, SQLNode clause) {
        if (table.getConditionalJoin() instanceof ANDNode) {
            // Exists and it is an AND node.
            table.getConditionalJoin().addChild(clause);
        } else if (table.getConditionalJoin() != null) {
            // Exists, but any other type.
            final ANDNode andNode = new ANDNode(table.getConditionalJoin(), null);
            andNode.addChild(clause);
            table.setConditionalJoin(andNode);
        } else {
            // There is no conditionals in this table.
            table.setConditionalJoin((AbstractConditionalNode) clause);
        }
    }

    /**
     * Optimize the joins clause.
     */
    @Override
    public void compile() {
        if (optimizeConditions(condition)) {
            condition = null;
        }

        // Reduce default conditions.
        condition = reduce(condition);

        // Reduce table conditions.
        for (final PlanTableNode table : this.tables) {
            table.setConditionalJoin(reduce(table.getConditionalJoin()));
        }
    }

    private int getTableIndex(final ParadoxTable table) {
        int index = -1;
        for (int i = 0; i < this.tables.size(); i++) {
            if (this.tables.get(i).getTable().equals(table)) {
                index = i;
                break;
            }
        }

        return index;
    }

    private PlanTableNode getPlanTable(final ParadoxTable table) {
        return tables.stream()
                .filter(t -> table.equals(t.getTable()))
                .findFirst().orElse(null);
    }

    private List<Object[]> processJoinByType(final ParadoxConnection connection, final List<Column> columnsLoaded,
                                             final List<Object[]> rawData, final PlanTableNode table,
                                             final List<Object[]> tableData, final Object[] parameters)
            throws SQLException {
        List<Object[]> localValues;
        switch (table.getJoinType()) {
            case RIGHT:
                localValues = processRightJoin(connection, columnsLoaded, rawData, table, tableData, parameters);
                break;
            case LEFT:
                localValues = processLeftJoin(connection, columnsLoaded, rawData, table, tableData, parameters);
                break;
            case FULL:
                localValues = processFullJoin(connection, columnsLoaded, rawData, table, tableData, parameters);
                break;
            default:
                // CROSS and INNER joins.
                localValues = processInnerJoin(connection, columnsLoaded, rawData, table, tableData, parameters);
                break;
        }
        return localValues;
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
        if (node instanceof ValueNode) {
            this.columns.add(new Column((ValueNode) node));
            return;
        }

        final List<ParadoxField> fields = getParadoxFields(node);
        fields.stream().map(Column::new).findFirst().ifPresent((Column c) -> {
            c.setName(node.getAlias());
            this.columns.add(c);
        });
    }

    private List<ParadoxField> getParadoxFields(final FieldNode node) throws ParadoxException {
        final List<ParadoxField> fields = new ArrayList<>();

        for (final PlanTableNode table : this.tables) {
            if (node.getTableName() == null || table.isThis(node.getTableName())) {
                fields.addAll(Arrays.stream(table.getTable().getFields())
                        .filter(f -> f.getName().equalsIgnoreCase(node.getName()))
                        .collect(Collectors.toList()));
            }
        }

        if (fields.isEmpty()) {
            throw new ParadoxException(ParadoxException.Error.INVALID_COLUMN, node.toString(), node.getPosition());
        } else if (fields.size() > 1) {
            throw new ParadoxException(ParadoxException.Error.COLUMN_AMBIGUOUS_DEFINED, node.toString(),
                    node.getPosition());
        }

        return fields;
    }

    public void addOrderColumn(final FieldNode node, final OrderType type) throws SQLException {
        final List<ParadoxField> fields = getParadoxFields(node);
        fields.stream().map(Column::new).findFirst().ifPresent((Column c) -> {
            c.setName(node.getAlias());
            this.orderByFields.add(c);
            this.orderTypes.add(type);
        });
    }

    public void addOrderColumn(final Column column, final OrderType type) {
        this.orderByFields.add(column);
        this.orderTypes.add(type);
    }

    private List<Object[]> processInnerJoin(final ParadoxConnection connection, final List<Column> columnsLoaded,
                                            final List<Object[]> rawData, final PlanTableNode table,
                                            final List<Object[]> tableData, final Object[] parameters)
            throws SQLException {
        final Object[] column = new Object[columnsLoaded.size()];
        final List<Object[]> localValues = new ArrayList<>(100);

        for (final Object[] cols : rawData) {
            System.arraycopy(cols, 0, column, 0, cols.length);

            for (final Object[] newCols : tableData) {
                checkCancel();
                System.arraycopy(newCols, 0, column, cols.length, newCols.length);

                if (table.getConditionalJoin() != null && !table.getConditionalJoin()
                        .evaluate(connection, column, parameters)) {
                    continue;
                }

                localValues.add(column.clone());
            }
        }

        return localValues;
    }

    /**
     * Process the node and change it to it's table (if it is possible).
     *
     * @param node the node to process.
     * @return <code>true</code> if the node is processed and needed to be removed.
     */
    private boolean optimizeConditions(final SQLNode node) {
        if (node instanceof ANDNode) {
            ANDNode andNode = (ANDNode) node;
            andNode.getChildren().removeIf(this::optimizeConditions);
            return node.getClauseFields().isEmpty();
        } else if (node != null && !(node instanceof ORNode)) {
            // Don't process OR nodes.

            final List<ParadoxField> conditionalFields = new ArrayList<>();

            final Set<FieldNode> fields = node.getClauseFields();
            fields.forEach((FieldNode fn) -> {
                for (final PlanTableNode table : this.tables) {
                    if (table.isThis(fn.getTableName())) {
                        conditionalFields.addAll(Arrays.stream(table.getTable().getFields())
                                .filter(f -> f.getName().equalsIgnoreCase(fn.getName()))
                                .collect(Collectors.toSet()));
                    }
                }
            });

            if (conditionalFields.size() == 1) {
                // FIELD = VALUE

                final ParadoxTable paradoxTable = conditionalFields.get(0).getTable();
                final PlanTableNode planTableNode = getPlanTable(paradoxTable);

                if (planTableNode != null &&
                        (planTableNode.getJoinType() == JoinType.CROSS
                                || planTableNode.getJoinType() == JoinType.INNER)) {
                    // Do not change OUTER joins.
                    addAndClause(planTableNode, node);
                    return true;
                }
            } else if (conditionalFields.size() > 1) {
                // FIELD = FIELD
                final ParadoxTable paradoxTable1 = conditionalFields.get(0).getTable();
                final ParadoxTable paradoxTable2 = conditionalFields.get(1).getTable();

                final int index1 = getTableIndex(paradoxTable1);
                final int index2 = getTableIndex(paradoxTable2);

                // Both tables exists?
                if (index1 != -1 && index2 != -1) {
                    // Use the last table to
                    int lastIndex = Math.max(index1, index2);

                    addAndClause(this.tables.get(lastIndex), node);
                    return true;
                }
            }
        }

        // Unprocessed.
        return false;
    }

    private List<Object[]> processLeftJoin(final ParadoxConnection connection, final List<Column> columnsLoaded,
                                           final List<Object[]> rawData, final PlanTableNode table,
                                           final List<Object[]> tableData, final Object[] parameters)
            throws SQLException {
        final Object[] column = new Object[columnsLoaded.size()];
        final List<Object[]> localValues = new ArrayList<>(100);

        for (final Object[] cols : rawData) {
            System.arraycopy(cols, 0, column, 0, cols.length);

            boolean changed = false;
            for (final Object[] newCols : tableData) {
                checkCancel();

                System.arraycopy(newCols, 0, column, cols.length, newCols.length);

                if (table.getConditionalJoin() != null && !table.getConditionalJoin()
                        .evaluate(connection, column, parameters)) {
                    continue;
                }

                changed = true;
                localValues.add(column.clone());
            }

            if (!changed) {
                Arrays.fill(column, cols.length, column.length, null);
                localValues.add(column.clone());
            }
        }
        return localValues;
    }

    private List<Object[]> processRightJoin(final ParadoxConnection connection, final List<Column> columnsLoaded,
                                            final List<Object[]> rawData, final PlanTableNode table,
                                            final List<Object[]> tableData, final Object[] parameters)
            throws SQLException {
        final Object[] column = new Object[columnsLoaded.size()];
        final List<Object[]> localValues = new ArrayList<>(100);

        for (final Object[] newCols : tableData) {
            System.arraycopy(newCols, 0, column, column.length - newCols.length, newCols.length);

            boolean changed = false;
            for (final Object[] cols : rawData) {
                checkCancel();

                System.arraycopy(cols, 0, column, 0, cols.length);

                if (table.getConditionalJoin() != null && !table.getConditionalJoin()
                        .evaluate(connection, column, parameters)) {
                    continue;
                }

                changed = true;
                localValues.add(column.clone());
            }

            if (!changed) {
                Arrays.fill(column, 0, column.length - newCols.length, null);
                localValues.add(column.clone());
            }
        }

        return localValues;
    }

    private List<Object[]> processFullJoin(final ParadoxConnection connection, final List<Column> columnsLoaded,
                                           final List<Object[]> rawData, final PlanTableNode table,
                                           final List<Object[]> tableData, final Object[] parameters)
            throws SQLException {
        final Object[] column = new Object[columnsLoaded.size()];
        final List<Object[]> localValues = new ArrayList<>(100);

        Set<Integer> inLeft = new HashSet<>();
        for (final Object[] cols : rawData) {
            System.arraycopy(cols, 0, column, 0, cols.length);

            boolean changed = false;
            for (int i = 0; i < tableData.size(); i++) {
                checkCancel();

                final Object[] newCols = tableData.get(i);
                System.arraycopy(newCols, 0, column, cols.length, newCols.length);

                if (table.getConditionalJoin() != null && !table.getConditionalJoin()
                        .evaluate(connection, column, parameters)) {
                    continue;
                }

                inLeft.add(i);
                changed = true;
                localValues.add(column.clone());
            }

            if (!changed) {
                Arrays.fill(column, cols.length, column.length, null);
                localValues.add(column.clone());
            }
        }

        // Itens not used in left join.
        Arrays.fill(column, 0, column.length, null);
        for (int i = 0; i < tableData.size(); i++) {
            checkCancel();

            if (!inLeft.contains(i)) {
                final Object[] newCols = tableData.get(i);
                System.arraycopy(newCols, 0, column, column.length - newCols.length, newCols.length);
                localValues.add(column.clone());
            }
        }
        return localValues;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void execute(final ParadoxConnection connection, final int maxRows, final Object[] parameters)
            throws SQLException {
        if (this.columns.isEmpty()) {
            return;
        }
        cancelled = false;

        final List<Column> columnsLoaded = new ArrayList<>();
        final List<Object[]> rawData = new ArrayList<>(100);
        for (final PlanTableNode table : this.tables) {
            checkCancel();

            // From columns in SELECT clause.
            final Set<Column> columnsToLoad =
                    this.columns.stream().filter(c -> c.isThis(table.getTable()))
                            .collect(Collectors.toSet());

            // From columns in ORDER BY clause.
            columnsToLoad.addAll(this.orderByFields.stream().filter(c -> c.isThis(table.getTable()))
                    .collect(Collectors.toSet()));

            // Fields from WHERE clause.
            getConditionalFields(table, columnsToLoad, this.condition);

            // Get fields from other tables join.
            for (final PlanTableNode tableToField : this.tables) {
                getConditionalFields(table, columnsToLoad, tableToField.getConditionalJoin());
            }

            // If there is a column to load.
            if (columnsToLoad.isEmpty()) {
                // Force the table loading (used in joins).
                columnsToLoad.add(new Column(table.getTable().getFields()[0]));
            }

            columnsLoaded.addAll(columnsToLoad);

            final List<Object[]> tableData = TableData.loadData(table.getTable(), columnsToLoad.stream()
                    .map(Column::getField).toArray(ParadoxField[]::new));
            if (table.getConditionalJoin() != null) {
                table.getConditionalJoin().setFieldIndexes(columnsLoaded, this.tables);
            }

            // First table
            if (rawData.isEmpty()) {
                if (table.getConditionalJoin() != null) {
                    // Filter WHERE joins.
                    tableData.removeIf(tableRow -> !table.getConditionalJoin()
                            .evaluate(connection, tableRow, parameters));
                }

                rawData.addAll(tableData);
            } else {
                final List<Object[]> localValues = processJoinByType(connection, columnsLoaded, rawData, table,
                        tableData, parameters);

                rawData.clear();
                rawData.addAll(localValues);
            }
        }

        // Stop here if there is no value to process.
        if (tables.isEmpty()) {
            final Object[] row = new Object[this.columns.size()];
            for (int i = 0; i < row.length; i++) {
                // A list of fixed value.
                row[i] = this.columns.get(i).getValue();
            }

            rawData.add(row);
        } else if (rawData.isEmpty()) {
            return;
        }

        setIndexes(columnsLoaded);

        // Find column indexes.
        final int[] mapColumns = mapColumnIndexes(columnsLoaded);

        processOrderBy(rawData, columnsLoaded);
        filter(connection, rawData, mapColumns, maxRows, parameters);
    }

    private void processOrderBy(final List<Object[]> rawData, final List<Column> loadedColumns) {
        if (orderByFields.isEmpty()) {
            // Nothing to do here, there are no order by fields.
            return;
        }

        final int[] mapColumns = new int[this.orderByFields.size()];
        Arrays.fill(mapColumns, -1);
        for (int i = 0; i < this.orderByFields.size(); i++) {
            final Column column = this.orderByFields.get(i);
            for (int loop = 0; loop < loadedColumns.size(); loop++) {
                if (loadedColumns.get(loop).getField().equals(column.getField())) {
                    mapColumns[i] = loop;
                    break;
                }
            }
        }

        // Build the comparator list.
        Comparator<Object[]> comparator = null;
        for (int i = 0; i < mapColumns.length; i++) {
            final int index = mapColumns[i];
            final OrderByComparator orderByComparator = new OrderByComparator(index, orderTypes.get(i));
            if (comparator == null) {
                comparator = orderByComparator;
            } else {
                comparator = comparator.thenComparing(orderByComparator);
            }
        }

        rawData.sort(comparator);
    }

    private void setIndexes(final List<Column> columns) throws SQLException {
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
        Arrays.fill(mapColumns, -1);
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

    private void filter(final ParadoxConnection connection, final List<Object[]> rowValues, final int[] mapColumns,
                        final int maxRows, final Object[] parameters) throws SQLException {

        for (final Object[] tableRow : rowValues) {
            checkCancel();

            // Filter WHERE joins.
            if (condition != null && !condition.evaluate(connection, tableRow, parameters)) {
                continue;
            }

            final Object[] finalRow = new Object[mapColumns.length];
            for (int i = 0; i < mapColumns.length; i++) {
                int index = mapColumns[i];
                if (index != -1) {
                    // A field mapped value.
                    finalRow[i] = tableRow[index];
                } else {
                    // A fixed value.
                    finalRow[i] = this.columns.get(i).getValue();
                }
            }

            if (!distinct || !isRowRepeated(finalRow)) {
                this.values.add(finalRow);
            }

            if (maxRows != 0 && values.size() == maxRows) {
                // Stop loading on max rows limit.
                break;
            }
        }
    }

    /**
     * Validate if this row is already in values list.
     *
     * @param row the row to check.
     * @return <code>true</code> if row is already in values list.
     */
    private boolean isRowRepeated(final Object[] row) {
        boolean ret = false;
        for (final Object[] currentRow : this.values) {
            if (Arrays.deepEquals(currentRow, row)) {
                ret = true;
                break;
            }
        }

        return ret;
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

    /**
     * Gets the statements WHERE conditions.
     *
     * @return the statements WHERE conditions.
     */
    public AbstractConditionalNode getCondition() {
        return condition;
    }

    /**
     * Gets the order by fields.
     *
     * @return the order by fields.
     */
    public List<Column> getOrderByFields() {
        return orderByFields;
    }

    /**
     * Cancel this statement execution.
     */
    @Override
    public void cancel() {
        cancelled = true;
    }

    /**
     * Check if this execution was cancelled.
     *
     * @throws SQLException if this execution was cancelled.
     */
    private void checkCancel() throws SQLException {
        if (cancelled) {
            cancelled = false;
            throw new ParadoxException(ParadoxException.Error.OPERATION_CANCELLED);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SelectPlan that = (SelectPlan) o;
        return distinct == that.distinct &&
                Objects.equals(columns, that.columns) &&
                Objects.equals(tables, that.tables) &&
                Objects.equals(condition, that.condition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columns, tables, distinct, condition);
    }
}
