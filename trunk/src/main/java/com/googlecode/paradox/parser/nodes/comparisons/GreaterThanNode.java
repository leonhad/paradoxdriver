package com.googlecode.paradox.parser.nodes.comparisons;

public class GreaterThanNode extends AbstractComparisonNode {

	public GreaterThanNode(final String name, final String value) {
		super(name, value);
	}

	@Override
	public String toString() {
		return getName() + " > " + getValue();
	}
}
