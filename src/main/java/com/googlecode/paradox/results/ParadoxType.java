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

import com.googlecode.paradox.utils.Constants;

import java.math.BigDecimal;
import java.sql.*;

/**
 * Stores the Paradox field types and SQL Types.
 *
 * @version 2.3
 * @see SQLType
 * @since 1.3
 */
public enum ParadoxType implements SQLType {

    /**
     * The VARCHAR type.
     */
    VARCHAR(1, Types.VARCHAR, true, String.class, 0, 0, Constants.MAX_STRING_SIZE, true),

    /**
     * The CHAR type.
     */
    CHAR(1, Types.CHAR, false, String.class, 0, 0, Constants.MAX_STRING_SIZE, true),

    /**
     * The date type.
     */
    DATE(2, Types.DATE, true, Date.class, 0, 0, 0x04, true),

    /**
     * The integer type.
     */
    INTEGER(3, Types.INTEGER, true, Integer.class, 0, 0, 0x02, true),

    /**
     * The integer type. Variant 2.
     */
    LONG(4, Types.INTEGER, true, Integer.class, 9, 0, 0x04, true),

    /**
     * The double type.
     */
    CURRENCY(5, Types.DOUBLE, true, Double.class, 10, 2, 0x08, true),

    /**
     * The numeric type.
     */
    NUMBER(6, Types.NUMERIC, true, Double.class, 15, 6,0x08, true),

    /**
     * The numeric type.
     */
    NUMERIC(6, Types.NUMERIC, true, Double.class, 15, 6, 0x08, true),

    /**
     * The numeric type.
     */
    DECIMAL(6, Types.DECIMAL, false, Double.class, 15, 6, 0x08, true),

    /**
     * The boolean type.
     */
    BOOLEAN(9, Types.BOOLEAN, true, Boolean.class, 0, 0, 1, true),

    /**
     * The CLOB type.
     */
    MEMO(0xC, Types.CLOB, true, Clob.class, 0, 0, 0, true),

    /**
     * the BLOB type.
     */
    BLOB(0xD, Types.BLOB, true, byte[].class, 0, 0, 0, true),

    /**
     * The VARCHAR type, variant 2.
     */
    FORMATTED_MEMO(0xE, Types.CLOB, true, Clob.class, 0, 0, 0, true),

    /**
     * the BLOB type, variant 2.
     */
    OLE(0xF, Types.BLOB, true, byte[].class, 0, 0, 0, true),

    /**
     * The graphics type.
     * <p>
     * It's really a BLOB that contains an image file.
     */
    GRAPHIC(0x10, Types.BLOB, true, byte[].class, 0, 0, 0, true),

    /**
     * The time type.
     */
    TIME(0x14, Types.TIME, true, Time.class, 0, 0, 0x04, true),

    /**
     * The time with timezone type.
     */
    TIME_WITH_TIMEZONE(0x14, Types.TIME_WITH_TIMEZONE, false, Time.class, 0, 0, 0x04, true),

    /**
     * The TIMESTAMP type.
     */
    TIMESTAMP(0x15, Types.TIMESTAMP, true, Timestamp.class, 0, 0, 0x08, true),

    /**
     * The timestamp with timezone type.
     */
    TIMESTAMP_WITH_TIMEZONE(0x15, Types.TIMESTAMP_WITH_TIMEZONE, false, Timestamp.class, 0, 0, 0x08, true),

    /**
     * The auto increment type.
     */
    AUTO_INCREMENT(0x16, Types.INTEGER, true, Integer.class, 9, 0, 0x04, true),

    /**
     * The binary type.
     */
    BCD(0x17, Types.NUMERIC, true, BigDecimal.class, 0, 0, 32, true),

    /**
     * The BLOB type, variant 3.
     */
    BYTES(0x18, Types.BINARY, true, byte[].class, 0, 0, 255, true),

    /**
     * Null Type.
     */
    NULL(0x0, Types.NULL, false, Object.class, 0, 0, 4, false);

    /**
     * Current values cache.
     */
    private static final ParadoxType[] VALUES = ParadoxType.values();

    /**
     * The SQL Type.
     */
    private final int sqlType;

    /**
     * The Paradox type.
     */
    private final int type;

    /**
     * If type is vendor specific.
     */
    private final boolean vendorSpecific;

    /**
     * Field associated Java class.
     */
    private final Class<?> javaClass;

    /**
     * Default field precision.
     */
    private final int precision;

    /**
     * Default field scale.
     */
    private final int scale;

