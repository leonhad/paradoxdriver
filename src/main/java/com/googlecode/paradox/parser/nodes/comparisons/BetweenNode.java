package com.googlecode.paradox.parser.nodes.comparisons;

import com.googlecode.paradox.parser.nodes.FieldNode;

public class BetweenNode extends AbstractComparisonNode {

	private final FieldNode middle;

	public BetweenNode(final FieldNode first, final FieldNode middle, final FieldNode last) {
		super("BETWEEN", first, last);
		this.middle = middle;
	}

	@Override
	public String toString() {
		return middle + " BETWEEN " + getFirst() + " AND " + getLast();
	}

	public FieldNode getMiddle() {
		return middle;
	}

}
