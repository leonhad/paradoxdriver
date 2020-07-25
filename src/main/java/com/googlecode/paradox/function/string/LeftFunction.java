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
 * The SQL LEFT function.
 *
 * @version 1.3
 * @since 1.6.0
 */
public class LeftFunction extends AbstractStringFunction {

    /**
     * The function name.
     */
    public static final String NAME = "LEFT";

    @Override
    public String getRemarks() {
        return "Extract a part of the string from the left side.";
    }

    @Override
    public Column[] getColumns() {
        return new Column[]{
                new Column(null, ParadoxType.VARCHAR,
                        "The extracted string.", 0, true, DatabaseMetaData.functionColumnResult),
                new Column("string", ParadoxType.VARCHAR,
                        "The string to extract", 1, true, DatabaseMetaData.functionColumnIn),
                new Column("number_of_chars", ParadoxType.INTEGER, "The number of chars to extract", 2, false,
                        DatabaseMetaData.functionColumnIn)
        };
    }

    @Override
    public int getParameterCount() {
        return 2;
    }

    @Override
    public Object execute(final ParadoxConnection connection, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) throws SQLException {
        if (values[0] == null) {
            return null;
        }

        final int size = ValuesConverter.getPositiveInteger(values[1]);
        final StringBuilder ret = new StringBuilder(values[0].toString());
        if (ret.length() > size) {
            ret.delete(size, ret.length());
        }

        return ret.toString();
    }
}
