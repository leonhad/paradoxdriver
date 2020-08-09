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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Tables view.
 *
 * @version 1.0
 * @since 1.6.0
 */
public class TablesView implements Table {

    /**
     * The current catalog.
     */
    private final String catalog;

    private final Field CATALOG = new Field("table_catalog", 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this,
            1);
    private final Field SCHEMA = new Field("table_schema", 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 1);
    private final Field NAME = new Field("table_name", 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 1);
    private final Field TYPE = new Field("table_type", 0, 0x0A, ParadoxType.VARCHAR, this, 1);

    /**
     * The connection information.
     */
    private final ConnectionInfo connectionInfo;

    public TablesView(final ConnectionInfo connectionInfo, final String catalog) {
        this.catalog = catalog;
        this.connectionInfo = connectionInfo;
    }

    @Override
    public String getName() {
        return "tables";
    }

    @Override
    public int getRowCount() {
        return 0;
    }

    @Override
    public TableType type() {
        return TableType.VIEW;
    }

    @Override
    public Field[] getFields() {
        return new Field[]{
                CATALOG,
                SCHEMA,
                NAME,
                TYPE
        };
    }

    @Override
    public String getSchemaName() {
        return ConnectionInfo.INFORMATION_SCHEMA;
    }

    @Override
    public Charset getCharset() {
        return StandardCharsets.UTF_8;
    }

    @Override
    public List<Object[]> load(final Field[] fields) throws SQLException {
        final List<Object[]> ret = new ArrayList<>();

        for (final Schema schema : connectionInfo.getSchemas(catalog, null)) {
            for (final Table table : schema.list(connectionInfo, null)) {
                String type = table.type().name();
                if (table.type() == TableType.TABLE) {
                    type = "BASE TABLE";
                }

                final Object[] row = new Object[fields.length];
                for (int i = 0; i < fields.length; i++) {
                    final Field field = fields[i];
                    Object value = null;
                    if (CATALOG.equals(field)) {
                        value = schema.catalogName();
                    } else if (SCHEMA.equals(field)) {
                        value = schema.name();
                    } else if (NAME.equals(field)) {
                        value = table.getName();
                    } else if (TYPE.equals(field)) {
                        value = type;
                    }

                    row[i] = value;
                }

                ret.add(row);
            }
        }

        return ret;
    }
}
