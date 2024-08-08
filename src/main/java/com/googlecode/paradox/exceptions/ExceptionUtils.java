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

/**
 * Exception formatter utils.
 *
 * @version 1.0
 * @since 1.6.0
 */
public final class ExceptionUtils {
    /**
     * Utility class, not for use.
     */
    private ExceptionUtils() {
        // Not used.
    }

    /**
     * Format a exception message.
     *
     * @param message the exception message.
     * @return the formatted message.
     */
    static String message(final String message) {
        return message(message, null, null);
    }

    /**
     * Format a exception message.
     *
     * @param message    the exception message.
     * @param position   the current scanner position.
     * @param parameters the message parameters.
     * @return the formatted message.
     */
    static String message(final String message, final ScannerPosition position, final Object[] parameters) {
        String msg = message;
        if (parameters != null && parameters.length > 0) {
            msg = String.format(msg, parameters);
        }

        if (position != null) {
            msg = String.format("%s in line %s, column %s.", msg, position.getLine(), position.getColumn());
        } else {
            msg += ".";
        }

        return msg;
    }

    /**
     * Format a exception message.
     *
     * @param message    the exception message.
     * @param parameters the message parameters.
     * @return the formatted message.
     */
    static String message(final String message, final Object[] parameters) {
        return ExceptionUtils.message(message, null, parameters);
    }

    /**
     * Format a exception message.
     *
     * @param message  the exception message.
     * @param position the current scanner position.
     * @return the formatted message.
     */
    static String message(final String message, final ScannerPosition position) {
        return ExceptionUtils.message(message, position, null);
    }
}
