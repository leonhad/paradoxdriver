/*
 * FieldFactory.java
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
package com.googlecode.paradox.data;

import com.googlecode.paradox.data.field.AutoIncrementField;
import com.googlecode.paradox.data.field.BooleanField;
import com.googlecode.paradox.data.field.DateField;
import com.googlecode.paradox.data.field.IntegerField;
import com.googlecode.paradox.data.field.LongField;
import com.googlecode.paradox.data.field.MemoField;
import com.googlecode.paradox.data.field.NumberField;
import com.googlecode.paradox.data.field.TimeField;
import com.googlecode.paradox.data.field.VarcharField;
import com.googlecode.paradox.data.table.value.FieldValue;
import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.metadata.ParadoxTable;
import com.googlecode.paradox.utils.SQLStates;

import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles field parsers.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.3
 */
final class FieldFactory {

    /**
     * Stores all available parsers.
     */
    private static final List<FieldParser> ALL_PARSES = new ArrayList<>();

    // Initialize the parsers.
    static {
        ALL_PARSES.add(new AutoIncrementField());
        ALL_PARSES.add(new BooleanField());
        ALL_PARSES.add(new DateField());
        ALL_PARSES.add(new IntegerField());
        ALL_PARSES.add(new LongField());
        ALL_PARSES.add(new MemoField());
        ALL_PARSES.add(new NumberField());
        ALL_PARSES.add(new TimeField());
        ALL_PARSES.add(new VarcharField());
    }

    /**
     * Utility class.
     */
    private FieldFactory() {
        // Utility class.
    }

    /**
     * Parses the filter;
     *
     * @param table
     *         the paradox tables.
     * @param buffer
     *         the buffer to read of.
     * @param field
     *         the paradox field.
     * @return the parsed value.
     * @throws SQLException
     *         in case of parse errors.
     */
    public static FieldValue parse(final ParadoxTable table, final ByteBuffer buffer, final ParadoxField field)
            throws SQLException {
        for (final FieldParser parser : ALL_PARSES) {
            if (parser.match(field.getType())) {
                return parser.parse(table, buffer, field);
            }
        }
        throw new SQLException("Field type unsupported.", SQLStates.TYPE_NOT_FOUND.getValue());
    }
}
