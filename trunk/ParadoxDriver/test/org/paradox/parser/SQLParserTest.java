package org.paradox.parser;

import junit.framework.Assert;
import org.junit.Test;
import org.paradox.parser.nodes.FieldNode;
import org.paradox.parser.nodes.SQLNode;
import org.paradox.parser.nodes.SelectNode;
import org.paradox.parser.nodes.TableNode;

public class SQLParserTest {

    @Test
    public void testOne() throws Exception {
        SQLParser parser = new SQLParser("SELECT * FROM cliente.db");
        SQLNode tree = parser.parse();

        Assert.assertTrue(tree instanceof SelectNode);

        SelectNode select = (SelectNode) tree;

        Assert.assertEquals(1, select.getFields().size());
        Assert.assertEquals("*", ((FieldNode) select.getFields().get(0)).getName());

        Assert.assertEquals(1, select.getTables().size());
        Assert.assertEquals("CLIENTE.DB", ((TableNode) select.getTables().get(0)).getName());
    }

    @Test
    public void testTwo() throws Exception {
        SQLParser parser = new SQLParser("SELECT CODIGO, NOME FROM cliente, estado");
        SQLNode tree = parser.parse();

        Assert.assertTrue(tree instanceof SelectNode);

        SelectNode select = (SelectNode) tree;

        Assert.assertEquals(2, select.getFields().size());
        Assert.assertEquals("CODIGO", ((FieldNode) select.getFields().get(0)).getName());
        Assert.assertEquals("NOME", ((FieldNode) select.getFields().get(1)).getName());

        Assert.assertEquals(2, select.getTables().size());
        Assert.assertEquals("CLIENTE", ((TableNode) select.getTables().get(0)).getName());
        Assert.assertEquals(2, select.getTables().size());
        Assert.assertEquals("ESTADO", ((TableNode) select.getTables().get(1)).getName());
    }

    @Test
    public void testTree() throws Exception {
        SQLParser parser = new SQLParser("SELECT * FROM \"cliente.db\"");
        SQLNode tree = parser.parse();

        Assert.assertTrue(tree instanceof SelectNode);

        SelectNode select = (SelectNode) tree;

        Assert.assertEquals(1, select.getFields().size());
        Assert.assertEquals("*", ((FieldNode) select.getFields().get(0)).getName());

        Assert.assertEquals(1, select.getTables().size());
        Assert.assertEquals("CLIENTE.DB", ((TableNode) select.getTables().get(0)).getName());
    }
}
