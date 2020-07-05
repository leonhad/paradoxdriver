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

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.parser.nodes.*;
import com.googlecode.paradox.parser.nodes.values.AsteriskNode;
import com.googlecode.paradox.parser.nodes.values.CharacterNode;
import com.googlecode.paradox.parser.nodes.values.NumericNode;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.planner.nodes.comparable.*;
import com.googlecode.paradox.planner.nodes.join.ANDNode;
import com.googlecode.paradox.planner.nodes.join.ORNode;
import com.googlecode.paradox.planner.nodes.value.NullNode;
import com.googlecode.paradox.planner.nodes.value.StringNode;
import com.googlecode.paradox.utils.Constants;
import com.googlecode.paradox.utils.SQLStates;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses a SQL statement.
 *
 * @version 1.6
 * @since 1.0
 */
public final class SQLParser {

    /**
     * The scanner used to read tokens.
     */
    private final Scanner scanner;

    /**
     * The SQL to parse.
     */
    private final String sql;

    /**
     * The current token.
     */
    private Token token;

    private final ParadoxConnection connection;

    /**
     * Creates a new instance.
     *
     * @param connection the Paradox connection.
     * @param sql        the SQL to parse.
     * @throws SQLException in case of parse errors.
     */
    public SQLParser(final ParadoxConnection connection, final String sql) throws SQLException {
        this.connection = connection;
        this.sql = sql;
        this.scanner = new Scanner(connection, sql);
    }

    /**
     * Parses the SQL statement.
     *
     * @return a list of statements.
     * @throws SQLException in case of parse errors.
     */
    public List<StatementNode> parse() throws SQLException {
        if (!this.scanner.hasNext()) {
            throw new SQLException(this.sql, SQLStates.INVALID_SQL.getValue());
        }
        this.token = this.scanner.nextToken();

        final ArrayList<StatementNode> statementList = new ArrayList<>();
        switch (this.token.getType()) {
            case SELECT:
                statementList.add(this.parseSelect());
                break;
            default:
                throw new SQLFeatureNotSupportedException(Constants.ERROR_UNSUPPORTED_OPERATION,
                        SQLStates.INVALID_SQL.getValue());
        }
        return statementList;
    }

    /**
     * Test for expected tokens.
     *
     * @param token the token to validate.
     * @throws SQLException in case of unexpected tokens.
     */
    private void expect(final TokenType token) throws SQLException {
        if (this.token.getType() != token) {
            throw new SQLException(String.format("Unexpected error in SQL syntax (%s)", this.token.getValue()),
                    SQLStates.INVALID_SQL.getValue());
        }

        if (this.scanner.hasNext()) {
            this.token = this.scanner.nextToken();
        } else {
            this.token = null;
        }
    }

