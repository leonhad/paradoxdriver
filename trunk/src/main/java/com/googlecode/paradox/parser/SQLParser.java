package com.googlecode.paradox.parser;

import java.io.IOException;
import java.nio.CharBuffer;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import com.googlecode.paradox.parser.nodes.FieldNode;
import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.parser.nodes.SelectNode;
import com.googlecode.paradox.parser.nodes.TableNode;
import com.googlecode.paradox.utils.SQLStates;

public class SQLParser {

	private final String sql;
	private final Scanner scanner;

	public SQLParser(final String sql) {
		this.sql = sql;
		scanner = new Scanner(CharBuffer.wrap(sql.toCharArray()));
	}

	public SQLNode parse() throws SQLException, IOException {
		if (scanner.hasNext()) {
			SQLNode tree = null;
			final Token token = scanner.nextToken();

			switch (token.getType()) {
			case SELECT:
				tree = parseSelect(null);
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
			return tree;
		}
		throw new SQLException("Invalid SQL: " + sql, SQLStates.INVALID_SQL);
	}

	private SQLNode parseSelect(final SQLNode parent) throws SQLException, IOException {
		final SelectNode select = new SelectNode(parent);
		Token t = null;

		boolean firstField = true;
		while (scanner.hasNext()) {
			t = scanner.nextToken();

			if (t.getType() != TokenType.FROM) {
				String tableName = null;
				String alias = null;

				// Field Name
				if (!firstField) {
					if (t.getType() != TokenType.COMMA) {
						throw new SQLException("Missing comma.", SQLStates.INVALID_SQL);
					}
					t = scanner.nextToken();
				}
				String fieldName = t.getValue();

				t = scanner.nextToken();
				if (t.getType() == TokenType.COMMA || t.getType() == TokenType.FROM) {
					scanner.pushBack(t);
				} else {
					// If it has a Table Name
					if (t.getType() == TokenType.PERIOD) {
						t = scanner.nextToken();
						tableName = fieldName;
						fieldName = t.getValue();
					}
					// Field alias (with AS identifier)
					if (t.getType() == TokenType.AS) {
						t = scanner.nextToken();
						alias = t.getValue();
					} else if (t.getType() == TokenType.IDENTIFIER) {
						// Field alias (without AS identifier)
						t = scanner.nextToken();
						alias = t.getValue();
					}
				}

				select.getFields().add(new FieldNode(select, tableName, fieldName, alias));
				firstField = false;
			} else {
				break;
			}
		}

		if (t.getType() == TokenType.FROM) {
			firstField = true;
			while (scanner.hasNext()) {
				t = scanner.nextToken();

				if (t.getType() != TokenType.WHERE) {
					if (!firstField) {
						if (t.getType() != TokenType.COMMA) {
							throw new SQLException("Missing comma.", SQLStates.INVALID_SQL);
						}
						t = scanner.nextToken();
					}
					select.getTables().add(new TableNode(select, t.getValue().toUpperCase()));
					firstField = false;
				} else if (t.getType() == TokenType.WHERE) {
					break;
				} else if (t != null) {
					throw new SQLException("Invalid SQL.", SQLStates.INVALID_SQL);
				}
			}
		} else {
			throw new SQLException("FROM spected.", SQLStates.INVALID_SQL);
		}
		return select;
	}
}
