/*
 * SQLParser.java
 *
 * 03/12/2009
 * Copyright (C) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.parser;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.paradox.parser.nodes.FieldNode;
import com.googlecode.paradox.parser.nodes.JoinNode;
import com.googlecode.paradox.parser.nodes.JoinType;
import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.parser.nodes.SelectNode;
import com.googlecode.paradox.parser.nodes.StatementNode;
import com.googlecode.paradox.parser.nodes.TableNode;
import com.googlecode.paradox.parser.nodes.comparisons.BetweenNode;
import com.googlecode.paradox.parser.nodes.comparisons.EqualsNode;
import com.googlecode.paradox.parser.nodes.comparisons.GreaterThanNode;
import com.googlecode.paradox.parser.nodes.comparisons.LessThanNode;
import com.googlecode.paradox.parser.nodes.comparisons.NotEqualsNode;
import com.googlecode.paradox.parser.nodes.conditional.ANDNode;
import com.googlecode.paradox.parser.nodes.conditional.ExistsNode;
import com.googlecode.paradox.parser.nodes.conditional.NOTNode;
import com.googlecode.paradox.parser.nodes.conditional.ORNode;
import com.googlecode.paradox.parser.nodes.conditional.XORNode;
import com.googlecode.paradox.parser.nodes.values.AsteriskNode;
import com.googlecode.paradox.parser.nodes.values.CharacterNode;
import com.googlecode.paradox.parser.nodes.values.NumericNode;
import com.googlecode.paradox.utils.SQLStates;

/**
 * Parses a SQL statement.
 *
 * @author Leonardo Alves da Costa
 * @since 1.0
 * @version 1.1
 */
public class SQLParser {

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

    /**
     * Creates a new instance.
     * 
     * @param sql
     *            the SQL to parse.
     * @throws SQLException
     *             in case of parse errors.
     */
    public SQLParser(final String sql) throws SQLException {
        this.sql = sql;
        scanner = new Scanner(sql);
    }

    /**
     * Test for expected tokens.
     * 
     * @param rparens
     *            the tokens to validate.
     * @throws SQLException
     *             in case of unexpected tokens.
     */
    private void expect(final TokenType... rparens) throws SQLException {
        boolean found = false;
        for (final TokenType rparen : rparens) {
            if (token.getType() == rparen) {
                // Expected do not happen
                found = true;
                break;
            }
        }
        if (!found) {
            throw new SQLException(String.format("Unexpected error in SQL syntax (%s)", token.getValue()), SQLStates.INVALID_SQL.getValue());
        }
        if (scanner.hasNext()) {
            token = scanner.nextToken();
        } else {
            token = null;
        }
    }

    /**
     * Test for a token.
     * 
     * @param rparen
     *            the token to test.
     * @param message
     *            message in case of invalid token.
     * @throws SQLException
     *             in case of parse errors.
     */
    private void expect(final TokenType rparen, final String message) throws SQLException {
        if (token.getType() != rparen) {
            throw new SQLException(message, SQLStates.INVALID_SQL.getValue());
        }
        if (scanner.hasNext()) {
            token = scanner.nextToken();
        } else {
            token = null;
        }
    }

    /**
     * Parses the SQL statement.
     * 
     * @return a list of statements.
     * @throws SQLException
     *             in case of parse errors.
     */
    public List<StatementNode> parse() throws SQLException {
        final ArrayList<StatementNode> statementList = new ArrayList<>();
        while (scanner.hasNext()) {
            token = scanner.nextToken();

            switch (token.getType()) {
            case SEMI:
                if (statementList.isEmpty()) {
                    throw new SQLException("Unexpected semicolon");
                }
                break;
            case SELECT:
                statementList.add(parseSelect());
                break;
            case INSERT:
                throw new SQLFeatureNotSupportedException("Not supported yet.", SQLStates.INVALID_SQL.getValue());
            case DELETE:
                throw new SQLFeatureNotSupportedException("Not supported yet.", SQLStates.INVALID_SQL.getValue());
            case UPDATE:
                throw new SQLFeatureNotSupportedException("Not supported yet.", SQLStates.INVALID_SQL.getValue());
            default:
                throw new SQLException("Invalid SQL: " + sql, SQLStates.INVALID_SQL.getValue());
            }
            return statementList;
        }
        throw new SQLException("Invalid SQL: " + sql, SQLStates.INVALID_SQL.getValue());
    }

