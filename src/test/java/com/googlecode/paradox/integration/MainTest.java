package com.googlecode.paradox.integration;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;

/**
 * Generic tests for Paradox Driver
 *
 * @author Leonardo Alves da Costa
 * @since 14/3/2009
 */
@Category(IntegrationTest.class)
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
            Assert.assertEquals("CUSTOMER", rs.getString("TABLE_NAME"));
            Assert.assertEquals("CustNo", rs.getString("COLUMN_NAME"));
            Assert.assertEquals("0", rs.getString("KEY_SEQ"));
            Assert.assertEquals("CustNo", rs.getString("PK_NAME"));
            // Assert.assertFalse(rs.next());
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
            final String[] names = new String[2];
            final DatabaseMetaData meta = conn.getMetaData();

            rs = meta.getIndexInfo("db", "APP", "customer.db", true, true);
            Assert.assertTrue(rs.next());
            names[0] = rs.getString("INDEX_NAME");
            Assert.assertTrue(rs.next());
            names[1] = rs.getString("INDEX_NAME");
            Assert.assertTrue(rs.next());

            Arrays.sort(names);
            Assert.assertEquals("CUSTOMER.PX", names[0]);
            Assert.assertEquals("CUSTOMER.X06", names[1]);

            while (rs.next()) {
                // System.out.println("TABLE_CAT: " +
                // rs.getString("TABLE_CAT"));
                // System.out.println("TABLE_SCHEM: " +
                // rs.getString("TABLE_SCHEM"));
                // System.out.println("TABLE_NAME: " +
                // rs.getString("TABLE_NAME"));
                // System.out.println("NON_UNIQUE: " +
                // rs.getString("NON_UNIQUE"));
                // System.out.println("INDEX_QUALIFIER: " +
                // rs.getString("INDEX_QUALIFIER"));
                // System.out.println("INDEX_NAME: " +
                // rs.getString("INDEX_NAME"));
                // System.out.println("TYPE: " + rs.getString("TYPE"));
                // System.out.println("ORDINAL_POSITION: " +
                // rs.getString("ORDINAL_POSITION"));
                // System.out.println("COLUMN_NAME: " +
                // rs.getString("COLUMN_NAME"));
                // System.out.println("ASC_OR_DESC: " +
                // rs.getString("ASC_OR_DESC"));
                // System.out.println("CARDINALITY: " +
                // rs.getString("CARDINALITY"));
                // System.out.println("PAGES: " + rs.getString("PAGES"));
                // System.out.println("FILTER_CONDITION: " +
                // rs.getString("FILTER_CONDITION"));
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
                 * System.out.println("TABLE_CAT: " +
                 * rs.getString("TABLE_CAT")); System.out.println(
                 * "TABLE_SCHEM: " + rs.getString("TABLE_SCHEM"));
                 * System.out.println("TABLE_NAME: " +
                 * rs.getString("TABLE_NAME")); System.out.println(
                 * "NON_UNIQUE: " + rs.getString("NON_UNIQUE"));
                 * System.out.println("INDEX_QUALIFIER: " +
                 * rs.getString("INDEX_QUALIFIER")); System.out.println(
                 * "INDEX_NAME: " + rs.getString("INDEX_NAME"));
                 * System.out.println("TYPE: " + rs.getString("TYPE"));
                 * System.out.println("ORDINAL_POSITION: " +
                 * rs.getString("ORDINAL_POSITION")); System.out.println(
                 * "COLUMN_NAME: " + rs.getString("COLUMN_NAME"));
                 * System.out.println("ASC_OR_DESC: " +
                 * rs.getString("ASC_OR_DESC")); System.out.println(
                 * "CARDINALITY: " + rs.getString("CARDINALITY"));
                 * System.out.println("PAGES: " + rs.getString("PAGES"));
                 * System.out.println("FILTER_CONDITION: " +
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
    public void testResultSetOneColumn() throws Exception {
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();

            rs = stmt.executeQuery("SELECT email FROM customer");

            Assert.assertTrue("No First row", rs.next());
            Assert.assertEquals("1 row:", "luke@fun.com", rs.getString("email"));
            Assert.assertTrue("No second row", rs.next());
            Assert.assertEquals("2 row:", "fmallory@freeport.org", rs.getString("email"));
            Assert.assertTrue("No third row", rs.next());
            Assert.assertEquals("3 row:", "lpetzold@earthenwear.com", rs.getString("email"));
            // String last = null;
            // while (rs.next()) {
            // last = rs.getString("email");
            // }
            // Assert.assertEquals("Last row:", "tromar@bellevue.org", last);
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    @Test
    public void testResultSetTwoColumn() throws Exception {
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();

            rs = stmt.executeQuery("SELECT email,custno  FROM customer");

            Assert.assertTrue("No First row", rs.next());
            Assert.assertEquals("1 row:", "luke@fun.com", rs.getString(1));
            Assert.assertEquals("1 row:", 1, rs.getInt(2));
            Assert.assertTrue("No second row", rs.next());
            Assert.assertEquals("2 row:", "fmallory@freeport.org", rs.getString("email"));
            Assert.assertEquals("2 row:", 2, rs.getInt("custNo"));
            Assert.assertTrue("No third row", rs.next());
            Assert.assertEquals("3 row:", "lpetzold@earthenwear.com", rs.getString("Email"));
            Assert.assertEquals("2 row:", 3, rs.getInt("CUSTNO"));
            // String last = null;
            // int no = 0;
            // while (rs.next()) {
            // last = rs.getString("email");
            // no = rs.getInt(2);
            // }
            // Assert.assertEquals("Last row:", "tromar@bellevue.org", last);
            // Assert.assertEquals("Last row:", 20, no);
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    @Test
    public void testResultSet() throws Exception {
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();

            rs = stmt.executeQuery("SELECT AC as 'ACode', State, CITIES FROM AREACODES");

            Assert.assertTrue("No First row", rs.next());
            Assert.assertEquals("Column 'AC':", rs.getString("ac"), "201");
            Assert.assertEquals("Column 'State':", rs.getString("State"), "NJ");
            Assert.assertEquals("Column 'Cities':", rs.getString("Cities"),
                    "Hackensack, Jersey City (201/551 overlay)");
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    @Test
    public void testResultSet2() throws Exception {
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();

            rs = stmt.executeQuery("SELECT \"id\", name, moneys FROM \"general.db\"");

            Assert.assertTrue("First record:", rs.next());
            Assert.assertEquals("1 row: ", "1 - Mari 100.0",
                    rs.getLong(1) + " - " + rs.getString(2) + " " + rs.getFloat(3));
            Assert.assertTrue("Second record:", rs.next());
            Assert.assertEquals("2 row: ", "2 - Katty 150.0",
                    rs.getLong(1) + " - " + rs.getString(2) + " " + rs.getFloat(3));
            Assert.assertTrue("Third record:", rs.next());
            Assert.assertEquals("2 row: ", "333333333 - Elizabet 75.0",
                    rs.getLong(1) + " - " + rs.getString(2) + " " + rs.getFloat(3));
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
}
