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
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.ParadoxResultSet;
import com.googlecode.paradox.exceptions.ParadoxDataException;
import com.googlecode.paradox.function.AbstractFunction;
import com.googlecode.paradox.function.FunctionFactory;
import com.googlecode.paradox.function.FunctionType;
import com.googlecode.paradox.planner.context.SelectContext;
import com.googlecode.paradox.planner.plan.SelectPlan;
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
 * @since 1.0
 */
public final class ParadoxDatabaseMetaData implements java.sql.DatabaseMetaData {

    /**
     * JDBC major version.
     */
    private static final int JDBC_MAJOR_VERSION = 4;

    /**
     * JDBC minor version.
     */
    private static final int JDBC_MINOR_VERSION = 2;

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
     * String max size.
     */
    private static final int STRING_MAX_SIZE = 255;

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
    public ParadoxDatabaseMetaData(final ParadoxConnection connection) {
        this.connection = connection;
        this.connectionInfo = connection.getConnectionInfo();
    }

    @Override
    public boolean allProceduresAreCallable() {
        return true;
    }

    @Override
    public boolean allTablesAreSelectable() {
        return true;
    }

    @Override
    public boolean autoCommitFailureClosesAllResultSets() {
        return false;
    }

    @Override
    public boolean dataDefinitionCausesTransactionCommit() {
        return true;
    }

    @Override
    public boolean dataDefinitionIgnoredInTransactions() {
        return false;
    }

    @Override
    public boolean deletesAreDetected(final int type) {
        return false;
    }

    @Override
    public boolean doesMaxRowSizeIncludeBlobs() {
        return true;
    }

    @Override
    public boolean generatedKeyAlwaysReturned() {
        return true;
    }

    @Override
    public ResultSet getAttributes(final String catalog, final String schemaPattern, final String typeNamePattern, final String attributeNamePattern) {
        return new ParadoxResultSet(this.connectionInfo, null, Collections.emptyList(), Collections.emptyList());
    }

    @Override
    public ResultSet getBestRowIdentifier(final String catalog, final String schema, final String table, final int scope, final boolean nullable) {
        return new ParadoxResultSet(this.connectionInfo, null, Collections.emptyList(), Collections.emptyList());
    }

    @Override
    public String getCatalogSeparator() {
        return ".";
    }

    @Override
    public String getCatalogTerm() {
        return "CATALOG";
    }

    @Override
    public ResultSet getClientInfoProperties() {
        return new ParadoxResultSet(this.connectionInfo, null, Collections.emptyList(), Collections.emptyList());
    }

