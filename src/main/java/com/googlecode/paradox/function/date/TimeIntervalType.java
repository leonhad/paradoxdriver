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
package com.googlecode.paradox.function.date;

import com.googlecode.paradox.utils.Utils;

/**
 * Time interval values.
 *
 * @version 1.0
 * @since 1.6.0
 */
public enum TimeIntervalType {

    /**
     * Milliseconds.
     */
    MILLISECOND,

    /**
     * Seconds.
     */
    SECOND,

    /**
     * Minutes.
     */
    MINUTE,

    /**
     * Hours.
     */
    HOUR,

    /**
     * Day in 24h.
     */
    DAY,

    /**
     * Day of the year.
     */
    DAYOFYEAR,

    /**
     * Week number.
     */
    WEEK,

    /**
     * Month (1-12).
     */
    MONTH,

    /**
     * Quarter.
     */
    QUARTER,

    /**
     * Year.
     */
    YEAR;

    /**
     * Check for invalid trim type.
     *
     * @param value the value to check.
     * @return <code>true</code> if the {@code value} is a valid trim type.
     */
    public static boolean isValidType(final String value) {
        return Utils.searchEnum(TimeIntervalType.class, value) != null;
    }
}
