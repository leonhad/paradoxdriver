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
package com.googlecode.paradox.function;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.exceptions.ParadoxSyntaxErrorException;
import com.googlecode.paradox.function.definition.IFunction;
import com.googlecode.paradox.parser.nodes.AsteriskNode;
import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.planner.nodes.ValueNode;
import com.googlecode.paradox.rowset.ValuesConverter;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * The SQL EXTRACT function.
 *
 * @version 1.0
 * @since 1.6.0
 */
@SuppressWarnings({"i18n-java:V1017", "java:S109"})
public class ExtractFunction implements IFunction {

    private static final String[] VALID_FORMATS = {"MILLISECOND", "SECOND", "MINUTE", "HOUR", "DAY", "WEEK",
            "MONTH", "QUARTER", "YEAR"};

    /**
     * The function name.
     */
    public static final String NAME = "EXTRACT";

    static {
        // Allow binary search.
        Arrays.sort(VALID_FORMATS);
    }

    @Override
    public int sqlType() {
        return Types.INTEGER;
    }

    @Override
    public int parameterCount() {
        return 2;
    }

    @Override
    public Object execute(final ParadoxConnection connection, final Object[] values, final int[] types)
            throws SQLException {

        if (values[0] == null || values[1] == null) {
            return null;
        }

        final String format = values[0].toString();
        final Object value = values[1];

        int ret;
        switch (format) {
            case "MILLISECOND":
                ret = getTimestamp(value).get(Calendar.MILLISECOND);
                break;
            case "SECOND":
                ret = getTimestamp(value).get(Calendar.SECOND);
                break;
            case "MINUTE":
                ret = getTimestamp(value).get(Calendar.MINUTE);
                break;
            case "HOUR":
                ret = getTimestamp(value).get(Calendar.HOUR_OF_DAY);
                break;
            case "DAY":
                ret = getDate(value).get(Calendar.DAY_OF_MONTH);
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
                throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.INVALID_PARAMETER_VALUE,
                        format);
        }

        return ret;
    }

    private static Calendar getTimestamp(final Object value) {
        Timestamp time = ValuesConverter.getTimestamp(value);
        Calendar c = Calendar.getInstance();
        c.setTime(time);
        return c;
    }

    private static Calendar getDate(final Object value) {
        Date time = ValuesConverter.getDate(value);
        Calendar c = Calendar.getInstance();
        c.setTime(time);
        return c;
    }

    @Override
    public void validate(List<SQLNode> parameters) throws ParadoxSyntaxErrorException {
        for (final SQLNode node : parameters) {
            if (node instanceof AsteriskNode) {
                throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.ASTERISK_IN_FUNCTION,
                        node.getPosition());
            }
        }

        final SQLNode value = parameters.get(0);
        if (Arrays.binarySearch(VALID_FORMATS, value.getName()) == -1) {
            throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.INVALID_PARAMETER_VALUE,
                    value.getName());
        }

        // Convert to a non fields do avoid Planner problems.
        parameters.set(0, new ValueNode(value.getName(), value.getPosition(), Types.VARCHAR));
    }
}