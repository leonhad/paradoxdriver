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

import com.googlecode.paradox.parser.ScannerPosition;
import com.googlecode.paradox.parser.TokenType;
import com.googlecode.paradox.planner.nodes.FieldNode;

/**
 * Stores the asterisk node.
 *
 * @version 1.4
 * @since 1.2
 */
public final class AsteriskNode extends FieldNode {

    /**
     * Creates a new instance.
     *
     * @param tableName the table name.
     * @param position  the current Scanner position.
     */
    public AsteriskNode(final String tableName, final ScannerPosition position) {
        super(tableName, TokenType.ASTERISK.name(), position);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        if (this.tableName != null) {
            builder.append(this.tableName);
            builder.append('.');
        }
        builder.append("*");
        return builder.toString();
    }
}
