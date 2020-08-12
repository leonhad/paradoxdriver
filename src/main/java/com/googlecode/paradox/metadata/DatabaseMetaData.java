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

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.ParadoxResultSet;
import com.googlecode.paradox.exceptions.ParadoxDataException;
import com.googlecode.paradox.function.AbstractFunction;
import com.googlecode.paradox.function.FunctionFactory;
import com.googlecode.paradox.function.FunctionType;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.utils.Constants;
import com.googlecode.paradox.utils.Expressions;
import com.googlecode.paradox.utils.Utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Creates an database metadata.
 *
 * @version 1.7
 * @since 1.0
 */
public final class DatabaseMetaData implements java.sql.DatabaseMetaData {

    /**
     * The tables cat name field.
     */
    public static final String TABLE_CAT = "TABLE_CAT";
    /**
     * The table names schema field.
     */
    public static final String TABLE_SCHEMA = "TABLE_SCHEM";
    public static final String TABLE_CATALOG = "TABLE_CATALOG";
    public static final String SPECIFIC_NAME = "SPECIFIC_NAME";
    public static final String DATA_TYPE = "DATA_TYPE";
    public static final String NULLABLE = "NULLABLE";
    public static final String CHAR_OCTET_LENGTH = "CHAR_OCTET_LENGTH";
    public static final String ORDINAL_POSITION = "ORDINAL_POSITION";
    public static final String IS_NULLABLE = "IS_NULLABLE";
    public static final String COLUMN_TYPE = "COLUMN_TYPE";
    public static final String TYPE_SCHEM = "TYPE_SCHEM";
    public static final String VIEW = "VIEW";
    public static final String SYSTEM_TABLE = "SYSTEM TABLE";
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
    /**
     * The connection information.
     */
    private final ConnectionInfo connectionInfo;
    /**
     * The Paradox connection.
     */
    private final ParadoxConnection connection;

