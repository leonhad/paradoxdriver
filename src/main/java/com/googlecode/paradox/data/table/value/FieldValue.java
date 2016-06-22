package com.googlecode.paradox.data.table.value;

import java.sql.Date;
import java.sql.SQLDataException;
import java.sql.Time;
import java.sql.Types;

import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.utils.SQLStates;

/**
 * Stores the database values in Java format.
 *
 * @author Leonardo Alves da Costa
 * @since 1.0
 * @version 1.0
 */
public class FieldValue {
    /**
     * Database value converted to Java
     */
    private Object value;
    /**
     * Value type in database
     *
     * @see Types
     */
    private final int type;

    /**
     * Reference on field
     */
    private ParadoxField field;

    /**
     * Store a database value already loaded in Java format.
     *
     * @param value
     *            Java value
     * @param type
     *            Database value type
     */
    public FieldValue(final Object value, final int type) {
        this.type = type;
        this.value = value;
    }

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
     * Return the field value in Java format
     *
     * @return the field value in Java format
     */
    public Object getValue() {
        return value;
    }

    /**
     * The Java {@link Types} value.
     *
     * @return The Java {@link Types} value.
     */
    public int getType() {
        return type;
    }

    public ParadoxField getField() {
        return field;
    }

    public void setField(final ParadoxField newVal) {
        field = newVal;
    }

    /**
     * Check for null value
     *
     * @return true if this value is NULL
     */
    public boolean isNull() {
        return value == null;
    }

    /**
     * Check for value type and return an Boolean value
     *
     * @return a valid Boolean value
     * @throws SQLDataException
     *             if this is not a Boolean value
     */
    public Boolean getBoolean() throws SQLDataException {
        if (type != Types.BOOLEAN) {
            throw new SQLDataException("Invalid field type.", SQLStates.INVALID_FIELD_VALUE);
        }
        return (Boolean) value;
    }

    /**
     * Check for value type and return an Number value
     *
     * @return a valid Number value
     * @throws SQLDataException
     *             if this is not a numeric value
     */
    public Number getNumber() throws SQLDataException {
        switch (type) {
        case Types.INTEGER:
        case Types.BIGINT:
        case Types.DOUBLE:
            return (Number) value;
        default:
            throw new SQLDataException("Invalid field type.", SQLStates.INVALID_FIELD_VALUE);
        }
    }

    /**
     * Check for value type and return an Time value
     *
     * @return a valid Time value
     * @throws SQLDataException
     *             if this is not a Time value
     */
    public Time getTime() throws SQLDataException {
        if (type != Types.TIME) {
            throw new SQLDataException("Invalid field type.", SQLStates.INVALID_FIELD_VALUE);
        }
        return (Time) value;
    }

    /**
     * Check for value type and return an Date value
     *
     * @return a valid Date value
     * @throws SQLDataException
     *             if this is not a Date value
     */
    public Date getDate() throws SQLDataException {
        if (type != Types.DATE) {
            throw new SQLDataException("Invalid field type.", SQLStates.INVALID_FIELD_VALUE);
        }
        return (Date) value;
    }
}
