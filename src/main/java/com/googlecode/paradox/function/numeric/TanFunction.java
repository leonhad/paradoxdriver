/*
 * Copyright (c) 2009 Leonardo Alves da Costa
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
 * The SQL TAN functions.
 *
 * @since 1.6.0
 */
public class TanFunction extends AbstractNumericFunction {

    /**
     * The function name.
     */
    public static final String NAME = "TAN";

    /**
     * Column parameter list.
     */
    private static final Column[] COLUMNS = {
            new Column(null, ParadoxType.NUMBER, "The the tangent of a number.", 0, false, RESULT),
            new Column("number", ParadoxType.NUMBER, "A numeric value.", 1, false, IN)
    };

    /**
     * Creates a new instance.
     */
    public TanFunction() {
        super();
    }

    @Override
    public String getRemarks() {
        return "Returns the tangent of a number.";
    }

    @Override
    public Column[] getColumns() {
        return COLUMNS;
    }

    @Override
    public Object execute(final ConnectionInfo connectionInfo, final Object[] values, final ParadoxType[] types, final FieldNode[] fields) {
        final Double value = ValuesConverter.getDouble(values[0], connectionInfo);
        if (value == null) {
            return null;
        }

        return Math.tan(value);
    }
}
