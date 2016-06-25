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

    @Override
    public String toString() {
        return type + " = " + value;
    };

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

    public boolean isConditionBreak() {
        return type == TokenType.ORDER || type == TokenType.HAVING || type == TokenType.RPAREN || type == TokenType.LEFT || type == TokenType.RIGHT || type == TokenType.OUTER
                || type == TokenType.INNER || type == TokenType.JOIN;
    }

}
