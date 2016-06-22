package com.googlecode.paradox.metadata;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.ParadoxResultSet;
import com.googlecode.paradox.data.IndexData;
import com.googlecode.paradox.data.PrimaryKeyData;
import com.googlecode.paradox.data.TableData;
import com.googlecode.paradox.data.ViewData;
import com.googlecode.paradox.data.table.value.FieldValue;
import com.googlecode.paradox.procedures.CallableProcedure;
import com.googlecode.paradox.procedures.ProcedureAS;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.utils.Constants;
import com.googlecode.paradox.utils.Expressions;
import com.googlecode.paradox.utils.SQLStates;

/**
 *
 * @author Leonardo Alves da Costa
 * @since 14/03/2009
 * @version 1.1
 */
public class ParadoxDatabaseMetaData implements DatabaseMetaData {

    private static final String REMARKS = "REMARKS";
    private static final String COLUMN_NAME = "COLUMN_NAME";
    private static final String TYPE_NAME = "TYPE_NAME";
    private static final String TABLE = "TABLE";
    private static final String TABLE_CAT = "TABLE_CAT";
    private static final String TABLE_SCHEM = "TABLE_SCHEM";
    private static final String TABLE_NAME = "TABLE_NAME";
    
    private final ParadoxConnection conn;

    public ParadoxDatabaseMetaData(final ParadoxConnection conn) {
        this.conn = conn;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean allProceduresAreCallable() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean allTablesAreSelectable() throws SQLException {
        return true;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public String getURL() throws SQLException {
        return conn.getUrl();
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public String getUserName() throws SQLException {
        return "SYSTEM";
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean isReadOnly() throws SQLException {
        return true;

    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean nullsAreSortedHigh() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean nullsAreSortedLow() throws SQLException {
        return true;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean nullsAreSortedAtStart() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean nullsAreSortedAtEnd() throws SQLException {
        return true;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public String getDatabaseProductName() throws SQLException {
        return Constants.DRIVER_NAME;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public String getDatabaseProductVersion() throws SQLException {
        return Constants.DRIVER_NAME + " " + Constants.DRIVER_VERSION;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public String getDriverName() throws SQLException {
        return Constants.DRIVER_NAME;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public String getDriverVersion() throws SQLException {
        return Constants.DRIVER_VERSION;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public int getDriverMajorVersion() {
        return Constants.MAJOR_VERSION;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public int getDriverMinorVersion() {
        return Constants.MINOR_VERSION;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean usesLocalFiles() throws SQLException {
        return true;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean usesLocalFilePerTable() throws SQLException {
        return true;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsMixedCaseIdentifiers() throws SQLException {
        return true;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean storesUpperCaseIdentifiers() throws SQLException {
        return true;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean storesLowerCaseIdentifiers() throws SQLException {
        return true;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean storesMixedCaseIdentifiers() throws SQLException {
        return true;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        return true;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        return true;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        return true;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        return true;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public String getIdentifierQuoteString() throws SQLException {
        return "\"";
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public String getSQLKeywords() throws SQLException {
        return "SELECT";
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public String getNumericFunctions() throws SQLException {
        return "AVERANGE,SUM";
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public String getStringFunctions() throws SQLException {
        return "";
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public String getSystemFunctions() throws SQLException {
        return "";
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public String getTimeDateFunctions() throws SQLException {
        return "";
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public String getSearchStringEscape() throws SQLException {
        return "\\";
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public String getExtraNameCharacters() throws SQLException {
        return "";
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsAlterTableWithAddColumn() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsAlterTableWithDropColumn() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsColumnAliasing() throws SQLException {
        return true;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean nullPlusNonNullIsNull() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsConvert() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsConvert(final int fromType, final int toType) throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsTableCorrelationNames() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsDifferentTableCorrelationNames() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsExpressionsInOrderBy() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsOrderByUnrelated() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsGroupBy() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsGroupByUnrelated() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsGroupByBeyondSelect() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsLikeEscapeClause() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsMultipleResultSets() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsMultipleTransactions() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsNonNullableColumns() throws SQLException {
        return true;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsMinimumSQLGrammar() throws SQLException {
        return true;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsCoreSQLGrammar() throws SQLException {
        return true;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsExtendedSQLGrammar() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsANSI92EntryLevelSQL() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsANSI92IntermediateSQL() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsANSI92FullSQL() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsIntegrityEnhancementFacility() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsOuterJoins() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsFullOuterJoins() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsLimitedOuterJoins() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public String getSchemaTerm() throws SQLException {
        return "SCHEMA";
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public String getProcedureTerm() throws SQLException {
        return "PROCEDURE";
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public String getCatalogTerm() throws SQLException {
        return "CATALOG";
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean isCatalogAtStart() throws SQLException {
        return true;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public String getCatalogSeparator() throws SQLException {
        return ".";
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsSchemasInDataManipulation() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsSchemasInProcedureCalls() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsSchemasInTableDefinitions() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsSchemasInIndexDefinitions() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsCatalogsInDataManipulation() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsCatalogsInTableDefinitions() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsPositionedDelete() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsPositionedUpdate() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsSelectForUpdate() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsStoredProcedures() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsSubqueriesInComparisons() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsSubqueriesInExists() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsSubqueriesInIns() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsSubqueriesInQuantifieds() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsCorrelatedSubqueries() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsUnion() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsUnionAll() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public int getMaxBinaryLiteralLength() throws SQLException {
        return 8;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public int getMaxCharLiteralLength() throws SQLException {
        return 255;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public int getMaxColumnNameLength() throws SQLException {
        return 8;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public int getMaxColumnsInGroupBy() throws SQLException {
        return 255;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public int getMaxColumnsInIndex() throws SQLException {
        return 255;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public int getMaxColumnsInOrderBy() throws SQLException {
        return 255;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public int getMaxColumnsInSelect() throws SQLException {
        return 255;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public int getMaxColumnsInTable() throws SQLException {
        return 255;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public int getMaxConnections() throws SQLException {
        // FIXME allow more than one connection
        return 1;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public int getMaxCursorNameLength() throws SQLException {
        return 255;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public int getMaxIndexLength() throws SQLException {
        return 255;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public int getMaxSchemaNameLength() throws SQLException {
        return 255;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public int getMaxProcedureNameLength() throws SQLException {
        return 255;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public int getMaxCatalogNameLength() throws SQLException {
        return 255;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public int getMaxRowSize() throws SQLException {
        return 255;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        return true;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public int getMaxStatementLength() throws SQLException {
        return Integer.MAX_VALUE;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public int getMaxStatements() throws SQLException {
        return Integer.MAX_VALUE;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public int getMaxTableNameLength() throws SQLException {
        return 255;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public int getMaxTablesInSelect() throws SQLException {
        return 255;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public int getMaxUserNameLength() throws SQLException {
        return 255;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public int getDefaultTransactionIsolation() throws SQLException {
        return Connection.TRANSACTION_NONE;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsTransactions() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsTransactionIsolationLevel(final int level) throws SQLException {
        return Connection.TRANSACTION_NONE != level;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public ResultSet getProcedures(final String catalog, final String schemaPattern, final String procedureNamePattern) throws SQLException {
        final ArrayList<Column> columns = new ArrayList<Column>(1);
        columns.add(new Column("PROCEDURE_CAT", Types.VARCHAR));
        columns.add(new Column("PROCEDURE_SCHEM", Types.VARCHAR));
        columns.add(new Column("PROCEDURE_NAME", Types.VARCHAR));
        columns.add(new Column("Reserved1", Types.VARCHAR));
        columns.add(new Column("Reserved2", Types.VARCHAR));
        columns.add(new Column("Reserved3", Types.VARCHAR));
        columns.add(new Column(REMARKS, Types.VARCHAR));
        columns.add(new Column("PROCEDURE_TYPE", Types.INTEGER));
        columns.add(new Column("SPECIFIC_NAME", Types.VARCHAR));

        final List<List<FieldValue>> values = new ArrayList<List<FieldValue>>();

        for (final CallableProcedure procedure : ProcedureAS.getInstance().list()) {
            final ArrayList<FieldValue> row = new ArrayList<FieldValue>();
            row.add(new FieldValue(conn.getCatalog(), Types.VARCHAR));
            row.add(new FieldValue(conn.getSchema(), Types.VARCHAR));
            row.add(new FieldValue(procedure.getName(), Types.VARCHAR));
            row.add(new FieldValue(Types.VARCHAR));
            row.add(new FieldValue(Types.VARCHAR));
            row.add(new FieldValue(Types.VARCHAR));
            row.add(new FieldValue(procedure.getRemarks(), Types.VARCHAR));
            row.add(new FieldValue(procedure.getReturnType(), Types.INTEGER));
            row.add(new FieldValue(procedure.getName(), Types.VARCHAR));
            values.add(row);
        }

        return new ParadoxResultSet(conn, null, values, columns);
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public ResultSet getProcedureColumns(final String catalog, final String schemaPattern, final String procedureNamePattern, final String columnNamePattern) throws SQLException {
        final ArrayList<Column> columns = new ArrayList<Column>(1);
        columns.add(new Column("PROCEDURE_CAT", Types.VARCHAR));
        columns.add(new Column("PROCEDURE_SCHEM", Types.VARCHAR));
        columns.add(new Column("PROCEDURE_NAME", Types.VARCHAR));
        columns.add(new Column(COLUMN_NAME, Types.VARCHAR));
        columns.add(new Column("COLUMN_TYPE", Types.INTEGER));
        columns.add(new Column("DATA_TYPE", Types.INTEGER));
        columns.add(new Column(TYPE_NAME, Types.VARCHAR));
        columns.add(new Column("PRECISION", Types.INTEGER));
        columns.add(new Column("LENGTH", Types.INTEGER));
        columns.add(new Column("SCALE", Types.INTEGER));
        columns.add(new Column("RADIX", Types.INTEGER));
        columns.add(new Column("NULLABLE", Types.INTEGER));
        columns.add(new Column(REMARKS, Types.VARCHAR));
        columns.add(new Column("COLUMN_DEF", Types.VARCHAR));
        columns.add(new Column("SQL_DATA_TYPE", Types.VARCHAR));
        columns.add(new Column("SQL_DATETIME_SUB", Types.VARCHAR));
        columns.add(new Column("CHAR_OCTET_LENGTH", Types.VARCHAR));
        columns.add(new Column("IS_NULLABLE", Types.VARCHAR));
        columns.add(new Column("SPECIFIC_NAME", Types.VARCHAR));

        final List<List<FieldValue>> values = new ArrayList<List<FieldValue>>();

        for (final CallableProcedure procedure : ProcedureAS.getInstance().list()) {
            if (Expressions.accept(procedure.getName(), procedureNamePattern)) {
                for (final ParadoxField field : procedure.getCols()) {
                    final ArrayList<FieldValue> row = new ArrayList<FieldValue>();
                    row.add(new FieldValue(conn.getCatalog(), Types.VARCHAR));
                    row.add(new FieldValue(conn.getSchema(), Types.VARCHAR));
                    row.add(new FieldValue(procedure.getName(), Types.VARCHAR));
                    row.add(new FieldValue(field.getName(), Types.VARCHAR));
                    row.add(new FieldValue(DatabaseMetaData.procedureColumnIn, Types.INTEGER));
                    row.add(new FieldValue(field.getSqlType(), Types.INTEGER));
                    row.add(new FieldValue(Column.getTypeName(field.getSqlType()), Types.VARCHAR));
                    row.add(new FieldValue(0, Types.INTEGER));
                    row.add(new FieldValue(field.getSize(), Types.INTEGER));
                    row.add(new FieldValue(0, Types.INTEGER));
                    row.add(new FieldValue(0, Types.INTEGER));
                    row.add(new FieldValue(DatabaseMetaData.procedureNullable));
                    row.add(new FieldValue(Types.VARCHAR));
                    row.add(new FieldValue(Types.VARCHAR));
                    row.add(new FieldValue(Types.VARCHAR));
                    row.add(new FieldValue(Types.VARCHAR));
                    row.add(new FieldValue(Types.VARCHAR));
                    row.add(new FieldValue(Types.VARCHAR));
                    row.add(new FieldValue("NO", Types.VARCHAR));
                    row.add(new FieldValue(procedure.getName(), Types.VARCHAR));
                    values.add(row);
                }
            }
        }

        return new ParadoxResultSet(conn, null, values, columns);
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public ResultSet getTables(final String catalog, final String schemaPattern, final String tableNamePattern, final String[] types) throws SQLException {
        final ArrayList<Column> columns = new ArrayList<Column>(1);
        columns.add(new Column(TABLE_CAT, Types.VARCHAR));
        columns.add(new Column(TABLE_SCHEM, Types.VARCHAR));
        columns.add(new Column("TABLE_NAME", Types.VARCHAR));
        columns.add(new Column("TABLE_TYPE", Types.VARCHAR));
        columns.add(new Column(REMARKS, Types.VARCHAR));
        columns.add(new Column("TYPE_CAT", Types.VARCHAR));
        columns.add(new Column("TYPE_SCHEM", Types.VARCHAR));
        columns.add(new Column(TYPE_NAME, Types.VARCHAR));
        columns.add(new Column("SELF_REFERENCING_COL_NAME", Types.VARCHAR));
        columns.add(new Column("REF_GENERATION", Types.VARCHAR));

        final List<List<FieldValue>> values = new ArrayList<List<FieldValue>>(1);

        if (types != null) {
            for (final String type : types) {
                if (TABLE.equalsIgnoreCase(type)) {
                    for (final ParadoxTable table : TableData.listTables(conn, tableNamePattern)) {
                        final ArrayList<FieldValue> row = new ArrayList<FieldValue>(1);
                        row.add(new FieldValue(conn.getCatalog(), Types.VARCHAR));
                        row.add(new FieldValue(conn.getSchema(), Types.VARCHAR));
                        row.add(new FieldValue(table.getName(), Types.VARCHAR));
                        row.add(new FieldValue(TABLE, Types.VARCHAR));
                        row.add(new FieldValue(Types.VARCHAR));
                        row.add(new FieldValue(Types.VARCHAR));
                        row.add(new FieldValue(Types.VARCHAR));
                        row.add(new FieldValue(Types.VARCHAR));
                        row.add(new FieldValue(Types.VARCHAR));
                        row.add(new FieldValue(Types.VARCHAR));
                        values.add(row);
                    }
                } else if ("VIEW".equalsIgnoreCase(type)) {
                    for (final ParadoxView view : ViewData.listViews(conn, tableNamePattern)) {
                        final ArrayList<FieldValue> row = new ArrayList<FieldValue>(1);
                        row.add(new FieldValue(conn.getCatalog(), Types.VARCHAR));
                        row.add(new FieldValue(conn.getSchema(), Types.VARCHAR));
                        row.add(new FieldValue(view.getName(), Types.VARCHAR));
                        row.add(new FieldValue("VIEW", Types.VARCHAR));
                        row.add(new FieldValue(Types.VARCHAR));
                        row.add(new FieldValue(Types.VARCHAR));
                        row.add(new FieldValue(Types.VARCHAR));
                        row.add(new FieldValue(Types.VARCHAR));
                        row.add(new FieldValue(Types.VARCHAR));
                        row.add(new FieldValue(Types.VARCHAR));
                        values.add(row);
                    }
                }
            }
        }
        return new ParadoxResultSet(conn, null, values, columns);
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public ResultSet getSchemas() throws SQLException {
        final ArrayList<Column> columns = new ArrayList<Column>(1);
        columns.add(new Column(TABLE_SCHEM, Types.VARCHAR));
        columns.add(new Column("TABLE_CATALOG", Types.VARCHAR));

        final ArrayList<FieldValue> row = new ArrayList<FieldValue>(1);
        final List<List<FieldValue>> values = new ArrayList<List<FieldValue>>(1);
        row.add(new FieldValue(conn.getSchema(), Types.VARCHAR));
        row.add(new FieldValue(conn.getCatalog(), Types.VARCHAR));
        values.add(row);

        return new ParadoxResultSet(conn, null, values, columns);
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public ResultSet getCatalogs() throws SQLException {
        final ArrayList<Column> columns = new ArrayList<Column>(1);
        columns.add(new Column(TABLE_CAT, Types.VARCHAR));

        final List<FieldValue> row = new ArrayList<FieldValue>(1);
        final List<List<FieldValue>> values = new ArrayList<List<FieldValue>>(1);
        row.add(new FieldValue(conn.getCatalog(), Types.VARCHAR));
        values.add(row);

        return new ParadoxResultSet(conn, null, values, columns);
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public ResultSet getTableTypes() throws SQLException {
        final ArrayList<Column> columns = new ArrayList<Column>(1);
        columns.add(new Column("TABLE_TYPE", Types.VARCHAR));

        final ArrayList<FieldValue> row = new ArrayList<FieldValue>(1);
        final List<List<FieldValue>> values = new ArrayList<List<FieldValue>>(1);
        row.add(new FieldValue(TABLE, Types.VARCHAR));
        row.add(new FieldValue("VIEW", Types.VARCHAR));
        row.add(new FieldValue("SYSTEM TABLE", Types.VARCHAR));
        values.add(row);

        return new ParadoxResultSet(conn, null, values, columns);
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public ResultSet getColumns(final String catalog, final String schemaPattern, final String tableNamePattern, final String columnNamePattern) throws SQLException {
        final ArrayList<Column> columns = new ArrayList<Column>(1);
        columns.add(new Column(TABLE_CAT, Types.VARCHAR));
        columns.add(new Column(TABLE_SCHEM, Types.VARCHAR));
        columns.add(new Column(TABLE_NAME, Types.VARCHAR));
        columns.add(new Column(COLUMN_NAME, Types.VARCHAR));
        columns.add(new Column("DATA_TYPE", Types.INTEGER));
        columns.add(new Column(TYPE_NAME, Types.VARCHAR));
        columns.add(new Column("COLUMN_SIZE", Types.INTEGER));
        columns.add(new Column("BUFFER_LENGTH", Types.INTEGER));
        columns.add(new Column("DECIMAL_DIGITS", Types.INTEGER));
        columns.add(new Column("NUM_PREC_RADIX", Types.INTEGER));
        columns.add(new Column("NULLABLE", Types.INTEGER));
        columns.add(new Column(REMARKS, Types.INTEGER));
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

        final List<List<FieldValue>> values = new ArrayList<List<FieldValue>>(1);

        final ArrayList<ParadoxTable> tables = TableData.listTables(conn);
        for (final ParadoxTable table : tables) {
            if (tableNamePattern == null || Expressions.accept(table.getName(), tableNamePattern)) {
                int ordinal = 1;
                for (final ParadoxField field : table.getFields()) {
                    if (columnNamePattern != null && !Expressions.accept(field.getName(), columnNamePattern)) {
                        continue;
                    }

                    final ArrayList<FieldValue> row = new ArrayList<FieldValue>();

                    final int type = field.getSqlType();
                    row.add(new FieldValue(conn.getCatalog(), Types.VARCHAR));
                    row.add(new FieldValue(conn.getSchema(), Types.VARCHAR));
                    row.add(new FieldValue(table.getName(), Types.VARCHAR));
                    row.add(new FieldValue(field.getAlias(), Types.VARCHAR));
                    row.add(new FieldValue(type, Types.INTEGER));
                    row.add(new FieldValue(Column.getTypeName(type), Types.VARCHAR));
                    row.add(new FieldValue(field.getSize(), Types.INTEGER));
                    row.add(new FieldValue(2048, Types.INTEGER));

                    switch (field.getType()) {
                        case 5:
                        case 6:
                            row.add(new FieldValue(2, Types.INTEGER));
                            break;
                        default:
                            row.add(new FieldValue(0, Types.INTEGER));
                            break;
                    }
                    row.add(new FieldValue(10, Types.INTEGER));
                    row.add(new FieldValue(DatabaseMetaData.columnNullableUnknown));
                    row.add(new FieldValue(Types.INTEGER));
                    row.add(new FieldValue(Types.VARCHAR));
                    row.add(new FieldValue(Types.INTEGER));
                    row.add(new FieldValue(Types.INTEGER));
                    row.add(new FieldValue(255, Types.INTEGER));
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
        }

        final List<? extends ParadoxDataFile> views = ViewData.listViews(conn);
        for (final ParadoxDataFile view : views) {
            if (tableNamePattern == null || Expressions.accept(view.getName(), tableNamePattern)) {
                int ordinal = 1;
                for (final ParadoxField field : view.getFields()) {
                    if (columnNamePattern != null && !Expressions.accept(field.getName(), columnNamePattern)) {
                        continue;
                    }

                    final ArrayList<FieldValue> row = new ArrayList<FieldValue>();

                    final int type = field.getSqlType();
                    row.add(new FieldValue(conn.getCatalog(), Types.VARCHAR));
                    row.add(new FieldValue(conn.getSchema(), Types.VARCHAR));
                    row.add(new FieldValue(view.getName(), Types.VARCHAR));
                    row.add(new FieldValue(field.getAlias(), Types.VARCHAR));
                    row.add(new FieldValue(type, Types.INTEGER));
                    row.add(new FieldValue(Column.getTypeName(type), Types.VARCHAR));
                    row.add(new FieldValue(field.getSize(), Types.INTEGER));
                    row.add(new FieldValue(2048, Types.INTEGER));

                    switch (field.getType()) {
                        case 5:
                        case 6:
                            row.add(new FieldValue(2, Types.INTEGER));
                            break;
                        default:
                            row.add(new FieldValue(0, Types.INTEGER));
                            break;
                    }
                    row.add(new FieldValue(10, Types.INTEGER));
                    row.add(new FieldValue(DatabaseMetaData.columnNullableUnknown, Types.INTEGER));
                    row.add(new FieldValue(Types.INTEGER));
                    row.add(new FieldValue(Types.VARCHAR));
                    row.add(new FieldValue(Types.INTEGER));
                    row.add(new FieldValue(Types.INTEGER));
                    row.add(new FieldValue(255, Types.INTEGER));
                    row.add(new FieldValue(ordinal, Types.INTEGER));
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
        }
        return new ParadoxResultSet(conn, null, values, columns);
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public ResultSet getColumnPrivileges(final String catalog, final String schema, final String table, final String columnNamePattern) throws SQLException {
        return new ParadoxResultSet(conn, null, new ArrayList<List<FieldValue>>(), new ArrayList<Column>());
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public ResultSet getTablePrivileges(final String catalog, final String schemaPattern, final String tableNamePattern) throws SQLException {
        return new ParadoxResultSet(conn, null, new ArrayList<List<FieldValue>>(), new ArrayList<Column>());
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public ResultSet getBestRowIdentifier(final String catalog, final String schema, final String table, final int scope, final boolean nullable) throws SQLException {
        return new ParadoxResultSet(conn, null, new ArrayList<List<FieldValue>>(), new ArrayList<Column>());
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public ResultSet getVersionColumns(final String catalog, final String schema, final String table) throws SQLException {
        return new ParadoxResultSet(conn, null, new ArrayList<List<FieldValue>>(), new ArrayList<Column>());
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public ResultSet getPrimaryKeys(final String catalog, final String schema, final String tableNamePattern) throws SQLException {
        final ArrayList<Column> columns = new ArrayList<Column>(1);
        columns.add(new Column(TABLE_CAT, Types.VARCHAR));
        columns.add(new Column(TABLE_SCHEM, Types.VARCHAR));
        columns.add(new Column(TABLE_NAME, Types.VARCHAR));
        columns.add(new Column(COLUMN_NAME, Types.VARCHAR));
        columns.add(new Column("KEY_SEQ", Types.INTEGER));
        columns.add(new Column("PK_NAME", Types.VARCHAR));

        final List<List<FieldValue>> values = new ArrayList<List<FieldValue>>(1);
        final ParadoxTable table = TableData.listTables(conn, tableNamePattern).get(0);

        int loop = 0;
        for (final ParadoxField pk : table.getPrimaryKeys()) {
            final ArrayList<FieldValue> row = new ArrayList<FieldValue>();
            row.add(new FieldValue(conn.getCatalog(), Types.VARCHAR));
            row.add(new FieldValue(conn.getSchema(), Types.VARCHAR));
            row.add(new FieldValue(table.getName(), Types.VARCHAR));
            row.add(new FieldValue(pk.getName(), Types.VARCHAR));
            row.add(new FieldValue(loop, Types.INTEGER));
            row.add(new FieldValue(pk.getName(), Types.VARCHAR));
            values.add(row);
            loop++;
        }
        return new ParadoxResultSet(conn, null, values, columns);
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public ResultSet getImportedKeys(final String catalog, final String schema, final String table) throws SQLException {
        return new ParadoxResultSet(conn, null, new ArrayList<List<FieldValue>>(), new ArrayList<Column>());
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public ResultSet getExportedKeys(final String catalog, final String schema, final String table) throws SQLException {
        return new ParadoxResultSet(conn, null, new ArrayList<List<FieldValue>>(), new ArrayList<Column>());
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public ResultSet getCrossReference(final String primaryCatalog, final String primarySchema, final String primaryTable, final String foreignCatalog, final String foreignSchema,
            final String foreignTable) throws SQLException {
        return new ParadoxResultSet(conn, null, new ArrayList<List<FieldValue>>(), new ArrayList<Column>());
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public ResultSet getTypeInfo() throws SQLException {
        return new ParadoxResultSet(conn, null, new ArrayList<List<FieldValue>>(), new ArrayList<Column>());
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public ResultSet getIndexInfo(final String catalog, final String schema, final String tableNamePattern, final boolean unique, final boolean approximate) throws SQLException {
        final ArrayList<Column> columns = new ArrayList<Column>(1);
        columns.add(new Column(TABLE_CAT, Types.VARCHAR));
        columns.add(new Column(TABLE_SCHEM, Types.VARCHAR));
        columns.add(new Column(TABLE_NAME, Types.VARCHAR));
        columns.add(new Column("NON_UNIQUE", Types.BOOLEAN));
        columns.add(new Column("INDEX_QUALIFIER", Types.VARCHAR));
        columns.add(new Column("INDEX_NAME", Types.VARCHAR));
        columns.add(new Column("TYPE", Types.INTEGER));
        columns.add(new Column("ORDINAL_POSITION", Types.INTEGER));
        columns.add(new Column(COLUMN_NAME, Types.VARCHAR));
        columns.add(new Column("ASC_OR_DESC", Types.VARCHAR));
        columns.add(new Column("CARDINALITY", Types.INTEGER));
        columns.add(new Column("PAGES", Types.INTEGER));
        columns.add(new Column("FILTER_CONDITION", Types.VARCHAR));

        final List<List<FieldValue>> values = new ArrayList<List<FieldValue>>(1);

        for (final ParadoxTable table : TableData.listTables(conn, tableNamePattern)) {
            final ParadoxPK primaryKeyIndex = PrimaryKeyData.getPrimaryKey(conn, table);

            if (primaryKeyIndex != null) {
                for (final ParadoxField pk : table.getPrimaryKeys()) {
                    final ArrayList<FieldValue> row = new ArrayList<FieldValue>();

                    row.add(new FieldValue(conn.getCatalog(), Types.VARCHAR));
                    row.add(new FieldValue(conn.getSchema(), Types.VARCHAR));
                    row.add(new FieldValue(table.getName(), Types.VARCHAR));
                    row.add(new FieldValue(Boolean.FALSE, Types.BOOLEAN));
                    row.add(new FieldValue(conn.getCatalog(), Types.VARCHAR));
                    row.add(new FieldValue(primaryKeyIndex.getName(), Types.VARCHAR));
                    row.add(new FieldValue(DatabaseMetaData.tableIndexHashed));
                    // FIXME get it from index file
                    row.add(new FieldValue(0, Types.INTEGER));
                    row.add(new FieldValue(pk.getName(), Types.VARCHAR));
                    row.add(new FieldValue("A", Types.VARCHAR));
                    // FIXME get it from index file
                    row.add(new FieldValue(0, Types.INTEGER));
                    row.add(new FieldValue(0, Types.INTEGER));
                    // row.add(String.valueOf(pk.getRowCount()));
                    // row.add(String.valueOf(pk.getUsedBlocks()));
                    row.add(null);

                    values.add(row);
                }
            }

            for (final ParadoxIndex index : IndexData.listIndexes(conn, tableNamePattern)) {
                int ordinal = 0;
                final ArrayList<FieldValue> row = new ArrayList<FieldValue>();
                for (final ParadoxField field : index.getFields()) {

                    row.add(new FieldValue(conn.getCatalog(), Types.VARCHAR));
                    row.add(new FieldValue(conn.getSchema(), Types.VARCHAR));
                    row.add(new FieldValue(index.getFatherName(), Types.VARCHAR));
                    row.add(new FieldValue(Boolean.FALSE, Types.BOOLEAN));
                    row.add(new FieldValue(conn.getCatalog(), Types.VARCHAR));
                    row.add(new FieldValue(index.getName(), Types.VARCHAR));
                    row.add(new FieldValue(DatabaseMetaData.tableIndexHashed, Types.INTEGER));
                    row.add(new FieldValue(ordinal, Types.INTEGER));
                    row.add(new FieldValue(field.getName(), Types.VARCHAR));
                    row.add(new FieldValue(index.getOrder(), Types.VARCHAR));
                    // FIXME get it from index file
                    row.add(new FieldValue(0, Types.INTEGER));
                    row.add(new FieldValue(0, Types.INTEGER));
                    // row.add(String.valueOf(pk.getRowCount()));
                    // row.add(String.valueOf(pk.getUsedBlocks()));
                    row.add(null);

                    values.add(row);
                    ordinal++;
                }
            }
        }
        return new ParadoxResultSet(conn, null, values, columns);
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsResultSetType(final int type) throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsResultSetConcurrency(final int type, final int concurrency) throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean ownUpdatesAreVisible(final int type) throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean ownDeletesAreVisible(final int type) throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean ownInsertsAreVisible(final int type) throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean othersUpdatesAreVisible(final int type) throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean othersDeletesAreVisible(final int type) throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean othersInsertsAreVisible(final int type) throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean updatesAreDetected(final int type) throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean deletesAreDetected(final int type) throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean insertsAreDetected(final int type) throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean supportsBatchUpdates() throws SQLException {
        return false;
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public ResultSet getUDTs(final String catalog, final String schemaPattern, final String typeNamePattern, final int[] types) throws SQLException {
        return new ParadoxResultSet(conn, null, new ArrayList<List<FieldValue>>(), new ArrayList<Column>());
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection() throws SQLException {
        return conn;
    }

    @Override
    public boolean supportsSavepoints() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsNamedParameters() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsMultipleOpenResults() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsGetGeneratedKeys() throws SQLException {
        return false;
    }

    @Override
    public ResultSet getSuperTypes(final String catalog, final String schemaPattern, final String typeNamePattern) throws SQLException {
        return new ParadoxResultSet(conn, null, new ArrayList<List<FieldValue>>(), new ArrayList<Column>());
    }

    @Override
    public ResultSet getSuperTables(final String catalog, final String schemaPattern, final String tableNamePattern) throws SQLException {
        return new ParadoxResultSet(conn, null, new ArrayList<List<FieldValue>>(), new ArrayList<Column>());
    }

    @Override
    public ResultSet getAttributes(final String catalog, final String schemaPattern, final String typeNamePattern, final String attributeNamePattern) throws SQLException {
        return new ParadoxResultSet(conn, null, new ArrayList<List<FieldValue>>(), new ArrayList<Column>());
    }

    @Override
    public boolean supportsResultSetHoldability(final int holdability) throws SQLException {
        return false;
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return conn.getHoldability();
    }

    @Override
    public int getDatabaseMajorVersion() throws SQLException {
        return 7;
    }

    @Override
    public int getDatabaseMinorVersion() throws SQLException {
        return 0;
    }

    @Override
    public int getJDBCMajorVersion() throws SQLException {
        return 4;
    }

    @Override
    public int getJDBCMinorVersion() throws SQLException {
        return 0;
    }

    @Override
    public int getSQLStateType() throws SQLException {
        return 0;
    }

    @Override
    public boolean locatorsUpdateCopy() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsStatementPooling() throws SQLException {
        return false;
    }

    @Override
    public RowIdLifetime getRowIdLifetime() throws SQLException {
        return RowIdLifetime.ROWID_UNSUPPORTED;
    }

    @Override
    public ResultSet getSchemas(final String catalog, final String schemaPattern) throws SQLException {
        if (catalog != null && !Expressions.accept(conn.getCatalog(), catalog) || schemaPattern != null && !Expressions.accept(conn.getSchema(), schemaPattern)) {
            return new ParadoxResultSet(conn, null, new ArrayList<List<FieldValue>>(), new ArrayList<Column>());
        }
        return getSchemas();
    }

    @Override
    public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
        return true;
    }

    @Override
    public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
        return false;
    }

    @Override
    public ResultSet getClientInfoProperties() throws SQLException {
        return new ParadoxResultSet(conn, null, new ArrayList<List<FieldValue>>(), new ArrayList<Column>());
    }

    @Override
    public ResultSet getFunctions(final String catalog, final String schemaPattern, final String functionNamePattern) throws SQLException {
        return new ParadoxResultSet(conn, null, new ArrayList<List<FieldValue>>(), new ArrayList<Column>());
    }

    @Override
    public ResultSet getFunctionColumns(final String catalog, final String schemaPattern, final String functionNamePattern, final String columnNamePattern) throws SQLException {
        return new ParadoxResultSet(conn, null, new ArrayList<List<FieldValue>>(), new ArrayList<Column>());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        if (isWrapperFor(iface)) {
            return (T) this;
        }
        throw new SQLException("Type not found.", SQLStates.TYPE_NOT_FOUND);
    }

    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return getClass().isAssignableFrom(iface);
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public ResultSet getPseudoColumns(final String catalog, final String schemaPattern, final String tableNamePattern, final String columnNamePattern) throws SQLException {
        return new ParadoxResultSet(conn, null, new ArrayList<List<FieldValue>>(), new ArrayList<Column>());
    }

    /**
     * *
     * {@inheritDoc}
     */
    @Override
    public boolean generatedKeyAlwaysReturned() throws SQLException {
        return true;
    }
}
