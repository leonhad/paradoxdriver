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

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.rowset.ValuesComparator;
import com.googlecode.paradox.utils.Expressions;

/**
 * Like node.
 *
 * @version 1.0
 * @since 1.6.0
 */
public class LikeNode extends AbstractComparableNode {

    /**
     * Create a new instance.
     *
     * @param connection the Paradox connection.
     * @param field      the first node.
     * @param last       the last node.
     */
    public LikeNode(final ParadoxConnection connection, final FieldNode field, final FieldNode last) {
        super(connection, "=", field, last);
    }

    @Override
    public boolean evaluate(final Object[] row, final ValuesComparator comparator) {
        final Object value1 = getValue(row, field);
        final Object value2 = getValue(row, last);

        if (value1 == null || value2 == null) {
            return false;
        }

        return Expressions.accept(connection, (String) value1, (String) value2, true);
    }
}
