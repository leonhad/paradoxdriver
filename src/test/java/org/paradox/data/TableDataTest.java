package org.paradox.data;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.paradox.ParadoxConnection;
import org.paradox.data.table.value.AbstractFieldValue;
import org.paradox.metadata.ParadoxField;
import org.paradox.metadata.ParadoxTable;
import org.paradox.test.MainTest;

public class TableDataTest {

	private ParadoxConnection conn;

	@BeforeClass
	public static void setUp() throws Exception {
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
	public void testLoadAreaCodes() throws Exception {
		final ParadoxTable table = TableData.listTables(conn, "areacodes.db").get(0);
		final ArrayList<ArrayList<AbstractFieldValue>> data = TableData.loadData(conn, table, table.getFields());
		Assert.assertEquals(table.getRowCount(), data.size());
	}

	@Test
	public void testLoadContacts() throws Exception {
		final ParadoxTable table = TableData.listTables(conn, "contacts.db").get(0);
		final ArrayList<ParadoxField> fields = new ArrayList<ParadoxField>();
		fields.add(table.getFields().get(0));
		TableData.loadData(conn, table, fields);
	}

	@Test
	public void testLoadCustomer() throws Exception {
		final ParadoxTable table = TableData.listTables(conn, "customer.db").get(0);
		final ArrayList<ParadoxField> fields = new ArrayList<ParadoxField>();
		fields.add(table.getFields().get(0));
		TableData.loadData(conn, table, fields);
	}

	@Test
	public void testLoadHercules() throws Exception {
		final ParadoxTable table = TableData.listTables(conn, "hercules.db").get(0);
		TableData.loadData(conn, table, table.getFields());
	}

	@Test
	public void testLoadOrders() throws Exception {
		final ParadoxTable table = TableData.listTables(conn, "orders.db").get(0);
		final ArrayList<ParadoxField> fields = new ArrayList<ParadoxField>();
		fields.add(table.getFields().get(0));
		TableData.loadData(conn, table, fields);
	}

	@Test
	public void testLoadServer() throws Exception {
		final ParadoxTable table = TableData.listTables(conn, "server.db").get(0);
		final ArrayList<ParadoxField> fields = new ArrayList<ParadoxField>();
		fields.add(table.getFields().get(0));
		TableData.loadData(conn, table, fields);
	}
}
