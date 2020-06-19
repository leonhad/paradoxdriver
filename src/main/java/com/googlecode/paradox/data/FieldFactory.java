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
package com.googlecode.paradox.data;

import com.googlecode.paradox.data.field.*;
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
public final class FieldFactory {

    /**
     * Stores all available parsers.
     */
    private static final List<FieldParser> ALL_PARSES = new ArrayList<>();

    // Initialize the parsers.
    static {
        FieldFactory.ALL_PARSES.add(new AutoIncrementField());
        FieldFactory.ALL_PARSES.add(new BooleanField());
        FieldFactory.ALL_PARSES.add(new BlobField());
        FieldFactory.ALL_PARSES.add(new DateField());
        FieldFactory.ALL_PARSES.add(new IntegerField());
        FieldFactory.ALL_PARSES.add(new LongField());
        FieldFactory.ALL_PARSES.add(new MemoField());
        FieldFactory.ALL_PARSES.add(new NumberField());
        FieldFactory.ALL_PARSES.add(new TimeField());
        FieldFactory.ALL_PARSES.add(new TimestampField());
        FieldFactory.ALL_PARSES.add(new VarcharField());
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
     * @param table  the paradox tables.
     * @param buffer the buffer to read of.
     * @param field  the paradox field.
     * @return the parsed value.
     * @throws SQLException in case of parse errors.
     */
    public static FieldValue parse(final ParadoxTable table, final ByteBuffer buffer, final ParadoxField field)
            throws SQLException {
        for (final FieldParser parser : FieldFactory.ALL_PARSES) {
            if (parser.match(field.getType())) {
                return parser.parse(table, buffer, field);
            }
        }
        throw new SQLException("Field type unsupported.", SQLStates.TYPE_NOT_FOUND.getValue());
    }
}
