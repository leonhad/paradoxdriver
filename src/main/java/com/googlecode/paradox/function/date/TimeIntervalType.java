package com.googlecode.paradox.function.date;

import com.googlecode.paradox.utils.Utils;

/**
 * Time interval values.
 *
 * @version 1.0
 * @since 1.6.0
 */
public enum TimeIntervalType {
    MILLISECOND,
    SECOND,
    MINUTE,
    HOUR,
    DAY,
    DAYOFYEAR,
    WEEK,
    MONTH,
    QUARTER,
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
