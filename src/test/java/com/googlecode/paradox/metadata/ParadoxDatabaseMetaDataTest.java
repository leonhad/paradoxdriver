package com.googlecode.paradox.metadata;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxResultSet;
import com.googlecode.paradox.integration.MainTest;
import org.junit.*;

import java.sql.*;

/**
 * Unit test for {@link ParadoxDatabaseMetaData} class.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.3
 */
public class ParadoxDatabaseMetaDataTest {
    /**
     * The connection string used in this tests.
     */
    public static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/";

    /**
     * The database connection.
     */
    private Connection conn;

    /**
     * Register the database driver.
     *
     * @throws Exception
     *         in case of failures.
     */
    @BeforeClass
    public static void setUp() throws Exception {
        Class.forName(Driver.class.getName());
    }

    /**
     * Close the test conneciton.
     *
     * @throws Exception
     *         in case of failures.
     */
    @After
    public void closeConnection() throws Exception {
        if (conn != null) {
            conn.close();
        }
    }

    /**
     * Connect to the test database.
     *
     * @throws Exception
     *         in case of failures.
     */
    @Before
    public void connect() throws Exception {
        conn = DriverManager.getConnection(MainTest.CONNECTION_STRING + "db");
    }

    /**
     * Test for attributes.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testAttributes() throws SQLException {
        try (ResultSet rs = conn.getMetaData().getAttributes("db", null, null, null)) {
            Assert.assertTrue(rs instanceof ParadoxResultSet);
        }
    }

    /**
     * Test for commit failure closes results.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testAutocommitFailureClosesResult() throws SQLException {
        Assert.assertFalse(conn.getMetaData().autoCommitFailureClosesAllResultSets());
    }

    /**
     * Test for database definition causes transaction commit.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testDatabaseDefinitionsCausesCommit() throws SQLException {
        Assert.assertTrue(conn.getMetaData().dataDefinitionCausesTransactionCommit());
    }

    /**
     * Test for catalog separator.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testCatalogSeparator() throws SQLException {
        Assert.assertEquals("Testing for catalog separator.", ".", conn.getMetaData().getCatalogSeparator());
    }

    /**
     * Test for catalog term.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testCatalogTerm() throws SQLException {
        Assert.assertEquals("Testing for catalog term.", "CATALOG", conn.getMetaData().getCatalogTerm());
    }

    /**
     * Test for columns.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testColumns() throws SQLException {
        try (ResultSet rs = conn.getMetaData().getColumns("db", "%", "%", "%")) {
            Assert.assertTrue(rs instanceof ParadoxResultSet);
        }
    }

    /**
     * Test for tables.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testTables() throws SQLException {
        String[] types = {"TABLE", "VIEW"};

        try (ResultSet rs = conn.getMetaData().getTables("db", "%", "%", types)) {
            Assert.assertTrue(rs instanceof ParadoxResultSet);
        }
    }

    /**
     * Test for tables with null type.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testTablesWithNullType() throws SQLException {
        try (ResultSet rs = conn.getMetaData().getTables("db", "%", "%", null)) {
            Assert.assertTrue(rs instanceof ParadoxResultSet);
        }
    }

    /**
     * Test for columns with null table pattern.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testColumnsWithNullTablePattern() throws SQLException {
        try (ResultSet rs = conn.getMetaData().getColumns("db", "%", null, "%")) {
            Assert.assertTrue(rs instanceof ParadoxResultSet);
        }
    }

    /**
     * Test for columns with invalid table pattern.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testColumnsWithInvalidTablePattern() throws SQLException {
        try (ResultSet rs = conn.getMetaData().getColumns("db", "%", "invalid_table", "%")) {
            Assert.assertTrue(rs instanceof ParadoxResultSet);
        }
    }

    /**
     * Test for client info properties.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testClientInfoProperties() throws SQLException {
        try (ResultSet rs = conn.getMetaData().getClientInfoProperties()) {
            Assert.assertTrue(rs instanceof ParadoxResultSet);
        }
    }

    /**
     * Test for column privileges.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testColumnPrivileges() throws SQLException {
        try (ResultSet rs = conn.getMetaData().getColumnPrivileges("db", "%", "%", "%")) {
            Assert.assertTrue(rs instanceof ParadoxResultSet);
        }
    }

    /**
     * Test for best row identifier.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testBestRowIdentifier() throws SQLException {
        try (ResultSet rs = conn.getMetaData().getBestRowIdentifier("db", "%", "%", 0, false)) {
            Assert.assertTrue(rs instanceof ParadoxResultSet);
        }
    }

    /**
     * Test for data definition ignored in transactions.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testDataDefinitionIgnoredInTransactions() throws SQLException {
        Assert.assertFalse("Testing for data definition ignored in transactions.",
                conn.getMetaData().dataDefinitionIgnoredInTransactions());
    }

    /**
     * Test for database major version.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testDatabaseMajorVersion() throws SQLException {
        Assert.assertEquals("Testing for database major version.", 7, conn.getMetaData().getDatabaseMajorVersion());
    }

    /**
     * Test for database minor version.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testDatabaseMinorVersion() throws SQLException {
        Assert.assertEquals("Testing for database minor version.", 0, conn.getMetaData().getDatabaseMinorVersion());
    }

    /**
     * Test for generated key always returned.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testGeneratedKeyAlwaysReturned() throws SQLException {
        Assert.assertTrue("Testing for data definition ignored in transactions.",
                conn.getMetaData().generatedKeyAlwaysReturned());
    }

    /**
     * Test for table types.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testTableTypes() throws SQLException {
        try (ResultSet rs = conn.getMetaData().getTableTypes()) {
            Assert.assertTrue("Get first result.", rs.next());
            Assert.assertEquals("Testing for table type.", "TABLE", rs.getString(1));
            Assert.assertTrue("Get second result.", rs.next());
            Assert.assertEquals("Testing for view type.", "VIEW", rs.getString(1));
            Assert.assertTrue("Get third result.", rs.next());
            Assert.assertEquals("Testing for system table type.", "SYSTEM TABLE", rs.getString(1));
            Assert.assertFalse("no more results.", rs.next());
        }
    }

    /**
     * Test for columns without column pattern.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testColumnsWithoutColumnPattern() throws SQLException {
        try (ResultSet rs = conn.getMetaData().getColumns("db", "%", "%", null)) {
            Assert.assertTrue(rs instanceof ParadoxResultSet);
        }
    }

    /**
     * Test for columns with invalid pattern.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testColumnsInvaidPattern() throws SQLException {
        try (ResultSet rs = conn.getMetaData().getColumns("db", "%", "%", "invalid_column")) {
            Assert.assertTrue(rs instanceof ParadoxResultSet);
        }
    }

    /**
     * Test for connection.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testConnection() throws SQLException {
        Assert.assertSame("Testing for connection.", conn, conn.getMetaData().getConnection());
    }

    /**
     * Test for deletes autodetectes.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testDeleteAutoDetects() throws SQLException {
        Assert.assertFalse(conn.getMetaData().deletesAreDetected(0));
    }

    /**
     * Test for imported keys.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testImportedKeys() throws SQLException {
        try (ResultSet rs = conn.getMetaData().getImportedKeys("db", null, "teste.db")) {
            Assert.assertTrue(rs instanceof ParadoxResultSet);
        }
    }

    /**
     * Test for JDBC version.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testJDBCVersion() throws SQLException {
        final DatabaseMetaData meta = conn.getMetaData();
        meta.getJDBCMajorVersion();
    }

    /**
     * Test for max rows include blobs.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testMaxRowsIncludesBlob() throws SQLException {
        Assert.assertTrue(conn.getMetaData().doesMaxRowSizeIncludeBlobs());
    }

    /**
     * Test for procedures callable.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testProcedureCallable() throws SQLException {
        Assert.assertFalse(conn.getMetaData().allProceduresAreCallable());
    }

    /**
     * Test for procedure columns.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testProcedureColumns() throws SQLException {
        try (ResultSet rs = conn.getMetaData().getProcedureColumns("db", null, "*", "*")) {
            Assert.assertTrue(rs instanceof ParadoxResultSet);
        }
    }

    /**
     * Test for procedures.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testProcedures() throws SQLException {
        try (ResultSet rs = conn.getMetaData().getProcedures("db", null, "*")) {
            Assert.assertTrue(rs instanceof ParadoxResultSet);
        }
    }

    /**
     * Test for schemas.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testSchemas() throws SQLException {
        try (ResultSet rs = conn.getMetaData().getSchemas()) {
            Assert.assertTrue(rs instanceof ParadoxResultSet);
        }
    }

    /**
     * Test for the catalog metadata.
     *
     * @throws Exception
     *         in case of failures.
     */
    @Test
    public void testCatalog() throws Exception {
        final DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getCatalogs()) {
            if (rs.next()) {
                Assert.assertEquals("db", rs.getString("TABLE_CAT"));
            } else {
                Assert.fail("No catalog selected.");
            }
        }
    }

    /**
     * Test for table selectable.
     *
     * @throws SQLException
     *         in case of errors.
     */
    @Test
    public void testTableSelectable() throws SQLException {
        Assert.assertTrue(conn.getMetaData().allTablesAreSelectable());
    }
}
