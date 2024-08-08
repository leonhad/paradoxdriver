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
import com.googlecode.paradox.exceptions.ParadoxSyntaxErrorException;
import com.googlecode.paradox.exceptions.SyntaxError;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.rowset.ValuesConverter;

import java.sql.SQLException;
import java.sql.Time;
import java.util.TimeZone;

/**
 * The SQL CURRENT_TIME function.
 *
 * @since 1.6.0
 */
public class CurrentTimeFunction extends AbstractDateFunction {

    /**
     * The function name.
     */
    public static final String NAME = "CURRENT_TIME";

    /**
     * Column parameter list.
     */
    private static final Column[] COLUMNS = {
            new Column(null, ParadoxType.TIME, "The current time.", 0, false, RESULT),
            new Column("precision", ParadoxType.INTEGER, "The time precision from 0 to 6. Ignored", 1, true, IN)
    };

    /**
     * Creates a new instance.
     */
    public CurrentTimeFunction() {
        super();
    }

    @Override
    public String getRemarks() {
        return "Gets the current time.";
    }

    @Override
    public Column[] getColumns() {
        return COLUMNS;
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
    public boolean isDeterministic() {
        return false;
    }

    /**
     * @return Always <code>0</code>, because it has no parameters.
     */
    @Override
    public int getParameterCount() {
        return 0;
    }

    @Override
    public int getMaxParameterCount() {
        return 1;
    }

    @Override
    public Object execute(final ConnectionInfo connectionInfo, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) throws SQLException {
        if (types.length == 1) {
            // This method is keep to adere SQL92 standards. The standards have values between 0 and 6.
            // This value is not used here.
            final int value = ValuesConverter.getPositiveInteger(values[0], connectionInfo);
            if (value < 0x00 || value > 0x06) {
                throw new ParadoxSyntaxErrorException(SyntaxError.INVALID_PARAMETER_VALUE, value);
            }
        }

        long time = System.currentTimeMillis();
        return ValuesConverter.removeDate(new Time(
                time + connectionInfo.getTimeZone().getOffset(time) - TimeZone.getDefault().getOffset(time)
        ));
    }
}
