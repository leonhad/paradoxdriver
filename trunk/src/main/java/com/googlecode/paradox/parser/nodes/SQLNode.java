package com.googlecode.paradox.parser.nodes;

public abstract class SQLNode {

    private final SQLNode parent;
    private final String name;

    public SQLNode(final SQLNode parent, final String name) {
        this.parent = parent;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public SQLNode getParent() {
        return parent;
    }
}
