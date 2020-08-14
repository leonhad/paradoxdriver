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
import com.googlecode.paradox.metadata.tables.*;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The information schema.
 *
 * @version 1.2
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
     * @param connectionInfo the connection information.
     * @param catalog        the catalog name.
     * @throws SQLException in case of failures.
     */
    public SystemSchema(final ConnectionInfo connectionInfo, final String catalog) throws SQLException {
        this.catalog = catalog;

        tables.add(new CheckConstraints(connectionInfo, catalog));
        tables.add(new ColumnPrivileges());
        tables.add(new Columns(connectionInfo, catalog));
        tables.add(new ColumnDomainUsage());
        tables.add(new KeyColumns(connectionInfo, catalog));
        tables.add(new Parameters(catalog));
        tables.add(new ReferentialConstraints());
        tables.add(new Routines(catalog));
        tables.add(new Schemata(connectionInfo, catalog));
        tables.add(new TableConstraints(connectionInfo, catalog));
        tables.add(new Tables(connectionInfo, catalog));
        tables.add(new ViewColumnUsage(connectionInfo, catalog));
        tables.add(new Views(connectionInfo, catalog));

        try {
            tables.add(load(connectionInfo, "check_constraints"));
            tables.add(load(connectionInfo, "column_domain_usage"));
            tables.add(load(connectionInfo, "column_privileges"));
            tables.add(load(connectionInfo, "columns"));
            tables.add(load(connectionInfo, "constraint_table_usage"));
            tables.add(load(connectionInfo, "key_column_usage"));
            tables.add(load(connectionInfo, "parameters"));
            tables.add(load(connectionInfo, "referential_constraints"));
            tables.add(load(connectionInfo, "routines"));
            tables.add(load(connectionInfo, "schemata"));
            tables.add(load(connectionInfo, "table_constraints"));
            tables.add(load(connectionInfo, "table_privileges"));
            tables.add(load(connectionInfo, "tables"));
            tables.add(load(connectionInfo, "view_column_usage"));
            tables.add(load(connectionInfo, "view_table_usage"));
            tables.add(load(connectionInfo, "views"));
        } catch (final IOException e) {
            throw new SQLException(e);
        }
    }

    /**
     * Loads a file view from the driver files.
     *
     * @param connectionInfo the connection information.
     * @param name           the view name.
     * @return the view.
     * @throws IOException in case of I/O exception.
     */
    private View load(final ConnectionInfo connectionInfo, final String name) throws IOException {
        try (final InputStream is = getClass().
                getResourceAsStream("/com/googlecode/paradox/information_schema/" + name + ".sql")) {
            return View.load(connectionInfo, ConnectionInfo.INFORMATION_SCHEMA, name, is);
        }
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
