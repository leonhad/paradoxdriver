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

    private static final FieldValue NULL = new FieldValue(Types.TIME);

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean match(final int type) {
        return type == ParadoxFieldType.TIME.getType();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public FieldValue parse(final ParadoxTable table, final ByteBuffer buffer, final ParadoxField field) {
        final long timeInMillis = buffer.getInt() & 0x0FFF_FFFFL;

        if (timeInMillis != 0) {
            final Calendar calendar = new GregorianCalendar(1, Calendar.JANUARY, 0);
            calendar.add(Calendar.MILLISECOND, (int) timeInMillis);
            final Time time = new Time(calendar.getTimeInMillis());
            return new FieldValue(time, Types.TIME);
        }
        
        return NULL;
    }
}
