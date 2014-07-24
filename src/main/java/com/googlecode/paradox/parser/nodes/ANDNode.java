package com.googlecode.paradox.parser.nodes;

public class ANDNode extends SQLNode {

	private final SQLNode child;

	public ANDNode(final SQLNode child) {
		super("AND");
		this.child = child;
	}

	public SQLNode getChild() {
		return child;
	}

}
