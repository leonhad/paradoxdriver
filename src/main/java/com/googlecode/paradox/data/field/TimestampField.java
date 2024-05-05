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
package com.googlecode.paradox.data.field;

import com.googlecode.paradox.data.FieldParser;
import com.googlecode.paradox.metadata.Field;
import com.googlecode.paradox.metadata.paradox.ParadoxTable;
import com.googlecode.paradox.results.ParadoxType;

import java.nio.ByteBuffer;
import java.sql.Timestamp;

/**
 * Parses time stamp fields.
 *
 * @since 1.2
 */
public final class TimestampField implements FieldParser {

    private static final long MILLIS_UNTIL_1970 = 62_135_683_200_000L;

    /**
     * Creates a new instance.
     */
    public TimestampField() {
        super();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean match(final ParadoxType type) {
        return type == ParadoxType.TIMESTAMP;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object parse(final ParadoxTable table, final ByteBuffer buffer, final Field field) {
        long rawValue = buffer.getLong();

        if (rawValue == 0) {
            return null;
        }

        if ((rawValue & 0x8000_0000_0000_0000L) != 0) {
            rawValue &= 0x7FFF_FFFF_FFFF_FFFFL;
        } else {
            rawValue = ~rawValue;
        }
        long value = (long) Double.longBitsToDouble(rawValue);

        return new Timestamp(value - MILLIS_UNTIL_1970);
    }
}