    /**
     * Test for a token.
     *
     * @param rparen  the token to test.
     * @param message message in case of invalid token.
     * @throws SQLException in case of parse errors.
     */
    private void expect(final TokenType rparen, final String message) throws SQLException {
        if (this.token.getType() != rparen) {
            throw new SQLException(message, SQLStates.INVALID_SQL.getValue());
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
     * @param select the select node.
     * @throws SQLException in case of parse errors.
     */
    private void parseAsterisk(final SelectNode select) throws SQLException {
        select.addField(new AsteriskNode(connection));
        this.expect(TokenType.ASTERISK);
    }

    /**
     * Parses between token.
     *
     * @param field the between field.
     * @return the between node.
     * @throws SQLException in case of parse errors.
     */
    private BetweenNode parseBetween(final FieldNode field) throws SQLException {
        this.expect(TokenType.BETWEEN);
        final FieldNode left = this.parseField();
        this.expect(TokenType.AND, "AND expected.");
        final FieldNode right = this.parseField();
        return new BetweenNode(connection, field, left, right);
    }

    /**
     * Parse the character token.
     *
     * @param select    the select node.
     * @param fieldName the field name.
     * @throws SQLException in case of parse errors.
     */
    private void parseCharacter(final SelectNode select, final String fieldName) throws SQLException {
        this.expect(TokenType.CHARACTER);

        final String fieldAlias = getFieldAlias(fieldName);
        select.addField(new CharacterNode(connection, fieldName, fieldAlias));
    }

    private String getFieldAlias(String fieldName) throws SQLException {
        String fieldAlias = fieldName;
        // Field alias (with AS identifier)
        if (this.token.getType() == TokenType.AS) {
            this.expect(TokenType.AS);
            fieldAlias = this.token.getValue();
            this.expect(TokenType.IDENTIFIER);
        } else if (this.token.getType() == TokenType.IDENTIFIER) {
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
        while (this.scanner.hasNext() && !this.token.isConditionBreak()) {
            if (this.token.isOperator()) {
                ret = this.parseOperators(ret);
            } else if (this.token.getType() == TokenType.L_PAREN) {
                this.expect(TokenType.L_PAREN);
                AbstractConditionalNode retValue = parseCondition();
                if (ret == null) {
                    ret = retValue;
                } else {
                    ret.addChild(retValue);
                }
                this.expect(TokenType.R_PAREN, "Right parenthesis expected");
            } else if (token.getType() == TokenType.WHERE || token.getType() == TokenType.ORDER) {
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
     * Parses the equals tokens.
     *
     * @param field the left field token.
     * @return the equals node.
     * @throws SQLException in case of parse errors.
     */
    private EqualsNode parseEquals(final FieldNode field) throws SQLException {
        this.expect(TokenType.EQUALS);
        final FieldNode value = this.parseField();
        return new EqualsNode(connection, field, value);
    }

    /**
     * Parses the table join fields.
     *
     * @return the field node.
     * @throws SQLException in case of errors.
     */
    private FieldNode parseField() throws SQLException {
        String tableName = null;
        String fieldName = this.token.getValue();

        FieldNode ret;
        if (this.token.getType() == TokenType.CHARACTER) {
            // Found a String value.
            this.expect(TokenType.CHARACTER);
            ret = new StringNode(connection, fieldName);
        } else if (this.token.getType() == TokenType.NUMERIC) {
            // Found a numeric value.
            this.expect(TokenType.NUMERIC);
            ret = new StringNode(connection, fieldName);
        } else if (this.token.getType() == TokenType.NULL) {
            this.expect(TokenType.NULL);
            ret = new NullNode(connection);
        } else {
            // Found a table field.
            this.expect(TokenType.IDENTIFIER);

            // If it has a Table Name
            if (this.scanner.hasNext() && (this.token.getType() == TokenType.PERIOD)) {
                this.expect(TokenType.PERIOD);
                tableName = fieldName;
                fieldName = this.token.getValue();
                this.expect(TokenType.IDENTIFIER);
            }
            ret = new FieldNode(connection, tableName, fieldName, fieldName);
        }

        return ret;
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
                throw new SQLException("Invalid operator.", SQLStates.INVALID_SQL.getValue());
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
        boolean firstField = true;
        while (this.scanner.hasNext()) {
            if (this.token.getType() == TokenType.DISTINCT) {
                throw new SQLException("Invalid statement.");
            }

            if (this.token.getType() != TokenType.FROM) {
                // Field Name
                if (!firstField) {
                    this.expect(TokenType.COMMA, "Missing comma.");
                }
                final String fieldName = this.token.getValue();

                if (this.token.getType() == TokenType.CHARACTER) {
                    this.parseCharacter(select, fieldName);
                } else if (this.token.getType() == TokenType.NUMERIC) {
                    this.parseNumeric(select, fieldName);
                } else if (this.token.getType() == TokenType.ASTERISK) {
                    this.parseAsterisk(select);
                } else {
                    this.parseIdentifier(select, fieldName);
                }

                firstField = false;
            } else {
                break;
            }
        }
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

        if (token != null
                && ((this.token.getType() == TokenType.IDENTIFIER) || (this.token.getType() == TokenType.AS))) {
            // Field alias (with AS identifier)
            if (this.token.getType() == TokenType.AS) {
                this.expect(TokenType.AS);
            }

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
        this.expect(TokenType.FROM);
        boolean firstField = true;
        do {
            if (this.token.getType() == TokenType.WHERE) {
                break;
            }
            if (!firstField) {
                this.expect(TokenType.COMMA, "Missing comma.");
            }
            if (this.token.getType() == TokenType.IDENTIFIER) {
                this.parseJoinTable(select);
                firstField = false;
            }
        } while (this.scanner.hasNext());

        if (this.scanner.hasNext() && (this.token.getType() == TokenType.WHERE)) {
            this.expect(TokenType.WHERE);
            select.setCondition(this.parseCondition());
        }
    }

    /**
     * Parse the identifier token associated with a field.
     *
     * @param select    the select node.
     * @param fieldName the field name.
     * @throws SQLException in case of parse errors.
     */
    private void parseIdentifier(final SelectNode select, final String fieldName)
            throws SQLException {
        String fieldAlias = fieldName;
        String newTableName = null;
        String newFieldName = fieldName;
        this.expect(TokenType.IDENTIFIER);

        if ((this.token.getType() == TokenType.IDENTIFIER) || (this.token.getType() == TokenType.AS)
                || (this.token.getType() == TokenType.PERIOD)) {
            // If it has a Table Name
            if (this.token.getType() == TokenType.PERIOD) {
                this.expect(TokenType.PERIOD);
                newTableName = fieldName;
                newFieldName = this.token.getValue();
                fieldAlias = newFieldName;

                if (this.token.getType() == TokenType.ASTERISK) {
                    this.expect(TokenType.ASTERISK);
                    select.addField(new AsteriskNode(connection, newTableName));
                    return;
                }

                this.expect(TokenType.IDENTIFIER);
            }

            fieldAlias = getFieldAlias(fieldAlias);
        }

        select.addField(new FieldNode(connection, newTableName, newFieldName, fieldAlias));
    }

    /**
     * Parses the join tokens.
     *
     * @param select the select node.
     * @throws SQLException in case of errors.
     */
    private void parseJoin(final SelectNode select) throws SQLException {
        while (this.scanner.hasNext() && (this.token.getType() != TokenType.COMMA)
                && (this.token.getType() != TokenType.WHERE)) {

            // Inner, right or cross join.
            JoinType joinType = JoinType.INNER;
            if (this.token.getType() == TokenType.FULL) {
                joinType = JoinType.FULL;
                this.expect(TokenType.FULL);
                if (this.token.getType() == TokenType.OUTER) {
                    this.expect(TokenType.OUTER);
                }
            } else if (this.token.getType() == TokenType.LEFT) {
                joinType = JoinType.LEFT;
                this.expect(TokenType.LEFT);
                if (this.token.getType() == TokenType.OUTER) {
                    this.expect(TokenType.OUTER);
                }
            } else if (this.token.getType() == TokenType.RIGHT) {
                joinType = JoinType.RIGHT;
                this.expect(TokenType.RIGHT);
                if (this.token.getType() == TokenType.OUTER) {
                    this.expect(TokenType.OUTER);
                }
            } else if (this.token.getType() == TokenType.CROSS) {
                joinType = JoinType.CROSS;
                this.expect(TokenType.CROSS);
            } else if (this.token.getType() == TokenType.INNER) {
                this.expect(TokenType.INNER);
            }

            this.expect(TokenType.JOIN);

            String schemaName = null;
            String tableName = this.token.getValue();
            this.expect(TokenType.IDENTIFIER);

            // Have schema name.
            if (this.scanner.hasNext() && this.token.getType() == TokenType.PERIOD) {
                expect(TokenType.PERIOD);
                schemaName = tableName;
                tableName = this.token.getValue();
                this.expect(TokenType.IDENTIFIER);
            }

            final String tableAlias = this.parseFields(tableName);

            final JoinNode joinTable = new JoinNode(connection, schemaName, tableName, tableAlias, joinType);
            if (joinType != JoinType.CROSS) {
                // Cross join don't have join clause.
                this.expect(TokenType.ON);
                joinTable.setCondition(this.parseCondition());
            }
            select.addTable(joinTable);
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
        this.expect(TokenType.IDENTIFIER);

        // Have schema name.
        if (this.token != null && this.token.getType() == TokenType.PERIOD) {
            expect(TokenType.PERIOD);
            schemaName = tableName;
            tableName = this.token.getValue();
            this.expect(TokenType.IDENTIFIER);
        }

        final String tableAlias = this.parseFields(tableName);

        final TableNode table = new TableNode(connection, schemaName, tableName, tableAlias);
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
        this.expect(TokenType.LESS);

        if (token.getType() == TokenType.EQUALS) {
            this.expect(TokenType.EQUALS);
            return new LessThanOrEqualsNode(connection, firstField, this.parseField());
        }

        return new LessThanNode(connection, firstField, parseField());
    }

    /**
     * Parses more token.
     *
     * @param firstField the left more token field.
     * @return the grater than node.
     * @throws SQLException in case of parse errors.
     */
    private AbstractComparableNode parseMore(final FieldNode firstField) throws SQLException {
        this.expect(TokenType.MORE);

        if (token.getType() == TokenType.EQUALS) {
            this.expect(TokenType.EQUALS);
            return new GreaterThanOrEqualsNode(connection, firstField, this.parseField());
        }

        return new GreaterThanNode(connection, firstField, this.parseField());
    }

    /**
     * Parses null conditional token.
     *
     * @param firstField the left more token field.
     * @return the null than node.
     * @throws SQLException in case of parse errors.
     */
    private AbstractComparableNode parseNull(final FieldNode firstField) throws SQLException {
        this.expect(TokenType.IS);
        AbstractComparableNode ret;
        if (token.getType() == TokenType.NOT) {
            this.expect(TokenType.NOT);
            ret = new IsNotNullNode(connection, firstField);
        } else {
            ret = new IsNullNode(connection, firstField);
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
        this.expect(TokenType.LIKE);

        return new LikeNode(connection, firstField, parseField());
    }

    /**
     * Parses ilike conditional.
     *
     * @param firstField the left more token field.
     * @return the null than node.
     * @throws SQLException in case of parse errors.
     */
    private ILikeNode parseILike(final FieldNode firstField) throws SQLException {
        this.expect(TokenType.ILIKE);

        return new ILikeNode(connection, firstField, parseField());
    }

    /**
     * Parses a not equals token.
     *
     * @param firstField the left not equals field.
     * @return the not equals node.
     * @throws SQLException in case of parse errors.
     */
    private NotEqualsNode parseNotEquals(final FieldNode firstField) throws SQLException {
        this.expect(TokenType.NOT_EQUALS);
        final FieldNode value = this.parseField();
        return new NotEqualsNode(connection, firstField, value);
    }

    /**
     * Parse the numeric token.
     *
     * @param select    the select node.
     * @param fieldName the field name.
     * @throws SQLException in case of parse errors.
     */
    private void parseNumeric(final SelectNode select, final String fieldName) throws SQLException {
        this.expect(TokenType.NUMERIC);

        final String fieldAlias = getFieldAlias(fieldName);
        select.addField(new NumericNode(connection, fieldName, fieldAlias));
    }

    /**
     * Parses the operators token.
     *
     * @param child the node child.
     * @return the conditional operator node.
     * @throws SQLException in case or errors.
     */
    private AbstractConditionalNode parseOperators(final AbstractConditionalNode child) throws SQLException {
        switch (this.token.getType()) {
            case AND:
                this.expect(TokenType.AND);
                return new ANDNode(connection, child);
            case OR:
                this.expect(TokenType.OR);
                return new ORNode(connection, child);
            default:
                throw new SQLException("Invalid operator location.", SQLStates.INVALID_SQL.getValue());
        }
    }

    /**
     * Parse a Select Statement.
     *
     * @return a select statement node.
     * @throws SQLException in case of parse errors.
     */
    private SelectNode parseSelect() throws SQLException {
        final SelectNode select = new SelectNode(connection);
        this.expect(TokenType.SELECT);

        // Allowed only in the beginning of Select Statement
        if (this.token.getType() == TokenType.DISTINCT) {
            select.setDistinct(true);
            this.expect(TokenType.DISTINCT);
        }

        // Field loop
        this.parseFields(select);

        if (this.token.getType() == TokenType.FROM) {
            this.parseFrom(select);
        } else {
            throw new SQLException("FROM expected.", SQLStates.INVALID_SQL.getValue());
        }
        return select;
    }
}
