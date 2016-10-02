/*
 * TimeField.java
 *
 * 07/06/2016
 * Copyright (C) 2016 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.data.field;

import com.googlecode.paradox.data.FieldParser;
import com.googlecode.paradox.data.table.value.FieldValue;
import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.metadata.ParadoxTable;

import java.nio.ByteBuffer;
import java.sql.Time;
import java.sql.Types;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Parses time fields.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.3
 */
public final class TimeField implements FieldParser {

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean match(final int type) {
        return type == 0x14;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public FieldValue parse(final ParadoxTable table, final ByteBuffer buffer, final ParadoxField field) {
        final int a1 = buffer.get();
        final int a2 = buffer.get();
        final int a3 = buffer.get();
        final int a4 = buffer.get();
        final long timeInMillis = a1 << 24 | a2 << 16 | a3 << 8 | a4;

        if (timeInMillis != 0) {
            final Calendar calendar = new GregorianCalendar(1, 0, 0);
            calendar.add(Calendar.MILLISECOND, (int) timeInMillis);
            final Time time = new Time(calendar.getTimeInMillis());
            return new FieldValue(time, Types.TIME);
        }
        return new FieldValue(Types.TIME);
    }
}
