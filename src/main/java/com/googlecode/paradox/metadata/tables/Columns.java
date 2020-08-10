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
 * Columns.
 *
 * @version 1.0
 * @since 1.6.0
 */
public class Columns implements Table {

    /**
     * The current catalog.
     */
    private final String catalogName;

    private final Field catalog = new Field("catalog", 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 1);
    private final Field schema = new Field("schema", 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 2);
    private final Field table = new Field("table", 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 3);
    private final Field name = new Field("name", 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 4);
    private final Field ordinal = new Field("ordinal", 0, 1, ParadoxType.INTEGER, this, 5);
    private final Field nullable = new Field("is_nullable", 0, 3, ParadoxType.VARCHAR, this, 6);
    private final Field maximumLength = new Field("maximum_length", 0, 4, ParadoxType.INTEGER, this, 7);
    private final Field octetLength = new Field("octet_length", 0, 4, ParadoxType.INTEGER, this, 8);
    private final Field precision = new Field("precision", 0, 4, ParadoxType.INTEGER, this, 9);
    private final Field scale = new Field("scale", 0, 4, ParadoxType.INTEGER, this, 10);
    private final Field type = new Field("type", 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 11);
    private final Field javaClass = new Field("java_class", 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this,
            12);

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
    public Columns(final ConnectionInfo connectionInfo, final String catalogName) {
        this.catalogName = catalogName;
        this.connectionInfo = connectionInfo;
    }

    @Override
    public String getName() {
        return "pdx_columns";
    }

    @Override
    public int getRowCount() throws SQLException {
        return load(new Field[0]).size();
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
                ordinal,
                nullable,
                maximumLength,
                octetLength,
                precision,
                scale,
                type,
                javaClass
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
                for (final Field fieldLocal : table.getFields()) {
                    final Object[] row = new Object[fields.length];
                    for (int i = 0; i < fields.length; i++) {
                        final Field field = fields[i];
                        if (fieldLocal == null) {
                            continue;
                        }

                        Object value = null;
                        if (catalog.equals(field)) {
                            value = schema.catalogName();
                        } else if (this.schema.equals(field)) {
                            value = schema.name();
                        } else if (this.table.equals(field)) {
                            value = table.getName();
                        } else if (this.name.equals(field)) {
                            value = fieldLocal.getName();
                        } else if (this.ordinal.equals(field)) {
                            value = fieldLocal.getOrderNum();
                        } else if (this.nullable.equals(field)) {
                            if (fieldLocal.isAutoIncrement()) {
                                value = "NO";
                            } else {
                                value = "YES";
                            }
                        } else if (this.maximumLength.equals(field)) {
                            value = fieldLocal.getSize();
                        } else if (this.octetLength.equals(field)) {
                            value = fieldLocal.getSize();
                        } else if (this.precision.equals(field)) {
                            value = fieldLocal.getPrecision();
                        } else if (this.scale.equals(field)) {
                            value = fieldLocal.getPrecision();
                        } else if (this.type.equals(field) && fieldLocal.getType() != null) {
                            value = fieldLocal.getType().getName();
                        } else if (this.javaClass.equals(field) && fieldLocal.getType() != null) {
                            value = fieldLocal.getType().getJavaClass().getName();
                            if ("[B".equals(value)) {
                                value = "byte[]";
                            }
                        }

                        row[i] = value;
                    }

                    ret.add(row);
                }
            }
        }

        return ret;
    }
}
