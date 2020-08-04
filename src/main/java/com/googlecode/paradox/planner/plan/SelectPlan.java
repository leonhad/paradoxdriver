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

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.data.TableData;
import com.googlecode.paradox.exceptions.ParadoxDataException;
import com.googlecode.paradox.exceptions.ParadoxException;
import com.googlecode.paradox.exceptions.ParadoxSyntaxErrorException;
import com.googlecode.paradox.exceptions.SyntaxError;
import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.metadata.ParadoxTable;
import com.googlecode.paradox.parser.nodes.*;
import com.googlecode.paradox.planner.FieldValueUtils;
import com.googlecode.paradox.planner.nodes.*;
import com.googlecode.paradox.planner.nodes.join.ANDNode;
import com.googlecode.paradox.planner.nodes.join.ORNode;
import com.googlecode.paradox.planner.sorting.OrderByComparator;
import com.googlecode.paradox.planner.sorting.OrderType;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.rowset.ValuesConverter;
import com.googlecode.paradox.utils.FunctionalUtils;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.googlecode.paradox.utils.FunctionalUtils.functionWrapper;
import static com.googlecode.paradox.utils.FunctionalUtils.predicateWrapper;

/**
 * Creates a SELECT plan for execution.
 *
 * @version 1.15
 * @since 1.1
 */
@SuppressWarnings({"java:S1448", "java:S1200"})
public final class SelectPlan implements Plan<List<Object[]>> {

    /**
     * The columns in this plan to show in result set.
     */
    private final List<Column> columns;
    /**
     * The columns to load in this plan, not only in result set.
     */
    private final List<Column> columnsFromFunctions = new ArrayList<>();
    /**
     * The tables in this plan.
     */
    private final List<PlanTableNode> tables;
    /**
     * If this result needs to be distinct.
     */
    private final boolean distinct;
    /**
     * Order by fields.
     */
    private final OrderByNode orderBy;
    /**
     * The group by node.
     */
    private final GroupByNode groupBy;
    /**
     * The data values.
     */
    private List<Object[]> values = Collections.emptyList();
    /**
     * The conditions to filter values
     */
    private AbstractConditionalNode condition;
    /**
     * If this statement was cancelled.
     * <p>
     * FIXME move to execution context.
     */
    private boolean cancelled;
    /**
     * Table joiner.
     */
    private final TableJoiner joiner = new TableJoiner();

    /**
     * Creates a SELECT plan.
     *
     * @param connectionInfo the connection info.
     * @param statement      the statement node.
     * @throws ParadoxSyntaxErrorException in case of failures.
     */
    public SelectPlan(final ConnectionInfo connectionInfo, final SelectNode statement) throws SQLException {
        this.condition = statement.getCondition();
        this.distinct = statement.isDistinct();

        // Load the table information.
        this.tables = statement.getTables().stream()
                .map(functionWrapper(table -> new PlanTableNode(connectionInfo, table)))
                .collect(Collectors.toList());

        this.columns = parseColumns(statement);
        this.groupBy = new GroupByNode(statement, this.tables, this.columns);
        this.orderBy = parseOrderBy(statement);

        if (this.columns.isEmpty()) {
            throw new ParadoxSyntaxErrorException(SyntaxError.EMPTY_COLUMN_LIST);
        }
    }

    @Override
    public void optimize() {
        if (optimizeConditions(condition)) {
            condition = null;
        }

        // Optimize default conditions.
        condition = SelectUtils.joinClauses(condition);

        // Optimize table conditions.
        for (final PlanTableNode table : this.tables) {
            table.setConditionalJoin(SelectUtils.joinClauses(table.getConditionalJoin()));
        }
    }

    /**
     * Parses the table columns.
     *
     * @param statement the SELECT statement.
     * @return the statement columns.
     * @throws SQLException in case of parse errors.
     */
    private List<Column> parseColumns(final SelectNode statement) throws SQLException {
        final List<Column> ret = new ArrayList<>();
        for (final SQLNode field : statement.getFields()) {
            if (field instanceof AsteriskNode) {
                if (this.tables.isEmpty()) {
                    throw new ParadoxSyntaxErrorException(SyntaxError.ASTERISK_WITHOUT_TABLE,
                            field.getPosition());
                }
                ret.addAll(parseAsterisk((AsteriskNode) field));
            } else {
                ret.addAll(processColumn((FieldNode) field));
            }
        }

        // Sets the column indexes.
        for (int i = 0; i < ret.size(); i++) {
            ret.get(i).setIndex(i);
        }

        return ret;
    }

