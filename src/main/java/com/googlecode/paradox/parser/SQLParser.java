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
import com.googlecode.paradox.exceptions.SyntaxError;
import com.googlecode.paradox.function.FunctionFactory;
import com.googlecode.paradox.function.date.ExtractFunction;
import com.googlecode.paradox.function.general.CastFunction;
import com.googlecode.paradox.function.general.ConvertFunction;
import com.googlecode.paradox.function.string.PositionFunction;
import com.googlecode.paradox.function.string.SubstringFunction;
import com.googlecode.paradox.function.string.TrimFunction;
import com.googlecode.paradox.parser.nodes.*;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.planner.nodes.FunctionNode;
import com.googlecode.paradox.planner.nodes.ParameterNode;
import com.googlecode.paradox.planner.nodes.ValueNode;
import com.googlecode.paradox.planner.nodes.comparable.*;
import com.googlecode.paradox.planner.nodes.join.ANDNode;
import com.googlecode.paradox.planner.nodes.join.ORNode;
import com.googlecode.paradox.planner.sorting.OrderType;
import com.googlecode.paradox.results.ParadoxType;

import java.sql.SQLException;

/**
 * Parses a SQL statement.
 *
 * @version 1.13
 * @since 1.0
 */
@SuppressWarnings("java:S1448")
public final class SQLParser {

    /**
     * The scanner used to read tokens.
     */
    private final Scanner scanner;

    /**
     * The current token.
     */
    private Token token;

    /**
     * Parameter count.
     */
    private int parameterCount;

    /**
     * Creates a new instance.
     *
     * @param sql the SQL to parse.
     * @throws SQLException in case of parse errors.
     */
    public SQLParser(final String sql) throws SQLException {
        this.scanner = new Scanner(sql);
    }

    /**
     * Parses the function name alias.
     *
     * @param functionName the function name.
     * @param position     the current scanner position.
     * @return the function node with alias set.
     * @throws SQLException in case of failures.
     */
    private static FunctionNode parseFunctionAlias(final String functionName, final ScannerPosition position)
            throws SQLException {
        final FunctionNode functionNode = new FunctionNode(functionName, position);
        functionNode.validate(position);
        return functionNode;
    }

    /**
     * Parses the SQL statement.
     *
     * @return a statement node.
     * @throws SQLException in case of parse errors.
     */
    public StatementNode parse() throws SQLException {
        if (!this.scanner.hasNext()) {
            throw new ParadoxSyntaxErrorException(SyntaxError.UNEXPECTED_END_OF_STATEMENT);
        }

        this.token = this.scanner.nextToken();

        StatementNode statementNode;
        if (isToken(TokenType.SELECT)) {
            statementNode = this.parseSelect();
        } else {
            throw new ParadoxSyntaxErrorException(SyntaxError.UNEXPECTED_TOKEN, token.getPosition());
        }

        statementNode.setParameterCount(parameterCount);
        return statementNode;
    }

    /**
     * Test for expected tokens.
     *
     * @param token the token to validate.
     * @throws SQLException in case of unexpected tokens.
     */
    private void expect(final TokenType token) throws SQLException {
        if (this.token == null) {
            throw new ParadoxSyntaxErrorException(SyntaxError.UNEXPECTED_END_OF_STATEMENT);
        } else if (this.token.getType() != token) {
            throw new ParadoxSyntaxErrorException(SyntaxError.UNEXPECTED_TOKEN, getPosition());
        }

        if (this.scanner.hasNext()) {
            this.token = this.scanner.nextToken();
        } else {
            this.token = null;
        }
    }

    /**
     * Test for expected COMMA if {@code enabled} is <code>true</code>.
     *
     * @param enabled <code>true</code> if the token can be checked.
     * @throws SQLException in case of unexpected tokens.
     */
    private void expectComma(final boolean enabled) throws SQLException {
        if (enabled) {
            expect(TokenType.COMMA);
        }
    }

    /**
     * Parse the asterisk token.
     *
     * @param tableName the table name.
     * @return the asterisk node.
     * @throws SQLException in case of parse errors.
     */
    private AsteriskNode parseAsterisk(final String tableName) throws SQLException {
        final ScannerPosition position = getPosition();
        this.expect(TokenType.ASTERISK);
        return new AsteriskNode(tableName, position);
    }

    /**
     * Parses between token.
     *
     * @param field the between field.
     * @return the between node.
     * @throws SQLException in case of parse errors.
     */
    private BetweenNode parseBetween(final FieldNode field) throws SQLException {
        final ScannerPosition position = getPosition();
        this.expect(TokenType.BETWEEN);
        final FieldNode left = this.parseField();
        this.expect(TokenType.AND);
        final FieldNode right = this.parseField();
        return new BetweenNode(field, left, right, position);
    }

