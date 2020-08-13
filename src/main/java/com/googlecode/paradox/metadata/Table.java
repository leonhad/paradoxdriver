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
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

/**
 * A generic table.
 *
 * @version 1.1
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
     * @throws SQLException in case of failures.
     */
    int getRowCount() throws SQLException;

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
    default boolean isWriteProtected() {
        return true;
    }

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
    default Charset getCharset() {
        return StandardCharsets.UTF_8;
    }

    /**
     * Loads the all table rows.
     *
     * @param fields the fields to load.
     * @return the all table rows filtered by {@code fields}.
     * @throws SQLException in case of failures.
     */
    List<Object[]> load(final Field[] fields) throws SQLException;

    /**
     * Gets the primary key index.
     *
     * @return the primary key index or {@code null} if there is no index.
     * @throws SQLException in case of failures.
     */
    default Index getPrimaryKeyIndex() throws SQLException {
        return null;
    }

    /**
     * Gets the table indexes.
     *
     * @return the table indexes.
     * @throws SQLException in case of failures.
     */
    default Index[] getIndexes() throws SQLException {
        return new Index[0];
    }

    /**
     * Gets if this table is encrypted.
     *
     * @return {@code true} if this table is encrypted.
     */
    default boolean isEncrypted() {
        return false;
    }

    /**
     * Gets the autoincrement value.
     *
     * @return the autoincrement value.
     */
    default int getAutoIncrementValue() {
        return 0;
    }
}
