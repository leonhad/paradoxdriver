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
 * @version 1.0
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
    private final Field ordinal = new Field("non_unique", 0, 4, ParadoxType.INTEGER, this, 6);
    private final Field ascOrDesc = new Field("asc_or_desc", 0, 4, ParadoxType.VARCHAR, this, 7);
    private final Field cardinality = new Field("cardinality", 0, 4, ParadoxType.INTEGER, this, 8);
    private final Field pages = new Field("pages", 0, 4, ParadoxType.INTEGER, this, 9);
    private final Field field = new Field("field", 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 10);

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
                field
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
                for (final Index index : table.getIndexes()) {
                    for (final Field indexField : index.getFields()) {
                        final Object[] row = new Object[fields.length];
                        for (int i = 0; i < fields.length; i++) {
                            final Field field = fields[i];
                            Object value = null;
                            if (catalog.equals(field)) {
                                value = schema.catalogName();
                            } else if (this.schema.equals(field)) {
                                value = schema.name();
                            } else if (this.table.equals(field)) {
                                value = table.getName();
                            } else if (name.equals(field)) {
                                value = index.getName();
                            } else if (this.nonUnique.equals(field)) {
                                value = !index.isUnique();
                            } else if (this.ordinal.equals(field)) {
                                value = index.getOrder();
                            } else if (this.ascOrDesc.equals(field)) {
                                if ("A".equals(index.getOrder())) {
                                    value = "ASC";
                                } else {
                                    value = "DESC";
                                }
                            } else if (this.cardinality.equals(field)) {
                                value = index.getRowCount();
                            } else if (this.pages.equals(field)) {
                                value = index.getTotalBlocks();
                            } else if (this.field.equals(field)) {
                                value = indexField.getName();
                            }

                            row[i] = value;
                        }

                        ret.add(row);
                    }

                    if (index.getFields().length == 0) {
                        final Object[] row = new Object[fields.length];
                        for (int i = 0; i < fields.length; i++) {
                            final Field field = fields[i];
                            Object value = null;
                            if (catalog.equals(field)) {
                                value = schema.catalogName();
                            } else if (this.schema.equals(field)) {
                                value = schema.name();
                            } else if (this.table.equals(field)) {
                                value = table.getName();
                            } else if (name.equals(field)) {
                                value = index.getName();
                            } else if (this.nonUnique.equals(field)) {
                                value = !index.isUnique();
                            } else if (this.ordinal.equals(field)) {
                                value = index.getOrder();
                            } else if (this.ascOrDesc.equals(field)) {
                                if ("A".equals(index.getOrder())) {
                                    value = "ASC";
                                } else {
                                    value = "DESC";
                                }
                            } else if (this.cardinality.equals(field)) {
                                value = index.getRowCount();
                            } else if (this.pages.equals(field)) {
                                value = index.getTotalBlocks();
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
