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

/**
 * The SQL bit length function.
 *
 * @since 1.6.0
 */
public class BitLengthFunction extends OctetLengthFunction {

    /**
     * The function name.
     */
    public static final String NAME = "BIT_LENGTH";

    /**
     * Column parameter list.
     */
    private static final Column[] COLUMNS = {
            new Column(null, ParadoxType.NUMBER, "The bit count of byte values.", 0, true, RESULT),
            new Column("bytes", ParadoxType.BLOB, "The byte values to count.", 1, true, IN)
    };

    /**
     * Creates a new instance.
     */
    public BitLengthFunction() {
        super();
    }

    @Override
    public String getRemarks() {
        return "Gets the length of the binary values in bits.";
    }

    @Override
    public Column[] getColumns() {
        return COLUMNS;
    }

    @Override
    public Object execute(final ConnectionInfo connectionInfo, final Object[] values, final ParadoxType[] types, final FieldNode[] fields) {
        return ((Integer) super.execute(connectionInfo, values, types, fields)) * 0x08;
    }
}
