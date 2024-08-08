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
 * The SQL UPPER function.
 *
 * @since 1.6.0
 */
public class UpperFunction extends AbstractStringFunction {

    /**
     * The function name.
     */
    public static final String NAME = "UPPER";

    /**
     * Column parameter list.
     */
    private static final Column[] COLUMNS = {
            new Column(null, ParadoxType.VARCHAR, "A varchar value in upper case.", 0, true, RESULT),
            new Column("value", ParadoxType.VARCHAR, "A value to convert.", 1, false, IN)
    };

    /**
     * Creates a new instance.
     */
    public UpperFunction() {
        super();
    }

    @Override
    public String getRemarks() {
        return "Converts a texto to upper case.";
    }

    @Override
    public Column[] getColumns() {
        return COLUMNS;
    }

    @Override
    public Object execute(final ConnectionInfo connectionInfo, final Object[] values, final ParadoxType[] types, final FieldNode[] fields) {

        return values[0].toString().toUpperCase(connectionInfo.getLocale());
    }
}
