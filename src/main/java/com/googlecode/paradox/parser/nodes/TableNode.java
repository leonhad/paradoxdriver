package com.googlecode.paradox.parser.nodes;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.paradox.utils.StringUtils;

public class TableNode extends SQLNode {

    private String alias;
    private final ArrayList<JoinNode> joins = new ArrayList<>();

    public TableNode(final String name, final String alias) {
        super(StringUtils.removeDb(name));
        this.alias = alias;
    }

    public void addJoin(final JoinNode join) {
        joins.add(join);
    }

    @Override
    public String getAlias() {
        return alias;
    }

    public List<JoinNode> getJoins() {
        return joins;
    }

    public void setAlias(final String alias) {
        this.alias = alias;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getName());
        if (!getName().equals(alias)) {
            builder.append(" AS ");
            builder.append(alias);
        }
        for (final JoinNode join : joins) {
            builder.append(" ");
            builder.append(join);
        }
        return builder.toString();
    }

}