    /**
     * Parse the character token.
     *
     * @param fieldName the field name.
     * @return the node value.
     * @throws SQLException in case of parse errors.
     */
    private ValueNode parseCharacter(final String fieldName) throws SQLException {
        final ScannerPosition position = getPosition();
        this.expect(TokenType.CHARACTER);

        return new ValueNode(fieldName, position, ParadoxType.VARCHAR);
    }

    private String getFieldAlias(String fieldName) throws SQLException {
        String fieldAlias = fieldName;

        if (isToken(TokenType.AS)) {
            // Field alias (with AS identifier)
            this.expect(TokenType.AS);
            fieldAlias = this.token.getValue();
            this.expect(TokenType.IDENTIFIER);
        } else if (isToken(TokenType.IDENTIFIER)) {
            // Field alias (without AS identifier)
            fieldAlias = this.token.getValue();
            this.expect(TokenType.IDENTIFIER);
        }

        return fieldAlias;
    }

    /**
     * Parses the conditional statements.
     *
     * @return the node.
     * @throws SQLException in case of parse errors.
     */
    private AbstractConditionalNode parseCondition() throws SQLException {
        AbstractConditionalNode ret = null;
        while (this.scanner.hasNext() && !this.token.isConditionBreak() && !this.token.isSelectBreak()) {
            ret = parseSubCondition(ret);
        }

        return ret;
    }

    /**
     * Parses the conditional statements.
     *
     * @return the node.
     * @throws SQLException in case of parse errors.
     */
    private AbstractConditionalNode parseSubCondition(final AbstractConditionalNode parent) throws SQLException {
        AbstractConditionalNode ret = parent;

        if (!this.token.isConditionBreak() && !this.token.isSelectBreak()) {
            if (ret != null && this.token.isOperator()) {
                // Not in first expression.
                ret = this.parseOperators(ret);
            } else if (isToken(TokenType.L_PAREN)) {
                this.expect(TokenType.L_PAREN);
                while (!isToken(TokenType.R_PAREN)) {
                    ret = parseSubCondition(ret);
                }

                this.expect(TokenType.R_PAREN);
            } else if (isToken(TokenType.NOT)) {
                // Token type NOT.
                final ScannerPosition position = this.token.getPosition();
                this.expect(TokenType.NOT);
                final NotNode node = new NotNode(position);
                node.addChild(parseSubCondition(null));

                if (parent == null) {
                    ret = node;
                } else {
                    parent.addChild(node);
                }
            } else {
                ret = this.parseFieldNode();
            }
        }

        return ret;
    }

    /**
     * Parses the equals tokens.
     *
     * @param field the left field token.
     * @return the equals node.
     * @throws SQLException in case of parse errors.
     */
    private EqualsNode parseEquals(final FieldNode field) throws SQLException {
        final ScannerPosition position = this.token.getPosition();
        this.expect(TokenType.EQUALS);
        final FieldNode value = this.parseField();
        return new EqualsNode(field, value, position);
    }

    /**
     * Parses the table join fields.
     *
     * @return the field node.
     * @throws SQLException in case of errors.
     */
    private FieldNode parseField() throws SQLException {
        String fieldName = this.token.getValue();
        final ScannerPosition position = getPosition();

        FieldNode ret;
        switch (this.token.getType()) {
            case CHARACTER:
                // Found a String value.
                ret = parseCharacter(fieldName);
                break;
            case NUMERIC:
                // Found a numeric value.
                ret = parseNumeric(fieldName);
                break;
            case NULL:
                ret = parseNull();
                break;
            case TRUE:
                ret = parseTrue(fieldName);
                break;
            case FALSE:
                ret = parseFalse(fieldName);
                break;
            case QUESTION_MARK:
                ret = parseParameter();
                break;
            default:
                // Found a field.
                ret = getFieldNode(fieldName, position);
                break;
        }

        return ret;
    }

    private FieldNode getFieldNode(final String fieldName, final ScannerPosition position) throws SQLException {
        String tableName = null;
        String name = fieldName;
        this.expect(TokenType.IDENTIFIER);

        // If it has a Table Name
        if (isToken(TokenType.PERIOD)) {
            this.expect(TokenType.PERIOD);
            tableName = name;
            name = this.token.getValue();
            this.expect(TokenType.IDENTIFIER);
        } else if (isToken(TokenType.L_PAREN)) {
            // function
            final FunctionNode node = parseFunction(fieldName, position, false);
            if (node.isGrouping()) {
                throw new ParadoxSyntaxErrorException(SyntaxError.INVALID_AGGREGATE_FUNCTION, position, node.getName());
            }

            return node;
        }

        return new FieldNode(tableName, name, position);
    }

