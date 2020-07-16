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
import com.googlecode.paradox.parser.nodes.*;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.planner.nodes.ParameterNode;
import com.googlecode.paradox.planner.nodes.ValueNode;
import com.googlecode.paradox.planner.nodes.comparable.*;
import com.googlecode.paradox.planner.nodes.join.ANDNode;
import com.googlecode.paradox.planner.nodes.join.ORNode;
import com.googlecode.paradox.planner.sorting.OrderType;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses a SQL statement.
 *
 * @version 1.8
 * @since 1.0
 */
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
     * Parses the SQL statement.
     *
     * @return a list of statements.
     * @throws SQLException in case of parse errors.
     */
    public List<StatementNode> parse() throws SQLException {
        if (!this.scanner.hasNext()) {
            throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.UNEXPECTED_END_OF_STATEMENT);
        }

        this.token = this.scanner.nextToken();

        final List<StatementNode> statementNodes = new ArrayList<>();
        if (isToken(TokenType.SELECT)) {
            statementNodes.add(this.parseSelect());
        } else {
            throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.UNEXPECTED_TOKEN,
                    token.getPosition());
        }

        statementNodes.forEach(s -> s.setParameterCount(parameterCount));

        return statementNodes;
    }

    /**
     * Test for expected tokens.
     *
     * @param token the token to validate.
     * @throws SQLException in case of unexpected tokens.
     */
    private void expect(final TokenType token) throws SQLException {
        if (this.token.getType() != token) {
            throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.UNEXPECTED_TOKEN,
                    this.token.getPosition());
        }

        if (this.scanner.hasNext()) {
            this.token = this.scanner.nextToken();
        } else {
            this.token = null;
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
        final ScannerPosition position = this.token.getPosition();
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
        final ScannerPosition position = this.token.getPosition();
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
        final ScannerPosition position = this.token.getPosition();
        this.expect(TokenType.CHARACTER);

        return new ValueNode(fieldName, fieldName, position, Types.VARCHAR);
    }

    private String getFieldAlias(String fieldName) throws SQLException {
        String fieldAlias = fieldName;

        if (this.token != null) {
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
        while (this.scanner.hasNext() && !this.token.isConditionBreak()) {
            if (this.token.isOperator()) {
                ret = this.parseOperators(ret);
            } else if (isToken(TokenType.L_PAREN)) {
                this.expect(TokenType.L_PAREN);
                AbstractConditionalNode retValue = parseCondition();
                if (ret == null) {
                    ret = retValue;
                } else {
                    ret.addChild(retValue);
                }
                this.expect(TokenType.R_PAREN);
            } else if (isConditionalEnd()) {
                return ret;
            } else {
                if (ret == null) {
                    ret = this.parseFieldNode();
                } else {
                    ret.addChild(this.parseFieldNode());
                }
            }
        }

        return ret;
    }

    /**
     * Is the token indicates the end of conditionals.
     *
     * @return <code>true</code> is the end of conditionals.
     */
    private boolean isConditionalEnd() {
        return isToken(TokenType.WHERE) || isToken(TokenType.ORDER);
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
        final ScannerPosition position = this.token.getPosition();

        FieldNode ret;
        switch (this.token.getType()) {
            case CHARACTER:
                // Found a String value.
                this.expect(TokenType.CHARACTER);
                ret = new ValueNode(fieldName, null, position, Types.VARCHAR);
                break;
            case NUMERIC:
                // Found a numeric value.
                this.expect(TokenType.NUMERIC);
                ret = new ValueNode(fieldName, null, position, Types.NUMERIC);
                break;
            case NULL:
                this.expect(TokenType.NULL);
                ret = new ValueNode(null, null, position, Types.NULL);
                break;
            case QUESTION_MARK:
                this.expect(TokenType.QUESTION_MARK);
                ret = new ParameterNode(parameterCount, position);
                parameterCount++;
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
        }
        return new FieldNode(tableName, name, name, position);
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
            default:
                throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.UNEXPECTED_TOKEN,
                        this.token.getPosition());
        }

        return node;
    }

    /**
     * Parse the field list in SELECT statement.
     *
     * @param select the select node.
     * @throws SQLException in case of parse errors.
     */
    private void parseFields(final SelectNode select) throws SQLException {
        if (this.token == null) {
            // No tokens to process here.
            return;
        }

        boolean firstField = true;
        do {
            if (isToken(TokenType.FROM)) {
                break;
            }

            if (!firstField) {
                this.expect(TokenType.COMMA);
            }

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
                case ASTERISK:
                    node = this.parseAsterisk(null);
                    break;
                default:
                    node = this.parseIdentifier(fieldName);
                    break;
            }

            node.setAlias(getFieldAlias(node.getAlias()));
            select.addField(node);

            firstField = false;
        } while (this.scanner.hasNext());
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
        ScannerPosition position = this.token.getPosition();
        this.expect(TokenType.FROM);
        boolean firstField = true;
        do {
            if (isToken(TokenType.WHERE) || isToken(TokenType.ORDER)) {
                break;
            }
            if (!firstField) {
                this.expect(TokenType.COMMA);
            }
            if (isToken(TokenType.IDENTIFIER)) {
                this.parseJoinTable(select);
                firstField = false;
            }
        } while (this.scanner.hasNext());

        if (select.getTables().isEmpty()) {
            if (this.token != null) {
                position = token.getPosition();
            } else {
                position.addOffset(TokenType.FROM.name().length());
            }

            throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.EMPTY_TABLE_LIST, position);
        }
    }

    /**
     * Parse the identifier token associated with a field.
     *
     * @param fieldName the field name.
     * @return the field node.
     * @throws SQLException in case of parse errors.
     */
    private SQLNode parseIdentifier(final String fieldName) throws SQLException {
        String newTableName = null;
        String newFieldName = fieldName;

        @SuppressWarnings("java:S1941") final ScannerPosition position = this.token.getPosition();
        this.expect(TokenType.IDENTIFIER);

        if (isToken(TokenType.L_PAREN)) {
            // function
            return parseFunction(fieldName);
        } else if (isToken(TokenType.PERIOD)) {
            this.expect(TokenType.PERIOD);
            newTableName = fieldName;
            newFieldName = this.token.getValue();

            if (isToken(TokenType.ASTERISK)) {
                return parseAsterisk(newTableName);
            }

            this.expect(TokenType.IDENTIFIER);
        }

        return new FieldNode(newTableName, newFieldName, newFieldName, position);
    }

    private FunctionNode parseFunction(final String functionName) throws SQLException {
        final FunctionNode functionNode = new FunctionNode(functionName, token.getPosition());
        this.expect(TokenType.L_PAREN);

        boolean first = true;
        while (!isToken(TokenType.R_PAREN)) {
            if (!first) {
                this.expect(TokenType.COMMA);
            } else {
                first = false;
            }

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
                case ASTERISK:
                    functionNode.addParameter(this.parseAsterisk(null));
                    break;
                default:
                    functionNode.addParameter(this.parseIdentifierFieldFunction(this.token.getValue()));
                    break;
            }
        }
        this.expect(TokenType.R_PAREN);
        return functionNode;
    }

    private SQLNode parseIdentifierFieldFunction(final String fieldName) throws SQLException {
        String newTableName = null;
        String newFieldName = fieldName;

        @SuppressWarnings("java:S1941") final ScannerPosition position = this.token.getPosition();
        this.expect(TokenType.IDENTIFIER);

        if (isToken(TokenType.L_PAREN)) {
            // function
            return parseFunction(fieldName);
        } else if (isToken(TokenType.PERIOD)) {
            // If it has a Table Name.
            this.expect(TokenType.PERIOD);
            newTableName = fieldName;
            newFieldName = this.token.getValue();

            this.expect(TokenType.IDENTIFIER);
        }

        return new FieldNode(newTableName, newFieldName, newFieldName, position);
    }

    /**
     * Parses the join tokens.
     *
     * @param select the select node.
     * @throws SQLException in case of errors.
     */
    private void parseJoin(final SelectNode select) throws SQLException {
        while (this.scanner.hasNext() && (!isToken(TokenType.COMMA) && !isToken(TokenType.WHERE)
                && !isToken(TokenType.ORDER))) {

            // Inner, right or cross join.
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
        final ScannerPosition position = this.token.getPosition();
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
        final ScannerPosition position = this.token.getPosition();
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
        final ScannerPosition position = this.token.getPosition();
        this.expect(TokenType.MORE);

        if (isToken(TokenType.EQUALS)) {
            this.expect(TokenType.EQUALS);
            return new GreaterThanOrEqualsNode(firstField, this.parseField(), position);
        }

        return new GreaterThanNode(firstField, this.parseField(), position);
    }

    private InNode parseIn(final FieldNode firstField) throws SQLException {
        ScannerPosition position = this.token.getPosition();
        this.expect(TokenType.IN);
        this.expect(TokenType.L_PAREN);

        final InNode in = new InNode(firstField, position);

        boolean first = true;
        do {
            if (!first) {
                this.expect(TokenType.COMMA);
            } else {
                first = false;
            }

            if (isToken(TokenType.NUMERIC)) {
                in.addField(new ValueNode(token.getValue(), token.getValue(), token.getPosition(), Types.NUMERIC));
                this.expect(TokenType.NUMERIC);
            } else if (isToken(TokenType.CHARACTER)) {
                in.addField(new ValueNode(token.getValue(), token.getValue(), token.getPosition(), Types.VARCHAR));
                this.expect(TokenType.CHARACTER);
            } else {
                position = null;
                if (this.token != null) {
                    position = this.token.getPosition();
                }
                throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.UNEXPECTED_TOKEN, position);
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
        final ScannerPosition position = this.token.getPosition();
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
     * Parses like conditional.
     *
     * @param firstField the left more token field.
     * @return the null than node.
     * @throws SQLException in case of parse errors.
     */
    private LikeNode parseLike(final FieldNode firstField) throws SQLException {
        final ScannerPosition position = this.token.getPosition();
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
        final ScannerPosition position = this.token.getPosition();
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

                if (value.getSqlType() != Types.VARCHAR || value.getName().length() != 1) {
                    throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.INVALID_CHAR);
                }

                likeNode.setEscape(value.getName().charAt(0));
            } else {
                throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.INVALID_CHAR);
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
        final ScannerPosition position = this.token.getPosition();
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
        final ScannerPosition position = token.getPosition();
        this.expect(TokenType.NUMERIC);

        return new ValueNode(fieldName, fieldName, position, Types.NUMERIC);
    }

    private ValueNode parseNull() throws SQLException {
        final ScannerPosition position = token.getPosition();
        this.expect(TokenType.NULL);

        return new ValueNode(null, "null", position, Types.NULL);
    }

    /**
     * Parses the operators token.
     *
     * @param child the node child.
     * @return the conditional operator node.
     * @throws SQLException in case or errors.
     */
    private AbstractConditionalNode parseOperators(final AbstractConditionalNode child) throws SQLException {
        final ScannerPosition position = this.token.getPosition();
        AbstractConditionalNode ret;
        if (isToken(TokenType.AND)) {
            this.expect(TokenType.AND);
            if (child instanceof ANDNode) {
                ret = child;
            } else {
                ret = new ANDNode(child, position);
            }
        } else {
            // TokenType OR.
            this.expect(TokenType.OR);
            if (child instanceof ORNode) {
                ret = child;
            } else {
                ret = new ORNode(child, position);
            }
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
        this.expect(TokenType.BY);

        boolean firstField = true;
        do {
            // Field Name
            if (!firstField) {
                this.expect(TokenType.COMMA);
            }

            final String fieldName = this.token.getValue();
            FieldNode fieldNode;
            final ScannerPosition position = token.getPosition();
            switch (this.token.getType()) {
                case NUMERIC:
                    this.expect(TokenType.NUMERIC);
                    fieldNode = new ValueNode(fieldName, fieldName, position, Types.NUMERIC);
                    break;
                case IDENTIFIER:
                    fieldNode = parseIdentifierFieldOnly(fieldName);
                    break;
                default:
                    throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.UNEXPECTED_TOKEN, position);
            }

            OrderType type = OrderType.ASC;
            if (this.isToken(TokenType.ASC)) {
                this.expect(TokenType.ASC);
                // Default order, nothing to change on it.
            } else if (this.isToken(TokenType.DESC)) {
                this.expect(TokenType.DESC);
                type = OrderType.DESC;
            }

            select.addOrderBy(fieldNode, type);

            firstField = false;
        } while (this.scanner.hasNext());
    }

    private FieldNode parseIdentifierFieldOnly(final String fieldName) throws SQLException {
        String newTableName = null;
        String newFieldName = fieldName;

        @SuppressWarnings("java:S1941") final ScannerPosition position = this.token.getPosition();
        this.expect(TokenType.IDENTIFIER);

        if (isToken(TokenType.PERIOD)) {
            // If it has a Table Name.
            this.expect(TokenType.PERIOD);
            newTableName = fieldName;
            newFieldName = this.token.getValue();

            this.expect(TokenType.IDENTIFIER);
        }

        return new FieldNode(newTableName, newFieldName, newFieldName, position);
    }

    /**
     * Parse a Select Statement.
     *
     * @return a select statement node.
     * @throws SQLException in case of parse errors.
     */
    private SelectNode parseSelect() throws SQLException {
        final ScannerPosition position = this.token.getPosition();
        final SelectNode select = new SelectNode(position);
        this.expect(TokenType.SELECT);

        // Allowed only in the beginning of Select Statement
        if (isToken(TokenType.DISTINCT)) {
            select.setDistinct(true);
            this.expect(TokenType.DISTINCT);
        }

        // Field loop
        this.parseFields(select);

        if (isToken(TokenType.FROM)) {
            this.parseFrom(select);

            // Only SELECT with FROM can have WHERE clause.
            if (this.scanner.hasNext() && (this.token.getType() == TokenType.WHERE)) {
                this.expect(TokenType.WHERE);
                select.setCondition(this.parseCondition());
            }

            if (isToken(TokenType.ORDER)) {
                this.parseOrderBy(select);
            }
        }

        if (this.scanner.hasNext() || this.token != null) {
            throw new ParadoxSyntaxErrorException(ParadoxSyntaxErrorException.Error.UNEXPECTED_TOKEN,
                    this.token.getPosition());
        }

        return select;
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
}
