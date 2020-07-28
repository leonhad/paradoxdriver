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

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.rowset.ValuesConverter;

import java.sql.SQLException;
import java.sql.Time;
import java.util.Calendar;

/**
 * The SQL HOUR function.
 *
 * @version 1.2
 * @since 1.6.0
 */
@SuppressWarnings({"i18n-java:V1017", "java:S109"})
public class HourFunction extends AbstractDateFunction {

    /**
     * The function name.
     */
    public static final String NAME = "HOUR";

    /**
     * Column parameter list.
     */
    private static final Column[] COLUMNS = {
            new Column(null, ParadoxType.INTEGER, "The hour.", 0, false, RESULT),
            new Column("date", ParadoxType.TIMESTAMP, "The time/datetime to extract the hour from.", 1, false, IN)
    };

    @Override
    public String getRemarks() {
        return "Extract the hour from a timestamp value.";
    }

    @Override
    public Column[] getColumns() {
        return COLUMNS;
    }

    @Override
    public Object execute(final ConnectionInfo connectionInfo, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) throws SQLException {

        final Time time = ValuesConverter.getTime(values[0]);
        if (time == null) {
            return null;
        }

        final Calendar c = Calendar.getInstance();
        c.setTime(time);

        return c.get(Calendar.HOUR_OF_DAY);
    }
}
