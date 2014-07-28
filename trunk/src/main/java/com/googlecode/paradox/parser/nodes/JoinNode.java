package com.googlecode.paradox.parser.nodes;

import java.util.ArrayList;
import java.util.Arrays;

public class JoinNode extends SQLNode {

	private JoinType type = JoinType.CROSS_JOIN;
	private String tableName;
	private String tableAlias;
	private ArrayList<SQLNode> conditions;

	public JoinNode() {
		super("JOIN");
	}

	@Override
	public String toString() {
		return "JOIN " + tableName + " AS " + tableAlias + " ON " + Arrays.deepToString(conditions.toArray());
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

	public ArrayList<SQLNode> getConditions() {
		return conditions;
	}

	public void setConditions(final ArrayList<SQLNode> conditions) {
		this.conditions = conditions;
	}

}
