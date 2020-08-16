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
import com.googlecode.paradox.exceptions.*;
import com.googlecode.paradox.metadata.Field;
import com.googlecode.paradox.metadata.Table;
import com.googlecode.paradox.parser.nodes.*;
import com.googlecode.paradox.planner.FieldValueUtils;
import com.googlecode.paradox.planner.context.SelectContext;
import com.googlecode.paradox.planner.nodes.*;
import com.googlecode.paradox.planner.nodes.join.ANDNode;
import com.googlecode.paradox.planner.nodes.join.ORNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;
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
 * @version 1.18
 * @since 1.1
 */
@SuppressWarnings({"java:S1448", "java:S1200"})
public final class SelectPlan implements Plan<List<Object[]>, SelectContext> {

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
     * The conditions to filter values
     */
    private AbstractConditionalNode condition;

    /**
     * The statement parameters count.
     */
    private final int parameterCount;

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
        this.parameterCount = statement.getParameterCount();

        // Load the table information.
        this.tables = statement.getTables().stream()
                .map(functionWrapper(table -> new PlanTableNode(connectionInfo, table)))
                .collect(Collectors.toList());

        this.columns = parseColumns(statement);
        this.groupBy = new GroupByNode(statement, this.tables, this.columns);
        this.orderBy = new OrderByNode(statement, this.tables, columns, connectionInfo, this.groupBy.isGroupBy());

        if (this.columns.isEmpty()) {
            throw new ParadoxSyntaxErrorException(SyntaxError.EMPTY_COLUMN_LIST);
        }

