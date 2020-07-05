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

public class ParadoxConnectionException extends SQLNonTransientException {

    private static final String BASE_CODE = "08";

    public ParadoxConnectionException(final Error error) {
        super(error.description, BASE_CODE + error.code);
    }

    public enum Error {
        DIRECTORY_NOT_FOUND("001", "Directory not found.");

        private final String code;

        private final String description;

        Error(String code, String description) {
            this.code = code;
            this.description = description;
        }
    }
}
