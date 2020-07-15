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
import com.googlecode.paradox.parser.ScannerPosition;
import com.googlecode.paradox.utils.Utils;

/**
 * Stores a table node.
 *
 * @version 1.5
 * @since 1.0
 */
public class TableNode extends SQLNode {

    /**
     * Table schema.
     */
    private final String schemaName;

    /**
     * Create a new instance.
     *
     * @param connection the Paradox connection.
     * @param schemaName the schema name.
     * @param name       the table name.
     * @param alias      the table alias.
     * @param position   the current Scanner position.
     */
    public TableNode(final ParadoxConnection connection, final String schemaName, final String name,
                     final String alias, ScannerPosition position) {
        super(connection, Utils.removeSuffix(name, "DB"), alias, position);

        this.schemaName = schemaName;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        if (schemaName != null) {
            builder.append(schemaName);
            builder.append(".");
        }
        builder.append(this.getName());
        if (!this.getName().equals(this.alias)) {
            builder.append(" AS ");
            builder.append(this.alias);
        }

        return builder.toString();
    }

    /**
     * Gets the schema name.
     *
     * @return the schema name.
     */
    public String getSchemaName() {
        return schemaName;
    }
}
