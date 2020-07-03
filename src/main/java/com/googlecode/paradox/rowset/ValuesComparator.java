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

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.IntPredicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Compare Paradox values.
 *
 * @version 1.4
 * @since 1.6.0
 */
public class ValuesComparator implements Comparator<Object>, Serializable {

    private static final Logger LOGGER = Logger.getLogger(ValuesComparator.class.getName());

    /**
     * Creates a new instance.
     */
    public ValuesComparator() {
        super();
    }

    public boolean compare(final Object o1, final Object o2, final IntPredicate condition) {
        if (o1 == null || o2 == null) {
            return false;
        }

        return condition.test(compare(o1, o2));
    }

    @Override
    public int compare(final Object o1, final Object o2) {
        if (o1 == null) {
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

        // Try to compare with Time values.
        if (o1 instanceof Time || o2 instanceof Time) {
            final Time n1 = ValuesConverter.getTime(o1);
            final Time n2 = ValuesConverter.getTime(o2);
            return n1.compareTo(n2);
        }

        // Try to compare with Timestamp values.
        if (o1 instanceof Timestamp || o2 instanceof Timestamp) {
            final Timestamp n1 = ValuesConverter.getTimestamp(o1);
            final Timestamp n2 = ValuesConverter.getTimestamp(o2);
            return n1.compareTo(n2);
        }

        // Try to compare with Date values.
        if (o1 instanceof Date || o2 instanceof Date) {
            final Date n1 = ValuesConverter.getDate(o1);
            final Date n2 = ValuesConverter.getDate(o2);
            return n1.compareTo(n2);
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
