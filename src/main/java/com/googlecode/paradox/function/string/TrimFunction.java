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
import com.googlecode.paradox.exceptions.ParadoxSyntaxErrorException;
import com.googlecode.paradox.function.FunctionType;
import com.googlecode.paradox.function.AbstractFunction;
import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.utils.Constants;
import com.googlecode.paradox.utils.Utils;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * The SQL TRIM function.
 *
 * @version 1.4
 * @since 1.6.0
 */
@SuppressWarnings({"java:S109", "i18n-java:V1017"})
public class TrimFunction extends AbstractFunction {

    /**
     * The function name.
     */
    public static final String NAME = "TRIM";

    /**
     * The trim type to use.
     */
    private TrimType type = TrimType.BOTH;

    /**
     * Creates a new instance.
     */
    public TrimFunction() {
        super();
    }

    @Override
    public String remarks() {
        return "Remove leading and trailing spaces from a string.";
    }

    @Override
    public Column[] getColumns() {
        return new Column[]{
                new Column(null, ParadoxType.VARCHAR, 0, Constants.MAX_STRING_SIZE,
                        "The extracted string.", 0, true, DatabaseMetaData.functionColumnResult),
                new Column("value", ParadoxType.VARCHAR, 0, Constants.MAX_STRING_SIZE,
                        "The string to extract from.", 1, true, DatabaseMetaData.functionColumnIn)
        };
    }

    @Override
    public FunctionType type() {
        return FunctionType.STRING;
    }

    @Override
    public ParadoxType fieldType() {
        return ParadoxType.VARCHAR;
    }

    @Override
    public int parameterCount() {
        return 1;
    }

    @Override
    public boolean isVariableParameters() {
        return true;
    }

    @Override
    public Object execute(final ParadoxConnection connection, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) throws SQLException {
        if (values[0] == null || (values.length > 1 && values[1] == null)) {
            return null;
        }

        if (values.length == 1) {
            // Trim always both using spaces.
            return values[0].toString().trim();
        } else {
            String value = values[1].toString();
            final char[] chars = values[0].toString().toCharArray();
            // Allow binary search.
            Arrays.sort(chars);

            StringBuilder ret = new StringBuilder(value);

            if (type == TrimType.BOTH || type == TrimType.LEADING) {
                // Trim left.
                while (ret.length() > 0 && Arrays.binarySearch(chars, ret.charAt(0)) >= 0) {
                    ret.deleteCharAt(0);
                }
            }

            if (type == TrimType.BOTH || type == TrimType.TRAILING) {
                // Trim right.
                while (ret.length() > 0 && Arrays.binarySearch(chars, ret.charAt(ret.length() - 1)) >= 0) {
                    ret.deleteCharAt(ret.length() - 1);
                }
            }

            return ret.toString();
        }
    }

    @Override
    @SuppressWarnings({"i18n-java:V1018", "java:S1449"})
    public void validate(List<SQLNode> parameters) throws ParadoxSyntaxErrorException {
        testForAsterisk(parameters);

        // If three parameters, the first needs to be a valid type.
        if (parameters.size() > 2) {
            final SQLNode value = parameters.get(0);

            this.type = Utils.searchEnum(TrimType.class, value.getName());
            if (type == null) {
                throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.INVALID_PARAMETER_VALUE,
                        value.getName());
            }

            // Remove the first parameter and convert it to enum type.
            parameters.remove(0);
        }
    }

    public static boolean isInvalidType(final String value) {
        return Utils.searchEnum(TrimType.class, value) == null;
    }

    private enum TrimType {
        BOTH,
        LEADING,
        TRAILING
    }
}
