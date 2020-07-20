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
import com.googlecode.paradox.exceptions.ParadoxNotSupportedException;
import com.googlecode.paradox.results.ParadoxType;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

/**
 * Used to creates and execute SQL plans.
 *
 * @version 1.5
 * @since 1.1
 */
public interface Plan {

    /**
     * Execute this plan.
     *
     * @param connection     the Paradox connection.
     * @param maxRows        the limit of rows that can be loaded. Zero means no
     *                       limit.
     * @param parameters     the statement parameters.
     * @param parameterTypes the parameter types.
     * @throws SQLException in case of failures.
     */
    void execute(final ParadoxConnection connection, final int maxRows, final Object[] parameters,
                 final ParadoxType[] parameterTypes) throws SQLException;

    /**
     * Optimize the statement.
     */
    void compile();

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
