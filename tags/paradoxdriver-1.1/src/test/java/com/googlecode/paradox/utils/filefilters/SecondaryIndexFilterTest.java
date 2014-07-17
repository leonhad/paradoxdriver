package com.googlecode.paradox.utils.filefilters;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

/**
 *
 * @author 72330554168
 */
public class SecondaryIndexFilterTest {

    private SecondaryIndexFilter filter = new SecondaryIndexFilter();

    @Test
    public void testAccept() {
        File file = new File("teste.xn2");
        assertTrue(filter.accept(file));
    }

}