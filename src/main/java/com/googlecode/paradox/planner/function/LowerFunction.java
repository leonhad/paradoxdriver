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
package com.googlecode.paradox.planner.function;

import com.googlecode.paradox.ParadoxConnection;

import java.sql.Types;

/**
 * The SQL lower function.
 *
 * @version 1.0
 * @since 1.6.0
 */
public class LowerFunction implements IFunction {

    /**
     * The function name.
     */
    public static final String NAME = "LOWER";

    @Override
    public int sqlType() {
        return Types.VARCHAR;
    }

    @Override
    public int parameterCount() {
        return 1;
    }

    @Override
    public Object execute(final ParadoxConnection connection, final Object[] values, final int[] types) {
        if (values[0] != null) {
            return values[0].toString().toLowerCase(connection.getLocale());
        }

        return null;
    }
}
