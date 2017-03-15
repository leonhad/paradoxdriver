/*
 * AbstractComparisonNode.java 03/12/2009 Copyright (C) 2009 Leonardo Alves da Costa This program is free software: you
 * can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should
 * have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.parser.nodes.comparisons;

import com.googlecode.paradox.parser.nodes.FieldNode;
import com.googlecode.paradox.parser.nodes.SQLNode;

/**
 * Stores a abstract comparision node.
 *
 * @author Leonardo Alves da Costa
 * @version 1.1
 * @since 1.1
 */
abstract class AbstractComparisonNode extends SQLNode {

    /**
     * The first node.
     */
    private final FieldNode first;

    /**
     * The last node.
     */
    private final FieldNode last;

    /**
     * Creates a new instance.
     *
     * @param name
     *            the condition name.
     * @param first
     *            the first node.
     * @param last
     *            the last node.
     */
    AbstractComparisonNode(final String name, final FieldNode first, final FieldNode last) {
        super(name);
        this.first = first;
        this.last = last;
    }

    /**
     * Gets the first node.
     *
     * @return the first node.
     */
    public FieldNode getFirst() {
        return first;
    }

    /**
     * Gets the last node.
     *
     * @return the last node.
     */
    public FieldNode getLast() {
        return last;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        return first + " " + getName() + " " + last;
    }
}
