/*
 * Copyright (C) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.googlecode.paradox.metadata;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxResultSet;
import com.googlecode.paradox.utils.Constants;
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
    private static final String CONNECTION_STRING = "jdbc:paradox:src/test/resources/";

    /**
     * The database connection.
     */
    private Connection conn;

    /**
     * Register the database driver.
     *
     * @throws Exception in case of failures.
     */
    @BeforeClass
    public static void init() throws Exception {
        Class.forName(Driver.class.getName());
    }

    /**
     * Close the test connection.
     *
     * @throws Exception in case of failures.
     */
    @After
    public void closeConnection() throws Exception {
        if (this.conn != null) {
            this.conn.close();
        }
    }

    /**
     * Connect to the test database.
     *
     * @throws Exception in case of failures.
     */
    @Before
    public void connect() throws Exception {
        this.conn = DriverManager.getConnection(ParadoxDatabaseMetaDataTest.CONNECTION_STRING + "db");
    }

    /**
     * Test for attributes.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testAttributes() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getAttributes("db", null, null, null)) {
            Assert.assertTrue("Invalid ResultSet state.", rs instanceof ParadoxResultSet);
        }
    }

    /**
     * Test for commit failure closes results.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testAutocommitFailureClosesResult() throws SQLException {
        Assert.assertFalse("Invalid value.", this.conn.getMetaData().autoCommitFailureClosesAllResultSets());
    }

    /**
     * Test for best row identifier.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testBestRowIdentifier() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getBestRowIdentifier("db", "%", "%", 0, false)) {
            Assert.assertTrue("Invalid value.", rs instanceof ParadoxResultSet);
        }
    }

    /**
     * Test for the catalog metadata.
     *
     * @throws Exception in case of failures.
     */
    @Test
    public void testCatalog() throws Exception {
        final DatabaseMetaData meta = this.conn.getMetaData();
        try (ResultSet rs = meta.getCatalogs()) {
            if (rs.next()) {
                Assert.assertEquals("Invalid value.", conn.getCatalog(), rs.getString("TABLE_CAT"));
            } else {
                Assert.fail("No catalog selected.");
            }
        }
    }

    /**
     * Test for catalog separator.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testCatalogSeparator() throws SQLException {
        Assert.assertEquals("Testing for catalog separator.", ".", this.conn.getMetaData().getCatalogSeparator());
    }

    /**
     * Test for catalog term.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testCatalogTerm() throws SQLException {
        Assert.assertEquals("Testing for catalog term.", "CATALOG", this.conn.getMetaData().getCatalogTerm());
    }

    /**
     * Test for client info properties.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testClientInfoProperties() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getClientInfoProperties()) {
            Assert.assertTrue("Invalid value.", rs instanceof ParadoxResultSet);
        }
    }

    /**
     * Test for column privileges.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testColumnPrivileges() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getColumnPrivileges("db", "%", "%", "%")) {
            Assert.assertTrue("Invalid value.", rs instanceof ParadoxResultSet);
        }
    }

    /**
     * Test for columns.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testColumns() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getColumns(null, "fields", "DATE4", "%")) {
            Assert.assertTrue("Invalid result set state.", rs.next());

            Assert.assertEquals("Invalid column name.", "DATE", rs.getString("COLUMN_NAME"));
        }
    }

    /**
     * Test for columns with invalid pattern.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testColumnsInvalidPattern() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getColumns(null, "%", "%", "invalid_column")) {
            Assert.assertTrue("Result not right instance.", rs instanceof ParadoxResultSet);
        }
    }

    /**
     * Test for columns with invalid table pattern.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testColumnsWithInvalidTablePattern() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getColumns("DB", "%", "invalid_table", "%")) {
            Assert.assertTrue("Invalid value.", rs instanceof ParadoxResultSet);
        }
    }

    /**
     * Test for columns with null table pattern.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testColumnsWithNullTablePattern() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getColumns("DB", "%", null, "%")) {
            Assert.assertTrue("Invalid value.", rs instanceof ParadoxResultSet);
        }
    }

    /**
     * Test for columns without column pattern.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testColumnsWithoutColumnPattern() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getColumns("DB", "%", "%", null)) {
            Assert.assertTrue("Invalid value.", rs instanceof ParadoxResultSet);
        }
    }

    /**
     * Test for connection.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testConnection() throws SQLException {
        Assert.assertSame("Testing for connection.", this.conn, this.conn.getMetaData().getConnection());
    }

    /**
     * Test for cross references.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testCrossReference() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getCrossReference("db", "%", "%", "db", "%", "%")) {
            Assert.assertTrue("Invalid value.", rs instanceof ParadoxResultSet);
        }
    }

    /**
     * Test for database definition causes transaction commit.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testDatabaseDefinitionsCausesCommit() throws SQLException {
        Assert.assertTrue("Invalid value.", this.conn.getMetaData().dataDefinitionCausesTransactionCommit());
    }

    /**
     * Test for database major version.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testDatabaseMajorVersion() throws SQLException {
        Assert.assertEquals("Testing for database major version.", 7,
                this.conn.getMetaData().getDatabaseMajorVersion());
    }

    /**
     * Test for database minor version.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testDatabaseMinorVersion() throws SQLException {
        Assert.assertEquals("Testing for database minor version.", 0,
                this.conn.getMetaData().getDatabaseMinorVersion());
    }

    /**
     * Test for database product name.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testDatabaseProductName() throws SQLException {
        Assert.assertEquals("Testing for database product name.", Constants.DRIVER_NAME,
                this.conn.getMetaData().getDatabaseProductName());
    }

    /**
     * Test for database product version.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testDatabaseProductVersion() throws SQLException {
        Assert.assertEquals("Testing for database product version.",
                Constants.DRIVER_NAME + " " + Constants.DRIVER_VERSION,
                this.conn.getMetaData().getDatabaseProductVersion());
    }

    /**
     * Test for data definition ignored in transactions.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testDataDefinitionIgnoredInTransactions() throws SQLException {
        Assert.assertFalse("Testing for data definition ignored in transactions.",
                this.conn.getMetaData().dataDefinitionIgnoredInTransactions());
    }

    /**
     * Test for default transaction isolation.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testDefaultTransactionIsolation() throws SQLException {
        Assert.assertEquals("Testing for default transaction isolation.", Connection.TRANSACTION_NONE,
                this.conn.getMetaData().getDefaultTransactionIsolation());
    }

    /**
     * Test for deletes auto detects.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testDeleteAutoDetects() throws SQLException {
        Assert.assertFalse("Invalid value.", this.conn.getMetaData().deletesAreDetected(0));
    }

    /**
     * Test for driver major version.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testDriverMajorVersion() throws SQLException {
        Assert.assertEquals("Testing for driver major version.", 1, this.conn.getMetaData().getDriverMajorVersion());
    }

    /**
     * Test for driver minor version.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testDriverMinorVersion() throws SQLException {
        Assert.assertEquals("Testing for driver minor version.", 6, this.conn.getMetaData().getDriverMinorVersion());
    }

    /**
     * Test for driver name.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testDriverName() throws SQLException {
        Assert.assertEquals("Testing for driver name.", Constants.DRIVER_NAME,
                this.conn.getMetaData().getDriverName());
    }

    /**
     * Test for driver version.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testDriverVersion() throws SQLException {
        Assert.assertEquals("Testing for driver version.", "1.6", this.conn.getMetaData().getDriverVersion());
    }

    /**
     * Test for exported keys.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testExportedKeys() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getExportedKeys("db", "%", "%")) {
            Assert.assertTrue("Invalid value.", rs instanceof ParadoxResultSet);
        }
    }

    /**
     * Test for extra name chars.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testExtraNameChars() throws SQLException {
        Assert.assertEquals("Testing for extra name chars.", "", this.conn.getMetaData().getExtraNameCharacters());
    }

    /**
     * Test for function columns.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testFunctionColumns() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getFunctionColumns(conn.getCatalog(), conn.getSchema(), null,
                null)) {
            Assert.assertTrue("Invalid value.", rs.next());
        }
    }

    /**
     * Test for functions.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testFunctions() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getFunctions("DATABASE", "db", "%")) {
            Assert.assertTrue("Invalid value.", rs.next());
        }
    }

    /**
     * Test for generated key always returned.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testGeneratedKeyAlwaysReturned() throws SQLException {
        Assert.assertTrue("Testing for data definition ignored in transactions.",
                this.conn.getMetaData().generatedKeyAlwaysReturned());
    }

    /**
     * Test for identifier quote string.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testIdentifierQuoteString() throws SQLException {
        Assert.assertEquals("Testing for extra name chars.", "\"", this.conn.getMetaData().getIdentifierQuoteString());
    }

    /**
     * Test for imported keys.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testImportedKeys() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getImportedKeys("db", null, "test.db")) {
            Assert.assertTrue("Invalid instance.", rs instanceof ParadoxResultSet);
        }
    }

    /**
     * Test for index info.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testIndexInfo() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getIndexInfo(conn.getCatalog(), "joins", "indexed", false, true)) {
            Assert.assertTrue("Test for ResultSet.", rs.next());
        }
    }

    /**
     * Test for JDBC version.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testJDBCVersion() throws SQLException {
        final DatabaseMetaData meta = this.conn.getMetaData();
        Assert.assertEquals("Test for major version", 4, meta.getJDBCMajorVersion());
        Assert.assertEquals("Test for minor version", 2, meta.getJDBCMinorVersion());
    }

    /**
     * Test for max binary literal length.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testMaxBinaryLiteralLength() throws SQLException {
        Assert.assertEquals("Testing for max binary literal length.", 8,
                this.conn.getMetaData().getMaxBinaryLiteralLength());
    }

    /**
     * Test for max catalog name length.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testMaxCatalogNameLength() throws SQLException {
        Assert.assertEquals("Testing for max catalog name length.", 255,
                this.conn.getMetaData().getMaxCatalogNameLength());
    }

    /**
     * Test for max char literal length.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testMaxCharLiteralLength() throws SQLException {
        Assert.assertEquals("Testing for max char literal name.", 255,
                this.conn.getMetaData().getMaxCharLiteralLength());
    }

    /**
     * Test for max column in group by.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testMaxColumnInGroupBy() throws SQLException {
        Assert.assertEquals("Testing for max column in group by.", 255,
                this.conn.getMetaData().getMaxColumnsInGroupBy());
    }

    /**
     * Test for max column in index.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testMaxColumnInIndex() throws SQLException {
        Assert.assertEquals("Testing for max column in index.", 255, this.conn.getMetaData().getMaxColumnsInIndex());
    }

    /**
     * Test for max column in order by.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testMaxColumnInOrderBy() throws SQLException {
        Assert.assertEquals("Testing for max column in order by.", 255,
                this.conn.getMetaData().getMaxColumnsInOrderBy());
    }

    /**
     * Test for max column in select.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testMaxColumnInSelect() throws SQLException {
        Assert.assertEquals("Testing for max column in select.", 255, this.conn.getMetaData().getMaxColumnsInSelect());
    }

    /**
     * Test for max column in table.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testMaxColumnInTable() throws SQLException {
        Assert.assertEquals("Testing for max column in table.", 255, this.conn.getMetaData().getMaxColumnsInTable());
    }

    /**
     * Test for max column name length.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testMaxColumnNameLength() throws SQLException {
        Assert.assertEquals("Testing for max column name length.", 8, this.conn.getMetaData().getMaxColumnNameLength());
    }

    /**
     * Test for max connections.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testMaxConnections() throws SQLException {
        Assert.assertEquals("Testing for max connections.", 1, this.conn.getMetaData().getMaxConnections());
    }

    /**
     * Test for max cursor name length.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testMaxCursorNameLength() throws SQLException {
        Assert.assertEquals("Testing for max cursor name length.", 255,
                this.conn.getMetaData().getMaxCursorNameLength());
    }

    /**
     * Test for max index length.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testMaxIndexLength() throws SQLException {
        Assert.assertEquals("Testing for max index length.", 255, this.conn.getMetaData().getMaxIndexLength());
    }

    /**
     * Test for max procedure name length.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testMaxProcedureNameLength() throws SQLException {
        Assert.assertEquals("Testing for max procedure name length.", 255,
                this.conn.getMetaData().getMaxProcedureNameLength());
    }

    /**
     * Test for max rows include blobs.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testMaxRowsIncludesBlob() throws SQLException {
        Assert.assertTrue("Invalid value.", this.conn.getMetaData().doesMaxRowSizeIncludeBlobs());
    }

    /**
     * Test for max row size.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testMaxRowSize() throws SQLException {
        Assert.assertEquals("Testing for max row size.", 255, this.conn.getMetaData().getMaxRowSize());
    }

    /**
     * Test for max statement length.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testMaxStatementLength() throws SQLException {
        Assert.assertEquals("Testing for max statement length.", Integer.MAX_VALUE,
                this.conn.getMetaData().getMaxStatementLength());
    }

    /**
     * Test for max statements.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testMaxStatements() throws SQLException {
        Assert.assertEquals("Testing for max column in table.", Integer.MAX_VALUE,
                this.conn.getMetaData().getMaxStatements());
    }

    /**
     * Test for max tables in select.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testMaxTableInSelect() throws SQLException {
        Assert.assertEquals("Testing for max column in table.", 255, this.conn.getMetaData().getMaxTablesInSelect());
    }

    /**
     * Test for max table name length.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testMaxTableNameLength() throws SQLException {
        Assert.assertEquals("Testing for max table name length.", 255, this.conn.getMetaData().getMaxTableNameLength());
    }

    /**
     * Test for max user name length.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testMaxUserNameLength() throws SQLException {
        Assert.assertEquals("Testing for max user name length.", 255, this.conn.getMetaData().getMaxUserNameLength());
    }

    /**
     * Test for numeric functions.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testNumericFunctions() throws SQLException {
        Assert.assertFalse("Testing for numeric functions.", this.conn.getMetaData().getNumericFunctions().isEmpty());
    }

    /**
     * Test for primary keys.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testPrimaryKeys() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getPrimaryKeys("DB", "%", "%")) {
            Assert.assertTrue("Test for ResultSet.", rs instanceof ParadoxResultSet);
        }
    }

    /**
     * Test for procedures callable.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testProcedureCallable() throws SQLException {
        Assert.assertTrue("Invalid callable value.", this.conn.getMetaData().allProceduresAreCallable());
    }

    /**
     * Test for procedure columns.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testProcedureColumns() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getProcedureColumns("db", "%", "%", "%")) {
            Assert.assertTrue("Invalid ResultSet instance.", rs instanceof ParadoxResultSet);
        }
    }

    /**
     * Test for procedure columns with invalid pattern.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testProcedureColumnsWithInvalidPattern() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getProcedureColumns("db", "%", "invalid_procedure", "%")) {
            Assert.assertTrue("Invalid ResultSet instance.", rs instanceof ParadoxResultSet);
        }
    }

    /**
     * Test for procedures.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testProcedures() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getProcedures("db", "%", "%")) {
            Assert.assertTrue("Invalid ResultSet instance.", rs instanceof ParadoxResultSet);
        }
    }

    /**
     * Test for schemas.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testSchemas() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getSchemas()) {

            // Classes schema.
            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid schema", "db", rs.getString("TABLE_SCHEM"));
            Assert.assertEquals("Invalid catalog", this.conn.getCatalog(), rs.getString("TABLE_CATALOG"));

            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid schema", "encrypt", rs.getString("TABLE_SCHEM"));
            Assert.assertEquals("Invalid catalog", this.conn.getCatalog(), rs.getString("TABLE_CATALOG"));

            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid schema", "fields", rs.getString("TABLE_SCHEM"));
            Assert.assertEquals("Invalid catalog", this.conn.getCatalog(), rs.getString("TABLE_CATALOG"));

            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid schema", "geog", rs.getString("TABLE_SCHEM"));
            Assert.assertEquals("Invalid catalog", this.conn.getCatalog(), rs.getString("TABLE_CATALOG"));

            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid schema", "information_schema", rs.getString("TABLE_SCHEM"));
            Assert.assertEquals("Invalid catalog", this.conn.getCatalog(), rs.getString("TABLE_CATALOG"));

            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid schema", "joins", rs.getString("TABLE_SCHEM"));
            Assert.assertEquals("Invalid catalog", this.conn.getCatalog(), rs.getString("TABLE_CATALOG"));

            Assert.assertFalse("Invalid ResultSet state.", rs.next());
        }
    }

    /**
     * Test for schemas with pattern.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testSchemaWithPattern() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getSchemas(this.conn.getCatalog(), "fiel%")) {
            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid schema", "fields", rs.getString("TABLE_SCHEM"));
            Assert.assertEquals("Invalid catalog", this.conn.getCatalog(), rs.getString("TABLE_CATALOG"));

            Assert.assertFalse("Invalid ResultSet state.", rs.next());
        }
    }

    /**
     * Test for tables.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testTables() throws SQLException {
        final String[] types = {"TABLE", "VIEW"};

        try (ResultSet rs = this.conn.getMetaData().getTables(conn.getCatalog(), "%", "%", types)) {
            Assert.assertTrue("Invalid result set state", rs.next());
        }
    }

    /**
     * Test for tables in eclipse.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testTablesEclipse() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getTables(conn.getCatalog(), "fields", null, null)) {
            Assert.assertTrue("Invalid result set state", rs.next());
        }
    }

    /**
     * Test for table selectable.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testTableSelectable() throws SQLException {
        Assert.assertTrue("Invalid table selectable.", this.conn.getMetaData().allTablesAreSelectable());
    }

    /**
     * Test for tables with null type.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testTablesWithNullType() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getTables(conn.getCatalog(), "%", "%", null)) {
            Assert.assertTrue("Invalid ResultSet instance.", rs instanceof ParadoxResultSet);
        }
    }

    /**
     * Test for table types.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testTableTypes() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getTableTypes()) {
            Assert.assertTrue("Get first result.", rs.next());
            Assert.assertEquals("Testing for system table type.", "SYSTEM TABLE", rs.getString(1));
            Assert.assertTrue("Get second result.", rs.next());
            Assert.assertEquals("Testing for table type.", "TABLE", rs.getString(1));
            Assert.assertTrue("Get third result.", rs.next());
            Assert.assertEquals("Testing for view type.", "VIEW", rs.getString(1));
            Assert.assertFalse("no more results.", rs.next());
        }
    }

    /**
     * Test for primary key metadata.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testPrimaryKey() throws SQLException {
        final DatabaseMetaData meta = this.conn.getMetaData();

        try (ResultSet rs = meta.getPrimaryKeys(null, "db", "CUSTOMER.db")) {
            Assert.assertTrue("Invalid ResultSet state", rs.next());
            Assert.assertEquals("Invalid value.", conn.getCatalog(), rs.getString("TABLE_CAT"));
            Assert.assertEquals("Invalid value.", "db", rs.getString("TABLE_SCHEM"));
            Assert.assertEquals("Invalid value.", "CUSTOMER", rs.getString("TABLE_NAME"));
            Assert.assertEquals("Invalid value.", "CustNo", rs.getString("COLUMN_NAME"));
            Assert.assertEquals("Invalid value.", "0", rs.getString("KEY_SEQ"));
            Assert.assertEquals("Invalid value.", "CUSTOMER.PX", rs.getString("PK_NAME"));
            Assert.assertFalse("Invalid ResultSet State.", rs.next());
        }
    }

    /**
     * Test for view columns metadata.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testViewColumns() throws SQLException {
        final DatabaseMetaData meta = this.conn.getMetaData();

        try (ResultSet rs = meta.getColumns(null, "db", "AREAS.QBE", "%")) {
            // Test for AC field.
            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Testing for table catalog.", conn.getCatalog(), rs.getString("TABLE_CAT"));
            Assert.assertEquals("Testing for table schema.", "db", rs.getString("TABLE_SCHEM"));
            Assert.assertEquals("Testing for table name.", "AREAS", rs.getString("TABLE_NAME"));
            Assert.assertEquals("Testing for column name.", "AC", rs.getString("COLUMN_NAME"));
            Assert.assertEquals("Testing for data type.", 12, rs.getInt("DATA_TYPE"));
            Assert.assertEquals("Testing for type name.", "VARCHAR", rs.getString("TYPE_NAME"));
            Assert.assertEquals("Testing for column size.", 5, rs.getInt("COLUMN_SIZE"));
            Assert.assertEquals("Testing for nullable.", DatabaseMetaData.columnNullable, rs.getInt("NULLABLE"));
            Assert.assertEquals("Testing for is nullable.", "YES", rs.getString("IS_NULLABLE"));
            Assert.assertEquals("Testing for is auto increment field.", "NO", rs.getString("IS_AUTOINCREMENT"));

            // Test for State field.
            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Testing for table catalog.", conn.getCatalog(), rs.getString("TABLE_CAT"));
            Assert.assertEquals("Testing for table schema.", "db", rs.getString("TABLE_SCHEM"));
            Assert.assertEquals("Testing for table name.", "AREAS", rs.getString("TABLE_NAME"));
            Assert.assertEquals("Testing for column name.", "State", rs.getString("COLUMN_NAME"));
            Assert.assertEquals("Testing for data type.", 12, rs.getInt("DATA_TYPE"));
            Assert.assertEquals("Testing for type name.", "VARCHAR", rs.getString("TYPE_NAME"));
            Assert.assertEquals("Testing for column size.", 3, rs.getInt("COLUMN_SIZE"));
            Assert.assertEquals("Testing for nullable.", DatabaseMetaData.columnNullable, rs.getInt("NULLABLE"));
            Assert.assertEquals("Testing for is nullable.", "YES", rs.getString("IS_NULLABLE"));
            Assert.assertEquals("Testing for is auto increment field.", "NO", rs.getString("IS_AUTOINCREMENT"));

            // Test for Cities field.
            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Testing for table catalog.", conn.getCatalog(), rs.getString("TABLE_CAT"));
            Assert.assertEquals("Testing for table schema.", "db", rs.getString("TABLE_SCHEM"));
            Assert.assertEquals("Testing for table name.", "AREAS", rs.getString("TABLE_NAME"));
            Assert.assertEquals("Testing for column name.", "Cities", rs.getString("COLUMN_NAME"));
            Assert.assertEquals("Testing for data type.", 12, rs.getInt("DATA_TYPE"));
            Assert.assertEquals("Testing for type name.", "VARCHAR", rs.getString("TYPE_NAME"));
            Assert.assertEquals("Testing for column size.", 157, rs.getInt("COLUMN_SIZE"));
            Assert.assertEquals("Testing for nullable.", DatabaseMetaData.columnNullable, rs.getInt("NULLABLE"));
            Assert.assertEquals("Testing for is nullable.", "YES", rs.getString("IS_NULLABLE"));
            Assert.assertEquals("Testing for is auto increment field.", "NO", rs.getString("IS_AUTOINCREMENT"));

            // No more results.
            Assert.assertFalse("Invalid ResultSet state.", rs.next());
        }
    }

    /**
     * Test for primary key metadata with two keys.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testPrimaryKeyTwoKeys() throws SQLException {
        final DatabaseMetaData meta = this.conn.getMetaData();

        try (ResultSet rs = meta.getPrimaryKeys(null, "db", "SERVER.db")) {
            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid value.", conn.getCatalog(), rs.getString("TABLE_CAT"));
            Assert.assertEquals("Invalid value.", "db", rs.getString("TABLE_SCHEM"));
            Assert.assertEquals("Invalid value.", "SERVER", rs.getString("TABLE_NAME"));
            Assert.assertEquals("Invalid value.", "REQTYPE", rs.getString("COLUMN_NAME"));
            Assert.assertEquals("Invalid value.", "0", rs.getString("KEY_SEQ"));
            Assert.assertEquals("Invalid value.", "SERVER.PX", rs.getString("PK_NAME"));
            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid value.", conn.getCatalog(), rs.getString("TABLE_CAT"));
            Assert.assertEquals("Invalid value.", "db", rs.getString("TABLE_SCHEM"));
            Assert.assertEquals("Invalid value.", "SERVER", rs.getString("TABLE_NAME"));
            Assert.assertEquals("Invalid value.", "URI", rs.getString("COLUMN_NAME"));
            Assert.assertEquals("Invalid value.", "1", rs.getString("KEY_SEQ"));
            Assert.assertEquals("Invalid value.", "SERVER.PX", rs.getString("PK_NAME"));
            Assert.assertFalse("Invalid ResultSet state.", rs.next());
        }
    }
}
