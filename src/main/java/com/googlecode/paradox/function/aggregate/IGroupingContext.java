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
package com.googlecode.paradox.function.aggregate;

/**
 * Stores a grouping value context for store temporary data.
 *
 * @param <T> the grouping return type.
 * @version 1.2
 * @since 1.6.0
 */
public interface IGroupingContext<T> {

    /**
     * Process a single grouping.
     *
     * @param context the context to use in grouping process.
     */
    void process(final IGroupingContext<T> context);

    /**
     * Gets the result value.
     *
     * @return the result value.
     */
    T toValue();
}
