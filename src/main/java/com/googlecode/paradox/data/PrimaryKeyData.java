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
package com.googlecode.paradox.data;

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.data.filefilters.PrimaryKeyFilter;
import com.googlecode.paradox.metadata.Table;
import com.googlecode.paradox.metadata.paradox.ParadoxPK;

import java.io.File;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Reads primary key data fields.
 *
 * @version 1.4
 * @since 1.0
 */
public final class PrimaryKeyData extends ParadoxData {

    /**
     * Utility class.
     */
    private PrimaryKeyData() {
        // Utility class.
    }

    /**
     * Gets the primary keys from the database file.
     *
     * @param currentSchema  the current schema file.
     * @param table          the tables primary key.
     * @param connectionInfo the connection information.
     * @return the primary keys.
     * @throws SQLException in case of load failures.
     */
    public static ParadoxPK getPrimaryKey(final File currentSchema, final Table table,
                                          final ConnectionInfo connectionInfo) throws SQLException {
        final String name = table.getName() + ".PX";

        final File[] fileList = currentSchema.listFiles(new PrimaryKeyFilter(connectionInfo.getLocale(), name));
        if ((fileList != null) && (fileList.length > 0)) {
            final ParadoxPK pk = loadHeader(fileList[0], connectionInfo);
            pk.setFields(Arrays.copyOf(table.getFields(), pk.getFieldCount()));
            return pk;
        }

        return null;
    }
}
