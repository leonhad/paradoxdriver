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
import com.googlecode.paradox.planner.FieldValueUtils;
import com.googlecode.paradox.planner.context.Context;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.Column;

import java.sql.SQLException;
import java.util.List;

/**
 * Is not null node.
 *
 * @version 1.6
 * @since 1.6.0
 */
public class IsNotNullNode extends AbstractComparableNode {

    /**
     * Create a new instance.
     *
     * @param field    the first node.
     * @param position the current Scanner position.
     */
    public IsNotNullNode(final FieldNode field, final ScannerPosition position) {
        super("IS NOT", field, null, position);
    }

    @Override
    public boolean evaluate(final Context context, final Object[] row, final List<Column> columnsLoaded)
            throws SQLException {
        final Object value1 = FieldValueUtils.getValue(context, row, field, columnsLoaded);
        return value1 != null;
    }
}
