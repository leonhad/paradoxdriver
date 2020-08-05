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

package com.googlecode.paradox.planner.context;

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.exceptions.ParadoxException;
import com.googlecode.paradox.exceptions.ParadoxNotSupportedException;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.utils.FunctionalUtils;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Locale;
import java.util.function.Predicate;

public class Context {

    /**
     * If this statement was cancelled.
     */
    protected boolean cancelled;

    private final ConnectionInfo connectionInfo;

    private final Object[] parameters;

    private final ParadoxType[] parameterTypes;

    protected Context(final ConnectionInfo connectionInfo, final Object[] parameters,
                      final ParadoxType[] parameterTypes) {
        this.connectionInfo = connectionInfo;
        this.parameters = parameters;
        this.parameterTypes = parameterTypes;
    }

    public ConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public ParadoxType[] getParameterTypes() {
        return parameterTypes;
    }

    public Locale getLocale() {
        return connectionInfo.getLocale();
    }

    /**
     * Cancel the statement execution.
     *
     * @throws SQLFeatureNotSupportedException when the statement not support the
     *                                         cancel operation.
     */
    public void cancel() throws SQLFeatureNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.CANCEL_NOT_SUPPORTED);
    }

    public <T> Predicate<T> getCancelPredicate() {
        return FunctionalUtils.predicateWrapper((T obj) -> checkCancelState());
    }

    /**
     * Check if this execution was cancelled.
     *
     * @return <code>true</code> if this statement was not cancelled.
     * @throws SQLException if this execution was cancelled.
     */
    public boolean checkCancelState() throws SQLException {
        if (cancelled) {
            throw new ParadoxException(ParadoxException.Error.OPERATION_CANCELLED);
        }

        return true;
    }
}
