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
import com.googlecode.paradox.function.FunctionType;
import com.googlecode.paradox.function.AbstractFunction;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.rowset.ValuesConverter;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * The SQL DATE function.
 *
 * @version 1.0
 * @since 1.6.0
 */
@SuppressWarnings({"i18n-java:V1017", "java:S109"})
public class DateFunction extends AbstractFunction {

    /**
     * The function name.
     */
    public static final String NAME = "DATE";

    @Override
    public String remarks() {
        return "Extract date from a timestamp value.";
    }

    @Override
    public Column[] getColumns() {
        return new Column[]{
                new Column(null, ParadoxType.DATE, 0, 4, "The date.", 0, false,
                        DatabaseMetaData.functionColumnResult),
                new Column("date", ParadoxType.TIMESTAMP, 0, 11, "The time/datetime to extract the time from.", 1,
                        false, DatabaseMetaData.functionColumnIn)
        };
    }

    @Override
    public FunctionType type() {
        return FunctionType.TIME_DATE;
    }

    @Override
    public ParadoxType fieldType() {
        return ParadoxType.DATE;
    }

    @Override
    public int parameterCount() {
        return 1;
    }

    @Override
    public Object execute(final ParadoxConnection connection, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) throws SQLException {

        return ValuesConverter.getDate(values[0]);
    }
}
