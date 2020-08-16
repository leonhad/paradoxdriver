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
import java.util.HashSet;
import java.util.Set;

/**
 * Stores index data.
 *
 * @version 1.3
 * @since 1.0
 */
public final class ParadoxIndex extends ParadoxDataFile implements Index {

    /**
     * Field order ID.
     */
    private String sortOrderID;

    /**
     * Paradox table.
     */
    private final Table table;

    /**
     * Creates a new instance.
     *
     * @param file           the file to read of.
     * @param connectionInfo the connection information.
     * @param table          the index original table.
     */
    public ParadoxIndex(final File file, final ConnectionInfo connectionInfo, final Table table) {
        super(file, connectionInfo);

        this.table = table;
    }

    /**
     * Gets the index order.
     *
     * @return the index order.
     */
    @Override
    public String getOrder() {
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
        final Set<Field> tableFields = new HashSet<>(Arrays.asList(table.getFields()));
        return Arrays.stream(fields).filter(tableFields::remove).toArray(Field[]::new);
    }

    /**
     * Gets the sorter order id.
     *
     * @return the sorter order id.
     */
    public String getSortOrderID() {
        return this.sortOrderID;
    }

    /**
     * Sets the sort order ID.
     *
     * @param sortOrderID the sort order ID to set.
     */
    public void setSortOrderID(final String sortOrderID) {
        this.sortOrderID = sortOrderID;
    }
}
