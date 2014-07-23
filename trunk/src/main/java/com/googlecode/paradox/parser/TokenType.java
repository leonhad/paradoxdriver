package com.googlecode.paradox.parser;

/**
 * SQL Tokens
 *
 * @author Leonardo Alves da Costa
 * @version 1.1
 */
public enum TokenType {
	IDENTIFIER,
	SELECT,
	INSERT,
	DELETE,
	UPDATE,
	FROM,
	DISTINCT,
	WHERE,
	JOIN,
	INNER,
	OUTER,
	INTO,
	EQUALS,
	PLUS("+"),
	MINUS("-"),
	LPAREN("("),
	RPAREN(")"),
	COMMA(","),
	ASTERISK("*"),
	PERIOD("."),
	SEMI(";"),
	AS,
	ORDER,
	BY,
	HAVING,
	EXISTS,
	NOT,
	AND,
	XOR,
	OR;

	private String value;

	private TokenType() {
		value = name();
	}

	private TokenType(final String value) {
		this.value = value;
	}

	public static TokenType get(final String value) {
		for (final TokenType token : TokenType.values()) {
			if (token.value.equals(value)) {
				return token;
			}
		}
		return null;
	}
}
