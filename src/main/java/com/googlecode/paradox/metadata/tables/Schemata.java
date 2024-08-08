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

package com.googlecode.paradox.metadata.tables;

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.metadata.*;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.utils.Constants;

import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;

/**
 * Schemata view.
 *
 * @version 1.2
 * @since 1.6.0
 */
public class Schemata implements Table {

    /**
     * The current catalog.
     */
    private final String catalogName;

    /**
     * The connection information.
     */
    private final ConnectionInfo connectionInfo;

    private final Field catalog = new Field("catalog_name", 0, 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 1);
    private final Field schema = new Field("schema_name", 0, 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 2);
    private final Field owner = new Field("schema_owner", 0, 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 3);
    private final Field characterCatalog = new Field("default_character_set_catalog", 0, 0, 6, ParadoxType.VARCHAR, this, 4);
    private final Field characterSchema = new Field("default_character_set_schema", 0, 0, 3, ParadoxType.VARCHAR, this, 5);
    private final Field characterName = new Field("default_character_set_name", 0, 0, 6, ParadoxType.VARCHAR, this, 6);

    /**
     * Creates a new instance.
     *
     * @param connectionInfo the connection information.
     * @param catalogName    the catalog name.
     */
    public Schemata(final ConnectionInfo connectionInfo, final String catalogName) {
        this.catalogName = catalogName;
        this.connectionInfo = connectionInfo;
    }

    @Override
    public String getName() {
        return "pdx_schemata";
    }

    @Override
    public TableType type() {
        return TableType.SYSTEM_TABLE;
    }

    @Override
    public Index[] getIndexes() {
        return new Index[]{
                new SoftIndex("schemata.pk", true,
                        new Field[]{catalog, schema}, IndexType.PRIMARY_KEY, this::getRowCount)
        };
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
    public int getRowCount() {
        try {
            return connectionInfo.getSchemas(catalogName, null).size();
        } catch (@SuppressWarnings("java:S1166") final SQLException e) {
            return 0;
        }
    }

    @Override
    public List<Object[]> load(final Field[] fields) throws SQLException {
        final Map<Field, Function<Schema, Object>> map = new HashMap<>();
        map.put(catalog, Schema::catalogName);
        map.put(schema, Schema::name);
        map.put(owner, s -> connectionInfo.getUser());
        map.put(characterName, s -> Optional.ofNullable(connectionInfo.getCharset()).map(Charset::displayName).orElse(null));

        final List<Object[]> ret = new ArrayList<>();
        for (final Schema currentSchema : connectionInfo.getSchemas(catalogName, null)) {
            final Object[] row = Table.getFieldValues(fields, map, currentSchema);
            ret.add(row);
        }

        return ret;
    }
}
