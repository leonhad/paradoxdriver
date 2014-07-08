package org.paradox.parser;

import java.io.IOException;
import java.nio.CharBuffer;
import static java.nio.CharBuffer.wrap;
import org.paradox.utils.SQLStates;
import java.sql.SQLException;
import org.paradox.parser.nodes.FieldNode;
import org.paradox.parser.nodes.SQLNode;
import org.paradox.parser.nodes.SelectNode;
import org.paradox.parser.nodes.TableNode;

public class SQLParser {

    private final String sql;
    private final Scanner scanner;

    public SQLParser(final String sql) {
        this.sql = sql;
        this.scanner = new Scanner(wrap(sql.toUpperCase().toCharArray()));
    }

    public SQLNode parse() throws SQLException, IOException {
        if (scanner.hasNext()) {
            SQLNode tree = null;
            final Token token = scanner.nextToken();

            if (token.getType() == TokenType.SELECT) {
                tree = parseSelect(null);
            } else if (token.getType() == TokenType.INSERT || token.getType() == TokenType.DELETE || token.getType() == TokenType.UPDATE) {
                throw new SQLException("Operação não suportada.", SQLStates.INVALID_SQL);
            } else {
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
                if (!firstField) {
                    if (t.getType() != TokenType.COMMA) {
                        throw new SQLException("Missing comma.", SQLStates.INVALID_SQL);
                    }
                    t = scanner.nextToken();
                }
                select.getFields().add(new FieldNode(select, t.getValue().toUpperCase()));
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
