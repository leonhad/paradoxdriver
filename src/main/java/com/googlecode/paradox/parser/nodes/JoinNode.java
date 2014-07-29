package com.googlecode.paradox.parser.nodes;

import java.util.ArrayList;

import com.googlecode.paradox.parser.nodes.comparisons.IComparision;

public class JoinNode extends SQLNode {

	private JoinType type = JoinType.CROSS_JOIN;
	private String tableName;
	private String tableAlias;
	private ArrayList<IComparision> conditions;

	public JoinNode() {
		super("JOIN");
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append(type);
		builder.append(" JOIN ");
		builder.append(tableName);
		if (!tableName.equals(tableAlias)) {
			builder.append(" AS ");
			builder.append(tableAlias);
		}
		builder.append(" ON ");
		for (final IComparision condition : conditions) {
			builder.append(condition);
			builder.append(" ");
		}
		return builder.toString();
	}

	public String getTableAlias() {
		return tableAlias;
	}

	public void setTableAlias(final String tableAlias) {
		this.tableAlias = tableAlias;
	}

	public JoinType getType() {
		return type;
	}

	public void setType(final JoinType type) {
		this.type = type;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(final String tableName) {
		this.tableName = tableName;
	}

	public ArrayList<IComparision> getConditions() {
		return conditions;
	}

	public void setConditions(final ArrayList<IComparision> conditions) {
		this.conditions = conditions;
	}

}
