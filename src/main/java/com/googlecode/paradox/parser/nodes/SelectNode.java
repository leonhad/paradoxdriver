package com.googlecode.paradox.parser.nodes;

import java.util.ArrayList;
import java.util.List;

public class SelectNode extends StatementNode {

    private ArrayList<SQLNode> conditions;
    private boolean distinct;
    private final ArrayList<SQLNode> fields = new ArrayList<>();
    private final ArrayList<IdentifierNode> groups = new ArrayList<>();
    private final ArrayList<IdentifierNode> order = new ArrayList<>();
    private final ArrayList<TableNode> tables = new ArrayList<>();

    public SelectNode() {
        super("SELECT");
    }

    public void addField(final SQLNode field) {
        fields.add(field);
    }

    public void addGroupBy(final IdentifierNode indentifier) {
        groups.add(indentifier);
    }

    public void addOrderBy(final IdentifierNode indentifier) {
        order.add(indentifier);
    }

    public void addTable(final TableNode table) {
        tables.add(table);
    }

    public List<SQLNode> getConditions() {
        return conditions;
    }

    public List<SQLNode> getFields() {
        return fields;
    }

    public List<IdentifierNode> getGroups() {
        return groups;
    }

    public List<IdentifierNode> getOrder() {
        return order;
    }

    public List<TableNode> getTables() {
        return tables;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public void setConditions(final ArrayList<SQLNode> conditions) {
        this.conditions = conditions;
    }

    public void setDistinct(final boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getName());
        builder.append(" ");

        boolean first = true;
        for (final SQLNode field : fields) {
            if (first) {
                first = false;
            } else {
                builder.append(", ");
            }
            builder.append(field);
        }
        if (tables.size() > 0) {
            builder.append(" FROM ");
            first = true;
            for (final TableNode table : tables) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append(table);
            }
        }
        if (conditions != null && conditions.isEmpty()) {
            builder.append(" WHERE ");
            first = true;
            for (final SQLNode cond : conditions) {
                if (first) {
                    first = false;
                } else {
                    builder.append(" ");
                }
                builder.append(cond);
            }
        }
        if (groups.isEmpty()) {
            builder.append(" GROUP BY ");
            first = true;
            for (final IdentifierNode group : groups) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append(group);
            }
        }
        if (order.isEmpty()) {
            builder.append(" ORDER BY ");
            first = true;
            for (final IdentifierNode ident : order) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append(ident);
            }
        }
        return builder.toString();
    }
}