    /**
     * Parses the field node.
     *
     * @return the field node.
     * @throws SQLException in case of parse errors.
     */
    private AbstractConditionalNode parseFieldNode() throws SQLException {
        final FieldNode firstField = this.parseField();
        AbstractConditionalNode node;

        switch (this.token.getType()) {
            case BETWEEN:
                node = this.parseBetween(firstField);
                break;
            case EQUALS:
                node = this.parseEquals(firstField);
                break;
            case NOT_EQUALS:
                node = this.parseNotEquals(firstField);
                break;
            case LESS:
                node = this.parseLess(firstField);
                break;
            case MORE:
                node = this.parseMore(firstField);
                break;
            case IN:
                node = this.parseIn(firstField);
                break;
            case IS:
                node = this.parseNull(firstField);
                break;
            case LIKE:
                node = this.parseLike(firstField);
                break;
            case ILIKE:
                node = this.parseILike(firstField);
                break;
            case NOT:
                node = this.parseNot(firstField);
                break;
            default:
                throw new ParadoxSyntaxErrorException(SyntaxError.UNEXPECTED_TOKEN,
                        this.token.getPosition());
        }

        return node;
    }

    /**
     * Parse the field list in SELECT statement.
     *
     * @param select          the select node.
     * @param enableAggregate if this field can have a aggregate function.
     * @throws SQLException in case of parse errors.
     */
    private void parseFields(final SelectNode select, final boolean enableAggregate) throws SQLException {
        boolean firstField = true;
        do {
            expectComma(!firstField);
            firstField = false;

            // Field Name
            final String fieldName = this.token.getValue();

            SQLNode node;
            switch (this.token.getType()) {
                case CHARACTER:
                    node = this.parseCharacter(fieldName);
                    break;
                case NUMERIC:
                    node = this.parseNumeric(fieldName);
                    break;
                case NULL:
                    node = this.parseNull();
                    break;
                case TRUE:
                    node = this.parseTrue(fieldName);
                    break;
                case FALSE:
                    node = this.parseFalse(fieldName);
                    break;
                case ASTERISK:
                    node = this.parseAsterisk(null);
                    break;
                case QUESTION_MARK:
                    node = parseParameter();
                    break;
                default:
                    node = this.parseIdentifier(fieldName, enableAggregate);
                    break;
            }

            node.setAlias(getFieldAlias(node.getAlias()));
            select.addField(node);
        } while (this.scanner.hasNext() && !isToken(TokenType.FROM));
    }

    /**
     * Parse table field names from WHERE.
     *
     * @param oldAlias the old alias name.
     * @return the new alias.
     * @throws SQLException in case of errors.
     */
    private String parseFields(final String oldAlias) throws SQLException {
        String tableAlias = oldAlias;

        if (isToken(TokenType.IDENTIFIER) || isToken(TokenType.AS)) {
            // Field alias (with AS identifier)
            testAndRemoveTokenType(TokenType.AS);

            // Field alias (without AS identifier)
            tableAlias = this.token.getValue();
            this.expect(TokenType.IDENTIFIER);
        }

        return tableAlias;
    }

    /**
     * Parse the FROM keyword.
     *
     * @param select the select node.
     * @throws SQLException in case of parse errors.
     */
    private void parseFrom(final SelectNode select) throws SQLException {
        ScannerPosition position = getPosition();
        this.expect(TokenType.FROM);
        boolean firstField = true;
        while (this.token != null && !this.token.isSelectBreak()) {
            expectComma(!firstField);
            firstField = false;

            this.parseJoinTable(select);
        }

        if (select.getTables().isEmpty()) {
            if (this.token != null) {
                position = getPosition();
            } else {
                addOffset(position, TokenType.FROM.name().length());
            }

            throw new ParadoxSyntaxErrorException(SyntaxError.EMPTY_TABLE_LIST, position);
        }
    }

    /**
     * Parse the identifier token associated with a field.
     *
     * @param fieldName       the field name.
     * @param enableAggregate if this field can have a aggregate function.
     * @return the field node.
     * @throws SQLException in case of parse errors.
     */
    private SQLNode parseIdentifier(final String fieldName, final boolean enableAggregate) throws SQLException {
        String newTableName = null;
        String newFieldName = fieldName;

        final ScannerPosition position = getPosition();

        // Just change to next token because some functions have clash names with
        // reserved words.
        if (this.scanner.hasNext()) {
            this.token = this.scanner.nextToken();
        } else {
            this.token = null;
        }

        if (isToken(TokenType.L_PAREN)) {
            // function
            return parseFunction(fieldName, position, enableAggregate);
        } else if (isToken(TokenType.PERIOD)) {
            this.expect(TokenType.PERIOD);
            newTableName = fieldName;
            newFieldName = this.token.getValue();

            if (isToken(TokenType.ASTERISK)) {
                return parseAsterisk(newTableName);
            }

            this.expect(TokenType.IDENTIFIER);
        } else if (FunctionFactory.isFunctionAlias(fieldName)) {
            // A field without table alias can be a function alias.
            return parseFunctionAlias(fieldName, position);
        }

        return new FieldNode(newTableName, newFieldName, position);
    }

