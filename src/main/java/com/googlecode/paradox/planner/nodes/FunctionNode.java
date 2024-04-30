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
package com.googlecode.paradox.planner.nodes;

import com.googlecode.paradox.exceptions.ParadoxSyntaxErrorException;
import com.googlecode.paradox.exceptions.SyntaxError;
import com.googlecode.paradox.function.AbstractFunction;
import com.googlecode.paradox.function.FunctionFactory;
import com.googlecode.paradox.function.aggregate.CountFunction;
import com.googlecode.paradox.parser.ScannerPosition;
import com.googlecode.paradox.parser.nodes.AsteriskNode;
import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.planner.FieldValueUtils;
import com.googlecode.paradox.planner.context.Context;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Stores a function node.
 *
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
    private final AbstractFunction function;

    /**
     * Field nodes.
     */
    private FieldNode[] fields;

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
            throw new ParadoxSyntaxErrorException(SyntaxError.FUNCTION_NOT_FOUND, position, name);
        }
    }

    /**
     * Validate the function parameters and values.
     *
     * @param position the scanner current position.
     * @throws ParadoxSyntaxErrorException in case of invalid function call.
     */
    public void validate(final ScannerPosition position) throws ParadoxSyntaxErrorException {
        // Test for parameters count.
        final int parameterCount = function.getParameterCount();
        final int maxParameterCount = function.getMaxParameterCount();
        if (function.isVariableParameters()) {
            if (parameters.size() < parameterCount) {
                throw new ParadoxSyntaxErrorException(SyntaxError.INVALID_PARAMETER_COUNT_MINIMUM, position,
                        parameterCount);
            } else if (maxParameterCount != 0 && maxParameterCount < parameters.size()) {
                throw new ParadoxSyntaxErrorException(SyntaxError.INVALID_PARAMETER_COUNT_MAXIMUM, position,
                        parameters.size(), maxParameterCount);
            }
        } else if (parameterCount != parameters.size()) {
            throw new ParadoxSyntaxErrorException(SyntaxError.INVALID_PARAMETER_COUNT, position, parameterCount);
        }

        // Lets the function validade the parameters statically.
        this.function.validate(this.parameters);
    }

    /**
     * Gets the parameters field list.
     *
     * @return the parameters field list.
     */
    @Override
    public Set<FieldNode> getClauseFields() {
        if (fields == null) {
            fields = parameters.stream().map((SQLNode field) -> {
                if (field instanceof FieldNode) {
                    return (FieldNode) field;
                }

                return null;
            }).toArray(FieldNode[]::new);
        }

        return Arrays.stream(fields).filter(Objects::nonNull).collect(Collectors.toSet());
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
     * Gets if this function is a grouping function.
     *
     * @return <code>true</code> if this function is a grouping function.
     */
    public boolean isGrouping() {
        final List<FunctionNode> functionNodes = this.getGroupingNodes();
        return function.isGrouping() || !functionNodes.isEmpty();
    }

    /**
     * Gets the second pass status.
     *
     * @return the second pass status.
     */
    public boolean isSecondPass() {
        final List<FunctionNode> functionNodes = this.getGroupingNodes();
        return !function.isGrouping() && !functionNodes.isEmpty();
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

    /**
     * The returned value type.
     *
     * @return the returned value type.
     */
    public ParadoxType getType() {
        return function.getFieldType();
    }

    /**
     * Gets the function alias name.
     *
     * @return the function alias name.
     */
    @Override
    public String getAlias() {
        if (alias == null || !alias.equals(name)) {
            return super.getAlias();
        } else {
            // We use toString() method because the function call has parameters values too.
            return toString();
        }
    }

    /**
     * Execute  the function.
     *
     * @param context       the execution context.
     * @param row           the current row values.
     * @param loadedColumns the list of loaded columns.
     * @return The function processed value.
     * @throws SQLException in case of failures.
     */
    public Object execute(final Context context, final Object[] row, final List<Column> loadedColumns)
            throws SQLException {
        final Object[] values = new Object[parameters.size()];
        final ParadoxType[] types = new ParadoxType[parameters.size()];

        for (int i = 0; i < parameters.size(); i++) {
            SQLNode param = parameters.get(i);
            types[i] = ParadoxType.NULL;

            if (param instanceof ValueNode) {
                values[i] = param.getName();
                types[i] = ((ValueNode) param).getType();
            } else if (param instanceof ParameterNode) {
                values[i] = FieldValueUtils.getValue(context, row, (FieldNode) param, loadedColumns);
                types[i] = context.getParameterTypes()[((ParameterNode) param).getParameterIndex()];
            } else if (param instanceof FunctionNode) {
                final FunctionNode functionNode = (FunctionNode) param;

                if (functionNode.isGrouping()) {
                    final int index = getIndex(loadedColumns, functionNode);

                    values[i] = row[index];
                    types[i] = loadedColumns.get(index).getType();
                } else {
                    values[i] = functionNode.execute(context, row, loadedColumns);
                    types[i] = functionNode.getType();
                }
            } else if (param instanceof AsteriskNode) {
                values[i] = param;
                types[i] = ParadoxType.NULL;
            } else {
                values[i] = FieldValueUtils.getValue(context, row, (FieldNode) param, loadedColumns);
                types[i] = loadedColumns.get(((FieldNode) param).getIndex()).getType();
            }
        }

        // Validate null parameter values.
        if (Stream.of(function.getColumns())
                .filter(c -> c.getColumnType() == AbstractFunction.IN && !c.isNullable())
                .anyMatch(c -> values[c.getIndex() - 1] == null)) {
            return null;
        }

        // If no problems found, execute the procedure.
        return function.execute(context.getConnectionInfo(), values, types, fields);
    }

    /**
     * Gets the function index.
     *
     * @param loadedColumns the loaded columns.
     * @param functionNode  the function node to search.
     * @return the function index.
     * @throws ParadoxSyntaxErrorException in case of function not found.
     */
    private static int getIndex(final List<Column> loadedColumns, final FunctionNode functionNode)
            throws ParadoxSyntaxErrorException {
        int index = -1;
        for (int l = 0; l < loadedColumns.size(); l++) {
            if (functionNode.equals(loadedColumns.get(l).getFunction())) {
                index = l;
                break;
            }
        }

        if (index == -1) {
            throw new ParadoxSyntaxErrorException(SyntaxError.INVALID_AGGREGATE_FUNCTION,
                    functionNode.getName());
        }

        return index;
    }

    /**
     * Gets the list of grouping node.
     *
     * @return the list of grouping node.
     */
    public List<FunctionNode> getGroupingNodes() {
        final List<FunctionNode> ret = new ArrayList<>();
        if (function.isGrouping()) {
            ret.add(this);
        }

        for (final SQLNode node : parameters) {
            if (node instanceof FunctionNode) {
                ret.addAll(((FunctionNode) node).getGroupingNodes());
            }
        }

        return ret;
    }

    /**
     * Check if this function is COUNT.
     *
     * @return <code>true</code> if this function is COUNT.
     */
    public boolean isCount() {
        return function instanceof CountFunction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final FunctionNode that = (FunctionNode) o;
        return Objects.deepEquals(parameters, that.parameters) && Objects.equals(function, that.function);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), parameters, function);
    }
}
