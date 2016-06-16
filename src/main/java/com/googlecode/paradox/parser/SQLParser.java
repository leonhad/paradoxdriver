package com.googlecode.paradox.parser;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;

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

public class SQLParser {

	private Token t;
	private final String sql;
	private final Scanner scanner;

	public SQLParser(final String sql) throws SQLException {
		this.sql = sql;
		scanner = new Scanner(sql);
	}

	public ArrayList<StatementNode> parse() throws SQLException {
		final ArrayList<StatementNode> statementList = new ArrayList<StatementNode>();
		while (scanner.hasNext()) {
			t = scanner.nextToken();

			switch (t.getType()) {
			case SEMI:
				if (statementList.size() == 0) {
					throw new SQLException("Unexpected semicolon");
				}
				break;
			case SELECT:
				statementList.add(parseSelect());
				break;
			case INSERT:
				throw new SQLFeatureNotSupportedException("Not supported yet.", SQLStates.INVALID_SQL);
			case DELETE:
				throw new SQLFeatureNotSupportedException("Not supported yet.", SQLStates.INVALID_SQL);
			case UPDATE:
				throw new SQLFeatureNotSupportedException("Not supported yet.", SQLStates.INVALID_SQL);
			default:
				throw new SQLException("Invalid SQL: " + sql, SQLStates.INVALID_SQL);
			}
			return statementList;
		}
		throw new SQLException("Invalid SQL: " + sql, SQLStates.INVALID_SQL);
	}

