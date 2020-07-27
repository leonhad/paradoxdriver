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

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.parser.ScannerPosition;
import com.googlecode.paradox.planner.FieldValueUtils;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.planner.nodes.PlanTableNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Stores a abstract comparable node.
 *
 * @version 1.9
 * @since 1.1
 */
public abstract class AbstractConditionalNode extends SQLNode {

    /**
     * Node childhood.
     */
    protected final List<SQLNode> children = new ArrayList<>();

    /**
     * The field node.
     */
    protected final FieldNode field;

    /**
     * Creates a new instance.
     *
     * @param name     the condition name.
     * @param field    the field node to compare.
     * @param position the current Scanner position.
     */
    public AbstractConditionalNode(final String name, final FieldNode field, final ScannerPosition position) {
        super(name, position);
        this.field = field;
    }

    public AbstractConditionalNode(final String name, ScannerPosition position) {
        this(name, null, position);
    }

    public abstract boolean evaluate(final ConnectionInfo connectionInfo, final Object[] row, final Object[] parameters,
                                     final ParadoxType[] parameterTypes, final List<Column> columnsLoaded)
            throws SQLException;

    public void setFieldIndexes(final List<Column> columns, final List<PlanTableNode> tables) throws SQLException {
        FieldValueUtils.setFieldIndex(field, columns, tables);
    }

    @Override
    public Set<FieldNode> getClauseFields() {
        final Set<FieldNode> set = super.getClauseFields();
        if (field != null) {
            set.addAll(field.getClauseFields());
        }

        return set;
    }

    /**
     * Get the field.
     *
     * @return the field.
     */
    public FieldNode getField() {
        return field;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        return field.toString();
    }

    /**
     * Add a child node.
     *
     * @param node the child node to add.
     */
    public void addChild(final SQLNode node) {
        this.children.add(node);
    }

    /**
     * Gets the child list.
     *
     * @return the child list.
     */
    public List<SQLNode> getChildren() {
        return children;
    }
}
