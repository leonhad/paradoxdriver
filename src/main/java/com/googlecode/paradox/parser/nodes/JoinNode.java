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

/**
 * Stores a join node.
 *
 * @version 1.5
 * @since 1.0
 */
public final class JoinNode extends TableNode {

    /**
     * The condition list.
     */
    private AbstractConditionalNode condition;

    /**
     * The join type.
     */
    private final JoinType joinType;

    /**
     * Create a new instance.
     *
     * @param connection the Paradox connection.
     * @param schemaName the schema name.
     * @param name       the table name.
     * @param alias      the table alias.
     * @param joinType   the table join type.
     * @param position   the current Scanner position.
     */
    public JoinNode(final ParadoxConnection connection, final String schemaName, final String name,
                    final String alias, final JoinType joinType, final ScannerPosition position) {
        super(connection, schemaName, name, alias, position);
        this.joinType = joinType;
    }

    /**
     * Gets the join type.
     *
     * @return the join type.
     */
    public JoinType getJoinType() {
        return this.joinType;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.joinType);
        builder.append(" JOIN ");
        builder.append(super.toString());

        if (this.condition != null) {
            builder.append(" ON ");
            builder.append(condition);
            builder.append(' ');
        }
        return builder.toString();
    }

    /**
     * Sets the condition list.
     *
     * @param condition the condition list.
     */
    public void setCondition(final AbstractConditionalNode condition) {
        this.condition = condition;
    }

    /**
     * Gets the join condition.
     *
     * @return the join condition.
     */
    public AbstractConditionalNode getCondition() {
        return condition;
    }
}
