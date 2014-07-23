package com.googlecode.paradox.parser.nodes;

public abstract class SQLNode {

	private final String name;

	public SQLNode(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
