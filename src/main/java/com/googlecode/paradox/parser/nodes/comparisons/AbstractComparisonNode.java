package com.googlecode.paradox.parser.nodes.comparisons;

import com.googlecode.paradox.parser.nodes.FieldNode;
import com.googlecode.paradox.parser.nodes.SQLNode;

public abstract class AbstractComparisonNode extends SQLNode implements IComparision {

	private final FieldNode first;
	private final FieldNode last;

	public AbstractComparisonNode(final String name, final FieldNode first, final FieldNode last) {
		super(name);
		this.first = first;
		this.last = last;
	}

	public FieldNode getFirst() {
		return first;
	}

	public FieldNode getLast() {
		return last;
	}

	@Override
	public String toString() {
		return first + " " + getName() + " " + last;
	}
}
