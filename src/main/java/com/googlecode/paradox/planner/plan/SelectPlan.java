package com.googlecode.paradox.planner.plan;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.data.TableData;
import com.googlecode.paradox.data.table.value.FieldValue;
import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.metadata.ParadoxTable;
import com.googlecode.paradox.planner.nodes.PlanTableNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.utils.SQLStates;

public class SelectPlan implements Plan {

	private final List<PlanTableNode> tables = new ArrayList<PlanTableNode>();
	private final List<Column> columns = new ArrayList<Column>();
	private List<List<FieldValue>> values = null;
	private ParadoxConnection conn;

	public SelectPlan(ParadoxConnection conn) {
		super();
		this.conn = conn;
	}

	@Override
	public void execute() throws SQLException {
		if (columns.size() == 0 || tables.size() == 0)
			return;

		for (Column column: columns) {
			for (PlanTableNode table: tables) {
				ParadoxTable pTable = table.getTable();
				if (column.getTableName().equalsIgnoreCase(pTable.getName())) {
					try {
						ParadoxField field = pTable.findField(column.getName());
						if (field == null)
							throw new SQLException("Column '" + column.getName() + "' not found in table '" +
													pTable.getName(), SQLStates.INVALID_FIELD_VALUE);
						// load table data
						final List<List<FieldValue>> tableData = TableData.loadData(conn, pTable, pTable.getFields());
						// search column index
						if (field.getOrderNum() > tableData.size() || field.getOrderNum() < 1)
							throw new SQLException("Invalid column position", SQLStates.INVALID_FIELD_VALUE);
						if (values == null)
							values = new ArrayList<List<FieldValue>>();
						int p = field.getOrderNum() - 1;
						List<FieldValue> resultRow;
						for (int j = 0; j < tableData.size(); j++) {
							if (j == values.size()) {
								resultRow = new ArrayList<FieldValue>();
								values.add(resultRow);
							} else {
								resultRow = values.get(j);
							}
							resultRow.add(tableData.get(j).get(p));
						}
					} catch (IOException x) {
						throw new SQLException("Error in table data reading", SQLStates.LOAD_DATA, x);
					}
				}
			}
		}
	}

	/**
	 * Values from tables in column order.
	 *
	 * @return array of array of values/ Can be null (empty result set);
	 */
	public List<List<FieldValue>> getValues(){
		return values;
	}

	public List<PlanTableNode> getTables() {
		return tables;
	}

	public void addTable(final PlanTableNode table) {
		tables.add(table);
	}

	public List<Column> getColumns() {
		return columns;
	}

	/**
	 * Add column from select list.
	 *
	 * @param name column name
	 * @param alias column alias (id as key)
	 * @throws SQLException - search column exception
	 */
	public void addColumn(String name, String alias) throws SQLException{
		ParadoxField field = findField(name);
		if (field == null) {
			if (alias != null && !alias.isEmpty())
				name += " " + alias;
			throw new SQLException(String.format("Invalid column name: '%s'", name), SQLStates.INVALID_COLUMN);
		}
		columns.add(field.getColumn());
	}

	private ParadoxField findField(String name) throws SQLException{
		List<ParadoxField> fields = new ArrayList<ParadoxField>(1);
		String prefix = null;
		int p = name.indexOf('.');
		if (p > -1) {
			prefix = name.substring(0, p - 1);
			name = name.substring(p);
		}
		/*
		Find column in table list

		select a.id from table a - true
		select id from table - true
		select id from table1, table2 - exception (if id exists in table1 and table2)
		 */
		for (final PlanTableNode table: tables) {
			if (table.getTable() == null)
				throw new SQLException("Empty table", SQLStates.INVALID_TABLE);

			if (prefix != null && table.getAlias() != null && !prefix.equalsIgnoreCase(table.getAlias())) continue;

			for (final ParadoxField field: table.getTable().getFields()) {
				if (field.getName().equalsIgnoreCase(name)) {
					fields.add(field);
					// Unique column in table
					break;
				}
			}
		}
		if (fields.size() > 0) {
			if (fields.size() > 1) {
				throw new SQLException("Column '" + name + "' ambiguously defined", SQLStates.COLUMN_AMBIQUOUS);
			} else
				return fields.get(0);
		}

		return null;
	}

	public void addColumnFromTable(ParadoxTable table) throws SQLException{
		for (ParadoxField field: table.getFields()) {
			columns.add(field.getColumn());
		}
	}
}
