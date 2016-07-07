/*
 * Scanner.java
 *
 * 03/14/2009
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

import java.nio.CharBuffer;
import java.sql.SQLException;
import java.util.ArrayList;

import com.googlecode.paradox.utils.SQLStates;

/**
 * SQL Scanner (read tokens from SQL String).
 *
 * @author Leonardo Alves da Costa
 * @since 1.0
 * @version 1.2
 */
public class Scanner {

    /**
     * Separators char.
     */
    private static final char[] SEPARATORS = { ' ', '\t', '\n', '\0', '\r' };

    /**
     * Special chars.
     */
    private static final char[] SPECIAL = { '(', ')', '+', '-', ',', '.', '=', ';' };

    /**
     * Character buffer used to parse the SQL.
     */
    private final CharBuffer buffer;

    /**
     * Read tokens.
     */
    private final ArrayList<Token> tokens = new ArrayList<>();

    /**
     * Value buffer.
     */
    private final StringBuilder value = new StringBuilder(299);

    /**
     * Creates a new instance.
     * 
     * @param buffer
     *            the buffer to read of.
     * @throws SQLException
     *             in case of parse errors.
     */
    public Scanner(final String buffer) throws SQLException {
        if (buffer == null) {
            throw new SQLException("NULL SQL Query.", SQLStates.INVALID_SQL.getValue());
        }
        this.buffer = CharBuffer.wrap(buffer.trim());
    }

    /**
     * Checks for maximum number dots allowed.
     * 
     * @param dotCount
     *            the dot count.
     * @throws SQLException
     *             in case of invalid dot count.
     */
    private void checkDotCount(final int dotCount) throws SQLException {
        if (dotCount > 1) {
            throw new SQLException("Invalid numeric format", SQLStates.INVALID_SQL.getValue());
        }
    }

    /**
     * Creates a token by value.
     * 
     * @param value
     *            to convert.
     * @return a new {@link Token}.
     */
    private Token getToken(final String value) {
        if (value.isEmpty()) {
            return null;
        }
        final TokenType token = TokenType.get(value.toUpperCase());
        if (token != null) {
            return new Token(token, value);
        }
        return new Token(TokenType.IDENTIFIER, value);
    }

    /**
     * If buffer has tokens.
     * 
     * @return true if the buffer still have tokens.
     * @throws SQLException
     *             in case of parse errors.
     */
    public boolean hasNext() throws SQLException {
        return !tokens.isEmpty() || buffer.hasRemaining();
    }

    /**
     * If the char is a separator.
     * 
     * @param value
     *            the char to identify.
     * @return true if the char is a separator.
     */
    private boolean isSeparator(final char value) {
        for (final char c : Scanner.SEPARATORS) {
            if (c == value) {
                return true;
            }
        }
        return false;
    }

    /**
     * if the value is a special char.
     * 
     * @param value
     *            the value to identify.
     * @return true if the value is a special char.
     */
    private boolean isSpecial(final char value) {
        for (final char c : Scanner.SPECIAL) {
            if (c == value) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the next value in buffer.
     * 
     * @return the next char.
     * @throws SQLException
     *             in case of parse errors.
     */
    private char nextChar() throws SQLException {
        return buffer.get();
    }

    /**
     * Gets the next {@link Token} in buffer.
     * 
     * @return the next {@link Token}.
     * @throws SQLException
     *             in case of parse errors.
     */
    public Token nextToken() throws SQLException {
        final int size = tokens.size();
        if (size > 0) {
            final Token token = tokens.get(size - 1);
            tokens.remove(size - 1);
            return token;
        }
        if (!hasNext()) {
            throw new SQLException("Unexpected end of SELECT statement.", SQLStates.INVALID_SQL.getValue());
        }
        value.delete(0, value.length());
        final boolean characters = parseIdentifier();
        if (characters) {
            return new Token(TokenType.CHARACTER, value.toString());
        } else if (Character.isDigit(value.charAt(0))) {
            return new Token(TokenType.NUMERIC, value.toString());
        }
        return getToken(value.toString());
    }

    /**
     * Parses identifier tokens.
     * 
     * @return if this token is an character token.
     * @throws SQLException
     *             in case of parser errors.
     */
    private boolean parseIdentifier() throws SQLException {
        while (hasNext()) {
            final char c = nextChar();

            // ignore separators
            if (isSeparator(c)) {
                continue;
            } else if (isSpecial(c)) {
                value.append(c);
                return false;
            } else if (c == '"' || c == '\'') {
                // identifiers with special chars
                boolean characters = false;
                if (c == '\'') {
                    // characters
                    characters = true;
                }
                parseString(c);
                return characters;
            } else {
                parseNumber(c);
                return false;
            }
        }
        return false;
    }

    /**
     * Parses a numeric char.
     * 
     * @param start
     *            the char to start of.
     * @throws SQLException
     *             in case of parse errors.
     */
    private void parseNumber(final char start) throws SQLException {
        char c = start;
        boolean numeric = false;
        int dotcount = 0;
        while (!isSeparator(c) && (numeric && c == '.' || !isSpecial(c))) {
            value.append(c);
            if (value.length() == 1) {
                numeric = Character.isDigit(value.charAt(0));
            } else if (c == '.') {
                dotcount++;

                // Only one dot per numeric value
                checkDotCount(dotcount);
            }
            if (hasNext()) {
                c = nextChar();
            } else {
                break;
            }
        }
        if (isSeparator(c) || isSpecial(c)) {
            pushBack();
        }
    }

    /**
     * Parses a {@link String} value.
     * 
     * @param type
     *            the string type (special char used).
     * @throws SQLException
     *             in case of parse errors.
     */
    private void parseString(final char type) throws SQLException {
        char c;
        do {
            if (hasNext()) {
                c = nextChar();
            } else {
                return;
            }
            if (c == type) {
                if (hasNext()) {
                    c = nextChar();
                } else {
                    return;
                }
                if (c == type) {
                    value.append(c);
                    // prevent breaking
                    c = ' ';
                } else {
                    pushBack();
                    return;
                }
            } else {
                value.append(c);
            }
        } while (c != type);
    }

    /**
     * Push back the read char.
     */
    private void pushBack() {
        buffer.position(buffer.position() - 1);
    }

    /**
     * Push back the given token in buffer.
     * 
     * @param token
     *            the token to push back.
     */
    public void pushBack(final Token token) {
        tokens.add(token);
    }
}
