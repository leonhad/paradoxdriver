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
import com.googlecode.paradox.parser.nodes.comparable.AbstractComparableNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores a select node.
 *
 * @author Leonardo Alves da Costa
 * @version 1.1
 * @since 1.0
 */
public final class SelectNode extends StatementNode {

    /**
     * The conditions list.
     */
    private AbstractComparableNode condition;
    /**
     * If has a distinct token.
     */
    private boolean distinct;
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
     * Create a new instance.
     *
     * @param connection the Paradox connection.
     */
    public SelectNode(final ParadoxConnection connection) {
        super(connection, "SELECT");
    }

    /**
     * Adds the field in the list.
     *
     * @param field the field to add.
     */
    public void addField(final SQLNode field) {
        this.fields.add(field);
    }

    /**
     * Adds the table in list.
     *
     * @param table the table to add
     */
    public void addTable(final TableNode table) {
        this.tables.add(table);
    }

    /**
     * Gets the condition list.
     *
     * @return the condition list.
     */
    public AbstractComparableNode getCondition() {
        return this.condition;
    }

    /**
     * Sets the condition tree.
     *
     * @param condition the condition tree.
     */
    public void setCondition(final AbstractComparableNode condition) {
        this.condition = condition;
    }

    /**
     * Gets the order by list.
     *
     * @return the order by list.
     */
    public List<IdentifierNode> getOrder() {
        return Collections.unmodifiableList(this.order);
    }

    /**
     * Gets the field list.
     *
     * @return the field list.
     */
    public List<SQLNode> getFields() {
        return this.fields;
    }

    /**
     * Gets the table list.
     *
     * @return the table list.
     */
    public List<TableNode> getTables() {
        return this.tables;
    }

    /**
     * Sets the distinct key present.
     *
     * @param distinct the distinct key present.
     */
    public void setDistinct(final boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * Show this node SELECT.
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.getName());
        builder.append(' ');

        this.buildFields(builder);
        this.buildFrom(builder);
        this.buildWhere(builder);
        this.buildGroupBy(builder);
        this.buildOrderBy(builder);
        return builder.toString();
    }

    /**
     * Build the fields description.
     *
     * @param builder builder to append fields.
     */
    private void buildFields(final StringBuilder builder) {
        boolean first = true;
        for (final SQLNode field : this.fields) {
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
     * @param builder builder to append FROM tables.
     */
    private void buildFrom(final StringBuilder builder) {
        boolean first;
        if (!this.tables.isEmpty()) {
            builder.append(" FROM ");
            first = true;
            for (final TableNode table : this.tables) {
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
     * @param builder builder to GROUP BY fields.
     */
    private void buildGroupBy(final StringBuilder builder) {
        boolean first;
        if (!this.groups.isEmpty()) {
            builder.append(" GROUP BY ");
            first = true;
            for (final IdentifierNode group : this.groups) {
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
     * @param builder builder to ORDER BY fields.
     */
    private void buildOrderBy(final StringBuilder builder) {
        boolean first;
        if (!this.order.isEmpty()) {
            builder.append(" ORDER BY ");
            first = true;
            for (final IdentifierNode identifier : this.order) {
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
     * @param builder builder to WHERE conditions.
     */
    private void buildWhere(final StringBuilder builder) {
        if (this.condition != null) {
            builder.append(" WHERE ");
            builder.append(' ');
            builder.append(condition);
        }
    }

    /**
     * Adds the group by identifier.
     *
     * @param identifier the group by identifier to add.
     */
    void addGroupBy(final IdentifierNode identifier) {
        this.groups.add(identifier);
    }

    /**
     * Adds the order by identifier.
     *
     * @param identifier the order by identifier to add.
     */
    void addOrderBy(final IdentifierNode identifier) {
        this.order.add(identifier);
    }

    /**
     * Gets the group list.
     *
     * @return the group list.
     */
    List<IdentifierNode> getGroups() {
        return Collections.unmodifiableList(this.groups);
    }

    /**
     * Get if this select has a distinct token.
     *
     * @return true if this select has a distinct token.
     */
    boolean isDistinct() {
        return this.distinct;
    }
}
