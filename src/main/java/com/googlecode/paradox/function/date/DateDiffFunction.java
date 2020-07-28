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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * The SQL DATEDIFF function.
 *
 * @version 1.5
 * @since 1.6.0
 */
@SuppressWarnings({"i18n-java:V1017", "java:S109"})
public class DateDiffFunction extends AbstractDateFunction {

    /**
     * The function name.
     */
    public static final String NAME = "DATEDIFF";
    /**
     * Column parameter list.
     */
    private static final Column[] COLUMNS = {
            new Column(null, ParadoxType.LONG, "The difference between dates.", 0, false, RESULT),
            new Column("interval", ParadoxType.VARCHAR, "The date interval to compare.", 1, false, IN),
            new Column("start_date", ParadoxType.TIMESTAMP, "The start date.", 2, false, IN),
            new Column("end_date", ParadoxType.TIMESTAMP, "The end date.", 3, false, IN)
    };
    /**
     * Time interval type.
     */
    private TimeIntervalType type;

    @Override
    public String getRemarks() {
        return "Returns the difference between two dates.";
    }

    @Override
    public Column[] getColumns() {
        return COLUMNS;
    }

    @Override
    public Object execute(final ConnectionInfo connectionInfo, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) throws SQLException {

        final Timestamp beginDate = ValuesConverter.getTimestamp(values[1]);
        final Timestamp endDate = ValuesConverter.getTimestamp(values[2]);
        if (beginDate == null || endDate == null) {
            return null;
        }

        final Instant localBeginDate = Instant.ofEpochMilli(beginDate.getTime());
        final Instant localEndDate = Instant.ofEpochMilli(endDate.getTime());

        final ZoneId zoneId = ZoneId.of(connectionInfo.getTimeZone().getID());
        final LocalDateTime localDate1 = LocalDateTime.ofInstant(localBeginDate, zoneId);
        final LocalDateTime localDate2 = LocalDateTime.ofInstant(localEndDate, zoneId);
        final Period period = Period.between(localDate1.toLocalDate(), localDate2.toLocalDate());

        long ret = 0;
        switch (type) {
            case MILLISECOND:
                ret = ChronoUnit.MILLIS.between(localBeginDate, localEndDate);
                break;
            case SECOND:
                ret = ChronoUnit.SECONDS.between(localBeginDate, localEndDate);
                break;
            case MINUTE:
                ret = ChronoUnit.MINUTES.between(localBeginDate, localEndDate);
                break;
            case HOUR:
                ret = ChronoUnit.HOURS.between(localBeginDate, localEndDate);
                break;
            case DAY:
                // Same as days of the year.
            case DAYOFYEAR:
                ret = ChronoUnit.DAYS.between(localBeginDate, localEndDate);
                break;
            case MONTH:
                ret = period.toTotalMonths();
                break;
            case YEAR:
                ret = period.getYears();
                break;
            case WEEK:
                ret = ChronoUnit.DAYS.between(localBeginDate, localEndDate) / 7;
                break;
            case QUARTER:
                ret = period.toTotalMonths() / 3;
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
}
