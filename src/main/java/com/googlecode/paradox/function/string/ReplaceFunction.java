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
package com.googlecode.paradox.function.string;

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;

/**
 * The SQL REPLACE function.
 *
 * @since 1.6.0
 */
public class ReplaceFunction extends AbstractStringFunction {

    /**
     * The function name.
     */
    public static final String NAME = "REPLACE";

    /**
     * Column parameter list.
     */
    private static final Column[] COLUMNS = {
            new Column(null, ParadoxType.VARCHAR, "The string or replaced.", 0, true, RESULT),
            new Column("value", ParadoxType.VARCHAR, "The original string.", 1, false, IN),
            new Column("old_string", ParadoxType.VARCHAR, "The string to be replaced.", 2, false, IN),
            new Column("new_string", ParadoxType.VARCHAR, "The new replacement string..", 3, false, IN)
    };

    /**
     * Creates a new instance.
     */
    public ReplaceFunction() {
        super();
    }

    @Override
    public String getRemarks() {
        return "Replaces all occurrences of a substring within a string, with a new substring.";
    }

    @Override
    public Column[] getColumns() {
        return COLUMNS;
    }

    @Override
    public Object execute(final ConnectionInfo connectionInfo, final Object[] values, final ParadoxType[] types, final FieldNode[] fields) {

        return values[0].toString().replace(values[1].toString(), values[2].toString());
    }
}
