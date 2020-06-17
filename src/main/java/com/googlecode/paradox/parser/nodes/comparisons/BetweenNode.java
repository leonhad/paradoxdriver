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
package com.googlecode.paradox.parser.nodes.comparisons;

import com.googlecode.paradox.parser.nodes.FieldNode;

/**
 * Stores the between node.
 *
 * @author Leonardo Alves da Costa
 * @version 1.1
 * @since 1.1
 */
public final class BetweenNode extends AbstractComparisonNode {
    
    /**
     * The field node.
     */
    private final FieldNode field;
    
    /**
     * Create a new instance.
     *
     * @param first
     *            the first node.
     * @param field
     *            the middle node.
     * @param last
     *            the last node.
     */
    public BetweenNode(final FieldNode field, final FieldNode first, final FieldNode last) {
        super("BETWEEN", first, last);
        this.field = field;
    }
    
    /**
     * Gets the field node.
     *
     * @return the field node.
     */
    public FieldNode getField() {
        return this.field;
    }
    
    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        return this.field + " BETWEEN " + this.getFirst() + " AND " + this.getLast();
    }
    
}
