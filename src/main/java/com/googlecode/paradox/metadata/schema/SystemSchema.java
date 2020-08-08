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

package com.googlecode.paradox.metadata.schema;

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.metadata.Schema;
import com.googlecode.paradox.metadata.Table;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The information schema.
 *
 * @version 1.0
 * @since 1.6.0
 */
public class SystemSchema implements Schema {

    /**
     * The current tables.
     */
    private final List<Table> tables = new ArrayList<>();

    /**
     * The current catalog.
     */
    private final File catalog;

    /**
     * Creates a new instance.
     *
     * @param catalog the current catalog.
     */
    public SystemSchema(final File catalog) {
        this.catalog = catalog;
    }

    @Override
    public List<Table> list(final ConnectionInfo connectionInfo, final String tablePattern) {
        return tables;
    }

    @Override
    public String name() {
        return "information_schema";
    }

    @Override
    public String catalogName() {
        return catalog.getName();
    }

    @Override
    public Table findTable(ConnectionInfo connectionInfo, String tableName) {
        return null;
    }
}
