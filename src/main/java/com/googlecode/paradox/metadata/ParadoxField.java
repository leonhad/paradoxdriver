package com.googlecode.paradox.metadata;

import java.sql.SQLException;
import java.sql.Types;

import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.utils.SQLStates;

/**
 *  Armazena os campos de uma tabela
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 16/03/2009
 */
public class ParadoxField {

    private String name;
    private byte type;
    private short size;
    private short physicsSize;
    private ParadoxTable table;
    private String tableName;
    private String alias;
    private String joinName;
    private boolean checked;
    private String expression;
    private int orderNum; // order of field in table/view (with 1)

    public ParadoxField(int orderNum) {
        this.orderNum = orderNum;
    }

    public ParadoxField() {
        this(1);
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

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ParadoxField other = (ParadoxField) obj;
        return !((this.name == null) ? (other.name != null) : !this.name.equals(other.name));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public boolean isAutoIncrement() {
        return type == 0x16;
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

    public void setTable(ParadoxTable table) {
        this.table = table;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    public byte getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(byte type) {
        this.type = type;
    }

    /**
     * @return the size
     */
    public short getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(short size) throws SQLException{
        this.physicsSize = size;
        if (getSqlType() == Types.CLOB || getSqlType() == Types.BLOB) {
            size -= 10;
        }
        this.size = size;
    }

    public short getPhysicsSize() {
        return physicsSize;
    }
    /**
     * @return the table
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName the table to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
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

    /**
     * @param alias the alias to set
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * @return the joinName
     */
    public String getJoinName() {
        return joinName;
    }

    /**
     * @param joinName the joinName to set
     */
    public void setJoinName(String joinName) {
        this.joinName = joinName;
    }

    /**
     * @return the checked
     */
    public boolean isChecked() {
        return checked;
    }

    /**
     * @param checked the checked to set
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    /**
     * @return the expression
     */
    public String getExpression() {
        return expression;
    }

    /**
     * @param expression the expression to set
     */
    public void setExpression(String expression) {
        this.expression = expression;
    }
}
