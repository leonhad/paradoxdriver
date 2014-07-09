package org.paradox.utils.filefilters;

import java.io.File;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author 72330554168
 */
public class PrimaryKeyFilterTest {

    private PrimaryKeyFilter filter = new PrimaryKeyFilter();

    @Test
    public void testAccept() {
        File file = new File("teste.px");
        assertTrue(filter.accept(file));
    }
}
