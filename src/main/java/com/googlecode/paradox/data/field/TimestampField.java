/*
 * TimestampField.java 07/06/2016 Copyright (C) 2016 Leonardo Alves da Costa This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.data.field;

import com.googlecode.paradox.data.FieldParser;
import com.googlecode.paradox.data.table.value.FieldValue;
import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.metadata.ParadoxTable;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

/**
 * Parses timestamp fields.
 *
 * @author Michael Berry
 */
public final class TimestampField implements FieldParser {

    private static final long MILLIS_UNTIL_1970 = 62135683200000L;

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean match(final int type) {
        return type == 0x15;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public FieldValue parse(final ParadoxTable table, final ByteBuffer buffer, final ParadoxField field) {
        long rawValue = buffer.getLong();
        if ((rawValue >>> 63) == 1) {
            rawValue &= 0x7FFFFFFFFFFFFFFFL;
        } else {
            rawValue = ~rawValue;
        }
        long value = (long) Double.longBitsToDouble(rawValue);

        Date date = new Date();
        date.setTime(value - MILLIS_UNTIL_1970);
        return new FieldValue(new Timestamp(date.getTime()), Types.TIMESTAMP);
    }
}
