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
import com.googlecode.paradox.data.TableData;
import com.googlecode.paradox.data.ViewData;
import com.googlecode.paradox.metadata.Schema;
import com.googlecode.paradox.metadata.Table;
import com.googlecode.paradox.metadata.View;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A directory schema.
 *
 * @version 1.1
 * @since 1.6.0
 */
public class DirectorySchema implements Schema {

    /**
     * The schema directory file.
     */
    private final File schemaFile;

    /**
     * Create a directory schema.
     *
     * @param schemaFile the schema directory file.
     */
    public DirectorySchema(final File schemaFile) {
        this.schemaFile = schemaFile;
    }

    @Override
    public List<Table> list(final ConnectionInfo connectionInfo, final String tablePattern) throws SQLException {
        final List<Table> ret = new ArrayList<>();
        ret.addAll(TableData.listTables(schemaFile, tablePattern, connectionInfo));
        ret.addAll(ViewData.listViews(schemaFile, tablePattern, connectionInfo));
        ret.addAll(View.listViews(schemaFile, tablePattern, connectionInfo));
        return ret;
    }

    @Override
    public String catalogName() {
        return schemaFile.getParentFile().getName();
    }

    @Override
    public String name() {
        return schemaFile.getName();
    }

    @Override
    public Table findTable(final ConnectionInfo connectionInfo, final String tableName) throws SQLException {
        final List<Table> tables = new ArrayList<>();
        tables.addAll(TableData.listTables(schemaFile, connectionInfo));
        tables.addAll(ViewData.listViews(schemaFile, connectionInfo));
        tables.addAll(View.search(connectionInfo, name(), schemaFile));
        return tables.stream().filter(table -> tableName.equalsIgnoreCase(table.getName())).findFirst().orElse(null);
    }
}
