/*
 * ParadoxDatabaseMetaData.java 03/14/2009 Copyright (C) 2009 Leonardo Alves da Costa This program is free software: you
 * can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should
 * have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.metadata;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.ParadoxResultSet;
import com.googlecode.paradox.data.IndexData;
import com.googlecode.paradox.data.PrimaryKeyData;
import com.googlecode.paradox.data.TableData;
import com.googlecode.paradox.data.ViewData;
import com.googlecode.paradox.data.table.value.FieldValue;
import com.googlecode.paradox.procedures.AbstractCallableProcedure;
import com.googlecode.paradox.procedures.ProcedureAS;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.utils.Constants;
import com.googlecode.paradox.utils.Expressions;
import com.googlecode.paradox.utils.Utils;
import com.googlecode.paradox.utils.filefilters.DirectoryFilter;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Creates an database metadata.
 *
 * @author Leonardo Alves da Costa
 * @version 1.1
 * @since 1.0
 */
public final class ParadoxDatabaseMetaData implements DatabaseMetaData {

    /**
     * The tables cat name field.
     */
    static final String TABLE_CAT = "TABLE_CAT";
    /**
     * The table names schema field.
     */
    static final String TABLE_SCHEMA = "TABLE_SCHEM";
    static final String TABLE_CATALOG = "TABLE_CATALOG";
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
    private static final int JDBC_MINOR_VERSION = 0;
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
        return false;
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
        return new ParadoxResultSet(this.conn, null, new ArrayList<List<FieldValue>>(), new ArrayList<Column>());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getBestRowIdentifier(final String catalog, final String schema, final String table,
            final int scope, final boolean nullable) {
        return new ParadoxResultSet(this.conn, null, new ArrayList<List<FieldValue>>(), new ArrayList<Column>());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getCatalogs() {
        final ArrayList<Column> columns = new ArrayList<>(1);
        columns.add(new Column(ParadoxDatabaseMetaData.TABLE_CAT, Types.VARCHAR));

        final List<FieldValue> row = new ArrayList<>(1);
        final List<List<FieldValue>> values = new ArrayList<>(1);
        row.add(new FieldValue(this.conn.getCatalog(), Types.VARCHAR));
        values.add(row);

        return new ParadoxResultSet(this.conn, null, values, columns);
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
        return new ParadoxResultSet(this.conn, null, new ArrayList<List<FieldValue>>(), new ArrayList<Column>());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getColumnPrivileges(final String catalog, final String schema, final String table,
            final String columnNamePattern) {
        return new ParadoxResultSet(this.conn, null, new ArrayList<List<FieldValue>>(), new ArrayList<Column>());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getColumns(final String catalog, final String schemaPattern, final String tableNamePattern,
            final String columnNamePattern) throws SQLException {
        final ArrayList<Column> columns = new ArrayList<>(1);
        columns.add(new Column(ParadoxDatabaseMetaData.TABLE_CAT, Types.VARCHAR));
        columns.add(new Column(ParadoxDatabaseMetaData.TABLE_SCHEMA, Types.VARCHAR));
        columns.add(new Column(ParadoxDatabaseMetaData.TABLE_NAME, Types.VARCHAR));
        columns.add(new Column(ParadoxDatabaseMetaData.COLUMN_NAME, Types.VARCHAR));
        columns.add(new Column("DATA_TYPE", Types.INTEGER));
        columns.add(new Column(ParadoxDatabaseMetaData.TYPE_NAME, Types.VARCHAR));
        columns.add(new Column("COLUMN_SIZE", Types.INTEGER));
        columns.add(new Column("BUFFER_LENGTH", Types.INTEGER));
        columns.add(new Column("DECIMAL_DIGITS", Types.INTEGER));
        columns.add(new Column("NUM_PREC_RADIX", Types.INTEGER));
        columns.add(new Column("NULLABLE", Types.INTEGER));
        columns.add(new Column(ParadoxDatabaseMetaData.REMARKS, Types.INTEGER));
        columns.add(new Column("COLUMN_DEF", Types.VARCHAR));
        columns.add(new Column("SQL_DATA_TYPE", Types.INTEGER));
        columns.add(new Column("SQL_DATETIME_SUB", Types.INTEGER));
        columns.add(new Column("CHAR_OCTET_LENGTH", Types.INTEGER));
        columns.add(new Column("ORDINAL_POSITION", Types.INTEGER));
        columns.add(new Column("IS_NULLABLE", Types.INTEGER));
        columns.add(new Column("SCOPE_CATLOG", Types.VARCHAR));
        columns.add(new Column("SCOPE_SCHEMA", Types.VARCHAR));
        columns.add(new Column("SCOPE_TABLE", Types.VARCHAR));
        columns.add(new Column("SOURCE_DATA_TYPE", Types.SMALLINT));
        columns.add(new Column("IS_AUTOINCREMENT", Types.VARCHAR));

        final List<List<FieldValue>> values = new ArrayList<>(1);

        for (final File currentSchema : this.conn.getSchema(catalog, schemaPattern)) {
            final List<ParadoxTable> tables = TableData.listTables(currentSchema, tableNamePattern, this.conn);
            for (final ParadoxTable table : tables) {
                this.fieldMetadata(catalog, currentSchema.getName(), columnNamePattern, values, table.getName(),
                        table.getFields());
            }

            final List<? extends ParadoxDataFile> views = ViewData.listViews(currentSchema, tableNamePattern,
                    this.conn);
            for (final ParadoxDataFile view : views) {
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
    public Connection getConnection() {
        return this.conn;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getCrossReference(final String primaryCatalog, final String primarySchema,
            final String primaryTable, final String foreignCatalog, final String foreignSchema,
            final String foreignTable) {
        return new ParadoxResultSet(this.conn, null, new ArrayList<List<FieldValue>>(), new ArrayList<Column>());
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
        return new ParadoxResultSet(this.conn, null, new ArrayList<List<FieldValue>>(), new ArrayList<Column>());
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
        return new ParadoxResultSet(this.conn, null, new ArrayList<List<FieldValue>>(), new ArrayList<Column>());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getFunctions(final String catalog, final String schemaPattern, final String functionNamePattern) {
        return new ParadoxResultSet(this.conn, null, new ArrayList<List<FieldValue>>(), new ArrayList<Column>());
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
        return new ParadoxResultSet(this.conn, null, new ArrayList<List<FieldValue>>(), new ArrayList<Column>());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getIndexInfo(final String catalog, final String schema, final String tableNamePattern,
            final boolean unique, final boolean approximate) throws SQLException {
        final ArrayList<Column> columns = new ArrayList<>(1);
        columns.add(new Column(ParadoxDatabaseMetaData.TABLE_CAT, Types.VARCHAR));
        columns.add(new Column(ParadoxDatabaseMetaData.TABLE_SCHEMA, Types.VARCHAR));
        columns.add(new Column(ParadoxDatabaseMetaData.TABLE_NAME, Types.VARCHAR));
        columns.add(new Column("NON_UNIQUE", Types.BOOLEAN));
        columns.add(new Column("INDEX_QUALIFIER", Types.VARCHAR));
        columns.add(new Column("INDEX_NAME", Types.VARCHAR));
        columns.add(new Column("TYPE", Types.INTEGER));
        columns.add(new Column("ORDINAL_POSITION", Types.INTEGER));
        columns.add(new Column(ParadoxDatabaseMetaData.COLUMN_NAME, Types.VARCHAR));
        columns.add(new Column("ASC_OR_DESC", Types.VARCHAR));
        columns.add(new Column("CARDINALITY", Types.INTEGER));
        columns.add(new Column("PAGES", Types.INTEGER));
        columns.add(new Column("FILTER_CONDITION", Types.VARCHAR));

        final List<List<FieldValue>> values = new ArrayList<>(1);
        final FieldValue fieldZero = new FieldValue(0, Types.INTEGER);

        for (final File currentSchema : this.conn.getSchema(catalog, schema)) {
            for (final ParadoxTable table : TableData.listTables(currentSchema, tableNamePattern, this.conn)) {
                getPrimaryKeyIndex(catalog, values, fieldZero, currentSchema, table);
                getTableIndexInfo(catalog, tableNamePattern, values, fieldZero, currentSchema);
            }
        }
        return new ParadoxResultSet(this.conn, null, values, columns);
    }

    private void getTableIndexInfo(String catalog, String tableNamePattern, List<List<FieldValue>> values,
            FieldValue fieldZero, File currentSchema) throws SQLException {
        for (final ParadoxIndex index : IndexData.listIndexes(currentSchema, tableNamePattern, this.conn)) {
            int ordinal = 0;
            for (final ParadoxField field : index.getFields()) {
                final ArrayList<FieldValue> row = new ArrayList<>();
                row.add(new FieldValue(catalog, Types.VARCHAR));
                row.add(new FieldValue(currentSchema.getName(), Types.VARCHAR));
                row.add(new FieldValue(index.getParentName(), Types.VARCHAR));
                row.add(new FieldValue(Boolean.FALSE, Types.BOOLEAN));
                row.add(new FieldValue(catalog, Types.VARCHAR));
                row.add(new FieldValue(index.getName(), Types.VARCHAR));
                row.add(new FieldValue(DatabaseMetaData.tableIndexHashed, Types.INTEGER));
                row.add(new FieldValue(ordinal, Types.INTEGER));
                row.add(new FieldValue(field.getName(), Types.VARCHAR));
                row.add(new FieldValue(index.getOrder(), Types.VARCHAR));
                row.add(fieldZero);
                row.add(fieldZero);
                row.add(null);

                values.add(row);
                ordinal++;
            }
        }
    }

    private void getPrimaryKeyIndex(String catalog, List<List<FieldValue>> values, FieldValue fieldZero,
            File currentSchema, ParadoxTable table) throws SQLException {
        final ParadoxPK primaryKeyIndex = PrimaryKeyData.getPrimaryKey(currentSchema, table, this.conn);
        if (primaryKeyIndex != null) {
            for (final ParadoxField pk : table.getPrimaryKeys()) {
                final ArrayList<FieldValue> row = new ArrayList<>();

                row.add(new FieldValue(catalog, Types.VARCHAR));
                row.add(new FieldValue(currentSchema.getName(), Types.VARCHAR));
                row.add(new FieldValue(table.getName(), Types.VARCHAR));
                row.add(new FieldValue(Boolean.FALSE, Types.BOOLEAN));
                row.add(new FieldValue(catalog, Types.VARCHAR));
                row.add(new FieldValue(primaryKeyIndex.getName(), Types.VARCHAR));
                row.add(new FieldValue(DatabaseMetaData.tableIndexHashed));
                row.add(fieldZero);
                row.add(new FieldValue(pk.getName(), Types.VARCHAR));
                row.add(new FieldValue("A", Types.VARCHAR));
                row.add(fieldZero);
                row.add(fieldZero);
                row.add(null);

                values.add(row);
            }
        }
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

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getPrimaryKeys(final String catalog, final String schema, final String tableNamePattern) throws
            SQLException {
        final ArrayList<Column> columns = new ArrayList<>(1);
        columns.add(new Column(ParadoxDatabaseMetaData.TABLE_CAT, Types.VARCHAR));
        columns.add(new Column(ParadoxDatabaseMetaData.TABLE_SCHEMA, Types.VARCHAR));
        columns.add(new Column(ParadoxDatabaseMetaData.TABLE_NAME, Types.VARCHAR));
        columns.add(new Column(ParadoxDatabaseMetaData.COLUMN_NAME, Types.VARCHAR));
        columns.add(new Column("KEY_SEQ", Types.INTEGER));
        columns.add(new Column("PK_NAME", Types.VARCHAR));

        final List<List<FieldValue>> values = new ArrayList<>(1);

        for (final File currentSchema : this.conn.getSchema(catalog, schema)) {
            for (final ParadoxTable table : TableData.listTables(currentSchema, tableNamePattern, this.conn)) {
                int loop = 0;
                for (final ParadoxField pk : table.getPrimaryKeys()) {
                    final ArrayList<FieldValue> row = new ArrayList<>();
                    row.add(new FieldValue(catalog, Types.VARCHAR));
                    row.add(new FieldValue(currentSchema.getName(), Types.VARCHAR));
                    row.add(new FieldValue(table.getName(), Types.VARCHAR));
                    row.add(new FieldValue(pk.getName(), Types.VARCHAR));
                    row.add(new FieldValue(loop, Types.INTEGER));
                    row.add(new FieldValue(pk.getName(), Types.VARCHAR));
                    values.add(row);
                    loop++;
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
            final String procedureNamePattern, final String columnNamePattern) throws SQLException {
        final ArrayList<Column> columns = new ArrayList<>(1);
        columns.add(new Column("PROCEDURE_CAT", Types.VARCHAR));
        columns.add(new Column("PROCEDURE_SCHEM", Types.VARCHAR));
        columns.add(new Column("PROCEDURE_NAME", Types.VARCHAR));
        columns.add(new Column(ParadoxDatabaseMetaData.COLUMN_NAME, Types.VARCHAR));
        columns.add(new Column("COLUMN_TYPE", Types.INTEGER));
        columns.add(new Column("DATA_TYPE", Types.INTEGER));
        columns.add(new Column(ParadoxDatabaseMetaData.TYPE_NAME, Types.VARCHAR));
        columns.add(new Column("PRECISION", Types.INTEGER));
        columns.add(new Column("LENGTH", Types.INTEGER));
        columns.add(new Column("SCALE", Types.INTEGER));
        columns.add(new Column("RADIX", Types.INTEGER));
        columns.add(new Column("NULLABLE", Types.INTEGER));
        columns.add(new Column(ParadoxDatabaseMetaData.REMARKS, Types.VARCHAR));
        columns.add(new Column("COLUMN_DEF", Types.VARCHAR));
        columns.add(new Column("SQL_DATA_TYPE", Types.VARCHAR));
        columns.add(new Column("SQL_DATETIME_SUB", Types.VARCHAR));
        columns.add(new Column("CHAR_OCTET_LENGTH", Types.VARCHAR));
        columns.add(new Column("IS_NULLABLE", Types.VARCHAR));
        columns.add(new Column("SPECIFIC_NAME", Types.VARCHAR));

        final List<List<FieldValue>> values = new ArrayList<>();
        final FieldValue fieldZero = new FieldValue(0, Types.INTEGER);
        final FieldValue fieldVarchar = new FieldValue(Types.VARCHAR);

        for (final AbstractCallableProcedure procedure : ProcedureAS.getInstance().list()) {
            if (Expressions.accept(procedure.getName(), procedureNamePattern)) {
                for (final ParadoxField field : procedure.getCols()) {
                    final ArrayList<FieldValue> row = new ArrayList<>();
                    row.add(new FieldValue(catalog, Types.VARCHAR));
                    row.add(new FieldValue(this.conn.getCurrentSchema(), Types.VARCHAR));
                    row.add(new FieldValue(procedure.getName(), Types.VARCHAR));
                    row.add(new FieldValue(field.getName(), Types.VARCHAR));
                    row.add(new FieldValue(DatabaseMetaData.procedureColumnIn, Types.INTEGER));
                    row.add(new FieldValue(field.getSqlType(), Types.INTEGER));
                    row.add(new FieldValue(Column.getTypeName(field.getSqlType()), Types.VARCHAR));
                    row.add(fieldZero);
                    row.add(new FieldValue(field.getSize(), Types.INTEGER));
                    row.add(fieldZero);
                    row.add(fieldZero);
                    row.add(new FieldValue(DatabaseMetaData.procedureNullable));
                    row.add(fieldVarchar);
                    row.add(fieldVarchar);
                    row.add(fieldVarchar);
                    row.add(fieldVarchar);
                    row.add(fieldVarchar);
                    row.add(fieldVarchar);
                    row.add(new FieldValue("NO", Types.VARCHAR));
                    row.add(new FieldValue(procedure.getName(), Types.VARCHAR));
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
        columns.add(new Column("SPECIFIC_NAME", Types.VARCHAR));

        final List<List<FieldValue>> values = new ArrayList<>();

        for (final AbstractCallableProcedure procedure : ProcedureAS.getInstance().list()) {
            final ArrayList<FieldValue> row = new ArrayList<>();
            row.add(new FieldValue(catalog, Types.VARCHAR));
            row.add(new FieldValue(this.conn.getSchema(), Types.VARCHAR));
            row.add(new FieldValue(procedure.getName(), Types.VARCHAR));
            row.add(new FieldValue(Types.VARCHAR));
            row.add(new FieldValue(Types.VARCHAR));
            row.add(new FieldValue(Types.VARCHAR));
            row.add(new FieldValue(procedure.getRemarks(), Types.VARCHAR));
            row.add(new FieldValue(procedure.getReturnType(), Types.INTEGER));
            row.add(new FieldValue(procedure.getName(), Types.VARCHAR));
            values.add(row);
        }

        return new ParadoxResultSet(this.conn, null, values, columns);
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
        return new ParadoxResultSet(this.conn, null, new ArrayList<List<FieldValue>>(), new ArrayList<Column>());
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
    public ResultSet getSchemas() {
        final ArrayList<Column> columns = new ArrayList<>(1);
        columns.add(new Column(TABLE_SCHEMA, Types.VARCHAR));
        columns.add(new Column(TABLE_CATALOG, Types.VARCHAR));

        final List<List<FieldValue>> values = new ArrayList<>(1);

        final File catalog = conn.getCurrentCatalog();
        final File[] schemas = catalog.listFiles(new DirectoryFilter());

        if (schemas != null) {
            Arrays.sort(schemas);
            for (final File schema : schemas) {
                final ArrayList<FieldValue> row = new ArrayList<>(1);
                row.add(new FieldValue(schema.getName().toLowerCase(), Types.VARCHAR));
                row.add(new FieldValue(catalog.getName().toLowerCase(), Types.VARCHAR));
                values.add(row);
            }
        }

        return new ParadoxResultSet(this.conn, null, values, columns);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getSchemas(final String catalog, final String schemaPattern) {
        if (((catalog != null) && !Expressions.accept(this.conn.getCatalog(), catalog))
                || ((schemaPattern != null) && !Expressions.accept(this.conn.getSchema(), schemaPattern))) {
            return new ParadoxResultSet(this.conn, null, new ArrayList<List<FieldValue>>(), new ArrayList<Column>());
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
        return "\\";
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getSQLKeywords() {
        return "SELECT";
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
        return new ParadoxResultSet(this.conn, null, new ArrayList<List<FieldValue>>(), new ArrayList<Column>());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getSuperTypes(final String catalog, final String schemaPattern, final String typeNamePattern) {
        return new ParadoxResultSet(this.conn, null, new ArrayList<List<FieldValue>>(), new ArrayList<Column>());
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
        return new ParadoxResultSet(this.conn, null, new ArrayList<List<FieldValue>>(), new ArrayList<Column>());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getTables(final String catalog, final String schemaPattern, final String tableNamePattern,
            final String[] types) throws SQLException {
        final ArrayList<Column> columns = new ArrayList<>(1);
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

        final List<List<FieldValue>> values = new ArrayList<>();
        if (types != null) {
            for (final File currentSchema : this.conn.getSchema(catalog, schemaPattern)) {
                for (final String type : types) {
                    if (ParadoxDatabaseMetaData.TABLE.equalsIgnoreCase(type)) {
                        this.formatTable(catalog, schemaPattern, tableNamePattern, values);
                    } else if ("VIEW".equalsIgnoreCase(type)) {
                        this.formatView(tableNamePattern, values, currentSchema);
                    }
                }
            }
        }
        return new ParadoxResultSet(this.conn, null, values, columns);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getTableTypes() {
        final ArrayList<Column> columns = new ArrayList<>(1);
        columns.add(new Column("TABLE_TYPE", Types.VARCHAR));

        final List<List<FieldValue>> values = new ArrayList<>(3);
        values.add(Collections.singletonList(new FieldValue(ParadoxDatabaseMetaData.TABLE, Types.VARCHAR)));
        values.add(Collections.singletonList(new FieldValue("VIEW", Types.VARCHAR)));
        values.add(Collections.singletonList(new FieldValue("SYSTEM TABLE", Types.VARCHAR)));

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
        return new ParadoxResultSet(this.conn, null, new ArrayList<List<FieldValue>>(), new ArrayList<Column>());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSet getUDTs(final String catalog, final String schemaPattern, final String typeNamePattern,
            final int[] types) {
        return new ParadoxResultSet(this.conn, null, new ArrayList<List<FieldValue>>(), new ArrayList<Column>());
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
        return new ParadoxResultSet(this.conn, null, new ArrayList<List<FieldValue>>(), new ArrayList<Column>());
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
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean storesLowerCaseQuotedIdentifiers() {
        return true;
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
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean storesUpperCaseQuotedIdentifiers() {
        return true;
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
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsANSI92FullSQL() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsANSI92IntermediateSQL() {
        return false;
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
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsConvert(final int fromType, final int toType) {
        return false;
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
        return false;
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
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsLimitedOuterJoins() {
        return false;
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
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsMixedCaseQuotedIdentifiers() {
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsMultipleOpenResults() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsMultipleResultSets() {
        return false;
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
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsOuterJoins() {
        return false;
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
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsSchemasInIndexDefinitions() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean supportsSchemasInPrivilegeDefinitions() {
        return false;
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
        return false;
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
        return true;
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
     * Gets fields metadata.
     *
     * @param catalog           the catalog name.
     * @param schema            the schema name.
     * @param columnNamePattern column pattern to search of.
     * @param values            the table values.
     * @param tableName         the table name.
     * @param fields            the field list.
     * @throws SQLException in case of errors.
     */
    private void fieldMetadata(final String catalog, final String schema, final String columnNamePattern,
            final List<List<FieldValue>> values,
            final String tableName, final List<ParadoxField> fields) throws SQLException {
        int ordinal = 1;
        for (final ParadoxField field : fields) {
            if ((columnNamePattern != null) && !Expressions.accept(field.getName(), columnNamePattern)) {
                continue;
            }

            final ArrayList<FieldValue> row = new ArrayList<>();

            final int type = field.getSqlType();
            row.add(new FieldValue(catalog, Types.VARCHAR));
            row.add(new FieldValue(schema, Types.VARCHAR));
            row.add(new FieldValue(tableName, Types.VARCHAR));
            row.add(new FieldValue(field.getAlias(), Types.VARCHAR));
            row.add(new FieldValue(type, Types.INTEGER));
            row.add(new FieldValue(Column.getTypeName(type), Types.VARCHAR));
            row.add(new FieldValue(field.getSize(), Types.INTEGER));
            row.add(new FieldValue(ParadoxDatabaseMetaData.MAX_INT_SIZE, Types.INTEGER));

            if ((field.getType() == 5) || (field.getType() == 6)) {
                row.add(new FieldValue(2, Types.INTEGER));
            } else {
                row.add(new FieldValue(0, Types.INTEGER));
            }
            row.add(new FieldValue(10, Types.INTEGER));
            row.add(new FieldValue(DatabaseMetaData.columnNullableUnknown));
            row.add(new FieldValue(Types.INTEGER));
            row.add(new FieldValue(Types.VARCHAR));
            row.add(new FieldValue(Types.INTEGER));
            row.add(new FieldValue(Types.INTEGER));
            row.add(new FieldValue(ParadoxDatabaseMetaData.STRING_MAX_SIZE, Types.INTEGER));
            row.add(new FieldValue(ordinal));
            row.add(new FieldValue("YES", Types.VARCHAR));
            row.add(new FieldValue(Types.VARCHAR));
            row.add(new FieldValue(Types.VARCHAR));
            row.add(new FieldValue(Types.INTEGER));
            row.add(new FieldValue(type, Types.INTEGER));
            if (field.isAutoIncrement()) {
                row.add(new FieldValue("YES", Types.VARCHAR));
            } else {
                row.add(new FieldValue("NO", Types.VARCHAR));
            }
            ordinal++;
            values.add(row);
        }
    }

    /**
     * Format a single row metadata.
     *
     * @param name the row name.
     * @param type the row type.
     * @return the row.
     */
    private List<FieldValue> formatRow(final String name, final String type, final String catalog,
            final String schema) {
        final ArrayList<FieldValue> row = new ArrayList<>(1);
        row.add(new FieldValue(catalog, Types.VARCHAR));
        row.add(new FieldValue(schema, Types.VARCHAR));
        row.add(new FieldValue(name, Types.VARCHAR));
        row.add(new FieldValue(type, Types.VARCHAR));
        row.add(new FieldValue(Types.VARCHAR));
        row.add(new FieldValue(Types.VARCHAR));
        row.add(new FieldValue(Types.VARCHAR));
        row.add(new FieldValue(Types.VARCHAR));
        row.add(new FieldValue(Types.VARCHAR));
        row.add(new FieldValue(Types.VARCHAR));
        return row;
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
            final List<List<FieldValue>> values) throws SQLException {
        for (final File schema : this.conn.getSchema(catalog, schemaPattern)) {
            for (final ParadoxTable table : TableData.listTables(schema, tableNamePattern, this.conn)) {
                values.add(this.formatRow(table.getName(), TABLE, catalog, schema.getName()));
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
    private void formatView(final String tableNamePattern, final List<List<FieldValue>> values,
            final File currentSchema) throws SQLException {
        for (final ParadoxView view : ViewData.listViews(currentSchema, tableNamePattern, this.conn)) {
            values.add(this.formatRow(view.getName(), "VIEW", this.conn.getCatalog(), currentSchema.getName()));
        }
    }
}
