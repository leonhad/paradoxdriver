package org.paradox.utils.filefilters;

import java.io.File;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author 72330554168
 */
public class ViewFilterTest {

    private ViewFilter filter = new ViewFilter();

    @Test
    public void testAccept() {
        File file = new File("teste.qbe");
        assertTrue(filter.accept(file));
    }

}