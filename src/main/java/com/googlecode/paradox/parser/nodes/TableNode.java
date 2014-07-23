package com.googlecode.paradox.parser.nodes;

import com.googlecode.paradox.metadata.ParadoxTable;

public class TableNode extends SQLNode {

	private ParadoxTable table;

	public TableNode(final String name) {
		super(name);
	}

	public ParadoxTable getTable() {
		return table;
	}

	public void setTable(final ParadoxTable table) {
		this.table = table;
	}
}
