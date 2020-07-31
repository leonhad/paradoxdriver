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
package com.googlecode.paradox.utils;

import com.googlecode.paradox.exceptions.InternalException;
import com.googlecode.paradox.function.grouping.IGroupingContext;
import com.googlecode.paradox.rowset.ValuesComparator;

import java.io.Serializable;
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
     * @param indexes fields with grouping functions.
     * @return the predicate to grouping fields.
     */
    @SuppressWarnings({"unchecked", "raw", "java:S5612"})
    public static Predicate<Object[]> groupingByKeys(final int[] indexes) {
        final List<Object[]> seen = new ArrayList<>();
        return (Object[] value) -> {
            final Object[] current = seen.stream().filter(o -> equalsGrouping(o, value)).findAny().orElse(null);
            if (current == null) {
                // Just add, no grouping.
                seen.add(value);
                return true;
            } else {
                // Do grouping.
                Arrays.stream(indexes).forEach((int index) ->
                        ((IGroupingContext<?>) current[index]).process((IGroupingContext) value[index]));
            }

            return false;
        };
    }

    public static Function<Object[], Object[]> removeGrouping(final int[] indexes) {
        return (Object[] value) -> {
            Arrays.stream(indexes).forEach((int index) ->
                    value[index] = ((IGroupingContext<?>) value[index]).toValue());
            return value;
        };
    }

    /**
     * Compute an equals in array ignores grouping values.
     *
     * @param o1 the first array to compare.
     * @param o2 the second array to compare.
     * @return <code>true</code> if the two arrays are equals.
     */
    private static boolean equalsGrouping(Object[] o1, Object[] o2) {
        for (int i = 0; i < o1.length; i++) {
            if (!(o1[i] instanceof IGroupingContext)) {
                int ret = ValuesComparator.compare(o1[i], o2[i]);
                if (ret != 0) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Predicate to simulate the distinct on Object array.
     *
     * @return the predicate to simulate the distinct on Object array.
     */
    public static Predicate<Object[]> distinctByKey() {
        final Set<Object[]> seen = new TreeSet<>(new CompareArray());
        return seen::add;
    }

    /**
     * A distinct comparator to use with Object array.
     */
    private static class CompareArray implements Comparator<Object[]>, Serializable {

        @Override
        @SuppressWarnings("java:S1142")
        public int compare(Object[] o1, Object[] o2) {
            if (o1 == o2) {
                return 0;
            } else if (o1 == null) {
                return -1;
            } else if (o2 == null) {
                return 1;
            }

            for (int i = 0; i < o1.length; i++) {
                int ret = ValuesComparator.compare(o1[i], o2[i]);
                if (ret != 0) {
                    return ret;
                }
            }

            return 0;
        }
    }
}
