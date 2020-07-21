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
import com.googlecode.paradox.function.IFunction;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;

import java.sql.DatabaseMetaData;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The SQL VERSION functions.
 *
 * @version 1.2
 * @since 1.6.0
 */
public class IsNumericFunction implements IFunction {

    /**
     * The function name.
     */
    public static final String NAME = "ISNUMERIC";
    private static final Logger LOGGER = Logger.getLogger(IsNumericFunction.class.getName());

    @Override
    public String remarks() {
        return "Checks if the value can be a numeric value.";
    }

    @Override
    public Column[] getColumns() {
        return new Column[]{
                new Column(null, ParadoxType.BOOLEAN, 8, 15, "True if the value is numeric.", 0, false,
                        DatabaseMetaData.functionColumnResult),
                new Column("number", ParadoxType.VARCHAR, 8, 15, "The value to check.", 1, true,
                        DatabaseMetaData.functionColumnIn)
        };
    }

    @Override
    public FunctionType type() {
        return FunctionType.NUMERIC;
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

        try {
            Double.parseDouble(String.valueOf(values[0]));
            return 1;
        } catch (final NumberFormatException e) {
            LOGGER.log(Level.FINEST, e.getMessage(), e);
        }

        return 0;
    }
}
