package com.googlecode.paradox.utils.filefilters;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link PrimaryKeyFilter}.
 *
 * @author Leonardo Alves da Costa
 * @version 1.1
 * @since 1.0
 */
public class PrimaryKeyFilterTest {

    private final PrimaryKeyFilter filter = new PrimaryKeyFilter();

    /**
     * Test for acceptance.
     */
    @Test
    public void testAccept() {
        final File file = new File("test.px");
        assertTrue("Invalid file filter.", filter.accept(file));
    }
}
