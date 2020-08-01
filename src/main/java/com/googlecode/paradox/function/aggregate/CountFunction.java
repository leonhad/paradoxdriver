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
package com.googlecode.paradox.function.aggregate;

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.function.aggregate.context.CountContext;
import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;

import java.util.List;

/**
 * The SQL count function.
 *
 * @version 1.3
 * @since 1.6.0
 */
public class CountFunction extends AbstractGroupingFunction<Integer> {

    /**
     * The function name.
     */
    public static final String NAME = "COUNT";

    /**
     * Column parameter list.
     */
    private static final Column[] COLUMNS = {
            new Column(null, ParadoxType.LONG, "The number of rows.", 0, true, RESULT),
            new Column("value", ParadoxType.NULL, "Any value to count.", 1, false, IN),
    };

    @Override
    public String getRemarks() {
        return "Returns the number of rows that value is not null.";
    }

    @Override
    public Column[] getColumns() {
        return COLUMNS;
    }

    @Override
    public CountContext execute(final ConnectionInfo connectionInfo, final Object[] values,
                                final ParadoxType[] types, final FieldNode[] fields) {
        int value = 0;
        if (values[0] != null) {
            value = 1;
        }

        return new CountContext(value);
    }

    @Override
    public void validate(final List<SQLNode> parameters) {
        // Do nothing. This function is always valid. We are only counting rows.
    }
}
