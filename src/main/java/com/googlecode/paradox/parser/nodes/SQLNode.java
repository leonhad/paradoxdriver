package com.googlecode.paradox.parser.nodes;

import java.util.ArrayList;

public class SQLNode {

	private final String name;
	private ArrayList<SQLNode> children;

	public SQLNode(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public ArrayList<SQLNode> getChildren() {
		return children;
	}

	public void setChildren(final ArrayList<SQLNode> children) {
		this.children = children;
	}

}
