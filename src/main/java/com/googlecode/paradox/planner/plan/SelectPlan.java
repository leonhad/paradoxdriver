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
import com.googlecode.paradox.planner.FieldValueUtils;
import com.googlecode.paradox.planner.nodes.*;
import com.googlecode.paradox.planner.nodes.join.ANDNode;
import com.googlecode.paradox.planner.nodes.join.AbstractJoinNode;
import com.googlecode.paradox.planner.nodes.join.ORNode;
import com.googlecode.paradox.planner.sorting.OrderByComparator;
import com.googlecode.paradox.planner.sorting.OrderType;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Creates a SELECT plan for execution.
 *
 * @version 1.10
 * @since 1.1
 */
public final class SelectPlan implements Plan {

    /**
     * The columns in this plan to show in result set.
     */
    private final List<Column> columns = new ArrayList<>();
    /**
     * The columns to load in this plan, not only in result set.
     */
    private final List<Column> columnsFromFunctions = new ArrayList<>();
    /**
     * The tables in this plan.
     */
    private final List<PlanTableNode> tables = new ArrayList<>();
    /**
     * If this result needs to be distinct.
     */
    private final boolean distinct;
    /**
     * Order by fields.
     */
    private final List<Column> orderByFields = new ArrayList<>();
    /**
     * Order type.
     */
    private final List<OrderType> orderTypes = new ArrayList<>();
    /**
     * The data values.
     */
    private List<Object[]> values = new ArrayList<>(0x7F);
    /**
     * The conditions to filter values
     */
    private AbstractConditionalNode condition;
    /**
     * If this statement was cancelled.
     */
    private boolean cancelled;

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

    private static void getConditionalFields(final PlanTableNode table, final Set<Column> columnsToLoad,
                                             final AbstractConditionalNode condition) {
        if (condition != null) {
            final Set<FieldNode> fields = condition.getClauseFields();
            fields.forEach((FieldNode node) -> {
                if (table.isThis(node.getTableName())) {
                    Arrays.stream(table.getTable().getFields())
                            .filter(f -> f.getName().equalsIgnoreCase(node.getName())).map(Column::new)
                            .forEach(columnsToLoad::add);
                }
            });
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
        return tables.stream().filter(t -> table.equals(t.getTable())).findFirst().orElse(null);
    }

    private List<Object[]> processJoinByType(final ParadoxConnection connection, final List<Column> columnsLoaded,
                                             final List<Object[]> rawData, final PlanTableNode table,
                                             final List<Object[]> tableData, final Object[] parameters,
                                             final ParadoxType[] parameterTypes) throws SQLException {
        List<Object[]> localValues;
        switch (table.getJoinType()) {
            case RIGHT:
                localValues = processRightJoin(connection, columnsLoaded, rawData, table, tableData, parameters,
                        parameterTypes);
                break;
            case LEFT:
                localValues = processLeftJoin(connection, columnsLoaded, rawData, table, tableData, parameters,
                        parameterTypes);
                break;
            case FULL:
                localValues = processFullJoin(connection, columnsLoaded, rawData, table, tableData, parameters,
                        parameterTypes);
                break;
            default:
                // CROSS and INNER joins.
                localValues = processInnerJoin(connection, columnsLoaded, rawData, table, tableData, parameters,
                        parameterTypes);
                break;
        }
        return localValues;
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
        } else if (node instanceof FunctionNode) {
            final List<Column> columnsToProcess = getParadoxFields(node);

            // The fist column is always the function column.
            final Column column = columnsToProcess.get(0);
            column.setName(node.getAlias());
            columns.add(column);
            this.columnsFromFunctions.addAll(columnsToProcess);
        } else {
            final List<Column> columnsToProcess = getParadoxFields(node);
            columnsToProcess.forEach((Column column) -> {
                column.setName(node.getAlias());
                this.columns.add(column);
                this.columnsFromFunctions.add(column);
            });
        }
    }

    private List<Column> getParadoxFields(final FieldNode node) throws ParadoxException {
        final List<Column> ret = new ArrayList<>();

        if (node instanceof FunctionNode) {
            final FunctionNode functionNode = (FunctionNode) node;

            // Create the column for the function.
            ret.add(new Column(functionNode));

            // Parses function fields in function parameters.
            for (final FieldNode field : functionNode.getFields()) {
                ret.addAll(getParadoxFields(field));
            }
        } else if (!(node instanceof ValueNode) && !(node instanceof ParameterNode)) {
            for (final PlanTableNode table : this.tables) {
                if (node.getTableName() == null || table.isThis(node.getTableName())) {
                    node.setTable(table.getTable());
                    ret.addAll(Arrays.stream(table.getTable().getFields())
                            .filter(f -> f.getName().equalsIgnoreCase(node.getName()))
                            .map(Column::new)
                            .collect(Collectors.toList()));
                }
            }

            if (ret.isEmpty()) {
                throw new ParadoxException(ParadoxException.Error.INVALID_COLUMN, node.getPosition(), node.toString());
            } else if (ret.size() > 1) {
                throw new ParadoxException(ParadoxException.Error.COLUMN_AMBIGUOUS_DEFINED, node.getPosition(),
                        node.toString());
            }
        }

        return ret;
    }

