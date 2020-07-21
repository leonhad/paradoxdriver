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
package com.googlecode.paradox.function.string;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.function.FunctionType;
import com.googlecode.paradox.function.IFunction;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * The SQL POSITION function.
 *
 * @version 1.2
 * @since 1.6.0
 */
public class PositionFunction implements IFunction {

    /**
     * The function name.
     */
    public static final String NAME = "POSITION";

    @Override
    public String remarks() {
    	return "Gets the position of the pattern in a string value.";
    }
    
    @Override
    public Column[] getColumns() {
        return new Column[]{
                new Column(null, ParadoxType.INTEGER, 8, 0, "The position in the string. Zero if no found.", 0, true, DatabaseMetaData.functionColumnResult),
                new Column("string", ParadoxType.VARCHAR, 255, 0, "The string to search from.", 1, true, DatabaseMetaData.functionColumnIn),
                new Column("pattern", ParadoxType.VARCHAR, 255, 0, "The pattern to find.", 2, true, DatabaseMetaData.functionColumnIn)
        };
    }
   
    @Override
    public FunctionType type() {
        return FunctionType.STRING;
    }
    
    @Override
    public ParadoxType fieldType() {
        return ParadoxType.INTEGER;
    }

    @Override
    public int parameterCount() {
        return 2;
    }

    @Override
    public Object execute(final ParadoxConnection connection, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) throws SQLException {

        if (values[0] == null || values[1] == null) {
            return null;
        }

        return values[1].toString().indexOf(values[0].toString()) + 1;
    }
}
