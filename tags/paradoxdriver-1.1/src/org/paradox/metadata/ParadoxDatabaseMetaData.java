package org.paradox.metadata;

import org.paradox.ParadoxConnection;
import org.paradox.ParadoxResultSet;
import org.paradox.utils.SQLStates;
import org.paradox.results.ColumnDTO;
import org.paradox.utils.Constants;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import static org.paradox.data.IndexData.listIndexes;
import static org.paradox.data.PrimaryKeyData.getPrimaryKey;
import static org.paradox.data.TableData.listTables;
import static org.paradox.data.ViewData.listViews;
import org.paradox.data.table.value.AbstractFieldValue;
import org.paradox.data.table.value.BooleanValue;
import org.paradox.data.table.value.IntegerValue;
import org.paradox.data.table.value.StringValue;
import org.paradox.procedures.CallableProcedure;
import static org.paradox.procedures.ProcedureAS.getInstance;
import static org.paradox.results.ColumnDTO.getTypeName;
import static org.paradox.utils.Expressions.accept;

/**
 *
 * @author leonardocosta
 */
public class ParadoxDatabaseMetaData implements DatabaseMetaData {

    private final ParadoxConnection conn;

    public ParadoxDatabaseMetaData(final ParadoxConnection conn) {
        this.conn = conn;
    }

    @Override
    public boolean allProceduresAreCallable() throws SQLException {
        return false;
    }

    @Override
    public boolean allTablesAreSelectable() throws SQLException {
        return true;
    }

    @Override
    public String getURL() throws SQLException {
        return conn.getUrl();
    }

    public String getUserName() throws SQLException {
        return "SYSTEM";
    }

    public boolean isReadOnly() throws SQLException {
        return true;

    }

    public boolean nullsAreSortedHigh() throws SQLException {
        return false;
    }

    public boolean nullsAreSortedLow() throws SQLException {
        return true;
    }

    public boolean nullsAreSortedAtStart() throws SQLException {
        return false;
    }

    public boolean nullsAreSortedAtEnd() throws SQLException {
        return true;
    }

    public String getDatabaseProductName() throws SQLException {
        return Constants.DRIVER_NAME;
    }

    public String getDatabaseProductVersion() throws SQLException {
        return Constants.DRIVER_NAME + " " + Constants.DRIVER_VERSION;
    }

    public String getDriverName() throws SQLException {
        return Constants.DRIVER_NAME;
    }

    public String getDriverVersion() throws SQLException {
        return Constants.DRIVER_VERSION;
    }

    public int getDriverMajorVersion() {
        return Constants.MAJOR_VERSION;
    }

    public int getDriverMinorVersion() {
        return Constants.MINOR_VERSION;
    }

    public boolean usesLocalFiles() throws SQLException {
        return true;
    }

    public boolean usesLocalFilePerTable() throws SQLException {
        return true;
    }

    public boolean supportsMixedCaseIdentifiers() throws SQLException {
        return true;
    }

    public boolean storesUpperCaseIdentifiers() throws SQLException {
        return true;
    }

    public boolean storesLowerCaseIdentifiers() throws SQLException {
        return true;
    }