    /**
     * Handles function specific separators (only for separators).
     *
     * @param functionName the function name to identify it.
     * @param node         the function node.
     * @return <code>true</code> if the separator was handled.
     * @throws SQLException in case of failures.
     */
    private boolean isFunctionSpecific(final String functionName, final FunctionNode node) throws SQLException {
        boolean ret = false;
        if (functionName.equalsIgnoreCase(PositionFunction.NAME)) {
            // POSITION(a in b).
            this.expect(TokenType.IN);
            ret = true;
        } else if (functionName.equalsIgnoreCase(ExtractFunction.NAME)) {
            // EXTRACT(a FROM b).
            this.expect(TokenType.FROM);
            ret = true;
        } else if (functionName.equalsIgnoreCase(TrimFunction.NAME) && this.token != null) {
            if (TrimFunction.isValidType(node.getParameters().get(0).getName())) {
                // TRIM(TYPE...
                if (node.getParameters().size() == 0x02) {
                    // TRIM(TYPE 'CHARS'...
                    this.expect(TokenType.FROM);
                } else if (node.getParameters().size() != 1) {
                    // TRIM(TYPE 'CHARS' FROM 'X'...
                    throw new ParadoxSyntaxErrorException(SyntaxError.INVALID_PARAMETER_VALUE,
                            this.token.getPosition(), this.token.getValue());
                }
            } else if (node.getParameters().size() == 1) {
                // TRIM('CHARS' ...).
                this.expect(TokenType.FROM);
            } else {
                // TRIM('CHARS' FROM 'TEXT).
                throw new ParadoxSyntaxErrorException(SyntaxError.INVALID_PARAMETER_VALUE, this.token.getPosition(),
                        this.token.getValue());
            }

            // Do nothing, no separator here. TRIM(TYPE...
            ret = true;
        } else if (functionName.equalsIgnoreCase(SubstringFunction.NAME)) {
            // SUBSTRING(VALUE FROM start FOR length).

            if (isToken(TokenType.COMMA)) {
                this.expect(TokenType.COMMA);
            } else if (node.getParameters().size() == 1) {
                this.expect(TokenType.FROM);
            } else {
                this.expect(TokenType.FOR);
            }

            ret = true;
        } else if (functionName.equalsIgnoreCase(ConvertFunction.NAME)) {
            if (isToken(TokenType.USING)) {
                node.getParameters().add(
                        new ValueNode(this.token.getValue(), this.token.getPosition(), ParadoxType.VARCHAR));
                this.expect(TokenType.USING);
            } else {
                this.expect(TokenType.COMMA);
            }

            ret = true;
        } else if (functionName.equalsIgnoreCase(CastFunction.NAME)) {
            this.expect(TokenType.AS);
            ret = true;
        }

        return ret;
    }

    /**
     * Parses a function node.
     *
     * @param functionName    the function name.
     * @param position        the current scanner position.
     * @param enableAggregate if this field can have a aggregate function.
     * @return the function node.
     * @throws SQLException in case of failures.
     */
    private FunctionNode parseFunction(final String functionName, final ScannerPosition position,
                                       final boolean enableAggregate) throws SQLException {
        final FunctionNode functionNode = new FunctionNode(functionName, position);
        this.expect(TokenType.L_PAREN);

        boolean first = true;
        while (!isToken(TokenType.R_PAREN)) {
            // Is a function specific separator?
            if (!first && !isFunctionSpecific(functionName, functionNode)) {
                this.expect(TokenType.COMMA);
            }

            first = false;

            switch (this.token.getType()) {
                case CHARACTER:
                    functionNode.addParameter(this.parseCharacter(this.token.getValue()));
                    break;
                case NUMERIC:
                    functionNode.addParameter(this.parseNumeric(this.token.getValue()));
                    break;
                case NULL:
                    functionNode.addParameter(this.parseNull());
                    break;
                case TRUE:
                    functionNode.addParameter(this.parseTrue(this.token.getValue()));
                    break;
                case FALSE:
                    functionNode.addParameter(this.parseFalse(this.token.getValue()));
                    break;
                case ASTERISK:
                    functionNode.addParameter(this.parseAsterisk(null));
                    break;
                case QUESTION_MARK:
                    functionNode.addParameter(this.parseParameter());
                    break;
                default:
                    functionNode.addParameter(this.parseIdentifierFieldFunction(this.token.getValue(),
                            enableAggregate));
                    break;
            }
        }

        final ScannerPosition endPosition = getPosition();
        this.expect(TokenType.R_PAREN);

        functionNode.validate(endPosition);
        return functionNode;
    }

    private ParameterNode parseParameter() throws SQLException {
        final ScannerPosition position = this.token.getPosition();
        this.expect(TokenType.QUESTION_MARK);
        final ParameterNode node = new ParameterNode(parameterCount, position);
        parameterCount++;
        return node;
    }

