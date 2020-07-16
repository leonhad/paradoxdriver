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

import java.util.ArrayList;
import java.util.List;

/**
 * Stores a function node.
 *
 * @version 1.0
 * @since 1.6.0
 */
public class FunctionNode extends SQLNode {

    /**
     * The list of parameters of this function.
     */
    private final List<SQLNode> parameters = new ArrayList<>();

    /**
     * Creates a new instance.
     *
     * @param name     the function name.
     * @param position the current Scanner position.
     */
    public FunctionNode(final String name, final ScannerPosition position) {
        super(name, position);
    }

    /**
     * Return The list of parameters of this function.
     *
     * @return The list of parameters of this function.
     */
    public List<SQLNode> getParameters() {
        return parameters;
    }

    /**
     * Add a new parameter to this function.
     *
     * @param parameter the parameter to add.
     */
    public void addParameter(final SQLNode parameter) {
        this.parameters.add(parameter);
    }

    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(name);
        buffer.append("(");

        for (int i = 0; i < parameters.size(); i++) {
            if (i != 0) {
                buffer.append(", ");
            }

            buffer.append(parameters.get(i));
        }

        buffer.append(")");
        return buffer.toString();
    }
}
