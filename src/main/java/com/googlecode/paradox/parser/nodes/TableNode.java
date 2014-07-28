package com.googlecode.paradox.parser.nodes;

import java.util.ArrayList;
import java.util.Arrays;

public class TableNode extends SQLNode {

	private String alias;
	private final ArrayList<JoinNode> joins = new ArrayList<JoinNode>();

	public TableNode(final String name, final String alias) {
		super(name);
		this.alias = alias;
	}

	@Override
	public String toString() {
		return getName() + " as " + alias + " " + Arrays.deepToString(joins.toArray());
	};

	public String getAlias() {
		return alias;
	}

	public ArrayList<JoinNode> getJoins() {
		return joins;
	}

	public void addJoin(final JoinNode join) {
		joins.add(join);
	}

	public void setAlias(final String alias) {
		this.alias = alias;
	}

}
