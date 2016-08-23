package com.googlecode.paradox.planner.nodes;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link PlanTableNode} class.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.3
 */
public class PlanTableNodeTest {

    @Test
    public void testInstance() {
        PlanTableNode node = new PlanTableNode();
        node.setAlias("alias");
        node.setTable(null);

        Assert.assertEquals("Alias not equals.", "alias", node.getAlias());
        Assert.assertNull("Table not equals.", node.getTable());
    }
}