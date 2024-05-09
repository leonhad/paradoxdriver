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
import com.googlecode.paradox.data.filefilters.SecondaryIndexFilter;
import com.googlecode.paradox.metadata.Index;
import com.googlecode.paradox.metadata.Table;
import com.googlecode.paradox.metadata.paradox.ParadoxIndex;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads index data files.
 *
 * @version 1.9
 * @since 1.0
 */
public final class IndexData extends AbstractParadoxData {

    /**
     * Utility class.
     */
    private IndexData() {
        super();
    }

    /**
     * List the indexes in a database file.
     *
     * @param currentSchema  the current schema file.
     * @param table          the index original table.
     * @param connectionInfo the connection information.
     * @return a list of {@link ParadoxIndex}.
     * @throws SQLException in case of reading failures.
     */
    public static List<Index> listIndexes(final File currentSchema, final Table table, final ConnectionInfo connectionInfo) throws SQLException {
        final ArrayList<Index> indexes = new ArrayList<>();
        String indexNamePattern = table.getName() + ".X__";
        File[] fileList = currentSchema.listFiles(new SecondaryIndexFilter(connectionInfo.getLocale(), indexNamePattern));

        if (fileList != null) {
            for (final File file : fileList) {
                final ParadoxIndex index = loadHeader(file, connectionInfo);
                index.setTable(table);
                indexes.add(index);
            }
        }

        return indexes;
    }
}
