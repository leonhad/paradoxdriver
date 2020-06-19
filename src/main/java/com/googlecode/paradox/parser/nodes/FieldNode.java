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
package com.googlecode.paradox.parser.nodes;

import com.googlecode.paradox.ParadoxConnection;

/**
 * Stores the field values.
 *
 * @author Leonardo Alves da Costa
 * @version 1.1
 * @since 1.0
 */
public final class FieldNode extends SQLNode {

    /**
     * This field table name.
     */
    private final String tableName;

    /**
     * Stores field values (from select statements).
     *
     * @param connection the Paradox connection.
     * @param tableName  the table name.
     * @param fieldName  the field name.
     * @param alias      the field name alias.
     */
    public FieldNode(final ParadoxConnection connection, final String tableName, final String fieldName,
                     final String alias) {
        super(connection, fieldName, alias);
        this.tableName = tableName;
    }

    /**
     * Gets the table name.
     *
     * @return the table name.
     */
    public String getTableName() {
        return this.tableName;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        if (this.tableName != null) {
            builder.append(this.tableName);
            builder.append('.');
        }
        builder.append(this.getName());

        if ((this.alias != null) && !this.getName().equals(this.alias)) {
            builder.append(" AS ");
            builder.append(this.alias);
        }
        return builder.toString();
    }

}
