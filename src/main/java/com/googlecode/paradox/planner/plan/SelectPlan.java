package com.googlecode.paradox.planner.plan;

import java.sql.SQLException;
import java.util.ArrayList;

import com.googlecode.paradox.planner.nodes.PlanTableNode;

public class SelectPlan implements Plan {

	private final ArrayList<PlanTableNode> tables = new ArrayList<PlanTableNode>();

	@Override
	public void execute() throws SQLException {
		// final ArrayList<ArrayList<FieldValue>> values = TableData.loadData(conn, from.get(0).getTable(), fieldList);
	}

	public ArrayList<PlanTableNode> getTables() {
		return tables;
	}

	public void addTable(final PlanTableNode table) {
		tables.add(table);
	}

}
