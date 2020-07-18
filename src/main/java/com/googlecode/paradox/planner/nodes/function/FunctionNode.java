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
package com.googlecode.paradox.planner.nodes.function;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.exceptions.ParadoxSyntaxErrorException;
import com.googlecode.paradox.parser.ScannerPosition;
import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.planner.FunctionFactory;
import com.googlecode.paradox.planner.function.IFunction;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.planner.nodes.FieldUtils;
import com.googlecode.paradox.planner.nodes.ValueNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Stores a function node.
 *
 * @version 1.1
 * @since 1.6.0
 */
public class FunctionNode extends FieldNode {

    /**
     * The list of parameters of this function.
     */
    private final List<SQLNode> parameters = new ArrayList<>();

    /**
     * This function instance.
     */
    private final IFunction function;

    /**
     * Creates a new instance.
     *
     * @param name     the function name.
     * @param position the current Scanner position.
     * @throws ParadoxSyntaxErrorException in case of function not found.
     */
    public FunctionNode(final String name, final ScannerPosition position) throws ParadoxSyntaxErrorException {
        super(null, name, position);

        function = FunctionFactory.getByName(name);
        if (function == null) {
            throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.FUNCTION_NOT_FOUND, name, position);
        }
    }

    public void validate() throws ParadoxSyntaxErrorException {
        if (function.parameterCount() != parameters.size()) {
            throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.INVALID_PARAMETER_COUNT);
        }
    }

    public List<FieldNode> getFields() {
        return parameters.stream().filter(field -> field instanceof FieldNode)
                .map(field -> (FieldNode) field).collect(Collectors.toList());
    }

    /**
     * Return The list of parameters of this function.
     *
     * @return The list of parameters of this function.
     */
    public List<SQLNode> getParameters() {
        return parameters;
    }

    /**
     * Add a new parameter to this function.
     *
     * @param parameter the parameter to add.
     */
    public void addParameter(final SQLNode parameter) {
        this.parameters.add(parameter);
    }

    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(name);
        buffer.append("(");

        for (int i = 0; i < parameters.size(); i++) {
            if (i != 0) {
                buffer.append(", ");
            }

            buffer.append(parameters.get(i));
        }

        buffer.append(")");
        return buffer.toString();
    }

    public int getSqlType() {
        return function.sqlType();
    }

    @Override
    public String getAlias() {
        if (alias == null || !alias.equals(name)) {
            return super.getAlias();
        } else {
            return toString();
        }
    }

    public Object getValue(final ParadoxConnection connection, final Object[] row, final Object[] valueParameters) {
        final Object[] values = new Object[parameters.size()];
        for (int i = 0; i < parameters.size(); i++) {
            SQLNode param = parameters.get(i);

            if (param instanceof ValueNode) {
                values[i] = param.getName();
            } else {
                values[i] = FieldUtils.getValue(row, (FieldNode) param, valueParameters);
            }
        }

        return function.execute(connection, values);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass() || !super.equals(o)) {
            return false;
        }

        FunctionNode that = (FunctionNode) o;
        return Objects.equals(parameters, that.parameters) && Objects.equals(function, that.function);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), parameters, function);
    }
}