	/**
	 * Parse a Select Statement
	 *
	 * @param parent
	 *            Statement Owner
	 * @return a Select Statement Node
	 * @throws SQLException
	 *             Invalid SQL
	 * @throws IOException
	 *             in case of parser exception
	 */
	private SelectNode parseSelect() throws SQLException {
		final SelectNode select = new SelectNode();
		expect(TokenType.SELECT);

		// Allowed only in the beginning of Select Statement
		if (t.getType() == TokenType.DISTINCT) {
			select.setDistinct(true);
			expect(TokenType.DISTINCT);
		}

		// Field loop
		boolean firstField = true;
		while (scanner.hasNext()) {
			if (t.getType() == TokenType.DISTINCT) {
				throw new SQLException("Invalid statement.");
			}

			if (t.getType() != TokenType.FROM) {
				// Field Name
				if (!firstField) {
					expect(TokenType.COMMA, "Missing comma.");
				}
				String tableName = null;
				String fieldName = t.getValue();
				String fieldAlias = fieldName;

				if (t.getType() == TokenType.CHARACTER) {
					expect(TokenType.CHARACTER);
					// Field alias (with AS identifier)
					if (t.getType() == TokenType.AS) {
						expect(TokenType.AS);
						fieldAlias = t.getValue();
						expect(TokenType.IDENTIFIER);
					} else if (t.getType() == TokenType.IDENTIFIER) {
						// Field alias (without AS identifier)
						fieldAlias = t.getValue();
						expect(TokenType.IDENTIFIER);
					}
					select.getFields().add(new CharacterNode(fieldName, fieldAlias));
				} else if (t.getType() == TokenType.NUMERIC) {
					expect(TokenType.NUMERIC);
					// Field alias (with AS identifier)
					if (t.getType() == TokenType.AS) {
						expect(TokenType.AS);
						fieldAlias = t.getValue();
						expect(TokenType.IDENTIFIER);
					} else if (t.getType() == TokenType.IDENTIFIER) {
						// Field alias (without AS identifier)
						fieldAlias = t.getValue();
						expect(TokenType.IDENTIFIER);
					}
					select.getFields().add(new NumericNode(fieldName, fieldAlias));
				}else if (t.getType() == TokenType.ASTERISK) {
					select.getFields().add(new AsteriskNode());
					expect(TokenType.ASTERISK);
				} else {
					expect(TokenType.IDENTIFIER);

					if (t.getType() == TokenType.IDENTIFIER || t.getType() == TokenType.AS || t.getType() == TokenType.PERIOD) {
						// If it has a Table Name
						if (t.getType() == TokenType.PERIOD) {
							expect(TokenType.PERIOD);
							tableName = fieldName;
							fieldAlias = fieldName;
							fieldName = t.getValue();
							expect(TokenType.IDENTIFIER);
						}
						// Field alias (with AS identifier)
						if (t.getType() == TokenType.AS) {
							expect(TokenType.AS);
							fieldAlias = t.getValue();
							// may be: 	select bebebe as name
							//			select bebebe as "Name"
							//			select bebebe as 'Name'
							expect(TokenType.CHARACTER, TokenType.IDENTIFIER);
						} else if (t.getType() == TokenType.IDENTIFIER) {
							// Field alias (without AS identifier)
							fieldAlias = t.getValue();
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

		if (t.getType() == TokenType.FROM) {
			expect(TokenType.FROM);
			firstField = true;
			do {
				if (t.getType() == TokenType.WHERE) {
					break;
				}
				if (!firstField) {
					expect(TokenType.COMMA, "Missing comma.");
				}
				if (t.getType() == TokenType.IDENTIFIER) {
					final String tableName = t.getValue();
					String tableAlias = tableName;

					if (scanner.hasNext()) {
						expect(TokenType.IDENTIFIER);
						if (t.getType() == TokenType.IDENTIFIER || t.getType() == TokenType.AS) {
							// Field alias (with AS identifier)
							if (t.getType() == TokenType.AS) {
								expect(TokenType.AS);
								tableAlias = t.getValue();
								expect(TokenType.IDENTIFIER);
							} else if (t.getType() == TokenType.IDENTIFIER) {
								// Field alias (without AS identifier)
								tableAlias = t.getValue();
								expect(TokenType.IDENTIFIER);
							}
						}
					}

					final TableNode table = new TableNode(tableName, tableAlias);
					while (scanner.hasNext() && t.getType() != TokenType.COMMA && t.getType() != TokenType.WHERE) {
						final JoinNode join = new JoinNode();

						// Inner join
						if (t.getType() == TokenType.LEFT) {
							join.setType(JoinType.LEFT_JOIN);
							expect(TokenType.LEFT);
						} else if (t.getType() == TokenType.RIGHT) {
							join.setType(JoinType.RIGHT_JOIN);
							expect(TokenType.RIGHT);
						}
						if (t.getType() == TokenType.INNER) {
							expect(TokenType.INNER);
						} else if (t.getType() == TokenType.OUTER) {
							expect(TokenType.OUTER);
						}
						expect(TokenType.JOIN);
						join.setTableName(t.getValue());
						join.setTableAlias(t.getValue());
						expect(TokenType.IDENTIFIER);
						if (t.getType() == TokenType.AS) {
							expect(TokenType.AS);
							join.setTableAlias(t.getValue());
							expect(TokenType.IDENTIFIER);
						} else if (t.getType() != TokenType.ON) {
							join.setTableAlias(t.getValue());
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
				if (t.getType() == TokenType.WHERE) {
					expect(TokenType.WHERE);
					select.setConditions(parseConditionList());
				}
			}
		} else {
			throw new SQLException("FROM expected.", SQLStates.INVALID_SQL);
		}
		return select;
	}

	private ArrayList<SQLNode> parseConditionList() throws SQLException {
		final ArrayList<SQLNode> conditions = new ArrayList<SQLNode>();

		while (scanner.hasNext()) {
			if (t.isConditionBreak()) {
				break;
			}
			conditions.add(parseCondition());
		}
		return conditions;
	}

	private SQLNode parseCondition() throws SQLException {
		if (t.getType() == TokenType.NOT) {
			return new NOTNode(parseCondition());
		} else if (t.isOperator()) {
			switch (t.getType()) {
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
				throw new SQLException("Invalid operator location.", SQLStates.INVALID_SQL);
			}
		} else if (t.getType() == TokenType.LPAREN) {
			new SQLNode(null);
			// group.setChildren(parseConditionList());
			expect(TokenType.RPAREN, "Right parentesis expected");
		} else if (t.getType() == TokenType.EXISTS) {
			expect(TokenType.EXISTS);
			expect(TokenType.LPAREN, "Left parentesis expected.");
			final SelectNode select = parseSelect();
			expect(TokenType.RPAREN, "Left parentesis expected.");
			return new ExistsNode(select);
		} else {
			final FieldNode firstField = parseField();

			switch (t.getType()) {
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
				throw new SQLException("Invalid operator.", SQLStates.INVALID_SQL);
			}
		}
		return null;
	}

	private FieldNode parseField() throws SQLException {
		String tableName = null;
		String fieldName = t.getValue();

		expect(TokenType.IDENTIFIER, TokenType.NUMERIC, TokenType.CHARACTER);

		// If it has a Table Name
		if (scanner.hasNext() && t.getType() == TokenType.PERIOD) {
			expect(TokenType.PERIOD);
			tableName = fieldName;
			fieldName = t.getValue();
			expect(TokenType.IDENTIFIER);
		}
		return new FieldNode(tableName, fieldName, fieldName);
	}

	private void expect(final TokenType rparen, final String message) throws SQLException {
		if (t.getType() != rparen) {
			throw new SQLException(message, SQLStates.INVALID_SQL);
		}
		if (scanner.hasNext()) {
			t = scanner.nextToken();
		} else {
			t = null;
		}
	}

	private void expect(final TokenType ... rparens) throws SQLException {
		boolean found = false;
		for (TokenType rparen : rparens) {
			if (t.getType() == rparen) {
				// Expected do not happen
				found = true;
				break;
			}
		}
		if (!found) {
			throw new SQLException(String.format("Unexpected error in SQL syntax (%s)", t.getValue()), SQLStates.INVALID_SQL);
		}
		if (scanner.hasNext()) {
			t = scanner.nextToken();
		} else {
			t = null;
		}
	}
}
