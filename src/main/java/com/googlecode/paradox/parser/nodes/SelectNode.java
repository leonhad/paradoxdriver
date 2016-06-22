package com.googlecode.paradox.parser.nodes;

import java.util.ArrayList;

public class SelectNode extends StatementNode {

    private boolean distinct;
    private final ArrayList<SQLNode> fields = new ArrayList<SQLNode>();
    private final ArrayList<TableNode> tables = new ArrayList<TableNode>();
    private final ArrayList<IdentifierNode> groups = new ArrayList<IdentifierNode>();
    private final ArrayList<IdentifierNode> order = new ArrayList<IdentifierNode>();
    private ArrayList<SQLNode> conditions;

    public SelectNode() {
        super("SELECT");
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
        if (conditions != null && conditions.size() > 0) {
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
        if (groups.size() > 0) {
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
        if (order.size() > 0) {
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

    public boolean isDistinct() {
        return distinct;
    }

    public void setDistinct(final boolean distinct) {
        this.distinct = distinct;
    }

    public void addField(final SQLNode field) {
        fields.add(field);
    }

    public void addTable(final TableNode table) {
        tables.add(table);
    }

    public void addGroupBy(final IdentifierNode indentifier) {
        groups.add(indentifier);
    }

    public void addOrderBy(final IdentifierNode indentifier) {
        order.add(indentifier);
    }

    public void setConditions(final ArrayList<SQLNode> conditions) {
        this.conditions = conditions;
    }

    public ArrayList<SQLNode> getFields() {
        return fields;
    }

    public ArrayList<TableNode> getTables() {
        return tables;
    }

    public ArrayList<IdentifierNode> getGroups() {
        return groups;
    }

    public ArrayList<IdentifierNode> getOrder() {
        return order;
    }

    public ArrayList<SQLNode> getConditions() {
        return conditions;
    }
}
