package com.googlecode.paradox.parser.nodes.comparisons;

public class EqualsNode extends AbstractComparisonNode {

	public EqualsNode(final String name, final String value) {
		super(name, value);
	}

	@Override
	public String toString() {
		return getName() + " = " + getValue();
	}
}
