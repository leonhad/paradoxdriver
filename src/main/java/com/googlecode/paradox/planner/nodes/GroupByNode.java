/*
 * Copyright (c) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.planner.nodes;

import com.googlecode.paradox.exceptions.ParadoxNotSupportedException;
import com.googlecode.paradox.exceptions.ParadoxSyntaxErrorException;
import com.googlecode.paradox.exceptions.SyntaxError;
import com.googlecode.paradox.metadata.paradox.ParadoxTable;
import com.googlecode.paradox.parser.nodes.SelectNode;
import com.googlecode.paradox.planner.context.SelectContext;
import com.googlecode.paradox.planner.plan.SelectUtils;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.utils.FunctionalUtils;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.googlecode.paradox.utils.FunctionalUtils.functionWrapper;

/**
 * Group by node.
 *
 * @version 1.1
 * @since 1.6.0
 */
public class GroupByNode {

    /**
     * Grouping columns.
     */
    private final int[] functionColumns;

    /**
     * Grouping columns.
     */
    private final int[] groupColumns;

    /**
     * Group by fields.
     */
    private final List<Column> columns = new ArrayList<>();

    /**
     * If this plan has a aggregate.
     */
    private final boolean groupBy;

    /**
     * Creates a new instance.
     *
     * @param statement the SELECT statement node.
     * @param tables    the current table nodes.
     * @param columns   the current columns.
     * @throws SQLException in case of failures.
     */
    @SuppressWarnings("java:S3776")
    public GroupByNode(final SelectNode statement, final List<PlanTableNode> tables, final List<Column> columns)
            throws SQLException {
        // Create columns to use in SELECT statement.
        for (final FieldNode field : statement.getGroups()) {
            if (field instanceof ParameterNode) {
                throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED,
                        field.getPosition());
            } else if (field instanceof ValueNode) {
                addGroupColumn(new Column((ValueNode) field), columns);
            } else {
                addGroupColumn(field, tables, columns);
            }
        }

        groupBy = columns.stream()
                .map(Column::getFunction)
                .filter(Objects::nonNull)
                .anyMatch(FunctionNode::isGrouping) || !this.columns.isEmpty();

        if (groupBy) {
            // Validate statically the group by clause.
            final Set<Column> groupColumnsToCheck = new HashSet<>(this.columns);
            final List<Column> fields = columns.stream()
                    .filter(c -> c.getFunction() == null || !c.getFunction().isGrouping())
                    .filter(c -> !groupColumnsToCheck.remove(c))
                    .collect(Collectors.toList());

            if (!fields.isEmpty()) {
                throw new ParadoxSyntaxErrorException(SyntaxError.NOT_GROUP_BY);
            }
        }

        final List<Column> columnList = columns.stream()
                .map(SelectUtils::getGroupingFunctions)
                .flatMap(Collection::stream)
                .map(Column::new)
                .collect(Collectors.toList());

        for (final Column column : columnList) {
            final int index = columns.indexOf(column);
            if (index < 0) {
                column.setHidden(true);
                column.getFunction().setIndex(columns.size());
                columns.add(column);
            } else {
                column.getFunction().setIndex(index);
            }
        }

        // Columns with a grouping function.
        functionColumns = columns.stream()
                .map(Column::getFunction)
                .filter(Objects::nonNull)
                .filter(FunctionNode::isGrouping)
                .filter(f -> !f.isSecondPass())
                .mapToInt(FunctionNode::getIndex)
                .toArray();

        // Key columns to do the grouping.
        final HashSet<Column> columnsToCheck = new HashSet<>(this.columns);
        groupColumns = columns.stream()
                .filter(c -> c.getFunction() == null || !c.getFunction().isGrouping())
                .filter(c -> !c.isHidden() || columnsToCheck.remove(c))
                .mapToInt(Column::getIndex)
                .toArray();
    }

    /**
     * Add a group by column to this plan.
     *
     * @param node the node to convert to a column.
     * @throws SQLException in case of failures.
     */
    private void addGroupColumn(final FieldNode node, final List<PlanTableNode> tables, final List<Column> columns)
            throws SQLException {
        SelectUtils.getParadoxFields(node, tables).forEach(field -> addGroupColumn(field, columns));
    }

    /**
     * Add a group by column to this plan.
     *
     * @param column the column to add.
     */
    private void addGroupColumn(final Column column, final List<Column> columns) {
        this.columns.add(column);

        if (!columns.contains(column)) {
            // If not in SELECT statement, add as a hidden column in ResultSet.
            column.setHidden(true);
            columns.add(column);
        }
    }

    /**
     * Process the group by stream.
     *
     * @param context the execution context.
     * @param stream  the load stream.
     * @param columns the current columns.
     * @return the stream with group by filter.
     */
    public Stream<Object[]> processStream(final SelectContext context, final Stream<Object[]> stream,
                                          final List<Column> columns) {
        if (!groupBy) {
            return stream;
        }

        // Is not possible to group in parallel.
        return stream.filter(FunctionalUtils.groupingByKeys(functionColumns, groupColumns, context.getConnectionInfo()))
                .collect(Collectors.toList()).stream()
                .filter(context.getCancelPredicate())
                .map(functionWrapper(FunctionalUtils.removeGrouping(context, functionColumns, columns)));
    }

    /**
     * Gets the group by column with the table specified.
     *
     * @param table the table to filter.
     * @return the column table list used in order by.
     */
    public Set<Column> getColumns(final ParadoxTable table) {
        return this.columns.stream()
                .filter(c -> c.isThis(table))
                .collect(Collectors.toSet());
    }

    /**
     * Gets if this statement has a aggregation step.
     *
     * @return <code>true</code> if this statement has a aggregation step.
     */
    public boolean isGroupBy() {
        return groupBy;
    }

    /**
     * Gets the group by columns.
     *
     * @return the group by columns.
     */
    public List<Column> getColumns() {
        return columns;
    }
}
