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
package com.googlecode.paradox.metadata.paradox;

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.metadata.Field;
import com.googlecode.paradox.metadata.Index;
import com.googlecode.paradox.metadata.Table;

import java.io.File;
import java.util.Arrays;

/**
 * Stores index data.
 *
 * @version 1.4
 * @since 1.0
 */
public final class ParadoxIndex extends ParadoxDataFile implements Index {

    /**
     * Creates a new instance.
     *
     * @param file           the file to read of.
     * @param connectionInfo the connection information.
     */
    public ParadoxIndex(final File file, final ConnectionInfo connectionInfo) {
        super(file, connectionInfo);
    }

    /**
     * Gets the index order.
     *
     * @return the index order.
     */
    @Override
    public String getOrder() {
        // FIXME use enum.
        final int referential = this.getReferentialIntegrity();
        if ((referential == 0x10) || (referential == 0x11) || (referential == 0x30)) {
            return "D";
        }

        return "A";
    }

    @Override
    public boolean isUnique() {
        final int referential = this.getReferentialIntegrity();
        return (referential == 0x20) || (referential == 0x21) || (referential == 0x30);
    }

    @Override
    public Field[] getFields() {
        return fields;
    }

    /**
     * Sets the index table.
     *
     * @param table the table to set.
     */
    public void setTable(Table table) {
        Arrays.stream(fields).forEach(field -> field.setTable(table));
    }
}
