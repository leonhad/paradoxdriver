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
package com.googlecode.paradox;

import com.googlecode.paradox.data.filefilters.DirectoryFilter;
import com.googlecode.paradox.exceptions.ParadoxConnectionException;
import com.googlecode.paradox.exceptions.ParadoxException;
import com.googlecode.paradox.exceptions.ParadoxNotSupportedException;
import com.googlecode.paradox.metadata.ParadoxDatabaseMetaData;
import com.googlecode.paradox.rowset.ParadoxBlob;
import com.googlecode.paradox.rowset.ParadoxClob;
import com.googlecode.paradox.utils.Expressions;
import com.googlecode.paradox.utils.Utils;

import java.io.File;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

/**
 * JDBC Paradox connection implementation.
 *
 * @version 1.0
 * @since 1.0
 */
public final class ParadoxConnection implements Connection {

    public static final String INFORMATION_SCHEMA = "information_schema";
    /**
     * Database catalog.
     */
    private final File catalog;
    /**
     * Stores the opened statements.
     */
    private final ArrayList<Statement> statements = new ArrayList<>();
    /**
     * Driver URL.
     */
    private final String url;
    /**
     * Default charset.
     */
    private final Charset charset;
    /**
     * Connection locale.
     */
    private final Locale locale;
    /**
     * Time zone.
     */
    private final TimeZone timeZone;
    /**
     * Auto Commit flag.
     */
    private boolean autocommit = true;
    /**
     * Connection properties info.
     */
    private Properties clientInfo = new Properties();
    /**
     * If this connection is closed.
     */
    private boolean closed;
    /**
     * This connection holdability.
     */
    private int holdability = ResultSet.CLOSE_CURSORS_AT_COMMIT;
    /**
     * Default timeout.
     */
    private int networkTimeout;
    /**
     * If this connection is read only.
     */
    private boolean readonly = true;
    /**
     * Selected Schema.
     */
    private File schema;
    /**
     * Stores the transaction isolation mode.
     */
    private int transactionIsolation = Connection.TRANSACTION_NONE;
    /**
     * Stores the JDBC type mapping.
     */
    private Map<String, Class<?>> typeMap;
    /**
     * BCD field precision.
     */
    private boolean bcdRounding = true;

