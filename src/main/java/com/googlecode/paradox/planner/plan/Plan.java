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

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.exceptions.ParadoxNotSupportedException;
import com.googlecode.paradox.results.ParadoxType;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

/**
 * Used to creates and execute SQL plans.
 *
 * @param <T> the return type.
 * @version 1.7
 * @since 1.1
 */
public interface Plan<T> {

    /**
     * Execute this plan.
     *
     * @param connectionInfo the connection information.
     * @param maxRows        the limit of rows that can be loaded. Zero means no
     *                       limit.
     * @param parameters     the statement parameters.
     * @param parameterTypes the parameter types.
     * @return the processed value. In SELECT, it returns the values, in others, the columns affected.
     * @throws SQLException in case of failures.
     */
    T execute(final ConnectionInfo connectionInfo, final int maxRows, final Object[] parameters,
              final ParadoxType[] parameterTypes) throws SQLException;

    /**
     * Optimize the statement. This step is optional for most planing.
     */
    default void optimize() {
        // Do nothing.
    }

    /**
     * Cancel the statement execution.
     *
     * @throws SQLFeatureNotSupportedException when the statement not support the
     *                                         cancel operation.
     */
    default void cancel() throws SQLFeatureNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.CANCEL_NOT_SUPPORTED);
    }
}
