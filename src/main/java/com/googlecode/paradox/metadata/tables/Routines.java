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
import com.googlecode.paradox.function.AbstractFunction;
import com.googlecode.paradox.function.FunctionFactory;
import com.googlecode.paradox.metadata.*;
import com.googlecode.paradox.metadata.tables.data.TableDetails;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.utils.Constants;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Routines.
 *
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

    private boolean isResultType(Column column) {
        return column.getColumnType() == AbstractFunction.RESULT;
    }

    @Override
    public List<Object[]> load(final Field[] fields) throws SQLException {
        final Map<Field, Function<TableDetails, Object>> map = new HashMap<>();
        map.put(catalog, details -> catalogName);
        map.put(schema, details -> details.getSchema().name());
        map.put(name, TableDetails::getFunctionName);
        map.put(type, details -> "FUNCTION");
        map.put(dataType, details -> Arrays.stream(details.getFunction().getColumns()).filter(this::isResultType).map(Column::getType).map(ParadoxType::name).findFirst().orElse(null));
        map.put(maximumLength, details -> Arrays.stream(details.getFunction().getColumns()).filter(this::isResultType).map(Column::getSize).findFirst().orElse(null));
        map.put(octetLength, details -> Arrays.stream(details.getFunction().getColumns()).filter(this::isResultType).map(Column::getOctets).findFirst().orElse(null));
        map.put(scale, details -> Arrays.stream(details.getFunction().getColumns()).filter(this::isResultType).map(Column::getScale).findFirst().orElse(null));
        map.put(precision, details -> Arrays.stream(details.getFunction().getColumns()).filter(this::isResultType).map(Column::getPrecision).findFirst().orElse(null));
        map.put(radix, details -> Arrays.stream(details.getFunction().getColumns()).filter(this::isResultType).map(Column::getRadix).filter(Objects::nonNull).findFirst().orElse(null));
        map.put(body, details -> "EXTERNAL");
        map.put(definition, details -> details.getFunction().definition());
        map.put(isDeterministic, details -> details.getFunction().isDeterministic());
        map.put(sqlDataAccess, details -> "READS");
        map.put(isNullCall, details -> description(Arrays.stream(details.getFunction().getColumns()).filter(this::isResultType).noneMatch(Column::isNullable)));
        map.put(isImplicitly, details -> "NO");
        map.put(remarks, details -> details.getFunction().getRemarks());

        final List<Object[]> ret = new ArrayList<>();
        for (final Schema localSchema : connectionInfo.getSchemas(catalogName, null)) {
            for (final Map.Entry<String, Supplier<? extends AbstractFunction>> entry : FunctionFactory.getFunctions().entrySet()) {
                final TableDetails details = new TableDetails();
                details.setSchema(localSchema);
                details.setFunction(entry.getValue().get());
                details.setFunctionName(entry.getKey());

                final Object[] row = Table.getFieldValues(fields, map, details);
                ret.add(row);
            }
        }

        return ret;
    }
}
