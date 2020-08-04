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

import com.googlecode.paradox.exceptions.ParadoxSyntaxErrorException;
import com.googlecode.paradox.exceptions.SyntaxError;
import com.googlecode.paradox.metadata.ParadoxTable;
import com.googlecode.paradox.planner.sorting.OrderType;
import com.googlecode.paradox.results.Column;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Stores one order by field information.
 *
 * @version 1.1
 * @since 1.6.0
 */
public class OrderByNode {

    /**
     * Order by columns.
     */
    private final List<Column> column = new ArrayList<>();

    /**
     * Order by types.
     */
    private final List<OrderType> type = new ArrayList<>();

    /**
     * Creates a new instance.
     */
    public OrderByNode() {
        super();
    }

    /**
     * If the planner has a order by.
     *
     * @return <code>true</code> if the planner has a order by.
     */
    public boolean isOrderBy() {
        return !this.column.isEmpty();
    }

    /**
     * Add a order by column.
     *
     * @param column the column class.
     * @param type   the column type.
     */
    public void add(final Column column, final OrderType type) {
        this.column.add(column);
        this.type.add(type);
    }

    /**
     * Check if this order by is valid.
     *
     * @param groupBy <code>true</code> if the group by is enabled.
     * @param columns the SELECT statement columns (hidden or not).
     * @throws ParadoxSyntaxErrorException if the order by is invalid.
     */
    public void checkColumns(final boolean groupBy, final List<Column> columns) throws ParadoxSyntaxErrorException {
        if (groupBy && isOrderBy()) {
            // Check for group by expression.
            final List<Column> columnsFound = columns.stream()
                    .filter(c -> !c.isHidden()).collect(Collectors.toList());

            if (!columnsFound.containsAll(columns)) {
                throw new ParadoxSyntaxErrorException(SyntaxError.ORDER_BY_NOT_IN_GROUP_BY);
            }
        }
    }

    /**
     * Gets the order by column with the table specified.
     *
     * @param table the table to filter.
     * @return the column table list used in order by.
     */
    public Set<Column> getColumns(final ParadoxTable table) {
        return this.column.stream()
                .filter(c -> c.isThis(table))
                .collect(Collectors.toSet());
    }

    /**
     * Gets the column by index.
     *
     * @param index the column index.
     * @return the column by index.
     */
    public Column getColumn(final int index) {
        return column.get(index);
    }

    /**
     * Gets the column type by index.
     *
     * @param index the column index.
     * @return the column type by index.
     */
    public OrderType getType(int index) {
        return type.get(index);
    }

    /**
     * Gets the column count.
     *
     * @return the column count.
     */
    public int count() {
        return column.size();
    }
}
