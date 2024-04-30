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
package com.googlecode.paradox.function;

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.exceptions.ParadoxSyntaxErrorException;
import com.googlecode.paradox.exceptions.SyntaxError;
import com.googlecode.paradox.parser.nodes.AsteriskNode;
import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * SQL function interface.
 *
 * @version 1.6
 * @since 1.6.0
 */
public abstract class AbstractFunction {

    /**
     * Function IN parameter.
     */
    public static final int IN = DatabaseMetaData.functionColumnIn;

    /**
     * Function returns parameter.
     */
    public static final int RESULT = DatabaseMetaData.functionColumnResult;

    /**
     * Creates a new instance.
     */
    protected AbstractFunction() {
        super();
    }

    /**
     * Gets the function type.
     *
     * @return the function type.
     */
    public abstract FunctionType getType();

    /**
     * Gets the functions remarks.
     *
     * @return the function remarks.
     */
    public abstract String getRemarks();

    /**
     * Gets the columns metadata.
     *
     * @return the columns metadata.
     */
    public abstract Column[] getColumns();

    /**
     * The returned value type.
     *
     * @return the returned value type.
     */
    public ParadoxType getFieldType() {
        return Stream.of(getColumns())
                .filter(c -> c.getColumnType() == RESULT)
                .map(Column::getType)
                .findFirst().orElse(ParadoxType.NULL);
    }

    /**
     * The function parameters count.
     *
     * @return the function parameters count.
     */
    public int getParameterCount() {
        return (int) Stream.of(getColumns())
                .filter(c -> c.getColumnType() == IN)
                .count();
    }

    /**
     * Gets if this function has variable parameters.
     *
     * @return <code>true</code> if this function has variable parameters.
     */
    public boolean isVariableParameters() {
        return false;
    }

    /**
     * Gets if this function is a grouping function.
     *
     * @return <code>true</code> if this function is a grouping function.
     */
    public boolean isGrouping() {
        return false;
    }

    /**
     * Execute the function.
     *
     * @param connectionInfo the connection information.
     * @param values         the row values.
     * @param types          the fields SQL type.
     * @param fields         the original field list.
     * @return The function processed value.
     * @throws SQLException in case of failures.
     */
    public abstract Object execute(final ConnectionInfo connectionInfo, final Object[] values,
                                   final ParadoxType[] types, final FieldNode[] fields) throws SQLException;

    /**
     * If this function can be called without parenthesis.
     *
     * @return <code>true</code> if this function can be called without parenthesis.
     */
    public boolean isAllowAlias() {
        return false;
    }

    /**
     * Gets the max parameters allowed in function.
     *
     * @return the max parameters allowed in function.
     */
    @SuppressWarnings("java:S3400")
    public int getMaxParameterCount() {
        return 0;
    }

    /**
     * Lets the function validate the parameters in static way.
     *
     * @param parameters the parameter list.
     * @throws ParadoxSyntaxErrorException in case of syntax errors.
     */
    @SuppressWarnings("java:S3242")
    public void validate(final List<SQLNode> parameters) throws ParadoxSyntaxErrorException {
        for (final SQLNode node : parameters) {
            if (node instanceof AsteriskNode) {
                throw new ParadoxSyntaxErrorException(SyntaxError.ASTERISK_IN_FUNCTION,
                        node.getPosition());
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        return o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.getClass());
    }

    public boolean isDeterministic() {
        return true;
    }

    public String definition() {
        return null;
    }
}
