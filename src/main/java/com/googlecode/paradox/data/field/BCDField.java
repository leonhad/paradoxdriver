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
import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.metadata.ParadoxTable;
import com.googlecode.paradox.results.ParadoxFieldType;

import java.nio.ByteBuffer;

/**
 * Parses boolean fields.
 *
 * @author Leonardo Alves da Costa
 * @version 1.1
 * @since 1.3
 */
public final class BCDField implements FieldParser {

    public static final int BCD_SIZE = 17;
    public static final int MAX_DIGITS = 32;
    public static final byte SECOND_BYTE = 4;

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean match(final int type) {
        return type == ParadoxFieldType.BCD.getType();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object parse(final ParadoxTable table, final ByteBuffer buffer, final ParadoxField field) {
        final byte[] valueBuffer = new byte[BCD_SIZE];

        System.arraycopy(buffer.array(), buffer.position(), valueBuffer, 0, valueBuffer.length);
        buffer.position(buffer.position() + valueBuffer.length);

        if (valueBuffer[0] == 0) {
            return null;
        }

        boolean negative = (valueBuffer[0] & 0x80) == 0;
        if (negative) {
            for (int i = 1; i < valueBuffer.length; i++) {
                valueBuffer[i] ^= 0xFF;
            }
        }

        final StringBuilder sb = new StringBuilder();
        for (int i = 1; i < valueBuffer.length; i++) {
            byte high = (byte) (valueBuffer[i] & 0xF0);
            high >>>= SECOND_BYTE;
            high = (byte) (high & 0x0f);
            byte low = (byte) (valueBuffer[i] & 0x0F);

            sb.append(high);
            sb.append(low);
        }

        int realSize = field.getRealSize();
        if (realSize == MAX_DIGITS) {
            sb.insert(0, "0.");
        } else {
            sb.insert(sb.length() - realSize, '.');
            if (realSize == 0) {
                sb.append('0');
            }
            removeLeadingZeroes(sb);
        }

        if (negative) {
            sb.insert(0, '-');
        }
        return Double.parseDouble(sb.toString());
    }

    private static void removeLeadingZeroes(final StringBuilder builder) {
        while (builder.length() > 1 && builder.charAt(0) == '0') {
            builder.deleteCharAt(0);
        }
    }
}
