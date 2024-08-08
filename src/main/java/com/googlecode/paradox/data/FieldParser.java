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
package com.googlecode.paradox.data;

import com.googlecode.paradox.metadata.Field;
import com.googlecode.paradox.metadata.paradox.ParadoxTable;
import com.googlecode.paradox.results.ParadoxType;

import java.nio.ByteBuffer;
import java.sql.SQLException;

/**
 * Parses a database field.
 *
 * @version 1.2
 * @since 1.3
 */
public interface FieldParser {

    /**
     * If this parses matches with the paradox field type.
     *
     * @param type the field type.
     * @return true if I can handle this type.
     */
    boolean match(final ParadoxType type);

    /**
     * Parses the field.
     *
     * @param table  the table.
     * @param buffer the buffer to read of.
     * @param field  the  field.
     * @return the parsed value.
     * @throws SQLException in case of parse errors.
     */
    Object parse(final ParadoxTable table, final ByteBuffer buffer, final Field field) throws SQLException;
}
