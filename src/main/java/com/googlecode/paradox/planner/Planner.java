package com.googlecode.paradox.planner;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.data.TableData;
import com.googlecode.paradox.metadata.ParadoxTable;
import com.googlecode.paradox.parser.TokenType;
import com.googlecode.paradox.parser.nodes.*;
import com.googlecode.paradox.parser.nodes.values.AsteriskNode;
import com.googlecode.paradox.planner.nodes.PlanTableNode;
import com.googlecode.paradox.planner.plan.Plan;
import com.googlecode.paradox.planner.plan.SelectPlan;
import com.googlecode.paradox.utils.SQLStates;

public class Planner {

	private final ParadoxConnection conn;

	public Planner(final ParadoxConnection conn) {
		this.conn = conn;
	}

	public Plan create(final StatementNode statement) throws SQLException {
		if (statement instanceof SelectNode) {
			return createSelect((SelectNode) statement);
		} else {
			throw new SQLFeatureNotSupportedException();
		}
	}

	private Plan createSelect(final SelectNode statement) throws SQLException {
		final SelectPlan plan = new SelectPlan(conn);
		final ArrayList<ParadoxTable> paradoxTables = TableData.listTables(conn);

		// Load the table metadata
		for (final TableNode table : statement.getTables()) {
			final PlanTableNode node = new PlanTableNode();
			for (final ParadoxTable paradoxTable : paradoxTables) {
				if (paradoxTable.getName().equalsIgnoreCase(table.getName())) {
					node.setTable(paradoxTable);
					break;
				}
			}
			if (node.getTable() == null) {
				throw new SQLException("Table " + table.getName() + " not found.", SQLStates.INVALID_SQL);
			}
			if (!table.getName().equals(table.getAlias())) {
				node.setAlias(table.getAlias());
			}
			plan.addTable(node);
		}
		for (final PlanTableNode table : plan.getTables()) {
			conn.debug("Add table '%s'", table.getTable().toString());
		}

		for (final SQLNode field: statement.getFields()) {
			String name = field.getName();
			if (field instanceof AsteriskNode) {
				for (final PlanTableNode table : plan.getTables()) {
					plan.addColumnFromTable(table.getTable());
				}
			} else {
				String alias = field.getAlias();
				if (name == null || name.isEmpty()) {
					throw new SQLException("Column name is empty");
				}
				plan.addColumn(name, alias);
			}
		}
		if (plan.getColumns().size() == 0)
			throw new SQLException("Empty column list", SQLStates.INVALID_SQL);

		return plan;
	}
}
