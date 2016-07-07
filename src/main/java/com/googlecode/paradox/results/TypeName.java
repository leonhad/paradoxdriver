/*
 * TypeName.java
 *
 * 07/06/2016
 * Copyright (C) 2016 Leonardo Alves da Costa
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

import java.sql.SQLException;
import java.sql.Types;

import com.googlecode.paradox.utils.SQLStates;

/**
 * Stores the SQL type names.
 * 
 * @author Leonardo Alves da Costa
 * @since 1.3
 * @version 1.0
 */
public enum TypeName {

    /**
     * The binary type.
     */
    BINARY(Types.BINARY, "BINARY"),

    /**
     * The BLOB type.
     */
    BLOB(Types.BLOB, "BLOB"),

    /**
     * The boolean type.
     */
    BOOLEAN(Types.BOOLEAN, "BOOLEAN"),

    /**
     * The date type.
     */
    DATE(Types.DATE, "DATE"),

    /**
     * The double type.
     */
    DOUBLE(Types.DOUBLE, "DOUBLE"),

    /**
     * The integer type.
     */
    INTEGER(Types.INTEGER, "INTEGER"),

    /**
     * The numeric type.
     */
    NUMERIC(Types.NUMERIC, "NUMERIC"),

    /**
     * The time type.
     */
    TIME(Types.TIME, "TIME"),

    /**
     * The TIMESTAMP type.
     */
    TIMESTAMP(Types.TIMESTAMP, "TIMESTAMP"),

    /**
     * The VARCHAR type.
     */
    VARCHAR(Types.VARCHAR, "VARCHAR");

    private static final TypeName[] VALUES = values();

    /**
     * The type name.
     */
    private final String name;

    /**
     * The SQL Type.
     */
    private final int type;

    /**
     * Creates a new instance.
     * 
     * @param type
     *            the SQL type.
     * @param name
     *            the type name.
     */
    private TypeName(final int type, final String name) {
        this.type = type;
        this.name = name;
    }

    /**
     * Gets the field name by its type.
     * 
     * @param type
     *            the type value.
     * @return the field type name.
     * @throws SQLException
     *             in case of invalid type.
     */
    public static String getName(final int type) throws SQLException {
        for (final TypeName typeName : VALUES) {
            if (typeName.getType() == type) {
                return typeName.getName();
            }
        }
        throw new SQLException("Type not found: " + type, SQLStates.TYPE_NOT_FOUND.getValue());
    }

    /**
     * Gets the type name.
     * 
     * @return the type name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the SQL type.
     * 
     * @return the SQL type.
     */
    public int getType() {
        return type;
    }
}
