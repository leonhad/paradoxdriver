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
import com.googlecode.paradox.exceptions.ParadoxSyntaxErrorException;
import com.googlecode.paradox.function.AbstractFunction;
import com.googlecode.paradox.function.FunctionType;
import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.planner.nodes.ValueNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.rowset.ValuesConverter;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * The SQL DATE ADD function.
 *
 * @version 1.1
 * @since 1.6.0
 */
@SuppressWarnings({"i18n-java:V1017", "java:S109"})
public class DateAddFunction extends AbstractFunction {

    /**
     * The function name.
     */
    public static final String NAME = "DATEADD";

    private static final String[] VALID_FORMATS = {"MILLISECOND", "SECOND", "MINUTE", "HOUR", "DAY", "DAYOFYEAR",
            "WEEK", "MONTH", "QUARTER", "YEAR"};

    static {
        // Allow binary search.
        Arrays.sort(VALID_FORMATS);
    }

    @Override
    public String remarks() {
        return "Adds a time/date interval to a date and then returns the date. The interval can be: "
                + Arrays.toString(VALID_FORMATS) + ".";
    }

    @Override
    public Column[] getColumns() {
        return new Column[]{
                new Column(null, ParadoxType.TIMESTAMP, "The result date.", 0, false,
                        DatabaseMetaData.functionColumnResult),
                new Column("format", ParadoxType.VARCHAR,
                        "The interval format. The interval can be: " + Arrays.toString(VALID_FORMATS) + ".", 1, false,
                        DatabaseMetaData.functionColumnIn),
                new Column("number", ParadoxType.INTEGER, "The value to add.", 2, false,
                        DatabaseMetaData.functionColumnIn),
                new Column("date", ParadoxType.TIMESTAMP, "The date to add.", 3, false,
                        DatabaseMetaData.functionColumnIn)
        };
    }

    @Override
    public FunctionType type() {
        return FunctionType.TIME_DATE;
    }

    @Override
    public ParadoxType fieldType() {
        return ParadoxType.TIMESTAMP;
    }

    @Override
    public int parameterCount() {
        return 3;
    }

    @Override
    public Object execute(final ParadoxConnection connection, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) throws SQLException {

        final Integer number = ValuesConverter.getInteger(values[1]);
        final Timestamp timestamp = ValuesConverter.getTimestamp(values[2]);
        if (number == null || timestamp == null) {
            return null;
        }

        final String format = values[0].toString();

        final Calendar c = Calendar.getInstance();
        c.setTime(timestamp);

        switch (format) {
            case "MILLISECOND":
                c.add(Calendar.MILLISECOND, number);
                break;
            case "SECOND":
                c.add(Calendar.SECOND, number);
                break;
            case "MINUTE":
                c.add(Calendar.MINUTE, number);
                break;
            case "HOUR":
                c.add(Calendar.HOUR_OF_DAY, number);
                break;
            case "DAY":
                c.add(Calendar.DAY_OF_MONTH, number);
                break;
            case "DAYOFYEAR":
                c.add(Calendar.DAY_OF_YEAR, number);
                break;
            case "MONTH":
                c.add(Calendar.MONTH, number);
                break;
            case "YEAR":
                c.add(Calendar.YEAR, number);
                break;
            case "WEEK":
                c.add(Calendar.WEEK_OF_MONTH, number);
                break;
            case "QUARTER":
                c.add(Calendar.MONTH, number * 0x03);
                break;
            default:
                throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.INVALID_PARAMETER_VALUE,
                        format);
        }

        return new Timestamp(c.getTimeInMillis());
    }

    @Override
    @SuppressWarnings({"i18n-java:V1018", "java:S1449"})
    public void validate(final List<SQLNode> parameters) throws ParadoxSyntaxErrorException {
        testForAsterisk(parameters);

        final SQLNode value = parameters.get(0);
        if (Arrays.binarySearch(VALID_FORMATS, value.getName().toUpperCase()) < 0) {
            throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.INVALID_PARAMETER_VALUE,
                    value.getName());
        }

        // Convert to a non fields do avoid Planner problems.
        parameters.set(0, new ValueNode(value.getName().toUpperCase(), value.getPosition(), ParadoxType.VARCHAR));
    }
}
