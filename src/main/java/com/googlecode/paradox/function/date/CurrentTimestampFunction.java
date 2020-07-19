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
package com.googlecode.paradox.function.date;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.function.IFunction;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.TimeZone;

/**
 * The SQL CURRENT_TIMESTAMP function.
 *
 * @version 1.2
 * @since 1.6.0
 */
public class CurrentTimestampFunction implements IFunction {

    /**
     * The function name.
     */
    public static final String NAME = "CURRENT_TIMESTAMP";

    @Override
    public int sqlType() {
        return Types.TIMESTAMP;
    }

    @Override
    public int parameterCount() {
        return 0;
    }

    @Override
    public boolean isAllowAlias() {
        return true;
    }

    @Override
    public Object execute(final ParadoxConnection connection, final Object[] values, final int[] types) {
        long time = System.currentTimeMillis();
        return new Timestamp(time + connection.getTimeZone().getOffset(time) - TimeZone.getDefault().getOffset(time));
    }
}
