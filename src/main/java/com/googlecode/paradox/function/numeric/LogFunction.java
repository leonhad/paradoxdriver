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
package com.googlecode.paradox.function.numeric;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.exceptions.ParadoxSyntaxErrorException;
import com.googlecode.paradox.function.FunctionType;
import com.googlecode.paradox.function.AbstractFunction;
import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.rowset.ValuesConverter;

import java.sql.DatabaseMetaData;
import java.util.List;

/**
 * The SQL LOG functions.
 *
 * @version 1.1
 * @since 1.6.0
 */
public class LogFunction extends AbstractFunction {

    /**
     * The function name.
     */
    public static final String NAME = "LOG";

    @Override
    public String remarks() {
        return "Returns the natural logarithm of a specified number, " +
                "or the logarithm of the number to the specified base.";
    }

    @Override
    public Column[] getColumns() {
        return new Column[]{
                new Column(null, ParadoxType.NUMBER, 8, 15,
                        "The logarithm of the number.", 0, false,
                        DatabaseMetaData.functionColumnResult),
                new Column("number", ParadoxType.NUMBER, 8, 15,
                        "The number to return the natural logarithm of. Must be greater than 0.", 1,
                        false, DatabaseMetaData.functionColumnIn),
                new Column("base", ParadoxType.NUMBER, 8, 15,
                        "The base the natural logarithm is to be calculated with. Must be greater than 1.", 2,
                        true, DatabaseMetaData.functionColumnIn)
        };
    }

    @Override
    public FunctionType type() {
        return FunctionType.NUMERIC;
    }

    @Override
    public ParadoxType fieldType() {
        return ParadoxType.NUMBER;
    }

    @Override
    public int parameterCount() {
        return 1;
    }

    @Override
    public boolean isVariableParameters() {
        return true;
    }

    @Override
    public Object execute(final ParadoxConnection connection, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) {
        final Double value = ValuesConverter.getDouble(values[0]);
        if (value == null) {
            return null;
        }

        if (values.length == 2) {
            final Double base = ValuesConverter.getDouble(values[1]);
            if (base != null) {
                return Math.log(value) / Math.log(base);
            }
        }

        return Math.log(value);
    }

    @Override
    public void validate(List<SQLNode> parameters) throws ParadoxSyntaxErrorException {
        testForAsterisk(parameters);

        if (parameters.size() > 2) {
            throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.INVALID_PARAMETER_COUNT, 3);
        }
    }
}