    /**
     * Creates an database metadata.
     *
     * @param connection the Paradox connection.
     */
    public DatabaseMetaData(final ParadoxConnection connection) {
        this.connection = connection;
        this.connectionInfo = connection.getConnectionInfo();
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
        return new ParadoxResultSet(this.connectionInfo, null, Collections.emptyList(), Collections.emptyList());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getBestRowIdentifier(final String catalog, final String schema, final String table,
                                          final int scope, final boolean nullable) {
        return new ParadoxResultSet(this.connectionInfo, null, Collections.emptyList(), Collections.emptyList());
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
        return new ParadoxResultSet(this.connectionInfo, null, Collections.emptyList(), Collections.emptyList());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getColumnPrivileges(final String catalog, final String schema, final String table,
                                         final String columnNamePattern) {
        return new ParadoxResultSet(this.connectionInfo, null, Collections.emptyList(), Collections.emptyList());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Connection getConnection() {
        return this.connection;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getCrossReference(final String primaryCatalog, final String primarySchema,
                                       final String primaryTable, final String foreignCatalog,
                                       final String foreignSchema,
                                       final String foreignTable) {
        return new ParadoxResultSet(this.connectionInfo, null, Collections.emptyList(), Collections.emptyList());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getDatabaseMajorVersion() {
        return DatabaseMetaData.PARADOX_MAJOR_VERSION;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getDatabaseMinorVersion() {
        return DatabaseMetaData.PARADOX_MINOR_VERSION;
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
        return new ParadoxResultSet(this.connectionInfo, null, Collections.emptyList(), Collections.emptyList());
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
                                        final String functionNamePattern, final String columnNamePattern) {
        final ArrayList<Column> columns = new ArrayList<>();
        columns.add(new Column("FUNCTION_CAT", ParadoxType.VARCHAR));
        columns.add(new Column("FUNCTION_SCHEM", ParadoxType.VARCHAR));
        columns.add(new Column("FUNCTION_NAME", ParadoxType.VARCHAR));
        columns.add(new Column(COLUMN_NAME, ParadoxType.VARCHAR));
        columns.add(new Column(COLUMN_TYPE, ParadoxType.INTEGER));
        columns.add(new Column(DATA_TYPE, ParadoxType.INTEGER));
        columns.add(new Column(TYPE_NAME, ParadoxType.VARCHAR));
        columns.add(new Column("PRECISION", ParadoxType.INTEGER));
        columns.add(new Column("LENGTH", ParadoxType.INTEGER));
        columns.add(new Column("SCALE", ParadoxType.INTEGER));
        columns.add(new Column("RADIX", ParadoxType.INTEGER));
        columns.add(new Column(NULLABLE, ParadoxType.INTEGER));
        columns.add(new Column(REMARKS, ParadoxType.VARCHAR));
        columns.add(new Column(CHAR_OCTET_LENGTH, ParadoxType.INTEGER));
        columns.add(new Column(ORDINAL_POSITION, ParadoxType.INTEGER));
        columns.add(new Column(IS_NULLABLE, ParadoxType.VARCHAR));
        columns.add(new Column(SPECIFIC_NAME, ParadoxType.VARCHAR));

        final List<Object[]> values = new ArrayList<>();

        for (final Map.Entry<String, Supplier<? extends AbstractFunction>> function :
                FunctionFactory.getFunctions().entrySet()) {
            if ((catalog != null && !catalog.equalsIgnoreCase(connectionInfo.getCatalog()))
                    && (schemaPattern != null
                    && !Expressions.accept(connectionInfo.getLocale(), connectionInfo.getCurrentSchema().name(),
                    schemaPattern, false, '\\'))
                    && (functionNamePattern != null && !Expressions.accept(connectionInfo.getLocale(),
                    function.getKey(),
                    functionNamePattern, false, '\\'))) {
                continue;
            }

            final AbstractFunction instance = function.getValue().get();
            for (final Column column : instance.getColumns()) {
                if (columnNamePattern != null
                        && !Expressions.accept(connectionInfo.getLocale(), column.getName(), columnNamePattern, false
                        , '\\')) {
                    continue;
                }

                final Object[] row = {
                        // Catalog.
                        connectionInfo.getCatalog(),
                        // Schema.
                        null,
                        // Name.
                        function.getKey(),
                        // Column name.
                        column.getName(),
                        // Column type.
                        column.getColumnType(),
                        // Data type.
                        column.getType().getSQLType(),
                        // Data type name.
                        column.getType().getName(),
                        // Precision.
                        column.getSize(),
                        // Length,
                        column.getSize(),
                        // Scale.
                        column.getPrecision(),
                        // Radix.
                        column.getRadix(),
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
                        null};

                values.add(row);
            }
        }

        return new ParadoxResultSet(this.connectionInfo, null, values, columns);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getFunctions(final String catalog, final String schemaPattern, final String functionNamePattern) {
        final ArrayList<Column> columns = new ArrayList<>();
        columns.add(new Column("FUNCTION_CAT", ParadoxType.VARCHAR));
        columns.add(new Column("FUNCTION_SCHEM", ParadoxType.VARCHAR));
        columns.add(new Column("FUNCTION_NAME", ParadoxType.VARCHAR));
        columns.add(new Column(REMARKS, ParadoxType.VARCHAR));
        columns.add(new Column("FUNCTION_TYPE", ParadoxType.INTEGER));
        columns.add(new Column(SPECIFIC_NAME, ParadoxType.VARCHAR));

        final List<Object[]> values = new ArrayList<>();
        for (final Map.Entry<String, Supplier<? extends AbstractFunction>> function :
                FunctionFactory.getFunctions().entrySet()) {
            if ((catalog != null && !catalog.equalsIgnoreCase(connectionInfo.getCatalog())) && (schemaPattern != null
                    && !Expressions.accept(connectionInfo.getLocale(), connectionInfo.getCurrentSchema().name(),
                    schemaPattern, false, '\\'))) {
                continue;
            }

            final AbstractFunction instance = function.getValue().get();

            final Object[] row = {
                    // Catalog.
                    connectionInfo.getCatalog(),
                    // Schema.
                    null,
                    // Name.
                    function.getKey(),
                    // Remarks.
                    instance.getRemarks(),
                    // Type.
                    functionResultUnknown,
                    // Specific name.
                    null
            };

            values.add(row);
        }

        return new ParadoxResultSet(this.connectionInfo, null, values, columns);
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
        return new ParadoxResultSet(this.connectionInfo, null, Collections.emptyList(), Collections.emptyList());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getCatalogs() throws ParadoxDataException {
        final List<Column> columns = Collections
                .singletonList(new Column(DatabaseMetaData.TABLE_CAT, ParadoxType.VARCHAR));

        final List<Object[]> values = connectionInfo.listCatalogs().stream()
                .map(name -> new Object[]{name})
                .collect(Collectors.toList());

        return new ParadoxResultSet(this.connectionInfo, null, values, columns);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getColumns(final String catalog, final String schemaPattern, final String tableNamePattern,
                                final String columnNamePattern) throws SQLException {
        final ArrayList<Column> columns = new ArrayList<>();
        columns.add(new Column(TABLE_CAT, ParadoxType.VARCHAR));
        columns.add(new Column(TABLE_SCHEMA, ParadoxType.VARCHAR));
        columns.add(new Column(TABLE_NAME, ParadoxType.VARCHAR));
        columns.add(new Column(COLUMN_NAME, ParadoxType.VARCHAR));
        columns.add(new Column(DATA_TYPE, ParadoxType.INTEGER));
        columns.add(new Column(TYPE_NAME, ParadoxType.VARCHAR));
        columns.add(new Column("COLUMN_SIZE", ParadoxType.INTEGER));
        columns.add(new Column("BUFFER_LENGTH", ParadoxType.INTEGER));
        columns.add(new Column("DECIMAL_DIGITS", ParadoxType.INTEGER));
        columns.add(new Column("NUM_PREC_RADIX", ParadoxType.INTEGER));
        columns.add(new Column(NULLABLE, ParadoxType.INTEGER));
        columns.add(new Column(REMARKS, ParadoxType.INTEGER));
        columns.add(new Column("COLUMN_DEF", ParadoxType.VARCHAR));
        columns.add(new Column("SQL_DATA_TYPE", ParadoxType.INTEGER));
        columns.add(new Column("SQL_DATETIME_SUB", ParadoxType.INTEGER));
        columns.add(new Column(CHAR_OCTET_LENGTH, ParadoxType.INTEGER));
        columns.add(new Column(ORDINAL_POSITION, ParadoxType.INTEGER));
        columns.add(new Column(IS_NULLABLE, ParadoxType.INTEGER));
        columns.add(new Column("SCOPE_CATLOG", ParadoxType.VARCHAR));
        columns.add(new Column("SCOPE_SCHEMA", ParadoxType.VARCHAR));
        columns.add(new Column("SCOPE_TABLE", ParadoxType.VARCHAR));
        columns.add(new Column("SOURCE_DATA_TYPE", ParadoxType.INTEGER));
        columns.add(new Column("IS_AUTOINCREMENT", ParadoxType.VARCHAR));

        final List<Object[]> values = new ArrayList<>();

        for (final Schema schema : this.connectionInfo.getSchemas(catalog, schemaPattern)) {
            final List<Table> tables = schema.list(this.connectionInfo, tableNamePattern);
            for (final Table table : tables) {
                this.fieldMetadata(connectionInfo.getCatalog(), schema.name(), columnNamePattern, values,
                        table.getName(), table.getFields());
            }
        }

        return new ParadoxResultSet(this.connectionInfo, null, values, columns);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getIndexInfo(final String catalog, final String schemaName, final String tableNamePattern,
                                  final boolean unique, final boolean approximate) throws SQLException {
        final ArrayList<Column> columns = new ArrayList<>();
        columns.add(new Column(TABLE_CAT, ParadoxType.VARCHAR));
        columns.add(new Column(TABLE_SCHEMA, ParadoxType.VARCHAR));
        columns.add(new Column(TABLE_NAME, ParadoxType.VARCHAR));
        columns.add(new Column("NON_UNIQUE", ParadoxType.BOOLEAN));
        columns.add(new Column("INDEX_QUALIFIER", ParadoxType.VARCHAR));
        columns.add(new Column("INDEX_NAME", ParadoxType.VARCHAR));
        columns.add(new Column("TYPE", ParadoxType.INTEGER));
        columns.add(new Column(ORDINAL_POSITION, ParadoxType.INTEGER));
        columns.add(new Column(COLUMN_NAME, ParadoxType.VARCHAR));
        columns.add(new Column("ASC_OR_DESC", ParadoxType.VARCHAR));
        columns.add(new Column("CARDINALITY", ParadoxType.INTEGER));
        columns.add(new Column("PAGES", ParadoxType.INTEGER));
        columns.add(new Column("FILTER_CONDITION", ParadoxType.VARCHAR));

        final List<Object[]> values = new ArrayList<>();
        for (final Schema schema : this.connectionInfo.getSchemas(catalog, schemaName)) {
            for (final Table table : schema.list(this.connectionInfo, tableNamePattern)) {
                for (final Index index : table.getIndexes()) {
                    for (final Field field : index.getFields()) {
                        final Object[] row = new Object[]{
                                catalog,
                                table.getSchemaName(),
                                table.getName(),
                                !index.isUnique(),
                                catalog,
                                index.getName(),
                                java.sql.DatabaseMetaData.tableIndexHashed,
                                field.getOrderNum(),
                                field.getName(),
                                index.getOrder(),
                                index.getRowCount(),
                                index.getTotalBlocks(),
                                null
                        };

                        values.add(row);
                    }
                }

                // FIXME load .VAL indexes (references).
            }
        }

        return new ParadoxResultSet(this.connectionInfo, null, values, columns);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getJDBCMajorVersion() {
        return DatabaseMetaData.JDBC_MAJOR_VERSION;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getJDBCMinorVersion() {
        return DatabaseMetaData.JDBC_MINOR_VERSION;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMaxBinaryLiteralLength() {
        return DatabaseMetaData.PARADOX_MAX_COLUMN_NAME;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMaxCatalogNameLength() {
        return DatabaseMetaData.STRING_MAX_SIZE;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMaxCharLiteralLength() {
        return DatabaseMetaData.STRING_MAX_SIZE;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMaxColumnNameLength() {
        return DatabaseMetaData.PARADOX_MAX_COLUMN_NAME;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMaxColumnsInGroupBy() {
        return DatabaseMetaData.STRING_MAX_SIZE;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMaxColumnsInIndex() {
        return DatabaseMetaData.STRING_MAX_SIZE;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMaxColumnsInOrderBy() {
        return DatabaseMetaData.STRING_MAX_SIZE;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMaxColumnsInSelect() {
        return DatabaseMetaData.STRING_MAX_SIZE;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMaxColumnsInTable() {
        return DatabaseMetaData.STRING_MAX_SIZE;
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
        return DatabaseMetaData.STRING_MAX_SIZE;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMaxIndexLength() {
        return DatabaseMetaData.STRING_MAX_SIZE;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMaxProcedureNameLength() {
        return DatabaseMetaData.STRING_MAX_SIZE;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMaxRowSize() {
        return DatabaseMetaData.STRING_MAX_SIZE;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMaxSchemaNameLength() {
        return DatabaseMetaData.STRING_MAX_SIZE;
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
        return DatabaseMetaData.STRING_MAX_SIZE;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMaxTablesInSelect() {
        return DatabaseMetaData.STRING_MAX_SIZE;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMaxUserNameLength() {
        return DatabaseMetaData.STRING_MAX_SIZE;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getNumericFunctions() {
        return FunctionFactory.getByType(FunctionType.NUMERIC);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getPrimaryKeys(final String catalog, final String schemaName, final String tableNamePattern)
            throws SQLException {
        final List<Column> columns = new ArrayList<>();
        columns.add(new Column(TABLE_CAT, ParadoxType.VARCHAR));
        columns.add(new Column(TABLE_SCHEMA, ParadoxType.VARCHAR));
        columns.add(new Column(TABLE_NAME, ParadoxType.VARCHAR));
        columns.add(new Column(COLUMN_NAME, ParadoxType.VARCHAR));
        columns.add(new Column("KEY_SEQ", ParadoxType.INTEGER));
        columns.add(new Column("PK_NAME", ParadoxType.VARCHAR));

        final List<Object[]> values = new ArrayList<>();

        for (final Schema schema : this.connectionInfo.getSchemas(catalog, schemaName)) {
            for (final Table table : schema.list(this.connectionInfo, tableNamePattern)) {
                final Index index = table.getPrimaryKeyIndex();
                if (index != null) {
                    for (final Field field : index.getFields()) {
                        final Object[] row = new Object[]{
                                schema.catalogName(),
                                schema.name(),
                                table.getName(),
                                field.getName(),
                                field.getOrderNum(),
                                index.getName()
                        };

                        values.add(row);
                    }
                }
            }
        }

        values.sort((o1, o2) -> ((String) o1[3]).compareToIgnoreCase((String) o2[3]));

        return new ParadoxResultSet(this.connectionInfo, null, values, columns);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getProcedureColumns(final String catalog, final String schemaPattern,
                                         final String procedureNamePattern, final String columnNamePattern) {
        final ArrayList<Column> columns = new ArrayList<>();
        columns.add(new Column("PROCEDURE_CAT", ParadoxType.VARCHAR));
        columns.add(new Column("PROCEDURE_SCHEM", ParadoxType.VARCHAR));
        columns.add(new Column("PROCEDURE_NAME", ParadoxType.VARCHAR));
        columns.add(new Column(COLUMN_NAME, ParadoxType.VARCHAR));
        columns.add(new Column(COLUMN_TYPE, ParadoxType.INTEGER));
        columns.add(new Column(DATA_TYPE, ParadoxType.INTEGER));
        columns.add(new Column(TYPE_NAME, ParadoxType.VARCHAR));
        columns.add(new Column("PRECISION", ParadoxType.INTEGER));
        columns.add(new Column("LENGTH", ParadoxType.INTEGER));
        columns.add(new Column("SCALE", ParadoxType.INTEGER));
        columns.add(new Column("RADIX", ParadoxType.INTEGER));
        columns.add(new Column(NULLABLE, ParadoxType.INTEGER));
        columns.add(new Column(REMARKS, ParadoxType.VARCHAR));
        columns.add(new Column("COLUMN_DEF", ParadoxType.VARCHAR));
        columns.add(new Column("SQL_DATA_TYPE", ParadoxType.VARCHAR));
        columns.add(new Column("SQL_DATETIME_SUB", ParadoxType.VARCHAR));
        columns.add(new Column(CHAR_OCTET_LENGTH, ParadoxType.VARCHAR));
        columns.add(new Column(IS_NULLABLE, ParadoxType.VARCHAR));
        columns.add(new Column(SPECIFIC_NAME, ParadoxType.VARCHAR));

        return new ParadoxResultSet(this.connectionInfo, null, Collections.emptyList(), columns);
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
        return new ParadoxResultSet(this.connectionInfo, null, Collections.emptyList(), Collections.emptyList());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getResultSetHoldability() {
        return this.connection.getHoldability();
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
        columns.add(new Column("PROCEDURE_CAT", ParadoxType.VARCHAR));
        columns.add(new Column("PROCEDURE_SCHEM", ParadoxType.VARCHAR));
        columns.add(new Column("PROCEDURE_NAME", ParadoxType.VARCHAR));
        columns.add(new Column("Reserved1", ParadoxType.VARCHAR));
        columns.add(new Column("Reserved2", ParadoxType.VARCHAR));
        columns.add(new Column("Reserved3", ParadoxType.VARCHAR));
        columns.add(new Column(REMARKS, ParadoxType.VARCHAR));
        columns.add(new Column("PROCEDURE_TYPE", ParadoxType.INTEGER));
        columns.add(new Column(SPECIFIC_NAME, ParadoxType.VARCHAR));

        return new ParadoxResultSet(this.connectionInfo, null, Collections.emptyList(), columns);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getSchemas(final String catalog, final String schemaPattern) throws SQLException {
        final ArrayList<Column> columns = new ArrayList<>();
        columns.add(new Column(TABLE_SCHEMA, ParadoxType.VARCHAR));
        columns.add(new Column(TABLE_CATALOG, ParadoxType.VARCHAR));

        final List<String[]> values = new ArrayList<>();
        for (final String catalogName : this.connectionInfo.listCatalogs()) {
            if (catalog == null || Expressions.accept(connectionInfo.getLocale(), catalogName, catalog, false, '\\')) {
                values.addAll(this.connectionInfo.getSchemas(catalogName, schemaPattern).stream()
                        .map(schema -> new String[]{
                                schema.name(),
                                catalogName
                        }).collect(Collectors.toList()));
            }
        }

        return new ParadoxResultSet(this.connectionInfo, null, values, columns);
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
        return java.sql.DatabaseMetaData.sqlStateSQL;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getStringFunctions() {
        return FunctionFactory.getByType(FunctionType.STRING);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getSuperTables(final String catalog, final String schemaPattern, final String tableNamePattern) {
        return new ParadoxResultSet(this.connectionInfo, null, Collections.emptyList(), Collections.emptyList());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getSuperTypes(final String catalog, final String schemaPattern, final String typeNamePattern) {
        return new ParadoxResultSet(this.connectionInfo, null, Collections.emptyList(), Collections.emptyList());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getSystemFunctions() {
        return FunctionFactory.getByType(FunctionType.SYSTEM);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getTablePrivileges(final String catalog, final String schemaPattern,
                                        final String tableNamePattern) {
        return new ParadoxResultSet(this.connectionInfo, null, Collections.emptyList(), Collections.emptyList());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getSchemas() throws SQLException {
        final ArrayList<Column> columns = new ArrayList<>();
        columns.add(new Column(TABLE_SCHEMA, ParadoxType.VARCHAR));
        columns.add(new Column(TABLE_CATALOG, ParadoxType.VARCHAR));

        final List<String[]> values = new ArrayList<>();
        for (final String catalog : this.connectionInfo.listCatalogs()) {
            values.addAll(this.connectionInfo.getSchemas(catalog, null).stream()
                    .map(schema -> new String[]{
                            schema.name(),
                            catalog
                    }).collect(Collectors.toList()));
        }

        return new ParadoxResultSet(this.connectionInfo, null, values, columns);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getTables(final String catalog, final String schemaPattern, final String tableNamePattern,
                               final String[] types) throws SQLException {
        final ArrayList<Column> columns = new ArrayList<>();
        columns.add(new Column(TABLE_CAT, ParadoxType.VARCHAR));// 2
        columns.add(new Column(TABLE_SCHEMA, ParadoxType.VARCHAR));
        columns.add(new Column(TABLE_NAME, ParadoxType.VARCHAR));
        columns.add(new Column("TABLE_TYPE", ParadoxType.VARCHAR));// 1
        columns.add(new Column(REMARKS, ParadoxType.VARCHAR));
        columns.add(new Column("TYPE_CAT", ParadoxType.VARCHAR));
        columns.add(new Column(TYPE_SCHEM, ParadoxType.VARCHAR));// 3
        columns.add(new Column(TYPE_NAME, ParadoxType.VARCHAR));// 4
        columns.add(new Column("SELF_REFERENCING_COL_NAME", ParadoxType.VARCHAR));
        columns.add(new Column("REF_GENERATION", ParadoxType.VARCHAR));

        final List<Object[]> values = new ArrayList<>();
        List<String> typeList = Collections.emptyList();
        if (types != null) {
            typeList = Arrays.asList(types);
        }

        for (final Schema schema : this.connectionInfo.getSchemas(catalog, schemaPattern)) {
            for (final Table table : schema.list(this.connectionInfo, tableNamePattern)) {
                if (types == null || typeList.contains(table.type().typeName())) {
                    values.add(new Object[]{
                            schema.catalogName(),
                            schema.name(),
                            table.getName(),
                            table.type().description(),
                            null,
                            null,
                            null,
                            null,
                            null,
                            null
                    });
                }
            }
        }

        return new ParadoxResultSet(this.connectionInfo, null, values, columns);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getTimeDateFunctions() {
        return FunctionFactory.getByType(FunctionType.TIME_DATE);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getTypeInfo() {
        return new ParadoxResultSet(this.connectionInfo, null, Collections.emptyList(), Collections.emptyList());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getUDTs(final String catalog, final String schemaPattern, final String typeNamePattern,
                             final int[] types) {
        return new ParadoxResultSet(this.connectionInfo, null, Collections.emptyList(), Collections.emptyList());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getURL() {
        return this.connectionInfo.getUrl();
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
        return new ParadoxResultSet(this.connectionInfo, null, Collections.emptyList(), Collections.emptyList());
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
        return false;
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
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean nullsAreSortedLow() {
        return false;
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
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsGroupByBeyondSelect() {
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsGroupByUnrelated() {
        return true;
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
        return type == ResultSet.TYPE_FORWARD_ONLY || type == ResultSet.TYPE_SCROLL_INSENSITIVE
                || type == ResultSet.TYPE_SCROLL_SENSITIVE;
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
        return false;
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
        final List<Column> columns = Collections.singletonList(new Column("TABLE_TYPE", ParadoxType.VARCHAR));

        final List<String[]> values = new ArrayList<>(4);
        values.add(new String[]{VIEW});
        values.add(new String[]{TABLE});
        values.add(new String[]{SYSTEM_TABLE});

        values.sort(Comparator.comparing(o -> o[0]));

        return new ParadoxResultSet(this.connectionInfo, null, values, columns);
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
                               final List<Object[]> values, final String tableName, final Field[] fields) {
        int ordinal = 1;
        for (final Field field : fields) {
            if ((columnNamePattern != null) && !Expressions.accept(connectionInfo.getLocale(), field.getName(),
                    columnNamePattern,
                    false, Constants.ESCAPE_CHAR)) {
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
            row.add(ParadoxType.valueOf(type).getName());
            // Column size.
            row.add(field.getSize());
            // Buffer length.
            row.add(DatabaseMetaData.MAX_INT_SIZE);
            // Decimal digits.
            row.add(field.getPrecision());
            // Number precision radix.
            row.add(field.getType().getRadix());
            // Nullable.
            if (field.isAutoIncrement()) {
                row.add(java.sql.DatabaseMetaData.columnNoNulls);
            } else {
                row.add(java.sql.DatabaseMetaData.columnNullable);
            }
            // Column remarks.
            row.add(null);
            // Column default value.
            row.add(null);
            // Column SQL data type.
            row.add(field.getSqlType());
            // Subtype code for datetime and SQL-92 interval data types. For other data
            // types, this column returns NULL.
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
}
