package org.paradox.utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author 72330554168
 */
public class ExpressionsTest {

    @Test
    public void testEquals() {
        assertTrue(Expressions.accept("TABLE", "table", false));
    }

    @Test
    public void testNotEquals() {
        assertFalse(Expressions.accept("TABLE", "table", true));
    }

    @Test
    public void testMix() {
        assertTrue(Expressions.accept("TESTE.X02", "%.X??"));
    }

    @Test
    public void testExtra() {
        assertFalse(Expressions.accept("TESTE.QBE~", "%.QBE"));
    }

    @Test
    public void testUnique() {
        assertTrue(Expressions.accept("TABLE", "TAB?E"));
    }

    @Test
    public void testLikes() {
        assertTrue(Expressions.accept("TABLE", "TA%"));
    }
}