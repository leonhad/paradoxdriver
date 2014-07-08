package org.paradox.data;

import static java.lang.Class.forName;
import java.sql.Driver;
import static java.sql.DriverManager.getConnection;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.paradox.ParadoxConnection;
import static org.paradox.data.TableData.listTables;
import static org.paradox.data.TableData.loadData;
import org.paradox.data.table.value.AbstractFieldValue;
import org.paradox.metadata.ParadoxField;
import org.paradox.metadata.ParadoxTable;

public class TableDataTest {

    @Before
    public void setUp() throws ClassNotFoundException {
        forName(Driver.class.getName());
    }

    @Test
    public void testLoadAreaCodes() throws Exception {
        ParadoxConnection conn = (ParadoxConnection) getConnection("jdbc:paradox:./db");
        ParadoxTable table = listTables(conn, "areacodes.db").get(0);
        ArrayList<ArrayList<AbstractFieldValue>> data = loadData(conn, table, table.getFields());
        Assert.assertEquals(table.getRowCount(), data.size());
    }

    @Test
    public void testLoadContacts() throws Exception {
        ParadoxConnection conn = (ParadoxConnection) getConnection("jdbc:paradox:./db");
        ParadoxTable table = listTables(conn, "contacts.db").get(0);
        ArrayList<ParadoxField> fields = new ArrayList<ParadoxField>();
        fields.add(table.getFields().get(0));
        loadData(conn, table, fields);
    }

    @Test
    public void testLoadCustomer() throws Exception {
        ParadoxConnection conn = (ParadoxConnection) getConnection("jdbc:paradox:./db");
        ParadoxTable table = listTables(conn, "customer.db").get(0);
        ArrayList<ParadoxField> fields = new ArrayList<ParadoxField>();
        fields.add(table.getFields().get(0));
        loadData(conn, table, fields);
    }

    @Test
    public void testLoadHercules() throws Exception {
        ParadoxConnection conn = (ParadoxConnection) getConnection("jdbc:paradox:./db");
        ParadoxTable table = listTables(conn, "hercules.db").get(0);
        loadData(conn, table, table.getFields());
    }
    
    @Test
    public void testLoadOrders() throws Exception {
        ParadoxConnection conn = (ParadoxConnection) getConnection("jdbc:paradox:./db");
        ParadoxTable table = listTables(conn, "orders.db").get(0);
        ArrayList<ParadoxField> fields = new ArrayList<ParadoxField>();
        fields.add(table.getFields().get(0));
        loadData(conn, table, fields);
    }
    
    @Test
    public void testLoadServer() throws Exception {
        ParadoxConnection conn = (ParadoxConnection) getConnection("jdbc:paradox:./db");
        ParadoxTable table = listTables(conn, "server.db").get(0);
        ArrayList<ParadoxField> fields = new ArrayList<ParadoxField>();
        fields.add(table.getFields().get(0));
        loadData(conn, table, fields);
    }
}
