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
package com.googlecode.paradox.planner.nodes;

import com.googlecode.paradox.parser.ScannerPosition;
import com.googlecode.paradox.results.ParadoxType;

import java.util.Objects;

/**
 * Stores a node value.
 *
 * @version 1.4
 * @since 1.6.0
 */
public class ValueNode extends FieldNode {

    /**
     * The SQL type.
     */
    private final ParadoxType type;

    public ValueNode(final String name, final ScannerPosition position, final ParadoxType type) {
        super(null, name, position);
        this.type = type;
    }

    /**
     * Gets the field type.
     *
     * @return the field type.
     */
    public ParadoxType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ValueNode fieldNode = (ValueNode) o;
        return Objects.equals(type, fieldNode.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableName, alias, name, type);
    }

    @Override
    public String toString() {
        if (type == ParadoxType.VARCHAR) {
            return String.format("'%s'", this.name);
        }

        return this.name;
    }
}
