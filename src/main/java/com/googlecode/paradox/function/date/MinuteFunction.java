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
package com.googlecode.paradox.function.date;

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.rowset.ValuesConverter;

import java.sql.SQLException;
import java.sql.Time;
import java.util.Calendar;

/**
 * The SQL MINUTE function.
 *
 * @since 1.6.0
 */
public class MinuteFunction extends AbstractDateFunction {

    /**
     * The function name.
     */
    public static final String NAME = "MINUTE";

    /**
     * Column parameter list.
     */
    private static final Column[] COLUMNS = {
            new Column(null, ParadoxType.INTEGER, "The minute.", 0, false, RESULT),
            new Column("date", ParadoxType.TIMESTAMP, "The time/datetime to extract the minute from.", 1, false, IN)
    };

    /**
     * Creates a new instance.
     */
    public MinuteFunction() {
        super();
    }

    @Override
    public String getRemarks() {
        return "Extract the minute from a timestamp value.";
    }

    @Override
    public Column[] getColumns() {
        return COLUMNS;
    }

    @Override
    public Object execute(final ConnectionInfo connectionInfo, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) throws SQLException {

        final Time time = ValuesConverter.getTime(values[0], connectionInfo);
        if (time == null) {
            return null;
        }

        final Calendar c = Calendar.getInstance();
        c.setTime(time);
        return c.get(Calendar.MINUTE);
    }
}
