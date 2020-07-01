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
package com.googlecode.paradox.parser;

/**
 * SQL Tokens
 *
 * @author Leonardo Costa
 * @version 1.3
 * @since 1.0
 */
public enum TokenType {
    /**
     * And token.
     */
    AND,

    /**
     * AS token.
     */
    AS,

    /**
     * All fields token.
     */
    ASTERISK("*"),

    /**
     * Between token.
     */
    BETWEEN,

    /**
     * By token.
     */
    BY,

    /**
     * Character literal.
     */
    CHARACTER(null),

    /**
     * Comma token.
     */
    COMMA(","),

    /**
     * Delete token.
     */
    DELETE,

    /**
     * Distinct token.
     */
    DISTINCT,

    /**
     * Equals token.
     */
    EQUALS("="),

    /**
     * From token.
     */
    FROM,

    /**
     * Identifier token.
     */
    IDENTIFIER,

    /**
     * Inner token.
     */
    INNER,

    /**
     * Insert token.
     */
    INSERT,

    /**
     * Into token.
     */
    INTO,

    /**
     * Join token.
     */
    JOIN,

    /**
     * Left token.
     */
    LEFT,

    /**
     * Less token.
     */
    LESS("<"),

    /**
     * Left parenthesis token.
     */
    LPAREN("("),

    /**
     * Minus token.
     */
    MINUS("-"),

    /**
     * More token.
     */
    MORE(">"),

    /**
     * Not equals token.
     */
    NOT_EQUALS("<>"),

    /**
     * Null token.
     */
    NULL,

    /**
     * Numeric token.
     */
    NUMERIC(null),

    /**
     * ON token.
     */
    ON,

    /**
     * OR token.
     */
    OR,

    /**
     * Order token.
     */
    ORDER,

    /**
     * Outer token.
     */
    OUTER,

    /**
     * Period token.
     */
    PERIOD("."),

    /**
     * Plus token.
     */
    PLUS("+"),

    /**
     * Right token.
     */
    RIGHT,

    /**
     * Right parenthesis token.
     */
    RPAREN(")"),

    /**
     * Select token.
     */
    SELECT,

    /**
     * Semicolon token.
     */
    SEMI(";"),

    /**
     * Update token.
     */
    UPDATE,

    /**
     * Where token.
     */
    WHERE;

    /**
     * Stores the conditional break values.
     */
    private static final TokenType[] CONDITIONAL_BREAKS = {ORDER, RPAREN, LEFT, RIGHT, OUTER, INNER, JOIN};

    /**
     * Stores the operator values.
     */
    private static final TokenType[] OPERATORS = {AND, OR};

    /**
     * Token value.
     */
    private final String value;

    /**
     * Creates a new instance.
     */
    TokenType() {
        this.value = this.name();
    }

    /**
     * Creates a new instance.
     *
     * @param value the token value.
     */
    TokenType(final String value) {
        this.value = value;
    }

    /**
     * Gets the token by value.
     *
     * @param value the value to search in the token list.
     * @return the token by value.
     */
    public static TokenType get(final String value) {
        for (final TokenType token : TokenType.values()) {
            if (value.equals(token.value)) {
                return token;
            }
        }
        return null;
    }

    /**
     * Gets if the token is a conditional break.
     *
     * @param tokenType the type to test of.
     * @return true if this token is a conditional break.
     */
    public static boolean isConditionalBreak(final TokenType tokenType) {
        for (final TokenType token : TokenType.CONDITIONAL_BREAKS) {
            if (token == tokenType) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets if the token for operator types.
     *
     * @param tokenType the type to test of.
     * @return true if this token is an operator.
     */
    public static boolean isOperator(final TokenType tokenType) {
        for (final TokenType token : TokenType.OPERATORS) {
            if (token == tokenType) {
                return true;
            }
        }
        return false;
    }
}
