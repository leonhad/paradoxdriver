package org.paradox.data;

import static java.lang.Class.forName;
import static java.sql.DriverManager.getConnection;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import org.paradox.Driver;
import org.paradox.ParadoxConnection;
import static org.paradox.data.IndexData.listIndexes;
import org.paradox.metadata.ParadoxIndex;

/**
 *
 * @author 72330554168
 */
public class IndexDataTest {

    @Before
    public void setUp() throws ClassNotFoundException {
        forName(Driver.class.getName());
    }

    @Test
    public void testListIndexes() throws Exception {
        ParadoxConnection conn = (ParadoxConnection)getConnection("jdbc:paradox:./db");
        ArrayList<ParadoxIndex> indexes = listIndexes(conn, "CLIENTE.db");
//        for (final ParadoxIndex index : indexes) {
//            System.out.println(index.getName() + "-" + index.getOrder());
//        }
    }

}