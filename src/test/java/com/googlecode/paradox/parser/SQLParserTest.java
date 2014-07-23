package com.googlecode.paradox.parser;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.parser.nodes.SelectNode;
import com.googlecode.paradox.parser.nodes.StatementNode;

public class SQLParserTest {

	@Test
	public void testOne() throws Exception {
		final SQLParser parser = new SQLParser("SELECT * FROM \"client.db\"");
		final ArrayList<StatementNode> list = parser.parse();
		final SQLNode tree = list.get(0);

		Assert.assertTrue(tree instanceof SelectNode);

		final SelectNode select = (SelectNode) tree;

		Assert.assertEquals(1, select.getFields().size());
		Assert.assertEquals("*", select.getFields().get(0).getName());

		Assert.assertEquals(1, select.getTables().size());
		Assert.assertEquals("client.db", select.getTables().get(0).getName());
	}

	@Test
	public void testTwoTable() throws Exception {
		final SQLParser parser = new SQLParser("select CODIGO as código, estado.NOME nome FROM cliente, estado");
		final ArrayList<StatementNode> list = parser.parse();
		final SQLNode tree = list.get(0);

		Assert.assertTrue(tree instanceof SelectNode);

		final SelectNode select = (SelectNode) tree;

		Assert.assertEquals(2, select.getFields().size());
		Assert.assertEquals("CODIGO", select.getFields().get(0).getName());
		Assert.assertEquals("código", select.getFields().get(0).getAlias());

		Assert.assertEquals("estado", select.getFields().get(1).getTableName());
		Assert.assertEquals("NOME", select.getFields().get(1).getName());
		Assert.assertEquals("nome", select.getFields().get(1).getAlias());

		Assert.assertEquals(2, select.getTables().size());
		Assert.assertEquals("cliente", select.getTables().get(0).getName());
		Assert.assertEquals("estado", select.getTables().get(1).getName());
	}

	@Test
	public void testTwoTableWithAlias() throws Exception {
		final SQLParser parser = new SQLParser("select * name FROM client as cli, state STATE");
		final ArrayList<StatementNode> list = parser.parse();
		final SQLNode tree = list.get(0);

		Assert.assertTrue(tree instanceof SelectNode);

		final SelectNode select = (SelectNode) tree;

		Assert.assertEquals(1, select.getFields().size());
		Assert.assertEquals(2, select.getTables().size());

		Assert.assertEquals("*", select.getFields().get(0).getName());

		Assert.assertEquals("client", select.getTables().get(0).getName());
		Assert.assertEquals("cli", select.getTables().get(0).getAlias());
		Assert.assertEquals("state", select.getTables().get(1).getName());
		Assert.assertEquals("STATE", select.getTables().get(1).getAlias());
	}

	@Test
	public void testTree() throws Exception {
		final SQLParser parser = new SQLParser("SELECT * FROM \"client.db\"");
		final ArrayList<StatementNode> list = parser.parse();
		final SQLNode tree = list.get(0);

		Assert.assertTrue(tree instanceof SelectNode);

		final SelectNode select = (SelectNode) tree;

		Assert.assertEquals(1, select.getFields().size());
		Assert.assertEquals("*", select.getFields().get(0).getName());

		Assert.assertEquals(1, select.getTables().size());
		Assert.assertEquals("client.db", select.getTables().get(0).getName());
	}
}
