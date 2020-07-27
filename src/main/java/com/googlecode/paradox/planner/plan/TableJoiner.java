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
import com.googlecode.paradox.exceptions.ParadoxException;
import com.googlecode.paradox.planner.nodes.PlanTableNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;

import java.sql.SQLException;
import java.util.*;

/**
 * Table joiner.
 *
 * @version 1.0
 * @since 1.6.0
 */
class TableJoiner {
    /**
     * If this process was cancelled.
     */
    private boolean cancelled;

    /**
     * Creates a new instance.
     */
    TableJoiner() {
        cancelled = false;
    }

    public List<Object[]> processJoinByType(final ParadoxConnection connection, final List<Column> columnsLoaded,
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

    private List<Object[]> processLeftJoin(final ParadoxConnection connection, final List<Column> columnsLoaded,
                                           final List<Object[]> rawData, final PlanTableNode table,
                                           final List<Object[]> tableData, final Object[] parameters,
                                           final ParadoxType[] parameterTypes) throws SQLException {
        final Object[] column = new Object[columnsLoaded.size()];
        final List<Object[]> localValues = new ArrayList<>(0x7F);

        for (final Object[] cols : rawData) {
            System.arraycopy(cols, 0, column, 0, cols.length);

            boolean changed = false;
            for (final Object[] newCols : tableData) {
                ensureNotCancelled();

                System.arraycopy(newCols, 0, column, cols.length, newCols.length);

                if (table.getConditionalJoin() != null && !table.getConditionalJoin()
                        .evaluate(connection, column, parameters, parameterTypes, columnsLoaded)) {
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
        final List<Object[]> localValues = new ArrayList<>(0x7F);

        for (final Object[] newCols : tableData) {
            System.arraycopy(newCols, 0, column, column.length - newCols.length, newCols.length);

            boolean changed = false;
            for (final Object[] cols : rawData) {
                ensureNotCancelled();

                System.arraycopy(cols, 0, column, 0, cols.length);

                if (table.getConditionalJoin() != null && !table.getConditionalJoin()
                        .evaluate(connection, column, parameters, parameterTypes, columnsLoaded)) {
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
        final List<Object[]> localValues = new ArrayList<>(0x7F);

        Set<Integer> inLeft = new HashSet<>();
        for (final Object[] cols : rawData) {
            System.arraycopy(cols, 0, column, 0, cols.length);

            boolean changed = false;
            for (int i = 0; i < tableData.size(); i++) {
                ensureNotCancelled();

                final Object[] newCols = tableData.get(i);
                System.arraycopy(newCols, 0, column, cols.length, newCols.length);

                if (table.getConditionalJoin() != null && !table.getConditionalJoin()
                        .evaluate(connection, column, parameters, parameterTypes, columnsLoaded)) {
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
            ensureNotCancelled();

            if (!inLeft.contains(i)) {
                final Object[] newCols = tableData.get(i);
                System.arraycopy(newCols, 0, column, column.length - newCols.length, newCols.length);
                localValues.add(column.clone());
            }
        }

        return localValues;
    }

    private List<Object[]> processInnerJoin(final ParadoxConnection connection, final List<Column> columnsLoaded,
                                            final List<Object[]> rawData, final PlanTableNode table,
                                            final List<Object[]> tableData, final Object[] parameters,
                                            final ParadoxType[] parameterTypes) throws SQLException {
        final Object[] column = new Object[columnsLoaded.size()];

        int initialCapacity;

        // Is this a cartesian merge?
        if (table.getConditionalJoin() != null) {
            // Start with the final size.
            initialCapacity = rawData.size() * tableData.size();
        } else {
            // If not, start with 100% of the total size.
            initialCapacity = 0x7F;
        }

        final ArrayList<Object[]> localValues = new ArrayList<>(initialCapacity);

        for (final Object[] cols : rawData) {
            System.arraycopy(cols, 0, column, 0, cols.length);

            for (final Object[] newCols : tableData) {
                ensureNotCancelled();
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
     * Check if this execution was cancelled.
     *
     * @throws SQLException if this execution was cancelled.
     */
    private void ensureNotCancelled() throws SQLException {
        if (cancelled) {
            throw new ParadoxException(ParadoxException.Error.OPERATION_CANCELLED);
        }
    }

    /**
     * Cancel this statement execution.
     */
    public void cancel() {
        cancelled = true;
    }

    /**
     * Resets the cancel state.
     */
    public void reset() {
        cancelled = false;
    }
}
