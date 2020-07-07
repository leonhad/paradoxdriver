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

public class ParadoxDataException extends SQLDataException {

    private static final String BASE_CODE = "22";

    public ParadoxDataException(final Error error) {
        super(error.description, BASE_CODE + error.code);
    }

    public ParadoxDataException(final Error error, final Exception original) {
        super(error.description, BASE_CODE + error.code, original);
    }

    public enum Error {
        BLOB_FILE_NOT_FOUND("001", "Blob file not found for table"),

        TOO_MANY_BLOB_FILES("002", "Too many blob files for table"),

        ERROR_OPENING_BLOB_FILE("003", "Error while opening blob file"),

        FIELD_TYPE_NOT_SUPPORTED("004", "Field type not supported"),

        INVALID_FIELD_SIZE("005", "Invalid field size"),

        ERROR_LOADING_DATA("006", "Error loading data from disk"),

        INVALID_DATE("007", "Invalid date"),

        NO_RESULT_SET_AVAILABLE("008", "ResultSet not available");

        private final String code;

        private final String description;

        Error(String code, String description) {
            this.code = code;
            this.description = description;
        }
    }
}
