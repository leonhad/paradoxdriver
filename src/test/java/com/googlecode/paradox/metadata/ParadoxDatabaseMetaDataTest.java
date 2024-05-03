/*
 * Copyright (c) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.googlecode.paradox.metadata;

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxResultSet;
import com.googlecode.paradox.utils.Constants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for {@link ParadoxDatabaseMetaData} class.
 *
 * @since 1.3
 */
class ParadoxDatabaseMetaDataTest {
    /**
     * The connection string used by tests.
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
    @BeforeAll
    static void init() throws Exception {
        Class.forName(Driver.class.getName());
    }

    /**
     * Close the test connection.
     *
     * @throws Exception in case of failures.
     */
    @AfterEach
    void closeConnection() throws Exception {
        if (this.conn != null) {
            this.conn.close();
        }
    }

    /**
     * Connect to the test database.
     *
     * @throws Exception in case of failures.
     */
    @BeforeEach
    void connect() throws Exception {
        this.conn = DriverManager.getConnection(ParadoxDatabaseMetaDataTest.CONNECTION_STRING + "db");
    }

    /**
     * Test for attributes.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testAttributes() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getAttributes("db", null, null, null)) {
            assertInstanceOf(ParadoxResultSet.class, rs);
        }
    }

    /**
     * Test for commit failure closes results.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testAutocommitFailureClosesResult() throws SQLException {
        assertFalse(this.conn.getMetaData().autoCommitFailureClosesAllResultSets());
    }

    /**
     * Test for best row identifier.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testBestRowIdentifier() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getBestRowIdentifier("db", "%", "%", 0, false)) {
            assertInstanceOf(ParadoxResultSet.class, rs);
        }
    }

    /**
     * Test for the catalog metadata.
     *
     * @throws Exception in case of failures.
     */
    @Test
    void testCatalog() throws Exception {
        final java.sql.DatabaseMetaData meta = this.conn.getMetaData();
        try (ResultSet rs = meta.getCatalogs()) {
            if (rs.next()) {
                assertEquals(conn.getCatalog(), rs.getString("TABLE_CAT"));
            } else {
                fail("No catalog selected.");
            }
        }
    }

