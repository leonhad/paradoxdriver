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

import java.sql.SQLFeatureNotSupportedException;

/**
 * Exception related to unsupported features.
 *
 * @version 1.2
 * @since 1.6.0
 */
@SuppressWarnings("java:S110")
public class ParadoxNotSupportedException extends SQLFeatureNotSupportedException {

    /**
     * Base code for SQL State.
     */
    private static final String BASE_CODE = "0A";

    /**
     * Base code for SQL State.
     */
    private static final String SQL_STATE = BASE_CODE + "000";

    /**
     * The connection error exception.
     *
     * @param error the error.
     */
    public ParadoxNotSupportedException(final Error error) {
        super(ExceptionUtils.message(error.description), SQL_STATE, Integer.parseInt(0x0A + error.code));
    }

    /**
     * Error codes.
     */
    public enum Error {
        /**
         * Operation is not supported by the driver.
         */
        OPERATION_NOT_SUPPORTED("001", "Operation not supported."),

        /**
         * Can't change catalog name.
         */
        CATALOG_CHANGE("002", "Change catalog is not supported"),

        /**
         * Use batch operation.
         */
        USE_BATCH_OPERATION("003", "Use batch for more than one statement"),

        /**
         * This operation can't be cancelled.
         */
        CANCEL_NOT_SUPPORTED("005", "Cancel operation in this type of statement is not supported.");

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
