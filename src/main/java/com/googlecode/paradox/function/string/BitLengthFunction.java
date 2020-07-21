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

import java.sql.DatabaseMetaData;

/**
 * The SQL bit length function.
 *
 * @version 1.2
 * @since 1.6.0
 */
public class BitLengthFunction extends OctetLengthFunction {

    /**
     * The function name.
     */
    public static final String NAME = "BIT_LENGTH";

    @Override
    public String remarks() {
        return "Gets the length of the binary values in bits.";
    }

    @Override
    public Column[] getColumns() {
        return new Column[]{
                new Column(null, ParadoxType.NUMBER, 8, 15, "The bit count of byte values.", 0, true,
                        DatabaseMetaData.functionColumnResult),
                new Column("bytes", ParadoxType.BLOB, 0, 0, "The byte values to count.", 1, true,
                        DatabaseMetaData.functionColumnIn)
        };
    }

    @Override
    public Object execute(final ParadoxConnection connection, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) {
        return ((Integer) super.execute(connection, values, types, fields)) * 0x08;
    }
}
