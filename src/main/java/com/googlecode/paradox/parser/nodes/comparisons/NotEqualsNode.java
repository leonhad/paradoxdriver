package com.googlecode.paradox.parser.nodes.comparisons;

import com.googlecode.paradox.parser.nodes.FieldNode;

/**
 * Stores the not equals node.
 * 
 * @author Leonardo Alves da Costa
 * @since 1.1
 * @version 1.0
 */
public class NotEqualsNode extends AbstractComparisonNode {

    /**
     * Create a new instance.
     * 
     * @param first
     *            the first node.
     * @param last
     *            the last node.
     */
    public NotEqualsNode(final FieldNode first, final FieldNode last) {
        super("<>", first, last);
    }
}
