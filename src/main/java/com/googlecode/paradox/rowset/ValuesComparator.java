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

import com.googlecode.paradox.ConnectionInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.function.IntPredicate;

/**
 * Compare Paradox values.
 *
 * @version 1.8
 * @since 1.6.0
 */
@SuppressWarnings({"java:S1142", "java:S3776", "java:S1541"})
public final class ValuesComparator {

    /**
     * Epsilon to use in double comparing.
     */
    private static final double EPSILON = Double.MIN_VALUE * 8;

    /**
     * Creates a new instance.
     */
    private ValuesComparator() {
        // Not used.
    }

    public static boolean compare(final Object o1, final Object o2, final IntPredicate condition,
                                  final ConnectionInfo connectionInfo) {
        if (o1 == null || o2 == null) {
            return false;
        }

        return condition.test(compare(o1, o2, connectionInfo));
    }

    @SuppressWarnings("java:S138")
    public static boolean equals(final Object o1, final Object o2, final ConnectionInfo connectionInfo) {
        if (o1 == null || o2 == null) {
            return false;
        }

        // Try to compare with Boolean values.
        if (o1 instanceof Boolean || o2 instanceof Boolean) {
            final Boolean n1 = ValuesConverter.getBoolean(o1, connectionInfo);
            final Boolean n2 = ValuesConverter.getBoolean(o2, connectionInfo);
            return n1.equals(n2);
        }

        // Try to compare with Byte values.
        if (o1 instanceof Byte || o2 instanceof Byte) {
            try {
                final Byte n1 = ValuesConverter.getByte(o1, connectionInfo);
                final Byte n2 = ValuesConverter.getByte(o2, connectionInfo);
                return n1.equals(n2);
            } catch (final NumberFormatException e) {
                connectionInfo.addWarning(e);
            }
        }

        // Try to compare with Double values.
        if (o1 instanceof Double || o2 instanceof Double) {
            try {
                final double n1 = ValuesConverter.getDouble(o1, connectionInfo);
                final double n2 = ValuesConverter.getDouble(o2, connectionInfo);
                return Math.abs(n1 - n2) < EPSILON;
            } catch (final NumberFormatException e) {
                connectionInfo.addWarning(e);
            }
        }

        // Try to compare with BigDecimal values.
        if (o1 instanceof BigDecimal || o2 instanceof BigDecimal) {
            try {
                BigDecimal n1 = ValuesConverter.getBigDecimal(o1, connectionInfo);
                BigDecimal n2 = ValuesConverter.getBigDecimal(o2, connectionInfo);

                n1 = n1.setScale(Math.max(n1.scale(), n2.scale()), RoundingMode.UNNECESSARY);
                n2 = n2.setScale(Math.max(n1.scale(), n2.scale()), RoundingMode.UNNECESSARY);
                return n1.equals(n2);
            } catch (final NumberFormatException e) {
                connectionInfo.addWarning(e);
            }
        }

        // Try to compare with Integer values.
        if (o1 instanceof Integer || o2 instanceof Integer) {
            try {
                final Integer n1 = ValuesConverter.getInteger(o1, connectionInfo);
                final Integer n2 = ValuesConverter.getInteger(o2, connectionInfo);
                return n1.equals(n2);
            } catch (final NumberFormatException e) {
                connectionInfo.addWarning(e);
            }
        }

        // Try to compare with Long values.
        if (o1 instanceof Long || o2 instanceof Long) {
            try {
                final Long n1 = ValuesConverter.getLong(o1, connectionInfo);
                final Long n2 = ValuesConverter.getLong(o2, connectionInfo);
                return n1.equals(n2);
            } catch (final NumberFormatException e) {
                connectionInfo.addWarning(e);
            }
        }

        // Try to compare with Time values.
        if (o1 instanceof Time || o2 instanceof Time) {
            try {
                final Time n1 = ValuesConverter.getTime(o1, connectionInfo);
                final Time n2 = ValuesConverter.getTime(o2, connectionInfo);
                return n1.equals(n2);
            } catch (final IllegalArgumentException e) {
                connectionInfo.addWarning(e);
            }
        }

        // Try to compare with Timestamp values.
        if (o1 instanceof Timestamp || o2 instanceof Timestamp) {
            try {
                final Timestamp n1 = ValuesConverter.getTimestamp(o1, connectionInfo);
                final Timestamp n2 = ValuesConverter.getTimestamp(o2, connectionInfo);
                return n1.equals(n2);
            } catch (final IllegalArgumentException e) {
                connectionInfo.addWarning(e);
            }
        }

        // Try to compare with Date values.
        if (o1 instanceof Date || o2 instanceof Date) {
            try {
                final Date n1 = ValuesConverter.getDate(o1, connectionInfo);
                final Date n2 = ValuesConverter.getDate(o2, connectionInfo);
                return n1.equals(n2);
            } catch (final IllegalArgumentException e) {
                connectionInfo.addWarning(e);
            }
        }

        // Try to compare with String values.
        if (o1 instanceof String || o2 instanceof String) {
            final String n1 = ValuesConverter.getString(o1, connectionInfo);
            final String n2 = ValuesConverter.getString(o2, connectionInfo);
            return n1.length() == n2.length() && n1.equals(n2);
        }

        // Try to compare with String values.
        if (o1 instanceof byte[] || o2 instanceof byte[]) {
            final byte[] n1 = ValuesConverter.getByteArray(o1, connectionInfo);
            final byte[] n2 = ValuesConverter.getByteArray(o2, connectionInfo);
            return Arrays.equals(n1, n2);
        }

        return false;
    }

