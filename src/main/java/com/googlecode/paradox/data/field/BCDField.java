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
import com.googlecode.paradox.metadata.paradox.ParadoxField;
import com.googlecode.paradox.metadata.paradox.ParadoxTable;
import com.googlecode.paradox.results.ParadoxType;

import java.math.BigDecimal;
import java.nio.ByteBuffer;

/**
 * Parses boolean fields.
 *
 * @since 1.3
 */
public final class BCDField implements FieldParser {

    /**
     * BCD real size.
     */
    public static final int BCD_SIZE = 17;
    /**
     * Max digits a BCD can have.
     */
    public static final int MAX_DIGITS = 32;
    /**
     * Second position in byte.
     */
    private static final byte SECOND_BYTE = 4;

    /**
     * Creates a new instance.
     */
    public BCDField() {
        super();
    }

    private static void removeLeadingZeroes(final StringBuilder builder) {
        while (builder.length() > 1 && builder.charAt(0) == '0') {
            builder.deleteCharAt(0);
        }
    }

    @Override
    public boolean match(final ParadoxType type) {
        return type == ParadoxType.BCD;
    }

    @Override
    public Object parse(final ParadoxTable table, final ByteBuffer buffer, final Field originalField) {
        final byte[] valueBuffer = new byte[BCD_SIZE];

        System.arraycopy(buffer.array(), buffer.position(), valueBuffer, 0, valueBuffer.length);
        buffer.position(buffer.position() + valueBuffer.length);

        if (valueBuffer[0] == 0) {
            return null;
        }

        boolean negative = (valueBuffer[0] & 0x80) == 0;
        if (negative) {
            for (int i = 1; i < valueBuffer.length; i++) {
                valueBuffer[i] ^= (byte) 0xFF;
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

        final ParadoxField field = (ParadoxField) originalField;
        final int precision = field.getPrecision();
        if (precision == MAX_DIGITS) {
            sb.insert(0, "0.");
        } else {
            sb.insert(sb.length() - precision, '.');
            if (precision == 0) {
                sb.append('0');
            }
            removeLeadingZeroes(sb);
        }

        if (negative) {
            sb.insert(0, '-');
        }

        if (table.getConnectionInfo().isBcdRounding()) {
            return Double.valueOf(sb.toString());
        }

        return new BigDecimal(sb.toString());
    }
}
