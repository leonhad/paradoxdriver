package org.paradox.data;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.Before;
import org.junit.Ignore;
import org.paradox.ParadoxConnection;
import org.paradox.metadata.ParadoxField;
import org.paradox.metadata.ParadoxTable;

public class TableDataTest {

    @Before
    public void setUp() throws ClassNotFoundException {
        Class.forName(Driver.class.getName());
    }

    @Test
    public void testLoadDataProduto() throws Exception {
        ParadoxConnection conn = (ParadoxConnection) DriverManager.getConnection("jdbc:paradox:./db");
        ParadoxTable table = TableData.listTables(conn, "Produto.db").get(0);
        ArrayList<ParadoxField> fields = new ArrayList<ParadoxField>();
        fields.add(table.getFields().get(0));
        TableData.loadData(conn, table, fields);
    }

    @Test
    public void testLoadDataEmpresa() throws Exception {
        ParadoxConnection conn = (ParadoxConnection) DriverManager.getConnection("jdbc:paradox:./db");
        ParadoxTable table = TableData.listTables(conn, "Empresa.db").get(0);
        ArrayList<ParadoxField> fields = new ArrayList<ParadoxField>();
        fields.add(table.getFields().get(0));
        TableData.loadData(conn, table, fields);
    }

    @Test
    public void testLoadDataControleCaixa() throws Exception {
        ParadoxConnection conn = (ParadoxConnection) DriverManager.getConnection("jdbc:paradox:./db");
        ParadoxTable table = TableData.listTables(conn, "ControleCaixa.db").get(0);
        ArrayList<ParadoxField> fields = new ArrayList<ParadoxField>();
        fields.add(table.getFields().get(0));
        TableData.loadData(conn, table, fields);
    }

    @Test
    public void testLoadDataWork() throws Exception {
        // FIXME verificar resultado
        ParadoxConnection conn = (ParadoxConnection) DriverManager.getConnection("jdbc:paradox:./db");
        ParadoxTable table = TableData.listTables(conn, "Work.db").get(0);
        ArrayList<ParadoxField> fields = new ArrayList<ParadoxField>();
        fields.add(table.getFields().get(0));
        TableData.loadData(conn, table, fields);
    }
}