    public boolean storesMixedCaseIdentifiers() throws SQLException {
        return true;
    }

    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        return true;
    }

    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        return true;
    }

    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        return true;
    }

    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        return true;
    }

    public String getIdentifierQuoteString() throws SQLException {
        return "\"";
    }

    public String getSQLKeywords() throws SQLException {
        return "SELECT";
    }

    public String getNumericFunctions() throws SQLException {
        return "AVERANGE,SUM";
    }

    public String getStringFunctions() throws SQLException {
        return null;
    }

    public String getSystemFunctions() throws SQLException {
        return null;
    }

    public String getTimeDateFunctions() throws SQLException {
        return null;
    }

    public String getSearchStringEscape() throws SQLException {
        return "\\";
    }

    public String getExtraNameCharacters() throws SQLException {
        return "";
    }

    public boolean supportsAlterTableWithAddColumn() throws SQLException {
        return false;
    }

    public boolean supportsAlterTableWithDropColumn() throws SQLException {
        return false;
    }

    public boolean supportsColumnAliasing() throws SQLException {
        return true;
    }

    public boolean nullPlusNonNullIsNull() throws SQLException {
        return false;
    }

    public boolean supportsConvert() throws SQLException {
        return false;
    }

    public boolean supportsConvert(int fromType, int toType) throws SQLException {
        return false;
    }

    public boolean supportsTableCorrelationNames() throws SQLException {
        return false;
    }

    public boolean supportsDifferentTableCorrelationNames() throws SQLException {
        return false;
    }

    public boolean supportsExpressionsInOrderBy() throws SQLException {
        return false;
    }

    public boolean supportsOrderByUnrelated() throws SQLException {
        return false;
    }

    public boolean supportsGroupBy() throws SQLException {
        return false;
    }

    public boolean supportsGroupByUnrelated() throws SQLException {
        return false;
    }

    public boolean supportsGroupByBeyondSelect() throws SQLException {
        return false;
    }

    public boolean supportsLikeEscapeClause() throws SQLException {
        return false;
    }

    public boolean supportsMultipleResultSets() throws SQLException {
        return false;
    }

    public boolean supportsMultipleTransactions() throws SQLException {
        return false;
    }

    public boolean supportsNonNullableColumns() throws SQLException {
        return true;
    }

    public boolean supportsMinimumSQLGrammar() throws SQLException {
        return true;
    }

    public boolean supportsCoreSQLGrammar() throws SQLException {
        return true;
    }

    public boolean supportsExtendedSQLGrammar() throws SQLException {
        return false;
    }

    public boolean supportsANSI92EntryLevelSQL() throws SQLException {
        return false;
    }

    public boolean supportsANSI92IntermediateSQL() throws SQLException {
        return false;
    }

    public boolean supportsANSI92FullSQL() throws SQLException {
        return false;
    }

    public boolean supportsIntegrityEnhancementFacility() throws SQLException {
        return false;
    }

    public boolean supportsOuterJoins() throws SQLException {
        return false;
    }

    public boolean supportsFullOuterJoins() throws SQLException {
        return false;
    }

    public boolean supportsLimitedOuterJoins() throws SQLException {
        return false;
    }

    public String getSchemaTerm() throws SQLException {
        return "SCHEMA";
    }

    public String getProcedureTerm() throws SQLException {
        return "PROCEDURE";
    }

    public String getCatalogTerm() throws SQLException {
        return "CATALOG";
    }

    public boolean isCatalogAtStart() throws SQLException {
        return true;
    }

    public String getCatalogSeparator() throws SQLException {
        return ".";
    }

    public boolean supportsSchemasInDataManipulation() throws SQLException {
        return false;
    }

    public boolean supportsSchemasInProcedureCalls() throws SQLException {
        return false;
    }

    public boolean supportsSchemasInTableDefinitions() throws SQLException {
        return false;
    }

    public boolean supportsSchemasInIndexDefinitions() throws SQLException {
        return false;
    }

    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        return false;
    }

    public boolean supportsCatalogsInDataManipulation() throws SQLException {
        return false;
    }

    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        return false;
    }

    public boolean supportsCatalogsInTableDefinitions() throws SQLException {
        return false;
    }

    public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        return false;
    }

    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        return false;
    }

    public boolean supportsPositionedDelete() throws SQLException {
        return false;
    }

    public boolean supportsPositionedUpdate() throws SQLException {
        return false;
    }

    public boolean supportsSelectForUpdate() throws SQLException {
        return false;
    }

    public boolean supportsStoredProcedures() throws SQLException {
        return false;
    }

    public boolean supportsSubqueriesInComparisons() throws SQLException {
        return false;
    }

    public boolean supportsSubqueriesInExists() throws SQLException {
        return false;
    }

    public boolean supportsSubqueriesInIns() throws SQLException {
        return false;
    }

    public boolean supportsSubqueriesInQuantifieds() throws SQLException {
        return false;
    }

    public boolean supportsCorrelatedSubqueries() throws SQLException {
        return false;
    }

    public boolean supportsUnion() throws SQLException {
        return false;
    }

    public boolean supportsUnionAll() throws SQLException {
        return false;
    }

    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        return false;
    }

    public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        return false;
    }

    public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        return false;
    }

    public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        return false;
    }

    public int getMaxBinaryLiteralLength() throws SQLException {
        return 8;
    }

    public int getMaxCharLiteralLength() throws SQLException {
        return 255;
    }

    public int getMaxColumnNameLength() throws SQLException {
        return 8;
    }

    public int getMaxColumnsInGroupBy() throws SQLException {
        return 255;
    }

    public int getMaxColumnsInIndex() throws SQLException {
        return 255;
    }

    public int getMaxColumnsInOrderBy() throws SQLException {
        return 255;
    }

    public int getMaxColumnsInSelect() throws SQLException {
        return 255;
    }

    public int getMaxColumnsInTable() throws SQLException {
        return 255;
    }

    public int getMaxConnections() throws SQLException {
        return 1;
    }

    public int getMaxCursorNameLength() throws SQLException {
        return 255;
    }

    public int getMaxIndexLength() throws SQLException {
        return 255;
    }

    public int getMaxSchemaNameLength() throws SQLException {
        return 255;
    }

    public int getMaxProcedureNameLength() throws SQLException {
        return 255;
    }

    public int getMaxCatalogNameLength() throws SQLException {
        return 255;
    }

    public int getMaxRowSize() throws SQLException {
        return 255;
    }

    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        return true;
    }

    public int getMaxStatementLength() throws SQLException {
        return Integer.MAX_VALUE;
    }

    public int getMaxStatements() throws SQLException {
        return Integer.MAX_VALUE;
    }

    public int getMaxTableNameLength() throws SQLException {
        return 255;
    }

    public int getMaxTablesInSelect() throws SQLException {
        return 255;
    }

    public int getMaxUserNameLength() throws SQLException {
        return 255;
    }

    public int getDefaultTransactionIsolation() throws SQLException {
        return Connection.TRANSACTION_NONE;
    }

    public boolean supportsTransactions() throws SQLException {
        return false;
    }

    public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
        return Connection.TRANSACTION_NONE != level;
    }

    public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
        return false;
    }

    public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
        return false;
    }

    public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
        return false;
    }

    public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
        return false;
    }

    public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
        final ArrayList<ColumnDTO> columns = new ArrayList<ColumnDTO>(1);
        columns.add(new ColumnDTO("PROCEDURE_CAT", Types.VARCHAR));
        columns.add(new ColumnDTO("PROCEDURE_SCHEM", Types.VARCHAR));
        columns.add(new ColumnDTO("PROCEDURE_NAME", Types.VARCHAR));
        columns.add(new ColumnDTO("Reserved1", Types.VARCHAR));
        columns.add(new ColumnDTO("Reserved2", Types.VARCHAR));
        columns.add(new ColumnDTO("Reserved3", Types.VARCHAR));
        columns.add(new ColumnDTO("REMARKS", Types.VARCHAR)); // Comment
        columns.add(new ColumnDTO("PROCEDURE_TYPE", Types.INTEGER));
        columns.add(new ColumnDTO("SPECIFIC_NAME", Types.VARCHAR));

        final ArrayList<ArrayList<AbstractFieldValue>> values = new ArrayList<ArrayList<AbstractFieldValue>>();

        for (final CallableProcedure procedure : getInstance().list()) {
            final ArrayList<AbstractFieldValue> row = new ArrayList<AbstractFieldValue>();
            row.add(new StringValue(conn.getCatalog()));
            row.add(new StringValue(conn.getSchema()));
            row.add(new StringValue(procedure.getName()));
            row.add(new StringValue());
            row.add(new StringValue());
            row.add(new StringValue());
            row.add(new StringValue(procedure.getRemarks()));
            row.add(new IntegerValue(procedure.getReturnType()));
            row.add(new StringValue(procedure.getName()));
            values.add(row);
        }

        return new ParadoxResultSet(conn, null, values, columns);
    }

    public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException {
        final ArrayList<ColumnDTO> columns = new ArrayList<ColumnDTO>(1);
        columns.add(new ColumnDTO("PROCEDURE_CAT", Types.VARCHAR));
        columns.add(new ColumnDTO("PROCEDURE_SCHEM", Types.VARCHAR));
        columns.add(new ColumnDTO("PROCEDURE_NAME", Types.VARCHAR));
        columns.add(new ColumnDTO("COLUMN_NAME", Types.VARCHAR));
        columns.add(new ColumnDTO("COLUMN_TYPE", Types.INTEGER));
        columns.add(new ColumnDTO("DATA_TYPE", Types.INTEGER));
        columns.add(new ColumnDTO("TYPE_NAME", Types.VARCHAR));
        columns.add(new ColumnDTO("PRECISION", Types.INTEGER));
        columns.add(new ColumnDTO("LENGTH", Types.INTEGER));
        columns.add(new ColumnDTO("SCALE", Types.INTEGER));
        columns.add(new ColumnDTO("RADIX", Types.INTEGER));
        columns.add(new ColumnDTO("NULLABLE", Types.INTEGER));
        columns.add(new ColumnDTO("REMARKS", Types.VARCHAR));
        columns.add(new ColumnDTO("COLUMN_DEF", Types.VARCHAR));
        columns.add(new ColumnDTO("SQL_DATA_TYPE", Types.VARCHAR));
        columns.add(new ColumnDTO("SQL_DATETIME_SUB", Types.VARCHAR));
        columns.add(new ColumnDTO("CHAR_OCTET_LENGTH", Types.VARCHAR));
        columns.add(new ColumnDTO("IS_NULLABLE", Types.VARCHAR));
        columns.add(new ColumnDTO("SPECIFIC_NAME", Types.VARCHAR));

        final ArrayList<ArrayList<AbstractFieldValue>> values = new ArrayList<ArrayList<AbstractFieldValue>>();

        for (final CallableProcedure procedure : getInstance().list()) {
            if (accept(procedure.getName(), procedureNamePattern)) {
                for (final ParadoxField field : procedure.getCols()) {
                    final ArrayList<AbstractFieldValue> row = new ArrayList<AbstractFieldValue>();
                    row.add(new StringValue(conn.getCatalog()));
                    row.add(new StringValue(conn.getSchema()));
                    row.add(new StringValue(procedure.getName()));
                    row.add(new StringValue(field.getName()));
                    row.add(new IntegerValue((DatabaseMetaData.procedureColumnIn)));
                    row.add(new IntegerValue((field.getSqlType())));
                    row.add(new StringValue(getTypeName(field.getSqlType())));
                    row.add(new IntegerValue(0));
                    row.add(new IntegerValue(field.getSize()));
                    row.add(new IntegerValue(0));
                    row.add(new IntegerValue(0));
                    row.add(new IntegerValue(DatabaseMetaData.procedureNullable));
                    row.add(null);
                    row.add(null);
                    row.add(null);
                    row.add(null);
                    row.add(null);
                    row.add(null);
                    row.add(new StringValue("NO"));
                    row.add(new StringValue(procedure.getName()));
                    values.add(row);
                }
            }
        }

        return new ParadoxResultSet(conn, null, values, columns);
    }

    public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
        final ArrayList<ColumnDTO> columns = new ArrayList<ColumnDTO>(1);
        columns.add(new ColumnDTO("TABLE_CAT", Types.VARCHAR));
        columns.add(new ColumnDTO("TABLE_SCHEM", Types.VARCHAR));
        columns.add(new ColumnDTO("TABLE_NAME", Types.VARCHAR));
        columns.add(new ColumnDTO("TABLE_TYPE", Types.VARCHAR));
        columns.add(new ColumnDTO("REMARKS", Types.VARCHAR));
        columns.add(new ColumnDTO("TYPE_CAT", Types.VARCHAR));
        columns.add(new ColumnDTO("TYPE_SCHEM", Types.VARCHAR));
        columns.add(new ColumnDTO("TYPE_NAME", Types.VARCHAR));
        columns.add(new ColumnDTO("SELF_REFERENCING_COL_NAME", Types.VARCHAR));
        columns.add(new ColumnDTO("REF_GENERATION", Types.VARCHAR));

        final ArrayList<ArrayList<AbstractFieldValue>> values = new ArrayList<ArrayList<AbstractFieldValue>>(1);

        if (types != null) {
            for (final String type : types) {
                if ("TABLE".equalsIgnoreCase(type)) {
                    for (final ParadoxTable table : listTables(conn, tableNamePattern)) {
                        final ArrayList<AbstractFieldValue> row = new ArrayList<AbstractFieldValue>(1);
                        row.add(new StringValue(conn.getCatalog()));
                        row.add(new StringValue(conn.getSchema()));
                        row.add(new StringValue(table.getName()));
                        row.add(new StringValue("TABLE"));
                        row.add(new StringValue());
                        row.add(null);
                        row.add(null);
                        row.add(null);
                        // FIXME col id
                        row.add(null);
                        row.add(null);
                        values.add(row);
                    }
                } else if ("VIEW".equalsIgnoreCase(type)) {
                    for (final ParadoxView view : listViews(conn, tableNamePattern)) {
                        final ArrayList<AbstractFieldValue> row = new ArrayList<AbstractFieldValue>(1);
                        row.add(new StringValue(conn.getCatalog()));
                        row.add(new StringValue(conn.getSchema()));
                        row.add(new StringValue(view.getName()));
                        row.add(new StringValue("VIEW"));
                        row.add(null);
                        row.add(null);
                        row.add(null);
                        row.add(null);
                        row.add(null);
                        row.add(null);
                        values.add(row);
                    }
                }
            }
        }
        return new ParadoxResultSet(conn, null, values, columns);
    }

    public ResultSet getSchemas() throws SQLException {
        final ArrayList<ColumnDTO> columns = new ArrayList<ColumnDTO>(1);
        columns.add(new ColumnDTO("TABLE_SCHEM", Types.VARCHAR));
        columns.add(new ColumnDTO("TABLE_CATALOG", Types.VARCHAR));

        final ArrayList<AbstractFieldValue> row = new ArrayList<AbstractFieldValue>(1);
        final ArrayList<ArrayList<AbstractFieldValue>> values = new ArrayList<ArrayList<AbstractFieldValue>>(1);
        row.add(new StringValue(conn.getSchema()));
        row.add(new StringValue(conn.getCatalog()));
        values.add(row);

        return new ParadoxResultSet(conn, null, values, columns);
    }

    public ResultSet getCatalogs() throws SQLException {
        final ArrayList<ColumnDTO> columns = new ArrayList<ColumnDTO>(1);
        columns.add(new ColumnDTO("TABLE_CAT", Types.VARCHAR));

        final ArrayList<AbstractFieldValue> row = new ArrayList<AbstractFieldValue>(1);
        final ArrayList<ArrayList<AbstractFieldValue>> values = new ArrayList<ArrayList<AbstractFieldValue>>(1);
        row.add(new StringValue(conn.getCatalog()));
        values.add(row);

        return new ParadoxResultSet(conn, null, values, columns);
    }

    public ResultSet getTableTypes() throws SQLException {
        final ArrayList<ColumnDTO> columns = new ArrayList<ColumnDTO>(1);
        columns.add(new ColumnDTO("TABLE_TYPE", Types.VARCHAR));

        final ArrayList<AbstractFieldValue> row = new ArrayList<AbstractFieldValue>(1);
        final ArrayList<ArrayList<AbstractFieldValue>> values = new ArrayList<ArrayList<AbstractFieldValue>>(1);
        row.add(new StringValue("TABLE"));
        row.add(new StringValue("VIEW"));
        row.add(new StringValue("SYSTEM TABLE"));
        values.add(row);

        return new ParadoxResultSet(conn, null, values, columns);
    }

    public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
        final ArrayList<ColumnDTO> columns = new ArrayList<ColumnDTO>(1);
        columns.add(new ColumnDTO("TABLE_CAT", Types.VARCHAR));
        columns.add(new ColumnDTO("TABLE_SCHEM", Types.VARCHAR));
        columns.add(new ColumnDTO("TABLE_NAME", Types.VARCHAR));
        columns.add(new ColumnDTO("COLUMN_NAME", Types.VARCHAR));
        columns.add(new ColumnDTO("DATA_TYPE", Types.INTEGER));
        columns.add(new ColumnDTO("TYPE_NAME", Types.VARCHAR));
        columns.add(new ColumnDTO("COLUMN_SIZE", Types.INTEGER));
        columns.add(new ColumnDTO("BUFFER_LENGTH", Types.INTEGER));
        columns.add(new ColumnDTO("DECIMAL_DIGITS", Types.INTEGER));
        columns.add(new ColumnDTO("NUM_PREC_RADIX", Types.INTEGER));
        columns.add(new ColumnDTO("NULLABLE", Types.INTEGER));
        columns.add(new ColumnDTO("REMARKS", Types.INTEGER));
        columns.add(new ColumnDTO("COLUMN_DEF", Types.VARCHAR));
        columns.add(new ColumnDTO("SQL_DATA_TYPE", Types.INTEGER));
        columns.add(new ColumnDTO("SQL_DATETIME_SUB", Types.INTEGER));
        columns.add(new ColumnDTO("CHAR_OCTET_LENGTH", Types.INTEGER));
        columns.add(new ColumnDTO("ORDINAL_POSITION", Types.INTEGER));
        columns.add(new ColumnDTO("IS_NULLABLE", Types.INTEGER));
        columns.add(new ColumnDTO("SCOPE_CATLOG", Types.VARCHAR));
        columns.add(new ColumnDTO("SCOPE_SCHEMA", Types.VARCHAR));
        columns.add(new ColumnDTO("SCOPE_TABLE", Types.VARCHAR));
        columns.add(new ColumnDTO("SOURCE_DATA_TYPE", Types.SMALLINT));
        columns.add(new ColumnDTO("IS_AUTOINCREMENT", Types.VARCHAR));

        final ArrayList<ArrayList<AbstractFieldValue>> values = new ArrayList<ArrayList<AbstractFieldValue>>(1);

        final ArrayList<ParadoxTable> tables = listTables(conn);
        for (final ParadoxTable table : tables) {
            if (tableNamePattern == null || accept(table.getName(), tableNamePattern)) {
                int ordinal = 1;
                for (final ParadoxField field : table.getFields()) {
                    if (columnNamePattern != null && !accept(field.getName(), columnNamePattern)) {
                        continue;
                    }

                    final ArrayList<AbstractFieldValue> row = new ArrayList<AbstractFieldValue>();

                    final int type = field.getSqlType();
                    row.add(new StringValue(conn.getCatalog()));
                    row.add(new StringValue(conn.getSchema()));
                    row.add(new StringValue(table.getName()));
                    row.add(new StringValue(field.getAlias()));
                    row.add(new IntegerValue(type));
                    row.add(new StringValue(getTypeName(type)));
                    row.add(new IntegerValue(field.getSize()));
                    row.add(new IntegerValue(2048));

                    switch (field.getType()) {
                        case 5:
                        case 6:
                            row.add(new IntegerValue(2));
                            break;
                        default:
                            row.add(new IntegerValue(0));
                            break;
                    }
                    row.add(new IntegerValue(10));
                    row.add(new IntegerValue(columnNullableUnknown));
                    row.add(null);
                    row.add(null);
                    row.add(null);
                    row.add(null);
                    row.add(new IntegerValue(255));
                    row.add(new IntegerValue(ordinal));
                    row.add(new StringValue("YES"));
                    row.add(null);
                    row.add(null);
                    row.add(null);
                    row.add(new IntegerValue(type));
                    if (field.isAutoIncrement()) {
                        row.add(new StringValue("YES"));
                    } else {
                        row.add(new StringValue("NO"));
                    }
                    ordinal++;
                    values.add(row);
                }
            }
        }

        final ArrayList<? extends AbstractTable> views = listViews(conn);
        for (final AbstractTable view : views) {
            if (tableNamePattern == null || accept(view.getName(), tableNamePattern)) {
                int ordinal = 1;
                for (final ParadoxField field : view.getFields()) {
                    if (columnNamePattern != null && !accept(field.getName(), columnNamePattern)) {
                        continue;
                    }

                    final ArrayList<AbstractFieldValue> row = new ArrayList<AbstractFieldValue>();

                    final int type = field.getSqlType();
                    row.add(new StringValue(conn.getCatalog()));
                    row.add(new StringValue(conn.getSchema()));
                    row.add(new StringValue(view.getName()));
                    row.add(new StringValue(field.getAlias()));
                    row.add(new IntegerValue(type));
                    row.add(new StringValue(getTypeName(type)));
                    row.add(new IntegerValue(field.getSize()));
                    row.add(new IntegerValue(2048));

                    switch (field.getType()) {
                        case 5:
                        case 6:
                            row.add(new IntegerValue(2));
                            break;
                        default:
                            row.add(new IntegerValue(0));
                            break;
                    }
                    row.add(new IntegerValue(10));
                    row.add(new IntegerValue(columnNullableUnknown));
                    row.add(null);
                    row.add(null);
                    row.add(null);
                    row.add(null);
                    row.add(new IntegerValue(255));
                    row.add(new IntegerValue(ordinal));
                    row.add(new StringValue("YES"));
                    row.add(null);
                    row.add(null);
                    row.add(null);
                    row.add(new IntegerValue(type));
                    if (field.isAutoIncrement()) {
                        row.add(new StringValue("YES"));
                    } else {
                        row.add(new StringValue("NO"));
                    }
                    ordinal++;
                    values.add(row);
                }
            }
        }
        return new ParadoxResultSet(conn, null, values, columns);
    }

    public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException {
        return new ParadoxResultSet(conn, null, new ArrayList<ArrayList<AbstractFieldValue>>(), new ArrayList<ColumnDTO>());
    }

    public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
        return new ParadoxResultSet(conn, null, new ArrayList<ArrayList<AbstractFieldValue>>(), new ArrayList<ColumnDTO>());
    }

    public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException {
        return new ParadoxResultSet(conn, null, new ArrayList<ArrayList<AbstractFieldValue>>(), new ArrayList<ColumnDTO>());
    }

    public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException {
        return new ParadoxResultSet(conn, null, new ArrayList<ArrayList<AbstractFieldValue>>(), new ArrayList<ColumnDTO>());
    }

    public ResultSet getPrimaryKeys(final String catalog, final String schema, final String tableNamePattern) throws SQLException {
        final ArrayList<ColumnDTO> columns = new ArrayList<ColumnDTO>(1);
        columns.add(new ColumnDTO("TABLE_CAT", Types.VARCHAR));
        columns.add(new ColumnDTO("TABLE_SCHEM", Types.VARCHAR));
        columns.add(new ColumnDTO("TABLE_NAME", Types.VARCHAR));
        columns.add(new ColumnDTO("COLUMN_NAME", Types.VARCHAR));
        columns.add(new ColumnDTO("KEY_SEQ", Types.INTEGER));
        columns.add(new ColumnDTO("PK_NAME", Types.VARCHAR));

        final ArrayList<ArrayList<AbstractFieldValue>> values = new ArrayList<ArrayList<AbstractFieldValue>>(1);

        final ParadoxTable table = listTables(conn, tableNamePattern).get(0);

        int loop = 0;
        for (final ParadoxField pk : table.getPrimaryKeys()) {
            final ArrayList<AbstractFieldValue> row = new ArrayList<AbstractFieldValue>();
            row.add(new StringValue(conn.getCatalog()));
            row.add(new StringValue(conn.getSchema()));
            row.add(new StringValue(table.getName()));
            row.add(new StringValue(pk.getName()));
            row.add(new IntegerValue(loop));
            row.add(new StringValue(pk.getName()));
            values.add(row);
            loop++;
        }
        return new ParadoxResultSet(conn, null, values, columns);
    }

    public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
        return new ParadoxResultSet(conn, null, new ArrayList<ArrayList<AbstractFieldValue>>(), new ArrayList<ColumnDTO>());
    }

    public ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException {
        return new ParadoxResultSet(conn, null, new ArrayList<ArrayList<AbstractFieldValue>>(), new ArrayList<ColumnDTO>());
    }

    public ResultSet getCrossReference(String primaryCatalog, String primarySchema, String primaryTable, String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException {
        return new ParadoxResultSet(conn, null, new ArrayList<ArrayList<AbstractFieldValue>>(), new ArrayList<ColumnDTO>());
    }

    public ResultSet getTypeInfo() throws SQLException {
        return new ParadoxResultSet(conn, null, new ArrayList<ArrayList<AbstractFieldValue>>(), new ArrayList<ColumnDTO>());
    }

    public ResultSet getIndexInfo(final String catalog, final String schema, final String tableNamePattern, final boolean unique, final boolean approximate) throws SQLException {
        final ArrayList<ColumnDTO> columns = new ArrayList<ColumnDTO>(1);
        columns.add(new ColumnDTO("TABLE_CAT", Types.VARCHAR));
        columns.add(new ColumnDTO("TABLE_SCHEM", Types.VARCHAR));
        columns.add(new ColumnDTO("TABLE_NAME", Types.VARCHAR));
        columns.add(new ColumnDTO("NON_UNIQUE", Types.BOOLEAN));
        columns.add(new ColumnDTO("INDEX_QUALIFIER", Types.VARCHAR));
        columns.add(new ColumnDTO("INDEX_NAME", Types.VARCHAR));
        columns.add(new ColumnDTO("TYPE", Types.INTEGER));
        columns.add(new ColumnDTO("ORDINAL_POSITION", Types.INTEGER));
        columns.add(new ColumnDTO("COLUMN_NAME", Types.VARCHAR));
        columns.add(new ColumnDTO("ASC_OR_DESC", Types.VARCHAR));
        columns.add(new ColumnDTO("CARDINALITY", Types.INTEGER));
        columns.add(new ColumnDTO("PAGES", Types.INTEGER));
        columns.add(new ColumnDTO("FILTER_CONDITION", Types.VARCHAR));

        final ArrayList<ArrayList<AbstractFieldValue>> values = new ArrayList<ArrayList<AbstractFieldValue>>(1);

        for (final ParadoxTable table : listTables(conn, tableNamePattern)) {
            final ParadoxPK primaryKeyIndex = getPrimaryKey(conn, table);

            if (primaryKeyIndex != null) {
                for (final ParadoxField pk : table.getPrimaryKeys()) {
                    final ArrayList<AbstractFieldValue> row = new ArrayList<AbstractFieldValue>();

                    row.add(new StringValue(conn.getCatalog()));
                    row.add(new StringValue(conn.getSchema()));
                    row.add(new StringValue(table.getName()));
                    row.add(new BooleanValue(false));
                    row.add(new StringValue(conn.getCatalog()));
                    row.add(new StringValue(primaryKeyIndex.getName()));
                    row.add(new IntegerValue(tableIndexHashed));
                    // FIXME get it from index file
                    row.add(new IntegerValue(0));
                    row.add(new StringValue(pk.getName()));
                    row.add(new StringValue("A"));
                    // FIXME get it from index file
                    row.add(new IntegerValue(0));
                    row.add(new IntegerValue(0));
                    //row.add(String.valueOf(pk.getRowCount()));
                    //row.add(String.valueOf(pk.getUsedBlocks()));
                    row.add(null);

                    values.add(row);
                }
            }

            for (final ParadoxIndex index : listIndexes(conn, tableNamePattern)) {
                int ordinal = 0;
                for (final ParadoxField field : index.getFields()) {
                    final ArrayList<AbstractFieldValue> row = new ArrayList<AbstractFieldValue>();

                    row.add(new StringValue(conn.getCatalog()));
                    row.add(new StringValue(conn.getSchema()));
                    row.add(new StringValue(index.getFatherName()));
                    row.add(new BooleanValue(false));
                    row.add(new StringValue(conn.getCatalog()));
                    row.add(new StringValue(index.getName()));
                    row.add(new IntegerValue(tableIndexHashed));
                    row.add(new IntegerValue(ordinal));
                    row.add(new StringValue(field.getName()));
                    row.add(new StringValue(index.getOrder()));
                    // FIXME get it from index file
                    row.add(new IntegerValue(0));
                    row.add(new IntegerValue(0));
                    //row.add(String.valueOf(pk.getRowCount()));
                    //row.add(String.valueOf(pk.getUsedBlocks()));
                    row.add(null);

                    values.add(row);
                    ordinal++;
                }
            }
        }
        return new ParadoxResultSet(conn, null, values, columns);
    }

    public boolean supportsResultSetType(int type) throws SQLException {
        return false;
    }

    public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
        return false;
    }

    public boolean ownUpdatesAreVisible(int type) throws SQLException {
        return false;
    }

    public boolean ownDeletesAreVisible(int type) throws SQLException {
        return false;
    }

    public boolean ownInsertsAreVisible(int type) throws SQLException {
        return false;
    }

    public boolean othersUpdatesAreVisible(int type) throws SQLException {
        return false;
    }

    public boolean othersDeletesAreVisible(int type) throws SQLException {
        return false;
    }

    public boolean othersInsertsAreVisible(int type) throws SQLException {
        return false;
    }

    public boolean updatesAreDetected(int type) throws SQLException {
        return false;
    }

    public boolean deletesAreDetected(int type) throws SQLException {
        return false;
    }

    public boolean insertsAreDetected(int type) throws SQLException {
        return false;
    }

    public boolean supportsBatchUpdates() throws SQLException {
        return false;
    }

    public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException {
        return new ParadoxResultSet(conn, null, new ArrayList<ArrayList<AbstractFieldValue>>(), new ArrayList<ColumnDTO>());
    }

    public Connection getConnection() throws SQLException {
        return conn;
    }

    public boolean supportsSavepoints() throws SQLException {
        return false;
    }

    public boolean supportsNamedParameters() throws SQLException {
        return false;
    }

    public boolean supportsMultipleOpenResults() throws SQLException {
        return false;
    }

    public boolean supportsGetGeneratedKeys() throws SQLException {
        return false;
    }

    public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) throws SQLException {
        return new ParadoxResultSet(conn, null, new ArrayList<ArrayList<AbstractFieldValue>>(), new ArrayList<ColumnDTO>());
    }

    public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
        return new ParadoxResultSet(conn, null, new ArrayList<ArrayList<AbstractFieldValue>>(), new ArrayList<ColumnDTO>());
    }

    public ResultSet getAttributes(String catalog, String schemaPattern, String typeNamePattern, String attributeNamePattern) throws SQLException {
        return new ParadoxResultSet(conn, null, new ArrayList<ArrayList<AbstractFieldValue>>(), new ArrayList<ColumnDTO>());
    }

    public boolean supportsResultSetHoldability(int holdability) throws SQLException {
        return false;
    }

    public int getResultSetHoldability() throws SQLException {
        return conn.getHoldability();
    }

    public int getDatabaseMajorVersion() throws SQLException {
        return 8;
    }

    public int getDatabaseMinorVersion() throws SQLException {
        return 0;
    }

    public int getJDBCMajorVersion() throws SQLException {
        return 4;
    }

    public int getJDBCMinorVersion() throws SQLException {
        return 0;
    }

    public int getSQLStateType() throws SQLException {
        return 0;
    }

    public boolean locatorsUpdateCopy() throws SQLException {
        return false;
    }

    public boolean supportsStatementPooling() throws SQLException {
        return false;
    }

    @Override
    public RowIdLifetime getRowIdLifetime() throws SQLException {
        return RowIdLifetime.ROWID_UNSUPPORTED;
    }

    @Override
    public ResultSet getSchemas(final String catalog, final String schemaPattern) throws SQLException {
        if ((catalog != null && !accept(conn.getCatalog(), catalog))
                || (schemaPattern != null && !accept(conn.getSchema(), schemaPattern))) {

            return new ParadoxResultSet(conn, null, new ArrayList<ArrayList<AbstractFieldValue>>(), new ArrayList<ColumnDTO>());
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
        return new ParadoxResultSet(conn, null, new ArrayList<ArrayList<AbstractFieldValue>>(), new ArrayList<ColumnDTO>());
    }

    @Override
    public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException {
        return new ParadoxResultSet(conn, null, new ArrayList<ArrayList<AbstractFieldValue>>(), new ArrayList<ColumnDTO>());
    }

    @Override
    public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern) throws SQLException {
        return new ParadoxResultSet(conn, null, new ArrayList<ArrayList<AbstractFieldValue>>(), new ArrayList<ColumnDTO>());
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

    public ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean generatedKeyAlwaysReturned() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
