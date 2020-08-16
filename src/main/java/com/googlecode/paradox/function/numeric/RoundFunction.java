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

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * The SQL ROUND functions.
 *
 * @version 1.4
 * @since 1.6.0
 */
public class RoundFunction extends AbstractNumericFunction {

    /**
     * The function name.
     */
    public static final String NAME = "ROUND";

    /**
     * Column parameter list.
     */
    private static final Column[] COLUMNS = {
            new Column(null, ParadoxType.NUMBER, "The rounded number.", 0, false, RESULT),
            new Column("number", ParadoxType.NUMBER, "The number to be rounded.", 1,
                    false, IN),
            new Column("decimal", ParadoxType.INTEGER, "The number of decimal places to round number to.", 2,
                    false, IN),
            new Column("operation", ParadoxType.BOOLEAN, "If true, it rounds the result to the number of " +
                    "decimal, otherwise it truncates the result to the number of decimals. " +
                    "Default value is false.", 3, true, IN)
    };

    @Override
    public String getRemarks() {
        return "Returns a number to a specified number of decimal places.";
    }

    @Override
    public Column[] getColumns() {
        return COLUMNS;
    }

    @Override
    public int getParameterCount() {
        return 0x02;
    }

    @Override
    public boolean isVariableParameters() {
        return true;
    }

    @Override
    public int getMaxParameterCount() {
        return 0x03;
    }

    @Override
    public Object execute(final ConnectionInfo connectionInfo, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) {
        final BigDecimal value = ValuesConverter.getBigDecimal(values[0], connectionInfo);
        final Integer decimal = ValuesConverter.getInteger(values[1], connectionInfo);

        if (value == null || decimal == null) {
            return null;
        }

        Boolean rounding = null;
        if (values.length == 0x03) {
            rounding = ValuesConverter.getBoolean(values[0x02], connectionInfo);
        }

        RoundingMode mode = RoundingMode.HALF_UP;
        if (rounding != null && rounding) {
            mode = RoundingMode.FLOOR;
        }

        return value.setScale(decimal, mode);
    }
}
