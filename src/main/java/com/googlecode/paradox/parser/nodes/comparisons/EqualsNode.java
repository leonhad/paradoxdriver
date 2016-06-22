package com.googlecode.paradox.parser.nodes.comparisons;

import com.googlecode.paradox.parser.nodes.FieldNode;

public class EqualsNode extends AbstractComparisonNode {

    public EqualsNode(final FieldNode first, final FieldNode last) {
        super("=", first, last);
    }

}
