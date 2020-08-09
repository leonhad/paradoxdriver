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
 * Schemata view.
 *
 * @version 1.0
 * @since 1.6.0
 */
public class SchemataView implements Table {

    /**
     * The current catalog.
     */
    private final String catalogName;

    private final Field catalog = new Field("catalog_name", 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this,
            1);
    private final Field schema = new Field("schema_name", 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 1);
    private final Field owner = new Field("schema_owner", 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 1);
    private final Field characterCatalog = new Field("default_character_set_catalog", 0, 0x0A, ParadoxType.VARCHAR,
            this, 1);
    private final Field characterSchema = new Field("default_character_set_schema", 0, 0x0A, ParadoxType.VARCHAR,
            this, 1);
    private final Field characterName = new Field("default_character_set_name", 0, 0x0A, ParadoxType.VARCHAR, this, 1);

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
    public SchemataView(final ConnectionInfo connectionInfo, final String catalogName) {
        this.catalogName = catalogName;
        this.connectionInfo = connectionInfo;
    }

    @Override
    public String getName() {
        return "schemata";
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
                owner,
                characterCatalog,
                characterSchema,
                characterName
        };
    }

    @Override
    public String getSchemaName() {
        return ConnectionInfo.INFORMATION_SCHEMA;
    }

    @Override
    public List<Object[]> load(final Field[] fields) throws SQLException {
        final List<Object[]> ret = new ArrayList<>();

        for (final Schema currentSchema : connectionInfo.getSchemas(catalogName, null)) {
            final Object[] row = new Object[fields.length];
            for (int i = 0; i < fields.length; i++) {
                final Field field = fields[i];
                Object value = null;
                if (catalog.equals(field)) {
                    value = currentSchema.catalogName();
                } else if (this.schema.equals(field)) {
                    value = currentSchema.name();
                } else if (owner.equals(field)) {
                    value = currentSchema.name();
                }

                row[i] = value;
            }

            ret.add(row);
        }

        return ret;
    }
}
