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
import com.googlecode.paradox.metadata.Field;
import com.googlecode.paradox.metadata.Table;
import com.googlecode.paradox.metadata.TableType;
import com.googlecode.paradox.metadata.tables.data.TableDetails;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.utils.Constants;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Routines parameters.
 *
 * @since 1.6.0
 */
public class Parameters implements Table {

    /**
     * The current catalog.
     */
    private final String catalogName;

    private final Field catalog = new Field("catalog", 0, 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 1);
    private final Field schema = new Field("schema", 0, 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 2);
    private final Field routine = new Field("routine", 0, 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 3);
    private final Field ordinal = new Field("ordinal", 0, 0, 0, ParadoxType.INTEGER, this, 4);
    private final Field mode = new Field("mode", 0, 0, 0x03, ParadoxType.VARCHAR, this, 5);
    private final Field isResult = new Field("is_result", 0, 0, 0x03, ParadoxType.VARCHAR, this, 6);
    private final Field name = new Field("name", 0, 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 7);
    private final Field dataType = new Field("data_type", 0, 0, 0, ParadoxType.VARCHAR, this, 8);
    private final Field maximumLength = new Field("character_maximum_length", 0, 0, 0x0A, ParadoxType.VARCHAR, this, 9);
    private final Field octetLength = new Field("character_octet_length", 0, 0, 0, ParadoxType.VARCHAR, this, 10);
    private final Field precision = new Field("precision", 0, 0, 4, ParadoxType.INTEGER, this, 11);
    private final Field scale = new Field("scale", 0, 0, 4, ParadoxType.INTEGER, this, 12);
    private final Field radix = new Field("numeric_precision_radix", 0, 0, 4, ParadoxType.INTEGER, this, 13);
    private final Field remarks = new Field("remarks", 0, 0, 4, ParadoxType.VARCHAR, this, 14);

    /**
     * Creates a new instance.
     *
     * @param catalogName the catalog name.
     */
    public Parameters(final String catalogName) {
        this.catalogName = catalogName;
    }

    @Override
    public String getName() {
        return "pdx_routine_parameters";
    }

    @Override
    public TableType type() {
        return TableType.SYSTEM_TABLE;
    }

    @Override
    public Field[] getFields() {
        return new Field[]{catalog, schema, routine, ordinal, mode, isResult, name, dataType, maximumLength, octetLength, precision, scale, radix, remarks};
    }

    @Override
    public String getSchemaName() {
        return ConnectionInfo.INFORMATION_SCHEMA;
    }

    @Override
    public int getRowCount() {
        int sum = 0;
        for (final Map.Entry<String, Supplier<? extends AbstractFunction>> entry : FunctionFactory.getFunctions().entrySet()) {
            final AbstractFunction function = entry.getValue().get();
            sum += function.getColumns().length;
        }

        return sum;
    }

    private String getColumnType(int type) {
        String value = null;
        if (type == AbstractFunction.IN) {
            value = "IN";
        } else if (type == AbstractFunction.RESULT) {
            value = "OUT";
        }

        return value;
    }

    private boolean isResultType(Column column) {
        return column.getColumnType() == AbstractFunction.RESULT;
    }

    @Override
    public List<Object[]> load(final Field[] fields) {
        final Map<Field, Function<TableDetails, Object>> map = new HashMap<>();
        map.put(catalog, details -> catalogName);
        map.put(schema, details -> getSchemaName());
        map.put(routine, TableDetails::getFunctionName);
        map.put(ordinal, details -> details.getColumn().getIndex());
        map.put(mode, details -> getColumnType(details.getColumn().getColumnType()));
        map.put(isResult, details -> description(this.isResultType(details.getColumn())));
        map.put(name, details -> details.getColumn().getName());
        map.put(dataType, details -> Arrays.stream(details.getFunction().getColumns()).filter(this::isResultType).map(Column::getType).map(ParadoxType::name).findFirst().orElse(null));
        map.put(maximumLength, details -> Arrays.stream(details.getFunction().getColumns()).filter(this::isResultType).map(Column::getSize).findFirst().orElse(null));
        map.put(octetLength, details -> Arrays.stream(details.getFunction().getColumns()).filter(this::isResultType).map(Column::getOctets).findFirst().orElse(null));
        map.put(scale, details -> Arrays.stream(details.getFunction().getColumns()).filter(this::isResultType).map(Column::getScale).findFirst().orElse(null));
        map.put(precision, details -> Arrays.stream(details.getFunction().getColumns()).filter(this::isResultType).map(Column::getPrecision).findFirst().orElse(null));
        map.put(radix, details -> Arrays.stream(details.getFunction().getColumns()).filter(this::isResultType).map(Column::getRadix).filter(Objects::nonNull).findFirst().orElse(null));
        map.put(remarks, details -> Arrays.stream(details.getFunction().getColumns()).filter(this::isResultType).map(Column::getRemarks).filter(Objects::nonNull).findFirst().orElse(null));

        final List<Object[]> ret = new ArrayList<>();
        for (final Map.Entry<String, Supplier<? extends AbstractFunction>> entry : FunctionFactory.getFunctions().entrySet()) {
            final AbstractFunction function = entry.getValue().get();
            for (final Column column : function.getColumns()) {
                final TableDetails details = new TableDetails();
                details.setFunction(entry.getValue().get());
                details.setFunctionName(entry.getKey());
                details.setColumn(column);

                final Object[] row = Table.getFieldValues(fields, map, details);
                ret.add(row);
            }

        }

        return ret;
    }
}
