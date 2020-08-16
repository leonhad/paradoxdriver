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
 * Tables.
 *
 * @version 1.1
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

    private final Field catalog = new Field("catalog", 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 1);
    private final Field schema = new Field("schema", 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 2);
    private final Field name = new Field("name", 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 3);
    private final Field type = new Field("type", 0, 0x0A, ParadoxType.VARCHAR, this, 4);
    private final Field typeName = new Field("type_name", 0, 0x0A, ParadoxType.VARCHAR, this, 5);
    private final Field charset = new Field("charset", 0, 0, ParadoxType.VARCHAR, this, 6);
    private final Field encrypted = new Field("encrypted", 0, 3, ParadoxType.VARCHAR, this, 7);
    private final Field writeProtected = new Field("write_protected", 0, 3, ParadoxType.VARCHAR, this, 8);
    private final Field count = new Field("count", 0, 0, ParadoxType.VARCHAR, this, 9);
    private final Field blockSize = new Field("block_size", 0, 2, ParadoxType.INTEGER, this, 10);
    private final Field totalBlocks = new Field("total_blocks", 0, 2, ParadoxType.INTEGER, this, 11);
    private final Field usedBlocks = new Field("used_blocks", 0, 2, ParadoxType.INTEGER, this, 12);
    private final Field freeBlocks = new Field("free_blocks", 0, 2, ParadoxType.INTEGER, this, 13);
    private final Field recordSize = new Field("record_size", 0, 2, ParadoxType.INTEGER, this, 14);

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
    public List<Object[]> load(final Field[] fields) throws SQLException {
        final List<Object[]> ret = new ArrayList<>();

        for (final Schema schema : connectionInfo.getSchemas(catalogName, null)) {
            for (final Table table : schema.list(connectionInfo, null)) {
                final Object[] row = new Object[fields.length];
                for (int i = 0; i < fields.length; i++) {
                    final Field field = fields[i];
                    Object value = null;
                    if (catalog.equals(field)) {
                        value = schema.catalogName();
                    } else if (this.schema.equals(field)) {
                        value = schema.name();
                    } else if (name.equals(field)) {
                        value = table.getName();
                    } else if (this.type.equals(field)) {
                        value = table.type().description();
                    } else if (this.typeName.equals(field)) {
                        value = table.type().typeName();
                    } else if (this.charset.equals(field) && table.getCharset() != null) {
                        value = table.getCharset().displayName();
                    } else if (this.encrypted.equals(field)) {
                        if (table.isEncrypted()) {
                            value = "YES";
                        } else {
                            value = "NO";
                        }
                    } else if (this.writeProtected.equals(field)) {
                        if (table.isWriteProtected()) {
                            value = "YES";
                        } else {
                            value = "NO";
                        }
                    } else if (this.count.equals(field) && table.getCharset() != null) {
                        value = table.getRowCount();
                    } else if (this.blockSize.equals(field)) {
                        value = table.getBlockSizeBytes();
                    } else if (this.totalBlocks.equals(field)) {
                        value = table.getTotalBlocks();
                    } else if (this.usedBlocks.equals(field)) {
                        value = table.getUsedBlocks();
                    } else if (this.freeBlocks.equals(field)) {
                        value = table.getTotalBlocks() - table.getUsedBlocks();
                    } else if (this.recordSize.equals(field)) {
                        value = table.getRecordSize();
                    }

                    row[i] = value;
                }

                ret.add(row);
            }
        }

        return ret;
    }
}
