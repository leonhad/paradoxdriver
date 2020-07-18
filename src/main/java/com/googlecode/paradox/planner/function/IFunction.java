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
package com.googlecode.paradox.planner.function;

import com.googlecode.paradox.ParadoxConnection;

/**
 * SQL function interface.
 *
 * @version 1.0
 * @since 1.6.0
 */
public interface IFunction {

    /**
     * The returned value SQL type.
     *
     * @return the returned value SQL type.
     */
    int sqlType();

    /**
     * The function parameters count.
     *
     * @return the function parameters count.
     */
    int parameterCount();

    /**
     * Gets if this function has variable parameters.
     *
     * @return <code>true</code> if this function has variable parameters.
     */
    default boolean isVariableParameters() {
        return false;
    }

    /**
     * Gets if this function is a grouping function.
     *
     * @return <code>true</code> if this function is a grouping function.
     */
    default boolean isGrouping() {
        return false;
    }

    /**
     * Execute the function.
     *
     * @param connection the Paradox connection.
     * @param values     the row values.
     * @return The function processed value.
     */
    Object execute(ParadoxConnection connection, Object[] values);
}