    @Override
    public ResultSet getColumnPrivileges(final String catalog, final String schema, final String table,
                                         final String columnNamePattern) {
        return new ParadoxResultSet(this.connectionInfo, null, Collections.emptyList(), Collections.emptyList());
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public ResultSet getCrossReference(final String catalog, final String schemaPattern, final String primaryTable, final String foreignCatalog, final String foreignSchema,
                                       final String foreignTable) throws SQLException {
        final ArrayList<Column> columns = new ArrayList<>();
        columns.add(new Column("PKTABLE_CAT", ParadoxType.VARCHAR));
        columns.add(new Column("PKTABLE_SCHEM", ParadoxType.VARCHAR));
        columns.add(new Column("PKTABLE_NAME", ParadoxType.VARCHAR));
        columns.add(new Column("PKCOLUMN_NAME", ParadoxType.VARCHAR));
        columns.add(new Column("FKTABLE_CAT", ParadoxType.INTEGER));
        columns.add(new Column("FKTABLE_SCHEM", ParadoxType.INTEGER));
        columns.add(new Column("FKTABLE_NAME", ParadoxType.VARCHAR));
        columns.add(new Column("FKCOLUMN_NAME", ParadoxType.INTEGER));
        columns.add(new Column("KEY_SEQ", ParadoxType.INTEGER));
        columns.add(new Column("UPDATE_RULE", ParadoxType.INTEGER));
        columns.add(new Column("DELETE_RULE", ParadoxType.INTEGER));
        columns.add(new Column("FK_NAME", ParadoxType.INTEGER));
        columns.add(new Column("PK_NAME", ParadoxType.VARCHAR));
        columns.add(new Column("DEFERRABILITY", ParadoxType.INTEGER));

        final List<Object[]> values = new ArrayList<>();

        if (foreignCatalog != null && !foreignCatalog.equals(connectionInfo.getCatalog())) {
            return new ParadoxResultSet(this.connectionInfo, null, values, columns);
        }

        List<Schema> schemas = connectionInfo.getSchemas(catalog, schemaPattern);
        for (Schema schema : schemas) {
            if (foreignSchema != null && !schema.name().equals(foreignSchema)) {
                continue;
            }

            List<Table> tables = schema.list(connectionInfo, Optional.ofNullable(foreignTable).orElse("%"));
            for (Table table : tables) {
                ForeignKey[] fks = table.getForeignKeys();
                if (fks != null) {
                    for (ForeignKey foreignKey : fks) {
                        if (foreignKey.getReferencedTable().getName().equals(primaryTable)) {
                            for (int index = 0; index < foreignKey.getOriginFields().length; index++) {
                                Field[] originFields = foreignKey.getOriginFields();
                                Field[] referencedFields = foreignKey.getReferencedFields();

                                final Object[] row = {
                                        // PKTABLE_CAT.
                                        connectionInfo.getCatalog(),
                                        // PKTABLE_SCHEM.
                                        schema.name(),
                                        // PKTABLE_NAME.
                                        foreignKey.getOriginTable().getName(),
                                        // PKCOLUMN_NAME.
                                        referencedFields[index].getName(),
                                        // FKTABLE_CAT.
                                        connectionInfo.getCatalog(),
                                        // FKTABLE_SCHEM.
                                        schema.name(),
                                        // FKTABLE_NAME.
                                        foreignKey.getReferencedTable().getName(),
                                        // FKCOLUMN_NAME.
                                        originFields[index].getName(),
                                        // KEY_SEQ.
                                        index + 1,
                                        // UPDATE_RULE.
                                        foreignKey.isCascade() ? importedKeyCascade : importedKeyNoAction,
                                        // DELETE_RULE.
                                        foreignKey.isCascade() ? importedKeyCascade : importedKeyNoAction,
                                        // FK_NAME
                                        foreignKey.getName(),
                                        // PK_NAME.
                                        null,
                                        // DEFERRABILITY.
                                        importedKeyNotDeferrable,
                                };

                                values.add(row);
                            }
                        }
                    }
                }
            }
        }

        // FIXME foreign keys
        return new ParadoxResultSet(this.connectionInfo, null, values, columns);
    }

    @Override
    public int getDatabaseMajorVersion() {
        return ParadoxDatabaseMetaData.PARADOX_MAJOR_VERSION;
    }

    @Override
    public int getDatabaseMinorVersion() {
        return ParadoxDatabaseMetaData.PARADOX_MINOR_VERSION;
    }

    @Override
    public String getDatabaseProductName() {
        return Constants.DRIVER_NAME;
    }

    @Override
    public String getDatabaseProductVersion() {
        return Constants.DRIVER_NAME + " " + Constants.DRIVER_VERSION;
    }

    @Override
    public int getDefaultTransactionIsolation() {
        return Connection.TRANSACTION_NONE;
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
    public String getDriverName() {
        return Constants.DRIVER_NAME;
    }

    @Override
    public String getDriverVersion() {
        return Constants.DRIVER_VERSION;
    }

    @Override
    public ResultSet getExportedKeys(final String catalog, final String schema, final String table) {
        return new ParadoxResultSet(this.connectionInfo, null, Collections.emptyList(), Collections.emptyList());
    }

    @Override
    public String getExtraNameCharacters() {
        return "";
    }

    @Override
    public ResultSet getFunctionColumns(final String catalog, final String schemaPattern, final String functionNamePattern, final String columnNamePattern) {
        final ArrayList<Column> columns = new ArrayList<>();
        columns.add(new Column("FUNCTION_CAT", ParadoxType.VARCHAR));
        columns.add(new Column("FUNCTION_SCHEM", ParadoxType.VARCHAR));
        columns.add(new Column("FUNCTION_NAME", ParadoxType.VARCHAR));
        columns.add(new Column("COLUMN_NAME", ParadoxType.VARCHAR));
        columns.add(new Column("COLUMN_TYPE", ParadoxType.INTEGER));
        columns.add(new Column("DATA_TYPE", ParadoxType.INTEGER));
        columns.add(new Column("TYPE_NAME", ParadoxType.VARCHAR));
        columns.add(new Column("PRECISION", ParadoxType.INTEGER));
        columns.add(new Column("LENGTH", ParadoxType.INTEGER));
        columns.add(new Column("SCALE", ParadoxType.INTEGER));
        columns.add(new Column("RADIX", ParadoxType.INTEGER));
        columns.add(new Column("NULLABLE", ParadoxType.INTEGER));
        columns.add(new Column("REMARKS", ParadoxType.VARCHAR));
        columns.add(new Column("CHAR_OCTET_LENGTH", ParadoxType.INTEGER));
        columns.add(new Column("ORDINAL_POSITION", ParadoxType.INTEGER));
        columns.add(new Column("IS_NULLABLE", ParadoxType.VARCHAR));
        columns.add(new Column("SPECIFIC_NAME", ParadoxType.VARCHAR));

        final List<Object[]> values = new ArrayList<>();

        for (final Map.Entry<String, Supplier<? extends AbstractFunction>> function :
                FunctionFactory.getFunctions().entrySet()) {
            if (!catalog.equalsIgnoreCase(connectionInfo.getCatalog())
                    && !Expressions.accept(connectionInfo.getLocale(), connectionInfo.getCurrentSchema().name(),
                    schemaPattern, false, '\\')
                    && !Expressions.accept(connectionInfo.getLocale(), function.getKey(), functionNamePattern, false,
                    '\\')) {
                continue;
            }

            final AbstractFunction instance = function.getValue().get();
            for (final Column column : instance.getColumns()) {
                if (!Expressions.accept(connectionInfo.getLocale(), column.getName(), columnNamePattern, false, '\\')) {
                    continue;
                }

                String nullableText = "NO";
                int nullable = functionNoNulls;
                if (column.isNullable()) {
                    nullableText = "YES";
                    nullable = functionNullable;
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
                        column.getType().name(),
                        // Precision.
                        column.getPrecision(),
                        // Length,
                        column.getSize(),
                        // Scale.
                        column.getScale(),
                        // Radix.
                        column.getRadix(),
                        // Nullable
                        nullable,
                        // Remarks.
                        column.getRemarks(),
                        // Octets.
                        column.getOctets(),
                        // Ordinal position.
                        column.getIndex(),
                        // Is nullable.
                        nullableText,
                        // Specific name.
                        null};

                values.add(row);
            }
        }

        return new ParadoxResultSet(this.connectionInfo, null, values, columns);
    }

    @Override
    public ResultSet getFunctions(final String catalog, final String schemaPattern, final String functionNamePattern)
            throws SQLException {
        final String sql = "select \"catalog\" as FUNCTION_CAT,\n" +
                "       \"schema\" as FUNCTION_SCHEM,\n" +
                "       name as FUNCTION_NAME,\n" +
                "       remarks as REMARKS,\n" +
                "       cast(1 as numeric) as FUNCTION_TYPE,\n" +
                "       name as SPECIFIC_NAME\n" +
                "from information_schema.pdx_routines\n" +
                "where (? is null or \"catalog\" = ?)\n" +
                "  and (? is null or \"schema\" ilike ?)\n" +
                "  and (? is null or \"name\" ilike ?)\n" +
                "  and type = 'FUNCTION'\n" +
                "order by \"catalog\", \"schema\", name";

        final SelectPlan selectPlan = (SelectPlan) connection.createPlan(sql);
        final SelectContext context = selectPlan.createContext(connectionInfo,
                new Object[]{
                        catalog,
                        catalog,
                        schemaPattern,
                        schemaPattern,
                        functionNamePattern,
                        functionNamePattern
                },
                new ParadoxType[]{
                        ParadoxType.VARCHAR,
                        ParadoxType.VARCHAR,
                        ParadoxType.VARCHAR,
                        ParadoxType.VARCHAR,
                        ParadoxType.VARCHAR,
                        ParadoxType.VARCHAR,
                });

        final List<Object[]> values = selectPlan.execute(context);
        return new ParadoxResultSet(this.connectionInfo, null, values, selectPlan.getColumns());
    }

    @Override
    public String getIdentifierQuoteString() {
        return "\"";
    }

    @Override
    public ResultSet getImportedKeys(final String catalog, final String schemaPattern, final String tableName) throws SQLException {
        final ArrayList<Column> columns = new ArrayList<>();
        columns.add(new Column("PKTABLE_CAT", ParadoxType.VARCHAR));
        columns.add(new Column("PKTABLE_SCHEM", ParadoxType.VARCHAR));
        columns.add(new Column("PKTABLE_NAME", ParadoxType.VARCHAR));
        columns.add(new Column("PKCOLUMN_NAME", ParadoxType.VARCHAR));
        columns.add(new Column("FKTABLE_CAT", ParadoxType.INTEGER));
        columns.add(new Column("FKTABLE_SCHEM", ParadoxType.INTEGER));
        columns.add(new Column("FKTABLE_NAME", ParadoxType.VARCHAR));
        columns.add(new Column("FKCOLUMN_NAME", ParadoxType.INTEGER));
        columns.add(new Column("KEY_SEQ", ParadoxType.INTEGER));
        columns.add(new Column("UPDATE_RULE", ParadoxType.INTEGER));
        columns.add(new Column("DELETE_RULE", ParadoxType.INTEGER));
        columns.add(new Column("FK_NAME", ParadoxType.INTEGER));
        columns.add(new Column("PK_NAME", ParadoxType.VARCHAR));
        columns.add(new Column("DEFERRABILITY", ParadoxType.INTEGER));

        final List<Object[]> values = new ArrayList<>();

        List<Schema> schemas = connectionInfo.getSchemas(catalog, schemaPattern);
        for (Schema schema : schemas) {
            List<Table> tables = schema.list(connectionInfo, tableName);
            for (Table table : tables) {
                ForeignKey[] fks = table.getForeignKeys();
                if (fks != null) {
                    for (ForeignKey foreignKey : fks) {
                        for (int index = 0; index < foreignKey.getOriginFields().length; index++) {
                            Field[] originFields = foreignKey.getOriginFields();
                            Field[] referencedFields = foreignKey.getReferencedFields();

                            final Object[] row = {
                                    // PKTABLE_CAT.
                                    connectionInfo.getCatalog(),
                                    // PKTABLE_SCHEM.
                                    schema.name(),
                                    // PKTABLE_NAME.
                                    foreignKey.getOriginTable().getName(),
                                    // PKCOLUMN_NAME.
                                    referencedFields[index].getName(),
                                    // FKTABLE_CAT.
                                    connectionInfo.getCatalog(),
                                    // FKTABLE_SCHEM.
                                    schema.name(),
                                    // FKTABLE_NAME.
                                    foreignKey.getReferencedTable().getName(),
                                    // FKCOLUMN_NAME.
                                    originFields[index].getName(),
                                    // KEY_SEQ.
                                    index + 1,
                                    // UPDATE_RULE.
                                    foreignKey.isCascade() ? importedKeyCascade : importedKeyNoAction,
                                    // DELETE_RULE.
                                    foreignKey.isCascade() ? importedKeyCascade : importedKeyNoAction,
                                    // FK_NAME
                                    foreignKey.getName(),
                                    // PK_NAME.
                                    null,
                                    // DEFERRABILITY.
                                    importedKeyNotDeferrable,
                            };

                            values.add(row);
                        }
                    }
                }
            }
        }

        // FIXME foreign keys
        return new ParadoxResultSet(this.connectionInfo, null, values, columns);
    }

    @Override
    public ResultSet getCatalogs() throws ParadoxDataException {
        final List<Column> columns = Collections
                .singletonList(new Column("TABLE_CAT", ParadoxType.VARCHAR));

        final List<Object[]> values = connectionInfo.listCatalogs().stream()
                .map(name -> new Object[]{name})
                .collect(Collectors.toList());

        return new ParadoxResultSet(this.connectionInfo, null, values, columns);
    }

    @Override
    public ResultSet getColumns(final String catalog, final String schemaPattern, final String tableNamePattern, final String columnNamePattern) throws SQLException {
        final String sql = "select \"catalog\"             as TABLE_CAT,\n" +
                "       \"schema\"              as TABLE_SCHEM,\n" +
                "       \"table\"               as TABLE_NAME,\n" +
                "       name                  as COLUMN_NAME,\n" +
                "       java_type_id          as DATA_TYPE,\n" +
                "       type                  as TYPE_NAME,\n" +
                "       maximum_length        as COLUMN_SIZE,\n" +
                "       cast(null as numeric) as BUFFER_LENGTH,\n" +
                "       scale                 as DECIMAL_DIGITS,\n" +
                "       radix                 as NUM_PREC_RADIX,\n" +
                "       nullable              as NULLABLE,\n" +
                "       cast(null as varchar) as REMARKS,\n" +
                "       cast(null as varchar) as COLUMN_DEF,\n" +
                "       java_type_id          as SQL_DATA_TYPE,\n" +
                "       0                     as SQL_DATETIME_SUB,\n" +
                "       \"octet_length\"        as CHAR_OCTET_LENGTH,\n" +
                "       ordinal               as ORDINAL_POSITION,\n" +
                "       is_nullable           as IS_NULLABLE,\n" +
                "       cast(null as varchar) as SCOPE_CATALOG,\n" +
                "       cast(null as varchar) as SCOPE_SCHEMA,\n" +
                "       java_type_id          as SOURCE_DATA_TYPE,\n" +
                "       is_autoincrement      as IS_AUTOINCREMENT,\n" +
                "       'NO'                  as IS_GENERATEDCOLUMN\n" +
                "from information_schema.pdx_columns\n" +
                "where (? is null or \"catalog\" = ?)\n" +
                "  and (? is null or \"schema\" ilike ?)\n" +
                "  and (? is null or \"table\" ilike ?)\n" +
                "  and (? is null or name ilike ?)\n" +
                "order by \"catalog\", \"schema\", name";

        final SelectPlan selectPlan = (SelectPlan) connection.createPlan(sql);
        final SelectContext context = selectPlan.createContext(connectionInfo,
                new Object[]{
                        catalog,
                        catalog,
                        schemaPattern,
                        schemaPattern,
                        tableNamePattern,
                        tableNamePattern,
                        columnNamePattern,
                        columnNamePattern
                },
                new ParadoxType[]{
                        ParadoxType.VARCHAR,
                        ParadoxType.VARCHAR,
                        ParadoxType.VARCHAR,
                        ParadoxType.VARCHAR,
                        ParadoxType.VARCHAR,
                        ParadoxType.VARCHAR,
                        ParadoxType.VARCHAR,
                        ParadoxType.VARCHAR
                });

        final List<Object[]> values = selectPlan.execute(context);
        return new ParadoxResultSet(this.connectionInfo, null, values, selectPlan.getColumns());
    }

    @Override
    public ResultSet getIndexInfo(final String catalog, final String schemaName, final String tableNamePattern, final boolean unique, final boolean approximate) throws SQLException {

        final String sql = "select index_catalog as      TABLE_CAT,\n" +
                "       index_schema  as      TABLE_SCHEM,\n" +
                "       table_name    as      TABLE_NAME,\n" +
                "       non_unique    as      NON_UNIQUE,\n" +
                "       index_catalog as      INDEX_QUALIFIER,\n" +
                "       index_name    as INDEX_NAME,\n" +
                "       cast(2 as NUMERIC) as TYPE,\n" +
                "       ordinal as ORDINAL_POSITION,\n" +
                "       field as COLUMN_NAME,\n" +
                "       substring(asc_or_desc, 1, 1) as ASC_OR_DESC,\n" +
                "       cardinality as CARDINALITY,\n" +
                "       pages as PAGES,\n" +
                "       cast(null as VARCHAR) FILTER_CONDITION\n" +
                "from information_schema.pdx_indexes\n" +
                "where (? is null or upper(\"index_catalog\") ilike upper(?))\n" +
                "  and (? is null or upper(\"index_schema\") ilike upper(?))\n" +
                "  and (? is null or \"table_name\" ilike ?)\n" +
                "  and (? = false or non_unique = false)\n" +
                "order by non_unique, index_name, ordinal";

        final SelectPlan selectPlan = (SelectPlan) connection.createPlan(sql);
        final SelectContext context = selectPlan.createContext(connectionInfo,
                new Object[]{
                        catalog,
                        catalog,
                        schemaName,
                        schemaName,
                        tableNamePattern,
                        tableNamePattern,
                        unique,
                        unique
                },
                new ParadoxType[]{
                        ParadoxType.VARCHAR,
                        ParadoxType.VARCHAR,
                        ParadoxType.VARCHAR,
                        ParadoxType.VARCHAR,
                        ParadoxType.VARCHAR,
                        ParadoxType.VARCHAR,
                        ParadoxType.BOOLEAN,
                        ParadoxType.BOOLEAN,
                });

        final List<Object[]> values = selectPlan.execute(context);
        return new ParadoxResultSet(this.connectionInfo, null, values, selectPlan.getColumns());
    }

    @Override
    public int getJDBCMajorVersion() {
        return ParadoxDatabaseMetaData.JDBC_MAJOR_VERSION;
    }

    @Override
    public int getJDBCMinorVersion() {
        return ParadoxDatabaseMetaData.JDBC_MINOR_VERSION;
    }

    @Override
    public int getMaxBinaryLiteralLength() {
        return ParadoxDatabaseMetaData.PARADOX_MAX_COLUMN_NAME;
    }

    @Override
    public int getMaxCatalogNameLength() {
        return ParadoxDatabaseMetaData.STRING_MAX_SIZE;
    }

    @Override
    public int getMaxCharLiteralLength() {
        return ParadoxDatabaseMetaData.STRING_MAX_SIZE;
    }

    @Override
    public int getMaxColumnNameLength() {
        return ParadoxDatabaseMetaData.PARADOX_MAX_COLUMN_NAME;
    }

    @Override
    public int getMaxColumnsInGroupBy() {
        return ParadoxDatabaseMetaData.STRING_MAX_SIZE;
    }

    @Override
    public int getMaxColumnsInIndex() {
        return ParadoxDatabaseMetaData.STRING_MAX_SIZE;
    }

    @Override
    public int getMaxColumnsInOrderBy() {
        return ParadoxDatabaseMetaData.STRING_MAX_SIZE;
    }

    @Override
    public int getMaxColumnsInSelect() {
        return ParadoxDatabaseMetaData.STRING_MAX_SIZE;
    }

    @Override
    public int getMaxColumnsInTable() {
        return ParadoxDatabaseMetaData.STRING_MAX_SIZE;
    }

    @Override
    public int getMaxConnections() {
        return 1;
    }

    @Override
    public int getMaxCursorNameLength() {
        return ParadoxDatabaseMetaData.STRING_MAX_SIZE;
    }

    @Override
    public int getMaxIndexLength() {
        return ParadoxDatabaseMetaData.STRING_MAX_SIZE;
    }

    @Override
    public int getMaxProcedureNameLength() {
        return ParadoxDatabaseMetaData.STRING_MAX_SIZE;
    }

    @Override
    public int getMaxRowSize() {
        return ParadoxDatabaseMetaData.STRING_MAX_SIZE;
    }

    @Override
    public int getMaxSchemaNameLength() {
        return ParadoxDatabaseMetaData.STRING_MAX_SIZE;
    }

    @Override
    public int getMaxStatementLength() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMaxStatements() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMaxTableNameLength() {
        return ParadoxDatabaseMetaData.STRING_MAX_SIZE;
    }

    @Override
    public int getMaxTablesInSelect() {
        return ParadoxDatabaseMetaData.STRING_MAX_SIZE;
    }

    @Override
    public int getMaxUserNameLength() {
        return ParadoxDatabaseMetaData.STRING_MAX_SIZE;
    }

    @Override
    public String getNumericFunctions() {
        return FunctionFactory.getByType(FunctionType.NUMERIC);
    }

    @Override
    public ResultSet getPrimaryKeys(final String catalog, final String schemaName, final String tableNamePattern)
            throws SQLException {
        final String sql = "select \"catalog\"       as TABLE_CAT,\n" +
                "       \"schema\"        as TABLE_SCHEM,\n" +
                "       \"table\"         as TABLE_NAME,\n" +
                "       name            as COLUMN_NAME,\n" +
                "       ordinal         as KEY_SEQ,\n" +
                "       constraint_name as PK_NAME\n" +
                "from information_schema.pdx_key_columns\n" +
                "where (? is null or \"catalog\" = ?)\n" +
                "  and (? is null or \"schema\" = ?)\n" +
                "  and (? is null or \"table\" ilike ?)\n" +
                "order by name";

        final SelectPlan selectPlan = (SelectPlan) connection.createPlan(sql);
        final SelectContext context = selectPlan.createContext(connectionInfo,
                new Object[]{
                        catalog,
                        catalog,
                        schemaName,
                        schemaName,
                        tableNamePattern,
                        tableNamePattern
                },
                new ParadoxType[]{
                        ParadoxType.VARCHAR,
                        ParadoxType.VARCHAR,
                        ParadoxType.VARCHAR,
                        ParadoxType.VARCHAR,
                        ParadoxType.VARCHAR,
                        ParadoxType.VARCHAR
                });

        final List<Object[]> values = selectPlan.execute(context);
        return new ParadoxResultSet(this.connectionInfo, null, values, selectPlan.getColumns());
    }

    @Override
    public ResultSet getProcedureColumns(final String catalog, final String schemaPattern, final String procedureNamePattern, final String columnNamePattern) {
        return new ParadoxResultSet(this.connectionInfo, null, Collections.emptyList(), Collections.emptyList());
    }

    @Override
    public String getProcedureTerm() {
        return "PROCEDURE";
    }

    @Override
    public ResultSet getPseudoColumns(final String catalog, final String schemaPattern, final String tableNamePattern, final String columnNamePattern) {
        return new ParadoxResultSet(this.connectionInfo, null, Collections.emptyList(), Collections.emptyList());
    }

    @Override
    public int getResultSetHoldability() {
        return this.connection.getHoldability();
    }

    @Override
    public RowIdLifetime getRowIdLifetime() {
        return RowIdLifetime.ROWID_UNSUPPORTED;
    }

    @Override
    public ResultSet getProcedures(final String catalog, final String schemaPattern, final String procedureNamePattern) {
        return new ParadoxResultSet(this.connectionInfo, null, Collections.emptyList(), Collections.emptyList());
    }

    @Override
    public ResultSet getSchemas(final String catalog, final String schemaPattern) throws SQLException {
        final ArrayList<Column> columns = new ArrayList<>();
        columns.add(new Column("TABLE_SCHEM", ParadoxType.VARCHAR));
        columns.add(new Column("TABLE_CATALOG", ParadoxType.VARCHAR));

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

    @Override
    public String getSchemaTerm() {
        return "SCHEMA";
    }

    @Override
    public String getSearchStringEscape() {
        return Character.toString(Constants.ESCAPE_CHAR);
    }

    @Override
    public String getSQLKeywords() {
        return "ILIKE";
    }

    @Override
    public int getSQLStateType() {
        return java.sql.DatabaseMetaData.sqlStateSQL;
    }

    @Override
    public String getStringFunctions() {
        return FunctionFactory.getByType(FunctionType.STRING);
    }

    @Override
    public ResultSet getSuperTables(final String catalog, final String schemaPattern, final String tableNamePattern) {
        return new ParadoxResultSet(this.connectionInfo, null, Collections.emptyList(), Collections.emptyList());
    }

    @Override
    public ResultSet getSuperTypes(final String catalog, final String schemaPattern, final String typeNamePattern) {
        return new ParadoxResultSet(this.connectionInfo, null, Collections.emptyList(), Collections.emptyList());
    }

    @Override
    public String getSystemFunctions() {
        return FunctionFactory.getByType(FunctionType.SYSTEM);
    }

    @Override
    public ResultSet getTablePrivileges(final String catalog, final String schemaPattern, final String tableNamePattern) {
        return new ParadoxResultSet(this.connectionInfo, null, Collections.emptyList(), Collections.emptyList());
    }

    @Override
    public ResultSet getSchemas() throws SQLException {
        final ArrayList<Column> columns = new ArrayList<>();
        columns.add(new Column("TABLE_SCHEM", ParadoxType.VARCHAR));
        columns.add(new Column("TABLE_CATALOG", ParadoxType.VARCHAR));

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

    @Override
    public ResultSet getTables(final String catalog, final String schemaPattern, final String tableNamePattern, final String[] types) throws SQLException {
        String sql = "select \"catalog\"             as TABLE_CAT,\n" +
                "       \"schema\"            as TABLE_SCHEM,\n" +
                "       name                  as TABLE_NAME,\n" +
                "       type_name             as TABLE_TYPE,\n" +
                "       cast(null as VARCHAR) as REMARKS,\n" +
                "       cast(null as VARCHAR) as TYPE_CAT,\n" +
                "       cast(null as VARCHAR) as TYPE_SCHEM,\n" +
                "       cast(null as VARCHAR) as SELF_REFERENCING_COL_NAME,\n" +
                "       cast(null as VARCHAR) as REF_GENERATION\n" +
                "from information_schema.pdx_tables\n" +
                "where (? is null or \"catalog\" = ?)\n" +
                "    and (? is null or \"schema\" ilike ?)\n";

        if (types != null) {
            sql += " and upper(type_name) in (" + Arrays.stream(types)
                    .map(type -> "'" + type + "'")
                    .map(String::toUpperCase)
                    .collect(Collectors.joining(",")) + ") ";
        }

        sql += " order by \"catalog\", \"schema\", name ";

        final SelectPlan selectPlan = (SelectPlan) connection.createPlan(sql);
        final SelectContext context = selectPlan.createContext(connectionInfo,
                new Object[]{catalog, catalog, schemaPattern, schemaPattern},
                new ParadoxType[]{ParadoxType.VARCHAR, ParadoxType.VARCHAR, ParadoxType.VARCHAR, ParadoxType.VARCHAR});

        final List<Object[]> values = selectPlan.execute(context);
        return new ParadoxResultSet(this.connectionInfo, null, values, selectPlan.getColumns());
    }

    @Override
    public String getTimeDateFunctions() {
        return FunctionFactory.getByType(FunctionType.TIME_DATE);
    }

    @Override
    public ResultSet getTypeInfo() {
        final ArrayList<Column> columns = new ArrayList<>();
        columns.add(new Column("TYPE_NAME", ParadoxType.VARCHAR));
        columns.add(new Column("DATA_TYPE", ParadoxType.INTEGER));
        columns.add(new Column("PRECISION", ParadoxType.INTEGER));
        columns.add(new Column("LITERAL_PREFIX", ParadoxType.VARCHAR));
        columns.add(new Column("LITERAL_SUFFIX", ParadoxType.VARCHAR));
        columns.add(new Column("CREATE_PARAMS", ParadoxType.VARCHAR));
        columns.add(new Column("NULLABLE", ParadoxType.INTEGER));
        columns.add(new Column("CASE_SENSITIVE", ParadoxType.BOOLEAN));
        columns.add(new Column("SEARCHABLE", ParadoxType.INTEGER));
        columns.add(new Column("UNSIGNED_ATTRIBUTE", ParadoxType.BOOLEAN));
        columns.add(new Column("FIXED_PREC_SCALE", ParadoxType.BOOLEAN));
        columns.add(new Column("AUTO_INCREMENT", ParadoxType.BOOLEAN));
        columns.add(new Column("LOCAL_TYPE_NAME", ParadoxType.VARCHAR));
        columns.add(new Column("MINIMUM_SCALE", ParadoxType.INTEGER));
        columns.add(new Column("MAXIMUM_SCALE", ParadoxType.INTEGER));
        columns.add(new Column("SQL_DATA_TYPE", ParadoxType.INTEGER));
        columns.add(new Column("SQL_DATETIME_SUB", ParadoxType.INTEGER));
        columns.add(new Column("NUM_PREC_RADIX", ParadoxType.INTEGER));

        final List<Object[]> values = new ArrayList<>();

        for (final ParadoxType type : ParadoxType.values()) {
            int searchable = typePredNone;
            if (type.isSearchable()) {
                searchable = typeSearchable;
            }

            final Object[] row = {
                    // Type name.
                    type.getName(),
                    // Data type.
                    type.getSQLType(),
                    // Precision.
                    type.getPrecision(),
                    // Literal prefix.
                    null,
                    // Literal suffix.
                    null,
                    // Create params.
                    null,
                    // Nullable.
                    typeNullableUnknown,
                    // Case sensitive.
                    false,
                    // Searchable,
                    searchable,
                    // Unsigned attribute.
                    type.isNumeric(),
                    // Fixed prec scale.
                    type == ParadoxType.CURRENCY,
                    // Autoincrement
                    type == ParadoxType.AUTO_INCREMENT,
                    // Local type name.
                    null,
                    // Minimum scale.
                    0,
                    // Maximum scale.
                    type.getPrecision(),
                    // SQL data type.
                    type.getSQLType(),
                    // SQL datetime sub.
                    0,
                    // Radix
                    type.getRadix()
            };

            values.add(row);
        }

        return new ParadoxResultSet(this.connectionInfo, null, values, columns);
    }

    @Override
    public ResultSet getUDTs(final String catalog, final String schemaPattern, final String typeNamePattern, final int[] types) {
        return new ParadoxResultSet(this.connectionInfo, null, Collections.emptyList(), Collections.emptyList());
    }

    @Override
    public String getURL() {
        return this.connectionInfo.getUrl();
    }

    @Override
    public String getUserName() {
        return connectionInfo.getUser();
    }

    @Override
    public ResultSet getVersionColumns(final String catalog, final String schema, final String table) {
        return new ParadoxResultSet(this.connectionInfo, null, Collections.emptyList(), Collections.emptyList());
    }

    @Override
    public boolean insertsAreDetected(final int type) {
        return false;
    }

    @Override
    public boolean isCatalogAtStart() {
        return true;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public boolean isWrapperFor(final Class<?> iFace) {
        return Utils.isWrapperFor(this, iFace);
    }

    @Override
    public boolean locatorsUpdateCopy() {
        return false;
    }

    @Override
    public boolean nullPlusNonNullIsNull() {
        return false;
    }

    @Override
    public boolean nullsAreSortedAtEnd() {
        return false;
    }

    @Override
    public boolean nullsAreSortedAtStart() {
        return false;
    }

    @Override
    public boolean nullsAreSortedHigh() {
        return true;
    }

    @Override
    public boolean nullsAreSortedLow() {
        return false;
    }

    @Override
    public boolean othersDeletesAreVisible(final int type) {
        return false;
    }

    @Override
    public boolean othersInsertsAreVisible(final int type) {
        return false;
    }

    @Override
    public boolean othersUpdatesAreVisible(final int type) {
        return false;
    }

    @Override
    public boolean ownDeletesAreVisible(final int type) {
        return false;
    }

    @Override
    public boolean ownInsertsAreVisible(final int type) {
        return false;
    }

    @Override
    public boolean ownUpdatesAreVisible(final int type) {
        return false;
    }

    @Override
    public boolean storesLowerCaseIdentifiers() {
        return false;
    }

    @Override
    public boolean storesLowerCaseQuotedIdentifiers() {
        return false;
    }

    @Override
    public boolean storesMixedCaseIdentifiers() {
        return true;
    }

    @Override
    public boolean storesMixedCaseQuotedIdentifiers() {
        return true;
    }

    @Override
    public boolean storesUpperCaseIdentifiers() {
        return false;
    }

    @Override
    public boolean storesUpperCaseQuotedIdentifiers() {
        return false;
    }

    @Override
    public boolean supportsAlterTableWithAddColumn() {
        return false;
    }

    @Override
    public boolean supportsAlterTableWithDropColumn() {
        return false;
    }

    @Override
    public boolean supportsANSI92EntryLevelSQL() {
        return true;
    }

    @Override
    public boolean supportsANSI92FullSQL() {
        return true;
    }

    @Override
    public boolean supportsANSI92IntermediateSQL() {
        return true;
    }

    @Override
    public boolean supportsBatchUpdates() {
        return false;
    }

    @Override
    public boolean supportsCatalogsInDataManipulation() {
        return false;
    }

    @Override
    public boolean supportsCatalogsInIndexDefinitions() {
        return false;
    }

    @Override
    public boolean supportsCatalogsInPrivilegeDefinitions() {
        return false;
    }

    @Override
    public boolean supportsCatalogsInProcedureCalls() {
        return false;
    }

    @Override
    public boolean supportsCatalogsInTableDefinitions() {
        return false;
    }

    @Override
    public boolean supportsColumnAliasing() {
        return true;
    }

    @Override
    public boolean supportsConvert() {
        return true;
    }

    @Override
    public boolean supportsConvert(final int fromType, final int toType) {
        // FIXME test for all conditions.
        return true;
    }

    @Override
    public boolean supportsCoreSQLGrammar() {
        return true;
    }

    @Override
    public boolean supportsCorrelatedSubqueries() {
        return false;
    }

    @Override
    public boolean supportsDataDefinitionAndDataManipulationTransactions() {
        return false;
    }

    @Override
    public boolean supportsDataManipulationTransactionsOnly() {
        return false;
    }

    @Override
    public boolean supportsDifferentTableCorrelationNames() {
        return false;
    }

    @Override
    public boolean supportsExpressionsInOrderBy() {
        return false;
    }

    @Override
    public boolean supportsExtendedSQLGrammar() {
        return false;
    }

    @Override
    public boolean supportsFullOuterJoins() {
        return true;
    }

    @Override
    public boolean supportsGetGeneratedKeys() {
        return false;
    }

    @Override
    public boolean supportsGroupBy() {
        return true;
    }

    @Override
    public boolean supportsGroupByBeyondSelect() {
        return true;
    }

    @Override
    public boolean supportsGroupByUnrelated() {
        return true;
    }

    @Override
    public boolean supportsIntegrityEnhancementFacility() {
        return false;
    }

    @Override
    public boolean supportsLikeEscapeClause() {
        return true;
    }

    @Override
    public boolean supportsLimitedOuterJoins() {
        return true;
    }

    @Override
    public boolean supportsMinimumSQLGrammar() {
        return true;
    }

    @Override
    public boolean supportsMixedCaseIdentifiers() {
        return false;
    }

    @Override
    public boolean supportsMixedCaseQuotedIdentifiers() {
        return false;
    }

    @Override
    public boolean supportsMultipleOpenResults() {
        return true;
    }

    @Override
    public boolean supportsMultipleResultSets() {
        return true;
    }

    @Override
    public boolean supportsMultipleTransactions() {
        return false;
    }

    @Override
    public boolean supportsNamedParameters() {
        return false;
    }

    @Override
    public boolean supportsNonNullableColumns() {
        return true;
    }

    @Override
    public boolean supportsOpenCursorsAcrossCommit() {
        return false;
    }

    @Override
    public boolean supportsOpenCursorsAcrossRollback() {
        return false;
    }

    @Override
    public boolean supportsOpenStatementsAcrossCommit() {
        return false;
    }

    @Override
    public boolean supportsOpenStatementsAcrossRollback() {
        return false;
    }

    @Override
    public boolean supportsOrderByUnrelated() {
        return true;
    }

    @Override
    public boolean supportsOuterJoins() {
        return true;
    }

    @Override
    public boolean supportsPositionedDelete() {
        return false;
    }

    @Override
    public boolean supportsPositionedUpdate() {
        return false;
    }

    @Override
    public boolean supportsResultSetConcurrency(final int type, final int concurrency) {
        return false;
    }

    @Override
    public boolean supportsResultSetHoldability(final int holdability) {
        return false;
    }

    @Override
    public boolean supportsResultSetType(final int type) {
        return type == ResultSet.TYPE_FORWARD_ONLY || type == ResultSet.TYPE_SCROLL_INSENSITIVE || type == ResultSet.TYPE_SCROLL_SENSITIVE;
    }

    @Override
    public boolean supportsSavepoints() {
        return false;
    }

    @Override
    public boolean supportsSchemasInDataManipulation() {
        return true;
    }

    @Override
    public boolean supportsSchemasInIndexDefinitions() {
        return true;
    }

    @Override
    public boolean supportsSchemasInPrivilegeDefinitions() {
        return true;
    }

    @Override
    public boolean supportsSchemasInProcedureCalls() {
        return false;
    }

    @Override
    public boolean supportsSchemasInTableDefinitions() {
        return true;
    }

    @Override
    public boolean supportsSelectForUpdate() {
        return false;
    }

    @Override
    public boolean supportsStatementPooling() {
        return false;
    }

    @Override
    public boolean supportsStoredFunctionsUsingCallSyntax() {
        return false;
    }

    @Override
    public boolean supportsStoredProcedures() {
        return false;
    }

    @Override
    public boolean supportsSubqueriesInComparisons() {
        return false;
    }

    @Override
    public boolean supportsSubqueriesInExists() {
        return false;
    }

    @Override
    public boolean supportsSubqueriesInIns() {
        return false;
    }

    @Override
    public boolean supportsSubqueriesInQuantifieds() {
        return false;
    }

    @Override
    public boolean supportsTableCorrelationNames() {
        return false;
    }

    @Override
    public boolean supportsTransactionIsolationLevel(final int level) {
        return Connection.TRANSACTION_NONE != level;
    }

    @Override
    public boolean supportsTransactions() {
        return false;
    }

    @Override
    public boolean supportsUnion() {
        return false;
    }

    @Override
    public boolean supportsUnionAll() {
        return false;
    }

    @Override
    public <T> T unwrap(final Class<T> iFace) throws SQLException {
        return Utils.unwrap(this, iFace);
    }

    @Override
    public boolean updatesAreDetected(final int type) {
        return false;
    }

    @Override
    public boolean usesLocalFilePerTable() {
        return true;
    }

    @Override
    public boolean usesLocalFiles() {
        return true;
    }

    @Override
    public ResultSet getTableTypes() {
        final List<Column> columns = Collections.singletonList(new Column("TABLE_TYPE", ParadoxType.VARCHAR));

        final List<String[]> values = Arrays.stream(TableType.values())
                .map(TableType::description).sorted()
                .map(type -> new String[]{type})
                .collect(Collectors.toList());

        return new ParadoxResultSet(this.connectionInfo, null, values, columns);
    }
}