    private FieldNode parseIdentifierFieldFunction(final String fieldName, final boolean enableAggregate)
            throws SQLException {
        String newTableName = null;
        String newFieldName = fieldName;

        final ScannerPosition position = getPosition();
        this.expect(TokenType.IDENTIFIER);

        if (isToken(TokenType.L_PAREN)) {
            // function
            final FunctionNode node = parseFunction(fieldName, position, enableAggregate);
            if (node.isGrouping() && !enableAggregate) {
                throw new ParadoxSyntaxErrorException(SyntaxError.INVALID_AGGREGATE_FUNCTION, position, node.getName());
            }

            return node;
        } else if (isToken(TokenType.PERIOD)) {
            // If it has a Table Name.
            this.expect(TokenType.PERIOD);
            newTableName = fieldName;
            newFieldName = this.token.getValue();

            this.expect(TokenType.IDENTIFIER);
        } else if (FunctionFactory.isFunctionAlias(fieldName)) {
            // A field without table alias can be a function alias.
            return parseFunctionAlias(fieldName, position);
        }

        return new FieldNode(newTableName, newFieldName, position);
    }

    /**
     * Parses the join tokens.
     *
     * @param select the select node.
     * @throws SQLException in case of errors.
     */
    private void parseJoin(final SelectNode select) throws SQLException {
        while (this.scanner.hasNext() && !isToken(TokenType.COMMA) && !this.token.isSelectBreak()) {
            // Inner, right or cross join.
            final JoinType joinType = getJoinType();
            this.expect(TokenType.JOIN);

            String schemaName = null;
            String tableName = this.token.getValue();
            this.expect(TokenType.IDENTIFIER);

            // Have schema name.
            if (isToken(TokenType.PERIOD)) {
                expect(TokenType.PERIOD);
                schemaName = tableName;
                tableName = this.token.getValue();
                this.expect(TokenType.IDENTIFIER);
            }

            final String tableAlias = this.parseFields(tableName);

            final JoinNode joinTable = new JoinNode(schemaName, tableName, tableAlias, joinType, null);
            if (joinType != JoinType.CROSS) {
                // Cross join don't have join clause.
                this.expect(TokenType.ON);
                joinTable.setCondition(this.parseCondition());
            }

            select.addTable(joinTable);
        }
    }

    /**
     * Parses the join type.
     *
     * @return the join type.
     * @throws SQLException in case of failures.
     */
    private JoinType getJoinType() throws SQLException {
        JoinType joinType = JoinType.INNER;
        switch (this.token.getType()) {
            case FULL:
                joinType = JoinType.FULL;
                this.expect(TokenType.FULL);
                testAndRemoveTokenType(TokenType.OUTER);
                break;
            case LEFT:
                joinType = JoinType.LEFT;
                this.expect(TokenType.LEFT);
                testAndRemoveTokenType(TokenType.OUTER);
                break;
            case RIGHT:
                joinType = JoinType.RIGHT;
                this.expect(TokenType.RIGHT);
                testAndRemoveTokenType(TokenType.OUTER);
                break;
            case CROSS:
                joinType = JoinType.CROSS;
                this.expect(TokenType.CROSS);
                break;
            case INNER:
                this.expect(TokenType.INNER);
                break;
            default:
                // Nothing to do here.
        }

        return joinType;
    }

    /**
     * Test for a desired token and remove it if is the right token.
     *
     * @param token the token to test.
     * @throws SQLException in case of failures.
     */
    private void testAndRemoveTokenType(final TokenType token) throws SQLException {
        if (isToken(token)) {
            this.expect(token);
        }
    }

    /**
     * Parse the tables name after a from keyword.
     *
     * @param select the select node.
     * @throws SQLException in case of parse errors.
     */
    private void parseJoinTable(final SelectNode select) throws SQLException {
        String schemaName = null;
        String tableName = this.token.getValue();
        final ScannerPosition position = getPosition();
        this.expect(TokenType.IDENTIFIER);

        // Have schema name.
        if (isToken(TokenType.PERIOD)) {
            expect(TokenType.PERIOD);
            schemaName = tableName;
            tableName = this.token.getValue();
            this.expect(TokenType.IDENTIFIER);
        }

        final String tableAlias = this.parseFields(tableName);

        final TableNode table = new TableNode(schemaName, tableName, tableAlias, position);
        select.addTable(table);

        // Parse possible table joins.
        this.parseJoin(select);
    }

    /**
     * Parses less token.
     *
     * @param firstField the left token field.
     * @return the less token.
     * @throws SQLException in case of parse errors.
     */
    private AbstractComparableNode parseLess(final FieldNode firstField) throws SQLException {
        final ScannerPosition position = getPosition();
        this.expect(TokenType.LESS);

        if (isToken(TokenType.EQUALS)) {
            this.expect(TokenType.EQUALS);
            return new LessThanOrEqualsNode(firstField, this.parseField(), position);
        }

        return new LessThanNode(firstField, parseField(), position);
    }

