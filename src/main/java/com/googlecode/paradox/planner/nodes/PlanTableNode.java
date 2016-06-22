package com.googlecode.paradox.planner.nodes;

import com.googlecode.paradox.metadata.ParadoxTable;

public class PlanTableNode {
    private ParadoxTable table;
    private String alias;

    public ParadoxTable getTable() {
        return table;
    }

    public void setTable(final ParadoxTable table) {
        this.table = table;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(final String alias) {
        this.alias = alias;
    }

}
