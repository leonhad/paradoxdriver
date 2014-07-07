package org.paradox.utils.filefilters;

import java.io.File;
import org.junit.Test;
import static org.junit.Assert.*;

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