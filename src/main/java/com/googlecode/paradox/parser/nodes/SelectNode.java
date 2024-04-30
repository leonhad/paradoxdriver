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
package com.googlecode.paradox.parser.nodes;

import com.googlecode.paradox.parser.ScannerPosition;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.planner.sorting.OrderType;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores a select node.
 *
 * @version 1.4
 * @since 1.0
 */
public final class SelectNode extends StatementNode {

    /**
     * The field list (SELECT).
     */
    private final List<SQLNode> fields = new ArrayList<>();
    /**
     * Group by values.
     */
    private final List<FieldNode> groups = new ArrayList<>();
    /**
     * Order by values.
     */
    private final List<FieldNode> order = new ArrayList<>();
    /**
     * Order type.
     */
    private final List<OrderType> orderTypes = new ArrayList<>();
    /**
     * The tables in from token.
     */
    private final List<TableNode> tables = new ArrayList<>();
    /**
     * The conditions list.
     */
    private AbstractConditionalNode condition;
    /**
     * If has a distinct token.
     */
    private boolean distinct;
    /**
     * Result set limit.
     */
    private Integer limit;
    /**
     * Result set offset.
     */
    private Integer offset;

    /**
     * Create a new instance.
     *
     * @param position the current Scanner position.
     */
    public SelectNode(final ScannerPosition position) {
        super("SELECT", position);
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
    public AbstractConditionalNode getCondition() {
        return this.condition;
    }

    /**
     * Sets the condition tree.
     *
     * @param condition the condition tree.
     */
    public void setCondition(final AbstractConditionalNode condition) {
        this.condition = condition;
    }

    /**
     * Gets the order by list.
     *
     * @return the order by list.
     */
    public List<FieldNode> getOrder() {
        return this.order;
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
            for (final FieldNode group : this.groups) {
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
            for (final FieldNode identifier : this.order) {
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
            builder.append(condition);
        }
    }

    /**
     * Adds the group by identifier.
     *
     * @param fieldNode the group by identifier to add.
     */
    public void addGroupBy(final FieldNode fieldNode) {
        this.groups.add(fieldNode);
    }

    /**
     * Adds the order by identifier.
     *
     * @param identifier the order by identifier to add.
     * @param type       the order type to use.
     */
    public void addOrderBy(final FieldNode identifier, final OrderType type) {
        this.order.add(identifier);
        this.orderTypes.add(type);
    }

    /**
     * Gets the group list.
     *
     * @return the group list.
     */
    public List<FieldNode> getGroups() {
        return this.groups;
    }

    /**
     * Get if this select has a distinct token.
     *
     * @return true if this select has a distinct token.
     */
    public boolean isDistinct() {
        return this.distinct;
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
     * Gets the order types.
     *
     * @return the order types.
     */
    public List<OrderType> getOrderTypes() {
        return orderTypes;
    }

    /**
     * Gets the result set limit.
     *
     * @return the result set limit.
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * Sets the result set limit.
     *
     * @param limit the result set limit.
     */
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * Gets the result set offset.
     *
     * @return the result set offset.
     */
    public Integer getOffset() {
        return offset;
    }

    /**
     * Sets the result set offset.
     *
     * @param offset the result set offset.
     */
    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}
