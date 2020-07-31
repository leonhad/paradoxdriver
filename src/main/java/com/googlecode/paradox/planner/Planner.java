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

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.exceptions.*;
import com.googlecode.paradox.metadata.ParadoxTable;
import com.googlecode.paradox.parser.nodes.*;
import com.googlecode.paradox.planner.nodes.*;
import com.googlecode.paradox.planner.plan.Plan;
import com.googlecode.paradox.planner.plan.SelectPlan;
import com.googlecode.paradox.planner.sorting.OrderType;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.rowset.ValuesConverter;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Creates a SQL execution plan.
 *
 * @version 1.5
 * @since 1.1
 */
public class Planner {

    /**
     * Create a new instance.
     */
    protected Planner() {
        super();
    }

    /**
     * Parses the table metadata.
     *
     * @param connectionInfo the connection information.
     * @param statement      the SELECT statement.
     * @param plan           the select execution plan.
     * @throws SQLException in case of parse errors.
     */
    private static void parseTableMetaData(final ConnectionInfo connectionInfo, final SelectNode statement,
                                           final SelectPlan plan) throws SQLException {
        for (final TableNode table : statement.getTables()) {
            final PlanTableNode node = new PlanTableNode();
            node.setTable(connectionInfo, table);
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
            if (field instanceof AsteriskNode) {
                if (plan.getTables().isEmpty()) {
                    throw new ParadoxSyntaxErrorException(SyntaxError.ASTERISK_WITHOUT_TABLE,
                            field.getPosition());
                }
                parseAsterisk(plan, (AsteriskNode) field);
            } else {
                plan.addColumn((FieldNode) field);
            }
        }
    }

    /**
     * Parses the group by fields.
     *
     * @param statement the SELECT statement.
     * @param plan      the SELECT execution plan.
     * @throws SQLException in case of parse errors.
     */
    private static void parseGroupBy(final SelectNode statement, final SelectPlan plan) throws SQLException {
        // Create columns to use in SELECT statement.
        for (final FieldNode field : statement.getGroups()) {
            if (field instanceof ParameterNode) {
                throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED,
                        field.getPosition());
            } else if (field instanceof ValueNode) {
                plan.addGroupColumn(new Column((ValueNode) field));
            } else {
                plan.addGroupColumn(field);
            }
        }

        final List<Column> groupColumns = plan.getGroupByFields();
        final boolean groupBy = plan.getColumns().stream()
                .map(Column::getFunction)
                .filter(Objects::nonNull)
                .anyMatch(FunctionNode::isGrouping);

        // This is a group by expression?
        if (groupBy) {
            // Validate statically the group by clause.
            final List<Column> fields = plan.getColumns().stream()
                    .filter(c -> c.getFunction() == null || !c.getFunction().isGrouping())
                    .filter(c -> !groupColumns.contains(c))
                    .collect(Collectors.toList());

            if (!fields.isEmpty()) {
                throw new ParadoxSyntaxErrorException(SyntaxError.NOT_GROUP_BY);
            }
        }
    }

    /**
     * Parses the order by fields.
     *
     * @param statement the SELECT statement.
     * @param plan      the SELECT execution plan.
     * @throws SQLException in case of parse errors.
     */
    private static void parseOrderBy(final SelectNode statement, final SelectPlan plan) throws SQLException {
        for (int i = 0; i < statement.getOrder().size(); i++) {
            final FieldNode field = statement.getOrder().get(i);
            final OrderType type = statement.getOrderTypes().get(i);
            if (field instanceof ValueNode) {
                int index = ValuesConverter.getInteger(field.getName());
                if (index > plan.getColumns().size()) {
                    throw new ParadoxException(ParadoxException.Error.INVALID_COLUMN_INDEX, index);
                }

                plan.addOrderColumn(plan.getColumns().get(index - 1), type);
            } else {
                plan.addOrderColumn(field, type);
            }
        }
    }

    /**
     * Parses the asterisk fields in SELECT.
     *
     * @param plan  the SELECT execution plan.
     * @param field the asterisk field.
     * @throws SQLException in case of parse errors.
     */
    private static void parseAsterisk(final SelectPlan plan, final AsteriskNode field) throws SQLException {
        if (field.getTableName() != null) {
            List<ParadoxTable> tables = plan.getTables().stream()
                    .filter(t -> t.isThis(field.getTableName()))
                    .map(PlanTableNode::getTable).collect(Collectors.toList());
            if (tables.isEmpty()) {
                throw new ParadoxDataException(ParadoxDataException.Error.TABLE_NOT_FOUND, field.getPosition(),
                        field.getTableName());
            } else if (tables.size() > 1) {
                throw new ParadoxException(ParadoxException.Error.TABLE_AMBIGUOUS_DEFINED, field.getPosition(),
                        field.getTableName());
            }

            plan.addColumnFromTable(tables.get(0));
        } else {
            plan.addColumnFromTables(plan.getTables());
        }
    }

    /**
     * Create a plan from given statement.
     *
     * @param connectionInfo the connection information.
     * @param statement      the statement to plan.
     * @return the execution plan.
     * @throws SQLException in case of plan errors.
     */
    public static Plan create(final ConnectionInfo connectionInfo, final StatementNode statement)
            throws SQLException {
        Plan ret;
        if (statement instanceof SelectNode) {
            ret = createSelect(connectionInfo, (SelectNode) statement);
        } else {
            throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
        }

        // Optimize the plan.
        ret.compile();
        return ret;
    }

    /**
     * Creates an SELECT plan.
     *
     * @param connectionInfo the connection information.
     * @param statement      the statement to parse.
     * @return the SELECT plan.
     * @throws SQLException in case of syntax error.
     */
    private static Plan createSelect(final ConnectionInfo connectionInfo, final SelectNode statement)
            throws SQLException {
        final SelectPlan plan = new SelectPlan(statement.getCondition(), statement.isDistinct());

        // Load the table metadata.
        parseTableMetaData(connectionInfo, statement, plan);
        parseColumns(statement, plan);
        parseGroupBy(statement, plan);
        parseOrderBy(statement, plan);

        if (plan.getColumns().isEmpty()) {
            throw new ParadoxSyntaxErrorException(SyntaxError.EMPTY_COLUMN_LIST);
        }

        return plan;
    }
}
