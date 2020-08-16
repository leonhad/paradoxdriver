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

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.rowset.ValuesConverter;

/**
 * The SQL LOG functions.
 *
 * @version 1.4
 * @since 1.6.0
 */
public class LogFunction extends AbstractNumericFunction {

    /**
     * The function name.
     */
    public static final String NAME = "LOG";

    /**
     * Column parameter list.
     */
    private static final Column[] COLUMNS = {
            new Column(null, ParadoxType.NUMBER, "The logarithm of the number.", 0, false, RESULT),
            new Column("number", ParadoxType.NUMBER, "The number to return the natural logarithm of. Must be greater " +
                    "than 0.", 1, false, IN),
            new Column("base", ParadoxType.NUMBER, "The base the natural logarithm is to be calculated with. Must be " +
                    "greater than 1.", 2, true, IN)
    };

    @Override
    public String getRemarks() {
        return "Returns the natural logarithm of a specified number, " +
                "or the logarithm of the number to the specified base.";
    }

    @Override
    public Column[] getColumns() {
        return COLUMNS;
    }

    @Override
    public int getParameterCount() {
        return 1;
    }

    @Override
    public int getMaxParameterCount() {
        return 0x02;
    }

    @Override
    public boolean isVariableParameters() {
        return true;
    }

    @Override
    public Object execute(final ConnectionInfo connectionInfo, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) {
        final Double value = ValuesConverter.getDouble(values[0], connectionInfo);
        if (value == null) {
            return null;
        }

        // There is a base to process?
        if (values.length > 1) {
            final Double base = ValuesConverter.getDouble(values[1], connectionInfo);
            if (base != null) {
                return Math.log(value) / Math.log(base);
            }
        }

        return Math.log(value);
    }
}
