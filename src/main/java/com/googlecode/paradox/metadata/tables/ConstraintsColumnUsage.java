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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ConstraintsColumnUsage implements Table {

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
    private final Field name = new Field("name", 0, 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 4);
    private final Field constraintName = new Field("constraint_name", 0, 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 4);

    /**
     * Creates a new instance.
     *
     * @param connectionInfo the connection information.
     * @param catalogName    the catalog name.
     */
    public ConstraintsColumnUsage(final ConnectionInfo connectionInfo, final String catalogName) {
        this.catalogName = catalogName;
        this.connectionInfo = connectionInfo;
    }

    @Override
    public String getName() {
        return "pdx_constraint_column_usage";
    }

    @Override
    public TableType type() {
        return TableType.SYSTEM_TABLE;
    }

    @Override
    public Index[] getIndexes() {
        return new Index[]{new SoftIndex("pdx_constraint_column_usage.pk", true, new Field[]{catalog, schema, table, name}, IndexType.PRIMARY_KEY, this::getRowCount)};
    }

    @Override
    public Field[] getFields() {
        return new Field[]{
                catalog,
                schema,
                table,
                name,
                constraintName
        };
    }

    @Override
    public String getSchemaName() {
        return ConnectionInfo.INFORMATION_SCHEMA;
    }

    @Override
    public int getRowCount() {
        try {
            return load(new Field[0]).size();
        } catch (SQLException e) {
            return 0;
        }
    }

    @Override
    public List<Object[]> load(final Field[] fields) throws SQLException {
        final Map<Field, Function<TableDetails, Object>> map = new HashMap<>();
        map.put(catalog, details -> details.getSchema().catalogName());
        map.put(schema, details -> details.getSchema().name());
        map.put(table, details -> {
            if (details.getTable() == null) {
                return details.getForeignKey().getReferencedTableName();
            }

            return details.getTable().getName();
        });
        map.put(name, details -> details.getCurrentField().getName());
        map.put(constraintName, details -> {
            if (details.getIndex() != null) {
                return details.getIndex().getName();
            }

            return details.getForeignKey().getName();
        });

        final List<Object[]> ret = new ArrayList<>();
        for (final Schema localSchema : connectionInfo.getSchemas(catalogName, null)) {
            for (final Table localTable : localSchema.list(connectionInfo, null)) {
                for (final Index index : localTable.getConstraints()) {
                    for (Field field : index.getFields()) {
                        final TableDetails details = new TableDetails();
                        details.setSchema(localSchema);
                        details.setTable(localTable);
                        details.setCurrentField(field);
                        details.setIndex(index);

                        final Object[] row = Table.getFieldValues(fields, map, details);
                        ret.add(row);
                    }
                }

                if (localTable instanceof ParadoxTable) {
//                    ParadoxForeignKey[] fks = ((ParadoxTable) localTable).getForeignKeys();
//                    for (ParadoxForeignKey fk : fks) {
//                        for (Field field : fk.getOriginFields()) {
//                            final TableDetails details = new TableDetails();
//                            details.setSchema(localSchema);
//                            details.setTable(localTable);
//                            details.setForeignKey(fk);
//                            details.setCurrentField(field);
//
//                            ret.add(Table.getFieldValues(fields, map, details));
//                        }
//                    }

                    // FIXME destination fields.
                }
            }
        }

        return ret;
    }
}
