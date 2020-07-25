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
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.rowset.ValuesConverter;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;

/**
 * The SQL DAYOFWEEK function.
 *
 * @version 1.1
 * @since 1.6.0
 */
@SuppressWarnings({"i18n-java:V1017", "java:S109"})
public class DayOfWeekFunction extends AbstractDateFunction {

    /**
     * The function name.
     */
    public static final String NAME = "DAYOFWEEK";

    /**
     * Column parameter list.
     */
    private static final Column[] COLUMNS = {
            new Column(null, ParadoxType.INTEGER, "The weekday (a number from 1 to 7).", 0, false, RESULT),
            new Column("date", ParadoxType.TIMESTAMP, "The time/datetime to extract the day from.", 1, false, IN)
    };

    @Override
    public String getRemarks() {
        return "Extract the weekday from a timestamp value  (a number from 1 to 7).";
    }

    @Override
    public Column[] getColumns() {
        return COLUMNS;
    }

    @Override
    public Object execute(final ParadoxConnection connection, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) throws SQLException {

        final Date date = ValuesConverter.getDate(values[0]);
        if (date == null) {
            return null;
        }

        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_WEEK);
    }
}
