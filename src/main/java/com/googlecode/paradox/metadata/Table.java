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
package com.googlecode.paradox.metadata;

import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.List;

/**
 * A generic table.
 *
 * @version 1.0
 * @since 1.6.0
 */
public interface Table {

    /**
     * Gets the table name.
     *
     * @return the table name.
     */
    String getName();

    /**
     * Gets the row count.
     *
     * @return the row count.
     */
    int getRowCount();

    /**
     * Gets the table type.
     *
     * @return the table type.
     */
    TableType type();

    /**
     * Gets the write protected value.
     *
     * @return the write protected value.
     */
    boolean isWriteProtected();

    /**
     * Gets the field list.
     *
     * @return the field list.
     */
    Field[] getFields();

    /**
     * Gets the schema name.
     *
     * @return the schema name.
     */
    String getSchemaName();

    /**
     * Gets the charset.
     *
     * @return the charset.
     */
    Charset getCharset();

    /**
     * Loads the all table rows.
     *
     * @param fields the fields to load.
     * @return the all table rows filtered by {@code fields}.
     * @throws SQLException in case of failures.
     */
    List<Object[]> load(final Field[] fields) throws SQLException;

    /**
     * Gets the primary keys list.
     *
     * @return the primary keys list.
     */
    Field[] getPrimaryKeys();

    /**
     * Gets the table indexes.
     *
     * @return the table indexes.
     */
    Index[] getIndexes() throws SQLException;

    /**
     * Gets the file total blocks.
     *
     * @return the file total blocks.
     */
    int getTotalBlocks();
}