    @SuppressWarnings("java:S138")
    public static int compare(final Object o1, final Object o2, final ConnectionInfo connectionInfo) {
        if (o1 == o2) {
            return 0;
        } else if (o1 == null) {
            return 1;
        } else if (o2 == null) {
            return -1;
        }

        // Try to compare with Boolean values.
        if (o1 instanceof Boolean || o2 instanceof Boolean) {
            final Boolean n1 = ValuesConverter.getBoolean(o1, connectionInfo);
            final Boolean n2 = ValuesConverter.getBoolean(o2, connectionInfo);
            return n1.compareTo(n2);
        }

        // Try to compare with Double values.
        if (o1 instanceof Double || o2 instanceof Double) {
            try {
                final Double n1 = ValuesConverter.getDouble(o1, connectionInfo);
                final Double n2 = ValuesConverter.getDouble(o2, connectionInfo);
                return n1.compareTo(n2);
            } catch (final NumberFormatException e) {
                connectionInfo.addWarning(e);
            }
        }

        // Try to compare with BigDecimal values.
        if (o1 instanceof BigDecimal || o2 instanceof BigDecimal) {
            try {
                final BigDecimal n1 = ValuesConverter.getBigDecimal(o1, connectionInfo);
                final BigDecimal n2 = ValuesConverter.getBigDecimal(o2, connectionInfo);
                return n1.compareTo(n2);
            } catch (final NumberFormatException e) {
                connectionInfo.addWarning(e);
            }
        }

        // Try to compare with Byte values.
        if (o1 instanceof Byte || o2 instanceof Byte) {
            try {
                final Byte n1 = ValuesConverter.getByte(o1, connectionInfo);
                final Byte n2 = ValuesConverter.getByte(o2, connectionInfo);
                return n1.compareTo(n2);
            } catch (final NumberFormatException e) {
                connectionInfo.addWarning(e);
            }
        }

        // Try to compare with Integer values.
        if (o1 instanceof Integer || o2 instanceof Integer) {
            try {
                final Integer n1 = ValuesConverter.getInteger(o1, connectionInfo);
                final Integer n2 = ValuesConverter.getInteger(o2, connectionInfo);
                return n1.compareTo(n2);
            } catch (final NumberFormatException e) {
                connectionInfo.addWarning(e);
            }
        }

        // Try to compare with Long values.
        if (o1 instanceof Long || o2 instanceof Long) {
            try {
                final Long n1 = ValuesConverter.getLong(o1, connectionInfo);
                final Long n2 = ValuesConverter.getLong(o2, connectionInfo);
                return n1.compareTo(n2);
            } catch (final NumberFormatException e) {
                connectionInfo.addWarning(e);
            }
        }

        // Try to compare with Time values.
        if (o1 instanceof Time || o2 instanceof Time) {
            try {
                final Time n1 = ValuesConverter.getTime(o1, connectionInfo);
                final Time n2 = ValuesConverter.getTime(o2, connectionInfo);
                return n1.compareTo(n2);
            } catch (final IllegalArgumentException e) {
                connectionInfo.addWarning(e);
            }
        }

        // Try to compare with Timestamp values.
        if (o1 instanceof Timestamp || o2 instanceof Timestamp) {
            try {
                final Timestamp n1 = ValuesConverter.getTimestamp(o1, connectionInfo);
                final Timestamp n2 = ValuesConverter.getTimestamp(o2, connectionInfo);
                return n1.compareTo(n2);
            } catch (final IllegalArgumentException e) {
                connectionInfo.addWarning(e);
            }
        }

        // Try to compare with Date values.
        if (o1 instanceof Date || o2 instanceof Date) {
            try {
                final Date n1 = ValuesConverter.getDate(o1, connectionInfo);
                final Date n2 = ValuesConverter.getDate(o2, connectionInfo);
                return n1.compareTo(n2);
            } catch (final IllegalArgumentException e) {
                connectionInfo.addWarning(e);
            }
        }

        // Try to compare with String values.
        if (o1 instanceof String || o2 instanceof String) {
            final String n1 = ValuesConverter.getString(o1, connectionInfo);
            final String n2 = ValuesConverter.getString(o2, connectionInfo);
            return n1.compareTo(n2);
        }

        // Try to compare with String values.
        if (o1 instanceof byte[] || o2 instanceof byte[]) {
            final byte[] n1 = ValuesConverter.getByteArray(o1, connectionInfo);
            final byte[] n2 = ValuesConverter.getByteArray(o2, connectionInfo);
            if (Arrays.equals(n1, n2)) {
                return 0;
            }

            return -1;
        }

        return -1;
    }
}
