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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Key columns.
 *
 * @since 1.6.0
 */
public class KeyColumns implements Table {

    /**
     * The current catalog.
     */
    private final String catalogName;

    /**
     * The connection information.
     */
    private final ConnectionInfo connectionInfo;

    private final Field catalog = new Field("catalog", 0, 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 1);
    private final Field schema = new Field("schema", 0, 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 2);
    private final Field table = new Field("table", 0, 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 3);
    private final Field constraintName = new Field("constraint_name", 0, 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 4);
    private final Field name = new Field("name", 0, 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 5);
    private final Field ordinal = new Field("ordinal", 0, 0, 4, ParadoxType.INTEGER, this, 6);

    /**
     * Creates a new instance.
     *
     * @param connectionInfo the connection information.
     * @param catalogName    the catalog name.
     */
    public KeyColumns(final ConnectionInfo connectionInfo, final String catalogName) {
        this.catalogName = catalogName;
        this.connectionInfo = connectionInfo;
    }

    @Override
    public String getName() {
        return "pdx_key_columns";
    }

    @Override
    public TableType type() {
        return TableType.SYSTEM_TABLE;
    }

    @Override
    public Index[] getIndexes() {
        return new Index[]{
                new SoftIndex("key_columns.pk", true, new Field[]{catalog, schema, table, constraintName, name},
                        IndexType.PRIMARY_KEY, this::getRowCount)
        };
    }

    @Override
    public Field[] getFields() {
        return new Field[]{
                catalog,
                schema,
                table,
                constraintName,
                name,
                ordinal
        };
    }

    @Override
    public String getSchemaName() {
        return ConnectionInfo.INFORMATION_SCHEMA;
    }

    @Override
    public int getRowCount() {
        try {
            int sum = 0;
            for (final Schema localSchema : connectionInfo.getSchemas(catalogName, null)) {
                for (final Table localTable : localSchema.list(connectionInfo, null)) {
                    Index index = localTable.getPrimaryKeyIndex();
                    if (index == null) {
                        continue;
                    }

                    sum += index.getFields().length;
                }
            }

            return sum;
        } catch (@SuppressWarnings("java:S1166") final SQLException e) {
            return 0;
        }
    }

    @Override
    public List<Object[]> load(final Field[] fields) throws SQLException {
        final Map<Field, Function<TableDetails, Object>> map = new HashMap<>();
        map.put(catalog, details -> details.getSchema().catalogName());
        map.put(schema, details -> details.getSchema().name());
        map.put(table, details -> details.getTable().getName());
        map.put(constraintName, details -> details.getIndex().getName());
        map.put(name, details -> details.getCurrentField().getName());
        map.put(ordinal, details -> details.getCurrentField().getOrderNum());

        final List<Object[]> ret = new ArrayList<>();
        for (final Schema localSchema : connectionInfo.getSchemas(catalogName, null)) {
            for (final Table localTable : localSchema.list(connectionInfo, null)) {
                Index index = localTable.getPrimaryKeyIndex();
                if (index == null) {
                    continue;
                }

                for (final Field fieldLocal : index.getFields()) {
                    final TableDetails details = new TableDetails();
                    details.setSchema(localSchema);
                    details.setTable(localTable);
                    details.setIndex(index);
                    details.setCurrentField(fieldLocal);

                    final Object[] row = Table.getFieldValues(fields, map, details);
                    ret.add(row);
                }
            }
        }

        return ret;
    }
}
