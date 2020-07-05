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

import java.sql.SQLDataException;

public class ParadoxException extends SQLDataException {

    private static final String BASE_CODE = "01";

    public ParadoxException(final Error error) {
        super(error.description, BASE_CODE + error.code);
    }

    public enum Error {
        INVALID_COLUMN("001", "Invalid column"),

        USE_NEXT_FIRST("002", "Call ResultSet.next() first"),

        NO_MORE_ROWS("003", "There are no more rows to read"),

        RESULT_SET_CLOSED("004", "The ResultSet is closed"),

        TYPE_NOT_FOUND("005", "Type not found"),

        COLUMN_AMBIGUOUS_DEFINED("006", "Column ambiguous defined");

        private final String code;

        private final String description;

        Error(String code, String description) {
            this.code = code;
            this.description = description;
        }
    }
}
