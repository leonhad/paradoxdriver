package com.googlecode.paradox.utils.filefilters;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

/**
 * Used to test secondary index filter.
 *
 * @author Leonardo Alves da Costa
 * @version 1.1
 * @since 22/07/2014
 */
public class SecondaryIndexFilterTest {

    private final SecondaryIndexFilter filter = new SecondaryIndexFilter();

    @Test
    public void testAccept() {
        final File file = new File("teste.xn2");
        Assert.assertTrue(filter.accept(file));
    }

}