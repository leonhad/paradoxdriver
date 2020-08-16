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
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * The SQL EXTRACT function.
 *
 * @version 1.5
 * @since 1.6.0
 */
@SuppressWarnings({"i18n-java:V1017", "java:S109"})
public class ExtractFunction extends AbstractDateFunction {

    /**
     * The function name.
     */
    public static final String NAME = "EXTRACT";

    /**
     * Time interval type.
     */
    private TimeIntervalType type;

    /**
     * Column parameter list.
     */
    private static final Column[] COLUMNS = {
            new Column(null, ParadoxType.INTEGER, "The part of the.", 0, false, RESULT),
            new Column("date_part", ParadoxType.VARCHAR, "The part name to extract.", 1, false, IN),
            new Column("date", ParadoxType.TIMESTAMP, "The date to extract.", 2, false, IN)
    };

    /**
     * Creates a new instance.
     */
    public ExtractFunction() {
        super();
    }

    private static Calendar getTime(final Object value, final ConnectionInfo connectionInfo) {
        Time time = ValuesConverter.getTime(value, connectionInfo);
        Calendar c = Calendar.getInstance();
        c.setTime(time);
        return c;
    }

    private static Calendar getTimestamp(final Object value, final ConnectionInfo connectionInfo) {
        Timestamp timestamp = ValuesConverter.getTimestamp(value, connectionInfo);
        Calendar c = Calendar.getInstance();
        c.setTime(timestamp);
        return c;
    }

    private static Calendar getDate(final Object value, final ConnectionInfo connectionInfo) {
        Date time = ValuesConverter.getDate(value, connectionInfo);
        Calendar c = Calendar.getInstance();
        c.setTime(time);
        return c;
    }

    @Override
    public String getRemarks() {
        return "Extract a value from date/time.";
    }

    @Override
    public Column[] getColumns() {
        return COLUMNS;
    }

    @Override
    @SuppressWarnings("java:S1541")
    public Object execute(final ConnectionInfo connectionInfo, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) throws SQLException {

        final Object value = values[1];

        int ret = 0;
        switch (type) {
            case MILLISECOND:
                ret = getTimestamp(value, connectionInfo).get(Calendar.MILLISECOND);
                break;
            case SECOND:
                ret = getTime(value, connectionInfo).get(Calendar.SECOND);
                break;
            case MINUTE:
                ret = getTime(value, connectionInfo).get(Calendar.MINUTE);
                break;
            case HOUR:
                ret = getTime(value, connectionInfo).get(Calendar.HOUR_OF_DAY);
                break;
            case DAY:
                ret = getDate(value, connectionInfo).get(Calendar.DAY_OF_MONTH);
                break;
            case DAYOFYEAR:
                ret = getDate(value, connectionInfo).get(Calendar.DAY_OF_YEAR);
                break;
            case MONTH:
                ret = getDate(value, connectionInfo).get(Calendar.MONTH) + 1;
                break;
            case YEAR:
                ret = getDate(value, connectionInfo).get(Calendar.YEAR);
                break;
            case WEEK:
                ret = getDate(value, connectionInfo).get(Calendar.WEEK_OF_YEAR);
                break;
            case QUARTER:
                ret = (getDate(value, connectionInfo).get(Calendar.MONTH) / 3) + 1;
                break;
        }

        return ret;
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

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ExtractFunction that = (ExtractFunction) o;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), type);
    }
}
