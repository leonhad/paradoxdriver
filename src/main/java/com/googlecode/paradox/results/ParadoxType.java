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

import java.math.BigDecimal;
import java.sql.*;

/**
 * Stores the Paradox field types and SQL Types.
 *
 * @version 2.0
 * @see SQLType
 * @since 1.3
 */
public enum ParadoxType implements SQLType {

    /**
     * The VARCHAR type.
     */
    VARCHAR(1, Types.VARCHAR, true, String.class),

    /**
     * The CHAR type.
     */
    CHAR(1, Types.VARCHAR, false, String.class),

    /**
     * The date type.
     */
    DATE(2, Types.DATE, true, Date.class),

    /**
     * The integer type.
     */
    INTEGER(3, Types.INTEGER, true, Integer.class),

    /**
     * The integer type. Variant 2.
     */
    LONG(4, Types.INTEGER, true, Integer.class),

    /**
     * The double type.
     */
    CURRENCY(5, Types.DOUBLE, true, Double.class),

    /**
     * The numeric type.
     */
    NUMBER(6, Types.NUMERIC, true, Double.class),

    /**
     * The numeric type.
     */
    DECIMAL(6, Types.DECIMAL, false, Double.class),

    /**
     * The boolean type.
     */
    BOOLEAN(9, Types.BOOLEAN, true, Boolean.class),

    /**
     * The CLOB type.
     */
    MEMO(0xC, Types.CLOB, true, Clob.class),

    /**
     * the BLOB type.
     */
    BLOB(0xD, Types.BLOB, true, byte[].class),

    /**
     * The VARCHAR type, variant 2.
     */
    FORMATTED_MEMO(0xE, Types.CLOB, true, Clob.class),

    /**
     * the BLOB type, variant 2.
     */
    OLE(0xF, Types.BLOB, true, byte[].class),

    /**
     * The graphics type.
     * <p>
     * It's really a BLOB that contains an image file.
     */
    GRAPHIC(0x10, Types.BLOB, true, byte[].class),

    /**
     * The time type.
     */
    TIME(0x14, Types.TIME, true, Time.class),

    /**
     * The time with timezone type.
     */
    TIME_WITH_TIMEZONE(0x14, Types.TIME_WITH_TIMEZONE, false, Time.class),

    /**
     * The TIMESTAMP type.
     */
    TIMESTAMP(0x15, Types.TIMESTAMP, true, Timestamp.class),

    /**
     * The timestamp with timezone type.
     */
    TIMESTAMP_WITH_TIMEZONE(0x15, Types.TIMESTAMP_WITH_TIMEZONE, false, Timestamp.class),

    /**
     * The auto increment type.
     */
    AUTO_INCREMENT(0x16, Types.INTEGER, true, Integer.class),

    /**
     * The binary type.
     */
    BCD(0x17, Types.NUMERIC, true, BigDecimal.class),

    /**
     * The BLOB type, variant 3.
     */
    BYTES(0x18, Types.BINARY, true, byte[].class),

    /**
     * Null Type.
     */
    NULL(0x0, Types.NULL, false, Object.class);

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
     * Creates a new instance.
     *
     * @param type           the Paradox type.
     * @param sqlType        the SQL type.
     * @param vendorSpecific if this value is vendor specific.
     */
    ParadoxType(final int type, final int sqlType, final boolean vendorSpecific, final Class<?> javaClass) {
        this.type = (byte) type;
        this.sqlType = sqlType;
        this.vendorSpecific = vendorSpecific;
        this.javaClass = javaClass;
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
    public static ParadoxType valueOf(int type) {
        for (ParadoxType sqlType : ParadoxType.class.getEnumConstants()) {
            if (type == sqlType.sqlType)
                return sqlType;
        }

        throw new IllegalArgumentException("Type:" + type + " is not a valid Types.java value.");
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

    public Class<?> getJavaClass() {
        return javaClass;
    }
}
