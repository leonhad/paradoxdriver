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

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;

/**
 * The SQL OCTET_LENGTH function.
 *
 * @version 1.4
 * @since 1.6.0
 */
public class OctetLengthFunction extends AbstractStringFunction {

    /**
     * The function name.
     */
    public static final String NAME = "OCTET_LENGTH";

    /**
     * Column parameter list.
     */
    private static final Column[] COLUMNS = {
            new Column(null, ParadoxType.INTEGER, "The bytes count.", 0, false, RESULT),
            new Column("bytes", ParadoxType.BLOB, "The byte values to count.", 1, false, IN)
    };

    @Override
    public String getRemarks() {
        return "Gets the length of the binary values in bytes.";
    }

    @Override
    public Column[] getColumns() {
        return COLUMNS;
    }

    @Override
    public Object execute(final ConnectionInfo connectionInfo, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) {
        Object value = values[0];
        int ret = 0;
        switch (types[0]) {
            case CHAR:
                // Same as memo.
            case MEMO:
                // Same as VARCHAR.
            case VARCHAR:
                if (value != null) {
                    ret = value.toString().length();
                }
                break;
            case BLOB:
                // Same as binary.
            case OLE:
                // Same as binary.
            case GRAPHIC:
                // Same as binary.
            case BYTES:
                ret = ((byte[]) value).length;
                break;
            default:
                // Uses the default size.
                ret = types[0].getSize();
        }

        return ret;
    }
}
