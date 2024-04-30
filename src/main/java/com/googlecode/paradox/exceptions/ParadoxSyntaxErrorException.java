/*
 * Copyright (c) 2009 Leonardo Alves da Costa
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

import java.sql.SQLSyntaxErrorException;

/**
 * Exception related to SQL syntax errors.
 *
 * @version 1.2
 * @since 1.6.0
 */
@SuppressWarnings("java:S110")
public class ParadoxSyntaxErrorException extends SQLSyntaxErrorException {

    /**
     * Base code for SQL State.
     */
    private static final String BASE_CODE = "42";

    /**
     * Base code for SQL State.
     */
    private static final String SQL_STATE = BASE_CODE + "000";

    /**
     * The connection error exception.
     *
     * @param error the error.
     */
    public ParadoxSyntaxErrorException(final SyntaxError error) {
        super(ExceptionUtils.message(error.getDescription()), SQL_STATE, Integer.parseInt(BASE_CODE + error.getCode()));
    }

    /**
     * The connection error exception.
     *
     * @param error    the error.
     * @param position the current scanner position.
     */
    public ParadoxSyntaxErrorException(final SyntaxError error, final ScannerPosition position) {
        super(ExceptionUtils.message(error.getDescription(), position), SQL_STATE,
                Integer.parseInt(BASE_CODE + error.getCode()));
    }

    /**
     * The connection error exception.
     *
     * @param error     the error.
     * @param position  the current scanner position.
     * @param parameter message parameters.
     */
    public ParadoxSyntaxErrorException(final SyntaxError error, final ScannerPosition position,
                                       final Object... parameter) {
        super(ExceptionUtils.message(error.getDescription(), position, parameter), SQL_STATE,
                Integer.parseInt(BASE_CODE + error.getCode()));
    }

    /**
     * The connection error exception.
     *
     * @param error     the error.
     * @param parameter message parameters.
     */
    public ParadoxSyntaxErrorException(final SyntaxError error, final Object... parameter) {
        super(ExceptionUtils.message(error.getDescription(), parameter), SQL_STATE,
                Integer.parseInt(BASE_CODE + error.getCode()));
    }

}