    /**
     * Default field size.
     */
    private final int size;

    /**
     * Is searchable.
     */
    private final boolean searchable;

    /**
     * Creates a new instance.
     *
     * @param type           the Paradox type.
     * @param sqlType        the SQL type.
     * @param vendorSpecific if this value is vendor specific.
     * @param javaClass      the associated Java class.
     * @param precision      the default field precision.
     * @param size           the default field size.
     * @param searchable     if the field type is searchable.
     */
    ParadoxType(final int type, final int sqlType, final boolean vendorSpecific, final Class<?> javaClass,
                final int precision, final int scale, final int size, final boolean searchable) {
        this.type = (byte) type;
        this.sqlType = sqlType;
        this.vendorSpecific = vendorSpecific;
        this.javaClass = javaClass;
        this.precision = precision;
        this.scale = scale;
        this.size = size;
        this.searchable = searchable;
    }

    /**
     * Gets the field name by Paradox type.
     *
     * @param type the type value.
     * @return the field type name.
     */
    public static ParadoxType valueOfVendor(final int type) {
        for (final ParadoxType typeName : ParadoxType.VALUES) {
            if (typeName.vendorSpecific && typeName.type == type) {
                return typeName;
            }
        }

        return NULL;
    }

    /**
     * Returns the {@code JDBCType} that corresponds to the specified
     * {@code Types} value
     *
     * @param type {@code Types} value
     * @return The {@code JDBCType} constant
     * @throws IllegalArgumentException if this enum type has no constant with
     *                                  the specified {@code Types} value
     * @see Types
     */
    public static ParadoxType valueOf(final int type) {
        for (ParadoxType sqlType : ParadoxType.class.getEnumConstants()) {
            if (type == sqlType.sqlType) {
                return sqlType;
            }
        }

        throw new IllegalArgumentException("The type " + type + " is not a valid Types.java value.");
    }

    /**
     * Gets the type based on a Java class.
     *
     * @param desiredClass the class to use in search.
     * @return the associated type.
     */
    public static ParadoxType valueOf(final Class<?> desiredClass) {
        for (ParadoxType sqlType : ParadoxType.class.getEnumConstants()) {
            if (desiredClass == sqlType.javaClass) {
                return sqlType;
            }
        }

        throw new IllegalArgumentException("The class " + desiredClass + " is not a supported class name.");
    }

    /**
     * Returns the name of the vendor that supports this data type.
     *
     * @return The name of the vendor for this data type which is
     * {@literal java.sql} for JDBCType.
     */
    @Override
    public String getVendor() {
        return "com.googlecode.paradox";
    }

    /**
     * Returns the vendor specific type number for the data type.
     *
     * @return An Integer representing the data type. For {@code JDBCType},
     * the value will be the same value as in {@code Types} for the data type.
     */
    @Override
    public Integer getVendorTypeNumber() {
        return type;
    }

    /**
     * Gets the SQL type.
     *
     * @return the SQL type.
     */
    public int getSQLType() {
        return this.sqlType;
    }

    /**
     * {@inheritDoc }
     *
     * @return The name of this {@code SQLType}.
     */
    @Override
    public String getName() {
        return JDBCType.valueOf(sqlType).name();
    }

    /**
     * Gets the corresponding Java class for this type.
     *
     * @return the corresponding Java class for this type.
     */
    public Class<?> getJavaClass() {
        return javaClass;
    }

    /**
     * Gets the default field precision.
     *
     * @return the default field precision.
     */
    public int getPrecision() {
        return precision;
    }

    /**
     * Gets the default field size.
     *
     * @return the default field size.
     */
    public int getSize() {
        return size;
    }

    /**
     * Gets the default field scale.
     *
     * @return the default field scale.
     */
    public int getScale() {
        return scale;
    }

    /**
     * If the field type is searchable.
     *
     * @return the field type is searchable.
     */
    public boolean isSearchable() {
        return searchable;
    }

    /**
     * Gets the type radix.
     *
     * @return the type radix.
     */
    public Integer getRadix() {
        switch (this) {
            case INTEGER:
            case LONG:
            case CURRENCY:
            case DECIMAL:
            case NUMBER:
            case NUMERIC:
            case BCD:
            case AUTO_INCREMENT:
                return 0xA;
            default:
                return null;
        }
    }

    /**
     * Gets if the type is a numeric.
     *
     * @return <code>true</code> if the type is a numeric.
     */
    public boolean isNumeric() {
        return getRadix() != null;
    }
}
