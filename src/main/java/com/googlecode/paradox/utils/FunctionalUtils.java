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
package com.googlecode.paradox.utils;

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.exceptions.InternalException;
import com.googlecode.paradox.function.aggregate.IGroupingContext;
import com.googlecode.paradox.planner.context.SelectContext;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.rowset.ValuesComparator;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Utility class to use with functional programming, mostly in Java Stream API.
 *
 * @version 1.1
 * @since 1.6.0
 */
public final class FunctionalUtils {

    /**
     * Utility class, not for use.
     */
    private FunctionalUtils() {
        // Not used.
    }

    /**
     * Functional interface to allow the use of exceptions .
     *
     * @param <T> the predicate type.
     * @param <E> the exception type.
     */
    @FunctionalInterface
    public interface PredicateWithExceptions<T, E extends SQLException> {
        boolean test(T t) throws E;
    }

    /**
     * Functional interface to allow the use of exceptions .
     *
     * @param <T> the predicate type.
     * @param <R> the function return type.
     * @param <E> the exception type.
     */
    @FunctionalInterface
    public interface FunctionWithExceptions<T, R, E extends SQLException> {
        R apply(T t) throws E;
    }

    /**
     * The predicate wrapper to allow the use of exceptions in stream API.
     *
     * @param fe  the predicate with exception.
     * @param <T> the predicate type.
     * @param <E> the exception type.
     * @return the predicate result.
     */
    @SuppressWarnings("java:S112")
    public static <T, E extends SQLException> Predicate<T> predicateWrapper(PredicateWithExceptions<T, E> fe) {
        return (T arg) -> {
            try {
                return fe.test(arg);
            } catch (final SQLException e) {
                throw new InternalException(e);
            }
        };
    }

    /**
     * The function wrapper to allow the use of exceptions in stream API.
     *
     * @param fe  the predicate with exception.
     * @param <T> the predicate type.
     * @param <R> the predicate return type.
     * @param <E> the exception type.
     * @return the predicate result.
     */
    @SuppressWarnings("java:S112")
    public static <T, R, E extends SQLException> Function<T, R> functionWrapper(FunctionWithExceptions<T, R, E> fe) {
        return (T arg) -> {
            try {
                return fe.apply(arg);
            } catch (final SQLException e) {
                throw new InternalException(e);
            }
        };
    }

    /**
     * Predicate to filter values for group by expressions.
     *
     * @param indexes        fields with grouping functions.
     * @param columns        the grouping columns.
     * @param connectionInfo the connection information.
     * @return the predicate to grouping fields.
     */
    @SuppressWarnings({"unchecked", "java:S5612", "rawtypes"})
    public static Predicate<Object[]> groupingByKeys(final int[] indexes, final int[] columns,
                                                     final ConnectionInfo connectionInfo) {
        final List<Object[]> seen = new ArrayList<>();
        return (Object[] value) -> {
            final Object[] current = seen.stream()
                    .filter(o -> equalsAggregate(o, value, columns, connectionInfo))
                    .findAny().orElse(null);

            if (current == null) {
                // Just add, no grouping.
                seen.add(value);
                return true;
            } else {
                // Do grouping.
                Arrays.stream(indexes).forEach((int index) ->
                        ((IGroupingContext<?>) current[index]).process((IGroupingContext) value[index],
                                connectionInfo));
            }

            return false;
        };
    }

    public static FunctionWithExceptions<Object[], Object[], SQLException> removeGrouping(
            final SelectContext context, final int[] indexes, final List<Column> columnsLoaded) {
        return (Object[] value) -> {
            for (final int index : indexes) {
                if (value[index] != null) {
                    value[index] = ((IGroupingContext<?>) value[index]).toValue();
                }
            }

            processSecondPass(context, columnsLoaded, value);

            return value;
        };
    }

    private static void processSecondPass(final SelectContext context, final List<Column> columnsLoaded,
                                          final Object[] value) throws SQLException {
        for (int i = 0; i < columnsLoaded.size(); i++) {
            if (columnsLoaded.get(i).isSecondPass()) {
                // A function processed value.
                value[i] = columnsLoaded.get(i).getFunction().execute(context, value, columnsLoaded);
            }
        }
    }

    /**
     * Compute an equals in array ignores grouping values.
     *
     * @param o1             the first array to compare.
     * @param o2             the second array to compare.
     * @param columns        the columns to compare.
     * @param connectionInfo the connection information.
     * @return <code>true</code> if the two arrays are equals.
     */
    private static boolean equalsAggregate(final Object[] o1, final Object[] o2, final int[] columns,
                                           final ConnectionInfo connectionInfo) {
        for (int i : columns) {
            // NULL are equals only in aggregation.
            if (o1[i] == o2[i]) {
                return true;
            }

            final boolean ret = ValuesComparator.equals(o1[i], o2[i], connectionInfo);
            if (!ret) {
                return false;
            }
        }

        return true;
    }

    /**
     * Predicate to simulate the distinct on Object array.
     *
     * @param columnsLoaded  the loaded columns. Used to ignore hidden columns.
     * @param connectionInfo the connection information.
     * @return the predicate to simulate the distinct on Object array.
     */
    public static Predicate<Object[]> distinctByKey(final List<Column> columnsLoaded,
                                                    final ConnectionInfo connectionInfo) {
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < columnsLoaded.size(); i++) {
            if (!columnsLoaded.get(i).isHidden()) {
                indexes.add(i);
            }
        }

        final Set<Object[]> seen = new TreeSet<>(
                new CompareArray(indexes.stream().mapToInt(Integer::intValue).toArray(), connectionInfo));
        return seen::add;
    }
}
