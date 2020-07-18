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
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.planner.nodes.FieldUtils;
import com.googlecode.paradox.planner.nodes.PlanTableNode;
import com.googlecode.paradox.results.Column;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Stores a abstract comparable node.
 *
 * @version 1.6
 * @since 1.1
 */
public abstract class AbstractConditionalNode extends SQLNode {

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

    public abstract boolean evaluate(final ParadoxConnection connection, final Object[] row, final Object[] parameters);

    public void setFieldIndexes(final List<Column> columns, final List<PlanTableNode> tables) throws SQLException {
        FieldUtils.getIndex(field, columns, tables);
    }

    @Override
    public Set<FieldNode> getClauseFields() {
        final Set<FieldNode> set = new HashSet<>();
        if (field != null) {
            set.add(field);
        }
        set.addAll(super.getClauseFields());
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
}
