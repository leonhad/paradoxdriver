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
package com.googlecode.paradox.function.general;

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.exceptions.ParadoxSyntaxErrorException;
import com.googlecode.paradox.exceptions.SyntaxError;
import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.rowset.ValuesConverter;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * The SQL CAST function.
 *
 * @version 1.6
 * @since 1.6.0
 */
@SuppressWarnings("java:S109")
public class CastFunction extends AbstractGeneralFunction {

    /**
     * The function name.
     */
    public static final String NAME = "CAST";

    /**
     * Column parameter list.
     */
    private static final Column[] COLUMNS = {
            new Column(null, ParadoxType.BYTES, "The converted field.", 0, true, RESULT),
            new Column("value", ParadoxType.VARCHAR, "The value to convert.", 1, true, IN),
            new Column("sql_type", ParadoxType.VARCHAR, "The SQL type to convert.", 2, true, IN)
    };

    private ParadoxType type = ParadoxType.BYTES;

    /**
     * Creates a new instance.
     */
    public CastFunction() {
        super();
    }

    @Override
    public String getRemarks() {
        return "Converts a field type to another.";
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
                          final FieldNode[] fields) throws SQLException {

        return ValuesConverter.convert(values[0], type, connectionInfo);
    }

    @Override
    @SuppressWarnings({"i18n-java:V1018", "java:S1449"})
    public void validate(final List<SQLNode> parameters) throws ParadoxSyntaxErrorException {
        super.validate(parameters);

        final SQLNode typeNode = parameters.get(1);
        if (typeNode instanceof FieldNode) {
            try {
                this.type = ParadoxType.valueOf(typeNode.getName().toUpperCase());
                parameters.remove(1);
            } catch (final IllegalArgumentException e) {
                throw new ParadoxSyntaxErrorException(SyntaxError.INVALID_PARAMETER_VALUE,
                        typeNode.getName(), typeNode.getPosition());
            }
        } else {
            throw new ParadoxSyntaxErrorException(SyntaxError.INVALID_PARAMETER_VALUE,
                    typeNode.getName(), typeNode.getPosition());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CastFunction that = (CastFunction) o;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), type);
    }
}
