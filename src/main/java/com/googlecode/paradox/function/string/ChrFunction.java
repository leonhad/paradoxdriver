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
import com.googlecode.paradox.rowset.ValuesConverter;

import java.sql.SQLException;

/**
 * The SQL CHR function.
 *
 * @version 1.5
 * @since 1.6.0
 */
public class ChrFunction extends AbstractStringFunction {

    /**
     * The function name.
     */
    public static final String NAME = "CHR";

    /**
     * Column parameter list.
     */
    private static final Column[] COLUMNS = {
            new Column(null, ParadoxType.CHAR, "A char value.", 0, false, RESULT),
            new Column("number", ParadoxType.INTEGER, "The integer to convert.", 1, true, IN)
    };

    static {
        // Fix the column return size.
        COLUMNS[0].setSize(1);
    }

    @Override
    public String getRemarks() {
        return "Converts a unicode integer value to char.";
    }

    @Override
    public Column[] getColumns() {
        return COLUMNS;
    }

    @Override
    public Object execute(final ParadoxConnection connection, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) throws SQLException {

        final int value = ValuesConverter.getPositiveInteger(values[0]);
        return (char) value;
    }
}
