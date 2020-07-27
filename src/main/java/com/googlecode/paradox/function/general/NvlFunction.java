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

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.exceptions.ParadoxSyntaxErrorException;
import com.googlecode.paradox.planner.FieldValueUtils;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;

/**
 * The SQL NVL function.
 *
 * @version 1.4
 * @since 1.6.0
 */
public class NvlFunction extends AbstractGeneralFunction {

    /**
     * The function name.
     */
    public static final String NAME = "NVL";

    /**
     * Column parameter list.
     */
    private static final Column[] COLUMNS = {
            new Column(null, ParadoxType.VARCHAR, "The string or replacement (if first is null).", 0, true, RESULT),
            new Column("string", ParadoxType.VARCHAR, "The string to test if null.", 1, true, IN),
            new Column("replacement", ParadoxType.VARCHAR, "The replacement in case of null.", 2, true, IN)
    };

    private ParadoxType type = ParadoxType.NULL;

    @Override
    public String getRemarks() {
        return "Return a specified value if the string is null.";
    }

    @Override
    public Column[] getColumns() {
        return COLUMNS;
    }

    @Override
    public ParadoxType getFieldType() {
        return type;
    }

    @Override
    public Object execute(final ConnectionInfo connectionInfo, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) throws ParadoxSyntaxErrorException {
        this.type = FieldValueUtils.getSqlType(values, types);

        if (values[0] != null) {
            return values[0];
        }

        return values[1];
    }
}
