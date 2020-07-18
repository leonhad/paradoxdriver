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

public class ParadoxSyntaxErrorException extends SQLSyntaxErrorException {

    private static final String BASE_CODE = "42";

    public ParadoxSyntaxErrorException(final Error error) {
        super(error.description, BASE_CODE + error.code);
    }

    public ParadoxSyntaxErrorException(final Error error, final ScannerPosition position) {
        super(message(error.description, position), BASE_CODE + error.code);
    }

    public ParadoxSyntaxErrorException(final Error error, final String parameter, final ScannerPosition position) {
        super(message(String.format(error.description, parameter), position), BASE_CODE + error.code);
    }

    private static String message(final String message, final ScannerPosition position) {
        if (position != null) {
            return String.format("%s in line %s, column %s.", message, position.getLine(), position.getColumn());
        }

        return message;
    }

    public enum Error {
        EMPTY_SQL("001", "Empty SQL string"),

        NUMBER_FORMAT("002", "Invalid numeric format"),

        UNEXPECTED_END_OF_STATEMENT("003", "Unexpected end of statement"),

        UNEXPECTED_TOKEN("004", "Unexpected token"),

        EMPTY_COLUMN_LIST("005", "Empty column list"),

        INVALID_SELECT_STATEMENT("006", "Invalid SELECT statement"),

        INVALID_CHAR("007", "Invalid value, expected a single char value"),

        EMPTY_TABLE_LIST("008", "Empty table list"),

        EMPTY_CONDITIONAL_LIST("009", "Where without any conditions"),

        ASTERISK_WITHOUT_TABLE("010", "Asterisk without table is not supported"),

        FUNCTION_NOT_FOUND("011", "Function %s not found"),

        INVALID_PARAMETER_COUNT("012", "Invalid function parameter count");

        private final String code;

        private final String description;

        Error(String code, String description) {
            this.code = code;
            this.description = description;
        }
    }
}
