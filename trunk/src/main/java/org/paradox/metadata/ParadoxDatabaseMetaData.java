package org.paradox.metadata;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import org.paradox.ParadoxConnection;
import org.paradox.ParadoxResultSet;
import org.paradox.data.IndexData;
import org.paradox.data.PrimaryKeyData;
import org.paradox.data.TableData;
import org.paradox.data.ViewData;
import org.paradox.data.table.value.FieldValue;
import org.paradox.procedures.CallableProcedure;
import org.paradox.procedures.ProcedureAS;
import org.paradox.results.Column;
import org.paradox.utils.Constants;
import org.paradox.utils.Expressions;
import org.paradox.utils.SQLStates;

/**
 * 
 * @author Leonardo Alves da Costa
 * @since 14/03/2009
 * @version 1.1
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

	@Override
	public String getUserName() throws SQLException {
		return "SYSTEM";
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		return true;

	}

	@Override
	public boolean nullsAreSortedHigh() throws SQLException {
		return false;
	}

	@Override
	public boolean nullsAreSortedLow() throws SQLException {
		return true;
	}

	@Override
	public boolean nullsAreSortedAtStart() throws SQLException {
		return false;
	}

	@Override
	public boolean nullsAreSortedAtEnd() throws SQLException {
		return true;
	}

	@Override
	public String getDatabaseProductName() throws SQLException {
		return Constants.DRIVER_NAME;
	}

	@Override
	public String getDatabaseProductVersion() throws SQLException {
		return Constants.DRIVER_NAME + " " + Constants.DRIVER_VERSION;
	}

	@Override
	public String getDriverName() throws SQLException {
		return Constants.DRIVER_NAME;
	}

	@Override
	public String getDriverVersion() throws SQLException {
		return Constants.DRIVER_VERSION;
	}

	@Override
	public int getDriverMajorVersion() {
		return Constants.MAJOR_VERSION;
	}

	@Override
	public int getDriverMinorVersion() {
		return Constants.MINOR_VERSION;
	}

	@Override
	public boolean usesLocalFiles() throws SQLException {
		return true;
	}

	@Override
	public boolean usesLocalFilePerTable() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsMixedCaseIdentifiers() throws SQLException {
		return true;
	}

	@Override
	public boolean storesUpperCaseIdentifiers() throws SQLException {
		return true;
	}

	@Override
	public boolean storesLowerCaseIdentifiers() throws SQLException {
		return true;
	}

	@Override
	public boolean storesMixedCaseIdentifiers() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
		return true;
	}

	@Override
	public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
		return true;
	}

	@Override
	public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
		return true;
	}

	@Override
	public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
		return true;
	}

	@Override
	public String getIdentifierQuoteString() throws SQLException {
		return "\"";
	}

	@Override
	public String getSQLKeywords() throws SQLException {
		return "SELECT";
	}

	@Override
	public String getNumericFunctions() throws SQLException {
		return "AVERANGE,SUM";
	}

	@Override
	public String getStringFunctions() throws SQLException {
		return null;
	}

	@Override
	public String getSystemFunctions() throws SQLException {
		return null;
	}

	@Override
	public String getTimeDateFunctions() throws SQLException {
		return null;
	}

	@Override
	public String getSearchStringEscape() throws SQLException {
		return "\\";
	}

	@Override
	public String getExtraNameCharacters() throws SQLException {
		return "";
	}

	@Override
	public boolean supportsAlterTableWithAddColumn() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsAlterTableWithDropColumn() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsColumnAliasing() throws SQLException {
		return true;
	}

	@Override
	public boolean nullPlusNonNullIsNull() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsConvert() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsConvert(final int fromType, final int toType) throws SQLException {
		return false;
	}

	@Override
	public boolean supportsTableCorrelationNames() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsDifferentTableCorrelationNames() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsExpressionsInOrderBy() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsOrderByUnrelated() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsGroupBy() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsGroupByUnrelated() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsGroupByBeyondSelect() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsLikeEscapeClause() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsMultipleResultSets() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsMultipleTransactions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsNonNullableColumns() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsMinimumSQLGrammar() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsCoreSQLGrammar() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsExtendedSQLGrammar() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsANSI92EntryLevelSQL() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsANSI92IntermediateSQL() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsANSI92FullSQL() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsIntegrityEnhancementFacility() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsOuterJoins() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsFullOuterJoins() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsLimitedOuterJoins() throws SQLException {
		return false;
	}

	@Override
	public String getSchemaTerm() throws SQLException {
		return "SCHEMA";
	}

	@Override
	public String getProcedureTerm() throws SQLException {
		return "PROCEDURE";
	}

	@Override
	public String getCatalogTerm() throws SQLException {
		return "CATALOG";
	}

	@Override
	public boolean isCatalogAtStart() throws SQLException {
		return true;
	}

	@Override
	public String getCatalogSeparator() throws SQLException {
		return ".";
	}

	@Override
	public boolean supportsSchemasInDataManipulation() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSchemasInProcedureCalls() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSchemasInTableDefinitions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSchemasInIndexDefinitions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsCatalogsInDataManipulation() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsCatalogsInProcedureCalls() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsCatalogsInTableDefinitions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsPositionedDelete() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsPositionedUpdate() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSelectForUpdate() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsStoredProcedures() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSubqueriesInComparisons() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSubqueriesInExists() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSubqueriesInIns() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSubqueriesInQuantifieds() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsCorrelatedSubqueries() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsUnion() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsUnionAll() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
		return false;
	}

	@Override
	public int getMaxBinaryLiteralLength() throws SQLException {
		return 8;
	}

	@Override
	public int getMaxCharLiteralLength() throws SQLException {
		return 255;
	}

	@Override
	public int getMaxColumnNameLength() throws SQLException {
		return 8;
	}

	@Override
	public int getMaxColumnsInGroupBy() throws SQLException {
		return 255;
	}

	@Override
	public int getMaxColumnsInIndex() throws SQLException {
		return 255;
	}

	@Override
	public int getMaxColumnsInOrderBy() throws SQLException {
		return 255;
	}

	@Override
	public int getMaxColumnsInSelect() throws SQLException {
		return 255;
	}

	@Override
	public int getMaxColumnsInTable() throws SQLException {
		return 255;
	}

	@Override
	public int getMaxConnections() throws SQLException {
		// FIXME allow more than one connection
		return 1;
	}

	@Override
	public int getMaxCursorNameLength() throws SQLException {
		return 255;
	}

	@Override
	public int getMaxIndexLength() throws SQLException {
		return 255;
	}

	@Override
	public int getMaxSchemaNameLength() throws SQLException {
		return 255;
	}

	@Override
	public int getMaxProcedureNameLength() throws SQLException {
		return 255;
	}

	@Override
	public int getMaxCatalogNameLength() throws SQLException {
		return 255;
	}

	@Override
	public int getMaxRowSize() throws SQLException {
		return 255;
	}

	@Override
	public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
		return true;
	}

	@Override
	public int getMaxStatementLength() throws SQLException {
		return Integer.MAX_VALUE;
	}

	@Override
	public int getMaxStatements() throws SQLException {
		return Integer.MAX_VALUE;
	}

	@Override
	public int getMaxTableNameLength() throws SQLException {
		return 255;
	}

	@Override
	public int getMaxTablesInSelect() throws SQLException {
		return 255;
	}

	@Override
	public int getMaxUserNameLength() throws SQLException {
		return 255;
	}

	@Override
	public int getDefaultTransactionIsolation() throws SQLException {
		return Connection.TRANSACTION_NONE;
	}

	@Override
	public boolean supportsTransactions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsTransactionIsolationLevel(final int level) throws SQLException {
		return Connection.TRANSACTION_NONE != level;
	}

	@Override
	public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
		return false;
	}

	@Override
	public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
		return false;
	}

	@Override
	public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
		return false;
	}

	@Override
	public ResultSet getProcedures(final String catalog, final String schemaPattern, final String procedureNamePattern) throws SQLException {
		final ArrayList<Column> columns = new ArrayList<Column>(1);
		columns.add(new Column("PROCEDURE_CAT", Types.VARCHAR));
		columns.add(new Column("PROCEDURE_SCHEM", Types.VARCHAR));
		columns.add(new Column("PROCEDURE_NAME", Types.VARCHAR));
		columns.add(new Column("Reserved1", Types.VARCHAR));
		columns.add(new Column("Reserved2", Types.VARCHAR));
		columns.add(new Column("Reserved3", Types.VARCHAR));
		columns.add(new Column("REMARKS", Types.VARCHAR)); // Comment
		columns.add(new Column("PROCEDURE_TYPE", Types.INTEGER));
		columns.add(new Column("SPECIFIC_NAME", Types.VARCHAR));

		final ArrayList<ArrayList<FieldValue>> values = new ArrayList<ArrayList<FieldValue>>();

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

	@Override
	public ResultSet getProcedureColumns(final String catalog, final String schemaPattern, final String procedureNamePattern, final String columnNamePattern) throws SQLException {
		final ArrayList<Column> columns = new ArrayList<Column>(1);
		columns.add(new Column("PROCEDURE_CAT", Types.VARCHAR));
		columns.add(new Column("PROCEDURE_SCHEM", Types.VARCHAR));
		columns.add(new Column("PROCEDURE_NAME", Types.VARCHAR));
		columns.add(new Column("COLUMN_NAME", Types.VARCHAR));
		columns.add(new Column("COLUMN_TYPE", Types.INTEGER));
		columns.add(new Column("DATA_TYPE", Types.INTEGER));
		columns.add(new Column("TYPE_NAME", Types.VARCHAR));
		columns.add(new Column("PRECISION", Types.INTEGER));
		columns.add(new Column("LENGTH", Types.INTEGER));
		columns.add(new Column("SCALE", Types.INTEGER));
		columns.add(new Column("RADIX", Types.INTEGER));
		columns.add(new Column("NULLABLE", Types.INTEGER));
		columns.add(new Column("REMARKS", Types.VARCHAR));
		columns.add(new Column("COLUMN_DEF", Types.VARCHAR));
		columns.add(new Column("SQL_DATA_TYPE", Types.VARCHAR));
		columns.add(new Column("SQL_DATETIME_SUB", Types.VARCHAR));
		columns.add(new Column("CHAR_OCTET_LENGTH", Types.VARCHAR));
		columns.add(new Column("IS_NULLABLE", Types.VARCHAR));
		columns.add(new Column("SPECIFIC_NAME", Types.VARCHAR));

		final ArrayList<ArrayList<FieldValue>> values = new ArrayList<ArrayList<FieldValue>>();

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

	@Override
	public ResultSet getTables(final String catalog, final String schemaPattern, final String tableNamePattern, final String[] types) throws SQLException {
		final ArrayList<Column> columns = new ArrayList<Column>(1);
		columns.add(new Column("TABLE_CAT", Types.VARCHAR));
		columns.add(new Column("TABLE_SCHEM", Types.VARCHAR));
		columns.add(new Column("TABLE_NAME", Types.VARCHAR));
		columns.add(new Column("TABLE_TYPE", Types.VARCHAR));
		columns.add(new Column("REMARKS", Types.VARCHAR));
		columns.add(new Column("TYPE_CAT", Types.VARCHAR));
		columns.add(new Column("TYPE_SCHEM", Types.VARCHAR));
		columns.add(new Column("TYPE_NAME", Types.VARCHAR));
		columns.add(new Column("SELF_REFERENCING_COL_NAME", Types.VARCHAR));
		columns.add(new Column("REF_GENERATION", Types.VARCHAR));

		final ArrayList<ArrayList<FieldValue>> values = new ArrayList<ArrayList<FieldValue>>(1);

		if (types != null) {
			for (final String type : types) {
				if ("TABLE".equalsIgnoreCase(type)) {
					for (final ParadoxTable table : TableData.listTables(conn, tableNamePattern)) {
						final ArrayList<FieldValue> row = new ArrayList<FieldValue>(1);
						row.add(new FieldValue(conn.getCatalog(), Types.VARCHAR));
						row.add(new FieldValue(conn.getSchema(), Types.VARCHAR));
						row.add(new FieldValue(table.getName(), Types.VARCHAR));
						row.add(new FieldValue("TABLE", Types.VARCHAR));
						row.add(new FieldValue(Types.VARCHAR));
						row.add(new FieldValue(Types.VARCHAR));
						row.add(new FieldValue(Types.VARCHAR));
						row.add(new FieldValue(Types.VARCHAR));
						// FIXME col id
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

	@Override
	public ResultSet getSchemas() throws SQLException {
		final ArrayList<Column> columns = new ArrayList<Column>(1);
		columns.add(new Column("TABLE_SCHEM", Types.VARCHAR));
		columns.add(new Column("TABLE_CATALOG", Types.VARCHAR));

		final ArrayList<FieldValue> row = new ArrayList<FieldValue>(1);
		final ArrayList<ArrayList<FieldValue>> values = new ArrayList<ArrayList<FieldValue>>(1);
		row.add(new FieldValue(conn.getSchema(), Types.VARCHAR));
		row.add(new FieldValue(conn.getCatalog(), Types.VARCHAR));
		values.add(row);

		return new ParadoxResultSet(conn, null, values, columns);
	}

	@Override
	public ResultSet getCatalogs() throws SQLException {
		final ArrayList<Column> columns = new ArrayList<Column>(1);
		columns.add(new Column("TABLE_CAT", Types.VARCHAR));

		final ArrayList<FieldValue> row = new ArrayList<FieldValue>(1);
		final ArrayList<ArrayList<FieldValue>> values = new ArrayList<ArrayList<FieldValue>>(1);
		row.add(new FieldValue(conn.getCatalog(), Types.VARCHAR));
		values.add(row);

		return new ParadoxResultSet(conn, null, values, columns);
	}

	@Override
	public ResultSet getTableTypes() throws SQLException {
		final ArrayList<Column> columns = new ArrayList<Column>(1);
		columns.add(new Column("TABLE_TYPE", Types.VARCHAR));

		final ArrayList<FieldValue> row = new ArrayList<FieldValue>(1);
		final ArrayList<ArrayList<FieldValue>> values = new ArrayList<ArrayList<FieldValue>>(1);
		row.add(new FieldValue("TABLE", Types.VARCHAR));
		row.add(new FieldValue("VIEW", Types.VARCHAR));
		row.add(new FieldValue("SYSTEM TABLE", Types.VARCHAR));
		values.add(row);

		return new ParadoxResultSet(conn, null, values, columns);
	}

	@Override
	public ResultSet getColumns(final String catalog, final String schemaPattern, final String tableNamePattern, final String columnNamePattern) throws SQLException {
		final ArrayList<Column> columns = new ArrayList<Column>(1);
		columns.add(new Column("TABLE_CAT", Types.VARCHAR));
		columns.add(new Column("TABLE_SCHEM", Types.VARCHAR));
		columns.add(new Column("TABLE_NAME", Types.VARCHAR));
		columns.add(new Column("COLUMN_NAME", Types.VARCHAR));
		columns.add(new Column("DATA_TYPE", Types.INTEGER));
		columns.add(new Column("TYPE_NAME", Types.VARCHAR));
		columns.add(new Column("COLUMN_SIZE", Types.INTEGER));
		columns.add(new Column("BUFFER_LENGTH", Types.INTEGER));
		columns.add(new Column("DECIMAL_DIGITS", Types.INTEGER));
		columns.add(new Column("NUM_PREC_RADIX", Types.INTEGER));
		columns.add(new Column("NULLABLE", Types.INTEGER));
		columns.add(new Column("REMARKS", Types.INTEGER));
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

		final ArrayList<ArrayList<FieldValue>> values = new ArrayList<ArrayList<FieldValue>>(1);

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

		final ArrayList<? extends AbstractTable> views = ViewData.listViews(conn);
		for (final AbstractTable view : views) {
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

	@Override
	public ResultSet getColumnPrivileges(final String catalog, final String schema, final String table, final String columnNamePattern) throws SQLException {
		return new ParadoxResultSet(conn, null, new ArrayList<ArrayList<FieldValue>>(), new ArrayList<Column>());
	}

	@Override
	public ResultSet getTablePrivileges(final String catalog, final String schemaPattern, final String tableNamePattern) throws SQLException {
		return new ParadoxResultSet(conn, null, new ArrayList<ArrayList<FieldValue>>(), new ArrayList<Column>());
	}

	@Override
	public ResultSet getBestRowIdentifier(final String catalog, final String schema, final String table, final int scope, final boolean nullable) throws SQLException {
		return new ParadoxResultSet(conn, null, new ArrayList<ArrayList<FieldValue>>(), new ArrayList<Column>());
	}

	@Override
	public ResultSet getVersionColumns(final String catalog, final String schema, final String table) throws SQLException {
		return new ParadoxResultSet(conn, null, new ArrayList<ArrayList<FieldValue>>(), new ArrayList<Column>());
	}

	@Override
	public ResultSet getPrimaryKeys(final String catalog, final String schema, final String tableNamePattern) throws SQLException {
		final ArrayList<Column> columns = new ArrayList<Column>(1);
		columns.add(new Column("TABLE_CAT", Types.VARCHAR));
		columns.add(new Column("TABLE_SCHEM", Types.VARCHAR));
		columns.add(new Column("TABLE_NAME", Types.VARCHAR));
		columns.add(new Column("COLUMN_NAME", Types.VARCHAR));
		columns.add(new Column("KEY_SEQ", Types.INTEGER));
		columns.add(new Column("PK_NAME", Types.VARCHAR));

		final ArrayList<ArrayList<FieldValue>> values = new ArrayList<ArrayList<FieldValue>>(1);
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

	@Override
	public ResultSet getImportedKeys(final String catalog, final String schema, final String table) throws SQLException {
		return new ParadoxResultSet(conn, null, new ArrayList<ArrayList<FieldValue>>(), new ArrayList<Column>());
	}

	@Override
	public ResultSet getExportedKeys(final String catalog, final String schema, final String table) throws SQLException {
		return new ParadoxResultSet(conn, null, new ArrayList<ArrayList<FieldValue>>(), new ArrayList<Column>());
	}

	@Override
	public ResultSet getCrossReference(final String primaryCatalog, final String primarySchema, final String primaryTable, final String foreignCatalog, final String foreignSchema,
			final String foreignTable) throws SQLException {
		return new ParadoxResultSet(conn, null, new ArrayList<ArrayList<FieldValue>>(), new ArrayList<Column>());
	}

	@Override
	public ResultSet getTypeInfo() throws SQLException {
		return new ParadoxResultSet(conn, null, new ArrayList<ArrayList<FieldValue>>(), new ArrayList<Column>());
	}

	@Override
	public ResultSet getIndexInfo(final String catalog, final String schema, final String tableNamePattern, final boolean unique, final boolean approximate) throws SQLException {
		final ArrayList<Column> columns = new ArrayList<Column>(1);
		columns.add(new Column("TABLE_CAT", Types.VARCHAR));
		columns.add(new Column("TABLE_SCHEM", Types.VARCHAR));
		columns.add(new Column("TABLE_NAME", Types.VARCHAR));
		columns.add(new Column("NON_UNIQUE", Types.BOOLEAN));
		columns.add(new Column("INDEX_QUALIFIER", Types.VARCHAR));
		columns.add(new Column("INDEX_NAME", Types.VARCHAR));
		columns.add(new Column("TYPE", Types.INTEGER));
		columns.add(new Column("ORDINAL_POSITION", Types.INTEGER));
		columns.add(new Column("COLUMN_NAME", Types.VARCHAR));
		columns.add(new Column("ASC_OR_DESC", Types.VARCHAR));
		columns.add(new Column("CARDINALITY", Types.INTEGER));
		columns.add(new Column("PAGES", Types.INTEGER));
		columns.add(new Column("FILTER_CONDITION", Types.VARCHAR));

		final ArrayList<ArrayList<FieldValue>> values = new ArrayList<ArrayList<FieldValue>>(1);

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
				for (final ParadoxField field : index.getFields()) {
					final ArrayList<FieldValue> row = new ArrayList<FieldValue>();

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

	@Override
	public boolean supportsResultSetType(final int type) throws SQLException {
		return false;
	}

	@Override
	public boolean supportsResultSetConcurrency(final int type, final int concurrency) throws SQLException {
		return false;
	}

	@Override
	public boolean ownUpdatesAreVisible(final int type) throws SQLException {
		return false;
	}

	@Override
	public boolean ownDeletesAreVisible(final int type) throws SQLException {
		return false;
	}

	@Override
	public boolean ownInsertsAreVisible(final int type) throws SQLException {
		return false;
	}

	@Override
	public boolean othersUpdatesAreVisible(final int type) throws SQLException {
		return false;
	}

	@Override
	public boolean othersDeletesAreVisible(final int type) throws SQLException {
		return false;
	}

	@Override
	public boolean othersInsertsAreVisible(final int type) throws SQLException {
		return false;
	}

	@Override
	public boolean updatesAreDetected(final int type) throws SQLException {
		return false;
	}

	@Override
	public boolean deletesAreDetected(final int type) throws SQLException {
		return false;
	}

	@Override
	public boolean insertsAreDetected(final int type) throws SQLException {
		return false;
	}

	@Override
	public boolean supportsBatchUpdates() throws SQLException {
		return false;
	}

	@Override
	public ResultSet getUDTs(final String catalog, final String schemaPattern, final String typeNamePattern, final int[] types) throws SQLException {
		return new ParadoxResultSet(conn, null, new ArrayList<ArrayList<FieldValue>>(), new ArrayList<Column>());
	}

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
		return new ParadoxResultSet(conn, null, new ArrayList<ArrayList<FieldValue>>(), new ArrayList<Column>());
	}

	@Override
	public ResultSet getSuperTables(final String catalog, final String schemaPattern, final String tableNamePattern) throws SQLException {
		return new ParadoxResultSet(conn, null, new ArrayList<ArrayList<FieldValue>>(), new ArrayList<Column>());
	}

	@Override
	public ResultSet getAttributes(final String catalog, final String schemaPattern, final String typeNamePattern, final String attributeNamePattern) throws SQLException {
		return new ParadoxResultSet(conn, null, new ArrayList<ArrayList<FieldValue>>(), new ArrayList<Column>());
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
		// FIXME get the higest table value
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
			return new ParadoxResultSet(conn, null, new ArrayList<ArrayList<FieldValue>>(), new ArrayList<Column>());
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
		return new ParadoxResultSet(conn, null, new ArrayList<ArrayList<FieldValue>>(), new ArrayList<Column>());
	}

	@Override
	public ResultSet getFunctions(final String catalog, final String schemaPattern, final String functionNamePattern) throws SQLException {
		return new ParadoxResultSet(conn, null, new ArrayList<ArrayList<FieldValue>>(), new ArrayList<Column>());
	}

	@Override
	public ResultSet getFunctionColumns(final String catalog, final String schemaPattern, final String functionNamePattern, final String columnNamePattern) throws SQLException {
		return new ParadoxResultSet(conn, null, new ArrayList<ArrayList<FieldValue>>(), new ArrayList<Column>());
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

	public ResultSet getPseudoColumns(final String catalog, final String schemaPattern, final String tableNamePattern, final String columnNamePattern) throws SQLException {
		return new ParadoxResultSet(conn, null, new ArrayList<ArrayList<FieldValue>>(), new ArrayList<Column>());
	}

	public boolean generatedKeyAlwaysReturned() throws SQLException {
		return true;
	}
}
