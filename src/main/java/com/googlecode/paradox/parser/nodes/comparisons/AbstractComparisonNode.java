package com.googlecode.paradox.parser.nodes.comparisons;

import com.googlecode.paradox.parser.nodes.SQLNode;

public abstract class AbstractComparisonNode extends SQLNode {

	private final String value;

	public AbstractComparisonNode(final String name, final String value) {
		super(name);
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
