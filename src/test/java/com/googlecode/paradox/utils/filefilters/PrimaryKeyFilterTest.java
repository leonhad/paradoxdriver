package com.googlecode.paradox.utils.filefilters;

import java.io.File;
import org.junit.Assert;
import org.junit.Test;

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
        Assert.assertTrue("Invalid file filter.", this.filter.accept(file));
    }
}
