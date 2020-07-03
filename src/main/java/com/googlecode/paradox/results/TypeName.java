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

import com.googlecode.paradox.utils.SQLStates;

import java.io.InputStream;
import java.sql.*;

/**
 * Stores the SQL type names.
 *
 * @version 1.2
 * @since 1.3
 */
public enum TypeName {
    
    /**
     * The binary type.
     */
    BINARY(Types.BINARY, "BINARY", InputStream.class.getName()),
    
    /**
     * The BLOB type.
     */
    BLOB(Types.BLOB, "BLOB", InputStream.class.getName()),
    
    /**
     * The boolean type.
     */
    BOOLEAN(Types.BOOLEAN, "BOOLEAN", Boolean.class.getName()),
    
    /**
     * The BLOB type.
     */
    CLOB(Types.CLOB, "CLOB", String.class.getName()),
    
    /**
     * The date type.
     */
    DATE(Types.DATE, "DATE", Date.class.getName()),

    /**
     * The integer type.
     */
    INTEGER(Types.INTEGER, "INTEGER", Integer.class.getName()),
    
    /**
     * The numeric type.
     */
    NUMERIC(Types.NUMERIC, "NUMERIC", Number.class.getName()),
    
    /**
     * The time type.
     */
    TIME(Types.TIME, "TIME", Time.class.getName()),
    
    /**
     * The TIMESTAMP type.
     */
    TIMESTAMP(Types.TIMESTAMP, "TIMESTAMP", Timestamp.class.getName()),
    
    /**
     * The VARCHAR type.
     */
    VARCHAR(Types.VARCHAR, "VARCHAR", String.class.getName());
    
    private static final TypeName[] VALUES = TypeName.values();
    
    /**
     * The class name.
     */
    private final String className;
    
    /**
     * The type name.
     */
    private final String name;
    
    /**
     * The SQL Type.
     */
    private final int sqlType;
    
    /**
     * Creates a new instance.
     *
     * @param sqlType
     *            the SQL type.
     * @param name
     *            the type name.
     */
    TypeName(final int sqlType, final String name, final String className) {
        this.sqlType = sqlType;
        this.name = name;
        this.className = className;
    }
    
    /**
     * Gets the class name by its SQL type.
     *
     * @param sqlType
     *            the SQL type value.
     * @return the class type name.
     * @throws SQLException
     *             in case of invalid type.
     */
    public static String getClassNameByType(final int sqlType) throws SQLException {
        for (final TypeName typeName : TypeName.VALUES) {
            if (typeName.getSQLType() == sqlType) {
                return typeName.getClassName();
            }
        }
        throw new SQLException("Type not found: " + sqlType, SQLStates.TYPE_NOT_FOUND.getValue());
    }
    
    /**
     * Gets the field name by its SQL type.
     *
     * @param sqlType
     *            the SQL type value.
     * @return the field type name.
     * @throws SQLException
     *             in case of invalid type.
     */
    public static String getTypeName(final int sqlType) throws SQLException {
        for (final TypeName typeName : TypeName.VALUES) {
            if (typeName.getSQLType() == sqlType) {
                return typeName.getName();
            }
        }
        throw new SQLException("Type not found: " + sqlType, SQLStates.TYPE_NOT_FOUND.getValue());
    }
    
    /**
     * Gets the class name.
     *
     * @return the class name.
     */
    public String getClassName() {
        return this.className;
    }
    
    /**
     * Gets the type name.
     *
     * @return the type name.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Gets the SQL type.
     *
     * @return the SQL type.
     */
    public int getSQLType() {
        return this.sqlType;
    }
    
}
