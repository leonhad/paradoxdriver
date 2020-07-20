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

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.exceptions.ParadoxSyntaxErrorException;
import com.googlecode.paradox.function.IFunction;
import com.googlecode.paradox.parser.nodes.AsteriskNode;
import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.rowset.ValuesConverter;

import java.sql.JDBCType;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/**
 * The SQL CAST function.
 *
 * @version 1.0
 * @since 1.6.0
 */
@SuppressWarnings("java:S109")
public class CastFunction implements IFunction {

    /**
     * The function name.
     */
    public static final String NAME = "CAST";
    private int sqlType = Types.VARCHAR;

    @Override
    public int sqlType() {
        return sqlType;
    }

    @Override
    public int parameterCount() {
        return 2;
    }

    @Override
    public Object execute(final ParadoxConnection connection, final Object[] values, final int[] types,
                          final FieldNode[] fields) throws SQLException {

        return ValuesConverter.convert(values[0], sqlType);
    }

    @Override
    @SuppressWarnings({"i18n-java:V1018", "java:S1449"})
    public void validate(List<SQLNode> parameters) throws ParadoxSyntaxErrorException {
        for (final SQLNode node : parameters) {
            if (node instanceof AsteriskNode) {
                throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.ASTERISK_IN_FUNCTION,
                        node.getPosition());
            }
        }

        final SQLNode typeNode = parameters.get(1);
        if (typeNode instanceof FieldNode) {
            try {
                this.sqlType = JDBCType.valueOf(typeNode.getName()).getVendorTypeNumber();
                parameters.remove(1);
            } catch (final IllegalArgumentException e) {
                throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.INVALID_PARAMETER_VALUE,
                        typeNode.getName(), typeNode.getPosition());
            }
        } else {
            throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.INVALID_PARAMETER_VALUE,
                    typeNode.getName(), typeNode.getPosition());
        }
    }
}
