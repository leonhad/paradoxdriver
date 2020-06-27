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
import com.googlecode.paradox.metadata.ParadoxDataFile;
import com.googlecode.paradox.metadata.ParadoxField;

import java.sql.SQLException;
import java.sql.Types;

/**
 * Column values from a ResultSet.
 *
 * @author Leonardo Alves da Costa
 * @version 1.1
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
    private final int type;

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
        return this.field.getTable().getName().equalsIgnoreCase(table.getName())
                && this.field.getTable().getSchemaName().equalsIgnoreCase(table.getSchemaName());
    }

    /**
     * Gets the field type description.
     *
     * @param type the field type.
     * @return the type description.
     * @throws SQLException if is an invalid type.
     */
    public static String getTypeName(final int type) throws SQLException {
        return TypeName.getTypeName(type);
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
}
