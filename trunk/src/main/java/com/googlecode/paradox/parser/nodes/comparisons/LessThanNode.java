package com.googlecode.paradox.parser.nodes.comparisons;

public class LessThanNode extends AbstractComparisonNode {

	public LessThanNode(final String name, final String value) {
		super(name, value);
	}

	@Override
	public String toString() {
		return getName() + " < " + getValue();
	}
}
