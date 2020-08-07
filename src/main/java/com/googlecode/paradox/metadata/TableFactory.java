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

package com.googlecode.paradox.metadata;

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.data.TableData;
import com.googlecode.paradox.metadata.paradox.ParadoxTable;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class TableFactory {

    public static List<Table> listTables(final File schema, final String tablePattern,
                                         final ConnectionInfo connectionInfo) throws SQLException {
        return TableData.listTables(schema, tablePattern, connectionInfo);
    }

    public static Table findTable(final String catalog, final String schema, final String tableName,
                                  final ConnectionInfo connectionInfo) throws SQLException {
        for (final ParadoxTable paradoxTable : TableData.listTables(schema, connectionInfo)) {
            if (schema.equalsIgnoreCase(paradoxTable.getSchemaName())
                    && tableName.equalsIgnoreCase(paradoxTable.getName())) {
                return paradoxTable;
            }
        }

        return null;
    }
}
