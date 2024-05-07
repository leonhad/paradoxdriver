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
import com.googlecode.paradox.metadata.paradox.ParadoxForeignKey;
import com.googlecode.paradox.metadata.paradox.ParadoxTable;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.utils.Constants;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;

/**
 * Referential constraints table.
 *
 * @version 1.2
 * @since 1.6.0
 */
public class ReferentialConstraints implements Table {

    /**
     * The current catalog.
     */
    private final String catalogName;

    /**
     * The connection information.
     */
    private final ConnectionInfo connectionInfo;

    private final Field catalog = new Field("constraint_catalog", 0, 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 1);
    private final Field schema = new Field("constraint_schema", 0, 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 2);
    private final Field name = new Field("constraint_name", 0, 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 3);
    private final Field check = new Field("match_option", 0, 0, 0x07, ParadoxType.VARCHAR, this, 4);
    private final Field updateRule = new Field("update_rule", 0, 0, 0x0B, ParadoxType.VARCHAR, this, 5);
    private final Field deleteRule = new Field("delete_rule", 0, 0, 0x0B, ParadoxType.VARCHAR, this, 6);

    /**
     * Creates a new instance.
     *
     * @param connectionInfo the connection information.
     * @param catalogName    the catalog name.
     */
    public ReferentialConstraints(final ConnectionInfo connectionInfo, final String catalogName) {
        this.catalogName = catalogName;
        this.connectionInfo = connectionInfo;
    }

    @Override
    public String getName() {
        return "pdx_referential_constraints";
    }

    @Override
    public TableType type() {
        return TableType.SYSTEM_TABLE;
    }

    @Override
    public Field[] getFields() {
        return new Field[]{
                catalog,
                schema,
                name,
                check,
                updateRule,
                deleteRule
        };
    }

    @Override
    public int getRowCount() {
        return 0;
    }

    @Override
    public String getSchemaName() {
        return ConnectionInfo.INFORMATION_SCHEMA;
    }

    @Override
    public List<Object[]> load(final Field[] fields) throws SQLException {
        final Map<Field, Function<TableDetails, Object>> map = new HashMap<>();
        map.put(catalog, details -> details.getSchema().catalogName());
        map.put(schema, details -> details.getSchema().name());
        map.put(name, details -> details.getForeignKey().getName());
        map.put(check, details -> "NONE");
        map.put(updateRule, details -> "RESTRICT");
        map.put(deleteRule, details -> {
            if (details.getForeignKey().isCascade()) {
                return "CASCADE";
            }

            return "RESTRICT";
        });

        final List<Object[]> ret = new ArrayList<>();
        for (final Schema localSchema : connectionInfo.getSchemas(catalogName, null)) {
            for (final Table localTable : localSchema.list(connectionInfo, null)) {
                if (localTable instanceof ParadoxTable) {
                    ParadoxForeignKey[] fks = ((ParadoxTable) localTable).getForeignKeys();
                    for (ParadoxForeignKey fk : fks) {
                        final TableDetails details = new TableDetails();
                        details.setSchema(localSchema);
                        details.setTable(localTable);
                        details.setForeignKey(fk);

                        final Object[] row = Table.getFieldValues(fields, map, details);
                        ret.add(row);
                    }
                }
            }
        }

        return ret;
    }
}
