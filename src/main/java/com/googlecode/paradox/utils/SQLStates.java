/*
 * SQLStates.java 03/12/2009 Copyright (C) 2009 Leonardo Alves da Costa This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.utils;

import java.sql.ResultSet;

/**
 * Store the SQL States.
 *
 * @author Leonardo Alves da Costa
 * @version 1.1
 * @since 1.0
 */
public enum SQLStates {
    
    /**
     * This driver can't change catalog.
     */
    CHANGE_CATALOG_NOT_SUPPORTED("1003"),
    
    /**
     * There is more than one column name in same reference.
     */
    COLUMN_AMBIQUOUS("1015"),
    
    /**
     * Used when some directory is not found
     */
    DIR_NOT_FOUND("1001"),
    
    /**
     * The referenced column in invalid.
     */
    INVALID_COLUMN("1010"),
    
    /**
     * Command invalid.
     */
    INVALID_COMMAND("1006"),
    
    /**
     * Invalid field value.
     */
    INVALID_FIELD_VALUE("1012"),
    
    /**
     * There is an error in I/O subsystem.
     */
    INVALID_IO("1016"),
    
    /**
     * Invalid parameter format or value.
     */
    INVALID_PARAMETER("1005"),
    
    /**
     * Column not found.
     */
    INVALID_ROW("1008"),
    
    /**
     * Invalid SQL.
     */
    INVALID_SQL("1007"),
    
    /**
     * Invalid SQL state.
     */
    INVALID_STATE("1011"),
    
    /**
     * Table format is invalid.
     */
    INVALID_TABLE("1014"),
    
    /**
     * Error in data load.
     */
    LOAD_DATA("1100"),
    
    /**
     * {@link ResultSet} not open for use.
     */
    RESULTSET_CLOSED("1009"),
    
    /**
     * Type not valid or unsupported.
     */
    TYPE_NOT_FOUND("1004");
    
    /**
     * The constant code.
     */
    private final String value;
    
    /**
     * Create a new instance.
     *
     * @param value
     *            the constant code.
     */
    SQLStates(final String value) {
        this.value = value;
    }
    
    /**
     * Gets the constant code.
     *
     * @return the constant code.
     */
    public String getValue() {
        return this.value;
    }
    
}
