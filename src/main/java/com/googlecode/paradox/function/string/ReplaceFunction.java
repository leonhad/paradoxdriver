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
import com.googlecode.paradox.function.IFunction;

import java.sql.Types;

/**
 * The SQL REPLACE function.
 *
 * @version 1.0
 * @since 1.6.0
 */
@SuppressWarnings("java:S109")
public class ReplaceFunction implements IFunction {

    /**
     * The function name.
     */
    public static final String NAME = "REPLACE";

    @Override
    public int sqlType() {
        return Types.VARCHAR;
    }

    @Override
    public int parameterCount() {
        return 3;
    }

    @Override
    public Object execute(final ParadoxConnection connection, final Object[] values, final int[] types) {
        if (values[0] == null || values[1] == null || values[2] == null) {
            return null;
        }

        return values[0].toString().replace(values[1].toString(), values[2].toString());
    }
}
