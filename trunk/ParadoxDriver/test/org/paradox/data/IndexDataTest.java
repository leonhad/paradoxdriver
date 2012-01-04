package org.paradox.data;

import java.sql.DriverManager;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import org.paradox.Driver;
import org.paradox.ParadoxConnection;
import org.paradox.metadata.ParadoxIndex;

/**
 *
 * @author 72330554168
 */
public class IndexDataTest {

    @Before
    public void setUp() throws ClassNotFoundException {
        Class.forName(Driver.class.getName());
    }

    @Test
    public void testListIndexes() throws Exception {
        ParadoxConnection conn = (ParadoxConnection)DriverManager.getConnection("jdbc:paradox:./db");
        ArrayList<ParadoxIndex> indexes = IndexData.listIndexes(conn, "CLIENTE.db");
//        for (final ParadoxIndex index : indexes) {
//            System.out.println(index.getName() + "-" + index.getOrder());
//        }
    }

}