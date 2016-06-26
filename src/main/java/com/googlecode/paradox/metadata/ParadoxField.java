package com.googlecode.paradox.metadata;

import java.sql.SQLException;
import java.sql.Types;

import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.utils.SQLStates;

/**
 * Stores a field from a table.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.0
 */
public class ParadoxField {

    private String alias;
    private boolean checked;
    private String expression;
    private String joinName;
    private String name;

    /**
     * Order of field in table/view (with 1)
     */
    private final int orderNum;
    private short physicsSize;
    private short size;
    private ParadoxTable table;
    private String tableName;
    private byte type;

    public ParadoxField() {
        this(1);
    }

    public ParadoxField(final int orderNum) {
        this.orderNum = orderNum;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ParadoxField other = (ParadoxField) obj;
        return !(name == null ? other.name != null : !name.equals(other.name));
    }

    /**
     * @return the alias
     */
    public String getAlias() {
        if (alias == null) {
            return name;
        }
        return alias;
    }

    public Column getColumn() throws SQLException {
        final Column dto = new Column(this);
        dto.setName(name.toUpperCase());
        dto.setType(getSqlType());
        dto.setAutoIncrement(type == 0x16);
        dto.setCurrency(type == 5);
        dto.setTableName(tableName);
        switch (type) {
        case 6:
            dto.setScale(2);
        case 5:
        case 0x16:
            dto.setPrecision(9);
        }
        return dto;
    }

    /**
     * @return the expression
     */
    public String getExpression() {
        return expression;
    }

    /**
     * @return the joinName
     */
    public String getJoinName() {
        return joinName;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public short getPhysicsSize() {
        return physicsSize;
    }

    /**
     * Gets the field size.
     *
     * @return the size.
     */
    public short getSize() {
        return size;
    }

    public int getSqlType() throws SQLException {
        switch (type) {
        case 1:
        case 0xE:
            return Types.VARCHAR;
        case 2:
            return Types.DATE;
        case 3:
        case 4:
        case 0x16:
            return Types.INTEGER;
        case 5:
            return Types.DOUBLE;
        case 6:
            return Types.NUMERIC;
        case 9:
            return Types.BOOLEAN;
        case 0xC:
            return Types.CLOB;
        case 0xD:
        case 0xF:
        case 0x18:
            return Types.BLOB;
        case 0x14:
            return Types.TIME;
        case 0x15:
            return Types.TIMESTAMP;
        case 0x17:
            return Types.BINARY;
        default:
            throw new SQLException("Type not found: " + type, SQLStates.TYPE_NOT_FOUND);
        }
    }

    public ParadoxTable getTable() {
        return table;
    }

    /**
     * @return the table
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @return the type
     */
    public byte getType() {
        return type;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (name != null ? name.hashCode() : 0);
        return hash;
    }

    public boolean isAutoIncrement() {
        return type == 0x16;
    }

    /**
     * @return the checked
     */
    public boolean isChecked() {
        return checked;
    }

    /**
     * @param alias
     *            the alias to set
     */
    public void setAlias(final String alias) {
        this.alias = alias;
    }

    /**
     * @param checked
     *            the checked to set
     */
    public void setChecked(final boolean checked) {
        this.checked = checked;
    }

    /**
     * @param expression
     *            the expression to set
     */
    public void setExpression(final String expression) {
        this.expression = expression;
    }

    /**
     * @param joinName
     *            the joinName to set
     */
    public void setJoinName(final String joinName) {
        this.joinName = joinName;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Sets the field size.
     *
     * @param size
     *            the size to set
     * @throws SQLException
     *             in case of wrong field type.
     */
    public void setSize(short size) throws SQLException {
        physicsSize = size;
        if (getSqlType() == Types.CLOB || getSqlType() == Types.BLOB) {
            size -= 10;
        }
        this.size = size;
    }

    public void setTable(final ParadoxTable table) {
        this.table = table;
    }

    /**
     * @param tableName
     *            the table to set
     */
    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(final byte type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return name;
    }
}