    /**
     * Parses the conditional statements.
     * 
     * @return the node.
     * @throws SQLException
     *             in case of parse errors.
     */
    private SQLNode parseCondition() throws SQLException {
        if (token.getType() == TokenType.NOT) {
            return new NOTNode(parseCondition());
        } else if (token.isOperator()) {
            switch (token.getType()) {
            case AND:
                expect(TokenType.AND);
                return new ANDNode(null);
            case OR:
                expect(TokenType.OR);
                return new ORNode(null);
            case XOR:
                expect(TokenType.XOR);
                return new XORNode(null);
            default:
                throw new SQLException("Invalid operator location.", SQLStates.INVALID_SQL.getValue());
            }
        } else if (token.getType() == TokenType.LPAREN) {
            expect(TokenType.RPAREN, "Right parentesis expected");
        } else if (token.getType() == TokenType.EXISTS) {
            expect(TokenType.EXISTS);
            expect(TokenType.LPAREN, "Left parentesis expected.");
            final SelectNode select = parseSelect();
            expect(TokenType.RPAREN, "Left parentesis expected.");
            return new ExistsNode(select);
        } else {
            final FieldNode firstField = parseField();

            switch (token.getType()) {
            case BETWEEN: {
                expect(TokenType.BETWEEN);
                final FieldNode left = parseField();
                expect(TokenType.AND, "AND expected.");
                final FieldNode right = parseField();
                return new BetweenNode(firstField, left, right);
            }
            case EQUALS: {
                expect(TokenType.EQUALS);
                final FieldNode value = parseField();
                return new EqualsNode(firstField, value);
            }
            case NOTEQUALS: {
                expect(TokenType.NOTEQUALS);
                final FieldNode value = parseField();
                return new NotEqualsNode(firstField, value);
            }
            case NOTEQUALS2: {
                expect(TokenType.NOTEQUALS2);
                final FieldNode value = parseField();
                return new NotEqualsNode(firstField, value);
            }
            case LESS: {
                expect(TokenType.LESS);
                final FieldNode value = parseField();
                return new LessThanNode(firstField, value);
            }
            case MORE: {
                expect(TokenType.MORE);
                final FieldNode value = parseField();
                return new GreaterThanNode(firstField, value);
            }
            default:
                throw new SQLException("Invalid operator.", SQLStates.INVALID_SQL.getValue());
            }
        }
        return null;
    }

    /**
     * Parses the conditional listing.
     * 
     * @return a list of nodes.
     * @throws SQLException
     *             in case of parse errors.
     */
    private ArrayList<SQLNode> parseConditionList() throws SQLException {
        final ArrayList<SQLNode> conditions = new ArrayList<>();

        while (scanner.hasNext()) {
            if (token.isConditionBreak()) {
                break;
            }
            conditions.add(parseCondition());
        }
        return conditions;
    }

    private FieldNode parseField() throws SQLException {
        String tableName = null;
        String fieldName = token.getValue();

        expect(TokenType.IDENTIFIER, TokenType.NUMERIC, TokenType.CHARACTER);

        // If it has a Table Name
        if (scanner.hasNext() && token.getType() == TokenType.PERIOD) {
            expect(TokenType.PERIOD);
            tableName = fieldName;
            fieldName = token.getValue();
            expect(TokenType.IDENTIFIER);
        }
        return new FieldNode(tableName, fieldName, fieldName);
    }

