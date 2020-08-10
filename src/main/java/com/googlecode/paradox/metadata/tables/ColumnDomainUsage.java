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
import com.googlecode.paradox.metadata.Table;
import com.googlecode.paradox.metadata.TableType;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.utils.Constants;

import java.util.Collections;
import java.util.List;

/**
 * Column domain usage table.
 *
 * @version 1.0
 * @since 1.6.0
 */
public class ColumnDomainUsage implements Table {

    private final Field catalog = new Field("domain_catalog", 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR,
            this, 1);
    private final Field schema = new Field("domain_schema", 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR,
            this, 2);
    private final Field name = new Field("domain_name", 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 3);
    private final Field tableCatalog = new Field("table_catalog", 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR,
            this, 4);
    private final Field tableSchema = new Field("table_schema", 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR,
            this, 5);
    private final Field tableName = new Field("table_name", 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 6);
    private final Field columnName = new Field("column_name", 0, 0x0A, ParadoxType.VARCHAR, this, 7);

    /**
     * Creates a new instance.
     */
    public ColumnDomainUsage() {
        super();
    }

    @Override
    public String getName() {
        return "pdx_column_domain_usage";
    }

    @Override
    public int getRowCount() {
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
    public List<Object[]> load(final Field[] fields) {
        return Collections.emptyList();
    }
}
