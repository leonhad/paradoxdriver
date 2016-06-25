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

    private static final char[] SEPARATORS = { ' ', '\t', '\n', '\0', '\r' };
    private static final char[] SPECIAL = { '(', ')', '+', '-', ',', '.', '=', ';' };
    private final CharBuffer buffer;
    private final ArrayList<Token> tokens = new ArrayList<Token>();

    private final StringBuilder value = new StringBuilder(299);

    public Scanner(final String buffer) throws SQLException {
        if (buffer == null) {
            throw new SQLException("NULL SQL Query.", SQLStates.INVALID_SQL);
        }
        this.buffer = CharBuffer.wrap(buffer.trim());
    }

    private Token getToken(final String value) {
        final TokenType token = TokenType.get(value.toUpperCase());
        if (token != null) {
            return new Token(token, value);
        }
        return new Token(TokenType.IDENTIFIER, value);
    }

    public boolean hasNext() throws SQLException {
        return !tokens.isEmpty() || buffer.hasRemaining();
    }

    private boolean isSeparator(final char value) {
        for (final char c : Scanner.SEPARATORS) {
            if (c == value) {
                return true;
            }
        }
        return false;
    }

    private boolean isSpecial(final char value) {
        for (final char c : Scanner.SPECIAL) {
            if (c == value) {
                return true;
            }
        }
        return false;
    }

    private char nextChar() throws SQLException {
        return buffer.get();
    }

    public Token nextToken() throws SQLException {
        final int size = tokens.size();
        if (size > 0) {
            final Token token = tokens.get(size - 1);
            tokens.remove(size - 1);
            return token;
        }
        if (!hasNext()) {
            throw new SQLException("Unexpected end of SELECT statement.", SQLStates.INVALID_SQL);
        }
        value.delete(0, value.length());
        boolean characters = false;
        while (hasNext()) {
            final char c = nextChar();

            // ignore separators
            if (isSeparator(c)) {
                continue;
            } else if (isSpecial(c)) {
                value.append(c);
                break;
            } else if (c == '"' || c == '\'') {
                // identifiers with special chars
                if (c == '\'') {
                    // characters
                    characters = true;
                }
                parseString(c);
                break;
            } else {
                parseNumber(c);
                break;
            }
        }
        if (characters) {
            return new Token(TokenType.CHARACTER, value.toString());
        } else if (Character.isDigit(value.charAt(0))) {
            return new Token(TokenType.NUMERIC, value.toString());
        } else if (value.length() > 0) {
            return getToken(value.toString());
        }
        return null;
    }

    private void parseNumber(char c) throws SQLException {
        boolean numeric = false;
        int dotcount = 0;
        while (!isSeparator(c) && (numeric && c == '.' || !isSpecial(c))) {
            value.append(c);
            if (value.length() == 1) {
                numeric = Character.isDigit(value.charAt(0));
            } else if (c == '.') {
                dotcount++;
                // Only one dot per numeric value
                if (dotcount > 1) {
                    throw new SQLException("Invalid numeric format", SQLStates.INVALID_SQL);
                }
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

    private void parseString(final char type) throws SQLException {
        char c;
        do {
            if (hasNext()) {
                c = nextChar();
            } else {
                break;
            }
            if (c == type) {
                if (hasNext()) {
                    c = nextChar();
                } else {
                    break;
                }
                if (c == type) {
                    value.append(c);
                    // prevent breaking
                    c = ' ';
                } else {
                    pushBack();
                    break;
                }
            } else {
                value.append(c);
            }
        } while (c != type);
    }

    private void pushBack() throws SQLException {
        buffer.position(buffer.position() - 1);
    }

    public void pushBack(final Token token) {
        tokens.add(token);
    }
}
