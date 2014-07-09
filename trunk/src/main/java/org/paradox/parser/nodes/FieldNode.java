package org.paradox.parser.nodes;

public class FieldNode extends SQLNode {

    private String tableName;

    public FieldNode(final SQLNode parent, final String name) {
        super(parent, name);
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
