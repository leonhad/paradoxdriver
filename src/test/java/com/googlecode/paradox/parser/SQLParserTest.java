/*
 * SQLParserTest.java
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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.googlecode.paradox.parser.nodes.FieldNode;
import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.parser.nodes.SelectNode;
import com.googlecode.paradox.parser.nodes.StatementNode;
import com.googlecode.paradox.parser.nodes.comparisons.EqualsNode;
import com.googlecode.paradox.parser.nodes.comparisons.NotEqualsNode;
import com.googlecode.paradox.parser.nodes.conditional.ANDNode;
import com.googlecode.paradox.parser.nodes.values.CharacterNode;
import com.googlecode.paradox.parser.nodes.values.NumericNode;

/**
 * Unit test for {@link SQLParser}.
 * 
 * @author Leonardo Alves da Costa
 * @since 1.0
 * @version 1.1
 */
public class SQLParserTest {

    /**
     * Test for column values.
     * 
     * @throws Exception
     *             in case of failures.
     */
    @Test
    public void testColumnValue() throws Exception {
        final SQLParser parser = new SQLParser("SELECT 'test', 123 as number FROM client");
        final List<StatementNode> list = parser.parse();
        final SQLNode tree = list.get(0);

        Assert.assertTrue(tree instanceof SelectNode);

        final SelectNode select = (SelectNode) tree;

        Assert.assertEquals(2, select.getFields().size());
        Assert.assertTrue(select.getFields().get(0) instanceof CharacterNode);
        Assert.assertEquals("TEST", select.getFields().get(0).getName());
        Assert.assertTrue(select.getFields().get(1) instanceof NumericNode);
        Assert.assertEquals("123", select.getFields().get(1).getName());
        Assert.assertEquals("number", select.getFields().get(1).getAlias());

        Assert.assertEquals(1, select.getTables().size());
        Assert.assertEquals("client", select.getTables().get(0).getName());
    }

    /**
     * Test for join token.
     * 
     * @throws Exception
     *             in case of failures.
     */
    @Test
    public void testJoin() throws Exception {
        final SQLParser parser = new SQLParser("SELECT * FROM client c inner join test t on test_id = id and a <> b left join table on a = b");
        final List<StatementNode> list = parser.parse();
        final SQLNode tree = list.get(0);

        Assert.assertTrue(tree instanceof SelectNode);

        final SelectNode select = (SelectNode) tree;

        Assert.assertEquals(1, select.getFields().size());
        Assert.assertEquals(TokenType.ASTERISK.name(), select.getFields().get(0).getName());

        Assert.assertEquals(1, select.getTables().size());
        Assert.assertEquals("client", select.getTables().get(0).getName());
    }

    /**
     * Test for SELECT token.
     * 
     * @throws Exception
     *             in case of failures.
     */
    @Test
    public void testSelect() throws Exception {
        final SQLParser parser = new SQLParser("SELECT * FROM client");
        final List<StatementNode> list = parser.parse();
        final SQLNode tree = list.get(0);

        Assert.assertTrue(tree instanceof SelectNode);

        final SelectNode select = (SelectNode) tree;

        Assert.assertEquals(1, select.getFields().size());
        Assert.assertEquals(TokenType.ASTERISK.name(), select.getFields().get(0).getName());

        Assert.assertEquals(1, select.getTables().size());
        Assert.assertEquals("client", select.getTables().get(0).getName());
    }

    /**
     * Test for tables.
     * 
     * @throws Exception
     *             in case of failures.
     */
    @Test
    public void testTable() throws Exception {
        final SQLParser parser = new SQLParser("SELECT * FROM \"client.db\"");
        final List<StatementNode> list = parser.parse();
        final SQLNode tree = list.get(0);

        Assert.assertTrue(tree instanceof SelectNode);

        final SelectNode select = (SelectNode) tree;

        Assert.assertEquals(1, select.getFields().size());
        Assert.assertEquals(TokenType.ASTERISK.name(), select.getFields().get(0).getName());

        Assert.assertEquals(1, select.getTables().size());
        Assert.assertEquals("client", select.getTables().get(0).getName());
    }

