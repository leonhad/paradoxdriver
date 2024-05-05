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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;

/**
 * Parses the numeric fields.
 *
 * @since 1.3
 */
public final class NumberField implements FieldParser {

    /**
     * Creates a new instance.
     */
    public NumberField() {
        super();
    }

    @Override
    public boolean match(final ParadoxType type) {
        return (type == ParadoxType.CURRENCY) || (type == ParadoxType.NUMBER);
    }

    @Override
    public Double parse(final ParadoxTable table, final ByteBuffer buffer, final Field field) {
        long value = buffer.getLong();
        if ((value & 0x8000_0000_0000_0000L) != 0) {
            value &= 0x7FFF_FFFF_FFFF_FFFFL;
        } else {
            value = ~value;
        }

        double v = Double.longBitsToDouble(value);

        if (Double.isNaN(v)) {
            return null;
        }

        if (field.getType() == ParadoxType.CURRENCY) {
            final BigDecimal decimal = BigDecimal.valueOf(v);
            v = decimal.setScale(field.getPrecision(), RoundingMode.HALF_UP).doubleValue();
        }

        return v;
    }
}
