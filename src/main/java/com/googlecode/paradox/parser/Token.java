/*
 * Token.java
 *
 * 03/12/2009
 * Copyright (C) 2009 Leonardo Alves da Costa
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
package com.googlecode.paradox.parser;

/**
 * Stores a token.
 * 
 * @author Leonardo Alves da Costa
 * @since 1.0
 * @version 1.0
 */
public class Token {

    /**
     * The token type.
     */
    private TokenType type;

    /**
     * The token value.
     */
    private String value;

    /**
     * Creates a new instance.
     * 
     * @param type
     *            the token type.
     * @param value
     *            the token value.
     */
    public Token(final TokenType type, final String value) {
        this.type = type;
        this.value = value;
    }

    /**
     * Gets the token type.
     * 
     * @return the token type.
     */
    public TokenType getType() {
        return type;
    }

    /**
     * Gets the token value.
     * 
     * @return the token value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Gets if this token is a conditional break.
     * 
     * @return true if this token is a conditional break.
     */
    public boolean isConditionBreak() {
        return type == TokenType.ORDER || type == TokenType.HAVING || type == TokenType.RPAREN || type == TokenType.LEFT || type == TokenType.RIGHT || type == TokenType.OUTER
                || type == TokenType.INNER || type == TokenType.JOIN;
    }

    /**
     * Gets if this token is an operator.
     * 
     * @return true if this token is an operator.
     */
    public boolean isOperator() {
        return type == TokenType.AND || type == TokenType.OR || type == TokenType.XOR;
    }

    /**
     * Sets the token type.
     * 
     * @param type
     *            the token type to set.
     */
    void setType(final TokenType type) {
        this.type = type;
    }

    /**
     * Sets the token value.
     * 
     * @param value
     *            the token value to set.
     */
    void setValue(final String value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return type + " = " + value;
    }

}