    /**
     * Parses the order by fields.
     *
     * @param statement the SELECT statement.
     * @return the order by node.
     * @throws SQLException in case of parse errors.
     */
    private OrderByNode parseOrderBy(final SelectNode statement) throws SQLException {
        final OrderByNode ret = new OrderByNode();
        for (int i = 0; i < statement.getOrder().size(); i++) {
            final FieldNode field = statement.getOrder().get(i);
            final OrderType type = statement.getOrderTypes().get(i);
            if (field instanceof ValueNode) {
                int index = ValuesConverter.getInteger(field.getName());
                if (index > this.columns.size()) {
                    throw new ParadoxException(ParadoxException.Error.INVALID_COLUMN_INDEX, index);
                }

                processOrderColumn(this.columns.get(index - 1), type, ret);
            } else {
                processOrderColumn(field, type, ret);
            }
        }

        ret.checkColumns(groupBy.isGroupBy(), this.columns);
        return ret;
    }

    /**
     * Parses the asterisk fields in SELECT.
     *
     * @param field the asterisk field.
     * @return the columns to add.
     * @throws SQLException in case of parse errors.
     */
    private List<Column> parseAsterisk(final AsteriskNode field) throws SQLException {
        if (field.getTableName() != null) {
            // Add all columns from one table.
            final List<ParadoxTable> tablesFound = this.tables.stream()
                    .filter(t -> t.isThis(field.getTableName()))
                    .map(PlanTableNode::getTable).collect(Collectors.toList());

            if (tablesFound.isEmpty()) {
                throw new ParadoxDataException(ParadoxDataException.Error.TABLE_NOT_FOUND, field.getPosition(),
                        field.getTableName());
            } else if (tablesFound.size() > 1) {
                throw new ParadoxException(ParadoxException.Error.TABLE_AMBIGUOUS_DEFINED, field.getPosition(),
                        field.getTableName());
            }

            return Arrays.stream(tablesFound.get(0).getFields()).map(Column::new).collect(Collectors.toList());
        } else {
            // Add all fields from all tables.
            return this.tables.stream()
                    .map(PlanTableNode::getTable)
                    .map(ParadoxTable::getFields)
                    .flatMap(Arrays::stream)
                    .map(Column::new)
                    .collect(Collectors.toList());
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

    /**
     * Add column from select list.
     *
     * @param node SQL node with column attributes.
     * @throws SQLException search column exception.
     */
    private List<Column> processColumn(final FieldNode node) throws SQLException {
        List<Column> ret;
        if (node instanceof ValueNode) {
            ret = Collections.singletonList(new Column((ValueNode) node));
        } else if (node instanceof ParameterNode) {
            ret = Collections.singletonList(new Column((ParameterNode) node));
        } else if (node instanceof FunctionNode) {
            final List<Column> columnsToProcess = SelectUtils.getParadoxFields(node, this.tables);

            // The fist column is always the function column.
            final Column column = columnsToProcess.get(0);
            column.setName(node.getAlias());
            ret = Collections.singletonList(column);
            this.columnsFromFunctions.addAll(columnsToProcess);
        } else {
            ret = SelectUtils.getParadoxFields(node, this.tables);
            ret.forEach((Column column) -> column.setName(node.getAlias()));
        }

        return ret;
    }

    /**
     * Add a order by column to this plan.
     *
     * @param node        the node to convert to a column.
     * @param type        the order by field type.
     * @param orderByNode the order by node to process.
     * @throws SQLException in case of failures.
     */
    private void processOrderColumn(final FieldNode node, final OrderType type, final OrderByNode orderByNode)
            throws SQLException {
        SelectUtils.getParadoxFields(node, this.tables)
                .forEach((Column column) -> processOrderColumn(column, type, orderByNode));
    }

    /**
     * Add a order by column to this plan.
     *
     * @param column the column to add.
     * @param type   the order by field type.
     * @param node   the order by node to process.
     */
    private void processOrderColumn(final Column column, final OrderType type, final OrderByNode node) {
        node.add(column, type);

        if (!this.columns.contains(column)) {
            // If not in SELECT statement, add as a hidden column in ResultSet.
            column.setHidden(true);
            this.columns.add(column);
        }
    }

    /**
     * Process the node and change it to it's table (if it is possible).
     *
     * @param node the node to process.
     * @return <code>true</code> if the node is processed and needed to be removed.
     */
    @SuppressWarnings({"java:S3776", "java:S1541"})
    private boolean optimizeConditions(final SQLNode node) {
        boolean ret = false;
        if (node instanceof ANDNode) {
            ANDNode andNode = (ANDNode) node;
            andNode.getChildren().removeIf(this::optimizeConditions);
            ret = node.getClauseFields().isEmpty();
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
                    SelectUtils.addAndClause(planTableNode, node);
                    ret = true;
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

                    SelectUtils.addAndClause(this.tables.get(lastIndex), node);
                    ret = true;
                }
            }
        }

        // Unprocessed.
        return ret;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    @SuppressWarnings({"java:S3776", "java:S1541"})
    public List<Object[]> execute(final ConnectionInfo connectionInfo, final int maxRows, final Object[] parameters,
                                  final ParadoxType[] parameterTypes) throws SQLException {

        // Can't do anything without fields defined.
        if (this.columns.isEmpty()) {
            return Collections.emptyList();
        }

        // Reset the cancelled state.
        cancelled = false;
        joiner.reset();

        final List<Column> columnsLoaded = new ArrayList<>();
        final List<Object[]> rawData = new ArrayList<>(0xFF);

        for (int tableIndex = 0; tableIndex < this.tables.size(); tableIndex++) {
            PlanTableNode table = this.tables.get(tableIndex);
            ensureNotCancelled();

            // Columns in SELECT clause.
            final Set<Column> tableColumns = this.columns.stream()
                    .filter(c -> c.isThis(table.getTable()))
                    .collect(Collectors.toSet());

            // Columns in SELECT functions.
            tableColumns.addAll(this.columnsFromFunctions.stream()
                    .filter(c -> c.isThis(table.getTable()))
                    .collect(Collectors.toSet()));

            // Columns in GROUP BY clause.
            tableColumns.addAll(this.groupBy.getColumns(table.getTable()));

            // Columns in ORDER BY clause.
            tableColumns.addAll(this.orderBy.getColumns(table.getTable()));

            // Fields from WHERE clause.
            tableColumns.addAll(SelectUtils.getConditionalFields(table, this.condition));

            // Get fields from other tables join.
            for (final PlanTableNode tableToField : this.tables) {
                tableColumns.addAll(SelectUtils.getConditionalFields(table, tableToField.getConditionalJoin()));
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

            // First table?
            if (tableIndex == 0) {
                if (table.getConditionalJoin() != null) {
                    rawData.addAll(tableData.stream()
                            .filter(predicateWrapper(o -> ensureNotCancelled()))
                            .filter(predicateWrapper(tableRow -> table.getConditionalJoin()
                                    .evaluate(connectionInfo, tableRow, parameters, parameterTypes, columnsLoaded)))
                            .collect(Collectors.toList()));
                } else {
                    // No conditions to process. Just use it.
                    rawData.addAll(tableData);
                }
            } else {
                final List<Object[]> ret = joiner.processJoinByType(connectionInfo, columnsLoaded, rawData, table,
                        tableData, parameters, parameterTypes);

                rawData.clear();
                rawData.addAll(ret);
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
            // No result to process, just return.
            return Collections.emptyList();
        }

        processIndexes(columnsLoaded);
        processFunctionIndexes(columnsLoaded);

        // Process parameter types.
        columns.stream()
                .filter(column -> column.getParameter() != null)
                .forEach(column -> column.setType(parameterTypes[column.getParameter().getParameterIndex()]));

        // Find column indexes.
        final int[] mapColumns = mapColumnIndexes(columnsLoaded);

        filter(connectionInfo, rawData, mapColumns, maxRows, parameters, parameterTypes, columnsLoaded);

        return this.values;
    }

    // FIXME move to order by node.
    private Comparator<Object[]> processOrderBy() {
        if (!orderBy.isOrderBy()) {
            // Nothing to do here, there are no order by fields.
            return null;
        }

        final int[] mapColumns = new int[this.orderBy.count()];
        Arrays.fill(mapColumns, -1);
        for (int i = 0; i < this.orderBy.count(); i++) {
            final Column column = this.orderBy.getColumn(i);
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
            final OrderByComparator orderByComparator = new OrderByComparator(index,
                    this.orderBy.getType(i));
            if (comparator == null) {
                comparator = orderByComparator;
            } else {
                comparator = comparator.thenComparing(orderByComparator);
            }
        }

        return comparator;
    }

    private void processIndexes(final List<Column> columns) throws SQLException {
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

    private void processFunctionIndexes(final List<Column> columnsLoaded) throws SQLException {
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

    private Object[] mapRow(final ConnectionInfo connectionInfo, final Object[] tableRow, final int[] mapColumns,
                            final Object[] parameters, final ParadoxType[] parameterTypes,
                            final List<Column> columnsLoaded) throws SQLException {
        final Object[] finalRow = new Object[mapColumns.length];
        for (int i = 0; i < mapColumns.length; i++) {
            ensureNotCancelled();

            int index = mapColumns[i];
            if (index != -1) {
                // A field mapped value.
                finalRow[i] = tableRow[index];
            } else {
                final ParameterNode parameterNode = this.columns.get(i).getParameter();
                final FunctionNode functionNode = this.columns.get(i).getFunction();
                if (parameterNode != null) {
                    // A parameter value.
                    finalRow[i] = parameters[parameterNode.getParameterIndex()];
                } else if (functionNode == null) {
                    // A fixed value.
                    finalRow[i] = this.columns.get(i).getValue();
                } else if (!this.columns.get(i).isSecondPass()) {
                    // A function processed value.
                    finalRow[i] = functionNode.execute(connectionInfo, tableRow, parameters, parameterTypes,
                            columnsLoaded);
                    // The function may change the result type in execution based on parameters values.
                    this.columns.get(i).setType(functionNode.getType());
                }
            }
        }

        return finalRow;
    }

    private void filter(final ConnectionInfo connectionInfo, final List<Object[]> rowValues, final int[] mapColumns,
                        final int maxRows, final Object[] parameters, final ParadoxType[] parameterTypes,
                        final List<Column> columnsLoaded) {

        Stream<Object[]> stream = rowValues.stream()
                .filter(predicateWrapper(c -> ensureNotCancelled()));

        if (condition != null) {
            stream = stream.filter(predicateWrapper((Object[] tableRow) ->
                    condition.evaluate(connectionInfo, tableRow, parameters, parameterTypes, columnsLoaded)
            ));
        }

        stream = stream.map(functionWrapper((Object[] tableRow) ->
                mapRow(connectionInfo, tableRow, mapColumns, parameters, parameterTypes, columnsLoaded)
        ));

        stream = this.groupBy.processStream(stream, connectionInfo, parameters, parameterTypes, this.columns,
                predicateWrapper(c -> ensureNotCancelled()));

        if (orderBy.isOrderBy()) {
            // FIXME Move to order by.
            stream = stream.sorted(Objects.requireNonNull(processOrderBy()));
        }

        if (distinct) {
            stream = stream.filter(FunctionalUtils.distinctByKey());
        }

        if (maxRows != 0) {
            stream = stream.limit(maxRows);
        }

        this.values = stream.collect(Collectors.toList());
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
        joiner.cancel();
    }

    /**
     * Check if this execution was cancelled.
     *
     * @return <code>true</code> if this statement was not cancelled.
     * @throws SQLException if this execution was cancelled.
     */
    private boolean ensureNotCancelled() throws SQLException {
        if (cancelled) {
            throw new ParadoxException(ParadoxException.Error.OPERATION_CANCELLED);
        }

        return true;
    }

    /**
     * Gets the order by node.
     *
     * @return the order by node.
     */
    public OrderByNode getOrderBy() {
        return orderBy;
    }

    /**
     * Gets the tables.
     *
     * @return the tables.
     */
    public List<PlanTableNode> getTables() {
        return tables;
    }

    /**
     * Gets the group by node.
     *
     * @return the group by node.
     */
    public GroupByNode getGroupBy() {
        return groupBy;
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
