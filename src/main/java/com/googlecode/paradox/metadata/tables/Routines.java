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
import com.googlecode.paradox.function.AbstractFunction;
import com.googlecode.paradox.function.FunctionFactory;
import com.googlecode.paradox.metadata.*;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.utils.Constants;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Supplier;

/**
 * Routines.
 *
 * @version 1.3
 * @since 1.6.0
 */
public class Routines implements Table {

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
    private final Field type = new Field("type", 0, 0, 0x0A, ParadoxType.VARCHAR, this, 4);
    private final Field dataType = new Field("data_type", 0, 0, 0, ParadoxType.VARCHAR, this, 4);
    private final Field maximumLength = new Field("character_maximum_length", 0, 0, 0x0A, ParadoxType.VARCHAR, this, 5);
    private final Field octetLength = new Field("character_octet_length", 0, 0, 0, ParadoxType.VARCHAR, this, 6);
    private final Field precision = new Field("precision", 0, 0, 4, ParadoxType.INTEGER, this, 7);
    private final Field scale = new Field("scale", 0, 0, 4, ParadoxType.INTEGER, this, 8);
    private final Field radix = new Field("numeric_precision_radix", 0, 0, 4, ParadoxType.INTEGER, this, 9);
    private final Field body = new Field("routine_body", 0, 0, 0, ParadoxType.VARCHAR, this, 10);
    private final Field definition = new Field("routine_definition", 0, 0, 0, ParadoxType.VARCHAR, this, 11);
    private final Field isDeterministic = new Field("is_deterministic", 0, 0, 3, ParadoxType.VARCHAR, this, 12);
    private final Field sqlDataAccess = new Field("sql_data_access", 0, 0, 30, ParadoxType.VARCHAR, this, 13);
    private final Field isNullCall = new Field("is_null_call", 0, 0, 3, ParadoxType.VARCHAR, this, 14);
    private final Field isImplicitly = new Field("is_implicitly_invocable", 0, 0, 3, ParadoxType.VARCHAR, this, 14);
    private final Field remarks = new Field("remarks", 0, 0, 0, ParadoxType.VARCHAR, this, 15);

    /**
     * Creates a new instance.
     *
     * @param connectionInfo the connection information.
     * @param catalogName    the catalog name.
     */
    public Routines(final ConnectionInfo connectionInfo, final String catalogName) {
        this.connectionInfo = connectionInfo;
        this.catalogName = catalogName;
    }

    @Override
    public String getName() {
        return "pdx_routines";
    }

    @Override
    public TableType type() {
        return TableType.SYSTEM_TABLE;
    }

    @Override
    public Index[] getIndexes() {
        return new Index[]{
                new SoftIndex("routines.pk", true,
                        new Field[]{catalog, schema, name, type}, IndexType.PRIMARY_KEY, this::getRowCount)
        };
    }

    @Override
    public Field[] getFields() {
        return new Field[]{
                catalog,
                schema,
                name,
                type,
                dataType,
                maximumLength,
                octetLength,
                precision,
                scale,
                radix,
                body,
                definition,
                isDeterministic,
                sqlDataAccess,
                isNullCall,
                isImplicitly,
                remarks
        };
    }

    @Override
    public String getSchemaName() {
        return ConnectionInfo.INFORMATION_SCHEMA;
    }

    @Override
    public int getRowCount() {
        try {
            return connectionInfo.getSchemas(catalogName, null).stream()
                    .mapToInt(localSchema -> FunctionFactory.getFunctions().size())
                    .sum();
        } catch (@SuppressWarnings("java:S1166") final SQLException e) {
            return 0;
        }
    }

    @Override
    public List<Object[]> load(final Field[] fields) throws SQLException {
        final List<Object[]> ret = new ArrayList<>();

        for (final Schema localSchema : connectionInfo.getSchemas(catalogName, null)) {
            for (final Map.Entry<String, Supplier<? extends AbstractFunction>> entry :
                    FunctionFactory.getFunctions().entrySet()) {
                final AbstractFunction function = entry.getValue().get();
                final Object[] row = new Object[fields.length];
                for (int i = 0; i < fields.length; i++) {
                    final Field field = fields[i];
                    Object value = null;
                    if (this.catalog.equals(field)) {
                        value = catalogName;
                    } else if (this.schema.equals(field)) {
                        value = localSchema.name();
                    } else if (this.name.equals(field)) {
                        value = entry.getKey();
                    } else if (this.type.equals(field)) {
                        value = "FUNCTION";
                    } else if (this.dataType.equals(field)) {
                        value = Arrays.stream(function.getColumns())
                                .filter(c -> c.getColumnType() == AbstractFunction.RESULT)
                                .map(Column::getType)
                                .map(ParadoxType::name)
                                .findFirst().orElse(null);
                    } else if (this.maximumLength.equals(field)) {
                        value = Arrays.stream(function.getColumns())
                                .filter(c -> c.getColumnType() == AbstractFunction.RESULT)
                                .map(Column::getSize)
                                .findFirst().orElse(null);
                    } else if (this.octetLength.equals(field)) {
                        value = Arrays.stream(function.getColumns())
                                .filter(c -> c.getColumnType() == AbstractFunction.RESULT)
                                .map(Column::getOctets)
                                .findFirst().orElse(null);
                    } else if (this.scale.equals(field)) {
                        value = Arrays.stream(function.getColumns())
                                .filter(c -> c.getColumnType() == AbstractFunction.RESULT)
                                .map(Column::getScale)
                                .findFirst().orElse(null);
                    } else if (this.precision.equals(field)) {
                        value = Arrays.stream(function.getColumns())
                                .filter(c -> c.getColumnType() == AbstractFunction.RESULT)
                                .map(Column::getPrecision)
                                .findFirst().orElse(null);
                    } else if (this.radix.equals(field)) {
                        value = Arrays.stream(function.getColumns())
                                .filter(c -> c.getColumnType() == AbstractFunction.RESULT)
                                .map(Column::getRadix)
                                .filter(Objects::nonNull)
                                .findFirst().orElse(null);
                    } else if (this.body.equals(field)) {
                        value = "EXTERNAL";
                    } else if (this.definition.equals(field)) {
                        value = function.definition();
                    } else if (this.isDeterministic.equals(field)) {
                        if (function.isDeterministic()) {
                            value = "YES";
                        } else {
                            value = "NO";
                        }
                    } else if (this.sqlDataAccess.equals(field)) {
                        value = "READS";
                    } else if (this.isNullCall.equals(field)) {
                        if (Arrays.stream(function.getColumns())
                                .filter(c -> c.getColumnType() != AbstractFunction.RESULT)
                                .anyMatch(Column::isNullable)) {
                            value = "NO";
                        } else {
                            value = "YES";
                        }
                    } else if (this.isImplicitly.equals(field)) {
                        value = "NO";
                    } else if (this.remarks.equals(field)) {
                        value = function.getRemarks();
                    }

                    row[i] = value;
                }

                ret.add(row);
            }
        }

        return ret;
    }
}
