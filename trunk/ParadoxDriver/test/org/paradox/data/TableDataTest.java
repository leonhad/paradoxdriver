package org.paradox.data;

import static java.lang.Class.forName;
import java.sql.Driver;
import java.sql.DriverManager;
import static java.sql.DriverManager.getConnection;
import static java.sql.DriverManager.getConnection;
import static java.sql.DriverManager.getConnection;
import static java.sql.DriverManager.getConnection;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.Before;
import org.junit.Ignore;
import org.paradox.ParadoxConnection;
import static org.paradox.data.TableData.listTables;
import static org.paradox.data.TableData.loadData;
import org.paradox.metadata.ParadoxField;
import org.paradox.metadata.ParadoxTable;

public class TableDataTest {

    @Before
    public void setUp() throws ClassNotFoundException {
        forName(Driver.class.getName());
    }

    @Test
    public void testLoadDataProduto() throws Exception {
        ParadoxConnection conn = (ParadoxConnection) getConnection("jdbc:paradox:./db");
        ParadoxTable table = listTables(conn, "Produto.db").get(0);
        ArrayList<ParadoxField> fields = new ArrayList<ParadoxField>();
        fields.add(table.getFields().get(0));
        loadData(conn, table, fields);
    }

    @Test
    public void testLoadDataEmpresa() throws Exception {
        ParadoxConnection conn = (ParadoxConnection) getConnection("jdbc:paradox:./db");
        ParadoxTable table = listTables(conn, "Empresa.db").get(0);
        ArrayList<ParadoxField> fields = new ArrayList<ParadoxField>();
        fields.add(table.getFields().get(0));
        loadData(conn, table, fields);
    }

    @Test
    public void testLoadDataControleCaixa() throws Exception {
        ParadoxConnection conn = (ParadoxConnection) getConnection("jdbc:paradox:./db");
        ParadoxTable table = listTables(conn, "ControleCaixa.db").get(0);
        ArrayList<ParadoxField> fields = new ArrayList<ParadoxField>();
        fields.add(table.getFields().get(0));
        loadData(conn, table, fields);
    }

    @Test
    public void testLoadDataWork() throws Exception {
        // FIXME verificar resultado
        ParadoxConnection conn = (ParadoxConnection) getConnection("jdbc:paradox:./db");
        ParadoxTable table = listTables(conn, "Work.db").get(0);
        ArrayList<ParadoxField> fields = new ArrayList<ParadoxField>();
        fields.add(table.getFields().get(0));
        loadData(conn, table, fields);
    }
}
