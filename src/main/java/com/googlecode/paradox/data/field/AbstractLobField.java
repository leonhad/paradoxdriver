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
import com.googlecode.paradox.data.ParadoxBuffer;
import com.googlecode.paradox.data.table.value.BlobDescriptor;
import com.googlecode.paradox.data.table.value.FieldValue;
import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.metadata.ParadoxTable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * LobField that acts as a superclass for Blob and Clob.
 *
 * @author Michael
 */
public abstract class AbstractLobField implements FieldParser {

    /**
     * {@inheritDoc}.
     */
    @Override
    public FieldValue parse(final ParadoxTable table, final ParadoxBuffer buffer, final ParadoxField field) {
        final ByteBuffer value = ByteBuffer.allocate(field.getSize());
        Arrays.fill(value.array(), (byte) 0);

        for (int chars = 0; chars < field.getSize(); chars++) {
            value.put(buffer.get());
        }

        value.flip();

        buffer.order(ByteOrder.LITTLE_ENDIAN);
        final long offset = buffer.getInt();
        // Length
        buffer.getInt();
        // Modifier
        buffer.getShort();
        buffer.order(ByteOrder.BIG_ENDIAN);

        final BlobDescriptor descriptor = getDescriptor(table);
        descriptor.setLeader(value.array());

        descriptor.setOffset(offset);

        return new FieldValue(descriptor, getFieldType());
    }

    public abstract BlobDescriptor getDescriptor(final ParadoxTable table);

    public abstract int getFieldType();
}
