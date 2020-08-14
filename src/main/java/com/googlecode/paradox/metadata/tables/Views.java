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

package com.googlecode.paradox.metadata.tables;

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.metadata.*;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.utils.Constants;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Views.
 *
 * @version 1.1
 * @since 1.6.0
 */
public class Views implements Table {

    /**
     * The current catalog.
     */
    private final String catalogName;

    /**
     * The connection information.
     */
    private final ConnectionInfo connectionInfo;

    private final Field catalog = new Field("catalog", 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this,
            1);
    private final Field schema = new Field("schema", 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 2);
    private final Field name = new Field("name", 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 3);
    private final Field definition = new Field("definition", 0, 0, ParadoxType.VARCHAR, this, 4);
    private final Field check = new Field("check_option", 0, 0x07, ParadoxType.VARCHAR, this, 5);
    private final Field updatable = new Field("is_updatable", 0, 0x02, ParadoxType.VARCHAR, this, 6);

    /**
     * Creates a new instance.
     *
     * @param connectionInfo the connection information.
     * @param catalogName    the catalog name.
     */
    public Views(final ConnectionInfo connectionInfo, final String catalogName) {
        this.catalogName = catalogName;
        this.connectionInfo = connectionInfo;
    }

    @Override
    public String getName() {
        return "pdx_views";
    }

    @Override
    public TableType type() {
        return TableType.SYSTEM_TABLE;
    }

    @Override
    public Index getPrimaryKeyIndex() {
        return new SoftIndex("views.pk", true,
                new Field[]{catalog, schema, name}, IndexType.PRIMARY_KEY, this::getRowCount);
    }

    @Override
    public Index[] getIndexes() {
        return new Index[]{
                new SoftIndex("views.pk", true,
                        new Field[]{catalog, schema, name}, IndexType.UNIQUE, this::getRowCount)
        };
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
                if (!(table instanceof View)) {
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
                        value = ((View) table).definition();
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
