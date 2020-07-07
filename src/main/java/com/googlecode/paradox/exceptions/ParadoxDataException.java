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

/**
 * Exception related to data errors.
 *
 * @version 1.0
 * @since 1.6.0
 */
public class ParadoxDataException extends SQLDataException {

    /**
     * Base code for SQL State.
     */
    private static final String BASE_CODE = "22";

    public ParadoxDataException(final Error error) {
        super(error.description, BASE_CODE + error.code);
    }

    public ParadoxDataException(final Error error, final Exception original) {
        super(error.description, BASE_CODE + error.code, original);
    }

    public ParadoxDataException(final Error error, final String parameter, final Exception original) {
        super(String.format(error.description, parameter), BASE_CODE + error.code, original);
    }

    /**
     * Data error codes.
     */
    public enum Error {
        BLOB_FILE_NOT_FOUND("001", "Blob file not found for table"),

        TOO_MANY_BLOB_FILES("002", "Too many blob files for table"),

        ERROR_OPENING_BLOB_FILE("003", "Error while opening blob file"),

        FIELD_TYPE_NOT_SUPPORTED("004", "Field type not supported"),

        INVALID_FIELD_SIZE("005", "Invalid field size"),

        ERROR_LOADING_DATA("006", "Error loading data from disk"),

        INVALID_DATE("007", "Invalid date"),

        NO_RESULT_SET_AVAILABLE("008", "ResultSet not available"),

        TABLE_NOT_FOUND("009", "Table not found."),

        BLOB_READ_HEAD_BLOCK("010", "Trying to read a head lob data"),

        BLOB_READ_FREE_BLOCK("011", "Trying to read a free lob data"),

        BLOB_INVALID_HEADER("012", "Invalid lob header"),

        BLOB_INVALID_HEADER_TYPE("013", "Invalid lob header type"),

        BLOB_INVALID_DECLARED_SIZE("014", "Invalid lob declared size"),

        BLOB_SINGLE_BLOCK_INVALID_INDEX("015", "Offset points to a single blob block but index field is not 0xFF."),

        INVALID_CONVERSION("016", "Invalid value conversion: %s");

        private final String code;

        private final String description;

        Error(String code, String description) {
            this.code = code;
            this.description = description;
        }
    }
}
