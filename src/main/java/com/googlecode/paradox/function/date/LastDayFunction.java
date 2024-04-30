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

import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;

/**
 * The SQL LAST_DAY function.
 *
 * @since 1.6.0
 */
@SuppressWarnings({"i18n-java:V1017", "java:S109"})
public class LastDayFunction extends AbstractDateFunction {

    /**
     * The function name.
     */
    public static final String NAME = "LAST_DAY";

    /**
     * Column parameter list.
     */
    private static final Column[] COLUMNS = {
            new Column(null, ParadoxType.DATE, "The date with the last day of the month.", 0, false, RESULT),
            new Column("date", ParadoxType.TIMESTAMP, "The time/datetime to extract the last day from.", 1, false, IN)
    };

    /**
     * Creates a new instance.
     */
    public LastDayFunction() {
        super();
    }

    @Override
    public String getRemarks() {
        return "Extract the last day of the month for a given date.";
    }

    @Override
    public Column[] getColumns() {
        return COLUMNS;
    }

    @Override
    public Object execute(final ConnectionInfo connectionInfo, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) throws SQLException {

        final Date date = ValuesConverter.getDate(values[0], connectionInfo);
        if (date == null) {
            return null;
        }

        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        return ValuesConverter.removeTime(c.getTime());
    }
}
