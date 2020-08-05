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
import com.googlecode.paradox.results.ParadoxType;

/**
 * The SELECT execution context.
 *
 * @version 1.0
 * @since 1.6.0
 */
public class SelectContext extends Context {

    /**
     * Max rows in SELECT statement.
     */
    private int maxRows;

    /**
     * Creates a new SELECT context.
     *
     * @param connectionInfo the connection information.
     * @param parameters     the parameter list.
     * @param parameterTypes the parameter type list.
     */
    public SelectContext(final ConnectionInfo connectionInfo, final Object[] parameters,
                         final ParadoxType[] parameterTypes) {
        super(connectionInfo, parameters, parameterTypes);
    }

    /**
     * Cancel this statement execution.
     */
    @Override
    public void cancel() {
        this.cancelled = true;
    }

    /**
     * Gets the {@link java.sql.ResultSet} max rows.
     *
     * @return the {@link java.sql.ResultSet} max rows.
     */
    public int getMaxRows() {
        return maxRows;
    }

    /**
     * Sets the {@link java.sql.ResultSet} max rows.
     *
     * @param maxRows the {@link java.sql.ResultSet} max rows.
     */
    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }
}
