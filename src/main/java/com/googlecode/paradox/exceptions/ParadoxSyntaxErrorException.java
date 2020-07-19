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
package com.googlecode.paradox.exceptions;

import com.googlecode.paradox.parser.ScannerPosition;

import java.sql.SQLSyntaxErrorException;

/**
 * Exception related to SQL syntax errors.
 *
 * @version 1.1
 * @since 1.6.0
 */
public class ParadoxSyntaxErrorException extends SQLSyntaxErrorException {

    /**
     * Base code for SQL State.
     */
    private static final String BASE_CODE = "42";

    /**
     * The connection error exception.
     *
     * @param error the error.
     */
    public ParadoxSyntaxErrorException(final Error error) {
        super(ExceptionUtils.message(error.description), BASE_CODE + error.code);
    }

    /**
     * The connection error exception.
     *
     * @param error    the error.
     * @param position the current scanner position.
     */
    public ParadoxSyntaxErrorException(final Error error, final ScannerPosition position) {
        super(ExceptionUtils.message(error.description, position), BASE_CODE + error.code);
    }

    /**
     * The connection error exception.
     *
     * @param error     the error.
     * @param position  the current scanner position.
     * @param parameter message parameters.
     */
    public ParadoxSyntaxErrorException(final Error error, final ScannerPosition position, final Object... parameter) {
        super(ExceptionUtils.message(error.description, position, parameter), BASE_CODE + error.code);
    }

    /**
     * The connection error exception.
     *
     * @param error     the error.
     * @param parameter message parameters.
     */
    public ParadoxSyntaxErrorException(final Error error, final Object... parameter) {
        super(ExceptionUtils.message(error.description, parameter), BASE_CODE + error.code);
    }

    /**
     * Error codes.
     */
    public enum Error {
        /**
         * Empty SQL.
         */
        EMPTY_SQL("001", "Empty SQL string"),

        /**
         * Number format error.
         */
        NUMBER_FORMAT("002", "Invalid numeric format"),

        /**
         * Unexpected end of statement.
         */
        UNEXPECTED_END_OF_STATEMENT("003", "Unexpected end of statement"),

        /**
         * Unexpected token.
         */
        UNEXPECTED_TOKEN("004", "Unexpected token"),

        /**
         * Empty column list.
         */
        EMPTY_COLUMN_LIST("005", "Empty column list"),

        /**
         * Invalid select statement.
         */
        INVALID_SELECT_STATEMENT("006", "Invalid SELECT statement"),

        /**
         * Invalid char.
         */
        INVALID_CHAR("007", "Invalid value, expected a single char value"),

        /**
         * Empty table list.
         */
        EMPTY_TABLE_LIST("008", "Empty table list"),

        /**
         * Empty conditional list.
         */
        EMPTY_CONDITIONAL_LIST("009", "Where without any conditions"),

        /**
         * Asterisk without table.
         */
        ASTERISK_WITHOUT_TABLE("010", "Asterisk without table is not supported"),

        /**
         * Function name not found.
         */
        FUNCTION_NOT_FOUND("011", "Function %s not found"),

        /**
         * Invalid parameter count.
         */
        INVALID_PARAMETER_COUNT("012", "Invalid function parameter count, expected %s"),

        /**
         * Invalid parameter count (minimum).
         */
        INVALID_PARAMETER_COUNT_MINIMUM("012", "Invalid function parameter count, expected at last %s"),

        /**
         * Can't use asterisk in function parameter.
         */
        ASTERISK_IN_FUNCTION("013", "Asterisk not supported in this type of function"),

        /**
         * Inconsistent data type.
         */
        INCONSISTENT_DATA_TYPE("014", "Inconsistent data types: expected %s got %s"),

        /**
         * Invalid parameter value.
         */
        INVALID_PARAMETER_VALUE("015", "Invalid parameter value: %s"),

        /**
         * Inconsistent parameter list.
         */
        INCONSISTENT_PARAMETER_LIST("016", "Inconsistent parameter list size");

        /**
         * SQL state code.
         */
        private final String code;

        /**
         * Error description.
         */
        private final String description;

        /**
         * Creates a new instance.
         *
         * @param code        the SQL state code.
         * @param description the error description.
         */
        Error(String code, String description) {
            this.code = code;
            this.description = description;
        }
    }
}
