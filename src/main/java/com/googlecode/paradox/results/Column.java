/*
 * Copyright (c) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.results;

import com.googlecode.paradox.ParadoxResultSet;
import com.googlecode.paradox.data.field.BCDField;
import com.googlecode.paradox.metadata.Field;
import com.googlecode.paradox.metadata.Table;
import com.googlecode.paradox.planner.nodes.FunctionNode;
import com.googlecode.paradox.planner.nodes.ParameterNode;
import com.googlecode.paradox.planner.nodes.ValueNode;

import java.sql.Types;
import java.util.Objects;

/**
 * Column values from a ResultSet.
 *
 * @version 1.10
 * @see ParadoxResultSet
 * @since 1.0
 */
@SuppressWarnings("java:S1448")
public final class Column {

    private final boolean nullable;
    /**
     * The paradox field associated to this field.
     */
    private Field field;
    /**
     * Column index.
     */
    private int index;
    /**
     * Column Name.
     */
    private String name;
    /**
     * The field precision.
     */
    private int precision;
    /**
     * The field scale.
     */
    private int scale;
    /**
     * Column size.
     */
    private int size;
    /**
     * The SQL data type.
     *
     * @see Types
     */
    private ParadoxType type;
    /**
     * Column remarks.
     */
    private String remarks;
    /**
     * The column type.
     */
    private int columnType;
    /**
     * Is this a hidden column.
     */
    private boolean hidden;
    /**
     * Single value.
     */
    private Object value;
    /**
     * The function associated to this value.
     */
    private FunctionNode function;
    /**
     * A parameter value.
     */
    private ParameterNode parameter;

    /**
     * Create a new instance.
     *
     * @param field the paradox field.
     */
    public Column(final Field field) {
        this(field.getAlias(), field.getType());
        this.field = field;
        this.precision = field.getPrecision();
        this.scale = field.getScale();
    }

    /**
     * Creates a new instance.
     *
     * @param parameter the parameter node.
     */
    public Column(final ParameterNode parameter) {
        this(parameter.getAlias(), ParadoxType.NULL);
        this.parameter = parameter;
    }

    /**
     * Creates a new instance.
     *
     * @param node the value node.
     */

    public Column(final ValueNode node) {
        this(node.getAlias(), node.getType());
        this.value = node.getName();
    }

    /**
     * Creates a new instance.
     *
     * @param node the function node.
     */
    public Column(final FunctionNode node) {
        this(node.getAlias(), node.getType());
        this.function = node;
    }

    /**
     * Creates a new instance.
     *
     * @param name       the column name.
     * @param type       the column type.
     * @param remarks    the column remarks.
     * @param index      the column index.
     * @param nullable   the column nullable.
     * @param columnType the column return type (IN ou RESULT).
     */
    @SuppressWarnings("java:S107")
    public Column(final String name, final ParadoxType type, final String remarks, final int index,
                  final boolean nullable, final int columnType) {
        this.name = name;
        this.type = type;
        this.precision = type.getPrecision();
        this.scale = type.getScale();
        this.size = type.getSize();
        this.remarks = remarks;
        this.index = index;
        this.nullable = nullable;
        this.columnType = columnType;
    }

    /**
     * Create a new instance.
     *
     * @param name the field name.
     * @param type the field type.
     */
    public Column(final String name, final ParadoxType type) {
        this.name = name;
        this.type = type;
        this.nullable = type != ParadoxType.AUTO_INCREMENT;
    }

    /**
     * Get if this column is from the table.
     *
     * @param table the table .
     * @return <code>true</code> if this column is from this table.
     */
    public boolean isThis(final Table table) {
        return this.field != null
                && this.field.getTable().getName().equalsIgnoreCase(table.getName())
                && this.field.getTable().getSchemaName().equalsIgnoreCase(table.getSchemaName());
    }

    /**
     * Gets the paradox field.
     *
     * @return the paradox field.
     */
    public Field getField() {
        return this.field;
    }

