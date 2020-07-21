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
package com.googlecode.paradox.planner.nodes.comparable;

import com.googlecode.paradox.parser.ScannerPosition;
import com.googlecode.paradox.parser.nodes.AbstractConditionalNode;
import com.googlecode.paradox.planner.FieldValueUtils;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.planner.nodes.PlanTableNode;
import com.googlecode.paradox.results.Column;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * Comparable node common code.
 *
 * @version 1.5
 * @since 1.6.0
 */
public abstract class AbstractComparableNode extends AbstractConditionalNode {

    /**
     * The last node.
     */
    protected final FieldNode last;

    /**
     * Creates a new instance.
     *
     * @param name     the node name.
     * @param field    the field node.
     * @param last     the last field node.
     * @param position the current Scanner position.
     */
    public AbstractComparableNode(final String name, final FieldNode field, final FieldNode last,
                                  final ScannerPosition position) {
        super(name, field, position);
        this.last = last;
    }

    /**
     * Gets the last field value.
     *
     * @return the last field value.
     */
    public final FieldNode getLast() {
        return last;
    }

    @Override
    public Set<FieldNode> getClauseFields() {
        final Set<FieldNode> nodes = super.getClauseFields();
        if (last != null) {
            nodes.add(last);
        }

        return nodes;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", field, name, last);
    }

    @Override
    public void setFieldIndexes(final List<Column> columns, final List<PlanTableNode> tables) throws SQLException {
        super.setFieldIndexes(columns, tables);
        FieldValueUtils.setFieldIndex(last, columns, tables);
    }
}
