package com.googlecode.paradox.test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;

/**
 * Generic tests for Paradox Driver
 *
 * @author Leonardo Alves da Costa
 * @since 14/3/2009
 */
@RunWith(JUnit4.class)
public class MainTest {

	public static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/";
	private Connection conn;

	@BeforeClass
	public static void setUp() throws Exception {
		Class.forName(Driver.class.getName());
	}

	@Before
	public void connect() throws Exception {
		conn = DriverManager.getConnection(MainTest.CONNECTION_STRING + "db");
	}

	@After
	public void closeConnection() throws Exception {
		if (conn != null) {
			conn.close();
		}
	}

	@Test
	public void testWrap() throws Exception {
		Assert.assertTrue(conn.isWrapperFor(ParadoxConnection.class));
		conn.unwrap(ParadoxConnection.class);
	}

	@Test
	public void testCatalog() throws Exception {
		ResultSet rs = null;

		final DatabaseMetaData meta = conn.getMetaData();
		try {
			rs = meta.getCatalogs();
			if (rs.next()) {
				Assert.assertEquals("db", rs.getString("TABLE_CAT"));
			} else {
				Assert.fail("No catalog selected.");
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
		}
	}

	@Test
	public void testPrimaryKey() throws Exception {
		ResultSet rs = null;

		try {
			final DatabaseMetaData meta = conn.getMetaData();

			rs = meta.getPrimaryKeys("db", "APP", "CUSTOMER.db");
			Assert.assertTrue(rs.next());
			Assert.assertEquals("db", rs.getString("TABLE_CAT"));
			Assert.assertEquals("APP", rs.getString("TABLE_SCHEM"));
			Assert.assertEquals("CUSTOMER.DB", rs.getString("TABLE_NAME"));
			Assert.assertEquals("CustNo", rs.getString("COLUMN_NAME"));
			Assert.assertEquals("0", rs.getString("KEY_SEQ"));
			Assert.assertEquals("CustNo", rs.getString("PK_NAME"));
			Assert.assertFalse(rs.next());
		} finally {
			if (rs != null) {
				rs.close();
			}
		}
	}

	@Test
	public void testIndexInfo() throws Exception {
		ResultSet rs = null;

		try {
			final DatabaseMetaData meta = conn.getMetaData();

			rs = meta.getIndexInfo("db", "APP", "customer.db", true, true);
			// assertTrue(rs.next());
			// assertEquals("CUSTOMER.PX", rs.getString("INDEX_NAME"));
			// assertTrue(rs.next());
			// assertEquals("CUSTOMER.X06", rs.getString("INDEX_NAME"));
			// assertFalse(rs.next());

			while (rs.next()) {
				// System.out.println("TABLE_CAT: " + rs.getString("TABLE_CAT"));
				// System.out.println("TABLE_SCHEM: " + rs.getString("TABLE_SCHEM"));
				// System.out.println("TABLE_NAME: " + rs.getString("TABLE_NAME"));
				// System.out.println("NON_UNIQUE: " + rs.getString("NON_UNIQUE"));
				// System.out.println("INDEX_QUALIFIER: " + rs.getString("INDEX_QUALIFIER"));
				// System.out.println("INDEX_NAME: " + rs.getString("INDEX_NAME"));
				// System.out.println("TYPE: " + rs.getString("TYPE"));
				// System.out.println("ORDINAL_POSITION: " + rs.getString("ORDINAL_POSITION"));
				// System.out.println("COLUMN_NAME: " + rs.getString("COLUMN_NAME"));
				// System.out.println("ASC_OR_DESC: " + rs.getString("ASC_OR_DESC"));
				// System.out.println("CARDINALITY: " + rs.getString("CARDINALITY"));
				// System.out.println("PAGES: " + rs.getString("PAGES"));
				// System.out.println("FILTER_CONDITION: " + rs.getString("FILTER_CONDITION"));
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
		}
	}

	@Test
	public void testViewColumns() throws Exception {
		ResultSet rs = null;

		try {
			final DatabaseMetaData meta = conn.getMetaData();

			rs = meta.getColumns("db", "APP", "CONTASAPAGAR.QBE", "%");
			while (rs.next()) {
				/*
				 * System.out.println("TABLE_CAT: " + rs.getString("TABLE_CAT")); System.out.println("TABLE_SCHEM: " + rs.getString("TABLE_SCHEM")); System.out.println("TABLE_NAME: " +
				 * rs.getString("TABLE_NAME")); System.out.println("NON_UNIQUE: " + rs.getString("NON_UNIQUE")); System.out.println("INDEX_QUALIFIER: " + rs.getString("INDEX_QUALIFIER"));
				 * System.out.println("INDEX_NAME: " + rs.getString("INDEX_NAME")); System.out.println("TYPE: " + rs.getString("TYPE")); System.out.println("ORDINAL_POSITION: " +
				 * rs.getString("ORDINAL_POSITION")); System.out.println("COLUMN_NAME: " + rs.getString("COLUMN_NAME")); System.out.println("ASC_OR_DESC: " + rs.getString("ASC_OR_DESC"));
				 * System.out.println("CARDINALITY: " + rs.getString("CARDINALITY")); System.out.println("PAGES: " + rs.getString("PAGES")); System.out.println("FILTER_CONDITION: " +
				 * rs.getString("FILTER_CONDITION"));
				 */
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
		}
	}

	@Test
	public void testResultSet() throws Exception {
		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.createStatement();

			rs = stmt.executeQuery("SELECT AC, State, CITIES FROM \"AREACODES.DB\"");

			while (rs.next()) {
				rs.getString("ac");
				rs.getString("State");
				rs.getString("Cities");
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
	}
}
