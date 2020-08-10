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
import com.googlecode.paradox.data.filefilters.SQLFilter;
import com.googlecode.paradox.metadata.Schema;
import com.googlecode.paradox.metadata.Table;
import com.googlecode.paradox.metadata.View;
import com.googlecode.paradox.metadata.tables.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The information schema.
 *
 * @version 1.0
 * @since 1.6.0
 */
public class SystemSchema implements Schema {

    private static final Logger LOGGER = Logger.getLogger(SystemSchema.class.getName());

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

        tables.add(new CheckConstraints());
        tables.add(new ColumnDomainUsage());
        tables.add(new Schemata(connectionInfo, catalog));
        tables.add(new Tables(connectionInfo, catalog));
        tables.add(new ViewColumnUsage(connectionInfo, catalog));
        tables.add(new Views(connectionInfo, catalog));

        try {
            tables.add(load(connectionInfo, "check_constraints"));
            tables.add(load(connectionInfo, "column_domain_usage"));
            tables.add(load(connectionInfo, "schemata"));
            tables.add(load(connectionInfo, "tables"));
            tables.add(load(connectionInfo, "view_column_usage"));
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
            return load(connectionInfo, catalog, ConnectionInfo.INFORMATION_SCHEMA, name, is);
        }
    }

    public static List<View> search(final ConnectionInfo connectionInfo, final String catalog, final String schemaName,
                                    final String name, final File directory, final Locale locale) {
        final List<View> views = new ArrayList<>();

        if (directory.isDirectory()) {
            final File[] files = directory.listFiles(new SQLFilter(locale));
            if (files != null) {
                Arrays.stream(files).filter(Objects::nonNull).forEach((File file) -> {
                    try {
                        views.add(load(connectionInfo, catalog, schemaName, name, file));
                    } catch (final IOException e) {
                        LOGGER.log(Level.FINEST, e.getMessage(), e);
                    }
                });
            }
        }

        return views;
    }

    public static View load(final ConnectionInfo connectionInfo, final String catalog, final String schemaName,
                            final String name, final InputStream inputStream) throws IOException {
        final char[] buffer = new char[0x800];
        final StringBuilder out = new StringBuilder();
        try (final InputStreamReader in = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            int rsz;
            while ((rsz = in.read(buffer, 0, buffer.length)) > 0) {
                out.append(buffer, 0, rsz);
            }
        }

        return new View(connectionInfo, catalog, schemaName, name, out.toString());
    }

    public static View load(final ConnectionInfo connectionInfo, final String catalog, final String schemaName,
                            final String name, final File file) throws IOException {
        try (final FileInputStream fis = new FileInputStream(file)) {
            return load(connectionInfo, catalog, schemaName, name, fis);
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
