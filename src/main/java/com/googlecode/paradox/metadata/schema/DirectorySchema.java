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
import com.googlecode.paradox.metadata.paradox.ParadoxTable;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DirectorySchema implements Schema {

    private final File schemaFile;

    public DirectorySchema(final File schemaFile) {
        this.schemaFile = schemaFile;
    }

    @Override
    public List<Table> list(final ConnectionInfo connectionInfo, final String tablePattern) throws SQLException {
        final List<Table> ret = new ArrayList<>();
        ret.addAll(TableData.listTables(schemaFile, tablePattern, connectionInfo));
        ret.addAll(ViewData.listViews(schemaFile, tablePattern, connectionInfo));
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
        for (final Table table : TableData.listTables(schemaFile, connectionInfo)) {
            if (tableName.equalsIgnoreCase(table.getName())) {
                return table;
            }
        }

        for (final Table table : ViewData.listViews(schemaFile, connectionInfo)) {
            if (tableName.equalsIgnoreCase(table.getName())) {
                return table;
            }
        }

        return null;
    }
}
