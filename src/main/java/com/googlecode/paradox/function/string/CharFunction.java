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
package com.googlecode.paradox.function.string;

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.rowset.ValuesConverter;

import java.sql.SQLException;

/**
 * The SQL CHAR function.
 *
 * @since 1.6.0
 */
public class CharFunction extends AbstractStringFunction {

    /**
     * The function name.
     */
    public static final String NAME = "CHAR";

    /**
     * Column parameter list.
     */
    private static final Column[] COLUMNS = {
            new Column(null, ParadoxType.CHAR, "A char type value.", 0, true, RESULT),
            new Column("value", ParadoxType.VARCHAR, "A value to convert.", 1, true, IN)
    };

    /**
     * Creates a new instance.
     */
    public CharFunction() {
        super();
    }

    @Override
    public String getRemarks() {
        return "Converts the value to char type.";
    }

    @Override
    public Column[] getColumns() {
        return COLUMNS;
    }

    @Override
    public Object execute(final ConnectionInfo connectionInfo, final Object[] values, final ParadoxType[] types, final FieldNode[] fields) throws SQLException {
        if (values[0] == null) {
            return null;
        }

        return ValuesConverter.getString(values[0], connectionInfo);
    }
}
