package com.googlecode.paradox.parser.nodes;


public class TableNode extends SQLNode {

	private String alias;

	public TableNode(final String name, final String alias) {
		super(name);
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(final String alias) {
		this.alias = alias;
	}

}
