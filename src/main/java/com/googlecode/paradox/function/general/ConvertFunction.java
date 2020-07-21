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
import com.googlecode.paradox.function.FunctionType;
import com.googlecode.paradox.function.IFunction;
import com.googlecode.paradox.parser.TokenType;
import com.googlecode.paradox.parser.nodes.AsteriskNode;
import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.planner.FieldValueUtils;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.rowset.ValuesConverter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

/**
 * The SQL CONVERT function.
 *
 * @version 1.1
 * @since 1.6.0
 */
@SuppressWarnings("java:S109")
public class ConvertFunction implements IFunction {

    private boolean convertCharset;
    
    private Charset charset;

    /**
     * The function name.
     */
    public static final String NAME = "CONVERT";
    
    private ParadoxType type = ParadoxType.VARCHAR;
    
    @Override
    public String remarks() {
    	return "Convert a string to charset specified. Example: CONVERT('value' USING utf8)";
    }
    
    @Override
    public Column[] getColumns() {
        return new Column[]{
                new Column(null, ParadoxType.VARCHAR, 255, 0, "The string converted with charset specified.", 0, true, DatabaseMetaData.functionColumnResult),
                new Column("value", ParadoxType.VARCHAR, 255, 0, "The value to convert.", 1, true, DatabaseMetaData.functionColumnIn),
                new Column("charset", ParadoxType.VARCHAR, 255, 0, "The charset name to convert.", 2, true, DatabaseMetaData.functionColumnIn)
        };
    }
    
    @Override
    public FunctionType type() {
        return FunctionType.SYSTEM;
    }
    
    @Override
    public ParadoxType fieldType() {
        return type;
    }

    @Override
    public int parameterCount() {
        return 2;
    }

    @Override
    public boolean isVariableParameters() {
        return true;
    }

    @Override
    public Object execute(final ParadoxConnection connection, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) throws SQLException {

        if (convertCharset) {
            Object value = values[0];
            if (value == null) {
                return null;
            } else if (value instanceof String) {
                Charset original = StandardCharsets.UTF_8;
                final FieldNode field = fields[0];
                if (field != null && field.getTable() != null) {
                    original = field.getTable().getCharset();
                }

                byte[] bytes = ((String) value).getBytes(original);

                return new String(bytes, charset);
            } else if (value instanceof byte[]) {
                return FieldValueUtils.convert((byte[]) value, this.charset).replace("\u0000", "");
            }
            throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.INVALID_PARAMETER_VALUE, value);
        } else {
            return ValuesConverter.convert(values[0], type);
        }
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

        if (parameters.size() == 3) {
            // If three parameters, the second needs to be a valid type.

            if (!parameters.get(1).getName().equalsIgnoreCase(TokenType.USING.name())) {
                throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.UNEXPECTED_TOKEN,
                        parameters.get(1).getPosition());
            }

            SQLNode charsetNode = parameters.get(2);
            try {
                charset = Charset.forName(charsetNode.getName());
            } catch (final UnsupportedCharsetException e) {
                throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.UNEXPECTED_TOKEN,
                        charsetNode.getPosition(), charsetNode.getName(), e);
            }

            // Charset conversion.
            convertCharset = true;

            // let in only original string.
            parameters.remove(2);
            parameters.remove(1);
        } else {
            // Conversion between types.

            final SQLNode typeNode = parameters.get(1);
            if (typeNode instanceof FieldNode) {
                try {
                    this.type = ParadoxType.valueOf(typeNode.getName());
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
}
