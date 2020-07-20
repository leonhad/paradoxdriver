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

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.ParadoxResultSet;
import com.googlecode.paradox.data.IndexData;
import com.googlecode.paradox.data.TableData;
import com.googlecode.paradox.data.ViewData;
import com.googlecode.paradox.data.filefilters.DirectoryFilter;
import com.googlecode.paradox.function.FunctionFactory;
import com.googlecode.paradox.function.IFunction;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.utils.Constants;
import com.googlecode.paradox.utils.Expressions;
import com.googlecode.paradox.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.function.Supplier;

/**
 * Creates an database metadata.
 *
 * @version 1.6
 * @since 1.0
 */
public final class ParadoxDatabaseMetaData implements DatabaseMetaData {

    /**
     * The tables cat name field.
     */
    public static final String TABLE_CAT = "TABLE_CAT";
    /**
     * The table names schema field.
     */
    public static final String TABLE_SCHEMA = "TABLE_SCHEM";
    public static final String TABLE_CATALOG = "TABLE_CATALOG";
    /**
     * The column name field.
     */
    private static final String COLUMN_NAME = "COLUMN_NAME";
    /**
     * JDBC major version.
     */
    private static final int JDBC_MAJOR_VERSION = 4;
    /**
     * JDBC minor version.
     */
    private static final int JDBC_MINOR_VERSION = 2;
    /**
     * Max field size.
     */
    private static final int MAX_INT_SIZE = 2_048;
    /**
     * Paradox major version.
     */
    private static final int PARADOX_MAJOR_VERSION = 7;
    /**
     * Paradox max column name.
     */
    private static final int PARADOX_MAX_COLUMN_NAME = 8;
    /**
     * Paradox minor version.
     */
    private static final int PARADOX_MINOR_VERSION = 0;
    /**
     * The remarks name field.
     */
    private static final String REMARKS = "REMARKS";
    /**
     * String max size.
     */
    private static final int STRING_MAX_SIZE = 255;
    /**
     * The tables field.
     */
    private static final String TABLE = "TABLE";
    /**
     * The tables name field.
     */
    private static final String TABLE_NAME = "TABLE_NAME";
    /**
     * The type name field.
     */
    private static final String TYPE_NAME = "TYPE_NAME";
    public static final int DEFAULT_NUMBER_RADIX = 10;
    public static final String SPECIFIC_NAME = "SPECIFIC_NAME";
    public static final String DATA_TYPE = "DATA_TYPE";
    public static final String NULLABLE = "NULLABLE";
    public static final String CHAR_OCTET_LENGTH = "CHAR_OCTET_LENGTH";
    public static final String ORDINAL_POSITION = "ORDINAL_POSITION";
    public static final String IS_NULLABLE = "IS_NULLABLE";
    public static final String COLUMN_TYPE = "COLUMN_TYPE";
    /**
     * The database connection.
     */
    private final ParadoxConnection conn;

