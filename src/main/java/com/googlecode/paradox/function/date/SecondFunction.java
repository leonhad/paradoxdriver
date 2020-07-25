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

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Calendar;

/**
 * The SQL SECOND function.
 *
 * @version 1.1
 * @since 1.6.0
 */
@SuppressWarnings({"i18n-java:V1017", "java:S109"})
public class SecondFunction extends AbstractDateFunction {

    /**
     * The function name.
     */
    public static final String NAME = "SECOND";

    @Override
    public String getRemarks() {
        return "Extract the second from a timestamp value.";
    }

    @Override
    public Column[] getColumns() {
        return new Column[]{
                new Column(null, ParadoxType.INTEGER, "The second.", 0, false,
                        DatabaseMetaData.functionColumnResult),
                new Column("date", ParadoxType.TIMESTAMP, "The time/datetime to extract the minute from.", 1,
                        false, DatabaseMetaData.functionColumnIn)
        };
    }

    @Override
    public ParadoxType getFieldType() {
        return ParadoxType.INTEGER;
    }

    @Override
    public int getParameterCount() {
        return 1;
    }

    @Override
    public Object execute(final ParadoxConnection connection, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) throws SQLException {

        final Time time = ValuesConverter.getTime(values[0]);
        if (time == null) {
            return null;
        }

        final Calendar c = Calendar.getInstance();
        c.setTime(time);
        return c.get(Calendar.SECOND);
    }
}
