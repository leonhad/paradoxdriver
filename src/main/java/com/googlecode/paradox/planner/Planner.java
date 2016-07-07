/*
 * Planner.java
 *
 * 03/12/2009
 * Copyright (C) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.planner;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.List;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.data.TableData;
import com.googlecode.paradox.metadata.ParadoxTable;
import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.parser.nodes.SelectNode;
import com.googlecode.paradox.parser.nodes.StatementNode;
import com.googlecode.paradox.parser.nodes.TableNode;
import com.googlecode.paradox.parser.nodes.values.AsteriskNode;
import com.googlecode.paradox.planner.nodes.PlanTableNode;
import com.googlecode.paradox.planner.plan.Plan;
import com.googlecode.paradox.planner.plan.SelectPlan;
import com.googlecode.paradox.utils.SQLStates;

/**
 * Creates a SQL execution plan.
 * 
 * @author Leonardo Alves da Costa
 * @since 1.1
 * @version 1.1
 */
public class Planner {

    /**
     * The database connection.
     */
    private final ParadoxConnection conn;

    /**
     * Create a new instance.
     * 
     * @param conn
     *            the database connection.
     */
    public Planner(final ParadoxConnection conn) {
        this.conn = conn;
    }

    /**
     * Create a plan from given statement.
     * 
     * @param statement
     *            the statement to plan.
     * @return the execution plan.
     * @throws SQLException
     *             in case of plan errors.
     */
    public Plan create(final StatementNode statement) throws SQLException {
        if (statement instanceof SelectNode) {
            return createSelect((SelectNode) statement);
        } else {
            throw new SQLFeatureNotSupportedException();
        }
    }

    /**
     * Creates an SELECT plan.
     *
     * @param statement
     *            the statement to parse.
     * @return the SELECT plan.
     * @throws SQLException
     *             in case of syntax error.
     */
    private Plan createSelect(final SelectNode statement) throws SQLException {
        final SelectPlan plan = new SelectPlan(conn);
        final List<ParadoxTable> paradoxTables = TableData.listTables(conn);

        // Load the table metadata.
        parseTableMetaData(statement, plan, paradoxTables);
        parseColumns(statement, plan);

        if (plan.getColumns().isEmpty()) {
            throw new SQLException("Empty column list", SQLStates.INVALID_SQL.getValue());
        }

        return plan;
    }

    /**
     * Parses the table columns.
     * 
     * @param statement
     *            the SELECT statement.
     * @param plan
     *            the SELECT execution plan.
     * @throws SQLException
     *             in case of parse errors.
     */
    private void parseColumns(final SelectNode statement, final SelectPlan plan) throws SQLException {
        for (final SQLNode field : statement.getFields()) {
            final String name = field.getName();
            if (field instanceof AsteriskNode) {
                for (final PlanTableNode table : plan.getTables()) {
                    plan.addColumnFromTable(table.getTable());
                }
            } else {
                if (name == null || name.isEmpty()) {
                    throw new SQLException("Column name is empty");
                }
                plan.addColumn(name);
            }
        }
    }

    /**
     * Parses the table metadata.
     * 
     * @param statement
     *            the SELECT statement.
     * @param plan
     *            the select execution plan.
     * @param paradoxTables
     *            the tables list.
     * @throws SQLException
     *             in case of parse errors.
     */
    private void parseTableMetaData(final SelectNode statement, final SelectPlan plan, final List<ParadoxTable> paradoxTables) throws SQLException {
        for (final TableNode table : statement.getTables()) {
            final PlanTableNode node = new PlanTableNode();
            for (final ParadoxTable paradoxTable : paradoxTables) {
                if (paradoxTable.getName().equalsIgnoreCase(table.getName())) {
                    node.setTable(paradoxTable);
                    break;
                }
            }
            if (node.getTable() == null) {
                throw new SQLException("Table " + table.getName() + " not found.", SQLStates.INVALID_SQL.getValue());
            }
            if (!table.getName().equals(table.getAlias())) {
                node.setAlias(table.getAlias());
            }
            plan.addTable(node);
        }
    }
}
