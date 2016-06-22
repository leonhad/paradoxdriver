package com.googlecode.paradox.utils.filefilters;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

/**
 *
 * @author 72330554168
 */
public class TableFilterTest {

    private final TableFilter filter = new TableFilter();

    /**
     * Test of accept method, of class TableFilter.
     */
    @Test
    public void testAccept() {
        final File file = new File("teste.db");
        assertTrue(filter.accept(file));
    }

}