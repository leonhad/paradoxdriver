package org.paradox.utils;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.paradox.utils.Expressions.accept;

/**
 *
 * @author 72330554168
 */
public class ExpressionsTest {

    @Test
    public void testEquals() {
        assertTrue(accept("TABLE", "table", false));
    }

    @Test
    public void testNotEquals() {
        assertFalse(accept("TABLE", "table", true));
    }

    @Test
    public void testMix() {
        assertTrue(accept("TESTE.X02", "%.X??"));
    }

    @Test
    public void testExtra() {
        assertFalse(accept("TESTE.QBE~", "%.QBE"));
    }

    @Test
    public void testUnique() {
        assertTrue(accept("TABLE", "TAB?E"));
    }

    @Test
    public void testLikes() {
        assertTrue(accept("TABLE", "TA%"));
    }
}