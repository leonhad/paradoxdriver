package org.paradox.parser.nodes;

import java.util.ArrayList;

public class SelectNode extends SQLNode {

    private ArrayList<FieldNode> fields = new ArrayList<FieldNode>();
    private ArrayList<TableNode> tables = new ArrayList<TableNode>();
    private ArrayList<SQLNode> groups = new ArrayList<SQLNode>();
    private ArrayList<SQLNode> orders = new ArrayList<SQLNode>();

    public SelectNode(final SQLNode parent) {
        super(parent, "SELECT");
    }

    /**
     * @return the fields
     */
    public ArrayList<FieldNode> getFields() {
        return fields;
    }

    /**
     * @param fields the fields to set
     */
    public void setFields(ArrayList<FieldNode> fields) {
        this.fields = fields;
    }

    /**
     * @return the tables
     */
    public ArrayList<TableNode> getTables() {
        return tables;
    }

    /**
     * @param tables the tables to set
     */
    public void setTables(ArrayList<TableNode> tables) {
        this.tables = tables;
    }

    /**
     * @return the groups
     */
    public ArrayList<SQLNode> getGroups() {
        return groups;
    }

    /**
     * @param groups the groups to set
     */
    public void setGroups(ArrayList<SQLNode> groups) {
        this.groups = groups;
    }

    /**
     * @return the orders
     */
    public ArrayList<SQLNode> getOrders() {
        return orders;
    }

    /**
     * @param orders the orders to set
     */
    public void setOrders(ArrayList<SQLNode> orders) {
        this.orders = orders;
    }
}
