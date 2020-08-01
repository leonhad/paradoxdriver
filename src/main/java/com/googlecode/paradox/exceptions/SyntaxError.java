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
package com.googlecode.paradox.exceptions;

/**
 * Error codes.
 *
 * @version 1.2
 * @since 1.6.0
 */
public enum SyntaxError {
    /**
     * Empty SQL.
     */
    EMPTY_SQL("001", "Empty SQL string"),

    /**
     * Number format error.
     */
    NUMBER_FORMAT("002", "Invalid numeric format"),

    /**
     * Unexpected end of statement.
     */
    UNEXPECTED_END_OF_STATEMENT("003", "Unexpected end of statement"),

    /**
     * Unexpected token.
     */
    UNEXPECTED_TOKEN("004", "Unexpected token"),

    /**
     * Empty column list.
     */
    EMPTY_COLUMN_LIST("005", "Empty column list"),

    /**
     * Invalid select statement.
     */
    INVALID_SELECT_STATEMENT("006", "Invalid SELECT statement"),

    /**
     * Invalid char.
     */
    INVALID_CHAR("007", "Invalid value, expected a single char value"),

    /**
     * Empty table list.
     */
    EMPTY_TABLE_LIST("008", "Empty table list"),

    /**
     * Empty conditional list.
     */
    EMPTY_CONDITIONAL_LIST("009", "Where without any conditions"),

    /**
     * Asterisk without table.
     */
    ASTERISK_WITHOUT_TABLE("010", "Asterisk without table is not supported"),

    /**
     * Function name not found.
     */
    FUNCTION_NOT_FOUND("011", "Function %s not found"),

    /**
     * Invalid parameter count.
     */
    INVALID_PARAMETER_COUNT("012", "Invalid function parameter count, expected %s"),

    /**
     * Invalid parameter count (minimum).
     */
    INVALID_PARAMETER_COUNT_MINIMUM("013", "Invalid function parameter count, expected at last %s"),

    /**
     * Invalid parameter count (maximum).
     */
    INVALID_PARAMETER_COUNT_MAXIMUM("014", "Invalid function parameter count, got %s expected at maximum %s"),

    /**
     * Can't use asterisk in function parameter.
     */
    ASTERISK_IN_FUNCTION("015", "Asterisk not supported in this type of function"),

    /**
     * Inconsistent data type.
     */
    INCONSISTENT_DATA_TYPE("016", "Inconsistent data types: expected %s got %s"),

    /**
     * Invalid parameter value.
     */
    INVALID_PARAMETER_VALUE("017", "Invalid parameter value: %s"),

    /**
     * Inconsistent parameter list.
     */
    INCONSISTENT_PARAMETER_LIST("018", "Inconsistent parameter list size"),

    /**
     * A character string without a ending char.
     */
    UNTERMINATED_STRING("019", "Unterminated character sequence"),

    /**
     * Invalid use of grouping function.
     */
    INVALID_AGGREGATE_FUNCTION("020", "Invalid use of aggregate function: %s"),

    /**
     * Not a group by expression.
     */
    NOT_GROUP_BY("021", "Not a group by expression"),

    /**
     * Order by column must be an aggregate or to be in group by expression.
     */
    ORDER_BY_NOT_IN_GROUP_BY("022", "Order by columns must be an aggregate or to be in group by expression");

    /**
     * SQL state code.
     */
    private final String code;

    /**
     * Error description.
     */
    private final String description;

    /**
     * Creates a new instance.
     *
     * @param code        the SQL state code.
     * @param description the error description.
     */
    SyntaxError(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * Gets the error description.
     *
     * @return the error description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the error code.
     *
     * @return the error code.
     */
    public String getCode() {
        return code;
    }
}
