package com.googlecode.paradox.data.field;

import com.googlecode.paradox.data.FieldParser;
import com.googlecode.paradox.data.table.value.BlobDescriptor;
import com.googlecode.paradox.data.table.value.FieldValue;
import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.metadata.ParadoxTable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import static com.googlecode.paradox.utils.Utils.flip;

/**
 * LobField that acts as a superclass for Blob and Clob.
 *
 * @author Michael
 */
public abstract class LobField implements FieldParser {

    /**
     * {@inheritDoc}.
     */
    @Override
    public FieldValue parse(final ParadoxTable table, final ByteBuffer buffer, final ParadoxField field) {
        final ByteBuffer value = ByteBuffer.allocate(field.getSize());
        Arrays.fill(value.array(), (byte) 0);

        for (int chars = 0; chars < field.getSize(); chars++) {
            value.put(buffer.get());
        }
        flip(value);

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
