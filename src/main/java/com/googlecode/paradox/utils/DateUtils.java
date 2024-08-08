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
package com.googlecode.paradox.utils;

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.exceptions.DataError;
import com.googlecode.paradox.exceptions.ParadoxDataException;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Utility class for date formats.
 *
 * @version 1.2
 * @since 1.0
 */
public final class DateUtils {

    /**
     * Amount of days in four years.
     */
    private static final int DAYS_PER_4_YEARS = 1461;

    /**
     * Amount of days in 400 years.
     */
    private static final int DAYS_PER_400_YEARS = 146_097;

    /**
     * Amount of days in 5 months.
     */
    private static final int DAYS_PER_5_MONTHS = 153;

    /**
     * Days offset in Paradox format.
     */
    private static final int SDN_OFFSET = 32_045;

    /**
     * Utility class.
     */
    private DateUtils() {
        // Utility class, not for use.
    }

    /**
     * Convert the Gregorian date to Paradox format.
     *
     * @param inputYear      the year to convert.
     * @param inputMonth     the month to convert.
     * @param inputDay       the day to convert.
     * @param connectionInfo the connection information.
     * @return the Paradox date.
     */
    public static long gregorianToSdn(final long inputYear, final long inputMonth, final long inputDay,
                                      final ConnectionInfo connectionInfo) {
        try {
            DateUtils.checkForDateBoundaries(inputYear, inputMonth, inputDay);
            DateUtils.checkYearBounds(inputYear, inputMonth, inputDay);
        } catch (final ParadoxDataException e) {
            connectionInfo.addWarning(e);
            return 0;
        }

        // Make year always a positive number.
        long year = DateUtils.fixYearBounds(inputYear);

        long month;
        // Adjust the start of the year.
        if (inputMonth > 2) {
            month = inputMonth - 3L;
        } else {
            month = inputMonth + 9L;
            year--;
        }

        return ((((year / 100) * DateUtils.DAYS_PER_400_YEARS) / 4) + (((year % 100) * DateUtils.DAYS_PER_4_YEARS) / 4)
                + (((month * DateUtils.DAYS_PER_5_MONTHS) + 2) / 5) + inputDay) - DateUtils.SDN_OFFSET;
    }

    /**
     * Convert the Paradox date to Gregorian format.
     *
     * @param sdn the Paradox date to convert.
     * @return the Java {@link Date}.
     */
    public static Date sdnToGregorian(final long sdn) {

        if (sdn <= 0) {
            return null;
        }

        long temp = ((sdn + DateUtils.SDN_OFFSET) * 4) - 1;

        /* Calculate the century (year/100). */
        final long century = temp / DateUtils.DAYS_PER_400_YEARS;

        /* Calculate the year and day of year (1 <= dayOfYear <= 366). */
        temp = (((temp % DateUtils.DAYS_PER_400_YEARS) / 4) * 4) + 3;
        long year = (century * 100) + (temp / DateUtils.DAYS_PER_4_YEARS);
        final long dayOfYear = ((temp % DateUtils.DAYS_PER_4_YEARS) / 4) + 1;

        /* Calculate the month and day of month. */
        temp = (dayOfYear * 5) - 3;
        long month = temp / DateUtils.DAYS_PER_5_MONTHS;
        final long day = ((temp % DateUtils.DAYS_PER_5_MONTHS) / 5) + 1;

        /* Convert to the normal beginning of the year. */
        if (month < 10) {
            month += 3;
        } else {
            year += 1;
            month -= 9;
        }

        /* Adjust to the B.C./A.D. type numbering. */
        year -= 4_800;
        if (year <= 0) {
            year--;
        }

        final Calendar calendar = new GregorianCalendar((int) year, (int) month - 1, (int) day);
        return new Date(calendar.getTime().getTime());
    }

    /**
     * Check for valid date boundaries.
     *
     * @param inputYear  the year to validate.
     * @param inputMonth the month to validate.
     * @param inputDay   the day to validate.
     * @throws ParadoxDataException in case of invalid date.z
     */
    private static void checkForDateBoundaries(final long inputYear, final long inputMonth, final long inputDay)
            throws ParadoxDataException {
        // Check for invalid year.
        if ((inputYear == 0) || (inputYear < -4_714)) {
            throw new ParadoxDataException(DataError.INVALID_DATE);
        }

        // Check for invalid month.
        if ((inputMonth <= 0) || (inputMonth > 12)) {
            throw new ParadoxDataException(DataError.INVALID_DATE);
        }

        // Check for invalid day.
        if ((inputDay <= 0) || (inputDay > 31)) {
            throw new ParadoxDataException(DataError.INVALID_DATE);
        }
    }

    /**
     * Check for dates before SDN 1 (November 25, 4714 B.C.).
     *
     * @param inputYear  the year to check.
     * @param inputMonth the month to check.
     * @param inputDay   the day to check.
     * @throws ParadoxDataException in case of invalid date.
     */
    private static void checkYearBounds(final long inputYear, final long inputMonth, final long inputDay)
            throws ParadoxDataException {
        if (inputYear == -4_714) {
            if (inputMonth < 11) {
                throw new ParadoxDataException(DataError.INVALID_DATE);
            }
            if ((inputMonth == 11) && (inputDay < 25)) {
                throw new ParadoxDataException(DataError.INVALID_DATE);
            }
        }
    }

    /**
     * Check for the year bounds.
     *
     * @param inputYear the year to test of.
     * @return the corrected year.
     */
    private static long fixYearBounds(final long inputYear) {
        long year;
        if (inputYear < 0) {
            year = inputYear + 4_801L;
        } else {
            year = inputYear + 4_800L;
        }
        return year;
    }
}
