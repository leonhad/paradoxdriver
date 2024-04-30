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

import java.util.Objects;

/**
 * Stores a parameter for the statements.
 *
 * @version 1.0
 * @since 1.6.0
 */
public class ParameterNode extends FieldNode {

    /**
     * The parameter index.
     */
    private final int parameterIndex;

    /**
     * Stores field values (from select statements).
     *
     * @param parameterIndex the parameter index.
     * @param position       the current Scanner position.
     */
    public ParameterNode(final int parameterIndex, final ScannerPosition position) {
        super(null, "?", position);
        this.parameterIndex = parameterIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ParameterNode that = (ParameterNode) o;
        return parameterIndex == that.parameterIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), parameterIndex);
    }

    /**
     * Get the parameter index.
     *
     * @return the parameter index.
     */
    public int getParameterIndex() {
        return parameterIndex;
    }

    /**
     * Gets the parameter value.
     *
     * @param values the value list.
     * @return the parameter value.
     */
    public Object getValue(final Object[] values) {
        return values[parameterIndex];
    }

    @Override
    public String toString() {
        if (alias != null && !"?".equals(alias)) {
            return "? as " + alias;
        }

        return "?";
    }
}
