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
     * @param type the type to set
     */
    void setType(TokenType type) {
        this.type = type;
    }

    /**
     * @return the value
     */
    String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    void setValue(String value) {
        this.value = value;
    }
}
