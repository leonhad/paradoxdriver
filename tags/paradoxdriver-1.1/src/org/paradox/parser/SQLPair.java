package org.paradox.parser;

/**
 *
 * @author lcosta
 */
public class SQLPair {
    private String left;
    private String right;
    private Token operator;

    /**
     * @return the left
     */
    String getLeft() {
        return left;
    }

    /**
     * @param left the left to set
     */
    void setLeft(String left) {
        this.left = left;
    }

    /**
     * @return the right
     */
    String getRight() {
        return right;
    }

    /**
     * @param right the right to set
     */
    void setRight(String right) {
        this.right = right;
    }

    /**
     * @return the operator
     */
    Token getOperator() {
        return operator;
    }

    /**
     * @param operator the operator to set
     */
    void setOperator(Token operator) {
        this.operator = operator;
    }
}
