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
import com.googlecode.paradox.function.FunctionType;
import com.googlecode.paradox.function.AbstractFunction;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.rowset.ValuesConverter;

import java.sql.DatabaseMetaData;

/**
 * The SQL ATN2 functions.
 *
 * @version 1.0
 * @since 1.6.0
 */
public class ATN2Function extends AbstractFunction {

    /**
     * The function name.
     */
    public static final String NAME = "ATN2";

    @Override
    public String remarks() {
        return "Returns the arc-tangent of two numbers.";
    }

    @Override
    public Column[] getColumns() {
        return new Column[]{
                new Column(null, ParadoxType.NUMBER, 8, 15,
                        "The the arc-tangent of a number.", 0, false,
                        DatabaseMetaData.functionColumnResult),
                new Column("number1", ParadoxType.NUMBER, 8, 15, "A numeric value.", 1,
                        false, DatabaseMetaData.functionColumnIn),
                new Column("number2", ParadoxType.NUMBER, 8, 15, "A numeric value.", 2,
                        false, DatabaseMetaData.functionColumnIn)
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
        return 2;
    }

    @Override
    public Object execute(final ParadoxConnection connection, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) {
        final Double value1 = ValuesConverter.getDouble(values[0]);
        final Double value2 = ValuesConverter.getDouble(values[1]);
        if (value1 == null || value2 == null) {
            return null;
        }

        return Math.atan2(value1, value2);
    }
}