    /**
     * Creates an database metadata.
     *
     * @param conn the database connection.
     */
    public ParadoxDatabaseMetaData(final ParadoxConnection conn) {
        this.conn = conn;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean allProceduresAreCallable() {
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean allTablesAreSelectable() {
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean autoCommitFailureClosesAllResultSets() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean dataDefinitionCausesTransactionCommit() {
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean dataDefinitionIgnoredInTransactions() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean deletesAreDetected(final int type) {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean doesMaxRowSizeIncludeBlobs() {
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean generatedKeyAlwaysReturned() {
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getAttributes(final String catalog, final String schemaPattern, final String typeNamePattern,
                                   final String attributeNamePattern) {
        return new ParadoxResultSet(this.conn, null, Collections.emptyList(), Collections.emptyList());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getBestRowIdentifier(final String catalog, final String schema, final String table,
                                          final int scope, final boolean nullable) {
        return new ParadoxResultSet(this.conn, null, Collections.emptyList(), Collections.emptyList());
    }

    private static void getPrimaryKeyIndex(String catalog, List<Object[]> values, File currentSchema,
                                           ParadoxTable table) {
        for (final ParadoxField pk : table.getPrimaryKeys()) {
            final Object[] row = new Object[]{
                    catalog,
                    currentSchema.getName(),
                    table.getName(),
                    Boolean.FALSE,
                    catalog,
                    table.getName() + ".PX",
                    DatabaseMetaData.tableIndexHashed,
                    pk.getOrderNum() - 1,
                    pk.getName(),
                    "A",
                    0,
                    0,
                    null
            };

            values.add(row);
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getCatalogSeparator() {
        return ".";
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getCatalogTerm() {
        return "CATALOG";
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getClientInfoProperties() {
        return new ParadoxResultSet(this.conn, null, Collections.emptyList(), Collections.emptyList());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getColumnPrivileges(final String catalog, final String schema, final String table,
                                         final String columnNamePattern) {
        return new ParadoxResultSet(this.conn, null, Collections.emptyList(), Collections.emptyList());
    }

    /**
     * Format a single row metadata.
     *
     * @param name the row name.
     * @param type the row type.
     * @return the row.
     */
    private static Object[] formatRow(final String name, final String type, final String catalog,
                                      final String schema) {
        return new Object[]{
                catalog,
                schema,
                name,
                type,
                null,
                null,
                null,
                null,
                null,
                null
        };
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Connection getConnection() {
        return this.conn;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getCrossReference(final String primaryCatalog, final String primarySchema,
                                       final String primaryTable, final String foreignCatalog,
                                       final String foreignSchema,
                                       final String foreignTable) {
        return new ParadoxResultSet(this.conn, null, Collections.emptyList(), Collections.emptyList());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getDatabaseMajorVersion() {
        return ParadoxDatabaseMetaData.PARADOX_MAJOR_VERSION;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getDatabaseMinorVersion() {
        return ParadoxDatabaseMetaData.PARADOX_MINOR_VERSION;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getDatabaseProductName() {
        return Constants.DRIVER_NAME;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getDatabaseProductVersion() {
        return Constants.DRIVER_NAME + " " + Constants.DRIVER_VERSION;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getDefaultTransactionIsolation() {
        return Connection.TRANSACTION_NONE;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getDriverMajorVersion() {
        return Constants.MAJOR_VERSION;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getDriverMinorVersion() {
        return Constants.MINOR_VERSION;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getDriverName() {
        return Constants.DRIVER_NAME;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getDriverVersion() {
        return Constants.DRIVER_VERSION;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getExportedKeys(final String catalog, final String schema, final String table) {
        return new ParadoxResultSet(this.conn, null, Collections.emptyList(), Collections.emptyList());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getExtraNameCharacters() {
        return "";
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getFunctionColumns(final String catalog, final String schemaPattern,
                                        final String functionNamePattern, final String columnNamePattern) throws SQLException {
        final ArrayList<Column> columns = new ArrayList<>();
        columns.add(new Column("FUNCTION_CAT", Types.VARCHAR));
        columns.add(new Column("FUNCTION_SCHEM", Types.VARCHAR));
        columns.add(new Column("FUNCTION_NAME", Types.VARCHAR));
        columns.add(new Column(COLUMN_NAME, Types.VARCHAR));
        columns.add(new Column(COLUMN_TYPE, Types.INTEGER));
        columns.add(new Column(DATA_TYPE, Types.INTEGER));
        columns.add(new Column(TYPE_NAME, Types.VARCHAR));
        columns.add(new Column("PRECISION", Types.INTEGER));
        columns.add(new Column("LENGTH", Types.INTEGER));
        columns.add(new Column("SCALE", Types.INTEGER));
        columns.add(new Column("RADIX", Types.INTEGER));
        columns.add(new Column(NULLABLE, Types.INTEGER));
        columns.add(new Column(REMARKS, Types.VARCHAR));
        columns.add(new Column(CHAR_OCTET_LENGTH, Types.INTEGER));
        columns.add(new Column(ORDINAL_POSITION, Types.INTEGER));
        columns.add(new Column(IS_NULLABLE, Types.VARCHAR));
        columns.add(new Column(SPECIFIC_NAME, Types.VARCHAR));

        final List<Object[]> values = new ArrayList<>();

        for (final Map.Entry<String, Supplier<? extends IFunction>> function : FunctionFactory.FUNCTIONS.entrySet()) {
            if ((catalog != null && !catalog.equalsIgnoreCase(conn.getCatalog()))
                //    || (schemaPattern != null && !schemaPattern.isEmpty())
                    || (functionNamePattern != null && !functionNamePattern.equalsIgnoreCase(function.getKey()))) {
               // continue;
            }

            final IFunction instance = function.getValue().get();
            for (final Column column : instance.getColumns()) {
                final Object[] row = {
                        // Catalog.
                        conn.getCatalog(),
                        // Schema.
                        schemaPattern,
                        // Name.
                        function.getKey(),
                        // Column name.
                        column.getName(),
                        // Column type.
                        functionColumnIn,
                        // Data type.
                        column.getType(),
                        // Data type name.
                        JDBCType.valueOf(column.getType()).getName(),
                        // Precision.
                        column.getPrecision(),
                        // Length,
                        column.getSize(),
                        // Scale.
                        column.getScale(),
                        // Radix.
                        10,
                        // Nullable
                        column.isNullable() ? functionNullable : functionNoNulls,
                        // Remarks.
                        column.getRemarks(),
                        // Octets.
                        column.getOctets(),
                        // Ordinal position.
                        column.getIndex(),
                        // Is nullable.
                        column.isNullable() ? "YES" : "NO",
                        // Specific name.
                        column.getName()
                };

                values.add(row);
            }
        }

        return new ParadoxResultSet(this.conn, null, values, columns);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getFunctions(final String catalog, final String schemaPattern, final String functionNamePattern) {
        final ArrayList<Column> columns = new ArrayList<>();
        columns.add(new Column("FUNCTION_CAT", Types.VARCHAR));
        columns.add(new Column("FUNCTION_SCHEM", Types.VARCHAR));
        columns.add(new Column("FUNCTION_NAME", Types.VARCHAR));
        columns.add(new Column(REMARKS, Types.VARCHAR));
        columns.add(new Column("FUNCTION_TYPE", Types.INTEGER));
        columns.add(new Column(SPECIFIC_NAME, Types.VARCHAR));

        final List<Object[]> values = new ArrayList<>();

        for (final Map.Entry<String, Supplier<? extends IFunction>> function : FunctionFactory.FUNCTIONS.entrySet()) {
            if ((catalog != null && !catalog.equalsIgnoreCase(conn.getCatalog()))
            //        || (schemaPattern != null && !schemaPattern.isEmpty())
            ) {
                continue;
            }

            final IFunction instance = function.getValue().get();

            final Object[] row = {
                    // Catalog.
                    conn.getCatalog(),
                    // Schema.
                    null,
                    // Name.
                    function.getKey(),
                    // Remarks.
                    instance.remarks(),
                    // Type.
                    functionNoTable,
                    // Specific name.
                    function.getKey()
            };

            values.add(row);
        }

        return new ParadoxResultSet(this.conn, null, values, columns);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getIdentifierQuoteString() {
        return "\"";
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getImportedKeys(final String catalog, final String schema, final String table) {
        // FIXME redo this primary keys metadata.
        return new ParadoxResultSet(this.conn, null, Collections.emptyList(), Collections.emptyList());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getCatalogs() {
        final List<Column> columns = Collections.singletonList(
                new Column(ParadoxDatabaseMetaData.TABLE_CAT, Types.VARCHAR));

        final List<Object[]> values = Collections.singletonList(
                new Object[]{this.conn.getCatalog()});

        return new ParadoxResultSet(this.conn, null, values, columns);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getColumns(final String catalog, final String schemaPattern, final String tableNamePattern,
                                final String columnNamePattern) throws SQLException {
        final ArrayList<Column> columns = new ArrayList<>();
        columns.add(new Column(ParadoxDatabaseMetaData.TABLE_CAT, Types.VARCHAR));
        columns.add(new Column(ParadoxDatabaseMetaData.TABLE_SCHEMA, Types.VARCHAR));
        columns.add(new Column(ParadoxDatabaseMetaData.TABLE_NAME, Types.VARCHAR));
        columns.add(new Column(ParadoxDatabaseMetaData.COLUMN_NAME, Types.VARCHAR));
        columns.add(new Column(DATA_TYPE, Types.INTEGER));
        columns.add(new Column(ParadoxDatabaseMetaData.TYPE_NAME, Types.VARCHAR));
        columns.add(new Column("COLUMN_SIZE", Types.INTEGER));
        columns.add(new Column("BUFFER_LENGTH", Types.INTEGER));
        columns.add(new Column("DECIMAL_DIGITS", Types.INTEGER));
        columns.add(new Column("NUM_PREC_RADIX", Types.INTEGER));
        columns.add(new Column(NULLABLE, Types.INTEGER));
        columns.add(new Column(ParadoxDatabaseMetaData.REMARKS, Types.INTEGER));
        columns.add(new Column("COLUMN_DEF", Types.VARCHAR));
        columns.add(new Column("SQL_DATA_TYPE", Types.INTEGER));
        columns.add(new Column("SQL_DATETIME_SUB", Types.INTEGER));
        columns.add(new Column(CHAR_OCTET_LENGTH, Types.INTEGER));
        columns.add(new Column(ORDINAL_POSITION, Types.INTEGER));
        columns.add(new Column(IS_NULLABLE, Types.INTEGER));
        columns.add(new Column("SCOPE_CATLOG", Types.VARCHAR));
        columns.add(new Column("SCOPE_SCHEMA", Types.VARCHAR));
        columns.add(new Column("SCOPE_TABLE", Types.VARCHAR));
        columns.add(new Column("SOURCE_DATA_TYPE", Types.SMALLINT));
        columns.add(new Column("IS_AUTOINCREMENT", Types.VARCHAR));

        final List<Object[]> values = new ArrayList<>();

        for (final File currentSchema : this.conn.getSchema(catalog, schemaPattern)) {
            final List<ParadoxTable> tables = TableData.listTables(currentSchema, tableNamePattern, this.conn);
            for (final ParadoxTable table : tables) {
                this.fieldMetadata(catalog, currentSchema.getName(), columnNamePattern, values, table.getName(),
                        table.getFields());
            }

            final List<ParadoxView> views = ViewData.listViews(currentSchema, tableNamePattern, this.conn);
            for (final ParadoxView view : views) {
                this.fieldMetadata(catalog, currentSchema.getName(), columnNamePattern, values, view.getName(),
                        view.getFields());
            }
        }

        return new ParadoxResultSet(this.conn, null, values, columns);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getIndexInfo(final String catalog, final String schema, final String tableNamePattern,
                                  final boolean unique, final boolean approximate) throws SQLException {
        final ArrayList<Column> columns = new ArrayList<>();
        columns.add(new Column(ParadoxDatabaseMetaData.TABLE_CAT, Types.VARCHAR));
        columns.add(new Column(ParadoxDatabaseMetaData.TABLE_SCHEMA, Types.VARCHAR));
        columns.add(new Column(ParadoxDatabaseMetaData.TABLE_NAME, Types.VARCHAR));
        columns.add(new Column("NON_UNIQUE", Types.BOOLEAN));
        columns.add(new Column("INDEX_QUALIFIER", Types.VARCHAR));
        columns.add(new Column("INDEX_NAME", Types.VARCHAR));
        columns.add(new Column("TYPE", Types.INTEGER));
        columns.add(new Column(ORDINAL_POSITION, Types.INTEGER));
        columns.add(new Column(ParadoxDatabaseMetaData.COLUMN_NAME, Types.VARCHAR));
        columns.add(new Column("ASC_OR_DESC", Types.VARCHAR));
        columns.add(new Column("CARDINALITY", Types.INTEGER));
        columns.add(new Column("PAGES", Types.INTEGER));
        columns.add(new Column("FILTER_CONDITION", Types.VARCHAR));

        final List<Object[]> values = new ArrayList<>();

        for (final File currentSchema : this.conn.getSchema(catalog, schema)) {
            for (final ParadoxTable table : TableData.listTables(currentSchema, tableNamePattern, this.conn)) {
                getPrimaryKeyIndex(catalog, values, currentSchema, table);
                getSecondaryIndexInfo(catalog, table, values, currentSchema);
            }
        }
        return new ParadoxResultSet(this.conn, null, values, columns);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getJDBCMajorVersion() {
        return ParadoxDatabaseMetaData.JDBC_MAJOR_VERSION;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getJDBCMinorVersion() {
        return ParadoxDatabaseMetaData.JDBC_MINOR_VERSION;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMaxBinaryLiteralLength() {
        return ParadoxDatabaseMetaData.PARADOX_MAX_COLUMN_NAME;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMaxCatalogNameLength() {
        return ParadoxDatabaseMetaData.STRING_MAX_SIZE;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMaxCharLiteralLength() {
        return ParadoxDatabaseMetaData.STRING_MAX_SIZE;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMaxColumnNameLength() {
        return ParadoxDatabaseMetaData.PARADOX_MAX_COLUMN_NAME;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMaxColumnsInGroupBy() {
        return ParadoxDatabaseMetaData.STRING_MAX_SIZE;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMaxColumnsInIndex() {
        return ParadoxDatabaseMetaData.STRING_MAX_SIZE;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMaxColumnsInOrderBy() {
        return ParadoxDatabaseMetaData.STRING_MAX_SIZE;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMaxColumnsInSelect() {
        return ParadoxDatabaseMetaData.STRING_MAX_SIZE;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMaxColumnsInTable() {
        return ParadoxDatabaseMetaData.STRING_MAX_SIZE;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMaxConnections() {
        return 1;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMaxCursorNameLength() {
        return ParadoxDatabaseMetaData.STRING_MAX_SIZE;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMaxIndexLength() {
        return ParadoxDatabaseMetaData.STRING_MAX_SIZE;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMaxProcedureNameLength() {
        return ParadoxDatabaseMetaData.STRING_MAX_SIZE;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMaxRowSize() {
        return ParadoxDatabaseMetaData.STRING_MAX_SIZE;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMaxSchemaNameLength() {
        return ParadoxDatabaseMetaData.STRING_MAX_SIZE;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMaxStatementLength() {
        return Integer.MAX_VALUE;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMaxStatements() {
        return Integer.MAX_VALUE;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMaxTableNameLength() {
        return ParadoxDatabaseMetaData.STRING_MAX_SIZE;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMaxTablesInSelect() {
        return ParadoxDatabaseMetaData.STRING_MAX_SIZE;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMaxUserNameLength() {
        return ParadoxDatabaseMetaData.STRING_MAX_SIZE;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getNumericFunctions() {
        return "AVERAGE,SUM";
    }

    private void getSecondaryIndexInfo(String catalog, ParadoxTable table, List<Object[]> values, File currentSchema)
            throws SQLException {
        for (final ParadoxIndex index : IndexData.listIndexes(currentSchema, table.getName(), this.conn)) {
            for (int loop = 0; loop < index.getFieldCount() - index.getPrimaryFieldCount(); loop++) {
                final ParadoxField field = index.getFields()[0];
                final Object[] row = new Object[]{
                        catalog,
                        currentSchema.getName(),
                        table.getName(),
                        !index.isUnique(),
                        catalog,
                        index.getName(),
                        DatabaseMetaData.tableIndexHashed,
                        field.getOrderNum() - 1,
                        field.getName(),
                        index.getOrder(),
                        0,
                        0,
                        null
                };

                values.add(row);
            }
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getPrimaryKeys(final String catalog, final String schema, final String tableNamePattern) throws
            SQLException {
        final List<Column> columns = new ArrayList<>();
        columns.add(new Column(ParadoxDatabaseMetaData.TABLE_CAT, Types.VARCHAR));
        columns.add(new Column(ParadoxDatabaseMetaData.TABLE_SCHEMA, Types.VARCHAR));
        columns.add(new Column(ParadoxDatabaseMetaData.TABLE_NAME, Types.VARCHAR));
        columns.add(new Column(ParadoxDatabaseMetaData.COLUMN_NAME, Types.VARCHAR));
        columns.add(new Column("KEY_SEQ", Types.INTEGER));
        columns.add(new Column("PK_NAME", Types.VARCHAR));

        final List<Object[]> values = new ArrayList<>();

        for (final File currentSchema : this.conn.getSchema(catalog, schema)) {
            for (final ParadoxTable table : TableData.listTables(currentSchema, tableNamePattern, this.conn)) {
                for (final ParadoxField pk : table.getPrimaryKeys()) {
                    final Object[] row = new Object[]{
                            catalog,
                            currentSchema.getName(),
                            table.getName(),
                            pk.getName(),
                            pk.getOrderNum() - 1,
                            table.getName() + ".PX",
                    };
                    values.add(row);
                }
            }
        }
        return new ParadoxResultSet(this.conn, null, values, columns);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getProcedureColumns(final String catalog, final String schemaPattern,
                                         final String procedureNamePattern, final String columnNamePattern) {
        final ArrayList<Column> columns = new ArrayList<>();
        columns.add(new Column("PROCEDURE_CAT", Types.VARCHAR));
        columns.add(new Column("PROCEDURE_SCHEM", Types.VARCHAR));
        columns.add(new Column("PROCEDURE_NAME", Types.VARCHAR));
        columns.add(new Column(ParadoxDatabaseMetaData.COLUMN_NAME, Types.VARCHAR));
        columns.add(new Column(COLUMN_TYPE, Types.INTEGER));
        columns.add(new Column(DATA_TYPE, Types.INTEGER));
        columns.add(new Column(ParadoxDatabaseMetaData.TYPE_NAME, Types.VARCHAR));
        columns.add(new Column("PRECISION", Types.INTEGER));
        columns.add(new Column("LENGTH", Types.INTEGER));
        columns.add(new Column("SCALE", Types.INTEGER));
        columns.add(new Column("RADIX", Types.INTEGER));
        columns.add(new Column(NULLABLE, Types.INTEGER));
        columns.add(new Column(ParadoxDatabaseMetaData.REMARKS, Types.VARCHAR));
        columns.add(new Column("COLUMN_DEF", Types.VARCHAR));
        columns.add(new Column("SQL_DATA_TYPE", Types.VARCHAR));
        columns.add(new Column("SQL_DATETIME_SUB", Types.VARCHAR));
        columns.add(new Column(CHAR_OCTET_LENGTH, Types.VARCHAR));
        columns.add(new Column(IS_NULLABLE, Types.VARCHAR));
        columns.add(new Column(SPECIFIC_NAME, Types.VARCHAR));

        return new ParadoxResultSet(this.conn, null, Collections.emptyList(), columns);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getProcedureTerm() {
        return "PROCEDURE";
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getPseudoColumns(final String catalog, final String schemaPattern, final String tableNamePattern,
                                      final String columnNamePattern) {
        return new ParadoxResultSet(this.conn, null, Collections.emptyList(), Collections.emptyList());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getResultSetHoldability() {
        return this.conn.getHoldability();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public RowIdLifetime getRowIdLifetime() {
        return RowIdLifetime.ROWID_UNSUPPORTED;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getProcedures(final String catalog, final String schemaPattern,
                                   final String procedureNamePattern) {
        final ArrayList<Column> columns = new ArrayList<>(1);
        columns.add(new Column("PROCEDURE_CAT", Types.VARCHAR));
        columns.add(new Column("PROCEDURE_SCHEM", Types.VARCHAR));
        columns.add(new Column("PROCEDURE_NAME", Types.VARCHAR));
        columns.add(new Column("Reserved1", Types.VARCHAR));
        columns.add(new Column("Reserved2", Types.VARCHAR));
        columns.add(new Column("Reserved3", Types.VARCHAR));
        columns.add(new Column(ParadoxDatabaseMetaData.REMARKS, Types.VARCHAR));
        columns.add(new Column("PROCEDURE_TYPE", Types.INTEGER));
        columns.add(new Column(SPECIFIC_NAME, Types.VARCHAR));

        return new ParadoxResultSet(this.conn, null, Collections.emptyList(), columns);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getSchemas(final String catalog, final String schemaPattern) {
        if (((catalog != null)
                && !Expressions.accept(conn.getLocale(), this.conn.getCatalog(), catalog, false, Constants.ESCAPE_CHAR))
                || ((schemaPattern != null)
                && !Expressions.accept(conn.getLocale(), this.conn.getSchema(), schemaPattern, false,
                Constants.ESCAPE_CHAR))) {
            return new ParadoxResultSet(this.conn, null, Collections.emptyList(), Collections.emptyList());
        }
        return this.getSchemas();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getSchemaTerm() {
        return "SCHEMA";
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getSearchStringEscape() {
        return Character.toString(Constants.ESCAPE_CHAR);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getSQLKeywords() {
        return "ILIKE";
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getSQLStateType() {
        return DatabaseMetaData.sqlStateSQL;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getStringFunctions() {
        return "";
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getSuperTables(final String catalog, final String schemaPattern, final String tableNamePattern) {
        return new ParadoxResultSet(this.conn, null, Collections.emptyList(), Collections.emptyList());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getSuperTypes(final String catalog, final String schemaPattern, final String typeNamePattern) {
        return new ParadoxResultSet(this.conn, null, Collections.emptyList(), Collections.emptyList());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getSystemFunctions() {
        return "";
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getTablePrivileges(final String catalog, final String schemaPattern,
                                        final String tableNamePattern) {
        return new ParadoxResultSet(this.conn, null, Collections.emptyList(), Collections.emptyList());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getSchemas() {
        final ArrayList<Column> columns = new ArrayList<>();
        columns.add(new Column(TABLE_SCHEMA, Types.VARCHAR));
        columns.add(new Column(TABLE_CATALOG, Types.VARCHAR));

        final List<Object[]> values = new ArrayList<>();

        final File catalog = conn.getCurrentCatalog();
        final File[] schemas = catalog.listFiles(new DirectoryFilter(conn.getLocale()));

        if (schemas != null) {
            Arrays.sort(schemas);
            for (final File schema : schemas) {
                final Object[] row = new Object[]{
                        schema.getName(),
                        catalog.getName(),
                };
                values.add(row);
            }
        }

        return new ParadoxResultSet(this.conn, null, values, columns);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getTables(final String catalog, final String schemaPattern, final String tableNamePattern,
                               final String[] types) throws SQLException {
        final ArrayList<Column> columns = new ArrayList<>();
        columns.add(new Column(ParadoxDatabaseMetaData.TABLE_CAT, Types.VARCHAR));
        columns.add(new Column(ParadoxDatabaseMetaData.TABLE_SCHEMA, Types.VARCHAR));
        columns.add(new Column(TABLE_NAME, Types.VARCHAR));
        columns.add(new Column("TABLE_TYPE", Types.VARCHAR));
        columns.add(new Column(REMARKS, Types.VARCHAR));
        columns.add(new Column("TYPE_CAT", Types.VARCHAR));
        columns.add(new Column("TYPE_SCHEM", Types.VARCHAR));
        columns.add(new Column(TYPE_NAME, Types.VARCHAR));
        columns.add(new Column("SELF_REFERENCING_COL_NAME", Types.VARCHAR));
        columns.add(new Column("REF_GENERATION", Types.VARCHAR));

        final List<Object[]> values = new ArrayList<>();
        if (types == null) {
            return new ParadoxResultSet(this.conn, null, values, columns);
        }
        for (final File currentSchema : this.conn.getSchema(catalog, schemaPattern)) {
            for (final String type : types) {
                if (ParadoxDatabaseMetaData.TABLE.equalsIgnoreCase(type)) {
                    this.formatTable(catalog, schemaPattern, tableNamePattern, values);
                } else if ("VIEW".equalsIgnoreCase(type)) {
                    this.formatView(tableNamePattern, values, currentSchema);
                }
            }
        }

        return new ParadoxResultSet(this.conn, null, values, columns);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getTimeDateFunctions() {
        return "";
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getTypeInfo() {
        return new ParadoxResultSet(this.conn, null, Collections.emptyList(), Collections.emptyList());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getUDTs(final String catalog, final String schemaPattern, final String typeNamePattern,
                             final int[] types) {
        return new ParadoxResultSet(this.conn, null, Collections.emptyList(), Collections.emptyList());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getURL() {
        return this.conn.getUrl();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getUserName() {
        return "SYSTEM";
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getVersionColumns(final String catalog, final String schema, final String table) {
        return new ParadoxResultSet(this.conn, null, Collections.emptyList(), Collections.emptyList());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean insertsAreDetected(final int type) {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean isCatalogAtStart() {
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean isReadOnly() {
        return true;

    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean isWrapperFor(final Class<?> iFace) {
        return Utils.isWrapperFor(this, iFace);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean locatorsUpdateCopy() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean nullPlusNonNullIsNull() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean nullsAreSortedAtEnd() {
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean nullsAreSortedAtStart() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean nullsAreSortedHigh() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean nullsAreSortedLow() {
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean othersDeletesAreVisible(final int type) {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean othersInsertsAreVisible(final int type) {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean othersUpdatesAreVisible(final int type) {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean ownDeletesAreVisible(final int type) {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean ownInsertsAreVisible(final int type) {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean ownUpdatesAreVisible(final int type) {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean storesLowerCaseIdentifiers() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean storesLowerCaseQuotedIdentifiers() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean storesMixedCaseIdentifiers() {
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean storesMixedCaseQuotedIdentifiers() {
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean storesUpperCaseIdentifiers() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean storesUpperCaseQuotedIdentifiers() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsAlterTableWithAddColumn() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsAlterTableWithDropColumn() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsANSI92EntryLevelSQL() {
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsANSI92FullSQL() {
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsANSI92IntermediateSQL() {
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsBatchUpdates() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsCatalogsInDataManipulation() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsCatalogsInIndexDefinitions() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsCatalogsInPrivilegeDefinitions() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsCatalogsInProcedureCalls() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsCatalogsInTableDefinitions() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsColumnAliasing() {
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsConvert() {
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsConvert(final int fromType, final int toType) {
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsCoreSQLGrammar() {
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsCorrelatedSubqueries() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsDataDefinitionAndDataManipulationTransactions() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsDataManipulationTransactionsOnly() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsDifferentTableCorrelationNames() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsExpressionsInOrderBy() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsExtendedSQLGrammar() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsFullOuterJoins() {
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsGetGeneratedKeys() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsGroupBy() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsGroupByBeyondSelect() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsGroupByUnrelated() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsIntegrityEnhancementFacility() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsLikeEscapeClause() {
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsLimitedOuterJoins() {
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsMinimumSQLGrammar() {
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsMixedCaseIdentifiers() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsMixedCaseQuotedIdentifiers() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsMultipleOpenResults() {
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsMultipleResultSets() {
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsMultipleTransactions() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsNamedParameters() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsNonNullableColumns() {
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsOpenCursorsAcrossCommit() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsOpenCursorsAcrossRollback() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsOpenStatementsAcrossCommit() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsOpenStatementsAcrossRollback() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsOrderByUnrelated() {
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsOuterJoins() {
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsPositionedDelete() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsPositionedUpdate() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsResultSetConcurrency(final int type, final int concurrency) {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsResultSetHoldability(final int holdability) {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsResultSetType(final int type) {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsSavepoints() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsSchemasInDataManipulation() {
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsSchemasInIndexDefinitions() {
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsSchemasInPrivilegeDefinitions() {
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsSchemasInProcedureCalls() {
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsSchemasInTableDefinitions() {
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsSelectForUpdate() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsStatementPooling() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsStoredFunctionsUsingCallSyntax() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsStoredProcedures() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsSubqueriesInComparisons() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsSubqueriesInExists() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsSubqueriesInIns() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsSubqueriesInQuantifieds() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsTableCorrelationNames() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsTransactionIsolationLevel(final int level) {
        return Connection.TRANSACTION_NONE != level;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsTransactions() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsUnion() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsUnionAll() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public <T> T unwrap(final Class<T> iFace) throws SQLException {
        return Utils.unwrap(this, iFace);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean updatesAreDetected(final int type) {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean usesLocalFilePerTable() {
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean usesLocalFiles() {
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getTableTypes() {
        final List<Column> columns = Collections.singletonList(new Column("TABLE_TYPE", Types.VARCHAR));

        final List<Object[]> values = new ArrayList<>(4);
        values.add(new Object[]{ParadoxDatabaseMetaData.TABLE});
        values.add(new Object[]{"VIEW"});
        values.add(new Object[]{"SYSTEM TABLE"});
        return new ParadoxResultSet(this.conn, null, values, columns);
    }

    /**
     * Gets fields metadata.
     *
     * @param catalog           the catalog name.
     * @param schema            the schema name.
     * @param columnNamePattern column pattern to search of.
     * @param values            the table values.
     * @param tableName         the table name.
     * @param fields            the field list.
     */
    private void fieldMetadata(final String catalog, final String schema, final String columnNamePattern,
                               final List<Object[]> values,
                               final String tableName, final ParadoxField[] fields) {
        int ordinal = 1;
        for (final ParadoxField field : fields) {
            if ((columnNamePattern != null) && !Expressions.accept(conn.getLocale(), field.getName(),
                    columnNamePattern, false, Constants.ESCAPE_CHAR)) {
                continue;
            }

            final List<Object> row = new ArrayList<>();

            final int type = field.getSqlType();
            // Table catalog.
            row.add(catalog);
            // Table schema.
            row.add(schema);
            // Table name.
            row.add(tableName);
            // Column name.
            row.add(field.getAlias());
            // Data type.
            row.add(type);
            // Type name.
            row.add(JDBCType.valueOf(type).getName());
            // Column size.
            row.add(field.getSize());
            // Buffer length.
            row.add(ParadoxDatabaseMetaData.MAX_INT_SIZE);
            // Decimal digits.
            row.add(field.getPrecision());
            // Number precision radix.
            row.add(DEFAULT_NUMBER_RADIX);
            // Nullable.
            if (field.isAutoIncrement()) {
                row.add(DatabaseMetaData.columnNoNulls);
            } else {
                row.add(DatabaseMetaData.columnNullable);
            }
            // Column remarks.
            row.add(null);
            // Column default value.
            row.add(null);
            // Column SQL data type.
            row.add(field.getSqlType());
            // Subtype code for datetime and SQL-92 interval data types. For other data types, this column returns NULL.
            row.add(null);
            // Column type in byte octets.
            row.add(field.getSize());
            // Ordinal position.
            row.add(ordinal);
            // Is field nullable.
            if (field.isAutoIncrement()) {
                row.add("NO");
            } else {
                row.add("YES");
            }
            // Scope catalog.
            row.add(null);
            // Scope schema.
            row.add(null);
            // Scope table.
            row.add(null);
            // Source datatype.
            row.add(type);

            // Is autoincrement.
            if (field.isAutoIncrement()) {
                row.add("YES");
            } else {
                row.add("NO");
            }

            ordinal++;
            values.add(row.toArray(new Object[0]));
        }
    }

    /**
     * Format a table metadata.
     *
     * @param schemaPattern    the schema pattern.
     * @param tableNamePattern the table name pattern.
     * @param values           the field values.
     * @throws SQLException in case of errors.
     */
    private void formatTable(final String catalog, final String schemaPattern, final String tableNamePattern,
                             final List<Object[]> values) throws SQLException {
        for (final File schema : this.conn.getSchema(catalog, schemaPattern)) {
            for (final ParadoxTable table : TableData.listTables(schema, tableNamePattern, this.conn)) {
                values.add(formatRow(table.getName(), TABLE, catalog, schema.getName()));
            }
        }
    }

    /**
     * Format a table view metadata.
     *
     * @param tableNamePattern the table view name pattern.
     * @param values           the table values.
     * @param currentSchema    the current schema file.
     * @throws SQLException in case of errors.
     */
    private void formatView(final String tableNamePattern, final List<Object[]> values,
                            final File currentSchema) throws SQLException {
        for (final ParadoxView view : ViewData.listViews(currentSchema, tableNamePattern, this.conn)) {
            values.add(formatRow(view.getName(), "VIEW", this.conn.getCatalog(), currentSchema.getName()));
        }
    }
}
