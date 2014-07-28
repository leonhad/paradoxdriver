package com.googlecode.paradox.parser.nodes;

import java.util.ArrayList;
import java.util.Arrays;

public class SelectNode extends StatementNode {

	private boolean distinct;
	private final ArrayList<FieldNode> fields = new ArrayList<FieldNode>();
	private final ArrayList<TableNode> tables = new ArrayList<TableNode>();
	private final ArrayList<IdentifierNode> groups = new ArrayList<IdentifierNode>();
	private final ArrayList<IdentifierNode> order = new ArrayList<IdentifierNode>();
	private ArrayList<SQLNode> conditions;

	public SelectNode() {
		super("SELECT");
	}

	@Override
	public String toString() {
		return getName() + " " + Arrays.deepToString(fields.toArray()) + " FROM " + Arrays.deepToString(tables.toArray()) + " WHERE " + Arrays.deepToString(conditions.toArray()) + " GROUP BY "
				+ Arrays.deepToString(groups.toArray()) + " ORDER BY " + Arrays.deepToString(order.toArray());
	}

	public boolean isDistinct() {
		return distinct;
	}

	public void setDistinct(final boolean distinct) {
		this.distinct = distinct;
	}

	public void addField(final FieldNode field) {
		fields.add(field);
	}

	public void addTable(final TableNode table) {
		tables.add(table);
	}

	public void addGroupBy(final IdentifierNode indentifier) {
		groups.add(indentifier);
	}

	public void addOrderBy(final IdentifierNode indentifier) {
		order.add(indentifier);
	}

	public void setConditions(final ArrayList<SQLNode> conditions) {
		this.conditions = conditions;
	}

	public ArrayList<FieldNode> getFields() {
		return fields;
	}

	public ArrayList<TableNode> getTables() {
		return tables;
	}

	public ArrayList<IdentifierNode> getGroups() {
		return groups;
	}

	public ArrayList<IdentifierNode> getOrder() {
		return order;
	}

	public ArrayList<SQLNode> getConditions() {
		return conditions;
	}
}
