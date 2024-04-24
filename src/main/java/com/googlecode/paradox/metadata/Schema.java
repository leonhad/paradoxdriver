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

import com.googlecode.paradox.ConnectionInfo;

import java.sql.SQLException;
import java.util.List;

/**
 * Generic schema.
 *
 * @since 1.6.0
 */
public interface Schema {

    /**
     * List the schema tables.
     *
     * @param connectionInfo the connection information.
     * @param tablePattern   the table pattern.
     * @return the table list.
     * @throws SQLException in case of failures.
     */
    List<Table> list(final ConnectionInfo connectionInfo, final String tablePattern) throws SQLException;

    /**
     * Gets the schema name.
     *
     * @return the schema name
     */
    String name();

    /**
     * Gets the catalog name.
     *
     * @return the catalog name.
     */
    String catalogName();

    /**
     * Find a single table in schema.
     *
     * @param connectionInfo the connection information.
     * @param tableName      the table name.
     * @return the desired table or {@code null} if not found.
     * @throws SQLException in case of failure.
     */
    Table findTable(final ConnectionInfo connectionInfo, final String tableName) throws SQLException;
}
