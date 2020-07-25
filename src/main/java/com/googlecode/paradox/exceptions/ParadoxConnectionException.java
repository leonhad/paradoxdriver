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

import java.sql.SQLNonTransientException;

/**
 * Exception for connection problems.
 *
 * @version 1.2
 * @since 1.6.0
 */
public class ParadoxConnectionException extends SQLNonTransientException {

    /**
     * SQL state base code.
     */
    private static final String BASE_CODE = "08";

    /**
     * The connection error exception.
     *
     * @param error the error.
     */
    public ParadoxConnectionException(final Error error) {
        super(ExceptionUtils.message(error.description), BASE_CODE, Integer.parseInt(BASE_CODE + error.code));
    }

    /**
     * Error codes.
     */
    public enum Error {
        /**
         * Directory not found in connection.
         */
        DIRECTORY_NOT_FOUND("001", "Directory not found.");

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
