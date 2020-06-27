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
 * Parses a VARCHAR field.
 *
 * @author Leonardo Costa
 * @version 1.1
 * @since 1.3
 */
public final class BytesField implements FieldParser {

    private static final FieldValue NULL = new FieldValue(ParadoxFieldType.BYTES.getSQLType());

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean match(final int type) {
        return type == ParadoxFieldType.BYTES.getType();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public FieldValue parse(final ParadoxTable table, final ByteBuffer buffer, final ParadoxField field) {
        final ByteBuffer bytes = ByteBuffer.allocate(field.getSize());

        // Track for NULL values.
        boolean allZeroes = true;
        for (int chars = 0; chars < field.getSize(); chars++) {
            byte value = buffer.get();
            bytes.put(value);

            if (value != 0) {
                allZeroes = false;
            }
        }

        if (allZeroes) {
            return NULL;
        }

        return new FieldValue(bytes.array(), ParadoxFieldType.BYTES.getSQLType());
    }

}
