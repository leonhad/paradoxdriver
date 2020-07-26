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

import java.sql.SQLException;

/**
 * Exception used internally to enable Stream API with exceptions.
 *
 * @version 1.0
 * @since 1.6.0
 */
public class InternalException extends RuntimeException {
    /**
     * Creates a new instance.
     *
     * @param cause the original exception.
     */
    public InternalException(final SQLException cause) {
        super(cause);
    }
}