    /**
     * Test for catalog separator.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testCatalogSeparator() throws SQLException {
        assertEquals(".", this.conn.getMetaData().getCatalogSeparator());
    }

    /**
     * Test for catalog term.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testCatalogTerm() throws SQLException {
        assertEquals("CATALOG", this.conn.getMetaData().getCatalogTerm());
    }

    /**
     * Test for client info properties.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testClientInfoProperties() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getClientInfoProperties()) {
            assertInstanceOf(ParadoxResultSet.class, rs);
        }
    }

    /**
     * Test for column privileges.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testColumnPrivileges() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getColumnPrivileges("db", "%", "%", "%")) {
            assertInstanceOf(ParadoxResultSet.class, rs);
        }
    }

    /**
     * Test for columns.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testColumns() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getColumns(null, "fields", "DATE4", "%")) {
            assertTrue(rs.next());

            assertEquals("DATE", rs.getString("COLUMN_NAME"));
        }
    }

    /**
     * Test for columns with invalid pattern.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testColumnsInvalidPattern() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getColumns(null, "%", "%", "invalid_column")) {
            assertInstanceOf(ParadoxResultSet.class, rs);
        }
    }

    /**
     * Test for columns with invalid table pattern.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testColumnsWithInvalidTablePattern() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getColumns("DB", "%", "invalid_table", "%")) {
            assertInstanceOf(ParadoxResultSet.class, rs);
        }
    }

    /**
     * Test for columns with null table pattern.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testColumnsWithNullTablePattern() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getColumns("DB", "%", null, "%")) {
            assertInstanceOf(ParadoxResultSet.class, rs);
        }
    }

    /**
     * Test for columns without column pattern.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testColumnsWithoutColumnPattern() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getColumns("DB", "%", "%", null)) {
            assertInstanceOf(ParadoxResultSet.class, rs);
        }
    }

    /**
     * Test for connection.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testConnection() throws SQLException {
        assertSame(this.conn, this.conn.getMetaData().getConnection());
    }

    /**
     * Test for cross references.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testCrossReference() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getCrossReference("db", "%", "%", "db", "%", "%")) {
            assertInstanceOf(ParadoxResultSet.class, rs);
        }
    }

    /**
     * Test for database definition causes transaction commit.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testDatabaseDefinitionsCausesCommit() throws SQLException {
        assertTrue(this.conn.getMetaData().dataDefinitionCausesTransactionCommit());
    }

    /**
     * Test for database major version.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testDatabaseMajorVersion() throws SQLException {
        assertEquals(7, this.conn.getMetaData().getDatabaseMajorVersion());
    }

    /**
     * Test for database minor version.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testDatabaseMinorVersion() throws SQLException {
        assertEquals(0, this.conn.getMetaData().getDatabaseMinorVersion());
    }

    /**
     * Test for database product name.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testDatabaseProductName() throws SQLException {
        assertEquals(Constants.DRIVER_NAME, this.conn.getMetaData().getDatabaseProductName());
    }

    /**
     * Test for database product version.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testDatabaseProductVersion() throws SQLException {
        assertEquals(Constants.DRIVER_NAME + " " + Constants.DRIVER_VERSION, this.conn.getMetaData().getDatabaseProductVersion());
    }

    /**
     * Test for data definition ignored in transactions.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testDataDefinitionIgnoredInTransactions() throws SQLException {
        assertFalse(this.conn.getMetaData().dataDefinitionIgnoredInTransactions());
    }

    /**
     * Test for default transaction isolation.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testDefaultTransactionIsolation() throws SQLException {
        assertEquals(Connection.TRANSACTION_NONE, this.conn.getMetaData().getDefaultTransactionIsolation());
    }

    /**
     * Test for deletes auto detects.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testDeleteAutoDetects() throws SQLException {
        assertFalse(this.conn.getMetaData().deletesAreDetected(0));
    }

    /**
     * Test for driver major version.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testDriverMajorVersion() throws SQLException {
        assertEquals(1, this.conn.getMetaData().getDriverMajorVersion());
    }

    /**
     * Test for driver minor version.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testDriverMinorVersion() throws SQLException {
        assertEquals(6, this.conn.getMetaData().getDriverMinorVersion());
    }

    /**
     * Test for driver name.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testDriverName() throws SQLException {
        assertEquals(Constants.DRIVER_NAME, this.conn.getMetaData().getDriverName());
    }

    /**
     * Test for driver version.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testDriverVersion() throws SQLException {
        assertEquals("1.6", this.conn.getMetaData().getDriverVersion());
    }

    /**
     * Test for exported keys.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testExportedKeys() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getExportedKeys("db", "%", "%")) {
            assertInstanceOf(ParadoxResultSet.class, rs);
        }
    }

    /**
     * Test for extra name chars.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testExtraNameChars() throws SQLException {
        assertEquals("", this.conn.getMetaData().getExtraNameCharacters());
    }

    /**
     * Test for function columns.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testFunctionColumns() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getFunctionColumns(conn.getCatalog(), conn.getSchema(), null,
                null)) {
            assertTrue(rs.next());
        }
    }

    /**
     * Test for functions.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testFunctions() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getFunctions(null, "db", "%")) {
            assertTrue(rs.next());
        }
    }

    /**
     * Test for generated key always returned.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testGeneratedKeyAlwaysReturned() throws SQLException {
        assertTrue(this.conn.getMetaData().generatedKeyAlwaysReturned());
    }

    /**
     * Test for identifier quote string.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testIdentifierQuoteString() throws SQLException {
        assertEquals("\"", this.conn.getMetaData().getIdentifierQuoteString());
    }

    /**
     * Test for imported keys.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testImportedKeys() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getImportedKeys("db", null, "test")) {
            assertInstanceOf(ParadoxResultSet.class, rs);
        }
    }

    /**
     * Test for index info.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testIndexInfo() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getIndexInfo(conn.getCatalog(), "joins", "indexed", false, true)) {
            assertTrue(rs.next());
        }
    }

    /**
     * Test for area codes index info.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testAreaCodesIndexInfo() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getIndexInfo(conn.getCatalog(), "db", "AREACODES", false, true)) {
            assertTrue(rs.next());
        }
    }

    /**
     * Test for JDBC version.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testJDBCVersion() throws SQLException {
        final java.sql.DatabaseMetaData meta = this.conn.getMetaData();
        assertEquals(4, meta.getJDBCMajorVersion());
        assertEquals(2, meta.getJDBCMinorVersion());
    }

    /**
     * Test for max binary literal length.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testMaxBinaryLiteralLength() throws SQLException {
        assertEquals(8, this.conn.getMetaData().getMaxBinaryLiteralLength());
    }

    /**
     * Test for max catalog name length.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testMaxCatalogNameLength() throws SQLException {
        assertEquals(255, this.conn.getMetaData().getMaxCatalogNameLength());
    }

    /**
     * Test for max char literal length.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testMaxCharLiteralLength() throws SQLException {
        assertEquals(255, this.conn.getMetaData().getMaxCharLiteralLength());
    }

    /**
     * Test for max column in group by.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testMaxColumnInGroupBy() throws SQLException {
        assertEquals(255, this.conn.getMetaData().getMaxColumnsInGroupBy());
    }

    /**
     * Test for max column in index.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testMaxColumnInIndex() throws SQLException {
        assertEquals(255, this.conn.getMetaData().getMaxColumnsInIndex());
    }

    /**
     * Test for max column in order by.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testMaxColumnInOrderBy() throws SQLException {
        assertEquals(255, this.conn.getMetaData().getMaxColumnsInOrderBy());
    }

    /**
     * Test for max column in select.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testMaxColumnInSelect() throws SQLException {
        assertEquals(255, this.conn.getMetaData().getMaxColumnsInSelect());
    }

    /**
     * Test for max column in table.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testMaxColumnInTable() throws SQLException {
        assertEquals(255, this.conn.getMetaData().getMaxColumnsInTable());
    }

    /**
     * Test for max column name length.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testMaxColumnNameLength() throws SQLException {
        assertEquals(8, this.conn.getMetaData().getMaxColumnNameLength());
    }

    /**
     * Test for max connections.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testMaxConnections() throws SQLException {
        assertEquals(1, this.conn.getMetaData().getMaxConnections());
    }

    /**
     * Test for max cursor name length.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testMaxCursorNameLength() throws SQLException {
        assertEquals(255, this.conn.getMetaData().getMaxCursorNameLength());
    }

    /**
     * Test for max index length.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testMaxIndexLength() throws SQLException {
        assertEquals(255, this.conn.getMetaData().getMaxIndexLength());
    }

    /**
     * Test for max procedure name length.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testMaxProcedureNameLength() throws SQLException {
        assertEquals(255, this.conn.getMetaData().getMaxProcedureNameLength());
    }

    /**
     * Test for max rows include blobs.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testMaxRowsIncludesBlob() throws SQLException {
        assertTrue(this.conn.getMetaData().doesMaxRowSizeIncludeBlobs());
    }

    /**
     * Test for max row size.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testMaxRowSize() throws SQLException {
        assertEquals(255, this.conn.getMetaData().getMaxRowSize());
    }

    /**
     * Test for max statement length.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testMaxStatementLength() throws SQLException {
        assertEquals(Integer.MAX_VALUE, this.conn.getMetaData().getMaxStatementLength());
    }

    /**
     * Test for max statements.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testMaxStatements() throws SQLException {
        assertEquals(Integer.MAX_VALUE, this.conn.getMetaData().getMaxStatements());
    }

    /**
     * Test for max tables in select.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testMaxTableInSelect() throws SQLException {
        assertEquals(255, this.conn.getMetaData().getMaxTablesInSelect());
    }

    /**
     * Test for max table name length.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testMaxTableNameLength() throws SQLException {
        assertEquals(255, this.conn.getMetaData().getMaxTableNameLength());
    }

    /**
     * Test for max user name length.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testMaxUserNameLength() throws SQLException {
        assertEquals(255, this.conn.getMetaData().getMaxUserNameLength());
    }

    /**
     * Test for numeric functions.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testNumericFunctions() throws SQLException {
        assertFalse(this.conn.getMetaData().getNumericFunctions().isEmpty());
    }

    /**
     * Test for primary keys.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testPrimaryKeys() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getPrimaryKeys(null, null, "%")) {
            assertInstanceOf(ParadoxResultSet.class, rs);
        }
    }

    /**
     * Test for procedures callable.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testProcedureCallable() throws SQLException {
        assertTrue(this.conn.getMetaData().allProceduresAreCallable());
    }

    /**
     * Test for procedure columns.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testProcedureColumns() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getProcedureColumns("db", "%", "%", "%")) {
            assertInstanceOf(ParadoxResultSet.class, rs);
        }
    }

    /**
     * Test for procedure columns with invalid pattern.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testProcedureColumnsWithInvalidPattern() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getProcedureColumns("db", "%", "invalid_procedure", "%")) {
            assertInstanceOf(ParadoxResultSet.class, rs);
        }
    }

    /**
     * Test for procedures.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testProcedures() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getProcedures("db", "%", "%")) {
            assertInstanceOf(ParadoxResultSet.class, rs);
        }
    }

    /**
     * Test for schemas.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testSchemas() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getSchemas()) {

            assertTrue(rs.next());
            assertEquals("areas", rs.getString("TABLE_SCHEM"));
            assertEquals(this.conn.getCatalog(), rs.getString("TABLE_CATALOG"));

            assertTrue(rs.next());
            assertEquals("db", rs.getString("TABLE_SCHEM"));
            assertEquals(this.conn.getCatalog(), rs.getString("TABLE_CATALOG"));

            assertTrue(rs.next());
            assertEquals("encrypt", rs.getString("TABLE_SCHEM"));
            assertEquals(this.conn.getCatalog(), rs.getString("TABLE_CATALOG"));

            assertTrue(rs.next());
            assertEquals("fields", rs.getString("TABLE_SCHEM"));
            assertEquals(this.conn.getCatalog(), rs.getString("TABLE_CATALOG"));

            assertTrue(rs.next());
            assertEquals("geog", rs.getString("TABLE_SCHEM"));
            assertEquals(this.conn.getCatalog(), rs.getString("TABLE_CATALOG"));

            assertTrue(rs.next());
            assertEquals("information_schema", rs.getString("TABLE_SCHEM"));
            assertEquals(this.conn.getCatalog(), rs.getString("TABLE_CATALOG"));

            assertTrue(rs.next());
            assertEquals("joins", rs.getString("TABLE_SCHEM"));
            assertEquals(this.conn.getCatalog(), rs.getString("TABLE_CATALOG"));

            assertTrue(rs.next());
            assertEquals("mtdemo", rs.getString("TABLE_SCHEM"));
            assertEquals(this.conn.getCatalog(), rs.getString("TABLE_CATALOG"));

            assertFalse(rs.next());
        }
    }

    /**
     * Test for schemas with catalog enabled.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testSchemasWithCatalogEnabled() throws SQLException {
        final Properties properties = new Properties();
        properties.put(ConnectionInfo.ENABLE_CATALOG_KEY, "true");

        try (final Connection connection = DriverManager.getConnection(CONNECTION_STRING + "db", properties);
             final ResultSet rs = connection.getMetaData().getSchemas()) {

            assertTrue(rs.next());
            assertEquals("com", rs.getString("TABLE_SCHEM"));
            assertEquals("java", rs.getString("TABLE_CATALOG"));

            assertTrue(rs.next());
            assertEquals("information_schema", rs.getString("TABLE_SCHEM"));
            assertEquals("java", rs.getString("TABLE_CATALOG"));

            assertTrue(rs.next());
            assertEquals("areas", rs.getString("TABLE_SCHEM"));
            assertEquals("resources", rs.getString("TABLE_CATALOG"));

            assertTrue(rs.next());
            assertEquals("db", rs.getString("TABLE_SCHEM"));
            assertEquals("resources", rs.getString("TABLE_CATALOG"));

            assertTrue(rs.next());
            assertEquals("encrypt", rs.getString("TABLE_SCHEM"));
            assertEquals("resources", rs.getString("TABLE_CATALOG"));

            assertTrue(rs.next());
            assertEquals("fields", rs.getString("TABLE_SCHEM"));
            assertEquals("resources", rs.getString("TABLE_CATALOG"));

            assertTrue(rs.next());
            assertEquals("geog", rs.getString("TABLE_SCHEM"));
            assertEquals("resources", rs.getString("TABLE_CATALOG"));

            assertTrue(rs.next());
            assertEquals("information_schema", rs.getString("TABLE_SCHEM"));
            assertEquals("resources", rs.getString("TABLE_CATALOG"));

            assertTrue(rs.next());
            assertEquals("joins", rs.getString("TABLE_SCHEM"));
            assertEquals("resources", rs.getString("TABLE_CATALOG"));

            assertTrue(rs.next());
            assertEquals("mtdemo", rs.getString("TABLE_SCHEM"));
            assertEquals("resources", rs.getString("TABLE_CATALOG"));

            assertFalse(rs.next());
        }
    }

    /**
     * Test for schemas with catalog.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testSchemasWithCatalog() throws SQLException {
        final Properties properties = new Properties();
        properties.put(ConnectionInfo.ENABLE_CATALOG_KEY, "true");
        try (final Connection connection = DriverManager.getConnection(CONNECTION_STRING + "db", properties);
             final ResultSet rs = connection.getMetaData().getSchemas("%", "%")) {

            assertTrue(rs.next());
            assertEquals("com", rs.getString("TABLE_SCHEM"));
            assertEquals("java", rs.getString("TABLE_CATALOG"));

            assertTrue(rs.next());
            assertEquals("information_schema", rs.getString("TABLE_SCHEM"));
            assertEquals("java", rs.getString("TABLE_CATALOG"));

            assertTrue(rs.next());
            assertEquals("areas", rs.getString("TABLE_SCHEM"));
            assertEquals("resources", rs.getString("TABLE_CATALOG"));

            assertTrue(rs.next());
            assertEquals("db", rs.getString("TABLE_SCHEM"));
            assertEquals("resources", rs.getString("TABLE_CATALOG"));

            assertTrue(rs.next());
            assertEquals("encrypt", rs.getString("TABLE_SCHEM"));
            assertEquals("resources", rs.getString("TABLE_CATALOG"));

            assertTrue(rs.next());
            assertEquals("fields", rs.getString("TABLE_SCHEM"));
            assertEquals("resources", rs.getString("TABLE_CATALOG"));

            assertTrue(rs.next());
            assertEquals("geog", rs.getString("TABLE_SCHEM"));
            assertEquals("resources", rs.getString("TABLE_CATALOG"));

            assertTrue(rs.next());
            assertEquals("information_schema", rs.getString("TABLE_SCHEM"));
            assertEquals("resources", rs.getString("TABLE_CATALOG"));

            assertTrue(rs.next());
            assertEquals("joins", rs.getString("TABLE_SCHEM"));
            assertEquals("resources", rs.getString("TABLE_CATALOG"));

            assertTrue(rs.next());
            assertEquals("mtdemo", rs.getString("TABLE_SCHEM"));
            assertEquals("resources", rs.getString("TABLE_CATALOG"));

            assertFalse(rs.next());
        }
    }

    /**
     * Test for schemas with pattern.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testSchemaWithPattern() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getSchemas(this.conn.getCatalog(), "fiel%")) {
            assertTrue(rs.next());
            assertEquals("fields", rs.getString("TABLE_SCHEM"));
            assertEquals(this.conn.getCatalog(), rs.getString("TABLE_CATALOG"));

            assertFalse(rs.next());
        }
    }

    /**
     * Test for tables.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testTables() throws SQLException {
        final String[] types = {"TABLE", "VIEW"};

        try (ResultSet rs = this.conn.getMetaData().getTables(conn.getCatalog(), "%", "%", types)) {
            assertTrue(rs.next());
        }
    }

    /**
     * Test for tables in eclipse.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testTablesEclipse() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getTables(conn.getCatalog(), "fields", null, null)) {
            assertTrue(rs.next());
        }
    }

    /**
     * Test for table selectable.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testTableSelectable() throws SQLException {
        assertTrue(this.conn.getMetaData().allTablesAreSelectable());
    }

    /**
     * Test for tables with null type.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testTablesWithNullType() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getTables(conn.getCatalog(), "%", "%", null)) {
            assertInstanceOf(ParadoxResultSet.class, rs);
        }
    }

    /**
     * Test for table types.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testTableTypes() throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getTableTypes()) {
            while (rs.next()) {
                assertNotNull(TableType.valueOf(rs.getString(1).replace(' ', '_')));
            }
        }
    }

    /**
     * Test for primary key metadata.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testPrimaryKey() throws SQLException {
        final java.sql.DatabaseMetaData meta = this.conn.getMetaData();

        try (ResultSet rs = meta.getPrimaryKeys(null, "db", "CUSTOMER")) {
            assertTrue(rs.next());
            assertEquals(conn.getCatalog(), rs.getString("TABLE_CAT"));
            assertEquals("db", rs.getString("TABLE_SCHEM"));
            assertEquals("CUSTOMER", rs.getString("TABLE_NAME"));
            assertEquals("CustNo", rs.getString("COLUMN_NAME"));
            assertEquals("1", rs.getString("KEY_SEQ"));
            assertEquals("CUSTOMER.PX", rs.getString("PK_NAME"));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for primary key metadata with two keys.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testPrimaryKeyTwoKeys() throws SQLException {
        final java.sql.DatabaseMetaData meta = this.conn.getMetaData();

        try (ResultSet rs = meta.getPrimaryKeys(null, "db", "SERVER")) {
            assertTrue(rs.next());
            assertEquals(conn.getCatalog(), rs.getString("TABLE_CAT"));
            assertEquals("db", rs.getString("TABLE_SCHEM"));
            assertEquals("SERVER", rs.getString("TABLE_NAME"));
            assertEquals("REQTYPE", rs.getString("COLUMN_NAME"));
            assertEquals("1", rs.getString("KEY_SEQ"));
            assertEquals("SERVER.PX", rs.getString("PK_NAME"));
            assertTrue(rs.next());
            assertEquals(conn.getCatalog(), rs.getString("TABLE_CAT"));
            assertEquals("db", rs.getString("TABLE_SCHEM"));
            assertEquals("SERVER", rs.getString("TABLE_NAME"));
            assertEquals("URI", rs.getString("COLUMN_NAME"));
            assertEquals("2", rs.getString("KEY_SEQ"));
            assertEquals("SERVER.PX", rs.getString("PK_NAME"));
            assertFalse(rs.next());
        }
    }
}
