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

import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

    // FIXME use enum.
    private static final String[] VALID_FORMATS = {"MILLISECOND", "SECOND", "MINUTE", "HOUR", "DAY", "DAYOFYEAR",
            "WEEK", "MONTH", "QUARTER", "YEAR"};

    /**
     * Column parameter list.
     */
    private static final Column[] COLUMNS = {
            new Column(null, ParadoxType.INTEGER, "The part of the.", 0, false, RESULT),
            new Column("date_part", ParadoxType.VARCHAR, "The part name to extract.", 1, false, IN),
            new Column("date", ParadoxType.TIMESTAMP, "The date to extract.", 2, false, IN)
    };

    static {
        // Allow binary search.
        Arrays.sort(VALID_FORMATS);
    }

    private static Calendar getTime(final Object value) {
        Time time = ValuesConverter.getTime(value);
        Calendar c = Calendar.getInstance();
        c.setTime(time);
        return c;
    }

    private static Calendar getTimestamp(final Object value) {
        Timestamp timestamp = ValuesConverter.getTimestamp(value);
        Calendar c = Calendar.getInstance();
        c.setTime(timestamp);
        return c;
    }

    private static Calendar getDate(final Object value) {
        Date time = ValuesConverter.getDate(value);
        Calendar c = Calendar.getInstance();
        c.setTime(time);
        return c;
    }

    @Override
    public String getRemarks() {
        return "Extract a value from date/time. The part to extract can be: " + Arrays.toString(VALID_FORMATS) + ".";
    }

    @Override
    public Column[] getColumns() {
        return COLUMNS;
    }

    @Override
    public int getParameterCount() {
        return 2;
    }

    @Override
    public Object execute(final ConnectionInfo connectionInfo, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) throws SQLException {

        final String format = values[0].toString();
        final Object value = values[1];

        int ret;
        switch (format) {
            case "MILLISECOND":
                ret = getTimestamp(value).get(Calendar.MILLISECOND);
                break;
            case "SECOND":
                ret = getTime(value).get(Calendar.SECOND);
                break;
            case "MINUTE":
                ret = getTime(value).get(Calendar.MINUTE);
                break;
            case "HOUR":
                ret = getTime(value).get(Calendar.HOUR_OF_DAY);
                break;
            case "DAY":
                ret = getDate(value).get(Calendar.DAY_OF_MONTH);
                break;
            case "DAYOFYEAR":
                ret = getDate(value).get(Calendar.DAY_OF_YEAR);
                break;
            case "MONTH":
                ret = getDate(value).get(Calendar.MONTH) + 1;
                break;
            case "YEAR":
                ret = getDate(value).get(Calendar.YEAR);
                break;
            case "WEEK":
                ret = getDate(value).get(Calendar.WEEK_OF_YEAR);
                break;
            case "QUARTER":
                ret = (getDate(value).get(Calendar.MONTH) / 3) + 1;
                break;
            default:
                throw new ParadoxSyntaxErrorException(SyntaxError.INVALID_PARAMETER_VALUE,
                        format);
        }

        return ret;
    }

    @Override
    @SuppressWarnings({"i18n-java:V1018", "java:S1449"})
    public void validate(final List<SQLNode> parameters) throws ParadoxSyntaxErrorException {
        super.validate(parameters);

        final SQLNode value = parameters.get(0);
        if (Arrays.binarySearch(VALID_FORMATS, value.getName().toUpperCase()) < 0) {
            throw new ParadoxSyntaxErrorException(SyntaxError.INVALID_PARAMETER_VALUE,
                    value.getName());
        }

        // Convert to a non fields do avoid Planner problems.
        parameters.set(0, new ValueNode(value.getName().toUpperCase(), value.getPosition(),
                ParadoxType.VARCHAR));
    }
}
