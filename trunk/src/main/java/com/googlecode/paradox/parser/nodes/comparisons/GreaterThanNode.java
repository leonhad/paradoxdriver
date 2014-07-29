package com.googlecode.paradox.parser.nodes.comparisons;

import com.googlecode.paradox.parser.nodes.FieldNode;

public class GreaterThanNode extends AbstractComparisonNode {

	public GreaterThanNode(final FieldNode first, final FieldNode last) {
		super(">", first, last);
	}

}
