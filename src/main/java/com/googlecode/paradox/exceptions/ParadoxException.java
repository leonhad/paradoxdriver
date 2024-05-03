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
package com.googlecode.paradox.exceptions;

import com.googlecode.paradox.parser.ScannerPosition;

import java.sql.SQLDataException;

/**
 * Generic exception.
 *
 * @version 1.2
 * @since 1.6.0
 */
@SuppressWarnings("java:S110")
public class ParadoxException extends SQLDataException {

    /**
     * SQL state base code.
     */
    private static final String BASE_CODE = "01";

    /**
     * Base code for SQL State.
     */
    private static final String SQL_STATE = BASE_CODE + "000";

    /**
     * The connection error exception.
     *
     * @param error the error.
     */
    public ParadoxException(final Error error) {
        super(ExceptionUtils.message(error.description), SQL_STATE, Integer.parseInt(BASE_CODE + error.code));
    }

    /**
     * The connection error exception.
     *
     * @param error     the error.
     * @param parameter message parameters.
     */
    public ParadoxException(final Error error, final Object... parameter) {
        super(ExceptionUtils.message(error.description, parameter), SQL_STATE,
                Integer.parseInt(BASE_CODE + error.code));
    }

    /**
     * The connection error exception.
     *
     * @param error     the error.
     * @param position  the current scanner position.
     * @param parameter message parameters.
     */
    public ParadoxException(final Error error, final ScannerPosition position, final Object... parameter) {
        super(ExceptionUtils.message(error.description, position, parameter), SQL_STATE,
                Integer.parseInt(BASE_CODE + error.code));
    }

    /**
     * Error codes.
     */
    public enum Error {
        /**
         * Invalid column name.
         */
        INVALID_COLUMN("001", "Invalid column name %s"),

        /**
         * Invalid column name.
         */
        INVALID_COLUMN_FILE("001", "Invalid column name %s in file %s"),

        /**
         * Call next() in result set first.
         */
        USE_NEXT_FIRST("002", "Call ResultSet.next() first"),

        /**
         * There are no more rows to read.
         */
        NO_MORE_ROWS("003", "There are no more rows to read"),

        /**
         * Result set already closed.
         */
        RESULT_SET_CLOSED("004", "The ResultSet is closed"),

        /**
         * Type specified not found.
         */
        TYPE_NOT_FOUND("005", "Type not found"),

        /**
         * There are more than one column with name specified.
         */
        COLUMN_AMBIGUOUS_DEFINED("006", "Column %s ambiguous defined"),

        /**
         * Invalid column index.
         */
        INVALID_COLUMN_INDEX("007", "Invalid column index %s"),

        /**
         * Invalid column index.
         */
        INVALID_COLUMN_INDEX_FILE("007", "Invalid column index %s in file %s"),

        /**
         * Invalid fetch direction.
         */
        INVALID_FETCH_DIRECTION("008", "Unsupported fetch direction %s"),

        /**
         * Invalid length specified.
         */
        INVALID_LENGTH_SPECIFIED("009", "Invalid length specified"),

        /**
         * Invalid position specified.
         */
        INVALID_POSITION_SPECIFIED("010", "Invalid position specified"),

        /**
         * Invalid catalog name.
         */
        INVALID_CATALOG_NAME("011", "Invalid catalog name"),

        /**
         * Schema not found.
         */
        SCHEMA_NOT_FOUND("012", "Schema not found"),

        /**
         * Invalid transaction level.
         */
        INVALID_TRANSACTION_LEVEL("013", "Invalid transaction level"),

        /**
         * There are more than one table with name specified.
         */
        TABLE_AMBIGUOUS_DEFINED("015", "Table %s ambiguous defined"),

        /**
         * Operation cancelled by the user.
         */
        OPERATION_CANCELLED("016", "Operation cancelled by the user");

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
