package com.googlecode.paradox.parser.nodes;

import java.util.Collection;

public class SQLNode {

	private final String name;
	private Collection<? extends SQLNode> children;

	public SQLNode(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public Collection<? extends SQLNode> getChildren() {
		return children;
	}

	public void setChildren(final Collection<? extends SQLNode> children) {
		this.children = children;
	}

}
