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
package com.googlecode.paradox.function;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.function.definition.IFunction;
import com.googlecode.paradox.rowset.ValuesConverter;

import java.sql.SQLException;
import java.sql.Types;

/**
 * The SQL RPAD function.
 *
 * @version 1.0
 * @since 1.6.0
 */
public class RPadFunction implements IFunction {

    /**
     * The function name.
     */
    public static final String NAME = "RPAD";

    @Override
    public int sqlType() {
        return Types.VARCHAR;
    }

    @Override
    public int parameterCount() {
        return 3;
    }

    @Override
    public Object execute(final ParadoxConnection connection, final Object[] values, final int[] types)
            throws SQLException {

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
