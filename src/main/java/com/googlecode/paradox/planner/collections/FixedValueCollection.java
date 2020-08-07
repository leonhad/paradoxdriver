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

package com.googlecode.paradox.planner.collections;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A collection with fixed values.
 *
 * @param <T> the collection type.
 * @version 1.0
 * @since 1.6.0
 */
public class FixedValueCollection<T> implements Collection<T> {

    /**
     * Collection size.
     */
    private int size;
    /**
     * The fixed value.
     */
    private final T value;

    /**
     * Creates a new instance.
     *
     * @param size the size.
     */
    public FixedValueCollection(final int size, final T value) {
        this.size = size;
        this.value = value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return new EmptyIterator();
    }

    /**
     * Iterator that uses same value for all rows.
     *
     * @version 1.0
     * @since 1.6.0
     */
    private class EmptyIterator implements Iterator<T> {

        /**
         * Current item in iterator.
         */
        private int current = -1;

        @Override
        public boolean hasNext() {
            return current + 1 < size;
        }

        @Override
        public T next() {
            if (current + 1 >= size) {
                throw new NoSuchElementException();
            }

            current++;
            return value;
        }
    }

    /**
     * Do not use this.
     *
     * @return the values in array format.
     */
    @Override
    public Object[] toArray() {
        final Object[] ret = new Object[size];
        Arrays.fill(ret, value);
        return ret;
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        T1[] ret = a;
        if (a.length != size) {
            ret = Arrays.copyOf(a, size);
        }

        Arrays.fill(ret, value);
        return ret;
    }

    @Override
    public boolean add(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        size = 0;
    }
}
