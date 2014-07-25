package com.googlecode.paradox.parser;

/**
 *
 * @author Leonardo Alves da Costa
 */
public class Token {
	private TokenType type;
	private String value;

	public Token(final TokenType type, final String value) {
		this.type = type;
		this.value = value;
	}

	/**
	 * @return the type
	 */
	TokenType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	void setType(final TokenType type) {
		this.type = type;
	}

	/**
	 * @return the value
	 */
	String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	void setValue(final String value) {
		this.value = value;
	}

	public boolean isOperator() {
		return type == TokenType.AND || type == TokenType.OR || type == TokenType.XOR;
	}

}
