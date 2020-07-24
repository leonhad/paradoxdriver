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
package com.googlecode.paradox.rowset;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.function.IntPredicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Compare Paradox values.
 *
 * @version 1.7
 * @since 1.6.0
 */
@SuppressWarnings({"java:S1142", "java:S3776", "java:S1541"})
public final class ValuesComparator {

    private static final Logger LOGGER = Logger.getLogger(ValuesComparator.class.getName());

    private static final double EPSILON = Double.MIN_VALUE * 8;

    /**
     * Creates a new instance.
     */
    private ValuesComparator() {
        super();
    }

    public static boolean compare(final Object o1, final Object o2, final IntPredicate condition) {
        if (o1 == null || o2 == null) {
            return false;
        }

        return condition.test(compare(o1, o2));
    }

    public static boolean equals(final Object o1, final Object o2) {
        if (o1 == null || o2 == null) {
            return false;
        }

        // Try to compare with Boolean values.
        if (o1 instanceof Boolean || o2 instanceof Boolean) {
            final Boolean n1 = ValuesConverter.getBoolean(o1);
            final Boolean n2 = ValuesConverter.getBoolean(o2);
            return n1.equals(n2);
        }

        // Try to compare with Byte values.
        if (o1 instanceof Byte || o2 instanceof Byte) {
            try {
                final Byte n1 = ValuesConverter.getByte(o1);
                final Byte n2 = ValuesConverter.getByte(o2);
                return n1.equals(n2);
            } catch (final NumberFormatException e) {
                LOGGER.log(Level.FINEST, e.getMessage(), e);
            }
        }

        // Try to compare with Integer values.
        if (o1 instanceof Integer || o2 instanceof Integer) {
            try {
                final Integer n1 = ValuesConverter.getInteger(o1);
                final Integer n2 = ValuesConverter.getInteger(o2);
                return n1.equals(n2);
            } catch (final NumberFormatException e) {
                LOGGER.log(Level.FINEST, e.getMessage(), e);
            }
        }

        // Try to compare with Long values.
        if (o1 instanceof Long || o2 instanceof Long) {
            try {
                final Long n1 = ValuesConverter.getLong(o1);
                final Long n2 = ValuesConverter.getLong(o2);
                return n1.equals(n2);
            } catch (final NumberFormatException e) {
                LOGGER.log(Level.FINEST, e.getMessage(), e);
            }
        }

        // Try to compare with Double values.
        if (o1 instanceof Double || o2 instanceof Double) {
            try {
                final double n1 = ValuesConverter.getDouble(o1);
                final double n2 = ValuesConverter.getDouble(o2);
                return Math.abs(n1 - n2) < EPSILON;
            } catch (final NumberFormatException e) {
                LOGGER.log(Level.FINEST, e.getMessage(), e);
            }
        }

        // Try to compare with BigDecimal values.
        if (o1 instanceof BigDecimal || o2 instanceof BigDecimal) {
            try {
                BigDecimal n1 = ValuesConverter.getBigDecimal(o1);
                BigDecimal n2 = ValuesConverter.getBigDecimal(o2);

                n1 = n1.setScale(Math.max(n1.scale(), n2.scale()), RoundingMode.UNNECESSARY);
                n2 = n2.setScale(Math.max(n1.scale(), n2.scale()), RoundingMode.UNNECESSARY);
                return n1.equals(n2);
            } catch (final NumberFormatException e) {
                LOGGER.log(Level.FINEST, e.getMessage(), e);
            }
        }

        // Try to compare with Time values.
        if (o1 instanceof Time || o2 instanceof Time) {
            try {
                final Time n1 = ValuesConverter.getTime(o1);
                final Time n2 = ValuesConverter.getTime(o2);
                return n1.equals(n2);
            } catch (final IllegalArgumentException e) {
                LOGGER.log(Level.FINEST, e.getMessage(), e);
            }
        }

        // Try to compare with Timestamp values.
        if (o1 instanceof Timestamp || o2 instanceof Timestamp) {
            try {
                final Timestamp n1 = ValuesConverter.getTimestamp(o1);
                final Timestamp n2 = ValuesConverter.getTimestamp(o2);
                return n1.equals(n2);
            } catch (final IllegalArgumentException e) {
                LOGGER.log(Level.FINEST, e.getMessage(), e);
            }
        }

        // Try to compare with Date values.
        if (o1 instanceof Date || o2 instanceof Date) {
            try {
                final Date n1 = ValuesConverter.getDate(o1);
                final Date n2 = ValuesConverter.getDate(o2);
                return n1.equals(n2);
            } catch (final IllegalArgumentException e) {
                LOGGER.log(Level.FINEST, e.getMessage(), e);
            }
        }

        // Try to compare with String values.
        if (o1 instanceof String || o2 instanceof String) {
            final String n1 = ValuesConverter.getString(o1);
            final String n2 = ValuesConverter.getString(o2);
            return n1.length() == n2.length() && n1.equals(n2);
        }

        // Try to compare with String values.
        if (o1 instanceof byte[] || o2 instanceof byte[]) {
            final byte[] n1 = ValuesConverter.getByteArray(o1);
            final byte[] n2 = ValuesConverter.getByteArray(o2);
            return Arrays.equals(n1, n2);
        }

        return false;
    }

