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
import com.googlecode.paradox.exceptions.ParadoxSyntaxErrorException;
import com.googlecode.paradox.exceptions.SyntaxError;
import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.planner.nodes.ValueNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.rowset.ValuesConverter;
import com.googlecode.paradox.utils.Utils;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

/**
 * The SQL DATE ADD function.
 *
 * @version 1.4
 * @since 1.6.0
 */
@SuppressWarnings({"i18n-java:V1017", "java:S109"})
public class DateAddFunction extends AbstractDateFunction {

    /**
     * The function name.
     */
    public static final String NAME = "DATEADD";

    /**
     * Time interval type.
     */
    private TimeIntervalType type;

    /**
     * Column parameter list.
     */
    private static final Column[] COLUMNS = {
            new Column(null, ParadoxType.TIMESTAMP, "The result date.", 0, false, RESULT),
            new Column("format", ParadoxType.VARCHAR, "The interval format.", 1, false, IN),
            new Column("number", ParadoxType.INTEGER, "The value to add.", 2, false, IN),
            new Column("date", ParadoxType.TIMESTAMP, "The date to add.", 3, false, IN)
    };

    /**
     * Creates a new instance.
     */
    public DateAddFunction() {
        super();
    }

    @Override
    public String getRemarks() {
        return "Adds a time/date interval to a date and then returns the date.";
    }

    @Override
    public Column[] getColumns() {
        return COLUMNS;
    }

    @Override
    @SuppressWarnings("java:S1541")
    public Object execute(final ConnectionInfo connectionInfo, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) throws SQLException {

        final Integer number = ValuesConverter.getInteger(values[1], connectionInfo);
        final Timestamp timestamp = ValuesConverter.getTimestamp(values[2], connectionInfo);
        if (number == null || timestamp == null) {
            return null;
        }

        final Calendar c = Calendar.getInstance();
        c.setTime(timestamp);

        switch (type) {
            case MILLISECOND:
                c.add(Calendar.MILLISECOND, number);
                break;
            case SECOND:
                c.add(Calendar.SECOND, number);
                break;
            case MINUTE:
                c.add(Calendar.MINUTE, number);
                break;
            case HOUR:
                c.add(Calendar.HOUR_OF_DAY, number);
                break;
            case DAY:
                c.add(Calendar.DAY_OF_MONTH, number);
                break;
            case DAYOFYEAR:
                c.add(Calendar.DAY_OF_YEAR, number);
                break;
            case MONTH:
                c.add(Calendar.MONTH, number);
                break;
            case YEAR:
                c.add(Calendar.YEAR, number);
                break;
            case WEEK:
                c.add(Calendar.WEEK_OF_MONTH, number);
                break;
            case QUARTER:
                c.add(Calendar.MONTH, number * 0x03);
                break;
        }

        return new Timestamp(c.getTimeInMillis());
    }

    @Override
    @SuppressWarnings({"i18n-java:V1018", "java:S1449"})
    public void validate(final List<SQLNode> parameters) throws ParadoxSyntaxErrorException {
        super.validate(parameters);

        final SQLNode value = parameters.get(0);
        type = Utils.searchEnum(TimeIntervalType.class, value.getName());

        if (type == null) {
            throw new ParadoxSyntaxErrorException(SyntaxError.INVALID_PARAMETER_VALUE,
                    value.getName());
        }

        // Convert to a non fields do avoid Planner problems.
        parameters.set(0, new ValueNode(value.getName().toUpperCase(), value.getPosition(),
                ParadoxType.VARCHAR));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass() || !super.equals(o)) {
            return false;
        }

        DateAddFunction that = (DateAddFunction) o;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), type);
    }
}
