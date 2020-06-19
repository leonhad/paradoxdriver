/*
 * Copyright (C) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.metadata;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxFieldType;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

/**
 * Stores a field from a table.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.0
 */
public final class ParadoxField {

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
    private int physicsSize;

    /**
     * The field size.
     */
    private int size;

    /**
     * The fields owner.
     */
    private ParadoxTable table;

    /**
     * The field table name.
     */
    private String tableName;

    /**
     * Paradox field type.
     */
    private byte type;

    private final ParadoxConnection connection;

    /**
     * Creates a new instance. it starts with {@link #getOrderNum()} with one.
     *
     * @param connection the Paradox connection.
     */
    public ParadoxField(final ParadoxConnection connection) {
        this(connection, 1);
    }

    /**
     * Creates a new instance.
     *
     * @param connection the Paradox connection.
     * @param orderNum   order number to start.
     */
    public ParadoxField(final ParadoxConnection connection, final int orderNum) {
        this.connection = connection;
        this.orderNum = orderNum;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final ParadoxField other = (ParadoxField) obj;
        return Objects.equals(this.name, other.name);
    }

    /**
     * Gets the field alias.
     *
     * @return the field alias.
     */
    public String getAlias() {
        if (this.alias == null) {
            return this.name;
        }
        return this.alias;
    }

    /**
     * The field column reference.
     *
     * @return the {@link Column} reference.
     * @throws SQLException in case of type error.
     */
    public Column getColumn() throws SQLException {
        final Column dto = new Column(this);
        dto.setName(this.name.toUpperCase(connection.getLocale()));
        dto.setType(this.getSqlType());
        dto.setTableName(this.tableName);
        return dto;
    }

    /**
     * Gets the field expression.
     *
     * @return the field expression.
     */
    public String getExpression() {
        return this.expression;
    }

    /**
     * Gets the join name.
     *
     * @return the join name.
     */
    public String getJoinName() {
        return this.joinName;
    }

    /**
     * Gets the field name.
     *
     * @return the field name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the field order.
     *
     * @return the field order.
     */
    public int getOrderNum() {
        return this.orderNum;
    }

    /**
     * Gets the field size.
     *
     * @return the size.
     */
    public int getSize() {
        return this.size;
    }

    /**
     * Gets the field table.
     *
     * @return the field table.
     */
    public ParadoxTable getTable() {
        return this.table;
    }

    /**
     * Gets the tables name.
     *
     * @return the tables name.
     */
    public String getTableName() {
        return this.tableName;
    }

    /**
     * Gets the field type.
     *
     * @return the field type.
     */
    public byte getType() {
        return this.type;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int hashCode() {
        final int hash = 7;
        return (17 * hash) + ((this.name != null) ? this.name.hashCode() : 0);
    }

    /**
     * Gets field checked status.
     *
     * @return true if this field is checked.
     */
    public boolean isChecked() {
        return this.checked;
    }

    /**
     * Sets the field alias.
     *
     * @param alias the alias to set.
     */
    public void setAlias(final String alias) {
        this.alias = alias;
    }

    /**
     * Sets the field checked status.
     *
     * @param checked the checked to set.
     */
    public void setChecked(final boolean checked) {
        this.checked = checked;
    }

    /**
     * Sets this field expression.
     *
     * @param expression the expression to set.
     */
    public void setExpression(final String expression) {
        this.expression = expression;
    }

    /**
     * Sets the field join name.
     *
     * @param joinName the join name to set.
     */
    public void setJoinName(final String joinName) {
        this.joinName = joinName;
    }

    /**
     * Sets the field name.
     *
     * @param name the name to set.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Sets the field size.
     *
     * @param size the size to set.
     * @throws SQLException in case of invalid field type.
     */
    public void setSize(final int size) throws SQLException {
        this.physicsSize = size;
        int sqlType = this.getSqlType();
        if ((sqlType == Types.CLOB) || (sqlType == Types.BLOB)) {
            this.size = size - 10;
        } else {
            this.size = size;
        }
    }

    /**
     * Sets the table reference.
     *
     * @param table the table reference.
     */
    public void setTable(final ParadoxTable table) {
        this.table = table;
    }

    /**
     * Sets the table name.
     *
     * @param tableName the table name to set.
     */
    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }

    /**
     * Sets the field type.
     *
     * @param type the type to set.
     */
    public void setType(final byte type) {
        this.type = type;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Gets the file size in file.
     *
     * @return the file size in file.
     */
    int getPhysicsSize() {
        return this.physicsSize;
    }

    /**
     * Gets the SQL field type.
     *
     * @return the SQL field type.
     * @throws SQLException in case of type not found.
     */
    int getSqlType() throws SQLException {
        return ParadoxFieldType.getSQLTypeByType(this.type);
    }

    /**
     * Gets the field auto increment status.
     *
     * @return true if this field is auto increment.
     */
    boolean isAutoIncrement() {
        return this.type == 0x16;
    }
}
