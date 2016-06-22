package com.googlecode.paradox.data;

import java.sql.DriverManager;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.integration.MainTest;

/**
 * Test for IndexData
 *
 * @author Leonardo Alves da Costa
 * @since 14/03/2009
 * @version 1.1
 */
public class IndexDataTest {

    private ParadoxConnection conn;

    @BeforeClass
    public static void setUp() throws ClassNotFoundException {
        Class.forName(Driver.class.getName());
    }

    @Before
    public void connect() throws Exception {
        conn = (ParadoxConnection) DriverManager.getConnection(MainTest.CONNECTION_STRING + "db");
    }

    @After
    public void closeConnection() throws Exception {
        if (conn != null) {
            conn.close();
        }
    }

    @Test
    public void testListIndexes() throws Exception {
        IndexData.listIndexes(conn, "CLIENTE.db");
        // ArrayList<ParadoxIndex> indexes = listIndexes(conn, "CLIENTE.db");
        // for (final ParadoxIndex index : indexes) {
        // System.out.println(index.getName() + "-" + index.getOrder());
        // }
    }
}
