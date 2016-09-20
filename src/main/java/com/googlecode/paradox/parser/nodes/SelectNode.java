/*
 * SelectNode.java
 *
 * 03/12/2009
 * Copyright (C) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.parser.nodes;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores a select node.
 *
 * @author Leonardo Alves da Costa
 * @version 1.1
 * @since 1.0
 */
public class SelectNode extends StatementNode {

    /**
     * The field list (SELECT).
     */
    private final ArrayList<SQLNode> fields = new ArrayList<>();
    /**
     * Group by values.
     */
    private final ArrayList<IdentifierNode> groups = new ArrayList<>();
    /**
     * Order by values.
     */
    private final ArrayList<IdentifierNode> order = new ArrayList<>();
    /**
     * The tables in from token.
     */
    private final ArrayList<TableNode> tables = new ArrayList<>();
    /**
     * The conditions list.
     */
    private List<SQLNode> conditions;
    /**
     * If has a distinct token.
     */
    private boolean distinct;

    /**
     * Create a new instance.
     */
    public SelectNode() {
        super("SELECT");
    }

    /**
     * Adds the field in the list.
     *
     * @param field
     *         the field to add.
     */
    public final void addField(final SQLNode field) {
        fields.add(field);
    }

    /**
     * Adds the group by identifier.
     *
     * @param identifier
     *         the group by identifier to add.
     */
    public final void addGroupBy(final IdentifierNode identifier) {
        groups.add(identifier);
    }

    /**
     * Adds the order by identifier.
     *
     * @param identifier
     *         the order by identifier to add.
     */
    public final void addOrderBy(final IdentifierNode identifier) {
        order.add(identifier);
    }

    /**
     * Adds the table in list.
     *
     * @param table
     *         the table to add
     */
    public final void addTable(final TableNode table) {
        tables.add(table);
    }

    /**
     * Build the fields description.
     *
     * @param builder
     *         builder to append fields.
     */
    private void buildFields(final StringBuilder builder) {
        boolean first = true;
        for (final SQLNode field : fields) {
            if (first) {
                first = false;
            } else {
                builder.append(", ");
            }
            builder.append(field);
        }
    }

    /**
     * Build the FROM description.
     *
     * @param builder
     *         builder to append FROM tables.
     */
    private void buildFrom(final StringBuilder builder) {
        boolean first;
        if (!tables.isEmpty()) {
            builder.append(" FROM ");
            first = true;
            for (final TableNode table : tables) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append(table);
            }
        }
    }

    /**
     * Build the grouping fields.
     *
     * @param builder
     *         builder to GROUP BY fields.
     */
    private void buildGroupBy(final StringBuilder builder) {
        boolean first;
        if (!groups.isEmpty()) {
            builder.append(" GROUP BY ");
            first = true;
            for (final IdentifierNode group : groups) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append(group);
            }
        }
    }

    /**
     * Build the fields order.
     *
     * @param builder
     *         builder to ORDER BY fields.
     */
    private void buildOrderBy(final StringBuilder builder) {
        boolean first;
        if (!order.isEmpty()) {
            builder.append(" ORDER BY ");
            first = true;
            for (final IdentifierNode identifier : order) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append(identifier);
            }
        }
    }

    /**
     * Build the WHERE conditions.
     *
     * @param builder
     *         builder to WHERE conditions.
     */
    private void buildWhere(final StringBuilder builder) {
        boolean first;
        if (conditions != null && !conditions.isEmpty()) {
            builder.append(" WHERE ");
            first = true;
            for (final SQLNode cond : conditions) {
                if (first) {
                    first = false;
                } else {
                    builder.append(' ');
                }
                builder.append(cond);
            }
        }
    }

    /**
     * Gets the condition list.
     *
     * @return the condition list.
     */
    public final List<SQLNode> getConditions() {
        return new ArrayList<>(conditions);
    }

    /**
     * Sets the condition list.
     *
     * @param conditions
     *         the condition list.
     */
    public final void setConditions(final List<SQLNode> conditions) {
        this.conditions = conditions;
    }

    /**
     * Gets the field list.
     *
     * @return the field list.
     */
    public final List<SQLNode> getFields() {
        return new ArrayList<>(fields);
    }

    /**
     * Gets the group list.
     *
     * @return the group list.
     */
    public final List<IdentifierNode> getGroups() {
        return new ArrayList<>(groups);
    }

    /**
     * Gets the order by list.
     *
     * @return the order by list.
     */
    public final List<IdentifierNode> getOrder() {
        return new ArrayList<>(order);
    }

    /**
     * Gets the table list.
     *
     * @return the table list.
     */
    public final List<TableNode> getTables() {
        return new ArrayList<>(tables);
    }

    /**
     * Get if this select has a distinct token.
     *
     * @return true if this select has a distinct token.
     */
    public final boolean isDistinct() {
        return distinct;
    }

    /**
     * Sets the distinct key present.
     *
     * @param distinct
     *         the distinct key present.
     */
    public final void setDistinct(final boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * Show this node SELECT.
     */
    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getName());
        builder.append(' ');

        buildFields(builder);
        buildFrom(builder);
        buildWhere(builder);
        buildGroupBy(builder);
        buildOrderBy(builder);
        return builder.toString();
    }
}
