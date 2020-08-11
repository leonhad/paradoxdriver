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
import com.googlecode.paradox.data.filefilters.SQLFilter;
import com.googlecode.paradox.exceptions.ParadoxNotSupportedException;
import com.googlecode.paradox.parser.SQLParser;
import com.googlecode.paradox.planner.Planner;
import com.googlecode.paradox.planner.context.SelectContext;
import com.googlecode.paradox.planner.nodes.PlanTableNode;
import com.googlecode.paradox.planner.plan.Plan;
import com.googlecode.paradox.planner.plan.SelectPlan;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.utils.Expressions;
import com.googlecode.paradox.utils.Utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * View support.
 *
 * @version 1.1
 * @since 1.6.0
 */
public class View implements Table {

    private static final Logger LOGGER = Logger.getLogger(View.class.getName());

    /**
     * The current catalog.
     */
    protected final String catalogName;

    /**
     * The connection information.
     */
    protected final ConnectionInfo connectionInfo;

    /**
     * View definition.
     */
    protected final String definition;

    /**
     * View name.
     */
    protected final String name;

    /**
     * The schema name.
     */
    private final String schemaName;

    /**
     * Select plan.
     */
    private SelectPlan selectPlan;

    /**
     * View fields.
     */
    private Field[] fields;

    /**
     * Creates a new instance.
     *
     * @param connectionInfo the connection information.
     * @param catalogName    the catalog name.
     * @param schemaName     the schema name.
     * @param name           the view name.
     * @param definition     the view definition.
     */
    public View(final ConnectionInfo connectionInfo, final String catalogName, final String schemaName,
                final String name, final String definition) {
        this.catalogName = catalogName;
        this.connectionInfo = connectionInfo;
        this.definition = definition;
        this.name = name;
        this.schemaName = schemaName;

    }

    /**
     * Gets the select execution plan.
     *
     * @return the select execution plan.
     * @throws SQLException in case of parse failures.
     */
    private SelectPlan getSelectPlan() throws SQLException {
        if (selectPlan == null) {
            final SQLParser parser = new SQLParser(definition);
            final Plan<?, ?> plan = Planner.create(connectionInfo, parser.parse());
            if (!(plan instanceof SelectPlan) || plan.getParameterCount() > 0) {
                throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
            }

            this.selectPlan = (SelectPlan) plan;
        }

        return this.selectPlan;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getRowCount() throws SQLException {
        return load(new Field[0]).size();
    }

    @Override
    public TableType type() {
        return TableType.VIEW;
    }

    @Override
    public Field[] getFields() {
        if (fields == null) {
            try {
                fields = getSelectPlan().getColumns().stream()
                        .map((Column column) -> {
                            final Field field = new Field(column);
                            field.setTable(this);
                            return field;
                        }).toArray(Field[]::new);
            } catch (final SQLException e) {
                LOGGER.log(Level.FINEST, e.getMessage(), e);
                fields = new Field[0];
            }
        }

        return fields;
    }

    @Override
    public String getSchemaName() {
        return schemaName;
    }

    @Override
    public List<Object[]> load(final Field[] fieldsToLoad) throws SQLException {
        final Field[] fieldsLoaded = getFields();
        final int[] mapColumns = new int[fieldsToLoad.length];
        Arrays.fill(mapColumns, -1);
        for (int i = 0; i < fieldsToLoad.length; i++) {
            final Field field = fieldsToLoad[i];
            for (int loop = 0; loop < fieldsLoaded.length; loop++) {
                if (fieldsLoaded[loop].equals(field)) {
                    mapColumns[i] = loop;
                    break;
                }
            }
        }

        final SelectContext context = getSelectPlan().createContext(connectionInfo, null, null);
        return getSelectPlan().execute(context).stream()
                .map((Object[] row) -> {
                    final Object[] newRow = new Object[mapColumns.length];
                    for (int i = 0; i < mapColumns.length; i++) {
                        newRow[i] = row[mapColumns[i]];
                    }
                    return newRow;
                })
                .collect(Collectors.toList());
    }

    /**
     * Gets the view definition.
     *
     * @return the view definition.
     */
    public String definition() {
        return definition;
    }

    /**
     * Gets the view definition list.
     *
     * @return the view definition list.
     * @throws SQLException in case of failures.
     */
    public Field[] usages() throws SQLException {
        return getSelectPlan().getTables().stream().map(PlanTableNode::getColumns).flatMap(Collection::stream)
                .map(Column::getField).toArray(Field[]::new);
    }

    public static List<View> listViews(final File currentSchema, final String viewNamePattern,
                                       final ConnectionInfo connectionInfo) {
        return search(connectionInfo, currentSchema.getParentFile().getName(), currentSchema.getName(), currentSchema)
                .stream().filter(view -> viewNamePattern == null ||
                        Expressions.accept(connectionInfo.getLocale(), view.getName(), viewNamePattern, false, '\\'))
                .collect(Collectors.toList());
    }

    /**
     * Search for views in schema.
     *
     * @param connectionInfo the connection information.
     * @param catalog        the catalog name.
     * @param schemaName     the schema name.
     * @param directory      the directory to search.
     * @return the view list.
     */
    public static List<View> search(final ConnectionInfo connectionInfo, final String catalog, final String schemaName,
                                    final File directory) {
        final List<View> views = new ArrayList<>();

        if (directory.isDirectory()) {
            final File[] files = directory.listFiles(new SQLFilter(connectionInfo.getLocale()));
            if (files != null) {
                Arrays.stream(files).filter(Objects::nonNull).forEach((File file) -> {
                    try {
                        views.add(load(connectionInfo, catalog, schemaName, file));
                    } catch (final IOException e) {
                        LOGGER.log(Level.FINEST, e.getMessage(), e);
                    }
                });
            }
        }

        return views;
    }

    /**
     * Loads a view from a stream.
     *
     * @param connectionInfo the connection information.
     * @param catalog        the catalog name.
     * @param schemaName     the schema name.
     * @param name           the view name.
     * @param inputStream    the {@link InputStream to load}.
     * @return the loaded view.
     * @throws IOException in case of load failures.
     */
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

    /**
     * Loads a view from a file.
     *
     * @param connectionInfo the connection information.
     * @param catalog        the catalog name.
     * @param schemaName     the schema name.
     * @param file           the view file.
     * @return the loaded view.
     * @throws IOException in case of load failures.
     */
    public static View load(final ConnectionInfo connectionInfo, final String catalog, final String schemaName,
                            final File file) throws IOException {
        try (final FileInputStream fis = new FileInputStream(file)) {
            return load(connectionInfo, catalog, schemaName, Utils.removeSuffix(file.getName()), fis);
        }
    }
}
