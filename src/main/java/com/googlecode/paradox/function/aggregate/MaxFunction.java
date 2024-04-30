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
package com.googlecode.paradox.function.aggregate;

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.function.aggregate.context.MaxContext;
import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.rowset.ValuesConverter;

import java.math.BigDecimal;
import java.util.List;

/**
 * The SQL MAX function.
 *
 * @version 1.2
 * @since 1.6.0
 */
public class MaxFunction extends AbstractGroupingFunction<BigDecimal> {

    /**
     * The function name.
     */
    public static final String NAME = "MAX";

    /**
     * Column parameter list.
     */
    private static final Column[] COLUMNS = {
            new Column(null, ParadoxType.BCD, "The maximum of the values.", 0, true, RESULT),
            new Column("value", ParadoxType.BCD, "The numeric value to check.", 1, false, IN),
    };

    @Override
    public String getRemarks() {
        return "Returns the maximum of a set of values.";
    }

    @Override
    public Column[] getColumns() {
        return COLUMNS;
    }

    @Override
    public MaxContext execute(final ConnectionInfo connectionInfo, final Object[] values,
                              final ParadoxType[] types, final FieldNode[] fields) {
        final BigDecimal value = ValuesConverter.getBigDecimal(values[0], connectionInfo);
        return new MaxContext(value);
    }

    @Override
    public void validate(final List<SQLNode> parameters) {
        // Do nothing. This function is always valid. We are only counting rows.
    }
}
