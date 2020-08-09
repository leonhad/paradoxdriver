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

package com.googlecode.paradox.metadata.views;

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.metadata.Field;
import com.googlecode.paradox.metadata.Schema;
import com.googlecode.paradox.metadata.Table;
import com.googlecode.paradox.metadata.TableType;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.utils.Constants;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Views view.
 *
 * @version 1.0
 * @since 1.6.0
 */
public class ViewsView implements Table {

    /**
     * The current catalog.
     */
    private final String catalogName;

    private final Field catalog = new Field("table_catalog", 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this,
            1);
    private final Field schema = new Field("table_schema", 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 1);
    private final Field name = new Field("table_name", 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 1);
    private final Field definition = new Field("view_definition", 0, 0, ParadoxType.VARCHAR, this, 1);
    private final Field check = new Field("check_option", 0, 0x07, ParadoxType.VARCHAR, this, 1);
    private final Field updatable = new Field("is_updatable", 0, 0x02, ParadoxType.VARCHAR, this, 1);

    /**
     * The connection information.
     */
    private final ConnectionInfo connectionInfo;

    /**
     * Creates a new instance.
     *
     * @param connectionInfo the connection information.
     * @param catalogName    the catalog name.
     */
    public ViewsView(final ConnectionInfo connectionInfo, final String catalogName) {
        this.catalogName = catalogName;
        this.connectionInfo = connectionInfo;
    }

    @Override
    public String getName() {
        return "views";
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
        return new Field[]{
                catalog,
                schema,
                name,
                definition,
                check,
                updatable
        };
    }

    @Override
    public String getSchemaName() {
        return ConnectionInfo.INFORMATION_SCHEMA;
    }

    @Override
    public List<Object[]> load(final Field[] fields) throws SQLException {
        final List<Object[]> ret = new ArrayList<>();

        for (final Schema schema : connectionInfo.getSchemas(catalogName, null)) {
            for (final Table table : schema.list(connectionInfo, null)) {
                if (table.type() != TableType.VIEW) {
                    continue;
                }

                final Object[] row = new Object[fields.length];
                for (int i = 0; i < fields.length; i++) {
                    final Field field = fields[i];
                    Object value = null;
                    if (catalog.equals(field)) {
                        value = schema.catalogName();
                    } else if (this.schema.equals(field)) {
                        value = schema.name();
                    } else if (name.equals(field)) {
                        value = table.getName();
                    } else if (this.definition.equals(field)) {
                        value = table.definition();
                    } else if (this.check.equals(field)) {
                        value = "NONE";
                    } else if (this.updatable.equals(field)) {
                        value = "NO";
                    }

                    row[i] = value;
                }

                ret.add(row);
            }
        }

        return ret;
    }
}
