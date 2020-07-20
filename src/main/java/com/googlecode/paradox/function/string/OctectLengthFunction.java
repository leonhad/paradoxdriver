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
import com.googlecode.paradox.function.IFunction;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.ParadoxType;

/**
 * The SQL octet length function.
 *
 * @version 1.1
 * @since 1.6.0
 */
public class OctectLengthFunction implements IFunction {

    /**
     * The function name.
     */
    public static final String NAME = "OCTET_LENGTH";

    @Override
    public ParadoxType type() {
        return ParadoxType.INTEGER;
    }

    @Override
    public int parameterCount() {
        return 1;
    }

    @Override
    public Object execute(final ParadoxConnection connection, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) {
        Object value = values[0];
        int ret = 0;
        switch (types[0]) {
            case MEMO:
                // Same as VARCHAR.
            case VARCHAR:
                if (value != null) {
                    ret = value.toString().length();
                }
                break;
            case BOOLEAN:
                ret = Byte.SIZE;
                break;
            case INTEGER:
                ret = Integer.BYTES;
                break;
            case DECIMAL:
                // Same as numeric.
            case NUMBER:
                ret = Double.BYTES;
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
            case TIMESTAMP:
                // Same as TIME.
            case TIME:
                // Same as DATE.
            case DATE:
                ret = Long.BYTES;
                break;
            default:
                ret = 0;
        }

        return ret;
    }
}
