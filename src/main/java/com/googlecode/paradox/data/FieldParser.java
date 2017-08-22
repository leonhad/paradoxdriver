/*
 * FieldParser.java 07/06/2016 Copyright (C) 2016 Leonardo Alves da Costa This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.data;

import com.googlecode.paradox.data.table.value.FieldValue;
import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.metadata.ParadoxTable;
import java.nio.ByteBuffer;
import java.sql.SQLException;

/**
 * Parses a database field.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.3
 */
public interface FieldParser {
    
    /**
     * If this parses matches with the paradox field type.
     *
     * @param type
     *            the field type.
     * @return true if I can handle this type.
     */
    boolean match(int type);
    
    /**
     * Parses the field.
     *
     * @param table
     *            the paradox tables.
     * @param buffer
     *            the buffer to read of.
     * @param field
     *            the paradox field.
     * @return the parsed value.
     * @throws SQLException
     *             in case of parse errors.
     */
    FieldValue parse(final ParadoxTable table, final ByteBuffer buffer, final ParadoxField field) throws SQLException;
}