    /**
     * Parses more token.
     *
     * @param firstField the left more token field.
     * @return the grater than node.
     * @throws SQLException in case of parse errors.
     */
    private AbstractComparableNode parseMore(final FieldNode firstField) throws SQLException {
        final ScannerPosition position = getPosition();
        this.expect(TokenType.MORE);

        if (isToken(TokenType.EQUALS)) {
            this.expect(TokenType.EQUALS);
            return new GreaterThanOrEqualsNode(firstField, this.parseField(), position);
        }

        return new GreaterThanNode(firstField, this.parseField(), position);
    }

    private InNode parseIn(final FieldNode firstField) throws SQLException {
        final ScannerPosition position = this.token.getPosition();
        this.expect(TokenType.IN);
        this.expect(TokenType.L_PAREN);

        final InNode in = new InNode(firstField, position);

        boolean first = true;
        do {
            expectComma(!first);
            first = false;

            if (isToken(TokenType.NUMERIC)) {
                in.addField(new ValueNode(token.getValue(), token.getPosition(), ParadoxType.NUMBER));
                this.expect(TokenType.NUMERIC);
            } else if (isToken(TokenType.CHARACTER)) {
                in.addField(new ValueNode(token.getValue(), token.getPosition(), ParadoxType.VARCHAR));
                this.expect(TokenType.CHARACTER);
            } else {
                throw new ParadoxSyntaxErrorException(SyntaxError.UNEXPECTED_TOKEN, getPosition());
            }

        } while (!isToken(TokenType.R_PAREN));

        this.expect(TokenType.R_PAREN);

        return in;
    }

    /**
     * Parses null conditional token.
     *
     * @param firstField the left more token field.
     * @return the null than node.
     * @throws SQLException in case of parse errors.
     */
    private AbstractComparableNode parseNull(final FieldNode firstField) throws SQLException {
        final ScannerPosition position = getPosition();
        this.expect(TokenType.IS);
        AbstractComparableNode ret;
        if (isToken(TokenType.NOT)) {
            this.expect(TokenType.NOT);
            ret = new IsNotNullNode(firstField, position);
        } else {
            ret = new IsNullNode(firstField, position);
        }

        this.expect(TokenType.NULL);
        return ret;
    }

    /**
     * Parses not conditional.
     *
     * @param firstField the left more token field.
     * @return the null than node.
     * @throws SQLException in case of parse errors.
     */
    private NotNode parseNot(final FieldNode firstField) throws SQLException {
        final ScannerPosition position = getPosition();
        this.expect(TokenType.NOT);

        final NotNode not = new NotNode(position);
        if (isToken(TokenType.LIKE)) {
            not.addChild(this.parseLike(firstField));
        } else if (isToken(TokenType.ILIKE)) {
            not.addChild(this.parseILike(firstField));
        } else if (isToken(TokenType.IN)) {
            not.addChild(this.parseIn(firstField));
        } else {
            throw new ParadoxSyntaxErrorException(SyntaxError.UNEXPECTED_TOKEN, getPosition());
        }

        return not;
    }

    /**
     * Parses like conditional.
     *
     * @param firstField the left more token field.
     * @return the null than node.
     * @throws SQLException in case of parse errors.
     */
    private LikeNode parseLike(final FieldNode firstField) throws SQLException {
        final ScannerPosition position = getPosition();
        this.expect(TokenType.LIKE);

        final LikeNode like = new LikeNode(firstField, parseField(), position);
        parseEscapeToken(like);
        return like;
    }

    /**
     * Parses insensitive like conditional.
     *
     * @param firstField the left more token field.
     * @return the null than node.
     * @throws SQLException in case of parse errors.
     */
    private ILikeNode parseILike(final FieldNode firstField) throws SQLException {
        final ScannerPosition position = getPosition();
        this.expect(TokenType.ILIKE);

        final ILikeNode iLikeNode = new ILikeNode(firstField, parseField(), position);
        parseEscapeToken(iLikeNode);
        return iLikeNode;
    }

    /**
     * Check and parses the ESCAPE node in like.
     *
     * @param likeNode the like node.
     * @throws SQLException in case of syntax errors.
     */
    private void parseEscapeToken(final LikeNode likeNode) throws SQLException {
        // Has an escape value?
        if (isToken(TokenType.ESCAPE)) {
            this.expect(TokenType.ESCAPE);
            FieldNode field = parseField();
            if (field instanceof ValueNode) {
                ValueNode value = (ValueNode) field;

                if (value.getType() != ParadoxType.VARCHAR || value.getName().length() != 1) {
                    throw new ParadoxSyntaxErrorException(SyntaxError.INVALID_CHAR);
                }

                likeNode.setEscape(value.getName().charAt(0));
            } else {
                throw new ParadoxSyntaxErrorException(SyntaxError.INVALID_CHAR);
            }
        }
    }

