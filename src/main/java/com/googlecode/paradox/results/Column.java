/*
 * ParadoxDataFile.java
 *
 * 03/12/2009
 * Copyright (C) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.results;

import com.googlecode.paradox.ParadoxResultSet;
import com.googlecode.paradox.metadata.ParadoxField;

import java.sql.SQLException;
import java.sql.Types;

/**
 * Column values from a ResultSet.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @see ParadoxResultSet
 * @since 1.0
 */
public final class Column {

    /**
     * If this column is auto incremented.
     */
    private boolean autoIncrement;

    /**
     * If this column is currency type.
     */
    private boolean currency;

    /**
     * The paradox field associated to this field.
     */
    private ParadoxField field;

    /**
     * Column index.
     */
    private int index;

    /**
     * The column max value.
     */
    private int maxSize = 255;

    /**
     * Column Name.
     */
    private String name;

    /**
     * If this field can be null.
     */
    private boolean nullable = true;

    /**
     * The field precision.
     */
    private int precision;

    /**
     * If this field is read only.
     */
    private boolean readOnly;

    /**
     * The field scale.
     */
    private int scale;

    /**
     * If this field is searchable.
     */
    private boolean searchable = true;

    /**
     * If this field has sign.
     */
    private boolean signed;

    /**
     * The tables name.
     */
    private String tableName;

    /**
     * The SQL data type.
     *
     * @see Types
     */
    private int type;

    /**
     * If this field is writable.
     */
    private boolean writable;

    /**
     * Create a new instance.
     */
    public Column() {
        // No need to change a field.
    }

    /**
     * Create a new instance.
     *
     * @param field
     *         the paradox field.
     */
    public Column(final ParadoxField field) {
        this(field.getName(), field.getType());
        this.field = field;
    }

    /**
     * Create a new instance.
     *
     * @param name
     *         the field name.
     * @param type
     *         the field type.
     */
    public Column(final String name, final int type) {
        this.name = name;
        setType(type);
    }

    /**
     * Gets the field type description.
     *
     * @param type
     *         the field type.
     * @return the type description.
     * @throws SQLException
     *         if is an invalid type.
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
        return field;
    }

    /**
     * Gets the field index.
     *
     * @return the field index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Gets the field max size.
     *
     * @return the field max size.
     */
    public int getMaxSize() {
        return maxSize;
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
     * Gets the field precision.
     *
     * @return the field precision.
     */
    public int getPrecision() {
        return precision;
    }

    /**
     * Gets the field scale.
     *
     * @return the field scale.
     */
    public int getScale() {
        return scale;
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
     * Gets the field SQL type.
     *
     * @return the field SQL type.
     */
    public int getType() {
        return type;
    }

    /**
     * Gets if this field is auto increment.
     *
     * @return true if this field is auto incremented.
     */
    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    /**
     * Gets if this field is a currency.
     *
     * @return true if this field is a current.
     */
    public boolean isCurrency() {
        return currency;
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
        return readOnly;
    }

    /**
     * Gets if this field can be search.
     *
     * @return true if this field can be search.
     */
    public boolean isSearchable() {
        return searchable;
    }

    /**
     * Gets if this field have sign.
     *
     * @return true if this field have sign.
     */
    public boolean isSigned() {
        return signed;
    }

    /**
     * Gets if this field is writable.
     *
     * @return true if this field is writable.
     */
    public boolean isWritable() {
        return writable;
    }

    /**
     * Sets the auto increment value.
     *
     * @param autoIncrement
     *         the auto increment value to set.
     */
    public void setAutoIncrement(final boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    /**
     * Sets if this field is a current.
     *
     * @param currency
     *         the currency to set.
     */
    public void setCurrency(final boolean currency) {
        this.currency = currency;
    }

    /**
     * Sets the paradox field.
     *
     * @param field
     *         the paradox field to set.
     */
    public void setField(final ParadoxField field) {
        this.field = field;
    }

    /**
     * Sets the field index.
     *
     * @param index
     *         the index to set.
     */
    public void setIndex(final int index) {
        this.index = index;
    }

    /**
     * Sets the field max size.
     *
     * @param maxSize
     *         the max size to set.
     */
    public void setMaxSize(final int maxSize) {
        this.maxSize = maxSize;
    }

    /**
     * Sets the field name
     *
     * @param name
     *         the field name to set.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Sets if this field can be null.
     *
     * @param nullable
     *         the nullable to set.
     */
    public void setNullable(final boolean nullable) {
        this.nullable = nullable;
    }

    /**
     * Sets the field precision.
     *
     * @param precision
     *         the precision to set.
     */
    public void setPrecision(final int precision) {
        this.precision = precision;
    }

    /**
     * Sets if this field is read only.
     *
     * @param readOnly
     *         the read only to set.
     */
    public void setReadOnly(final boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * Sets the field scale.
     *
     * @param scale
     *         the scale to set.
     */
    public void setScale(final int scale) {
        this.scale = scale;
    }

    /**
     * Sets if this field is searchable.
     *
     * @param searchable
     *         the searchable to set.
     */
    public void setSearchable(final boolean searchable) {
        this.searchable = searchable;
    }

    /**
     * Sets if this field has sign.
     *
     * @param signed
     *         the signed to set.
     */
    public void setSigned(final boolean signed) {
        this.signed = signed;
    }

    /**
     * Sets the tables name
     *
     * @param tableName
     *         the tables name to set.
     */
    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }

    /**
     * Sets the field SQL type.
     *
     * @param type
     *         the field SQL type to set.
     */
    public void setType(final int type) {
        this.type = type;

        if (type == ParadoxFieldType.NUMERIC.getType()) {
            scale = 2;
        } else if (type == ParadoxFieldType.DOUBLE.getType()) {
            currency = true;
            precision = 9;
        } else if (type == ParadoxFieldType.AUTO_INCREMENT.getType()) {
            autoIncrement = true;
            precision = 9;
        }
    }

    /**
     * Sets if this field is writable.
     *
     * @param writable
     *         the writable to set.
     */
    public void setWritable(final boolean writable) {
        this.writable = writable;
    }

}