    @SuppressWarnings("java:S138")
    public static int compare(final Object o1, final Object o2) {
        if (o1 == o2) {
            return 0;
        } else if (o1 == null) {
            return 1;
        } else if (o2 == null) {
            return -1;
        }

        // Try to compare with Boolean values.
        if (o1 instanceof Boolean || o2 instanceof Boolean) {
            final Boolean n1 = ValuesConverter.getBoolean(o1);
            final Boolean n2 = ValuesConverter.getBoolean(o2);
            return n1.compareTo(n2);
        }

        // Try to compare with Byte values.
        if (o1 instanceof Byte || o2 instanceof Byte) {
            try {
                final Byte n1 = ValuesConverter.getByte(o1);
                final Byte n2 = ValuesConverter.getByte(o2);
                return n1.compareTo(n2);
            } catch (final NumberFormatException e) {
                LOGGER.log(Level.FINEST, e.getMessage(), e);
            }
        }

        // Try to compare with Integer values.
        if (o1 instanceof Integer || o2 instanceof Integer) {
            try {
                final Integer n1 = ValuesConverter.getInteger(o1);
                final Integer n2 = ValuesConverter.getInteger(o2);
                return n1.compareTo(n2);
            } catch (final NumberFormatException e) {
                LOGGER.log(Level.FINEST, e.getMessage(), e);
            }
        }

        // Try to compare with Long values.
        if (o1 instanceof Long || o2 instanceof Long) {
            try {
                final Long n1 = ValuesConverter.getLong(o1);
                final Long n2 = ValuesConverter.getLong(o2);
                return n1.compareTo(n2);
            } catch (final NumberFormatException e) {
                LOGGER.log(Level.FINEST, e.getMessage(), e);
            }
        }

        // Try to compare with Double values.
        if (o1 instanceof Double || o2 instanceof Double) {
            try {
                final Double n1 = ValuesConverter.getDouble(o1);
                final Double n2 = ValuesConverter.getDouble(o2);
                return n1.compareTo(n2);
            } catch (final NumberFormatException e) {
                LOGGER.log(Level.FINEST, e.getMessage(), e);
            }
        }

        // Try to compare with BigDecimal values.
        if (o1 instanceof BigDecimal || o2 instanceof BigDecimal) {
            try {
                final BigDecimal n1 = ValuesConverter.getBigDecimal(o1);
                final BigDecimal n2 = ValuesConverter.getBigDecimal(o2);
                return n1.compareTo(n2);
            } catch (final NumberFormatException e) {
                LOGGER.log(Level.FINEST, e.getMessage(), e);
            }
        }

        // Try to compare with Time values.
        if (o1 instanceof Time || o2 instanceof Time) {
            try {
                final Time n1 = ValuesConverter.getTime(o1);
                final Time n2 = ValuesConverter.getTime(o2);
                return n1.compareTo(n2);
            } catch (final IllegalArgumentException e) {
                LOGGER.log(Level.FINEST, e.getMessage(), e);
            }
        }

        // Try to compare with Timestamp values.
        if (o1 instanceof Timestamp || o2 instanceof Timestamp) {
            try {
                final Timestamp n1 = ValuesConverter.getTimestamp(o1);
                final Timestamp n2 = ValuesConverter.getTimestamp(o2);
                return n1.compareTo(n2);
            } catch (final IllegalArgumentException e) {
                LOGGER.log(Level.FINEST, e.getMessage(), e);
            }
        }

        // Try to compare with Date values.
        if (o1 instanceof Date || o2 instanceof Date) {
            try {
                final Date n1 = ValuesConverter.getDate(o1);
                final Date n2 = ValuesConverter.getDate(o2);
                return n1.compareTo(n2);
            } catch (final IllegalArgumentException e) {
                LOGGER.log(Level.FINEST, e.getMessage(), e);
            }
        }

        // Try to compare with String values.
        if (o1 instanceof String || o2 instanceof String) {
            final String n1 = ValuesConverter.getString(o1);
            final String n2 = ValuesConverter.getString(o2);
            return n1.compareTo(n2);
        }

        // Try to compare with String values.
        if (o1 instanceof byte[] || o2 instanceof byte[]) {
            final byte[] n1 = ValuesConverter.getByteArray(o1);
            final byte[] n2 = ValuesConverter.getByteArray(o2);
            if (Arrays.equals(n1, n2)) {
                return 0;
            }

            return -1;
        }

        return -1;
    }
}
