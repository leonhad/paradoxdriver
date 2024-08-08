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
import com.googlecode.paradox.exceptions.ParadoxSyntaxErrorException;
import com.googlecode.paradox.exceptions.SyntaxError;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.rowset.ValuesConverter;

import java.sql.SQLException;

/**
 * The SQL SUBSTRING function.
 *
 * @since 1.6.0
 */
public class SubstringFunction extends AbstractStringFunction {

    /**
     * The function name.
     */
    public static final String NAME = "SUBSTRING";

    /**
     * Column parameter list.
     */
    private static final Column[] COLUMNS = {
            new Column(null, ParadoxType.VARCHAR, "The extracted string.", 0, false, RESULT),
            new Column("value", ParadoxType.VARCHAR, "The string to extract from.", 1, false, IN),
            new Column("start", ParadoxType.INTEGER, "The start position. Begin with 1.", 2, false, IN),
            new Column("length", ParadoxType.INTEGER, "The amount to extract.", 3, false, IN)
    };

    /**
     * Creates a new instance.
     */
    public SubstringFunction() {
        super();
    }

    @Override
    public String getRemarks() {
        return "Extracts some characters from a string.";
    }

    @Override
    public Column[] getColumns() {
        return COLUMNS;
    }

    @Override
    public Object execute(final ConnectionInfo connectionInfo, final Object[] values, final ParadoxType[] types, final FieldNode[] fields) throws SQLException {

        final int index = ValuesConverter.getPositiveInteger(values[1], connectionInfo) - 1;
        if (index == -1) {
            throw new ParadoxSyntaxErrorException(SyntaxError.INVALID_PARAMETER_VALUE, values[1]);
        }

        final String value = values[0].toString();
        if (index >= value.length()) {
            return "";
        }

        final int length = ValuesConverter.getPositiveInteger(values[2], connectionInfo);
        if (index + length > value.length()) {
            return value.substring(index);
        }

        return value.substring(index, index + length);
    }
}
