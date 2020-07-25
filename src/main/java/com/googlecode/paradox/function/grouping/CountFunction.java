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
package com.googlecode.paradox.function.grouping;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.function.FunctionType;
import com.googlecode.paradox.function.AbstractFunction;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.utils.Constants;

import java.sql.DatabaseMetaData;

/**
 * The COUNT grouping function.
 *
 * @version 1.2
 * @since 1.6.0
 */
public class CountFunction extends AbstractFunction {

    /**
     * The function name.
     */
    public static final String NAME = "COUNT";

    @Override
    public String remarks() {
        return "Gets the count of rows in group.";
    }

    @Override
    public Column[] getColumns() {
        return new Column[]{
                new Column(null, ParadoxType.INTEGER, "The row count in group.", 0, false,
                        DatabaseMetaData.functionColumnResult),
                new Column("value", ParadoxType.VARCHAR,
                        "The time precision from 0 to 6. Ignored", 1, true, DatabaseMetaData.functionColumnIn)
        };
    }

    @Override
    public FunctionType type() {
        return FunctionType.NUMERIC;
    }

    @Override
    public ParadoxType fieldType() {
        return ParadoxType.INTEGER;
    }

    @Override
    public int parameterCount() {
        return 1;
    }

    @Override
    public boolean isGrouping() {
        return true;
    }

    @Override
    public Object execute(final ParadoxConnection connection, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) {
        return 0;
    }
}
