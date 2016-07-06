/*
 * DateField.java
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
import com.googlecode.paradox.utils.DateUtils;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.sql.Types;

/**
 * Parses date fields.
 *
 * @author Leonardo Alves da Costa
 * @since 1.3
 * @version 1.0
 */
public class DateField implements FieldParser {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean match(final int type) {
        return type == 2;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public FieldValue parse(final ParadoxTable table, final ByteBuffer buffer, final ParadoxField field) {
        final int a1 = 0x000000FF & buffer.get();
        final int a2 = 0x000000FF & buffer.get();
        final int a3 = 0x000000FF & buffer.get();
        final int a4 = 0x000000FF & buffer.get();
        final long days = (a1 << 24 | a2 << 16 | a3 << 8 | a4) & 0x0FFFFFFFL;
        
        if ((a1 & 0xB0) != 0) {
            final Date date = DateUtils.sdnToGregorian(days + 1721425);
            return new FieldValue(date, Types.DATE);
        }
        return new FieldValue(Types.DATE);
    }
}
