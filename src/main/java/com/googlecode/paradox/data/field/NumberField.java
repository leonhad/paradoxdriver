/*
 * NumberField.java
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
import java.sql.Types;

/**
 * Parses the numeric fields.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.3
 */
public final class NumberField implements FieldParser {

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean match(final int type) {
        return type == 5 || type == 6;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public FieldValue parse(final ParadoxTable table, final ByteBuffer buffer, final ParadoxField field) {
        long value = buffer.getLong();
        //System.out.println("teste " + Long.toHexString(value));
        //System.out.println("teste " + Binary64ToDouble(value));
        
        return new FieldValue(Binary64ToDouble(value), Types.DOUBLE);
    }

    double Binary64ToDouble(long value) {
        long minus = -1, exponent;
        double fraction, result;

        if ((value & 0x8000000000000000l) == 0) {
            minus = 1;
        }
        exponent = ((value & 0x7FF0000000000000L) >> 52) - 1023;
        fraction = value & 0xFFFFFFFFFFFFFL + 0x10000000000000L;
        fraction = fraction / 0x10000000000000L;
        result = minus * fraction * Math.pow(2, exponent);
        return (result);
    }
}
