package com.googlecode.paradox.parser;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.ArrayList;

/**
 *
 * @author 72330554168
 */
public class Scanner {

	private static final char[] SEPARATORS = { ' ', '\t', '\n', '\0', '\r' };
	private static final char[] SPECIAL = { '(', ')', '+', '-', ',', '.', '=' };
	private final CharBuffer buffer;
	private final StringBuilder value = new StringBuilder(299);

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

	private void pushBack() throws IOException {
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

	public Token nextToken() throws IOException {
		final int size = tokens.size();
		if (size > 0) {
			final Token token = tokens.get(size - 1);
			tokens.remove(size - 1);
			return token;
		}
		value.delete(0, value.length());

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
				// identifiers with special chars
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
				while (!isSeparator(c) && !isSpecial(c)) {
					value.append(c);
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
		if (value.length() > 0) {
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
