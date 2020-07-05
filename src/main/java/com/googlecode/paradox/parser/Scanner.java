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

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.exceptions.ParadoxSyntaxErrorException;

import java.nio.CharBuffer;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * SQL Scanner (read tokens from SQL String).
 *
 * @version 1.4
 * @since 1.0
 */
public class Scanner {

    /**
     * Separators char.
     */
    private static final char[] SEPARATORS = {' ', '\t', '\n', '\0', '\r'};

    /**
     * Special chars.
     */
    private static final char[] SPECIAL = {'(', ')', '+', '-', ',', '.', '=', ';', '*'};

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
     * The Paradox connection.
     */
    private final ParadoxConnection connection;

    /**
     * The SQL current parser position.
     */
    private final ScannerPosition position = new ScannerPosition();

    /**
     * Start position.
     */
    private ScannerPosition startPosition;

    /**
     * Creates a new instance.
     *
     * @param connection the paradox connection.
     * @param buffer     the buffer to read of.
     * @throws SQLException in case of parse errors.
     */
    Scanner(final ParadoxConnection connection, final String buffer) throws SQLException {
        if (buffer == null || buffer.trim().isEmpty()) {
            throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.EMPTY_SQL);
        }
        this.connection = connection;
        this.buffer = CharBuffer.wrap(buffer.trim());
    }

    /**
     * Checks for maximum number dots allowed.
     *
     * @param dotCount the dot count.
     * @throws SQLException in case of invalid dot count.
     */
    private static void checkDotCount(final int dotCount) throws SQLException {
        if (dotCount > 1) {
            throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.NUMBER_FORMAT);
        }
    }

    /**
     * Check if is a character or a string.
     *
     * @param c the char to verify.
     * @return <code>true</code> if c is a char.
     */
    private static boolean isCharacters(final char c) {
        boolean characters = false;
        if (c == '\'') {
            // characters
            characters = true;
        }

        return characters;
    }

    /**
     * If the char is a separator.
     *
     * @param value the char to identify.
     * @return true if the char is a separator.
     */
    private static boolean isSeparator(final char value) {
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
     * @param value the value to identify.
     * @return true if the value is a special char.
     */
    private static boolean isSpecial(final char value) {
        for (final char c : Scanner.SPECIAL) {
            if (c == value) {
                return true;
            }
        }

        return false;
    }

    /**
     * Creates a token by value.
     *
     * @param value to convert.
     * @return a new {@link Token}.
     */
    private Token getToken(final String value) {
        if (value.isEmpty()) {
            return null;
        }

        final TokenType token = TokenType.get(value.toUpperCase(connection.getLocale()));
        if (token != null) {
            return new Token(token, value, startPosition);
        }

        return new Token(TokenType.IDENTIFIER, value, startPosition);
    }

    /**
     * Gets the next value in buffer.
     *
     * @return the next char.
     */
    private char nextChar() {
        final char c = this.buffer.get();
        position.add(c);
        return c;
    }

    /**
     * Parses identifier tokens.
     */
    private void parseIdentifier() {
        do {
            char c = nextChar();
            if (isSeparator(c)) {
                return;
            } else if (isSpecial(c)) {
                pushBack();
                return;
            }

            this.value.append(c);
        } while (this.hasNext());
    }

    /**
     * Parses a numeric char.
     *
     * @param start the char to start of.
     * @throws SQLException in case of parse errors.
     */
    private void parseNumber(final char start) throws SQLException {
        char c = start;
        int dotCount = 0;
        do {
            this.value.append(c);
            if (c == '.') {
                dotCount++;

                // Only one dot per numeric value
                Scanner.checkDotCount(dotCount);
            }
            if (this.hasNext()) {
                c = this.nextChar();
            } else {
                break;
            }
        } while ((!Scanner.isSeparator(c) && !Scanner.isSpecial(c)) || c == '.');

        if (Scanner.isSpecial(c)) {
            this.pushBack();
        }
    }

    /**
     * Parses a {@link String} value.
     *
     * @param type the string type (special char used).
     */
    private void parseString(final char type) {
        char c;
        do {
            if (this.hasNext()) {
                c = this.nextChar();
            } else {
                return;
            }
            if (c == type) {
                if (this.hasNext()) {
                    c = this.nextChar();
                } else {
                    return;
                }

                if (c == type) {
                    this.value.append(c);
                    // prevent breaking
                    c = ' ';
                } else {
                    this.pushBack();
                    return;
                }
            } else {
                this.value.append(c);
            }
        } while (c != type);
    }

    /**
     * Push back the read char.
     */
    private void pushBack() {
        position.back();
        buffer.position(this.buffer.position() - 1);
    }

    /**
     * If buffer has tokens.
     *
     * @return true if the buffer still have tokens.
     */
    boolean hasNext() {
        return !this.tokens.isEmpty() || this.buffer.hasRemaining();
    }

    /**
     * Gets the next {@link Token} in buffer.
     *
     * @return the next {@link Token}.
     * @throws SQLException in case of parse errors.
     */
    public Token nextToken() throws SQLException {
        final int size = this.tokens.size();
        if (size > 0) {
            final Token token = this.tokens.get(size - 1);
            this.tokens.remove(size - 1);
            return token;
        }

        if (!this.hasNext()) {
            throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.UNEXPECTED_END_OF_STATEMENT);
        }

        this.value.delete(0, this.value.length());

        // Ignore separators
        char c = nextNonSeparatorChar();
        if ((c == '"') || (c == '\'')) {
            return parseIdentifier(c);
        } else if (Character.isDigit(c)) {
            parseNumber(c);
            return new Token(TokenType.NUMERIC, this.value.toString(), startPosition);
        } else if (c == '-') {
            // Can be a minus sign only or a negative number.
            char nextChar = this.nextChar();
            // Restore the original scanner state.
            pushBack();

            if (Character.isDigit(nextChar)) {
                // It is a number.
                parseNumber(c);
                return new Token(TokenType.NUMERIC, this.value.toString(), startPosition);
            }

            // Only a minus sign.
            return getToken(Character.toString(c));
        } else if (isSpecial(c)) {
            return getToken(Character.toString(c));
        }

        pushBack();
        parseIdentifier();

        return getToken(this.value.toString());
    }

    private Token parseIdentifier(char c) {
        // identifiers with special chars
        final boolean characters = Scanner.isCharacters(c);
        this.parseString(c);

        if (characters) {
            return new Token(TokenType.CHARACTER, this.value.toString(), startPosition);
        } else {
            return new Token(TokenType.IDENTIFIER, this.value.toString(), startPosition);
        }
    }

    private char nextNonSeparatorChar() {
        char c;
        do {
            c = this.nextChar();
        } while (isSeparator(c));

        startPosition = position.lastPosition();

        return c;
    }

    /**
     * Push back the given token in buffer.
     *
     * @param token the token to push back.
     */
    public void pushBack(final Token token) {
        this.tokens.add(token);
    }
}
