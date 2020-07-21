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
package com.googlecode.paradox.function;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.exceptions.ParadoxSyntaxErrorException;
import com.googlecode.paradox.parser.nodes.AsteriskNode;
import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;

import java.sql.SQLException;
import java.util.List;

/**
 * SQL function interface.
 *
 * @version 1.3
 * @since 1.6.0
 */
public interface IFunction {

    /**
     * Gets the function type.
     *
     * @return the function type.
     */
    FunctionType type();

    /**
     * Gets the functions remarks.
     *
     * @return the functions remarks.
     */
    String remarks();

    /**
     * Gets the columns metadata.
     *
     * @return the columns metadata.
     */
    Column[] getColumns();

    /**
     * The returned value type.
     *
     * @return the returned value type.
     */
    ParadoxType fieldType();

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
     * @param types      the fields SQL type.
     * @param fields     the original field list.
     * @return The function processed value.
     * @throws SQLException in case of failures.
     */
    Object execute(final ParadoxConnection connection, final Object[] values, final ParadoxType[] types,
                   final FieldNode[] fields) throws SQLException;

    /**
     * If this function can be called without parenthesis.
     *
     * @return <code>true</code> if this function can be called without parenthesis.
     */
    default boolean isAllowAlias() {
        return false;
    }

    /**
     * Let's the function static validate the parameters.
     *
     * @param parameters the parameter list.
     * @throws ParadoxSyntaxErrorException in case of syntax errors.
     */
    default void validate(final List<SQLNode> parameters) throws ParadoxSyntaxErrorException {
        for (final SQLNode node : parameters) {
            if (node instanceof AsteriskNode) {
                throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.ASTERISK_IN_FUNCTION,
                        node.getPosition());
            }
        }
    }
}
