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

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.planner.context.Context;
import com.googlecode.paradox.results.ParadoxType;

import java.sql.SQLException;

/**
 * Used to creates and execute SQL plans.
 *
 * @param <T> the return type.
 * @param <C> the plan context type.
 * @since 1.1
 */
public interface Plan<T, C extends Context> {

    /**
     * Execute the plan.
     *
     * @param context the context to use.
     * @return the generated values.
     * @throws SQLException in case of failures.
     */
    T execute(final C context) throws SQLException;

    /**
     * Optimize the statement. This step is optional for most planing.
     */
    default void optimize() {
        // Do nothing.
    }

    /**
     * Creates a new execution context.
     *
     * @param connectionInfo the connection information.
     * @param parameters     the parameter list.
     * @param parameterTypes the parameter type list.
     * @return a new instance for execution context.
     */
    C createContext(final ConnectionInfo connectionInfo, final Object[] parameters, final ParadoxType[] parameterTypes);

    /**
     * Gets the parameters count.
     *
     * @return the parameters count.
     */
    int getParameterCount();
}
