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

import java.sql.DatabaseMetaData;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;

/**
 * Columns.
 *
 * @since 1.6.0
 */
public class Columns implements Table {

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
    private final Field ordinal = new Field("ordinal", 0, 0, 1, ParadoxType.INTEGER, this, 5);
    private final Field isNullable = new Field("is_nullable", 0, 0, 3, ParadoxType.VARCHAR, this, 6);
    private final Field autoincrement = new Field("is_autoincrement", 0, 0, 3, ParadoxType.VARCHAR, this, 7);
    private final Field incrementValue = new Field("autoincrement_value", 0, 0, 0, ParadoxType.INTEGER, this, 8);
    private final Field incrementStep = new Field("autoincrement_step", 0, 0, 0, ParadoxType.INTEGER, this, 9);
    private final Field maximumLength = new Field("maximum_length", 0, 0, 4, ParadoxType.INTEGER, this, 10);
    private final Field octetLength = new Field("octet_length", 0, 0, 4, ParadoxType.INTEGER, this, 11);
    private final Field precision = new Field("precision", 0, 0, 4, ParadoxType.INTEGER, this, 12);
    private final Field radix = new Field("radix", 0, 0, 4, ParadoxType.INTEGER, this, 13);
    private final Field scale = new Field("scale", 0, 0, 4, ParadoxType.INTEGER, this, 14);
    private final Field type = new Field("type", 0, 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 15);
    private final Field javaClass = new Field("java_class", 0, 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 16);
    private final Field javaType = new Field("java_type", 0, 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 17);
    private final Field javaTypeId = new Field("java_type_id", 0, 0, 4, ParadoxType.INTEGER, this, 18);
    private final Field nullable = new Field("nullable", 0, 0, 4, ParadoxType.INTEGER, this, 19);

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
    public TableType type() {
        return TableType.SYSTEM_TABLE;
    }

    @Override
    public Index[] getIndexes() {
        return new Index[]{
                new SoftIndex("columns.pk", true,
                        new Field[]{catalog, schema, table, name}, IndexType.PRIMARY_KEY, this::getRowCount)
        };
    }

    @Override
    public Field[] getFields() {
        return new Field[]{
                catalog,
                schema,
                table,
                name,
                ordinal,
                isNullable,
                autoincrement,
                incrementValue,
                incrementStep,
                maximumLength,
                octetLength,
                precision,
                radix,
                scale,
                type,
                javaClass,
                javaType,
                javaTypeId,
                nullable
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
                    sum += localTable.getFields().length;
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
        map.put(name, details -> details.getCurrentField().getName());
        map.put(ordinal, details -> details.getCurrentField().getOrderNum());
        map.put(isNullable, details -> description(!details.getCurrentField().isRequired()));
        map.put(autoincrement, details -> description(details.getCurrentField().isAutoIncrement()));
        map.put(incrementValue, details -> {
            if (details.getCurrentField().isAutoIncrement()) {
                return details.getTable().getAutoIncrementValue();
            }

            return null;
        });
        map.put(incrementStep, details -> {
            if (details.getCurrentField().isAutoIncrement()) {
                return 1;
            }

            return null;
        });
        map.put(maximumLength, details -> details.getCurrentField().getPrecision());
        map.put(octetLength, details -> details.getCurrentField().getRealSize());
        map.put(precision, details -> details.getCurrentField().getPrecision());
        map.put(radix, details -> Optional.ofNullable(details.getCurrentField()).map(Field::getType).map(ParadoxType::getRadix).orElse(null));
        map.put(scale, details -> details.getCurrentField().getScale());
        map.put(type, details -> Optional.ofNullable(details.getCurrentField()).map(Field::getType).map(ParadoxType::name).orElse(null));
        map.put(javaClass, details -> {
            String typeValue = Optional.ofNullable(details.getCurrentField()).map(Field::getType).map(ParadoxType::name).orElse(null);
            if ("[B".equals(typeValue)) {
                typeValue = "byte[]";
            }

            return typeValue;
        });
        map.put(javaType, details -> Optional.ofNullable(details.getCurrentField()).map(Field::getType).map(ParadoxType::getSQLType).map(JDBCType::valueOf).map(JDBCType::getName).orElse(null));
        map.put(javaTypeId, details -> Optional.ofNullable(details.getCurrentField()).map(Field::getType).map(ParadoxType::getSQLType).orElse(null));
        map.put(nullable, details -> {
            int nullValue = DatabaseMetaData.columnNullable;

            if (details.getCurrentField().isRequired()) {
                nullValue = DatabaseMetaData.columnNoNulls;
            }

            return nullValue;
        });

        final List<Object[]> ret = new ArrayList<>();
        for (final Schema localSchema : connectionInfo.getSchemas(catalogName, null)) {
            for (final Table localTable : localSchema.list(connectionInfo, null)) {
                for (final Field currentField : localTable.getFields()) {
                    final TableDetails details = new TableDetails();
                    details.setSchema(localSchema);
                    details.setTable(localTable);
                    details.setCurrentField(currentField);

                    final Object[] row = Table.getFieldValues(fields, map, details);
                    ret.add(row);
                }
            }
        }

        return ret;
    }
}
