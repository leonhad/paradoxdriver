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
package com.googlecode.paradox.planner.plan;

import com.googlecode.paradox.planner.collections.FixedValueCollection;
import com.googlecode.paradox.planner.context.Context;
import com.googlecode.paradox.planner.nodes.PlanTableNode;
import com.googlecode.paradox.results.Column;

import java.sql.SQLException;
import java.util.*;

/**
 * Table joiner.
 *
 * @version 1.2
 * @since 1.6.0
 */
final class TableJoiner {

    /**
     * Creates a new instance.
     */
    private TableJoiner() {
        // Not used.
    }

    public static Collection<Object[]> processJoinByType(final Context context, final List<Column> columnsLoaded,
                                                   final Collection<Object[]> rawData, final PlanTableNode table,
                                                   final Collection<Object[]> tableData) throws SQLException {
        Collection<Object[]> localValues;
        switch (table.getJoinType()) {
            case RIGHT:
                localValues = processRightJoin(context, columnsLoaded, rawData, table, tableData);
                break;
            case LEFT:
                localValues = processLeftJoin(context, columnsLoaded, rawData, table, tableData);
                break;
            case FULL:
                localValues = processFullJoin(context, columnsLoaded, rawData, table, tableData);
                break;
            default:
                // CROSS and INNER joins.
                localValues = processInnerJoin(context, columnsLoaded, rawData, table, tableData);
                break;
        }

        return localValues;
    }

    private static List<Object[]> processLeftJoin(final Context context, final List<Column> columnsLoaded,
                                                  final Collection<Object[]> rawData, final PlanTableNode table,
                                                  final Collection<Object[]> tableData) throws SQLException {
        final Object[] column = new Object[columnsLoaded.size()];
        final List<Object[]> localValues = new ArrayList<>(0x7F);

        for (final Object[] cols : rawData) {
            System.arraycopy(cols, 0, column, 0, cols.length);

            boolean changed = false;
            for (final Object[] newCols : tableData) {
                context.checkCancelState();

                System.arraycopy(newCols, 0, column, cols.length, newCols.length);

                if (table.getConditionalJoin() != null && !table.getConditionalJoin()
                        .evaluate(context, column, columnsLoaded)) {
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

    private static List<Object[]> processRightJoin(final Context context, final List<Column> columnsLoaded,
                                                   final Collection<Object[]> rawData, final PlanTableNode table,
                                                   final Collection<Object[]> tableData) throws SQLException {

        final Object[] column = new Object[columnsLoaded.size()];
        final List<Object[]> localValues = new ArrayList<>(0x7F);

        for (final Object[] newCols : tableData) {
            System.arraycopy(newCols, 0, column, column.length - newCols.length, newCols.length);

            boolean changed = false;
            for (final Object[] cols : rawData) {
                context.checkCancelState();

                System.arraycopy(cols, 0, column, 0, cols.length);

                if (table.getConditionalJoin() != null && !table.getConditionalJoin()
                        .evaluate(context, column, columnsLoaded)) {
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

    private static List<Object[]> processFullJoin(final Context context, final List<Column> columnsLoaded,
                                                  final Collection<Object[]> rawData, final PlanTableNode table,
                                                  final Collection<Object[]> tableData) throws SQLException {

        final Object[] column = new Object[columnsLoaded.size()];
        final List<Object[]> localValues = new ArrayList<>(0x7F);

        Set<Integer> inLeft = new HashSet<>();
        for (final Object[] cols : rawData) {
            System.arraycopy(cols, 0, column, 0, cols.length);

            boolean changed = false;
            int i = -1;
            for (final Object[] newCols : tableData) {
                context.checkCancelState();
                i++;

                System.arraycopy(newCols, 0, column, cols.length, newCols.length);

                if (table.getConditionalJoin() != null && !table.getConditionalJoin()
                        .evaluate(context, column, columnsLoaded)) {
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
        int i = 0;
        for (final Object[] newCols : tableData) {
            context.checkCancelState();

            if (!inLeft.contains(i)) {
                System.arraycopy(newCols, 0, column, column.length - newCols.length, newCols.length);
                localValues.add(column.clone());
            }

            i++;
        }

        return localValues;
    }

    private static Collection<Object[]> processInnerJoin(final Context context, final List<Column> columnsLoaded,
                                                         final Collection<Object[]> rawData, final PlanTableNode table,
                                                         final Collection<Object[]> tableData) throws SQLException {
        int initialCapacity;

        // Is this a cartesian merge?
        if (table.getConditionalJoin() != null) {
            // Start with the final size.
            initialCapacity = rawData.size() * tableData.size();
        } else {
            // If not, start with 100% of the total size.
            initialCapacity = 0x7F;
        }

        // Joining two tables without columns loaded (grouping function only?).
        if (columnsLoaded.isEmpty()) {
            return new FixedValueCollection<>(rawData.size() * tableData.size(), new Object[0]);
        }

        final ArrayList<Object[]> localValues = new ArrayList<>(initialCapacity);

        final Object[] column = new Object[columnsLoaded.size()];
        for (final Object[] cols : rawData) {
            System.arraycopy(cols, 0, column, 0, cols.length);

            for (final Object[] newCols : tableData) {
                context.checkCancelState();
                System.arraycopy(newCols, 0, column, cols.length, newCols.length);

                if (table.getConditionalJoin() != null
                        && !table.getConditionalJoin().evaluate(context, column, columnsLoaded)) {
                    continue;
                }

                localValues.add(column.clone());
            }
        }

        return localValues;
    }
}
