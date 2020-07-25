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
import com.googlecode.paradox.exceptions.SyntaxError;
import com.googlecode.paradox.function.AbstractFunction;
import com.googlecode.paradox.function.FunctionType;
import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.rowset.ValuesConverter;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;
import java.util.TimeZone;

/**
 * The SQL CURRENT_TIME function.
 *
 * @version 1.4
 * @since 1.6.0
 */
public class CurrentTimeFunction extends AbstractFunction {

    /**
     * The function name.
     */
    public static final String NAME = "CURRENT_TIME";

    @Override
    public String remarks() {
        return "Gets the current time.";
    }

    @Override
    public Column[] getColumns() {
        return new Column[]{
                new Column(null, ParadoxType.TIME, "The current time.", 0, false,
                        DatabaseMetaData.functionColumnResult),
                new Column("precision", ParadoxType.INTEGER, "The time precision from 0 to 6. Ignored", 1, true,
                        DatabaseMetaData.functionColumnIn)
        };
    }

    @Override
    public FunctionType type() {
        return FunctionType.TIME_DATE;
    }

    @Override
    public ParadoxType fieldType() {
        return ParadoxType.TIME;
    }

    @Override
    public int parameterCount() {
        return 0;
    }

    @Override
    public boolean isAllowAlias() {
        return true;
    }

    @Override
    public boolean isVariableParameters() {
        return true;
    }

    @Override
    public Object execute(final ParadoxConnection connection, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) throws SQLException {
        if (types.length == 1) {
            final int value = ValuesConverter.getPositiveInteger(values[0]);
            if (value < 0x00 || value > 0x06) {
                throw new ParadoxSyntaxErrorException(SyntaxError.INVALID_PARAMETER_VALUE,
                        values[0]);
            }
        }

        long time = System.currentTimeMillis();
        return ValuesConverter.removeDate(new Time(time + connection.getTimeZone().getOffset(time) - TimeZone.getDefault().getOffset(time)));
    }

    @Override
    public void validate(final List<SQLNode> parameters) throws ParadoxSyntaxErrorException {
        if (parameters.size() > 1) {
            throw new ParadoxSyntaxErrorException(SyntaxError.INVALID_PARAMETER_COUNT, "1");
        }
    }
}
