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

import com.googlecode.paradox.exceptions.ParadoxSyntaxErrorException;

import java.nio.CharBuffer;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

/**
 * SQL Scanner (read tokens from SQL String).
 *
 * @version 1.5
 * @since 1.0
 */
public class Scanner {

    /**
     * Separators char.
     */
    private static final char[] SEPARATORS = {' ', '\b', '\t', '\n', '\0', '\r'};

    /**
     * Special chars.
     */
    private static final char[] SPECIAL = {'(', ')', '+', '-', ',', '.', '=', ';', '*'};

    /**
     * Character buffer used to parse the SQL.
     */
    private final CharBuffer buffer;

    /**
     * Preloaded chars.
     */
    private final Queue<Character> preloaded = new ArrayDeque<>();

    /**
     * Read tokens.
     */
    private final ArrayList<Token> tokens = new ArrayList<>();

    /**
     * Value buffer.
     */
    private final StringBuilder value = new StringBuilder(299);

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
     * @param buffer the buffer to read of.
     * @throws SQLException in case of parse errors.
     */
    Scanner(final String buffer) throws SQLException {
        if (buffer == null || buffer.trim().isEmpty()) {
            throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.EMPTY_SQL);
        }

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

        final TokenType token = TokenType.get(value);
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
        if (!preloaded.isEmpty()) {
            return preloaded.poll();
        }

        char c = this.buffer.get();
        position.add(c);

        // Handles escape characters.
        if (c == '\\') {
            final char next = this.buffer.get();
            switch (next) {
                case 'n':
                    c = '\n';
                    break;
                case 'b':
                    c = '\b';
                    break;
                case 'r':
                    c = '\r';
                    break;
                case 't':
                    c = '\t';
                    break;
                case '\\':
                    // Keep the \ char.
                    break;
                default:
                    buffer.position(buffer.position() - 1);
            }
        }
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
                pushBack(c);
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
            this.pushBack(c);
        }
    }

    /**
     * Parses a character stream value.
     *
     * @param type the string type (special char used to start the string).
     */
    private void parseString(final char type) {
        char c = '\0';

        while (this.hasNext() && c != type) {
            c = this.nextChar();

            if (c != type) {
                this.value.append(c);
            } else if (hasNext()) {
                final char nextChar = this.nextChar();
                // Escaped string
                if (nextChar == type) {
                    this.value.append(c);
                    // Prevent breaking.
                    c = '\0';
                } else {
                    pushBack(nextChar);
                }
            }
        }
    }

    /**
     * Push back the read char.
     *
     * @param character the character to push back.
     */
    private void pushBack(char character) {
        preloaded.add(character);
    }

    /**
     * If buffer has tokens.
     *
     * @return true if the buffer still have tokens.
     */
    boolean hasNext() {
        return !this.preloaded.isEmpty() || !this.tokens.isEmpty() || this.buffer.hasRemaining();
    }

    /**
     * Test for empty statement.
     *
     * @throws ParadoxSyntaxErrorException in case of empty statement.
     */
    private void assertNotEmptyStatement() throws ParadoxSyntaxErrorException {
        if (!this.hasNext()) {
            throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.UNEXPECTED_END_OF_STATEMENT);
        }
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

        assertNotEmptyStatement();

        this.value.delete(0, this.value.length());

        // Ignore separators
        char c = nextNonSeparatorChar();
        Token ret = null;
        if ((c == '"') || (c == '\'')) {
            ret = parseIdentifier(c);
        } else if (c == '/') {
            // Test for multiline comment.
            char nextChar = this.nextChar();

            if (nextChar == '*') {
                parseMultilineComment();

                // Redo this from beginning.
                ret = nextToken();
            } else {
                // Restore the original scanner state and treat is as a normal identifier.
                pushBack(nextChar);
            }
        } else if (Character.isDigit(c)) {
            parseNumber(c);
            ret = new Token(TokenType.NUMERIC, this.value.toString(), startPosition);
        } else if (c == '-') {
            ret = parseMinusSign(c);
        } else if (isSpecial(c)) {
            ret = getToken(Character.toString(c));
        }

        // The token is already handled?
        if (ret == null) {
            pushBack(c);
            parseIdentifier();

            ret = getToken(this.value.toString());
        }

        return ret;
    }

    /**
     * Handle the minus sign.
     *
     * @param c the current char.
     * @return the token.
     * @throws SQLException in case of failures.
     */
    private Token parseMinusSign(char c) throws SQLException {
        // Can be a minus sign only or a negative number.
        char nextChar = this.nextChar();
        // Restore the original scanner state.
        pushBack(nextChar);

        if (Character.isDigit(nextChar)) {
            // It is a number.
            parseNumber(c);
            return new Token(TokenType.NUMERIC, this.value.toString(), startPosition);
        } else if (nextChar == '-') {
            // It is a comment.
            parseComment();

            // Redo this from beginning.
            return nextToken();
        }

        // Only a minus sign.
        return getToken(Character.toString(c));
    }

    private void parseComment() {
        char c;

        do {
            c = nextChar();
        } while (hasNext() && c != '\n');
    }

    private void parseMultilineComment() {
        char last;
        char c = '\0';

        do {
            last = c;
            c = nextChar();
        } while (hasNext() && (last != '*' || c != '/'));
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
