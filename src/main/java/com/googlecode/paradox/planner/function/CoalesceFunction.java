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
package com.googlecode.paradox.planner.function;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.exceptions.ParadoxSyntaxErrorException;
import com.googlecode.paradox.parser.nodes.AsteriskNode;
import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.planner.nodes.ValueNode;

import java.sql.JDBCType;
import java.sql.Types;
import java.util.List;

/**
 * The SQL coalesce function.
 *
 * @version 1.0
 * @since 1.6.0
 */
public class CoalesceFunction implements IFunction {

    /**
     * The function name.
     */
    public static final String NAME = "COALESCE";
    private int sqlType = Types.VARCHAR;

    @Override
    public int sqlType() {
        return sqlType;
    }

    @Override
    public int parameterCount() {
        return 0;
    }

    @Override
    public boolean isVariableParameters() {
        return true;
    }

    @Override
    public Object execute(final ParadoxConnection connection, final Object[] values, final int[] types)
            throws ParadoxSyntaxErrorException {
        if (types.length > 0) {
            int current = Types.NULL;
            for (int type : types) {
                if (current == Types.NULL) {
                    current = type;
                }

                if (current != Types.NULL && current != type) {
                    throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.INCONSISTENT_DATA_TYPE,
                            JDBCType.valueOf(current).name(), JDBCType.valueOf(type).name());
                }
            }
        }

        for (int i = 0; i < values.length; i++) {
            if (values[i] != null) {
                this.sqlType = types[i];
                return values[i];
            }
        }

        return null;
    }

    @Override
    public void validate(final List<SQLNode> parameters) throws ParadoxSyntaxErrorException {
        for (final SQLNode node : parameters) {
            if (node instanceof AsteriskNode) {
                throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.ASTERISK_IN_FUNCTION,
                        node.getPosition());
            }
        }

        if (!parameters.isEmpty()) {
            final SQLNode node = parameters.get(0);

            if (node instanceof ValueNode) {
                this.sqlType = ((ValueNode) node).getSqlType();
            }
        }
    }
}
