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
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.utils.Constants;

import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;

/**
 * Tables.
 *
 * @since 1.6.0
 */
public class Tables implements Table {

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
    private final Field typeName = new Field("type_name", 0, 0, 0x0A, ParadoxType.VARCHAR, this, 5);
    private final Field charset = new Field("charset", 0, 0, 0, ParadoxType.VARCHAR, this, 6);
    private final Field encrypted = new Field("encrypted", 0, 0, 3, ParadoxType.VARCHAR, this, 7);
    private final Field writeProtected = new Field("write_protected", 0, 0, 3, ParadoxType.VARCHAR, this, 8);
    private final Field count = new Field("count", 0, 0, 0, ParadoxType.VARCHAR, this, 9);
    private final Field blockSize = new Field("block_size", 0, 0, 2, ParadoxType.INTEGER, this, 10);
    private final Field totalBlocks = new Field("total_blocks", 0, 0, 2, ParadoxType.INTEGER, this, 11);
    private final Field usedBlocks = new Field("used_blocks", 0, 0, 2, ParadoxType.INTEGER, this, 12);
    private final Field freeBlocks = new Field("free_blocks", 0, 0, 2, ParadoxType.INTEGER, this, 13);
    private final Field recordSize = new Field("record_size", 0, 0, 2, ParadoxType.INTEGER, this, 14);

    /**
     * Creates a new instance.
     *
     * @param connectionInfo the connection information.
     * @param catalogName    the catalog name.
     */
    public Tables(final ConnectionInfo connectionInfo, final String catalogName) {
        this.catalogName = catalogName;
        this.connectionInfo = connectionInfo;
    }

    @Override
    public String getName() {
        return "pdx_tables";
    }

    @Override
    public TableType type() {
        return TableType.SYSTEM_TABLE;
    }

    @Override
    public Index[] getIndexes() {
        return new Index[]{
                new SoftIndex("tables.pk", true,
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
                typeName,
                charset,
                encrypted,
                writeProtected,
                count,
                blockSize,
                totalBlocks,
                usedBlocks,
                freeBlocks,
                recordSize
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
                sum += localSchema.list(connectionInfo, null).size();
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
        map.put(type, details -> details.getTable().type().description());
        map.put(typeName, details -> details.getTable().type().typeName());
        map.put(charset, details -> Optional.ofNullable(details.getTable().getCharset()).map(Charset::displayName).orElse(null));
        map.put(encrypted, details -> description(details.getTable().isEncrypted()));
        map.put(writeProtected, details -> description(details.getTable().isWriteProtected()));
        map.put(count, details -> details.getTable().getRowCount());
        map.put(blockSize, details -> details.getTable().getBlockSizeBytes());
        map.put(totalBlocks, details -> details.getTable().getTotalBlocks());
        map.put(usedBlocks, details -> details.getTable().getUsedBlocks());
        map.put(freeBlocks, details -> details.getTable().getTotalBlocks() - details.getTable().getUsedBlocks());
        map.put(recordSize, details -> details.getTable().getRecordSize());

        final List<Object[]> ret = new ArrayList<>();
        for (final Schema localSchema : connectionInfo.getSchemas(catalogName, null)) {
            for (final Table table : localSchema.list(connectionInfo, null)) {
                final TableDetails details = new TableDetails();
                details.setSchema(localSchema);
                details.setTable(table);

                final Object[] row = Table.getFieldValues(fields, map, details);
                ret.add(row);
            }
        }

        return ret;
    }
}
