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
package com.googlecode.paradox.function;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.function.definition.IFunction;

import java.sql.Types;

/**
 * The SQL bit length function.
 *
 * @version 1.0
 * @since 1.6.0
 */
public class BitLengthFunction implements IFunction {

    /**
     * The function name.
     */
    public static final String NAME = "BIT_LENGTH";

    @Override
    public int sqlType() {
        return Types.INTEGER;
    }

    @Override
    public int parameterCount() {
        return 1;
    }

    @Override
    public Object execute(final ParadoxConnection connection, final Object[] values, final int[] types) {
        Object value = values[0];
        int ret = 0;
        switch (types[0]) {
            case Types.CLOB:
                // Same as VARCHAR.
            case Types.VARCHAR:
                if (value != null) {
                    ret = value.toString().length();
                }
                break;
            case Types.BOOLEAN:
                // Same as BIT.
            case Types.BIT:
                ret = Byte.SIZE;
                break;
            case Types.INTEGER:
                ret = Integer.BYTES;
                break;
            case Types.FLOAT:
                ret = Float.BYTES;
                break;
            case Types.DECIMAL:
                // Same as numeric.
            case Types.NUMERIC:
                ret = Double.BYTES;
                break;
            case Types.BLOB:
                // Same as binary.
            case Types.BINARY:
                ret = ((byte[]) value).length;
                break;
            default:
                ret = 0;
        }

        return ret * 0x08;
    }
}
