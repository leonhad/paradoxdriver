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

import java.sql.SQLDataException;

public class ParadoxException extends SQLDataException {

    private static final String BASE_CODE = "01";

    public ParadoxException(final Error error) {
        super(error.description, BASE_CODE + error.code);
    }

    public ParadoxException(final Error error, final String parameter) {
        super(String.format(error.description, parameter), BASE_CODE + error.code);
    }

    public ParadoxException(final Error error, final String parameter, final ScannerPosition position) {
        super(message(String.format(error.description, parameter), position), BASE_CODE + error.code);
    }

    private static String message(final String message, final ScannerPosition position) {
        if (position != null) {
            return String.format("%s at line %s, column %s.", message, position.getLine(), position.getColumn());
        }

        return String.format("%s.", message);
    }

    public enum Error {
        INVALID_COLUMN("001", "Invalid column name: %s"),

        USE_NEXT_FIRST("002", "Call ResultSet.next() first"),

        NO_MORE_ROWS("003", "There are no more rows to read"),

        RESULT_SET_CLOSED("004", "The ResultSet is closed"),

        TYPE_NOT_FOUND("005", "Type not found"),

        COLUMN_AMBIGUOUS_DEFINED("006", "Column %s ambiguous defined"),

        INVALID_COLUMN_INDEX("007", "Invalid column index: %s"),

        INVALID_FETCH_DIRECTION("008", "Unsupported fetch direction %s"),

        INVALID_LENGTH_SPECIFIED("009", "Invalid length specified"),

        INVALID_POSITION_SPECIFIED("010", "Invalid position specified"),

        EMPTY_COLUMN_NAME("010", "Empty column name"),

        INVALID_CATALOG_NAME("010", "Invalid catalog name"),

        SCHEMA_NOT_FOUND("011", "Schema not found"),

        INVALID_TRANSACTION_LEVEL("012", "Invalid transaction level"),

        INVALID_TABLE("013", "Invalid table name: %s"),

        TABLE_AMBIGUOUS_DEFINED("014", "Table %s ambiguous defined");

        private final String code;

        private final String description;

        Error(String code, String description) {
            this.code = code;
            this.description = description;
        }
    }
}
