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
import com.googlecode.paradox.rowset.ValuesConverter;

import java.sql.SQLException;

/**
 * The SQL SPACE function.
 *
 * @version 1.5
 * @since 1.6.0
 */
public class SpaceFunction extends AbstractStringFunction {

    /**
     * The function name.
     */
    public static final String NAME = "SPACE";

    /**
     * Column parameter list.
     */
    private static final Column[] COLUMNS = {
            new Column(null, ParadoxType.VARCHAR, "The string spaces.", 0, false, RESULT),
            new Column("space_count", ParadoxType.INTEGER, "The space count.", 1, true, IN)
    };

    @Override
    public String getRemarks() {
        return "Return a string only with spaces.";
    }

    @Override
    public Column[] getColumns() {
        return COLUMNS;
    }

    @Override
    public Object execute(final ConnectionInfo connectionInfo, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) throws SQLException {

        final int size = ValuesConverter.getPositiveInteger(values[0], connectionInfo);
        final StringBuilder ret = new StringBuilder();
        for (int i = 0; i < size; i++) {
            ret.append(' ');
        }

        return ret.toString();
    }
}