    /**
     * Parses a not equals token.
     *
     * @param firstField the left not equals field.
     * @return the not equals node.
     * @throws SQLException in case of parse errors.
     */
    private NotEqualsNode parseNotEquals(final FieldNode firstField) throws SQLException {
        final ScannerPosition position = getPosition();
        this.expect(TokenType.NOT_EQUALS);
        final FieldNode value = this.parseField();
        return new NotEqualsNode(firstField, value, position);
    }

    /**
     * Parse the numeric token.
     *
     * @param fieldName the field name.
     * @return the field value.
     * @throws SQLException in case of parse errors.
     */
    private ValueNode parseNumeric(final String fieldName) throws SQLException {
        final ScannerPosition position = getPosition();
        this.expect(TokenType.NUMERIC);

        return new ValueNode(fieldName, position, ParadoxType.NUMBER);
    }

    /**
     * Parse the true token.
     *
     * @param fieldName the field name.
     * @return the field value.
     * @throws SQLException in case of parse errors.
     */
    private ValueNode parseTrue(final String fieldName) throws SQLException {
        final ScannerPosition position = getPosition();
        this.expect(TokenType.TRUE);

        final ValueNode value = new ValueNode("true", position, ParadoxType.BOOLEAN);
        value.setAlias(fieldName);
        return value;
    }

    /**
     * Parse the false token.
     *
     * @param fieldName the field name.
     * @return the field value.
     * @throws SQLException in case of parse errors.
     */
    private ValueNode parseFalse(final String fieldName) throws SQLException {
        final ScannerPosition position = getPosition();
        this.expect(TokenType.FALSE);

        final ValueNode value = new ValueNode("false", position, ParadoxType.BOOLEAN);
        value.setAlias(fieldName);
        return value;
    }

    private ValueNode parseNull() throws SQLException {
        final ScannerPosition position = getPosition();
        this.expect(TokenType.NULL);

        final ValueNode value = new ValueNode(null, position, ParadoxType.NULL);
        value.setAlias("null");
        return value;
    }

    /**
     * Parses the operators token.
     *
     * @param parent the node child.
     * @return the conditional operator node.
     * @throws SQLException in case or errors.
     */
    private AbstractConditionalNode parseOperators(final AbstractConditionalNode parent) throws SQLException {
        final ScannerPosition position = getPosition();
        AbstractConditionalNode ret;
        if (isToken(TokenType.AND)) {
            // Token type AND.
            this.expect(TokenType.AND);

            if (parent instanceof ANDNode) {
                ret = parent;
            } else {
                ret = new ANDNode(parent, position);
            }

            ret.addChild(this.parseSubCondition(null));
        } else {
            // Token type OR.
            this.expect(TokenType.OR);

            if (parent instanceof ORNode) {
                ret = parent;
            } else {
                ret = new ORNode(parent, position);
            }

            ret.addChild(this.parseSubCondition(null));
        }

        return ret;
    }

    /**
     * Parses ORDER BY node.
     *
     * @param select the select statement node.
     * @throws SQLException in case of failures.
     */
    private void parseOrderBy(final SelectNode select) throws SQLException {
        this.expect(TokenType.ORDER);

        ScannerPosition position = getPosition();
        this.expect(TokenType.BY);

        if (this.token == null) {
            addOffset(position, TokenType.BY.name().length());
            throw new ParadoxSyntaxErrorException(SyntaxError.EMPTY_COLUMN_LIST, position);
        }

        boolean firstField = true;
        while (this.token != null && !this.token.isSelectBreak()) {
            // Field Name
            expectComma(!firstField);
            firstField = false;

            final String fieldName = this.token.getValue();
            FieldNode fieldNode;
            position = getPosition();
            if (this.token.getType() == TokenType.NUMERIC) {
                fieldNode = parseNumeric(fieldName);
            } else if (this.token.getType() == TokenType.IDENTIFIER) {
                fieldNode = parseIdentifierFieldFunction(fieldName, true);
            } else {
                throw new ParadoxSyntaxErrorException(SyntaxError.UNEXPECTED_TOKEN, position);
            }

            OrderType type = getOrderType();

            select.addOrderBy(fieldNode, type);
        }
    }

    /**
     * Parses the order by type.
     *
     * @return the order by type.
     * @throws SQLException in case of failures.
     */
    private OrderType getOrderType() throws SQLException {
        OrderType type = OrderType.ASC;
        if (this.isToken(TokenType.ASC)) {
            this.expect(TokenType.ASC);
            // Default order, nothing to change on it.
        } else if (this.isToken(TokenType.DESC)) {
            this.expect(TokenType.DESC);
            type = OrderType.DESC;
        }
        return type;
    }

