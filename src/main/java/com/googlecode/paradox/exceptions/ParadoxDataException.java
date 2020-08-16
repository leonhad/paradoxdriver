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

import java.sql.SQLDataException;

/**
 * Exception related to data errors.
 *
 * @version 1.3
 * @since 1.6.0
 */
@SuppressWarnings("java:S110")
public class ParadoxDataException extends SQLDataException {

    /**
     * Base code for SQL State.
     */
    private static final String BASE_CODE = "22";

    /**
     * Base code for SQL State.
     */
    private static final String SQL_STATE = BASE_CODE + "000";

    /**
     * The connection error exception.
     *
     * @param dataError the error.
     */
    public ParadoxDataException(final DataError dataError) {
        super(ExceptionUtils.message(dataError.getDescription()), SQL_STATE,
                Integer.parseInt(BASE_CODE + dataError.getCode()));
    }

    /**
     * The connection error exception.
     *
     * @param dataError the error.
     * @param original  original exception.
     */
    public ParadoxDataException(final DataError dataError, final Exception original) {
        super(ExceptionUtils.message(dataError.getDescription()), SQL_STATE,
                Integer.parseInt(BASE_CODE + dataError.getCode()), original);
    }

    /**
     * The connection error exception.
     *
     * @param dataError the error.
     * @param parameter message parameters.
     */
    public ParadoxDataException(final DataError dataError, final Object... parameter) {
        super(ExceptionUtils.message(dataError.getDescription(), parameter), SQL_STATE,
                Integer.parseInt(BASE_CODE + dataError.getCode()));
    }

    /**
     * The connection error exception.
     *
     * @param dataError the error.
     * @param position  the current scanner position.
     * @param parameter message parameters.
     */
    public ParadoxDataException(final DataError dataError, final ScannerPosition position, final Object... parameter) {
        super(ExceptionUtils.message(dataError.getDescription(), position, parameter), SQL_STATE,
                Integer.parseInt(BASE_CODE + dataError.getCode()));
    }

    /**
     * The connection error exception.
     *
     * @param dataError the error.
     * @param original  original exception.
     * @param parameter message parameters.
     */
    public ParadoxDataException(final DataError dataError, final Exception original, final Object... parameter) {
        super(ExceptionUtils.message(dataError.getDescription(), parameter), SQL_STATE,
                Integer.parseInt(BASE_CODE + dataError.getCode()), original);
    }
}
