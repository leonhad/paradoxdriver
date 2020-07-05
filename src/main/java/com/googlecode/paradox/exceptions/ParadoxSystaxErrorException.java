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

import com.googlecode.paradox.parser.Token;

import java.sql.SQLSyntaxErrorException;

public class ParadoxSystaxErrorException extends SQLSyntaxErrorException {

    private static final String BASE_CODE = "42";

    public ParadoxSystaxErrorException(final Error error) {
        super(error.description, BASE_CODE + error.code);
    }

    public ParadoxSystaxErrorException(final Error error, final Token token) {
        super(message(error.description, token), BASE_CODE + error.code);
    }

    private static String message(final String message, final Token token) {
        return String.format("%s in %s in %s.", message, token.getValue(), token.getLocation());
    }

    public enum Error {
        EMPTY_SQL("001", "Empty SQL string"),

        NUMBER_FORMAT("002", "Invalid numeric format"),

        UNEXPECTED_END_OF_STATEMENT("003", "Unexpected end of statement"),

        UNEXPECTED_TOKEN("004", "Unexpected token"),

        EMPTY_COLUMN_LIST("005", "Empty column list");

        private final String code;

        private final String description;

        Error(String code, String description) {
            this.code = code;
            this.description = description;
        }
    }
}
