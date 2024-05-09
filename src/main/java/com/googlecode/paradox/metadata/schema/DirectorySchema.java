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
package com.googlecode.paradox.metadata.schema;

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.data.TableData;
import com.googlecode.paradox.data.filefilters.TableFilter;
import com.googlecode.paradox.metadata.Schema;
import com.googlecode.paradox.metadata.Table;
import com.googlecode.paradox.metadata.View;
import com.googlecode.paradox.metadata.paradox.ParadoxTable;
import com.googlecode.paradox.utils.Utils;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    private static final List<Table> TABLE_CACHE = new ArrayList<>();

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
        ret.addAll(TableData.listTables(this, tablePattern, connectionInfo));
        ret.addAll(View.listViews(schemaFile, tablePattern, connectionInfo));
        updateCache(connectionInfo);
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
    public Table findTable(final ConnectionInfo connectionInfo, final String tableName) {
        final List<Table> tables = new ArrayList<>();
        tables.addAll(TableData.listTables(this, null, connectionInfo));
        tables.addAll(View.search(connectionInfo, name(), schemaFile));
        Table ret = tables.stream().filter(table -> tableName.equalsIgnoreCase(table.getName())).findFirst().orElse(null);
        updateCache(connectionInfo);
        return ret;
    }

    public File getSchemaFile() {
        return schemaFile;
    }

    private void updateCache(ConnectionInfo connectionInfo) {
        final File[] files = schemaFile.listFiles(new TableFilter(connectionInfo.getLocale(), null));
        if (files != null) {
            final List<String> fileList = Arrays.stream(files).map(file -> Utils.removeSuffix(file.getName(), "DB"))                    .collect(Collectors.toList());
            TABLE_CACHE.removeIf(t -> !fileList.contains(t.getName()));
        } else {
            TABLE_CACHE.clear();
        }
    }

    public Table getFromCache(File file) {
        String tableName = Utils.removeSuffix(file.getName(), "DB");
        Table cachedTable = TABLE_CACHE.stream().filter(table -> table.getName().equals(tableName)).findFirst().orElse(null);
        if (cachedTable != null && ((ParadoxTable) cachedTable).getTimestamp() >= file.lastModified()) {
            return cachedTable;
        }

        return null;
    }

    public void addCache(ParadoxTable table, long lastModified) {
        table.setTimestamp(lastModified);
        TABLE_CACHE.removeIf(t -> t.getName().equals(table.getName()));
        TABLE_CACHE.add(table);
    }
}
