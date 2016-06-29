/*
 * ParadoxField.java
 *
 * 03/14/2009
 * Copyright (C) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.metadata;

import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.utils.SQLStates;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Stores a field from a table.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.0
 */
public class ParadoxField {
    
    /**
     * Stores the field alias.
     */
    private String alias;
    
    /**
     * If this field is checked.
     */
    private boolean checked;
    
    /**
     * This field expression.
     */
    private String expression;
    
    /**
     * The JOIN name.
     */
    private String joinName;
    
    /**
     * Field name.
     */
    private String name;
    
    /**
     * Order of field in table/view. The first value is one.
     */
    private final int orderNum;
    
    /**
     * The the field order.
     */
    private short physicsSize;
    
    /**
     * The field size.
     */
    private short size;
    
    /**
     * The fields owner.
     */
    private ParadoxTable table;
    
    /**
     * The field table name.
     */
    private String tableName;
    
    /**
     * Field type.
     */
    private byte type;
    
    /**
     * Creates a new instance. it starts with {@link #orderNum} with one.
     */
    public ParadoxField() {
        this(1);
    }
    
    /**
     * Creates a new instance.
     *
     * @param orderNum
     *            order number to start.
     */
    public ParadoxField(final int orderNum) {
        this.orderNum = orderNum;
    }
    
    /**
     * {@inheritDoc}
     */
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
     * Gets the field alias.
     *
     * @return the field alias.
     */
    public String getAlias() {
        if (alias == null) {
            return name;
        }
        return alias;
    }
    
    /**
     * The field column reference.
     *
     * @return the {@link Column} reference.
     * @throws SQLException
     *             in case of type error.
     */
    public Column getColumn() throws SQLException {
        final Column dto = new Column(this);
        dto.setName(name.toUpperCase());
        dto.setType(getSqlType());
        dto.setAutoIncrement(type == 0x16);
        dto.setCurrency(type == 5);
        dto.setTableName(tableName);
        // FIXME move to Column
        if (type == 6) {
            dto.setScale(2);
        } else if (type == 5 || type == 0x16) {
            dto.setPrecision(9);
        }
        return dto;
    }
    
    /**
     * Gets the field expression.
     *
     * @return the field expression.
     */
    public String getExpression() {
        return expression;
    }
    
    /**
     * Gets the join name.
     *
     * @return the join name.
     */
    public String getJoinName() {
        return joinName;
    }
    
    /**
     * Gets the field name.
     *
     * @return the field name.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the field order.
     *
     * @return the field order.
     */
    public int getOrderNum() {
        return orderNum;
    }
    
    /**
     * Gets the file size in file.
     *
     * @return the file size in file.
     */
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
    
    /**
     * Gets the SQL field type.
     *
     * @return the SQL field type.
     * @throws SQLException
     *             in case of type not found.
     */
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
            throw new SQLException("Type not found: " + type, SQLStates.TYPE_NOT_FOUND.getValue());
        }
    }
    
    /**
     * Gets the field table.
     *
     * @return the field table.
     */
    public ParadoxTable getTable() {
        return table;
    }
    
    /**
     * Gets the tables name.
     *
     * @return the tables name.
     */
    public String getTableName() {
        return tableName;
    }
    
    /**
     * Gets the field type.
     *
     * @return the field type.
     */
    public byte getType() {
        return type;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (name != null ? name.hashCode() : 0);
        return hash;
    }
    
    /**
     * Gets the field auto increment status.
     *
     * @return true if this field is auto increment.
     */
    public boolean isAutoIncrement() {
        return type == 0x16;
    }
    
    /**
     * Gets field checked status.
     *
     * @return true if this field is checked.
     */
    public boolean isChecked() {
        return checked;
    }
    
    /**
     * Sets the field alias.
     *
     * @param alias
     *            the alias to set.
     */
    public void setAlias(final String alias) {
        this.alias = alias;
    }
    
    /**
     * Sets the field checked status.
     *
     * @param checked
     *            the checked to set.
     */
    public void setChecked(final boolean checked) {
        this.checked = checked;
    }
    
    /**
     * Sets this field expression.
     *
     * @param expression
     *            the expression to set.
     */
    public void setExpression(final String expression) {
        this.expression = expression;
    }
    
    /**
     * Sets the field join name.
     *
     * @param joinName
     *            the join name to set.
     */
    public void setJoinName(final String joinName) {
        this.joinName = joinName;
    }
    
    /**
     * Sets the field name.
     *
     * @param name
     *            the name to set.
     */
    public void setName(final String name) {
        this.name = name;
    }
    
    /**
     * Sets the field size.
     *
     * @param size
     *            the size to set.
     * @throws SQLException
     *             in case of invalid field type.
     */
    public void setSize(final short size) throws SQLException {
        physicsSize = size;
        if (getSqlType() == Types.CLOB || getSqlType() == Types.BLOB) {
            this.size = (short) (size - 10);
        } else {
            this.size = size;
        }
    }
    
    /**
     * Sets the table reference.
     *
     * @param table
     *            the table reference.
     */
    public void setTable(final ParadoxTable table) {
        this.table = table;
    }
    
    /**
     * Sets the table name.
     *
     * @param tableName
     *            the table name to set.
     */
    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }
    
    /**
     * Sets the field type.
     *
     * @param type
     *            the type to set.
     */
    public void setType(final byte type) {
        this.type = type;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return name;
    }
}
