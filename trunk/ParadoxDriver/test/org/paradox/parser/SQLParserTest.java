package org.paradox.parser;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import org.junit.Test;
import org.paradox.parser.nodes.SQLNode;
import org.paradox.parser.nodes.SelectNode;

public class SQLParserTest {

    @Test
    public void testOne() throws Exception {
        SQLParser parser = new SQLParser("SELECT * FROM cliente.db");
        SQLNode tree = parser.parse();

        assertTrue(tree instanceof SelectNode);

        SelectNode select = (SelectNode) tree;

        assertEquals(1, select.getFields().size());
        assertEquals("*", select.getFields().get(0).getName());

        assertEquals(1, select.getTables().size());
        assertEquals("CLIENTE.DB", select.getTables().get(0).getName());
    }

    @Test
    public void testTwo() throws Exception {
        SQLParser parser = new SQLParser("SELECT CODIGO, NOME FROM cliente, estado");
        SQLNode tree = parser.parse();

        assertTrue(tree instanceof SelectNode);

        SelectNode select = (SelectNode) tree;

        assertEquals(2, select.getFields().size());
        assertEquals("CODIGO", select.getFields().get(0).getName());
        assertEquals("NOME", select.getFields().get(1).getName());

        assertEquals(2, select.getTables().size());
        assertEquals("CLIENTE", select.getTables().get(0).getName());
        assertEquals(2, select.getTables().size());
        assertEquals("ESTADO", select.getTables().get(1).getName());
    }

    @Test
    public void testTree() throws Exception {
        SQLParser parser = new SQLParser("SELECT * FROM \"cliente.db\"");
        SQLNode tree = parser.parse();

        assertTrue(tree instanceof SelectNode);

        SelectNode select = (SelectNode) tree;

        assertEquals(1, select.getFields().size());
        assertEquals("*", select.getFields().get(0).getName());

        assertEquals(1, select.getTables().size());
        assertEquals("CLIENTE.DB", select.getTables().get(0).getName());
    }
}
