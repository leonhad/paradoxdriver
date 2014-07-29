package com.googlecode.paradox.parser.nodes.comparisons;

import com.googlecode.paradox.parser.nodes.FieldNode;

public class LessThanNode extends AbstractComparisonNode {

	public LessThanNode(final FieldNode first, final FieldNode last) {
		super("<", first, last);
	}

}
