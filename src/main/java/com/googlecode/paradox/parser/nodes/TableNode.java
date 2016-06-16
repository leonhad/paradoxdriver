package com.googlecode.paradox.parser.nodes;

import com.googlecode.paradox.utils.StringUtils;

import java.util.ArrayList;

public class TableNode extends SQLNode {

	private String alias;
	private final ArrayList<JoinNode> joins = new ArrayList<JoinNode>();

	public TableNode(final String name, final String alias) {
		super(StringUtils.removeDb(name));
		this.alias = alias;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append(getName());
		if (!getName().equals(alias)) {
			builder.append(" AS ");
			builder.append(alias);
		}
		for (final JoinNode join : joins) {
			builder.append(" ");
			builder.append(join);
		}
		return builder.toString();
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
