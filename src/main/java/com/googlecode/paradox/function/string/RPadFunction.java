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
package com.googlecode.paradox.function.string;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.rowset.ValuesConverter;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * The SQL RPAD function.
 *
 * @version 1.3
 * @since 1.6.0
 */
public class RPadFunction extends AbstractStringFunction {

    /**
     * The function name.
     */
    public static final String NAME = "RPAD";

    @Override
    public String remarks() {
        return "Right-pads a string with another string, to a certain length.";
    }

    @Override
    public Column[] getColumns() {
        return new Column[]{
                new Column(null, ParadoxType.VARCHAR,
                        "The right-padded string.", 0, true, DatabaseMetaData.functionColumnResult),
                new Column("string", ParadoxType.VARCHAR,
                        "The original string.", 1, false, DatabaseMetaData.functionColumnIn),
                new Column("length", ParadoxType.VARCHAR,
                        "The length of the final string.", 2, false, DatabaseMetaData.functionColumnIn),
                new Column("lpad_string", ParadoxType.VARCHAR,
                        "The filler string to use.", 3, false, DatabaseMetaData.functionColumnIn)
        };
    }

    @Override
    public int parameterCount() {
        return 3;
    }

    @Override
    public Object execute(final ParadoxConnection connection, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) throws SQLException {

        if (values[0] == null || values[1] == null) {
            return null;
        }

        final String pattern = values[2].toString();

        final int size = ValuesConverter.getPositiveInteger(values[1]);
        final StringBuilder ret = new StringBuilder(values[0].toString());
        while (ret.length() < size) {
            ret.append(pattern);
        }

        return ret.substring(0, size);
    }
}