    /**
     * Test a SELECT with two tables.
     * 
     * @throws Exception
     *             in case of failures.
     */
    @Test
    public void testTwoTable() throws Exception {
        final SQLParser parser = new SQLParser("select CODIGO as código, estado.NOME nome FROM cliente, estado");
        final List<StatementNode> list = parser.parse();
        final SQLNode tree = list.get(0);

        Assert.assertTrue(tree instanceof SelectNode);

        final SelectNode select = (SelectNode) tree;

        Assert.assertEquals(2, select.getFields().size());
        Assert.assertEquals("CODIGO", select.getFields().get(0).getName());
        Assert.assertEquals("código", ((FieldNode) select.getFields().get(0)).getAlias());

        Assert.assertEquals("estado", ((FieldNode) select.getFields().get(1)).getTableName());
        Assert.assertEquals("NOME", select.getFields().get(1).getName());
        Assert.assertEquals("nome", ((FieldNode) select.getFields().get(1)).getAlias());

        Assert.assertEquals(2, select.getTables().size());
        Assert.assertEquals("cliente", select.getTables().get(0).getName());
        Assert.assertEquals("estado", select.getTables().get(1).getName());
    }

    /**
     * Test tables with alias.
     * 
     * @throws Exception
     *             in case of failures.
     */
    @Test
    public void testTwoTableWithAlias() throws Exception {
        final SQLParser parser = new SQLParser("select *, name FROM client as cli, state STATE");
        final List<StatementNode> list = parser.parse();
        final SQLNode tree = list.get(0);

        Assert.assertTrue(tree instanceof SelectNode);

        final SelectNode select = (SelectNode) tree;

        Assert.assertEquals(2, select.getFields().size());
        Assert.assertEquals(2, select.getTables().size());

        Assert.assertEquals(TokenType.ASTERISK.name(), select.getFields().get(0).getName());

        Assert.assertEquals("client", select.getTables().get(0).getName());
        Assert.assertEquals("cli", select.getTables().get(0).getAlias());
        Assert.assertEquals("state", select.getTables().get(1).getName());
        Assert.assertEquals("STATE", select.getTables().get(1).getAlias());
    }

    /**
     * Test for where token.
     * 
     * @throws Exception
     *             in case of failures.
     */
    @Test
    public void testWhere() throws Exception {
        final SQLParser parser = new SQLParser("SELECT * FROM client as test WHERE a = b and c <> t");
        final List<StatementNode> list = parser.parse();
        final SQLNode tree = list.get(0);

        Assert.assertTrue(tree instanceof SelectNode);

        final SelectNode select = (SelectNode) tree;

        Assert.assertEquals(1, select.getFields().size());
        Assert.assertEquals(TokenType.ASTERISK.name(), select.getFields().get(0).getName());

        Assert.assertEquals(1, select.getTables().size());
        Assert.assertEquals("client", select.getTables().get(0).getName());
        Assert.assertEquals("test", select.getTables().get(0).getAlias());

        Assert.assertEquals(3, select.getConditions().size());
        Assert.assertTrue(select.getConditions().get(0) instanceof EqualsNode);
        Assert.assertTrue(select.getConditions().get(1) instanceof ANDNode);
        Assert.assertTrue(select.getConditions().get(2) instanceof NotEqualsNode);
        Assert.assertEquals("a", ((EqualsNode) select.getConditions().get(0)).getFirst().getName());
        Assert.assertEquals("b", ((EqualsNode) select.getConditions().get(0)).getLast().getName());
        Assert.assertEquals("c", ((NotEqualsNode) select.getConditions().get(2)).getFirst().getName());
        Assert.assertEquals("t", ((NotEqualsNode) select.getConditions().get(2)).getLast().getName());
    }

    /**
     * Test a where with alias.
     * 
     * @throws Exception
     *             in case of failures.
     */
    @Test
    public void testWhereWithAlias() throws Exception {
        final SQLParser parser = new SQLParser("SELECT * FROM client as test WHERE test.a = c.b");
        final List<StatementNode> list = parser.parse();
        final SQLNode tree = list.get(0);

        Assert.assertTrue(tree instanceof SelectNode);

        final SelectNode select = (SelectNode) tree;

        Assert.assertEquals(1, select.getFields().size());
        Assert.assertEquals(TokenType.ASTERISK.name(), select.getFields().get(0).getName());

        Assert.assertEquals(1, select.getTables().size());
        Assert.assertEquals("client", select.getTables().get(0).getName());
        Assert.assertEquals("test", select.getTables().get(0).getAlias());

        Assert.assertEquals(1, select.getConditions().size());
        Assert.assertTrue(select.getConditions().get(0) instanceof EqualsNode);
        Assert.assertEquals("test", ((EqualsNode) select.getConditions().get(0)).getFirst().getTableName());
        Assert.assertEquals("a", ((EqualsNode) select.getConditions().get(0)).getFirst().getName());
        Assert.assertEquals("c", ((EqualsNode) select.getConditions().get(0)).getLast().getTableName());
        Assert.assertEquals("b", ((EqualsNode) select.getConditions().get(0)).getLast().getName());
    }
}
