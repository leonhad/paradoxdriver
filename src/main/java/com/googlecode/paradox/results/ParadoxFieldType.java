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

import java.sql.Types;

/**
 * Stores the Paradox field types and SQL Types.
 *
 * @version 1.1
 * @since 1.3
 */
public enum ParadoxFieldType {

    /**
     * The VARCHAR type.
     */
    VARCHAR(1, Types.VARCHAR),

    /**
     * The date type.
     */
    DATE(2, Types.DATE),

    /**
     * The integer type.
     */
    INTEGER(3, Types.INTEGER),

    /**
     * The integer type. Variant 2.
     */
    LONG(4, Types.INTEGER),

    /**
     * The double type.
     */
    CURRENCY(5, Types.NUMERIC),

    /**
     * The numeric type.
     */
    NUMBER(6, Types.NUMERIC),

    /**
     * The boolean type.
     */
    BOOLEAN(9, Types.BIT),

    /**
     * The CLOB type.
     */
    MEMO(0xC, Types.CLOB),

    /**
     * the BLOB type.
     */
    BLOB(0xD, Types.BLOB),

    /**
     * The VARCHAR type, variant 2.
     */
    FORMATTED_MEMO(0xE, Types.CLOB),

    /**
     * the BLOB type, variant 2.
     */
    OLE(0xF, Types.BLOB),

    /**
     * The graphics type.
     * <p>
     * It's really a BLOB that contains an image file.
     */
    GRAPHIC(0x10, Types.BLOB),

    /**
     * The time type.
     */
    TIME(0x14, Types.TIME),

    /**
     * The TIMESTAMP type.
     */
    TIMESTAMP(0x15, Types.TIMESTAMP),

    /**
     * The auto increment type.
     */
    AUTO_INCREMENT(0x16, Types.INTEGER),

    /**
     * The binary type.
     */
    BCD(0x17, Types.NUMERIC),

    /**
     * The BLOB type, variant 3.
     */
    BYTES(0x18, Types.BINARY);

    private static final ParadoxFieldType[] VALUES = ParadoxFieldType.values();

    /**
     * The SQL Type.
     */
    private final int sqlType;

    /**
     * The Paradox type.
     */
    private final byte type;

    /**
     * Creates a new instance.
     *
     * @param type    the Paradox type.
     * @param sqlType the SQL type.
     */
    ParadoxFieldType(final int type, final int sqlType) {
        this.type = (byte) type;
        this.sqlType = sqlType;
    }

    /**
     * Gets the field name by Paradox type.
     *
     * @param type the type value.
     * @return the field type name.
     */
    public static int getSQLTypeByType(final int type) {
        for (final ParadoxFieldType typeName : ParadoxFieldType.VALUES) {
            if (typeName.getType() == type) {
                return typeName.getSQLType();
            }
        }

        return Types.OTHER;
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
     * Gets the Paradox type.
     *
     * @return the Paradox type.
     */
    public byte getType() {
        return this.type;
    }
}
