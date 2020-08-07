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
import com.googlecode.paradox.metadata.Field;
import com.googlecode.paradox.metadata.paradox.ParadoxTable;
import com.googlecode.paradox.results.ParadoxType;

import java.nio.ByteBuffer;

/**
 * Parses integer fields.
 *
 * @version 1.5
 * @since 1.3
 */
public final class IntegerField implements FieldParser {

    private static final int NULL_VALUE = -32768;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean match(final ParadoxType type) {
        return type == ParadoxType.INTEGER;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Integer (2 bytes) fields are stored as two's complement with the high bit
     * inverted.
     */
    @Override
    public Object parse(final ParadoxTable table, final ByteBuffer buffer, final Field field) {
        final int v = (short) (buffer.getShort() ^ 0x8000);

        if (v == NULL_VALUE) {
            return null;
        }

        return v;
    }
}
