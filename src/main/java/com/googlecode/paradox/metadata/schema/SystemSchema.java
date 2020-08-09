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
import com.googlecode.paradox.metadata.View;
import com.googlecode.paradox.metadata.tables.Tables;
import com.googlecode.paradox.metadata.views.*;

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
     * The catalog name.
     */
    private final String catalog;

    /**
     * Creates a new instance.
     *
     * @param catalog the catalog name.
     */
    public SystemSchema(final ConnectionInfo connectionInfo, final String catalog) {
        this.catalog = catalog;

        // Tables
        tables.add(new Tables(connectionInfo, catalog));

        // Views
        tables.add(new CheckConstraintsView());
        tables.add(new ColumnDomainUsageView());
        tables.add(new SchemataView(connectionInfo, catalog));

        // FIXME use case for BASE TABLE instead of TABLE.
        tables.add(new View(connectionInfo, catalog,
                "select catalog as table_catalog, schema as table_schema, name as table_name," +
                        " type_name as table_type\n" +
                        "from information_schema.pdx_tables\n" +
                        "order by catalog, schema, name, type_name",
                "tables", ConnectionInfo.INFORMATION_SCHEMA));
        tables.add(new ViewColumnUsageView(connectionInfo, catalog));
        tables.add(new ViewsView(connectionInfo, catalog));
    }

    @Override
    public List<Table> list(final ConnectionInfo connectionInfo, final String tablePattern) {
        return tables;
    }

    @Override
    public String name() {
        return ConnectionInfo.INFORMATION_SCHEMA;
    }

    @Override
    public String catalogName() {
        return catalog;
    }

    @Override
    public Table findTable(final ConnectionInfo connectionInfo, final String tableName) {
        return tables.stream()
                .filter(t -> t.getName().equalsIgnoreCase(tableName))
                .findFirst().orElse(null);
    }
}
