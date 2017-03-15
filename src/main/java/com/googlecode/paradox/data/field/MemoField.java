/*
 * MemoField.java 07/06/2016 Copyright (C) 2016 Leonardo Alves da Costa This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.data.field;

import com.googlecode.paradox.data.FieldParser;
import com.googlecode.paradox.data.table.value.ClobDescriptor;
import com.googlecode.paradox.data.table.value.FieldValue;
import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.metadata.ParadoxTable;
import com.googlecode.paradox.utils.Utils;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;

/**
 * Parses memo fields.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.3
 */
public final class MemoField implements FieldParser {
    
    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean match(final int type) {
        return type == 0xC;
    }
    
    /**
     * {@inheritDoc}.
     */
    @Override
    public FieldValue parse(final ParadoxTable table, final ByteBuffer buffer, final ParadoxField field)
            throws SQLException {
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
        
        final ClobDescriptor descriptor = new ClobDescriptor(table.getBlobTable());
        descriptor.setLeader(Utils.parseString(value, table.getCharset()));
        
        descriptor.setOffset(offset);
        
        return new FieldValue(descriptor, Types.CLOB);
    }
    
}
