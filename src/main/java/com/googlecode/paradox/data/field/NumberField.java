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

/**
 * Parses the numeric fields.
 *
 * @author Leonardo Costa
 * @version 1.2
 * @since 1.3
 */
public final class NumberField implements FieldParser {

    private static final FieldValue NULL = new FieldValue(ParadoxFieldType.NUMBER.getSQLType());

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean match(final int type) {
        return (type == ParadoxFieldType.CURRENCY.getType()) || (type == ParadoxFieldType.NUMBER.getType());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public FieldValue parse(final ParadoxTable table, final ByteBuffer buffer, final ParadoxField field) {
        long value = buffer.getLong();
        if ((value & 0x8000_0000_0000_0000L) != 0) {
            value &= 0x7FFF_FFFF_FFFF_FFFFL;
        } else {
            value = ~value;
        }

        double v = Double.longBitsToDouble(value);

        if (Double.isNaN(v)) {
            return NULL;
        }
        return new FieldValue(v, ParadoxFieldType.NUMBER.getSQLType());
    }
}
