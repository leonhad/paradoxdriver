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
package com.googlecode.paradox.planner;

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.exceptions.InternalException;
import com.googlecode.paradox.exceptions.ParadoxNotSupportedException;
import com.googlecode.paradox.parser.nodes.SelectNode;
import com.googlecode.paradox.parser.nodes.StatementNode;
import com.googlecode.paradox.planner.plan.Plan;
import com.googlecode.paradox.planner.plan.SelectPlan;

import java.sql.SQLException;

/**
 * Factory to create a SQL execution plan.
 *
 * @version 1.8
 * @since 1.1
 */
public final class Planner {

    /**
     * Utility class, not for use..
     */
    private Planner() {
        // Unused.
    }

    /**
     * Create an execution plan from given statement.
     *
     * @param connectionInfo the connection information.
     * @param statement      the statement to plan.
     * @return the execution plan.
     * @throws SQLException in case of plan errors.
     */
    @SuppressWarnings("java:S1452")
    public static Plan<?> create(final ConnectionInfo connectionInfo, final StatementNode statement)
            throws SQLException {
        Plan<?> ret;
        try {
            if (statement instanceof SelectNode) {
                ret = new SelectPlan(connectionInfo, (SelectNode) statement);
            } else {
                throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
            }

            // Optimize the plan.
            ret.optimize();
        } catch (@SuppressWarnings("java:S1166") final InternalException e) {
            // Allow the use of exception functional wrapping.
            throw e.getCause();
        }

        return ret;
    }
}
