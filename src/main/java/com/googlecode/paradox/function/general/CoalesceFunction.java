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
package com.googlecode.paradox.function.general;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.exceptions.ParadoxSyntaxErrorException;
import com.googlecode.paradox.function.FunctionType;
import com.googlecode.paradox.function.AbstractFunction;
import com.googlecode.paradox.planner.FieldValueUtils;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.utils.Constants;

import java.sql.DatabaseMetaData;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * The SQL coalesce function.
 *
 * @version 1.3
 * @since 1.6.0
 */
public class CoalesceFunction extends AbstractFunction {

    /**
     * The function name.
     */
    public static final String NAME = "COALESCE";

    private ParadoxType type = ParadoxType.NULL;

    @Override
    public String remarks() {
        return "Return a specified value if the string is null.";
    }

    @Override
    public Column[] getColumns() {
        return new Column[]{
                new Column(null, ParadoxType.VARCHAR,
                        "The string or replacement (if first is null).", 0, true,
                        DatabaseMetaData.functionColumnResult),
                new Column("string", ParadoxType.VARCHAR,
                        "The string to test if null.", 1, true, DatabaseMetaData.functionColumnIn),
                new Column("replacement", ParadoxType.VARCHAR,
                        "The replacement in case of null.", 2, true,
                        DatabaseMetaData.functionColumnIn)
        };
    }

    @Override
    public FunctionType type() {
        return FunctionType.SYSTEM;
    }

    @Override
    public ParadoxType fieldType() {
        return type;
    }

    @Override
    public int parameterCount() {
        return 0;
    }

    @Override
    public boolean isVariableParameters() {
        return true;
    }

    @Override
    public Object execute(final ParadoxConnection connection, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) throws ParadoxSyntaxErrorException {
        this.type = FieldValueUtils.getSqlType(values, types);
        return Stream.of(values).filter(Objects::nonNull).findFirst().orElse(null);
    }
}
