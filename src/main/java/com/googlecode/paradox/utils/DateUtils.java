/*
 * DateUtils.java
 *
 * 03/12/2009
 * Copyright (C) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.utils;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Utility class for date formats.
 * 
 * @author Leonardo Alves da Costa
 * @since 1.0
 * @version 1.0
 */
public final class DateUtils {

    /**
     * Amount of days in four years.
     */
    private static final int DAYS_PER_4_YEARS = 1461;

    /**
     * Amount of days in 400 years.
     */
    private static final int DAYS_PER_400_YEARS = 146097;

    /**
     * Amount of days in 5 months.
     */
    private static final int DAYS_PER_5_MONTHS = 153;

    /**
     * Days offset in Paradox format.
     */
    private static final int SDN_OFFSET = 32045;

    /**
     * Utility class.
     */
    private DateUtils() {
        // Utility class, not for use.
    }

    /**
     * Convert the Gregorian date to Paradox format.
     * 
     * @param inputYear
     *            the year to convert.
     * @param inputMonth
     *            the month to convert.
     * @param inputDay
     *            the day to convert.
     * @return the Paradox date.
     */
    public static long gregorianToSdn(final long inputYear, final long inputMonth, final long inputDay) {
        long year;
        long month;

        // check for invalid year
        if (inputYear == 0 || inputYear < -4714 || inputMonth <= 0 || inputMonth > 12 || inputDay <= 0 || inputDay > 31) {
            return 0;
        }

        // Check for dates before SDN 1 (Nov 25, 4714 B.C.).
        if (inputYear == -4714) {
            if (inputMonth < 11) {
                return 0;
            }
            if (inputMonth == 11 && inputDay < 25) {
                return 0;
            }
        }

        // Make year always a positive number.
        if (inputYear < 0) {
            year = inputYear + 4801L;
        } else {
            year = inputYear + 4800L;
        }

        // Adjust the start of the year.
        if (inputMonth > 2) {
            month = inputMonth - 3L;
        } else {
            month = inputMonth + 9L;
            year--;
        }

        return year / 100 * DAYS_PER_400_YEARS / 4 + year % 100 * DAYS_PER_4_YEARS / 4 + (month * DAYS_PER_5_MONTHS + 2) / 5 + inputDay - SDN_OFFSET;
    }

    /**
     * Convert the Paradox date to Gregorian format.
     * 
     * @param sdn
     *            the Paradox date to convert.
     * @return the Java {@link Date}.
     */
    public static Date sdnToGregorian(final long sdn) {
        long century;
        long year;
        long month;
        long day;
        long temp;
        long dayOfYear;

        if (sdn <= 0) {
            return null;
        }

        temp = (sdn + SDN_OFFSET) * 4 - 1;

        /* Calculate the century (year/100). */
        century = temp / DAYS_PER_400_YEARS;

        /* Calculate the year and day of year (1 <= dayOfYear <= 366). */
        temp = temp % DAYS_PER_400_YEARS / 4 * 4 + 3;
        year = century * 100 + temp / DAYS_PER_4_YEARS;
        dayOfYear = temp % DAYS_PER_4_YEARS / 4 + 1;

        /* Calculate the month and day of month. */
        temp = dayOfYear * 5 - 3;
        month = temp / DAYS_PER_5_MONTHS;
        day = temp % DAYS_PER_5_MONTHS / 5 + 1;

        /* Convert to the normal beginning of the year. */
        if (month < 10) {
            month += 3;
        } else {
            year += 1;
            month -= 9;
        }

        /* Adjust to the B.C./A.D. type numbering. */
        year -= 4800;
        if (year <= 0) {
            year--;
        }
        final Calendar calendar = new GregorianCalendar((int) year, (int) month - 1, (int) day);
        return new java.sql.Date(calendar.getTime().getTime());
    }
}
