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

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * The SQL TEXT function.
 *
 * @version 1.3
 * @since 1.6.0
 */
public class TextFunction extends AbstractStringFunction {

    /**
     * The function name.
     */
    public static final String NAME = "TEXT";

    @Override
    public String getRemarks() {
        return "Converts the value to CLOB (text) type.";
    }

    @Override
    public Column[] getColumns() {
        return new Column[]{
                new Column(null, ParadoxType.MEMO, "A CLOB type value.", 0, true,
                        DatabaseMetaData.functionColumnResult),
                new Column("value", ParadoxType.VARCHAR,
                        "A value to convert.", 1, true, DatabaseMetaData.functionColumnIn)
        };
    }

    @Override
    public ParadoxType getFieldType() {
        return ParadoxType.MEMO;
    }

    @Override
    public int getParameterCount() {
        return 1;
    }

    @Override
    public Object execute(final ParadoxConnection connection, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) throws SQLException {
        if (values[0] == null) {
            return null;
        }

        return values[0].toString();
    }
}
