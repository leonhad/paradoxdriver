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

import java.sql.SQLException;

/**
 * The SQL POSITION function.
 *
 * @version 1.4
 * @since 1.6.0
 */
public class PositionFunction extends AbstractStringFunction {

    /**
     * The function name.
     */
    public static final String NAME = "POSITION";

    /**
     * Column parameter list.
     */
    private static final Column[] COLUMNS = {
            new Column(null, ParadoxType.INTEGER, "The position in the string. Zero if no found.", 0, true, RESULT),
            new Column("string", ParadoxType.VARCHAR, "The string to search from.", 1, false, IN),
            new Column("pattern", ParadoxType.VARCHAR, "The pattern to find.", 2, false, IN)
    };

    @Override
    public String getRemarks() {
        return "Gets the position of the pattern in a string value.";
    }

    @Override
    public Column[] getColumns() {
        return COLUMNS;
    }

    @Override
    public Object execute(final ConnectionInfo connectionInfo, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) throws SQLException {

        return values[1].toString().indexOf(values[0].toString()) + 1;
    }
}
