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

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Stores a SQL node.
 *
 * @author Leonardo Alves da Costa
 * @version 1.1
 * @since 1.0
 */
public class SQLNode {

    /**
     * Node childhood.
     */
    private Collection<? extends SQLNode> childhood;

    /**
     * The node alias.
     */
    protected String alias;

    /**
     * The node name.
     */
    protected final String name;

    protected final ParadoxConnection connection;

    /**
     * Create a new instance.
     *
     * @param connection the Paradox connection.
     * @param name       the node name.
     */
    protected SQLNode(final ParadoxConnection connection, final String name) {
        this.connection = connection;
        this.name = name;
        this.alias = name;
    }

    /**
     * Create a new instance.
     *
     * @param connection the Paradox connection.
     * @param name       the node name.
     * @param alias      the node alias.
     */
    protected SQLNode(final ParadoxConnection connection, final String name, final String alias) {
        this(connection, name);
        this.alias = alias;
    }

    public Set<FieldNode> getClausuleFields() {
        return Collections.emptySet();
    }

    /**
     * Gets the node alias.
     *
     * @return the node alias.
     */
    public final String getAlias() {
        return this.alias;
    }

    /**
     * Gets the childhood.
     *
     * @return the childhood.
     */
    @SuppressWarnings("unchecked")
    public final Collection<SQLNode> getChildhood() {
        return (Collection<SQLNode>) this.childhood;
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
     * Sets the node alias.
     *
     * @param alias the alias name.
     */
    public final void setAlias(final String alias) {
        this.alias = alias;
    }

    /**
     * Sets the childhood.
     *
     * @param childhood the childhood.
     */
    public final void setChildhood(final Collection<? extends SQLNode> childhood) {
        this.childhood = Collections.unmodifiableCollection(childhood);
    }
}
