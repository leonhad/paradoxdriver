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
 * The SQL CHR function.
 *
 * @version 1.5
 * @since 1.6.0
 */
public class ChrFunction extends AbstractStringFunction {

    /**
     * The function name.
     */
    public static final String NAME = "CHR";

    @Override
    public String getRemarks() {
        return "Converts a UNICODE char to integer value (UNICODE integer value).";
    }

    @Override
    public Column[] getColumns() {
        // FIXME char 1
        return new Column[]{
                new Column(null, ParadoxType.NUMBER, "The integer value of UNICODE char.", 0, false,
                        DatabaseMetaData.functionColumnResult),
                new Column("number", ParadoxType.CHAR, "The UNICODE char to convert.", 1, true,
                        DatabaseMetaData.functionColumnIn)
        };
    }

    @Override
    public ParadoxType getFieldType() {
        return ParadoxType.CHAR;
    }

    @Override
    public int getParameterCount() {
        return 1;
    }

    @Override
    public Object execute(final ParadoxConnection connection, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) throws SQLException {

        final int value = ValuesConverter.getPositiveInteger(values[0]);
        return (char) value;
    }
}
