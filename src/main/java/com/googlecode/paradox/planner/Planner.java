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
import com.googlecode.paradox.exceptions.ParadoxDataException;
import com.googlecode.paradox.exceptions.ParadoxException;
import com.googlecode.paradox.exceptions.ParadoxNotSupportedException;
import com.googlecode.paradox.exceptions.ParadoxSyntaxErrorException;
import com.googlecode.paradox.metadata.ParadoxTable;
import com.googlecode.paradox.parser.nodes.*;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.planner.nodes.PlanTableNode;
import com.googlecode.paradox.planner.nodes.ValueNode;
import com.googlecode.paradox.planner.plan.Plan;
import com.googlecode.paradox.planner.plan.SelectPlan;
import com.googlecode.paradox.planner.sorting.OrderType;
import com.googlecode.paradox.rowset.ValuesConverter;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Creates a SQL execution plan.
 *
 * @version 1.3
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
     * @param connection the Paradox connection.
     * @param statement  the SELECT statement.
     * @param plan       the select execution plan.
     * @throws SQLException in case of parse errors.
     */
    private static void parseTableMetaData(final ParadoxConnection connection, final SelectNode statement,
                                           final SelectPlan plan) throws SQLException {
        for (final TableNode table : statement.getTables()) {
            final PlanTableNode node = new PlanTableNode();
            node.setTable(connection, table);
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
                    throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.ASTERISK_WITHOUT_TABLE,
                            field.getPosition());
                }
                parseAsterisk(plan, (AsteriskNode) field);
            } else {
                plan.addColumn((FieldNode) field);
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
                    throw new ParadoxException(ParadoxException.Error.INVALID_COLUMN_INDEX, Integer.toString(index));
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
     * @param connection the Paradox connection.
     * @param statement  the statement to plan.
     * @return the execution plan.
     * @throws SQLException in case of plan errors.
     */
    public static Plan create(final ParadoxConnection connection, final StatementNode statement)
            throws SQLException {
        Plan ret;
        if (statement instanceof SelectNode) {
            ret = createSelect(connection, (SelectNode) statement);
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
     * @param connection the Paradox connection.
     * @param statement  the statement to parse.
     * @return the SELECT plan.
     * @throws SQLException in case of syntax error.
     */
    private static Plan createSelect(final ParadoxConnection connection, final SelectNode statement)
            throws SQLException {
        final SelectPlan plan = new SelectPlan(statement.getCondition(), statement.isDistinct());

        // Load the table metadata.
        parseTableMetaData(connection, statement, plan);
        parseColumns(statement, plan);
        parseOrderBy(statement, plan);

        if (plan.getColumns().isEmpty()) {
            throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.EMPTY_COLUMN_LIST);
        }

        return plan;
    }
}