    /**
     * Parse a Select Statement.
     *
     * @return a select statement node.
     * @throws SQLException
     *             in case of parse errors.
     */
    private SelectNode parseSelect() throws SQLException {
        final SelectNode select = new SelectNode();
        expect(TokenType.SELECT);

        // Allowed only in the beginning of Select Statement
        if (token.getType() == TokenType.DISTINCT) {
            select.setDistinct(true);
            expect(TokenType.DISTINCT);
        }

        // Field loop
        boolean firstField = true;
        while (scanner.hasNext()) {
            if (token.getType() == TokenType.DISTINCT) {
                throw new SQLException("Invalid statement.");
            }

            if (token.getType() != TokenType.FROM) {
                // Field Name
                if (!firstField) {
                    expect(TokenType.COMMA, "Missing comma.");
                }
                String tableName = null;
                String fieldName = token.getValue();
                String fieldAlias = fieldName;

                if (token.getType() == TokenType.CHARACTER) {
                    expect(TokenType.CHARACTER);
                    // Field alias (with AS identifier)
                    if (token.getType() == TokenType.AS) {
                        expect(TokenType.AS);
                        fieldAlias = token.getValue();
                        expect(TokenType.IDENTIFIER);
                    } else if (token.getType() == TokenType.IDENTIFIER) {
                        // Field alias (without AS identifier)
                        fieldAlias = token.getValue();
                        expect(TokenType.IDENTIFIER);
                    }
                    select.getFields().add(new CharacterNode(fieldName, fieldAlias));
                } else if (token.getType() == TokenType.NUMERIC) {
                    expect(TokenType.NUMERIC);
                    // Field alias (with AS identifier)
                    if (token.getType() == TokenType.AS) {
                        expect(TokenType.AS);
                        fieldAlias = token.getValue();
                        expect(TokenType.IDENTIFIER);
                    } else if (token.getType() == TokenType.IDENTIFIER) {
                        // Field alias (without AS identifier)
                        fieldAlias = token.getValue();
                        expect(TokenType.IDENTIFIER);
                    }
                    select.getFields().add(new NumericNode(fieldName, fieldAlias));
                } else if (token.getType() == TokenType.ASTERISK) {
                    select.getFields().add(new AsteriskNode());
                    expect(TokenType.ASTERISK);
                } else {
                    expect(TokenType.IDENTIFIER);

                    if (token.getType() == TokenType.IDENTIFIER || token.getType() == TokenType.AS || token.getType() == TokenType.PERIOD) {
                        // If it has a Table Name
                        if (token.getType() == TokenType.PERIOD) {
                            expect(TokenType.PERIOD);
                            tableName = fieldName;
                            fieldAlias = fieldName;
                            fieldName = token.getValue();
                            expect(TokenType.IDENTIFIER);
                        }
                        // Field alias (with AS identifier)
                        if (token.getType() == TokenType.AS) {
                            expect(TokenType.AS);
                            fieldAlias = token.getValue();
                            // may be: select bebebe as name
                            // select bebebe as "Name"
                            // select bebebe as 'Name'
                            expect(TokenType.CHARACTER, TokenType.IDENTIFIER);
                        } else if (token.getType() == TokenType.IDENTIFIER) {
                            // Field alias (without AS identifier)
                            fieldAlias = token.getValue();
                            expect(TokenType.IDENTIFIER);
                        }
                    }
                    select.getFields().add(new FieldNode(tableName, fieldName, fieldAlias));
                }
                firstField = false;
            } else {
                break;
            }
        }

        if (token.getType() == TokenType.FROM) {
            expect(TokenType.FROM);
            firstField = true;
            do {
                if (token.getType() == TokenType.WHERE) {
                    break;
                }
                if (!firstField) {
                    expect(TokenType.COMMA, "Missing comma.");
                }
                if (token.getType() == TokenType.IDENTIFIER) {
                    final String tableName = token.getValue();
                    String tableAlias = tableName;

                    if (scanner.hasNext()) {
                        expect(TokenType.IDENTIFIER);
                        if (token.getType() == TokenType.IDENTIFIER || token.getType() == TokenType.AS) {
                            // Field alias (with AS identifier)
                            if (token.getType() == TokenType.AS) {
                                expect(TokenType.AS);
                                tableAlias = token.getValue();
                                expect(TokenType.IDENTIFIER);
                            } else if (token.getType() == TokenType.IDENTIFIER) {
                                // Field alias (without AS identifier)
                                tableAlias = token.getValue();
                                expect(TokenType.IDENTIFIER);
                            }
                        }
                    }

                    final TableNode table = new TableNode(tableName, tableAlias);
                    while (scanner.hasNext() && token.getType() != TokenType.COMMA && token.getType() != TokenType.WHERE) {
                        final JoinNode join = new JoinNode();

                        // Inner join
                        if (token.getType() == TokenType.LEFT) {
                            join.setType(JoinType.LEFT_JOIN);
                            expect(TokenType.LEFT);
                        } else if (token.getType() == TokenType.RIGHT) {
                            join.setType(JoinType.RIGHT_JOIN);
                            expect(TokenType.RIGHT);
                        }
                        if (token.getType() == TokenType.INNER) {
                            expect(TokenType.INNER);
                        } else if (token.getType() == TokenType.OUTER) {
                            expect(TokenType.OUTER);
                        }
                        expect(TokenType.JOIN);
                        join.setTableName(token.getValue());
                        join.setTableAlias(token.getValue());
                        expect(TokenType.IDENTIFIER);
                        if (token.getType() == TokenType.AS) {
                            expect(TokenType.AS);
                            join.setTableAlias(token.getValue());
                            expect(TokenType.IDENTIFIER);
                        } else if (token.getType() != TokenType.ON) {
                            join.setTableAlias(token.getValue());
                            expect(TokenType.IDENTIFIER);
                        }
                        expect(TokenType.ON);
                        join.setConditions(parseConditionList());
                        table.addJoin(join);
                    }

                    select.getTables().add(table);
                    firstField = false;
                }
            } while (scanner.hasNext());

            if (scanner.hasNext()) {
                if (token.getType() == TokenType.WHERE) {
                    expect(TokenType.WHERE);
                    select.setConditions(parseConditionList());
                }
            }
        } else {
            throw new SQLException("FROM expected.", SQLStates.INVALID_SQL.getValue());
        }
        return select;
    }
}