        // Check for columns to load.
        for (final PlanTableNode table : this.tables) {
            // Columns in SELECT clause.
            table.addColumns(this.columns);

            // Columns in SELECT functions.
            table.addColumns(this.columnsFromFunctions);

            // Columns in GROUP BY clause.
            table.addColumns(this.groupBy.getColumns());

            // Columns in ORDER BY clause.
            table.addColumns(this.orderBy.getColumns());

            // Fields from WHERE clause.
            table.addColumns(SelectUtils.getConditionalFields(table, this.condition));

            // Get fields from other tables join.
            for (final PlanTableNode tableToField : this.tables) {
                table.addColumns(SelectUtils.getConditionalFields(table, tableToField.getConditionalJoin()));
            }
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

    @Override
    public SelectContext createContext(final ConnectionInfo connectionInfo, final Object[] parameters,
                                       final ParadoxType[] parameterTypes) {
        return new SelectContext(connectionInfo, parameters, parameterTypes);
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
     * Parses the asterisk fields in SELECT.
     *
     * @param field the asterisk field.
     * @return the columns to add.
     * @throws SQLException in case of parse errors.
     */
    private List<Column> parseAsterisk(final AsteriskNode field) throws SQLException {
        if (field.getTableName() != null) {
            // Add all columns from one table.
            final List<Table> tablesFound = this.tables.stream()
                    .filter(t -> t.isThis(field.getTableName()))
                    .map(PlanTableNode::getTable).collect(Collectors.toList());

            if (tablesFound.isEmpty()) {
                throw new ParadoxDataException(DataError.TABLE_NOT_FOUND, field.getPosition(),
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
                    .map(Table::getFields)
                    .flatMap(Arrays::stream)
                    .map(Column::new)
                    .collect(Collectors.toList());
        }
    }

    private int getTableIndex(final Table table) {
        int index = -1;
        for (int i = 0; i < this.tables.size(); i++) {
            if (this.tables.get(i).getTable().equals(table)) {
                index = i;
                break;
            }
        }

        return index;
    }

    private PlanTableNode getPlanTable(final Table table) {
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
            final List<Field> conditionalFields = new ArrayList<>();

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

                final Table paradoxTable = conditionalFields.get(0).getTable();
                final PlanTableNode planTableNode = getPlanTable(paradoxTable);

                if (planTableNode != null && (planTableNode.getJoinType() == JoinType.CROSS
                        || planTableNode.getJoinType() == JoinType.INNER)) {
                    // Do not change OUTER joins.
                    SelectUtils.addAndClause(planTableNode, node);
                    ret = true;
                }
            } else if (conditionalFields.size() > 1) {
                // FIELD = FIELD
                final Table paradoxTable1 = conditionalFields.get(0).getTable();
                final Table paradoxTable2 = conditionalFields.get(1).getTable();

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
    @SuppressWarnings({"java:S3776", "java:S1541", "java:S1142"})
    public List<Object[]> execute(final SelectContext context) throws SQLException {

        // Can't do anything without fields defined.
        if (this.columns.isEmpty()) {
            return Collections.emptyList();
        }

        final List<Column> columnsLoaded = new ArrayList<>();
        Collection<Object[]> rawData = Collections.emptyList();

        for (int tableIndex = 0; tableIndex < this.tables.size(); tableIndex++) {
            PlanTableNode table = this.tables.get(tableIndex);
            context.checkCancelState();

            final Collection<Object[]> tableData = table.load();
            columnsLoaded.addAll(table.getColumns());

            if (table.getConditionalJoin() != null) {
                table.getConditionalJoin().setFieldIndexes(columnsLoaded, this.tables);
            }

            // First table?
            if (tableIndex == 0) {
                if (table.getConditionalJoin() != null) {
                    rawData = tableData.stream()
                            .filter(context.getCancelPredicate())
                            .filter(predicateWrapper(tableRow ->
                                    table.getConditionalJoin().evaluate(context, tableRow, columnsLoaded)))
                            .collect(Collectors.toList());
                } else {
                    // No conditions to process. Just use it.
                    rawData = tableData;
                }
            } else {
                rawData = TableJoiner.processJoinByType(context, columnsLoaded, rawData, table,
                        tableData);
            }
        }

        // There is a table in FROM clause?
        if (tables.isEmpty()) {
            final Object[] row = new Object[this.columns.size()];
            for (int i = 0; i < row.length; i++) {
                // A list of fixed value.
                row[i] = this.columns.get(i).getValue();
            }

            rawData = Collections.singleton(row);
        } else if (rawData.isEmpty()) {
            // No result to process, just return.
            return Collections.emptyList();
        }

        if (canDoFastCount()) {
            final Object[] row = new Object[1];
            row[0] = rawData.size();
            return Collections.singletonList(row);
        }

        processIndexes(columnsLoaded);
        processFunctionIndexes(columnsLoaded);

        // Process parameter types.
        columns.stream()
                .filter(column -> column.getParameter() != null)
                .forEach(column -> column.setType(
                        context.getParameterTypes()[column.getParameter().getParameterIndex()]));

        // Find column indexes.
        final int[] mapColumns = mapColumnIndexes(columnsLoaded);

        return filter(context, rawData, mapColumns, columnsLoaded);
    }

    /**
     * Check if the SELECT statement is only counting rows.
     *
     * @return <code>true</code> if the SELECT statement is only counting rows.
     */
    @SuppressWarnings("java:S1067")
    private boolean canDoFastCount() {
        // If only count function in columns and conditions is processed by tables (condition is null).
        // Group by is not allowed too (no columns set).
        if (!this.groupBy.getColumns().isEmpty() || this.columns.size() != 1) {
            return false;
        }

        final Column column = this.columns.get(0);
        if (column.getFunction() == null || !column.getFunction().isCount()) {
            return false;
        }

        final Set<FieldNode> fields = column.getFunction().getClauseFields();
        final FieldNode field = fields.iterator().next();
        return (field instanceof AsteriskNode || field instanceof ValueNode) && !"null".equals(field.getName());
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

    private Object[] mapRow(final SelectContext context, final Object[] tableRow, final int[] mapColumns,
                            final List<Column> columnsLoaded) throws SQLException {

        final Object[] finalRow = new Object[mapColumns.length];
        for (int i = 0; i < mapColumns.length; i++) {
            int index = mapColumns[i];
            if (index != -1) {
                // A field mapped value.
                finalRow[i] = tableRow[index];
            } else {
                final ParameterNode parameterNode = this.columns.get(i).getParameter();
                final FunctionNode functionNode = this.columns.get(i).getFunction();
                if (parameterNode != null) {
                    // A parameter value.
                    finalRow[i] = context.getParameters()[parameterNode.getParameterIndex()];
                } else if (functionNode == null) {
                    // A fixed value.
                    finalRow[i] = this.columns.get(i).getValue();
                } else if (!this.columns.get(i).isSecondPass()) {
                    // A function processed value.
                    finalRow[i] = functionNode.execute(context, tableRow, columnsLoaded);
                    // The function may change the result type in execution based on parameters values.
                    this.columns.get(i).setType(functionNode.getType());
                }
            }
        }

        return finalRow;
    }

    private List<Object[]> filter(final SelectContext context, final Collection<Object[]> rowValues,
                                  final int[] mapColumns, final List<Column> columnsLoaded) {

        Stream<Object[]> stream = rowValues.stream()
                .filter(context.getCancelPredicate());

        if (condition != null) {
            stream = stream.filter(predicateWrapper((Object[] tableRow) ->
                    condition.evaluate(context, tableRow, columnsLoaded)
            ));
        }

        stream = stream.map(functionWrapper((Object[] tableRow) ->
                mapRow(context, tableRow, mapColumns, columnsLoaded)
        ));

        // Group by.
        stream = this.groupBy.processStream(context, stream, this.columns);

        // Order by.
        stream = this.orderBy.processStream(stream, this.columns, context.getConnectionInfo());

        // Distinct
        if (distinct) {
            stream = stream.filter(FunctionalUtils.distinctByKey(this.columns, context.getConnectionInfo()));
        }

        if (context.getMaxRows() != 0) {
            stream = stream.limit(context.getMaxRows());
        }

        return stream.collect(Collectors.toList());
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
    public int getParameterCount() {
        return this.parameterCount;
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