    public void addOrderColumn(final FieldNode node, final OrderType type) throws SQLException {
        getParadoxFields(node).forEach(column -> addOrderColumn(column, type));
    }

    public void addOrderColumn(final Column column, final OrderType type) {
        this.orderByFields.add(column);
        this.orderTypes.add(type);

        if (!this.columns.contains(column)) {
            // If not in SELECT statement, add as a hidden column in ResultSet.
            column.setHidden(true);
            this.columns.add(column);
        }
    }

    private List<Object[]> processInnerJoin(final ParadoxConnection connection, final List<Column> columnsLoaded,
                                            final List<Object[]> rawData, final PlanTableNode table,
                                            final List<Object[]> tableData, final Object[] parameters,
                                            final ParadoxType[] parameterTypes) throws SQLException {
        final Object[] column = new Object[columnsLoaded.size()];
        final List<Object[]> localValues = new ArrayList<>(100);

        for (final Object[] cols : rawData) {
            System.arraycopy(cols, 0, column, 0, cols.length);

            for (final Object[] newCols : tableData) {
                checkCancel();
                System.arraycopy(newCols, 0, column, cols.length, newCols.length);

                if (table.getConditionalJoin() != null
                        && !table.getConditionalJoin().evaluate(connection, column, parameters, parameterTypes,
                        columnsLoaded)) {
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
    @SuppressWarnings({"java:S3776", "java:S1541"})
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

                if (planTableNode != null && (planTableNode.getJoinType() == JoinType.CROSS
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
                                           final List<Object[]> tableData, final Object[] parameters,
                                           final ParadoxType[] parameterTypes) throws SQLException {
        final Object[] column = new Object[columnsLoaded.size()];
        final List<Object[]> localValues = new ArrayList<>(100);

        for (final Object[] cols : rawData) {
            System.arraycopy(cols, 0, column, 0, cols.length);

            boolean changed = false;
            for (final Object[] newCols : tableData) {
                checkCancel();

                System.arraycopy(newCols, 0, column, cols.length, newCols.length);

                if (table.getConditionalJoin() != null
                        && !table.getConditionalJoin().evaluate(connection, column, parameters, parameterTypes,
                        columnsLoaded)) {
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
                                            final List<Object[]> tableData, final Object[] parameters,
                                            final ParadoxType[] parameterTypes) throws SQLException {
        final Object[] column = new Object[columnsLoaded.size()];
        final List<Object[]> localValues = new ArrayList<>(100);

        for (final Object[] newCols : tableData) {
            System.arraycopy(newCols, 0, column, column.length - newCols.length, newCols.length);

            boolean changed = false;
            for (final Object[] cols : rawData) {
                checkCancel();

                System.arraycopy(cols, 0, column, 0, cols.length);

                if (table.getConditionalJoin() != null
                        && !table.getConditionalJoin().evaluate(connection, column, parameters, parameterTypes,
                        columnsLoaded)) {
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
                                           final List<Object[]> tableData, final Object[] parameters,
                                           final ParadoxType[] parameterTypes) throws SQLException {
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

                if (table.getConditionalJoin() != null && !table.getConditionalJoin().evaluate(connection, column,
                        parameters, parameterTypes, columnsLoaded)) {
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
    @SuppressWarnings({"java:S3776", "java:S1541"})
    public void execute(final ParadoxConnection connection, final int maxRows, final Object[] parameters,
                        final ParadoxType[] parameterTypes) throws SQLException {

        // Can't do anything without fields defined.
        if (this.columns.isEmpty()) {
            return;
        }

        // Reset the cancelled state.
        cancelled = false;

        final List<Column> columnsLoaded = new ArrayList<>();
        final List<Object[]> rawData = new ArrayList<>(100);

        for (int tableIndex = 0; tableIndex < this.tables.size(); tableIndex++) {
            PlanTableNode table = this.tables.get(tableIndex);
            checkCancel();

            // Columns in SELECT clause.
            final Set<Column> tableColumns = this.columns.stream()
                    .filter(c -> c.isThis(table.getTable()))
                    .collect(Collectors.toSet());

            // Columns in columns to load (hidden columns).
            tableColumns.addAll(this.columnsFromFunctions.stream()
                    .filter(c -> c.isThis(table.getTable()))
                    .collect(Collectors.toSet()));

            // Columns in ORDER BY clause.
            tableColumns.addAll(this.orderByFields.stream()
                    .filter(c -> c.isThis(table.getTable()))
                    .collect(Collectors.toSet()));

            // Fields from WHERE clause.
            getConditionalFields(table, tableColumns, this.condition);

            // Get fields from other tables join.
            for (final PlanTableNode tableToField : this.tables) {
                getConditionalFields(table, tableColumns, tableToField.getConditionalJoin());
            }

            // If there is a column to load.
            if (tableColumns.isEmpty()) {
                // Force the table loading (used in joins).
                tableColumns.add(new Column(table.getTable().getFields()[0]));
            }

            columnsLoaded.addAll(tableColumns);

            final List<Object[]> tableData = TableData.loadData(table.getTable(),
                    tableColumns.stream().map(Column::getField).toArray(ParadoxField[]::new));
            if (table.getConditionalJoin() != null) {
                table.getConditionalJoin().setFieldIndexes(columnsLoaded, this.tables);
            }

            // First table
            if (tableIndex == 0) {
                if (table.getConditionalJoin() != null) {
                    // Filter WHERE joins.
                    final Iterator<Object[]> i = tableData.iterator();
                    while (i.hasNext()) {
                        Object[] tableRow = i.next();
                        if (!table.getConditionalJoin().evaluate(connection, tableRow, parameters,
                                parameterTypes, columnsLoaded)) {
                            i.remove();
                        }
                    }
                }

                rawData.addAll(tableData);
            } else {
                final List<Object[]> localValues = processJoinByType(connection, columnsLoaded, rawData, table,
                        tableData, parameters, parameterTypes);

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
        setFunctionIndexes(columnsLoaded);

        // Find column indexes.
        final int[] mapColumns = mapColumnIndexes(columnsLoaded);

        filter(connection, rawData, mapColumns, maxRows, parameters, parameterTypes, columnsLoaded);

        processOrderBy();

        if (!orderByFields.isEmpty() && maxRows != 0 && values.size() > maxRows) {
            values = values.subList(0, maxRows);
        }
    }

    private void processOrderBy() {
        if (orderByFields.isEmpty()) {
            // Nothing to do here, there are no order by fields.
            return;
        }

        final int[] mapColumns = new int[this.orderByFields.size()];
        Arrays.fill(mapColumns, -1);
        for (int i = 0; i < this.orderByFields.size(); i++) {
            final Column column = this.orderByFields.get(i);
            mapColumns[i] = column.getIndex();
            for (int loop = 0; loop < this.columns.size(); loop++) {
                if (this.columns.get(loop).equals(column)) {
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

        values.sort(comparator);
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

    private void setFunctionIndexes(final List<Column> columnsLoaded) throws SQLException {
        for (final Column column : this.columns) {
            if (column.getFunction() != null) {
                FieldValueUtils.setFunctionIndexes(column.getFunction(), columnsLoaded, this.tables);
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
                        final int maxRows, final Object[] parameters, final ParadoxType[] parameterTypes,
                        final List<Column> columnsLoaded) throws SQLException {

        for (final Object[] tableRow : rowValues) {
            checkCancel();

            // Filter WHERE joins.
            if (condition != null && !condition.evaluate(connection, tableRow, parameters, parameterTypes,
                    columnsLoaded)) {
                continue;
            }

            final Object[] finalRow = new Object[mapColumns.length];
            for (int i = 0; i < mapColumns.length; i++) {
                int index = mapColumns[i];
                if (index != -1) {
                    // A field mapped value.
                    finalRow[i] = tableRow[index];
                } else {
                    FunctionNode functionNode = this.columns.get(i).getFunction();
                    if (functionNode == null) {
                        // A fixed value.
                        finalRow[i] = this.columns.get(i).getValue();
                    } else {
                        // A function processed value.
                        if (!functionNode.isGrouping()) {
                            // Not process grouping function by now.
                            finalRow[i] = functionNode.execute(connection, tableRow, parameters, parameterTypes,
                                    columnsLoaded);
                            // The function may change the result type.
                            this.columns.get(i).setType(functionNode.getType());
                        }
                    }
                }
            }

            if (!distinct || !isRowRepeated(finalRow)) {
                this.values.add(finalRow);
            }

            if (orderByFields.isEmpty() && maxRows != 0 && values.size() == maxRows) {
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
        return distinct == that.distinct && Objects.equals(columns, that.columns) && Objects.equals(tables, that.tables)
                && Objects.equals(condition, that.condition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columns, tables, distinct, condition);
    }
}
