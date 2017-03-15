/*
 * FieldValue.java 03/14/2009 Copyright (C) 2009 Leonardo Alves da Costa This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.data.table.value;

import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.utils.SQLStates;
import java.sql.Date;
import java.sql.SQLDataException;
import java.sql.Time;
import java.sql.Types;

/**
 * Stores the database values in Java format.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.0
 */
public final class FieldValue {

    /**
     * Invalid field message error.
     */
    private static final String ERROR_INVALID_TYPE = "Invalid field type.";
    /**
     * Reference on field.
     */
    private ParadoxField field;
    /**
     * Value type in database.
     *
     * @see Types
     */
    private final int type;
    /**
     * Database value converted to Java.
     */
    private Object value;

    /**
     * Constructor used for NULL values.
     *
     * @param type
     *            field type.
     */
    public FieldValue(final int type) {
        this.type = type;
    }

    /**
     * Store a database value already loaded in Java format.
     *
     * @param value
     *            Java value.
     * @param type
     *            Database value type.
     */
    public FieldValue(final Object value, final int type) {
        this.type = type;
        this.value = value;
    }

    /**
     * Check for value type and return an Boolean value.
     *
     * @return a valid Boolean value.
     * @throws SQLDataException
     *             if this is not a Boolean value.
     */
    public Boolean getBoolean() throws SQLDataException {
        if (type != Types.BOOLEAN) {
            throw new SQLDataException(FieldValue.ERROR_INVALID_TYPE, SQLStates.INVALID_FIELD_VALUE.getValue());
        }
        return (Boolean) value;
    }

    /**
     * Check for value type and return an Date value.
     *
     * @return a valid Date value.
     * @throws SQLDataException
     *             if this is not a Date value.
     */
    public Date getDate() throws SQLDataException {
        if (type != Types.DATE) {
            throw new SQLDataException(FieldValue.ERROR_INVALID_TYPE, SQLStates.INVALID_FIELD_VALUE.getValue());
        }
        return (Date) value;
    }

    /**
     * Gets the Paradox field.
     *
     * @return the Paradox field.
     */
    public ParadoxField getField() {
        return field;
    }

    /**
     * Check for value type and return an Number value.
     *
     * @return a valid Number value.
     * @throws SQLDataException
     *             if this is not a numeric value.
     */
    public Number getNumber() throws SQLDataException {
        switch (type) {
            case Types.INTEGER:
            case Types.BIGINT:
            case Types.DOUBLE:
                return (Number) value;
            default:
                throw new SQLDataException(FieldValue.ERROR_INVALID_TYPE, SQLStates.INVALID_FIELD_VALUE.getValue());
        }
    }

    /**
     * Check for value type and return an Time value.
     *
     * @return a valid Time value.
     * @throws SQLDataException
     *             if this is not a Time value.
     */
    public Time getTime() throws SQLDataException {
        if (type != Types.TIME) {
            throw new SQLDataException(FieldValue.ERROR_INVALID_TYPE, SQLStates.INVALID_FIELD_VALUE.getValue());
        }
        return (Time) value;
    }

    /**
     * The Java {@link Types} value.
     *
     * @return The Java {@link Types} value.
     */
    public int getType() {
        return type;
    }

    /**
     * Return the field value in Java format.
     *
     * @return the field value in Java format.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Check for null value.
     *
     * @return true if this value is NULL.
     */
    public boolean isNull() {
        return value == null;
    }

    /**
     * Sets the Paradox field.
     *
     * @param field
     *            the Paradox field.
     */
    public void setField(final ParadoxField field) {
        this.field = field;
    }
}
