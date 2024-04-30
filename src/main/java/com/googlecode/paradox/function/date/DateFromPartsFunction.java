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

import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * The SQL DATE FROM PARTS function.
 *
 * @version 1.3
 * @since 1.6.0
 */
@SuppressWarnings({"i18n-java:V1017", "java:S109"})
public class DateFromPartsFunction extends AbstractDateFunction {

    /**
     * The function name.
     */
    public static final String NAME = "DATEFROMPARTS";

    /**
     * Column parameter list.
     */
    private static final Column[] COLUMNS = {
            new Column(null, ParadoxType.DATE, "The date value.", 0, false, RESULT),
            new Column("year", ParadoxType.INTEGER, "Specifies a year (4 digits).", 1, false, IN),
            new Column("month", ParadoxType.INTEGER, "Specifies a month (from 1 to 12).", 2, false, IN),
            new Column("day", ParadoxType.INTEGER, "Specifies a day (from 1 to 31).", 3, false, IN)
    };

    @Override
    public String getRemarks() {
        return "Extract a date from the specified parts (year, month, and day values).";
    }

    @Override
    public Column[] getColumns() {
        return COLUMNS;
    }

    @Override
    public Object execute(final ConnectionInfo connectionInfo, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) throws SQLException {

        final Integer year = ValuesConverter.getInteger(values[0], connectionInfo);
        final Integer month = ValuesConverter.getInteger(values[1], connectionInfo);
        final Integer day = ValuesConverter.getInteger(values[2], connectionInfo);
        if (year == null || month == null || day == null) {
            return null;
        }

        final Calendar c = new GregorianCalendar(year, month - 1, day);
        return new Date(c.getTimeInMillis());
    }
}
