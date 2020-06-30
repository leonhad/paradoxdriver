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
import com.googlecode.paradox.data.field.BCDField;
import com.googlecode.paradox.results.ParadoxFieldType;

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
     * Default number precision.
     */
    private static final int NUMBER_PRECISION = 3;

    private static final int CURRENCY_PRECISION = 2;

    private static final int BLOB_SIZE_PADDING = 10;

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
    private int realSize;

    /**
     * The field precision.
     */
    private int precision;

    /**
     * The field size.
     */
    private int size;

    /**
     * The fields owner.
     */
    private ParadoxTable table;

    /**
     * Paradox field type.
     */
    private final byte type;

    private final ParadoxConnection connection;

    /**
     * Creates a new instance. it starts with {@link #getOrderNum()} with one.
     *
     * @param connection the Paradox connection.
     * @param type       the Paradox field type.
     */
    public ParadoxField(final ParadoxConnection connection, final byte type) {
        this(connection, type, 1);
    }

    /**
     * Creates a new instance.
     *
     * @param connection the Paradox connection.
     * @param type       the Paradox field type.
     * @param orderNum   order number to start.
     */
    public ParadoxField(final ParadoxConnection connection, final byte type, final int orderNum) {
        this.connection = connection;
        this.type = type;
        this.orderNum = orderNum;
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
     * Gets the field type.
     *
     * @return the field type.
     */
    public byte getType() {
        return this.type;
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
     */
    public void setSize(final int size) {
        this.realSize = size;
        int sqlType = this.getSqlType();
        if ((sqlType == Types.CLOB) || (sqlType == Types.BLOB)) {
            this.realSize -= BLOB_SIZE_PADDING;
            this.size = size;
        } else if (type == ParadoxFieldType.CURRENCY.getType()) {
            this.precision = CURRENCY_PRECISION;
            this.size = size;
        } else if (type == ParadoxFieldType.BCD.getType()) {
            this.precision = size;
            this.size = BCDField.BCD_SIZE;
        } else if (sqlType == Types.NUMERIC) {
            this.precision = NUMBER_PRECISION;
            this.size = size;
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
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        if (this.table == null) {
            return this.name;
        }

        return this.table.getName() + "." + this.name;
    }

    /**
     * Gets the file size in file.
     *
     * @return the file size in file.
     */
    public int getRealSize() {
        return this.realSize;
    }

    /**
     * Gets the SQL field type.
     *
     * @return the SQL field type.
     */
    public int getSqlType() {
        return ParadoxFieldType.getSQLTypeByType(this.type);
    }

    public ParadoxConnection getConnection() {
        return connection;
    }

    /**
     * Gets the field auto increment status.
     *
     * @return true if this field is auto increment.
     */
    boolean isAutoIncrement() {
        return this.type == ParadoxFieldType.AUTO_INCREMENT.getType();
    }

    public int getPrecision() {
        return precision;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ParadoxField that = (ParadoxField) o;
        return checked == that.checked &&
                orderNum == that.orderNum &&
                realSize == that.realSize &&
                size == that.size &&
                type == that.type &&
                Objects.equals(alias, that.alias) &&
                Objects.equals(expression, that.expression) &&
                Objects.equals(joinName, that.joinName) &&
                Objects.equals(name, that.name) &&
                Objects.equals(table, that.table);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alias, checked, expression, joinName, name, orderNum, realSize, size, table, type);
    }
}
