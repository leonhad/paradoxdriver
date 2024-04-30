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
package com.googlecode.paradox.planner.nodes.comparable;

import com.googlecode.paradox.parser.ScannerPosition;
import com.googlecode.paradox.planner.FieldValueUtils;
import com.googlecode.paradox.planner.context.Context;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.rowset.ValuesConverter;
import com.googlecode.paradox.utils.Expressions;

import java.sql.SQLException;
import java.util.List;

/**
 * Like node.
 *
 * @version 1.8
 * @since 1.6.0
 */
public class LikeNode extends AbstractComparableNode {

    /**
     * This like escape char.
     */
    protected char escape = '\\';

    /**
     * Create a new instance.
     *
     * @param field    the first node.
     * @param last     the last node.
     * @param position the current Scanner position.
     */
    public LikeNode(final FieldNode field, final FieldNode last, final ScannerPosition position) {
        super("like", field, last, position);
    }

    @Override
    public boolean evaluate(final Context context, final Object[] row, final List<Column> columnsLoaded)
            throws SQLException {
        final Object value1 = FieldValueUtils.getValue(context, row, field, columnsLoaded);
        final Object value2 = FieldValueUtils.getValue(context, row, last, columnsLoaded);

        if (value1 == null || value2 == null) {
            return false;
        }

        return Expressions.accept(context.getLocale(), ValuesConverter.getString(value1, context.getConnectionInfo()),
                ValuesConverter.getString(value2, context.getConnectionInfo()), true, escape);
    }

    /**
     * Gets the escape char.
     *
     * @return the escape char.
     */
    public char getEscape() {
        return escape;
    }

    /**
     * Sets the escape char.
     *
     * @param escape the escape char.
     */
    public void setEscape(char escape) {
        this.escape = escape;
    }
}
