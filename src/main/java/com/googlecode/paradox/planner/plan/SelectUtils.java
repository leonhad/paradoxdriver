/*
 * Copyright (c) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.googlecode.paradox.planner.plan;

import com.googlecode.paradox.exceptions.ParadoxException;
import com.googlecode.paradox.parser.nodes.AbstractConditionalNode;
import com.googlecode.paradox.parser.nodes.AsteriskNode;
import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.planner.nodes.*;
import com.googlecode.paradox.planner.nodes.join.ANDNode;
import com.googlecode.paradox.planner.nodes.join.AbstractJoinNode;
import com.googlecode.paradox.results.Column;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class to reduce Select Plan complexity.
 *
 * @since 1.6.0
 */
public final class SelectUtils {
    /**
     * Utility class, not for use.
     */
    private SelectUtils() {
        // Not using.
    }

    /**
     * Add a AND clause to the plan tree.
     *
     * @param table  the plan table.
     * @param clause the clause to add.
     */
    public static void addAndClause(final PlanTableNode table, SQLNode clause) {
        if (table.getConditionalJoin() instanceof ANDNode) {
            // Exists and it is an AND node.
            table.getConditionalJoin().addChild(clause);
        } else if (table.getConditionalJoin() != null) {
            // Exists, but any other type.
            final ANDNode andNode = new ANDNode(table.getConditionalJoin(), null);
            andNode.addChild(clause);
            table.setConditionalJoin(andNode);
        } else {
            // There is no conditionals in this table.
            table.setConditionalJoin((AbstractConditionalNode) clause);
        }
    }

    /**
     * Add a column to the grouping function.
     *
     * @param column the column to add.
     * @return the function grouping node.
     */
    public static List<FunctionNode> getGroupingFunctions(final Column column) {
        final FunctionNode function = column.getFunction();
        if (function == null) {
            return Collections.emptyList();
        }

        return function.getGroupingNodes();
    }

    /**
     * Gets the conditional fields.
     *
     * @param table     the plan table.
     * @param condition the condition used to search.
     * @return the column set.
     */
    public static Set<Column> getConditionalFields(final PlanTableNode table, final AbstractConditionalNode condition) {
        if (condition != null) {
            return condition.getClauseFields().stream()
                    .filter(node -> table.isThis(node.getTableName()))
                    .map(table::findField)
                    .filter(Objects::nonNull)
                    .map(Column::new)
                    .collect(Collectors.toSet());
        }

        return Collections.emptySet();
    }

    /**
     * Add a JOIN clauses.
     *
     * @param node the node to add.
     * @return the conditional node.
     */
    public static AbstractConditionalNode joinClauses(final AbstractConditionalNode node) {
        AbstractConditionalNode ret = node;

        // It is an 'AND' and 'OR' node?
        if (node instanceof AbstractJoinNode) {
            final List<SQLNode> children = node.getChildren();

            // Reduce all children.
            children.replaceAll(sqlNode -> joinClauses((AbstractConditionalNode) sqlNode));

            // Join only 'AND' and 'OR' nodes.
            while (ret instanceof AbstractJoinNode && ret.getChildren().size() <= 1) {
                if (ret.getChildren().isEmpty()) {
                    ret = null;
                } else {
                    ret = (AbstractConditionalNode) ret.getChildren().get(0);
                }
            }
        }

        return ret;
    }

    /**
     * Gets the fields from field node and plan tables.
     *
     * @param node   the field node.
     * @param tables the plan tables.
     * @return the column list.
     * @throws ParadoxException in case of failures.
     */
    public static List<Column> getParadoxFields(final FieldNode node, final List<PlanTableNode> tables)
            throws ParadoxException {
        final List<Column> ret = new ArrayList<>();

        if (node instanceof FunctionNode) {
            final FunctionNode functionNode = (FunctionNode) node;

            // Create the column for the function.
            ret.add(new Column(functionNode));

            // Parses function fields in function parameters.
            for (final FieldNode field : functionNode.getClauseFields()) {
                ret.addAll(getParadoxFields(field, tables));
            }
        } else if (!(node instanceof ValueNode) && !(node instanceof ParameterNode)
                && !(node instanceof AsteriskNode)) {
            for (final PlanTableNode table : tables) {
                if (node.getTableName() == null || table.isThis(node.getTableName())) {
                    node.setTable(table.getTable());
                    ret.addAll(Arrays.stream(table.getTable().getFields())
                            .filter(f -> f.getName().equalsIgnoreCase(node.getName()))
                            .map(Column::new)
                            .collect(Collectors.toList()));
                }
            }

            if (ret.isEmpty()) {
                throw new ParadoxException(ParadoxException.Error.INVALID_COLUMN, node.getPosition(), node.toString());
            } else if (ret.size() > 1) {
                throw new ParadoxException(ParadoxException.Error.COLUMN_AMBIGUOUS_DEFINED, node.getPosition(),
                        node.toString());
            }
        }

        return ret;
    }
}
