package com.googlecode.paradox.parser.nodes.comparisons;

import com.googlecode.paradox.parser.nodes.FieldNode;

public class NotEqualsNode extends AbstractComparisonNode {

    public NotEqualsNode(final FieldNode first, final FieldNode last) {
        super("<>", first, last);
    }
}
