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
package com.googlecode.paradox.results;

import com.googlecode.paradox.ParadoxResultSet;
import com.googlecode.paradox.function.FunctionNode;
import com.googlecode.paradox.metadata.ParadoxDataFile;
import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.planner.nodes.ValueNode;

import java.sql.JDBCType;
import java.sql.Types;
import java.util.Objects;

/**
 * Column values from a ResultSet.
 *
 * @version 1.2
 * @see ParadoxResultSet
 * @since 1.0
 */
public final class Column {

    /**
     * The paradox field associated to this field.
     */
    private ParadoxField field;

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
     * The SQL data type.
     *
     * @see Types
     */
    private int type;

    /**
     * Fixed value.
     */
    private Object value;

    /**
     * The function associated to this value.
     */
    private FunctionNode function;

    /**
     * Create a new instance.
     *
     * @param field the paradox field.
     */
    public Column(final ParadoxField field) {
        this(field.getName(), field.getSqlType());
        this.field = field;
        this.precision = field.getPrecision();
    }

    public Column(final ValueNode node) {
        this(node.getAlias(), node.getSqlType());
        this.value = node.getName();
    }

    public Column(final FunctionNode node) {
        this(node.getAlias(), node.getSqlType());
        this.function = node;
    }

    /**
     * Create a new instance.
     *
     * @param name the field name.
     * @param type the field type.
     */
    public Column(final String name, final int type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Get if this column is from the table.
     *
     * @param table the table .
     * @return <code>true</code> if this column is from this table.
     */
    public boolean isThis(final ParadoxDataFile table) {
        return this.field != null
                && this.field.getTable().getName().equalsIgnoreCase(table.getName())
                && this.field.getTable().getSchemaName().equalsIgnoreCase(table.getSchemaName());
    }

    /**
     * Gets the field type description.
     *
     * @param type the field type.
     * @return the type description.
     */
    public static String getTypeName(final int type) {
        return JDBCType.valueOf(type).name();
    }

    /**
     * Gets the paradox field.
     *
     * @return the paradox field.
     */
    public ParadoxField getField() {
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
     * Gets the field name.
     *
     * @return the field name.
     */
    public String getName() {
        return this.name;
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
     * Gets the field scale.
     *
     * @return the field scale.
     */
    public int getScale() {
        return this.precision;
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
    public int getType() {
        return this.type;
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
        return field != null && field.getType() == ParadoxFieldType.AUTO_INCREMENT.getType();
    }

    /**
     * Gets if this field is a currency.
     *
     * @return true if this field is a current.
     */
    public boolean isCurrency() {
        return field != null && field.getType() == ParadoxFieldType.CURRENCY.getType();
    }

    /**
     * Gets if this field can be null.
     *
     * @return true if this field can be null.
     */
    public boolean isNullable() {
        return field == null || field.getType() != ParadoxFieldType.AUTO_INCREMENT.getType();
    }

    /**
     * Gets if this field is read only.
     *
     * @return true if this field is read only.
     */
    public boolean isReadOnly() {
        return true;
    }

    /**
     * Gets if this field can be search.
     *
     * @return true if this field can be search.
     */
    public boolean isSearchable() {
        return type != Types.BLOB && type != Types.BINARY;
    }

    /**
     * Gets if this field have sign.
     *
     * @return true if this field have sign.
     */
    public boolean isSigned() {
        return type == Types.DECIMAL || type == Types.NUMERIC || type == Types.DOUBLE || type == Types.INTEGER;
    }

    /**
     * Gets if this field is writable.
     *
     * @return true if this field is writable.
     */
    public boolean isWritable() {
        return false;
    }

    /**
     * Sets the field index.
     *
     * @param index the index to set.
     */
    public void setIndex(final int index) {
        this.index = index;
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
     * Sets the field precision.
     *
     * @param precision the precision to set.
     */
    public void setPrecision(final int precision) {
        this.precision = precision;
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
     * Sets the SQL type.
     *
     * @param type the SQL type.
     */
    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        if (field == null) {
            return name;
        } else if (!field.getName().equals(name)) {
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
        return Objects.equals(field, column.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field);
    }
}
