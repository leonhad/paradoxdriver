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
import com.googlecode.paradox.planner.nodes.FieldNode;

import java.util.HashSet;
import java.util.Set;

/**
 * Stores a SQL node.
 *
 * @version 1.7
 * @since 1.0
 */
public class SQLNode {

    /**
     * SQL position.
     */
    private final ScannerPosition position;
    /**
     * The node alias.
     */
    protected String alias;
    /**
     * The node name.
     */
    protected String name;

    /**
     * Create a new instance.
     *
     * @param name     the node name.
     * @param position the current Scanner position.
     */
    protected SQLNode(final String name, final ScannerPosition position) {
        this.name = name;
        this.alias = name;
        this.position = position;
    }

    /**
     * Gets the clause fields.
     * @return the clause fields set.
     */
    public Set<FieldNode> getClauseFields() {
        return new HashSet<>();
    }

    /**
     * Gets the node alias.
     *
     * @return the node alias.
     */
    public String getAlias() {
        return this.alias;
    }

    /**
     * Sets the node alias.
     *
     * @param alias the alias name.
     */
    public final void setAlias(final String alias) {
        this.alias = alias;
    }

    /**
     * Gets the node name.
     *
     * @return the node name.
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Gets the scanner position.
     *
     * @return the scanner position.
     */
    public ScannerPosition getPosition() {
        return position;
    }
}
