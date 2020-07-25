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
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;

import java.sql.SQLException;

/**
 * The SQL ASCII function.
 *
 * @version 1.4
 * @since 1.6.0
 */
public class AsciiFunction extends AbstractStringFunction {

    /**
     * The function name.
     */
    @SuppressWarnings("i18n-java:V1008")
    public static final String NAME = "ASCII";

    /**
     * Column parameter list.
     */
    private static final Column[] COLUMNS = {
            new Column(null, ParadoxType.INTEGER, "A unicode integer value.", 0, true, RESULT),
            new Column("integer", ParadoxType.CHAR, "The char value to convert.", 1, false, IN)
    };

    static {
        // Fix the second parameter size.
        COLUMNS[1].setSize(1);
    }

    @Override
    public String getRemarks() {
        return "Converts a character value to unicode integer value.";
    }

    @Override
    public Column[] getColumns() {
        return COLUMNS;
    }

    @Override
    public Object execute(final ParadoxConnection connection, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) throws SQLException {
        Object value = values[0];
        if (value != null && !value.toString().isEmpty()) {
            return (int) value.toString().charAt(0);
        }

        return null;
    }
}
