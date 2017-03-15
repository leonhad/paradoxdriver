/*
 * TableNode.java 03/12/2009 Copyright (C) 2009 Leonardo Alves da Costa This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.parser.nodes;

import com.googlecode.paradox.utils.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores a table node.
 *
 * @author Leonardo Alves da Costa
 * @version 1.1
 * @since 1.0
 */
public final class TableNode extends SQLNode {

    /**
     * The table joins.
     */
    private final List<JoinNode> joins = new ArrayList<>();

    /**
     * Create a new instance.
     *
     * @param name
     *            the table name.
     * @param alias
     *            the table alias.
     */
    public TableNode(final String name, final String alias) {
        super(Utils.removeDb(name), alias);
    }

    /**
     * Adds the join table.
     *
     * @param join
     *            the join table.
     */
    public void addJoin(final JoinNode join) {
        joins.add(join);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getName());
        if (!getName().equals(alias)) {
            builder.append(" AS ");
            builder.append(alias);
        }
        for (final JoinNode join : joins) {
            builder.append(' ');
            builder.append(join);
        }
        return builder.toString();
    }

    /**
     * Gets the join tables.
     *
     * @return the join tables.
     */
    List<JoinNode> getJoins() {
        return Collections.unmodifiableList(joins);
    }

}
