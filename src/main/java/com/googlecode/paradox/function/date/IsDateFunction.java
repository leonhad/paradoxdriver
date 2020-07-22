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
package com.googlecode.paradox.function.date;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.function.FunctionType;
import com.googlecode.paradox.function.IFunction;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.rowset.ValuesConverter;

import java.sql.DatabaseMetaData;

/**
 * The SQL ISDATE functions.
 *
 * @version 1.0
 * @since 1.6.0
 */
public class IsDateFunction implements IFunction {

    /**
     * The function name.
     */
    public static final String NAME = "ISDATE";

    @Override
    public String remarks() {
        return "Checks if the value can be a date value.";
    }

    @Override
    public Column[] getColumns() {
        return new Column[]{
                new Column(null, ParadoxType.BOOLEAN, 0, 1, "True if the value is date.", 0, false,
                        DatabaseMetaData.functionColumnResult),
                new Column("number", ParadoxType.DATE, 0, 4, "The value to check.", 1, true,
                        DatabaseMetaData.functionColumnIn)
        };
    }

    @Override
    public FunctionType type() {
        return FunctionType.TIME_DATE;
    }

    @Override
    public ParadoxType fieldType() {
        return ParadoxType.INTEGER;
    }

    @Override
    public int parameterCount() {
        return 1;
    }

    @Override
    public Object execute(final ParadoxConnection connection, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) {
        if (values[0] == null) {
            return 0;
        }

        if (ValuesConverter.getDate(values[0]) != null) {
            return 1;
        }

        return 0;
    }
}
