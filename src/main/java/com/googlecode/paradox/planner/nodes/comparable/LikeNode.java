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
import com.googlecode.paradox.parser.ScannerPosition;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.planner.nodes.FieldUtils;
import com.googlecode.paradox.utils.Expressions;

/**
 * Like node.
 *
 * @version 1.4
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
    public boolean evaluate(final ParadoxConnection connection, final Object[] row, final Object[] parameters) {
        final Object value1 = FieldUtils.getValue(row, field, parameters);
        final Object value2 = FieldUtils.getValue(row, last, parameters);

        if (value1 == null || value2 == null) {
            return false;
        }

        return Expressions.accept(connection.getLocale(), (String) value1, (String) value2, true, escape);
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