    /**
     * Creates a new paradox connection.
     *
     * @param dir  database directory.
     * @param url  connect URL.
     * @param info the connection properties.
     * @throws SQLException in any connection fault.
     */
    public ParadoxConnection(final File dir, final String url, final Properties info) throws SQLException {
        this.url = url;

        if (!dir.exists() && !dir.isDirectory()) {
            throw new ParadoxConnectionException(ParadoxConnectionException.Error.DIRECTORY_NOT_FOUND);
        }

        final String charsetName = info.getProperty(Driver.CHARSET_KEY);
        if (charsetName != null && !charsetName.trim().isEmpty()) {
            this.charset = Charset.forName(charsetName);
        } else {
            this.charset = null;
        }

        final String localeName = info.getProperty(Driver.LOCALE_KEY);
        if (localeName != null && !localeName.trim().isEmpty()) {
            this.locale = Locale.forLanguageTag(localeName);
        } else {
            this.locale = Locale.US;
        }

        final String bcdRoundingName = info.getProperty(Driver.BCD_ROUNDING_KEY);
        if (bcdRoundingName != null && !bcdRoundingName.trim().isEmpty()) {
            this.bcdRounding = Boolean.parseBoolean(bcdRoundingName);
        }

        final String timeZoneId = info.getProperty(Driver.TIME_ZONE_KEY);
        if (timeZoneId != null && !timeZoneId.trim().isEmpty()) {
            this.timeZone = TimeZone.getTimeZone(timeZoneId);
        } else {
            this.timeZone = TimeZone.getDefault();
        }

        // Is a schema.
        this.schema = dir;
        this.catalog = dir.getParentFile();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void abort(final Executor executor) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void clearWarnings() {
        // Not used.
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void close() throws SQLException {
        for (final Statement stmt : this.statements) {
            stmt.close();
        }
        this.statements.clear();
        this.closed = true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void commit() throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Array createArrayOf(final String typeName, final Object[] elements) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Blob createBlob() {
        return new ParadoxBlob(new byte[0]);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Clob createClob() {
        return new ParadoxClob("");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public NClob createNClob() throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public SQLXML createSQLXML() throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Statement createStatement() {
        return createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency) {
        return createStatement(resultSetType, resultSetConcurrency, holdability);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency,
                                     final int resultSetHoldability) {
        final Statement stmt = new ParadoxStatement(this, resultSetType, resultSetConcurrency,
                resultSetHoldability);
        this.statements.add(stmt);
        return stmt;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Struct createStruct(final String typeName, final Object[] attributes) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean getAutoCommit() {
        return this.autocommit;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setAutoCommit(final boolean autoCommit) {
        this.autocommit = autoCommit;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getCatalog() {
        return "DATABASE";
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setCatalog(final String catalog) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.CATALOG_CHANGE);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Properties getClientInfo() {
        return new Properties(this.clientInfo);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setClientInfo(final Properties clientInfo) {
        this.clientInfo = new Properties(clientInfo);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getClientInfo(final String name) {
        return this.clientInfo.getProperty(name);
    }

    /**
     * Gets the current schema directory.
     *
     * @return the current schema directory.
     */
    public File getCurrentSchema() {
        return this.schema;
    }

    /**
     * Gets the current catalog directory.
     *
     * @return the current catalog directory.
     */
    public File getCurrentCatalog() {
        return this.catalog;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getHoldability() {
        return this.holdability;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setHoldability(final int holdability) {
        this.holdability = holdability;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public DatabaseMetaData getMetaData() {
        return new ParadoxDatabaseMetaData(this);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getNetworkTimeout() {
        return this.networkTimeout;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getSchema() {
        return this.schema.getName();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setSchema(final String schema) throws SQLException {
        final File file = new File(this.catalog, schema);
        if (!file.isDirectory()) {
            throw new ParadoxException(ParadoxException.Error.SCHEMA_NOT_FOUND);
        }
        this.schema = file;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getTransactionIsolation() {
        return this.transactionIsolation;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setTransactionIsolation(final int level) throws SQLException {
        if (Connection.TRANSACTION_NONE != level) {
            throw new ParadoxException(ParadoxException.Error.INVALID_TRANSACTION_LEVEL);
        }
        this.transactionIsolation = level;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Map<String, Class<?>> getTypeMap() {
        return this.typeMap;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setTypeMap(final Map<String, Class<?>> typeMap) {
        this.typeMap = typeMap;
    }

    /**
     * Gets the URL connection.
     *
     * @return the URL connection
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public SQLWarning getWarnings() {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean isClosed() {
        return this.closed;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean isReadOnly() {
        return this.readonly;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setReadOnly(final boolean readOnly) {
        this.readonly = readOnly;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean isValid(final int timeout) {
        return !this.closed;
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
    public String nativeSQL(final String sql) {
        return sql;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public CallableStatement prepareCall(final String sql) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * List the connections schema in selected catalog.
     *
     * @param catalog       the database catalog.
     * @param schemaPattern the schema pattern.
     * @return the schema directories.
     */
    public List<String> getSchemas(final String catalog, final String schemaPattern) {
        List<String> ret = new ArrayList<>();
        if (catalog == null || getCatalog().equalsIgnoreCase(catalog)) {
            final File[] schemas = this.catalog.listFiles(new DirectoryFilter(this.locale, schemaPattern));
            if (schemas != null) {
                Stream.of(schemas).filter(Objects::nonNull).map(File::getName).forEach(ret::add);
            }

            if (schemaPattern == null || Expressions.accept(this.locale, INFORMATION_SCHEMA, schemaPattern, false,
                    '\\')) {
                ret.add(INFORMATION_SCHEMA);
            }
        }

        ret.sort(Comparator.comparing(a -> a));
        return ret;
    }

    /**
     * List the connections schema in selected catalog.
     *
     * @param catalog       the database catalog.
     * @param schemaPattern the schema pattern.
     * @return the schema directories.
     */
    public List<File> getSchemaFiles(final String catalog, final String schemaPattern) {
        List<File> ret = new ArrayList<>();
        if (catalog == null || getCatalog().equalsIgnoreCase(catalog)) {
            final File[] schemas = this.catalog.listFiles(new DirectoryFilter(this.locale, schemaPattern));
            if (schemas != null) {
                Stream.of(schemas).filter(Objects::nonNull).forEach(ret::add);
            }
        }

        ret.sort(Comparator.comparing(a -> a));
        return ret;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency,
                                         final int resultSetHoldability) throws SQLException {
        return this.prepareCall(sql);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public PreparedStatement prepareStatement(final String sql) throws SQLException {
        return prepareStatement(sql, Statement.NO_GENERATED_KEYS);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType,
                                              final int resultSetConcurrency) throws SQLException {
        return prepareStatement(sql, resultSetType, resultSetConcurrency, holdability);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException {
        final PreparedStatement statement = prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY, holdability);
        ((ParadoxPreparedStatement) statement).setAutoGeneratedKeys(autoGeneratedKeys);
        return statement;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType,
                                         final int resultSetConcurrency) throws SQLException {
        return this.prepareCall(sql);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency,
                                              final int resultSetHoldability) throws SQLException {
        final ParadoxPreparedStatement statement = new ParadoxPreparedStatement(this, sql, resultSetType,
                resultSetConcurrency, holdability);
        this.statements.add(statement);
        return statement;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public PreparedStatement prepareStatement(final String sql, final String[] columnNames) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void releaseSavepoint(final Savepoint savepoint) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void rollback() throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void rollback(final Savepoint savepoint) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setClientInfo(final String name, final String value) {
        this.clientInfo.put(name, value);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setNetworkTimeout(final Executor executor, final int milliseconds) {
        this.networkTimeout = milliseconds;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Savepoint setSavepoint() throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Savepoint setSavepoint(final String name) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * Gets the default charset.
     *
     * @return the default charset.
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * Gets the connection locale.
     *
     * @return the connection locale.
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public <T> T unwrap(final Class<T> iFace) throws SQLException {
        return Utils.unwrap(this, iFace);
    }

    /**
     * Gets the BCD rounding.
     *
     * @return <code>true</code> if the BCD rounding is enabled.
     */
    public boolean isBcdRounding() {
        return bcdRounding;
    }

    /**
     * Gets the connection time zone.
     *
     * @return the connection time zone.
     */
    public TimeZone getTimeZone() {
        return timeZone;
    }
}
