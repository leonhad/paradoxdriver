package com.googlecode.paradox.parser;

import java.io.IOException;
import java.nio.CharBuffer;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;

import com.googlecode.paradox.parser.nodes.ANDNode;
import com.googlecode.paradox.parser.nodes.FieldNode;
import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.parser.nodes.SelectNode;
import com.googlecode.paradox.parser.nodes.StatementNode;
import com.googlecode.paradox.parser.nodes.TableNode;
import com.googlecode.paradox.utils.SQLStates;

public class SQLParser {

	private final String sql;
	private final Scanner scanner;

	public SQLParser(final String sql) {
		this.sql = sql;
		scanner = new Scanner(CharBuffer.wrap(sql.toCharArray()));
	}

	public ArrayList<StatementNode> parse() throws SQLException, IOException {
		final ArrayList<StatementNode> statementList = new ArrayList<StatementNode>();
		while (scanner.hasNext()) {
			final Token token = scanner.nextToken();

			switch (token.getType()) {
			case SEMI:
				if (statementList.size() == 0) {
					throw new SQLException("Unespected semicolon");
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
	private SelectNode parseSelect() throws SQLException, IOException {
		final SelectNode select = new SelectNode();
		Token t = scanner.nextToken();

		// Allowed only in the beginning of Select Statement
		if (t.getType() == TokenType.DISTINCT) {
			select.setDistinct(true);
		} else {
			scanner.pushBack(t);
		}

		// Field loop
		boolean firstField = true;
		while (scanner.hasNext()) {
			t = scanner.nextToken();

			if (t.getType() == TokenType.DISTINCT) {
				throw new SQLException("Invalid statement.");
			}

			if (t.getType() != TokenType.FROM) {
				// Field Name
				if (!firstField) {
					expect(t, TokenType.COMMA, "Missing comma.");
					t = scanner.nextToken();
				}
				String tableName = null;
				String fieldName = t.getValue();
				String fieldAlias = fieldName;

				t = scanner.nextToken();
				if (t.getType() != TokenType.IDENTIFIER && t.getType() != TokenType.AS && t.getType() != TokenType.PERIOD) {
					scanner.pushBack(t);
				} else {
					// If it has a Table Name
					if (t.getType() == TokenType.PERIOD) {
						t = scanner.nextToken();
						tableName = fieldName;
						fieldName = t.getValue();
						fieldAlias = fieldName;
						t = scanner.nextToken();
					}
					// Field alias (with AS identifier)
					if (t.getType() == TokenType.AS) {
						t = scanner.nextToken();
						fieldAlias = t.getValue();
					} else if (t.getType() == TokenType.IDENTIFIER) {
						// Field alias (without AS identifier)
						fieldAlias = t.getValue();
					}
				}

				select.getFields().add(new FieldNode(tableName, fieldName, fieldAlias));
				firstField = false;
			} else {
				break;
			}
		}

		if (t.getType() == TokenType.FROM) {
			firstField = true;
			while (scanner.hasNext()) {
				t = scanner.nextToken();

				if (t.getType() == TokenType.WHERE) {
					break;
				}
				if (!firstField) {
					expect(t, TokenType.COMMA, "Missing comma.");
					t = scanner.nextToken();
				}
				if (t.getType() == TokenType.IDENTIFIER) {
					final String tableName = t.getValue();
					String tableAlias = tableName;

					if (scanner.hasNext()) {
						t = scanner.nextToken();
						if (t.getType() != TokenType.IDENTIFIER && t.getType() != TokenType.AS) {
							scanner.pushBack(t);
						} else {
							// Field alias (with AS identifier)
							if (t.getType() == TokenType.AS) {
								t = scanner.nextToken();
								tableAlias = t.getValue();
							} else if (t.getType() == TokenType.IDENTIFIER) {
								// Field alias (without AS identifier)
								tableAlias = t.getValue();
							}
						}
					}
					select.getTables().add(new TableNode(tableName, tableAlias));
					firstField = false;
				}
			}

			if (t.getType() == TokenType.WHERE) {
				select.setConditions(parseConditionList());
			}
		} else {
			throw new SQLException("FROM expected.", SQLStates.INVALID_SQL);
		}
		return select;
	}

	private ArrayList<SQLNode> parseConditionList() throws IOException, SQLException {
		final ArrayList<SQLNode> conditions = new ArrayList<SQLNode>();
		boolean first = true;

		while (scanner.hasNext()) {
			final Token t = scanner.nextToken();
			scanner.pushBack(t);
			if (t.getType() == TokenType.ORDER || t.getType() == TokenType.HAVING || t.getType() == TokenType.RPAREN) {
				break;
			}
			conditions.add(parseCondition(first));
			first = false;
		}
		return conditions;
	}

	private SQLNode parseCondition(final boolean first) throws IOException, SQLException {
		final Token t = scanner.nextToken();

		if (isOperator(t)) {
			if (first) {
				throw new SQLException("Invalid start operator.", SQLStates.INVALID_SQL);
			}
			switch (t.getType()) {
			case AND:
				return new ANDNode(parseCondition(true));
			case OR:
				break;

			case XOR:

			default:
				throw new SQLException("Invalid operator location.", SQLStates.INVALID_SQL);
			}
		} else if (t.getType() == TokenType.LPAREN) {
			final SQLNode group = new SQLNode(null);
			group.setChildren(parseConditionList());
			expect(TokenType.RPAREN, "Right parentesis expected");
		}

		return null;
	}

	private void expect(final TokenType rparen, final String message) throws IOException, SQLException {
		final Token t = scanner.nextToken();
		if (t.getType() != rparen) {
			throw new SQLException(message, SQLStates.INVALID_SQL);
		}
	}

	private void expect(final Token t, final TokenType rparen, final String message) throws IOException, SQLException {
		if (t.getType() != rparen) {
			throw new SQLException(message, SQLStates.INVALID_SQL);
		}
	}

	private boolean isOperator(final Token t) {
		return t.getType() == TokenType.AND || t.getType() == TokenType.OR || t.getType() == TokenType.XOR;
	}
}