    /**
     * Gets the field index.
     *
     * @return the field index.
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * Sets the field index.
     *
     * @param index the index to set.
     */
    public void setIndex(final int index) {
        this.index = index;

        if (this.function != null) {
            this.function.setIndex(index);
        }
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
     * Sets the field name
     *
     * @param name the field name to set.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets the field precision.
     *
     * @return the field precision.
     */
    public int getPrecision() {
        return this.precision;
    }

    /**
     * Sets the field precision.
     *
     * @param precision the precision to set.
     */
    public void setPrecision(final int precision) {
        this.precision = precision;
    }

    /**
     * Gets the field scale.
     *
     * @return the field scale.
     */
    public int getScale() {
        if (isSigned()) {
            return this.scale;
        } else {
            return 0;
        }
    }

    /**
     * Gets the tables name.
     *
     * @return the tables name.
     */
    public String getTableName() {
        return this.field.getTable().getName();
    }

    /**
     * Gets the field SQL type.
     *
     * @return the field SQL type.
     */
    public ParadoxType getType() {
        return this.type;
    }

    /**
     * Sets the type.
     *
     * @param type the type.
     */
    public void setType(final ParadoxType type) {
        this.type = type;
    }

    /**
     * Gets the column value.
     *
     * @return the column value.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Gets if this field is auto increment.
     *
     * @return true if this field is auto incremented.
     */
    public boolean isAutoIncrement() {
        return field != null && field.getType() == ParadoxType.AUTO_INCREMENT;
    }

    /**
     * Gets if this field can be null.
     *
     * @return true if this field can be null.
     */
    public boolean isNullable() {
        return nullable;
    }

    /**
     * Gets if this field is read only.
     *
     * @return true if this field is read only.
     */
    public boolean isReadOnly() {
        return isAutoIncrement();
    }

    /**
     * Gets if this field is a currency.
     *
     * @return true if this field is a current.
     */
    public boolean isCurrency() {
        return field != null && field.getType() == ParadoxType.CURRENCY;
    }

    /**
     * Gets if this field can be searched.
     *
     * @return true if this field can be searched.
     */
    public boolean isSearchable() {
        return type.isSearchable();
    }

    /**
     * Gets if this field is writable.
     *
     * @return true if this field is writable.
     */
    public boolean isWritable() {
        return !isAutoIncrement();
    }

    /**
     * Gets the column octets size.
     *
     * @return the column octets size.
     */
    public Integer getOctets() {
        if (type == ParadoxType.BCD) {
            return BCDField.BCD_SIZE;
        }

        return size;
    }

    /**
     * Gets the hidden status.
     *
     * @return <code>true</code> if this column is hidden in ResultSet statement.
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Sets the hidden status of this column.
     *
     * @param hidden <code>true</code> if this column is hidden.
     */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    /**
     * Gets the function associated to this value.
     *
     * @return the function associated to this value.
     */
    public FunctionNode getFunction() {
        return function;
    }

    /**
     * Sets the column function.
     *
     * @param function the column function.
     */
    public void setFunction(FunctionNode function) {
        this.function = function;
    }

    /**
     * Sets the column size.
     *
     * @param size the column size.
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Gets if this field have sign.
     *
     * @return true if this field have sign.
     */
    public boolean isSigned() {
        return type == ParadoxType.DECIMAL || type == ParadoxType.NUMBER || type == ParadoxType.CURRENCY || type == ParadoxType.INTEGER;
    }

    /**
     * Gets the column size.
     *
     * @return the column size.
     */
    public int getSize() {
        return size;
    }

    /**
     * Gets the columns remarks.
     *
     * @return the column remarks.
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * Gets the column type.
     *
     * @return the column type.
     */
    public int getColumnType() {
        return columnType;
    }

    /**
     * Gets the parameter node.
     *
     * @return the parameter node.
     */
    public ParameterNode getParameter() {
        return parameter;
    }

    /**
     * Gets the second pass status.
     *
     * @return the second pass status.
     */
    public boolean isSecondPass() {
        if (function != null) {
            return function.isSecondPass();
        }

        return false;
    }

    /**
     * Gets the column radix.
     *
     * @return the column radix.
     */
    public Integer getRadix() {
        return type.getRadix();
    }

    @Override
    public String toString() {
        if (field == null) {
            return name;
        } else if (!Objects.equals(field.getName(), name)) {
            return field.toString() + " AS " + name;
        }

        return field.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Column column = (Column) o;
        boolean ret;
        if (field != null) {
            ret = Objects.equals(field, column.field);
        } else if (function != null) {
            ret = function.equals(column.function);
        } else {
            ret = Objects.equals(name, column.name);
        }

        return ret;
    }

    @Override
    public int hashCode() {
        if (field != null) {
            return Objects.hash(field);
        } else if (function != null) {
            return function.hashCode();
        } else {
            return Objects.hash(name);
        }
    }
}
