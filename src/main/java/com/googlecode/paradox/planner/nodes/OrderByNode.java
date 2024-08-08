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

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.exceptions.ParadoxException;
import com.googlecode.paradox.exceptions.ParadoxSyntaxErrorException;
import com.googlecode.paradox.exceptions.SyntaxError;
import com.googlecode.paradox.metadata.paradox.ParadoxTable;
import com.googlecode.paradox.parser.nodes.SelectNode;
import com.googlecode.paradox.planner.plan.SelectUtils;
import com.googlecode.paradox.planner.sorting.OrderByComparator;
import com.googlecode.paradox.planner.sorting.OrderType;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.rowset.ValuesConverter;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Stores one order by field information.
 *
 * @version 1.5
 * @since 1.6.0
 */
public class OrderByNode {

    /**
     * Order by columns.
     */
    private final List<Column> columns = new ArrayList<>();

    /**
     * Order by types.
     */
    private final List<OrderType> types = new ArrayList<>();

    /**
     * Creates a new instance.
     *
     * @param selectNode     the SELECT statement node.
     * @param tables         the tables.
     * @param selectColumns  the SELECT columns.
     * @param connectionInfo the connection information.
     * @param groupBy        if the SELECT statement has a group by.
     * @throws SQLException in case of failures.
     */
    public OrderByNode(final SelectNode selectNode, final List<PlanTableNode> tables,
                       final List<Column> selectColumns, final ConnectionInfo connectionInfo, final boolean groupBy)
            throws SQLException {

        for (int i = 0; i < selectNode.getOrder().size(); i++) {
            final FieldNode field = selectNode.getOrder().get(i);
            final OrderType type = selectNode.getOrderTypes().get(i);
            if (field instanceof ValueNode) {
                int index = ValuesConverter.getInteger(field.getName(), connectionInfo);
                if (index > selectColumns.size()) {
                    throw new ParadoxException(ParadoxException.Error.INVALID_COLUMN_INDEX, index);
                }

                processOrderColumn(selectColumns.get(index - 1), type, selectColumns);
            } else {
                SelectUtils.getParadoxFields(field, tables)
                        .forEach((Column c) -> processOrderColumn(c, type, selectColumns));
            }
        }

        if (groupBy && !this.columns.isEmpty()) {
            // Check for group by expression.
            final List<Column> columnsFound = selectColumns.stream()
                    .filter(c -> !c.isHidden()).collect(Collectors.toList());

            if (!columnsFound.containsAll(selectColumns)) {
                throw new ParadoxSyntaxErrorException(SyntaxError.ORDER_BY_NOT_IN_GROUP_BY);
            }
        }
    }

    /**
     * Add a order by column to this plan.
     *
     * @param column the column to add.
     * @param type   the order by field type.
     */
    private void processOrderColumn(final Column column, final OrderType type, final List<Column> selectColumns) {
        this.columns.add(column);
        this.types.add(type);

        if (!selectColumns.contains(column)) {
            // If not in SELECT statement, add as a hidden column in ResultSet.
            column.setHidden(true);
            selectColumns.add(column);
        }
    }

    /**
     * Process the stream with the order by.
     *
     * @param stream         the stream to process.
     * @param selectColumns  the SELECT columns.
     * @param connectionInfo the connection information.
     * @return the processes stream.
     */
    public Stream<Object[]> processStream(final Stream<Object[]> stream, final List<Column> selectColumns,
                                          final ConnectionInfo connectionInfo) {
        if (this.columns.isEmpty()) {
            // Nothing to do here, there are no order by fields.
            return stream;
        }

        final int[] mapColumns = new int[this.columns.size()];
        Arrays.fill(mapColumns, -1);
        for (int i = 0; i < this.columns.size(); i++) {
            final Column column = this.columns.get(i);
            mapColumns[i] = column.getIndex();
            for (int loop = 0; loop < selectColumns.size(); loop++) {
                if (selectColumns.get(loop).equals(column)) {
                    mapColumns[i] = loop;
                    break;
                }
            }
        }

        // Build the comparator list.
        Comparator<Object[]> comparator = null;
        for (int i = 0; i < mapColumns.length; i++) {
            final int index = mapColumns[i];
            final OrderByComparator orderByComparator = new OrderByComparator(index, this.types.get(i), connectionInfo);
            if (comparator == null) {
                comparator = orderByComparator;
            } else {
                comparator = comparator.thenComparing(orderByComparator);
            }
        }

        return stream.sorted(Objects.requireNonNull(comparator));
    }

    /**
     * Gets the order by column with the table specified.
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
     * Gets the order by columns.
     *
     * @return the order by columns.
     */
    public List<Column> getColumns() {
        return columns;
    }
}
