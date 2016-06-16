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
	LEFT,
	RIGHT,
	OUTER,
	ON,
	INTO,
	BETWEEN,
	ASTERISK("*"),
	EQUALS("="),
	PLUS("+"),
	MINUS("-"),
	LPAREN("("),
	RPAREN(")"),
	COMMA(","),
	PERIOD("."),
	SEMI(";"),
	MORE(">"),
	LESS("<"),
	NOTEQUALS("<>"),
	NOTEQUALS2("!="),
	AS,
	ORDER,
	BY,
	HAVING,
	EXISTS,
	NOT,
	AND,
	XOR,
	OR,
	CHARACTER(null), // Not find by it name
	NUMERIC(null), // Not find by it name
	NULL;

	private String value;

	private TokenType() {
		value = name();
	}

	private TokenType(final String value) {
		this.value = value;
	}

	public static TokenType get(final String value) {
		for (final TokenType token : TokenType.values()) {
			if (value.equals(token.value)) {
				return token;
			}
		}
		return null;
	}

}
