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
 * Indexes table.
 *
 * @version 1.1
 * @since 1.6.0
 */
public class Indexes implements Table {

    /**
     * The current catalog.
     */
    private final String catalogName;

    /**
     * The connection information.
     */
    private final ConnectionInfo connectionInfo;

    private final Field catalog = new Field("index_catalog", 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR,
            this, 1);
    private final Field schema = new Field("index_schema", 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR,
            this, 2);
    private final Field table = new Field("table_name", 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 3);
    private final Field name = new Field("index_name", 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 4);
    private final Field nonUnique = new Field("non_unique", 0, 1, ParadoxType.BOOLEAN, this, 5);
    private final Field ordinal = new Field("ordinal", 0, 4, ParadoxType.INTEGER, this, 6);
    private final Field ascOrDesc = new Field("asc_or_desc", 0, 4, ParadoxType.VARCHAR, this, 7);
    private final Field cardinality = new Field("cardinality", 0, 4, ParadoxType.INTEGER, this, 8);
    private final Field pages = new Field("pages", 0, 4, ParadoxType.INTEGER, this, 9);
    private final Field field = new Field("field", 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 10);
    private final Field type = new Field("type", 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 11);

    /**
     * Creates a new instance.
     *
     * @param connectionInfo the connection information.
     * @param catalogName    the catalog name.
     */
    public Indexes(final ConnectionInfo connectionInfo, final String catalogName) {
        this.catalogName = catalogName;
        this.connectionInfo = connectionInfo;
    }

    @Override
    public String getName() {
        return "pdx_indexes";
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
                table,
                name,
                nonUnique,
                ordinal,
                ascOrDesc,
                cardinality,
                pages,
                field,
                type
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
                    for (final Index index : localTable.getIndexes()) {
                        sum += index.getFields().length;
                    }
                }
            }

            return sum;
        } catch (@SuppressWarnings("java:S1166") final SQLException e) {
            return 0;
        }
    }

    @Override
    public List<Object[]> load(final Field[] fields) throws SQLException {
        final List<Object[]> ret = new ArrayList<>();

        for (final Schema localSchema : connectionInfo.getSchemas(catalogName, null)) {
            for (final Table localTable : localSchema.list(connectionInfo, null)) {
                for (final Index index : localTable.getIndexes()) {
                    for (final Field indexField : index.getFields()) {
                        final Object[] row = new Object[fields.length];
                        for (int i = 0; i < fields.length; i++) {
                            final Field localField = fields[i];
                            Object value = null;
                            if (catalog.equals(localField)) {
                                value = localSchema.catalogName();
                            } else if (this.schema.equals(localField)) {
                                value = localSchema.name();
                            } else if (this.table.equals(localField)) {
                                value = localTable.getName();
                            } else if (name.equals(localField)) {
                                value = index.getName();
                            } else if (this.nonUnique.equals(localField)) {
                                value = !index.isUnique();
                            } else if (this.ordinal.equals(localField)) {
                                value = indexField.getOrderNum();
                            } else if (this.ascOrDesc.equals(localField)) {
                                value = index.getOrder().name();
                            } else if (this.cardinality.equals(localField)) {
                                value = index.getRowCount();
                            } else if (this.pages.equals(localField)) {
                                value = index.getTotalBlocks();
                            } else if (this.field.equals(localField)) {
                                value = indexField.getName();
                            } else if (this.type.equals(localField)) {
                                value = index.type().description();
                            }

                            row[i] = value;
                        }

                        ret.add(row);
                    }
                }
            }
        }

        return ret;
    }
}
