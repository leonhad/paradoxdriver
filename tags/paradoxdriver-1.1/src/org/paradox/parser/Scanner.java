package org.paradox.parser;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.ArrayList;

/**
 *
 * @author 72330554168
 */
public class Scanner {

    private final CharBuffer buffer;

    private final ArrayList<Token> tokens = new ArrayList<Token>();

    public Scanner(final CharBuffer buffer) {
        this.buffer = buffer;
    }

    public boolean hasNext() throws IOException {
        return tokens.size() > 0 || buffer.hasRemaining();
    }

    private char nextChar() throws IOException {
        return buffer.get();
    }

    public void pushBack(final Token token) {
        tokens.add(token);
    }

    public Token nextToken() throws IOException {

        final int size = tokens.size();
        if (size > 0) {
            final Token token = tokens.get(size - 1);
            tokens.remove(size - 1);
            return token;
        }
        final StringBuilder value = new StringBuilder();
        boolean inString = false;

        linebreak:
        while (hasNext()) {
            final char c = nextChar();

            switch (c) {
                case ' ':
                    if (inString) {
                        value.append(" ");
                    } else if (value.length() != 0) {
                        break linebreak;
                    }
                    break;
                case '"':
                    if (inString) {
                        break linebreak;
                    }
                    inString = true;
                    break;
                case '(':
                case ')':
                case '+':
                case '-':
                case ',':
                case '=':
                    if (value.length() == 0) {
                        value.append(c);
                        break linebreak;
                    } else {
                        buffer.position(buffer.position() - 1);
                    }
                    break linebreak;
                default:
                    value.append(c);
            }
        }
        return getToken(value.toString());
    }

    private Token getToken(final String value) {
        if (value.equals("SELECT")) {
            return new Token(TokenType.SELECT, value);
        } else if (value.equals("INSERT")) {
            return new Token(TokenType.INSERT, value);
        } else if (value.equals("DELETE")) {
            return new Token(TokenType.DELETE, value);
        } else if (value.equals("UPDATE")) {
            return new Token(TokenType.UPDATE, value);
        } else if (value.equals("FROM")) {
            return new Token(TokenType.FROM, value);
        } else if (value.equals("WHERE")) {
            return new Token(TokenType.WHERE, value);
        } else if (value.equals("JOIN")) {
            return new Token(TokenType.DELETE, value);
        } else if (value.equals("INNER")) {
            return new Token(TokenType.INNER, value);
        } else if (value.equals("OUTER")) {
            return new Token(TokenType.OUTER, value);
        } else if (value.equals("=")) {
            return new Token(TokenType.EQUALS, value);
        } else if (value.equals("+")) {
            return new Token(TokenType.PLUS, value);
        } else if (value.equals("-")) {
            return new Token(TokenType.MINUS, value);
        } else if (value.equals("(")) {
            return new Token(TokenType.LPAREN, value);
        } else if (value.equals(")")) {
            return new Token(TokenType.RPAREN, value);
        } else if (value.equals(",")) {
            return new Token(TokenType.COMMA, value);
        }
        return new Token(TokenType.IDENTIFIER, value);
    }
}
