package com.googlecode.paradox.parser.nodes.comparisons;

import com.googlecode.paradox.parser.nodes.SQLNode;

public class BetweenNode extends SQLNode {

	private String left;
	private String right;

	public BetweenNode(final String name, final String left, final String right) {
		super(name);
		this.left = left;
		this.right = right;
	}

	@Override
	public String toString() {
		return getName() + " BETWEEN " + left + " AND " + right;
	}

	public String getLeft() {
		return left;
	}

	public void setLeft(final String left) {
		this.left = left;
	}

	public String getRight() {
		return right;
	}

	public void setRight(final String right) {
		this.right = right;
	}

}