    /**
     * Parses GROUP BY node.
     *
     * @param select the select statement node.
     * @throws SQLException in case of failures.
     */
    private void parseGroupBy(final SelectNode select) throws SQLException {
        this.expect(TokenType.GROUP);

        ScannerPosition position = getPosition();
        this.expect(TokenType.BY);

        if (this.token == null) {
            addOffset(position, TokenType.BY.name().length());
            throw new ParadoxSyntaxErrorException(SyntaxError.EMPTY_COLUMN_LIST, position);
        }

        boolean firstField = true;
        while (this.token != null && !this.token.isConditionBreak()) {
            // Field Name
            expectComma(!firstField);
            firstField = false;

            final String fieldName = this.token.getValue();
            FieldNode fieldNode;
            position = getPosition();
            if (isToken(TokenType.NUMERIC)) {
                fieldNode = parseNumeric(fieldName);
            } else if (isToken(TokenType.CHARACTER)) {
                fieldNode = parseCharacter(fieldName);
            } else if (isToken(TokenType.NULL)) {
                fieldNode = this.parseNull();
            } else if (isToken(TokenType.TRUE)) {
                fieldNode = this.parseTrue(this.token.getValue());
            } else if (isToken(TokenType.FALSE)) {
                fieldNode = this.parseFalse(this.token.getValue());
            } else if (isToken(TokenType.IDENTIFIER)) {
                fieldNode = parseIdentifierFieldFunction(fieldName, false);
            } else {
                throw new ParadoxSyntaxErrorException(SyntaxError.UNEXPECTED_TOKEN, position);
            }

            select.addGroupBy(fieldNode);
        }
    }

    /**
     * Parse a Select Statement.
     *
     * @return a select statement node.
     * @throws SQLException in case of parse errors.
     */
    private SelectNode parseSelect() throws SQLException {
        ScannerPosition position = getPosition();
        final SelectNode select = new SelectNode(position);
        this.expect(TokenType.SELECT);

        // Allowed only in the beginning of Select Statement
        if (isToken(TokenType.DISTINCT)) {
            select.setDistinct(true);
            this.expect(TokenType.DISTINCT);
        }

        // Field loop
        if (this.token != null) {
            this.parseFields(select, true);
        }

        if (select.getFields().isEmpty()) {
            addOffset(position, TokenType.SELECT.name().length());
            throw new ParadoxSyntaxErrorException(SyntaxError.EMPTY_COLUMN_LIST, position);
        }

        if (isToken(TokenType.FROM)) {
            this.parseFrom(select);

            // Only SELECT with FROM can have WHERE, GROUP BY and ORDER BY clause.
            if (isToken(TokenType.WHERE)) {
                parseWhere(select);
            }

            if (isToken(TokenType.GROUP)) {
                this.parseGroupBy(select);
            }

            if (isToken(TokenType.ORDER)) {
                this.parseOrderBy(select);
            }
        }

        if (isToken(TokenType.LIMIT)) {
            this.parseLimit(select);
        }

        if (isToken(TokenType.OFFSET)) {
            this.parseOffset(select);
        }

        if (this.scanner.hasNext() || this.token != null) {
            throw new ParadoxSyntaxErrorException(SyntaxError.UNEXPECTED_TOKEN,
                    this.token.getPosition());
        }

        return select;
    }

    /**
     * Parses the limit token.
     *
     * @param select the select node.
     * @throws SQLException in case of failures.
     */
    private void parseLimit(final SelectNode select) throws SQLException {
        this.expect(TokenType.LIMIT);
        select.setLimit(Integer.valueOf(this.token.getValue()));
        this.expect(TokenType.NUMERIC);
    }

    /**
     * Parses the offset token.
     *
     * @param select the select node.
     * @throws SQLException in case of failures.
     */
    private void parseOffset(final SelectNode select) throws SQLException {
        this.expect(TokenType.OFFSET);
        select.setOffset(Integer.valueOf(this.token.getValue()));
        this.expect(TokenType.NUMERIC);
    }

    /**
     * Parses the WHERE clause.
     *
     * @param select the select node.
     * @throws SQLException in case of failures.
     */
    private void parseWhere(final SelectNode select) throws SQLException {
        ScannerPosition position = getPosition();
        this.expect(TokenType.WHERE);
        select.setCondition(this.parseCondition());

        if (select.getCondition() == null) {
            addOffset(position, TokenType.WHERE.name().length());
            throw new ParadoxSyntaxErrorException(SyntaxError.EMPTY_CONDITIONAL_LIST,
                    position);
        }
    }

    /**
     * Check if the current token is the desired type.
     *
     * @param type the token to check.
     * @return <code>true</code> if the current token is the desired type.
     */
    private boolean isToken(final TokenType type) {
        return this.token != null && this.token.getType() == type;
    }

    /**
     * Safe way to get current scanner position.
     *
     * @return the current scanner position.
     */
    private ScannerPosition getPosition() {
        if (this.token != null) {
            return this.token.getPosition();
        }

        return null;
    }

    /**
     * Safe way to add a offset to a scanner position.
     *
     * @param position the scanner position.
     * @param offset   the offset to add.
     */
    private static void addOffset(final ScannerPosition position, final int offset) {
        if (position != null) {
            position.addOffset(offset);
        }
    }
}
