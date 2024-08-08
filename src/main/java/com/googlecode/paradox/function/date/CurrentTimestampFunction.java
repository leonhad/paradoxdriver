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
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;

import java.sql.Timestamp;
import java.util.TimeZone;

/**
 * The SQL CURRENT_TIMESTAMP function.
 *
 * @since 1.6.0
 */
public class CurrentTimestampFunction extends AbstractDateFunction {

    /**
     * The function name.
     */
    public static final String NAME = "CURRENT_TIMESTAMP";

    /**
     * Column parameter list.
     */
    private static final Column[] COLUMNS = {
            new Column(null, ParadoxType.TIMESTAMP, "The current timestamp.", 0, false, RESULT)
    };

    /**
     * Creates a new instance.
     */
    public CurrentTimestampFunction() {
        super();
    }

    @Override
    public String getRemarks() {
        return "Gets the current timestamp.";
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
    public boolean isDeterministic() {
        return false;
    }

    @Override
    public Object execute(final ConnectionInfo connectionInfo, final Object[] values, final ParadoxType[] types, final FieldNode[] fields) {
        long time = System.currentTimeMillis();
        return new Timestamp(time + connectionInfo.getTimeZone().getOffset(time)
                - TimeZone.getDefault().getOffset(time));
    }
}
