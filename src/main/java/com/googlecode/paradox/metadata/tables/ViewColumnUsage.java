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
import com.googlecode.paradox.metadata.tables.data.TableDetails;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.utils.Constants;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Views tables and columns usage.
 *
 * @since 1.6.0
 */
public class ViewColumnUsage implements Table {

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
    private final Field name = new Field("name", 0, 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 3);
    private final Field tableCatalog = new Field("table_catalog", 0, 0, 0, ParadoxType.VARCHAR, this, 4);
    private final Field tableSchema = new Field("table_schema", 0, 0, 0x07, ParadoxType.VARCHAR, this, 5);
    private final Field tableName = new Field("table_name", 0, 0, 0x02, ParadoxType.VARCHAR, this, 6);
    private final Field columnName = new Field("column_name", 0, 0, 0x02, ParadoxType.VARCHAR, this, 7);

    /**
     * Creates a new instance.
     *
     * @param connectionInfo the connection information.
     * @param catalogName    the catalog name.
     */
    public ViewColumnUsage(final ConnectionInfo connectionInfo, final String catalogName) {
        this.catalogName = catalogName;
        this.connectionInfo = connectionInfo;
    }

    @Override
    public String getName() {
        return "pdx_view_column_usage";
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
                tableCatalog,
                tableSchema,
                tableName,
                columnName
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
                sum += (int) localSchema.list(connectionInfo, null).stream().filter(View.class::isInstance).count();
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
        map.put(name, details -> details.getTable().getName());
        map.put(tableCatalog, details -> details.getSchema().catalogName());
        map.put(tableSchema, details -> details.getTable().getSchemaName());
        map.put(tableName, details -> details.getCurrentField().getTable().getName());
        map.put(columnName, details -> details.getCurrentField().getName());

        final List<Object[]> ret = new ArrayList<>();
        for (final Schema localSchema : connectionInfo.getSchemas(catalogName, null)) {
            for (final Table table : localSchema.list(connectionInfo, null)) {
                if (!(table instanceof View)) {
                    continue;
                }

                for (final Field currentField : ((View) table).usages()) {
                    final TableDetails details = new TableDetails();
                    details.setSchema(localSchema);
                    details.setTable(table);
                    details.setCurrentField(currentField);

                    final Object[] row = Table.getFieldValues(fields, map, details);
                    ret.add(row);
                }
            }
        }

        return ret;
    }
}
