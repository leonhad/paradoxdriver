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

/**
 * Stores a statement node.
 *
 * @version 1.4
 * @since 1.0
 */
public class StatementNode extends SQLNode {

    /**
     * The statement parameters count.
     */
    private int parameterCount;

    /**
     * Create a new instance.
     *
     * @param name     the statement name.
     * @param position the current Scanner position.
     */
    public StatementNode(final String name, final ScannerPosition position) {
        super(name, position);
    }

    /**
     * Gets the parameter count.
     *
     * @return the parameter count.
     */
    public int getParameterCount() {
        return parameterCount;
    }

    /**
     * Sets the parameter count.
     *
     * @param parameterCount the parameter count.
     */
    public void setParameterCount(int parameterCount) {
        this.parameterCount = parameterCount;
    }
}
