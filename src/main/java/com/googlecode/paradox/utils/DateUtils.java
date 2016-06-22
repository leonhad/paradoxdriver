package com.googlecode.paradox.utils;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

public final class DateUtils {

    private static final int SDN_OFFSET = 32045;
    private static final int DAYS_PER_5_MONTHS = 153;
    private static final int DAYS_PER_4_YEARS = 1461;
    private static final int DAYS_PER_400_YEARS = 146097;

    private DateUtils() {
        // Utility class, not instantiate.
    }
    
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

    public static long gregorianToSdn(final int inputYear, final int inputMonth, final int inputDay) {
        long year;
        long month;

        /* check for invalid dates */
        if (inputYear == 0 || inputYear < -4714 || inputMonth <= 0 || inputMonth > 12 || inputDay <= 0
                || inputDay > 31) {
            return 0;
        }

        /* check for dates before SDN 1 (Nov 25, 4714 B.C.) */
        if (inputYear == -4714) {
            if (inputMonth < 11) {
                return 0;
            }
            if (inputMonth == 11 && inputDay < 25) {
                return 0;
            }
        }

        /* Make year always a positive number. */
        if (inputYear < 0) {
            year = inputYear + 4801;
        } else {
            year = inputYear + 4800;
        }

        /* Adjust the start of the year. */
        if (inputMonth > 2) {
            month = inputMonth - 3;
        } else {
            month = inputMonth + 9;
            year--;
        }

        return year / 100 * DAYS_PER_400_YEARS / 4 + year % 100 * DAYS_PER_4_YEARS / 4
                + (month * DAYS_PER_5_MONTHS + 2) / 5 + inputDay - SDN_OFFSET;
    }
}
