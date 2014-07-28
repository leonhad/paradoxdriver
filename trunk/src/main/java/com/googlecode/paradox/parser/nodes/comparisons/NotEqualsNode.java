package com.googlecode.paradox.parser.nodes.comparisons;

public class NotEqualsNode extends AbstractComparisonNode {

	public NotEqualsNode(final String name, final String value) {
		super(name, value);
	}

	@Override
	public String toString() {
		return getName() + " <> " + getValue();
	}
}
