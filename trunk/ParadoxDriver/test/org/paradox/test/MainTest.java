package org.paradox.test;

import org.paradox.ParadoxConnection;
import org.paradox.Driver;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;

/**
 * Testes do drive do Paradox
 *
 * @author Leonardo Alves da Costa
 */
public class MainTest {

    @Before
    public void setUp() throws Exception {
        Class.forName(Driver.class.getName());
    }

    @Test
    public void testConnection() throws Exception {
        Connection conn = null;

        try {
            conn = DriverManager.getConnection("jdbc:paradox:./db");
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    @Test
    public void testWrap() throws Exception {
        Connection conn = null;

        try {
            conn = DriverManager.getConnection("jdbc:paradox:./db");
            if (conn.isWrapperFor(ParadoxConnection.class)) {
                conn.unwrap(ParadoxConnection.class);
            } else {
                throw new Exception("Is not a wrapper");
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    @Test
    public void testCatalog() throws Exception {
        Connection conn = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection("jdbc:paradox:./db");
            final DatabaseMetaData meta = conn.getMetaData();

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
            if (conn != null) {
                conn.close();
            }
        }
    }

    @Test
    public void testPrimaryKey() throws Exception {
        Connection conn = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection("jdbc:paradox:./db");
            final DatabaseMetaData meta = conn.getMetaData();

            rs = meta.getPrimaryKeys("db", "APP", "GENEROCLIENTE.db");
            while (rs.next()) {
                /*System.out.println("TABLE_CAT: " + rs.getString("TABLE_CAT"));
                System.out.println("TABLE_SCHEM: " + rs.getString("TABLE_SCHEM"));
                System.out.println("TABLE_NAME: " + rs.getString("TABLE_NAME"));
                System.out.println("NON_UNIQUE: " + rs.getString("NON_UNIQUE"));
                System.out.println("INDEX_QUALIFIER: " + rs.getString("INDEX_QUALIFIER"));
                System.out.println("INDEX_NAME: " + rs.getString("INDEX_NAME"));
                System.out.println("TYPE: " + rs.getString("TYPE"));
                System.out.println("ORDINAL_POSITION: " + rs.getString("ORDINAL_POSITION"));
                System.out.println("COLUMN_NAME: " + rs.getString("COLUMN_NAME"));
                System.out.println("ASC_OR_DESC: " + rs.getString("ASC_OR_DESC"));
                System.out.println("CARDINALITY: " + rs.getString("CARDINALITY"));
                System.out.println("PAGES: " + rs.getString("PAGES"));
                System.out.println("FILTER_CONDITION: " + rs.getString("FILTER_CONDITION"));*/
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    @Test
    public void testIndexInfo() throws Exception {
        Connection conn = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection("jdbc:paradox:./db");
            final DatabaseMetaData meta = conn.getMetaData();

            rs = meta.getIndexInfo("db", "APP", "TESTE.db", true, true);
            Assert.assertTrue(rs.next());
            Assert.assertEquals("TESTE.PX", rs.getString("INDEX_NAME"));
            Assert.assertTrue(rs.next());
            Assert.assertEquals("0123456789012345678901234", rs.getString("INDEX_NAME"));
            Assert.assertTrue(rs.next());
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    @Test
    public void testViewColumns() throws Exception {
        Connection conn = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection("jdbc:paradox:./db");
            final DatabaseMetaData meta = conn.getMetaData();

            rs = meta.getColumns("db", "APP", "CONTASAPAGAR.QBE", "%");
            while (rs.next()) {
                /*System.out.println("TABLE_CAT: " + rs.getString("TABLE_CAT"));
                System.out.println("TABLE_SCHEM: " + rs.getString("TABLE_SCHEM"));
                System.out.println("TABLE_NAME: " + rs.getString("TABLE_NAME"));
                System.out.println("NON_UNIQUE: " + rs.getString("NON_UNIQUE"));
                System.out.println("INDEX_QUALIFIER: " + rs.getString("INDEX_QUALIFIER"));
                System.out.println("INDEX_NAME: " + rs.getString("INDEX_NAME"));
                System.out.println("TYPE: " + rs.getString("TYPE"));
                System.out.println("ORDINAL_POSITION: " + rs.getString("ORDINAL_POSITION"));
                System.out.println("COLUMN_NAME: " + rs.getString("COLUMN_NAME"));
                System.out.println("ASC_OR_DESC: " + rs.getString("ASC_OR_DESC"));
                System.out.println("CARDINALITY: " + rs.getString("CARDINALITY"));
                System.out.println("PAGES: " + rs.getString("PAGES"));
                System.out.println("FILTER_CONDITION: " + rs.getString("FILTER_CONDITION"));*/
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    @Test
    public void testResultSet() throws Exception {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection("jdbc:paradox:./db");
            stmt = conn.createStatement();

            rs = stmt.executeQuery("SELECT \"codigo\", codigodist FROM \"PRODUTO.db\"");

            while (rs.next()) {
                //System.out.println(rs.getInt(1) + " - " + rs.getString(2));
            }
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
