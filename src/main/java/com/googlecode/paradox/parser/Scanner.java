package com.googlecode.paradox.parser;

import java.nio.CharBuffer;
import java.sql.SQLException;
import java.util.ArrayList;

import com.googlecode.paradox.utils.SQLStates;

/**
 * SQL Scanner (read tokens from SQL String).
 *
 * @author Leonardo Alves da Costa
 * @since 23/07/2014
 * @version 1.1
 */
public class Scanner {

    private static final char[] SEPARATORS = { ' ', '\t', '\n', '\0', '\r' };
    private static final char[] SPECIAL = { '(', ')', '+', '-', ',', '.', '=', ';' };
    private final CharBuffer buffer;
    private final StringBuilder value = new StringBuilder(299);

    private final ArrayList<Token> tokens = new ArrayList<Token>();

    public Scanner(final String buffer) throws SQLException {
        if (buffer == null) {
            throw new SQLException("NULL SQL Query.", SQLStates.INVALID_SQL);
        }
        this.buffer = CharBuffer.wrap(buffer.trim());
    }

    public boolean hasNext() throws SQLException {
        return tokens.size() > 0 || buffer.hasRemaining();
    }

    private char nextChar() throws SQLException {
        return buffer.get();
    }

    private void pushBack() throws SQLException {
        buffer.position(buffer.position() - 1);
    }

    public void pushBack(final Token token) {
        tokens.add(token);
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
            char c = nextChar();

            // ignore separators
            if (isSeparator(c)) {
                continue;
            } else if (isSpecial(c)) {
                value.append(c);
                break;
            } else if (c == '"') {
                // identifiers with special chars
                do {
                    if (hasNext()) {
                        c = nextChar();
                    } else {
                        break;
                    }
                    if (c == '"') {
                        if (hasNext()) {
                            c = nextChar();
                        } else {
                            break;
                        }
                        if (c == '"') {
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
                } while (c != '"');
                break;
            } else if (c == '\'') {
                // characters
                characters = true;
                do {
                    if (hasNext()) {
                        c = nextChar();
                    } else {
                        break;
                    }
                    if (c == '\'') {
                        if (hasNext()) {
                            c = nextChar();
                        } else {
                            break;
                        }
                        if (c == '\'') {
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
                } while (c != '\'');
                break;
            } else {
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

    private Token getToken(final String value) {
        final TokenType token = TokenType.get(value.toUpperCase());
        if (token != null) {
            return new Token(token, value);
        }
        return new Token(TokenType.IDENTIFIER, value);
    }
}
