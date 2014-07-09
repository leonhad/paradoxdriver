package org.paradox.utils.filefilters;

import java.io.File;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author 72330554168
 */
public class TableFilterTest {

    private TableFilter filter = new TableFilter();

    /**
     * Test of accept method, of class TableFilter.
     */
    @Test
    public void testAccept() {
        File file = new File("teste.db");
        assertTrue(filter.accept(file));
    }

}