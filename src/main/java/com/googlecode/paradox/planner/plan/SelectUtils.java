package com.googlecode.paradox.planner.plan;

import com.googlecode.paradox.parser.nodes.AbstractConditionalNode;
import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.planner.nodes.FunctionNode;
import com.googlecode.paradox.planner.nodes.PlanTableNode;
import com.googlecode.paradox.planner.nodes.join.ANDNode;
import com.googlecode.paradox.planner.nodes.join.AbstractJoinNode;
import com.googlecode.paradox.results.Column;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class to reduce Select Plan complexity.
 *
 * @version 1.0
 * @since 1.6.0
 */
final class SelectUtils {
    /**
     * Utility class, not for use.
     */
    private SelectUtils() {
        // Not using.
    }

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

    public static List<FunctionNode> getGroupingFunctions(final Column column) {
        final FunctionNode function = column.getFunction();
        if (function == null) {
            return Collections.emptyList();
        }

        return function.getGroupingNodes();
    }

    public static Set<Column> getConditionalFields(final PlanTableNode table, final AbstractConditionalNode condition) {
        if (condition != null) {
            return condition.getClauseFields().stream()
                    .filter(node -> table.isThis(node.getTableName()))
                    .map(table::getField)
                    .filter(Objects::nonNull)
                    .map(Column::new)
                    .collect(Collectors.toSet());
        }

        return Collections.emptySet();
    }

    public static AbstractConditionalNode joinClauses(final AbstractConditionalNode node) {
        AbstractConditionalNode ret = node;

        // It is an AND and OR node?
        if (node instanceof AbstractJoinNode) {
            final List<SQLNode> children = node.getChildren();

            // Reduce all children.
            for (int loop = 0; loop < children.size(); loop++) {
                children.set(loop, joinClauses((AbstractConditionalNode) children.get(loop)));
            }

            // Join only AND and OR nodes.
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
}
