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

public class ParadoxNotSupportedException extends SQLFeatureNotSupportedException {

    private static final String BASE_CODE = "0A";

    public ParadoxNotSupportedException(final Error error) {
        super(error.description, BASE_CODE + error.code);
    }

    public enum Error {
        OPERATION_NOT_SUPPORTED("001", "Operation not supported."),

        CATALOG_CHANGE("002", "Change catalog is not supported"),

        USE_BATCH_OPERATION("003", "Use batch for more than one statement"),

        INCONSISTENT_PARAMETER_LIST("004", "Inconsistent parameter list size");

        private final String code;

        private final String description;

        Error(String code, String description) {
            this.code = code;
            this.description = description;
        }
    }
}
