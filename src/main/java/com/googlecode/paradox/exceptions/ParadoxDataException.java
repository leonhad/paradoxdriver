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
 * @version 1.1
 * @since 1.6.0
 */
public class ParadoxDataException extends SQLDataException {

    /**
     * Base code for SQL State.
     */
    private static final String BASE_CODE = "22";

    /**
     * The connection error exception.
     *
     * @param error the error.
     */
    public ParadoxDataException(final Error error) {
        super(ExceptionUtils.message(error.description), BASE_CODE + error.code);
    }

    /**
     * The connection error exception.
     *
     * @param error    the error.
     * @param original original exception.
     */
    public ParadoxDataException(final Error error, final Exception original) {
        super(ExceptionUtils.message(error.description), BASE_CODE + error.code, original);
    }

    /**
     * The connection error exception.
     *
     * @param error     the error.
     * @param parameter message parameters.
     */
    public ParadoxDataException(final Error error, final Object... parameter) {
        super(ExceptionUtils.message(error.description, parameter), BASE_CODE + error.code);
    }

    /**
     * The connection error exception.
     *
     * @param error     the error.
     * @param original  original exception.
     * @param parameter message parameters.
     */
    public ParadoxDataException(final Error error, final Exception original, final Object... parameter) {
        super(ExceptionUtils.message(error.description, parameter), BASE_CODE + error.code, original);
    }

    /**
     * Error codes.
     */
    public enum Error {
        /**
         * Associated blob file not found on loading.
         */
        BLOB_FILE_NOT_FOUND("001", "Blob file not found for table"),

        /**
         * There are too many blob files to load.
         */
        TOO_MANY_BLOB_FILES("002", "Too many blob files for table"),

        /**
         * I/O error in blob loading.
         */
        ERROR_OPENING_BLOB_FILE("003", "Error while opening blob file"),

        /**
         * The file type is not supported.
         */
        FIELD_TYPE_NOT_SUPPORTED("004", "Field type not supported"),

        /**
         * The field size is invalid.
         */
        INVALID_FIELD_SIZE("005", "Invalid field size"),

        /**
         * Error loading data from disk.
         */
        ERROR_LOADING_DATA("006", "Error loading data from disk"),

        /**
         * Invalid date specified.
         */
        INVALID_DATE("007", "Invalid date"),

        /**
         * No result set available to return.
         */
        NO_RESULT_SET_AVAILABLE("008", "ResultSet not available"),

        /**
         * Table not found.
         */
        TABLE_NOT_FOUND("009", "Table %s not found."),

        /**
         * Error trying to load a lob head block.
         */
        BLOB_READ_HEAD_BLOCK("010", "Trying to read a head lob data"),

        /**
         * Error trying to load a lob free block.
         */
        BLOB_READ_FREE_BLOCK("011", "Trying to read a free lob data"),

        /**
         * Invalid blob header.
         */
        BLOB_INVALID_HEADER("012", "Invalid lob header"),

        /**
         * Invalid lob header type.
         */
        BLOB_INVALID_HEADER_TYPE("013", "Invalid lob header type"),

        /**
         * Invalid blob file declared size.
         */
        BLOB_INVALID_DECLARED_SIZE("014", "Invalid lob declared size"),

        /**
         * Invalid index in blob file.
         */
        BLOB_SINGLE_BLOCK_INVALID_INDEX("015", "Offset points to a single blob block but index field is not 0xFF."),

        /**
         * Invalid value conversion.
         */
        INVALID_CONVERSION("016", "Invalid value conversion: %s");

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
