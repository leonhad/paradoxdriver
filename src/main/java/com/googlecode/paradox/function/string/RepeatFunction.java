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
package com.googlecode.paradox.function.string;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.function.AbstractFunction;
import com.googlecode.paradox.function.FunctionType;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.rowset.ValuesConverter;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * The SQL REPEAT function.
 *
 * @version 1.2
 * @since 1.6.0
 */
public class RepeatFunction extends AbstractFunction {

    /**
     * The function name.
     */
    public static final String NAME = "REPEAT";

    @Override
    public String remarks() {
        return "Return a repeated string a specified number of times.";
    }

    @Override
    public Column[] getColumns() {
        return new Column[]{
                new Column(null, ParadoxType.VARCHAR,
                        "The string repeated.", 0, true, DatabaseMetaData.functionColumnResult),
                new Column("string", ParadoxType.VARCHAR,
                        "The string to repeat.", 1, true, DatabaseMetaData.functionColumnIn),
                new Column("count", ParadoxType.INTEGER,
                        "The repeat count.", 2, false, DatabaseMetaData.functionColumnIn)
        };
    }

    @Override
    public FunctionType type() {
        return FunctionType.STRING;
    }

    @Override
    public ParadoxType fieldType() {
        return ParadoxType.VARCHAR;
    }

    @Override
    public int parameterCount() {
        return 2;
    }

    @Override
    public Object execute(final ParadoxConnection connection, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) throws SQLException {

        final int size = ValuesConverter.getPositiveInteger(values[1]);
        final StringBuilder ret = new StringBuilder();
        for (int i = 0; i < size; i++) {
            ret.append(values[0]);
        }

        return ret.toString();
    }
}
