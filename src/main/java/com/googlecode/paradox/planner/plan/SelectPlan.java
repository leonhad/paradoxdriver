/*
 * SelectPlan.java 03/14/2009 Copyright (C) 2009 Leonardo Alves da Costa This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.planner.plan;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.data.TableData;
import com.googlecode.paradox.data.table.value.FieldValue;
import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.metadata.ParadoxTable;
import com.googlecode.paradox.planner.nodes.PlanTableNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.utils.SQLStates;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Creates a SELECT plan for execution.
 *
 * @author Leonardo Alves da Costa
 * @version 1.1
 * @since 1.1
 */
public final class SelectPlan implements Plan {
    
    /**
     * The columns in this plan.
     */
    private final List<Column> columns = new ArrayList<>();
    
    /**
     * The tables in this plan.
     */
    private final List<PlanTableNode> tables = new ArrayList<>();
    
    /**
     * The data values.
     */
    private final List<List<FieldValue>> values = new ArrayList<>();
    
    /**
     * Creates a SELECT plan.
     *
     * @param conn
     *            the Paradox connection.
     */
    public SelectPlan(final ParadoxConnection conn) {
    }
    
    /**
     * Add column from select list.
     *
     * @param name
     *            column name.
     * @throws SQLException
     *             search column exception.
     */
    public void addColumn(final String name) throws SQLException {
        final ParadoxField field = this.findField(name);
        if (field == null) {
            throw new SQLException(String.format("Invalid column name: '%s'", name),
                    SQLStates.INVALID_COLUMN.getValue());
        }
        this.columns.add(field.getColumn());
    }
    
    /**
     * Associate all columns from a table.
     *
     * @param table
     *            the table to scan.
     * @throws SQLException
     *             in case of wrong SQL type.
     */
    public void addColumnFromTable(final ParadoxTable table) throws SQLException {
        for (final ParadoxField field : table.getFields()) {
            this.columns.add(field.getColumn());
        }
    }
    
    /**
     * Adds a table to this plan.
     *
     * @param table
     *            the table.
     */
    public void addTable(final PlanTableNode table) {
        this.tables.add(table);
    }
    
    /**
     * {@inheritDoc}.
     */
    @Override
    public void execute() throws SQLException {
        if (this.columns.isEmpty() || this.tables.isEmpty()) {
            return;
        }
        
        for (final Column column : this.columns) {
            for (final PlanTableNode table : this.tables) {
                final ParadoxTable pTable = table.getTable();
                if (column.getTableName().equalsIgnoreCase(pTable.getName())) {
                    this.loadTableData(column, pTable);
                }
            }
        }
    }
    
    /**
     * Gets the columns in SELECT statement.
     *
     * @return the columns in SELECT statement.
     */
    public List<Column> getColumns() {
        return Collections.unmodifiableList(this.columns);
    }
    
    /**
     * Gets the tables in this plan.
     *
     * @return the tables in this plan.
     */
    public List<PlanTableNode> getTables() {
        return Collections.unmodifiableList(this.tables);
    }
    
    /**
     * Values from tables in column order.
     *
     * @return array of array of values/ Can be null (empty result set);
     */
    public List<List<FieldValue>> getValues() {
        return Collections.unmodifiableList(this.values);
    }
    
    /**
     * Fill the result row with a field order.
     *
     * @param tableData
     *            the table data load from.
     * @param fieldOrder
     *            the field order.
     */
    private void fillResultValues(final List<List<FieldValue>> tableData, final int fieldOrder) {
        for (int j = 0; j < tableData.size(); j++) {
            List<FieldValue> resultRow;
            if (j == this.values.size()) {
                resultRow = new ArrayList<>();
                this.values.add(resultRow);
            } else {
                resultRow = this.values.get(j);
            }
            resultRow.add(tableData.get(j).get(fieldOrder));
        }
    }
    
    /**
     * Finds a single column in the table list.
     *
     * @param fieldName
     *            the field name.
     * @param fields
     *            the field list.
     * @param prefix
     *            the field prefix.
     * @throws SQLException
     *             in case of parse errors.
     */
    private void findColumn(final String fieldName, final List<ParadoxField> fields, final String prefix)
            throws SQLException {
        for (final PlanTableNode table : this.tables) {
            if (table.getTable() == null) {
                throw new SQLException("Empty table", SQLStates.INVALID_TABLE.getValue());
            }
            
            if ((prefix != null) && (table.getAlias() != null) && !prefix.equalsIgnoreCase(table.getAlias())) {
                continue;
            }
            
            for (final ParadoxField field : table.getTable().getFields()) {
                if (field.getName().equalsIgnoreCase(fieldName)) {
                    fields.add(field);
                    // Unique column in table
                    break;
                }
            }
        }
    }
    
    /**
     * Find a paradox field by its name.
     *
     * @param name
     *            the field name.
     * @return the paradox field.
     * @throws SQLException
     *             in case of find errors.
     */
    private ParadoxField findField(final String name) throws SQLException {
        String newName = name;
        final List<ParadoxField> fields = new ArrayList<>(1);
        String prefix = null;
        final int p = newName.indexOf('.');
        if (p > -1) {
            prefix = newName.substring(0, p);
            newName = newName.substring(p + 1);
        }
        
        this.findColumn(newName, fields, prefix);
        if (!fields.isEmpty()) {
            if (fields.size() > 1) {
                throw new SQLException("Column '" + newName + "' ambiguously defined",
                        SQLStates.COLUMN_AMBIQUOUS.getValue());
            } else {
                return fields.get(0);
            }
        }
        
        return null;
    }
    
    /**
     * Load the table data form a table.
     *
     * @param column
     *            the column to load.
     * @param table
     *            the table to load.
     * @throws SQLException
     *             in case of execution errors.
     */
    private void loadTableData(final Column column, final ParadoxTable table) throws SQLException {
        final ParadoxField field = table.findField(column.getName());
        if (field == null) {
            throw new SQLException("Column '" + column.getName() + "' not found in table '" + table.getName(),
                    SQLStates.INVALID_FIELD_VALUE.getValue());
        }
        // load table data
        final List<List<FieldValue>> tableData = TableData.loadData(table, table.getFields());
        // search column index
        if ((field.getOrderNum() > table.getFields().size()) || (field.getOrderNum() < 1)) {
            throw new SQLException("Invalid column position", SQLStates.INVALID_FIELD_VALUE.getValue());
        }
        
        final int p = field.getOrderNum() - 1;
        this.fillResultValues(tableData, p);
    }
}
