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
import com.googlecode.paradox.exceptions.ParadoxSyntaxErrorException;
import com.googlecode.paradox.exceptions.SyntaxError;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;

import java.sql.DatabaseMetaData;

/**
 * The SQL CONCAT_WS function.
 *
 * @version 1.4
 * @since 1.6.0
 */
public class ConcatWSFunction extends AbstractStringFunction {

    /**
     * The function name.
     */
    public static final String NAME = "CONCAT_WS";

    @Override
    public String remarks() {
        return "Concatenate a sequence of strings with a separator. This functions support any number of parameters " +
                "above 1.";
    }

    @Override
    public Column[] getColumns() {
        return new Column[]{
                new Column(null, ParadoxType.VARCHAR,
                        "The concatenated string.", 0, true,
                        DatabaseMetaData.functionColumnResult),
                new Column("separator", ParadoxType.VARCHAR,
                        "The string separator", 1, true,
                        DatabaseMetaData.functionColumnIn),
                new Column("value", ParadoxType.VARCHAR,
                        "The string to concatenate", 2, true,
                        DatabaseMetaData.functionColumnIn)
        };
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
                          final FieldNode[] fields) throws ParadoxSyntaxErrorException {
        final Object separator = values[0];
        if (separator == null) {
            throw new ParadoxSyntaxErrorException(SyntaxError.INVALID_PARAMETER_VALUE, separator);
        }

        final StringBuilder ret = new StringBuilder();
        for (int i = 1; i < values.length; i++) {
            Object value = values[i];
            if (value != null) {
                if (i != 1) {
                    ret.append(separator);
                }

                ret.append(value);
            }
        }

        return ret.toString();
    }
}
