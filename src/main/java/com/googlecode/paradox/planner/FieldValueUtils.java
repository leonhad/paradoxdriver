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
package com.googlecode.paradox.planner;

import com.googlecode.paradox.exceptions.ParadoxDataException;
import com.googlecode.paradox.exceptions.ParadoxException;
import com.googlecode.paradox.exceptions.ParadoxSyntaxErrorException;
import com.googlecode.paradox.metadata.ParadoxTable;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.planner.nodes.ParameterNode;
import com.googlecode.paradox.planner.nodes.PlanTableNode;
import com.googlecode.paradox.planner.nodes.ValueNode;
import com.googlecode.paradox.results.Column;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.List;

/**
 * Field processing utilities.
 *
 * @version 1.1
 * @since 1.6.0
 */
public final class FieldValueUtils {

    /**
     * Utility class, not for use.
     */
    private FieldValueUtils() {
        // Not used.
    }

    /**
     * Convert a byte array to String using a charset specified.
     *
     * @param bytes   the byte array to convert.
     * @param charset the charset to use.
     * @return the converted String.
     * @throws ParadoxDataException in case of converter errors.
     */
    public static String convert(final byte[] bytes, final Charset charset) throws ParadoxDataException {
        final CharsetDecoder decoder = charset.newDecoder();
        decoder.onMalformedInput(CodingErrorAction.IGNORE);
        decoder.onUnmappableCharacter(CodingErrorAction.IGNORE);
        final ByteBuffer input = ByteBuffer.wrap(bytes);

        try {
            return decoder.decode(input).toString();
        } catch (CharacterCodingException e) {
            throw new ParadoxDataException(ParadoxDataException.Error.ERROR_LOADING_DATA, e);
        }
    }

    /**
     * Gets the first non NULL parameter type.
     *
     * @param values the row values.
     * @param types  the value types.
     * @return the first non NULL parameter's type.
     * @throws ParadoxSyntaxErrorException in case of inconsistent parameter types.
     */
    public static int getSqlType(final Object[] values, final int[] types) throws ParadoxSyntaxErrorException {
        if (types.length > 0) {
            int current = Types.NULL;
            for (int type : types) {
                if (current == Types.NULL) {
                    current = type;
                }

                if (current != Types.NULL && current != type) {
                    // The field types isn't the same (NULL ignored).
                    throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.INCONSISTENT_DATA_TYPE,
                            JDBCType.valueOf(current).name(), JDBCType.valueOf(type).name());
                }
            }
        }

        // Gets the first non null value type.
        for (int i = 0; i < values.length; i++) {
            if (values[i] != null) {
                return types[i];
            }
        }

        return Types.NULL;
    }

    /**
     * Sets the field indexes.
     *
     * @param field   the field node to set the index.
     * @param columns the loaded columns.
     * @param tables  the table list.
     * @throws SQLException in case of column ambiguous defined or field not found.
     */
    public static void setFieldIndex(final FieldNode field, final List<Column> columns,
                                     final Collection<PlanTableNode> tables) throws SQLException {

        // Do not set indexes in value or parameter nodes.
        if (field == null || field instanceof ValueNode || field instanceof ParameterNode) {
            return;
        }

        final String tableName = tables.stream()
                .filter(t -> t.getAlias().equalsIgnoreCase(field.getTableName()))
                .map(PlanTableNode::getTable).map(ParadoxTable::getName)
                .findFirst().orElse(field.getTableName());

        int index = -1;
        for (int i = 0; i < columns.size(); i++) {
            final Column column = columns.get(i);

            // Invalid table name.
            if (tableName != null && !tableName.equalsIgnoreCase(column.getField().getTable().getName())) {
                continue;
            }

            if (column.getField().getName().equalsIgnoreCase(field.getName())) {
                if (index != -1) {
                    throw new ParadoxException(ParadoxException.Error.COLUMN_AMBIGUOUS_DEFINED, field.toString());
                }
                index = i;
            }
        }

        if (index == -1) {
            throw new ParadoxException(ParadoxException.Error.INVALID_COLUMN, field.toString());
        }

        field.setIndex(index);
    }

    /**
     * Gets the row value based on field node.
     *
     * @param row        the row with values.
     * @param field      the field node with column data.
     * @param parameters the parameters list.
     * @return the column value.
     */
    public static Object getValue(final Object[] row, final FieldNode field, final Object[] parameters) {
        Object ret;
        if (field instanceof ParameterNode) {
            ret = ((ParameterNode) field).getValue(parameters);
        } else if (field.getIndex() == -1) {
            // Not a table field.
            ret = field.getName();
        } else {
            ret = row[field.getIndex()];
        }

        return ret;
    }
}
