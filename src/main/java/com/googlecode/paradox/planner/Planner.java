/*
 * Copyright (C) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.planner;

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

import java.io.File;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Creates a SQL execution plan.
 *
 * @author Leonardo Alves da Costa
 * @version 1.2
 * @since 1.1
 */
public class Planner {

    private final ParadoxConnection connection;

    /**
     * Create a new instance.
     *
     * @param connection the database connection.
     */
    public Planner(final ParadoxConnection connection) {
        this.connection = connection;
    }

    /**
     * Parses the table metadata.
     *
     * @param connection    the Paradox connection.
     * @param statement     the SELECT statement.
     * @param plan          the select execution plan.
     * @param paradoxTables the tables list.
     * @throws SQLException in case of parse errors.
     */
    private static void parseTableMetaData(final ParadoxConnection connection, final SelectNode statement,
                                           final SelectPlan plan, final List<ParadoxTable> paradoxTables)
            throws SQLException {
        for (final TableNode table : statement.getTables()) {
            final PlanTableNode node = new PlanTableNode();
            node.setTable(connection.getSchema(), table, paradoxTables);
            plan.addTable(node);
        }
    }

    /**
     * Parses the table columns.
     *
     * @param statement the SELECT statement.
     * @param plan      the SELECT execution plan.
     * @throws SQLException in case of parse errors.
     */
    private static void parseColumns(final SelectNode statement, final SelectPlan plan) throws SQLException {
        for (final SQLNode field : statement.getFields()) {
            final String name = field.getName();
            if (field instanceof AsteriskNode) {
                parseAsterisk(plan, (AsteriskNode) field);
            } else {
                if ((name == null) || name.isEmpty()) {
                    throw new SQLException("Column name is empty.");
                }

                plan.addColumn(name);
            }
        }
    }

    private static void parseAsterisk(final SelectPlan plan, final AsteriskNode field) throws SQLException {
        if (field.getTableName() != null) {
            List<ParadoxTable> tables = plan.getTables().stream()
                    .filter(t -> t.isThis(field.getTableName()))
                    .map(PlanTableNode::getTable).collect(Collectors.toList());
            if (tables.isEmpty()) {
                throw new SQLException("Table " + field.getTableName() + " not found.");
            } else if (tables.size() > 1) {
                throw new SQLException("Table " + field.getTableName() + " is ambigous.");
            }

            plan.addColumnFromTable(tables.get(0));
        } else {
            plan.addColumnFromTables(plan.getTables());
        }
    }

    /**
     * Create a plan from given statement.
     *
     * @param connection    the Paradox connection.
     * @param statement     the statement to plan.
     * @param currentSchema the current schema file.
     * @return the execution plan.
     * @throws SQLException in case of plan errors.
     */
    public final Plan create(final ParadoxConnection connection, final StatementNode statement,
                             final File currentSchema) throws SQLException {
        if (statement instanceof SelectNode) {
            return this.createSelect(connection, (SelectNode) statement, currentSchema);
        } else {
            throw new SQLFeatureNotSupportedException();
        }
    }

    /**
     * Creates an SELECT plan.
     *
     * @param connection    the Paradox connection.
     * @param statement     the statement to parse.
     * @param currentSchema the current schema file.
     * @return the SELECT plan.
     * @throws SQLException in case of syntax error.
     */
    private Plan createSelect(final ParadoxConnection connection, final SelectNode statement,
                              final File currentSchema) throws SQLException {
        final SelectPlan plan = new SelectPlan(statement.getConditions());
        final List<ParadoxTable> paradoxTables = TableData.listTables(currentSchema, this.connection);

        // Load the table metadata.
        parseTableMetaData(connection, statement, plan, paradoxTables);
        parseColumns(statement, plan);

        if (plan.getColumns().isEmpty()) {
            throw new SQLException("Empty column list.", SQLStates.INVALID_SQL.getValue());
        }

        return plan;
    }
}
