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
package com.googlecode.paradox.data.field;

import com.googlecode.paradox.data.FieldParser;
import com.googlecode.paradox.data.table.value.FieldValue;
import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.metadata.ParadoxTable;
import com.googlecode.paradox.results.ParadoxFieldType;

import java.nio.ByteBuffer;
import java.sql.Timestamp;

/**
 * Parses timestamp fields.
 *
 * @author Michael Berry
 */
public final class TimestampField implements FieldParser {

    private static final FieldValue NULL = new FieldValue(ParadoxFieldType.TIMESTAMP.getSQLType());
    private static final long MILLIS_UNTIL_1970 = 62_135_683_200_000L;

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean match(final int type) {
        return type == ParadoxFieldType.TIMESTAMP.getType();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public FieldValue parse(final ParadoxTable table, final ByteBuffer buffer, final ParadoxField field) {
        long rawValue = buffer.getLong();

        if (rawValue == 0) {
            return NULL;
        }

        if ((rawValue & 0x8000_0000_0000_0000L) != 0) {
            rawValue &= 0x7FFF_FFFF_FFFF_FFFFL;
        } else {
            rawValue = ~rawValue;
        }
        long value = (long) Double.longBitsToDouble(rawValue);

        return new FieldValue(new Timestamp(value - MILLIS_UNTIL_1970), ParadoxFieldType.TIMESTAMP.getSQLType());
    }
}
