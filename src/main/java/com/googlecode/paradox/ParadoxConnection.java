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
package com.googlecode.paradox;

import com.googlecode.paradox.exceptions.ParadoxConnectionException;
import com.googlecode.paradox.exceptions.ParadoxException;
import com.googlecode.paradox.exceptions.ParadoxNotSupportedException;
import com.googlecode.paradox.metadata.ParadoxDatabaseMetaData;
import com.googlecode.paradox.metadata.schema.DirectorySchema;
import com.googlecode.paradox.parser.SQLParser;
import com.googlecode.paradox.planner.Planner;
import com.googlecode.paradox.planner.plan.Plan;
import com.googlecode.paradox.rowset.ParadoxBlob;
import com.googlecode.paradox.rowset.ParadoxClob;
import com.googlecode.paradox.utils.Utils;

import java.io.File;
import java.lang.ref.SoftReference;
import java.sql.*;
import java.util.*;
import java.util.concurrent.Executor;

/**
 * JDBC Paradox connection implementation.
 *
 * @since 1.0
 */
public final class ParadoxConnection implements Connection {

    /**
     * Stores the opened statements.
     */
    private final List<ParadoxStatement> statements = new ArrayList<>();
    /**
     * Auto Commit flag.
     */
    private boolean autocommit = true;
    /**
     * If this connection is closed.
     */
    private boolean closed;
    /**
     * Default timeout.
     */
    private int networkTimeout;
    /**
     * If this connection is read only.
     */
    private boolean readonly = true;
    /**
     * Stores the transaction isolation mode.
     */
    private int transactionIsolation = Connection.TRANSACTION_NONE;
    /**
     * Stores the JDBC type mapping.
     */
    private Map<String, Class<?>> typeMap;
    /**
     * The connection property information.
     */
    private final ConnectionInfo connectionInfo;
    /**
     * Statement cache.
     */
    private static final HashMap<String, LinkedHashMap<String, SoftReference<Plan<?, ?>>>> STATEMENT_CACHE =
            new HashMap<>();

    /**
     * Creates a new paradox connection.
     *
     * @param dir  database directory.
     * @param url  connect URL.
     * @param info the connection properties.
     * @throws SQLException in case of any connection fault.
     */
    public ParadoxConnection(final File dir, final String url, final Properties info) throws SQLException {
        if (!dir.exists() && !dir.isDirectory()) {
            throw new ParadoxConnectionException(ParadoxConnectionException.Error.DIRECTORY_NOT_FOUND);
        } else if (dir.getParent() == null) {
            throw new ParadoxConnectionException(ParadoxConnectionException.Error.INVALID_ROOT_DIRECTORY);
        }

        this.connectionInfo = new ConnectionInfo(url);
        this.connectionInfo.setProperties(info);
        this.connectionInfo.setCurrentCatalog(dir.getParentFile());
        this.connectionInfo.setCurrentSchema(new DirectorySchema(dir));
    }

    /**
     * Creates an execution plan.
     *
     * @param sql the SQL to analyze.
     * @return the execution plan.
     * @throws SQLException in case of failures.
     */
    @SuppressWarnings("java:S1452")
    public Plan<?, ?> createPlan(final String sql) throws SQLException {
        final LinkedHashMap<String, SoftReference<Plan<?, ?>>> cache = STATEMENT_CACHE.computeIfAbsent(
                this.connectionInfo.getUrl(),
                k -> new LinkedHashMap<>(0x7f, 0.75F, true));

        final SoftReference<Plan<?, ?>> cached = cache.get(sql);
        Plan<?, ?> plan;
        if (cached == null || cached.get() == null) {
            final SQLParser parser = new SQLParser(sql);
            plan = Planner.create(connectionInfo, parser.parse());
            cache.put(sql, new SoftReference<>(plan));
        } else {
            plan = cached.get();
        }

        return plan;
    }

    @Override
    public void abort(final Executor executor) {
        executor.execute(this::abort);
    }

    /**
     * Abort the execution.
     */
    private void abort() {
        for (final ParadoxStatement stmt : statements) {
            try {
                stmt.cancel();
            } catch (final SQLException e) {
                // Do nothing.
            }

            try {
                stmt.close();
            } catch (final SQLException e) {
                // Do nothing.
            }
        }

        statements.clear();
        closed = true;
    }

    @Override
    public void clearWarnings() {
        // Not used.
    }

    @Override
    public void close() throws SQLException {
        for (final Statement stmt : this.statements) {
            stmt.close();
        }

        this.statements.clear();
        this.closed = true;
    }

    @Override
    public void commit() throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public Array createArrayOf(final String typeName, final Object[] elements) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public Blob createBlob() {
        return new ParadoxBlob(new byte[0]);
    }

    @Override
    public Clob createClob() {
        return new ParadoxClob("");
    }

    @Override
    public NClob createNClob() throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public Statement createStatement() {
        return createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    }

    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency) {
        return createStatement(resultSetType, resultSetConcurrency, connectionInfo.getHoldability());
    }

    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency,
                                     final int resultSetHoldability) {
        final ParadoxStatement stmt = new ParadoxStatement(this, resultSetType, resultSetConcurrency,
                resultSetHoldability);
        this.statements.add(stmt);
        return stmt;
    }

    @Override
    public Struct createStruct(final String typeName, final Object[] attributes) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public boolean getAutoCommit() {
        return this.autocommit;
    }

    @Override
    public void setAutoCommit(final boolean autoCommit) {
        this.autocommit = autoCommit;
    }

    @Override
    public String getCatalog() {
        return connectionInfo.getCatalog();
    }

    @Override
    public void setCatalog(final String catalog) throws SQLException {
        this.connectionInfo.setCatalog(catalog);
    }

    @Override
    public Properties getClientInfo() {
        return connectionInfo.getProperties();
    }

    @Override
    public void setClientInfo(final Properties clientInfo) throws SQLClientInfoException {
        this.connectionInfo.setProperties(clientInfo);
    }

    @Override
    public String getClientInfo(final String name) {
        return this.connectionInfo.getProperty(name);
    }

    @Override
    public int getHoldability() {
        return this.connectionInfo.getHoldability();
    }

    @Override
    public void setHoldability(final int holdability) {
        this.connectionInfo.setHoldability(holdability);
    }

    @Override
    public java.sql.DatabaseMetaData getMetaData() {
        return new ParadoxDatabaseMetaData(this);
    }

    @Override
    public int getNetworkTimeout() {
        return this.networkTimeout;
    }

    @Override
    public String getSchema() {
        return this.connectionInfo.getCurrentSchema().name();
    }

    @Override
    public void setSchema(final String schema) throws SQLException {
        this.connectionInfo.setCurrentSchema(schema);
    }

    @Override
    public int getTransactionIsolation() {
        return this.transactionIsolation;
    }

    @Override
    public void setTransactionIsolation(final int level) throws SQLException {
        if (Connection.TRANSACTION_NONE != level) {
            throw new ParadoxException(ParadoxException.Error.INVALID_TRANSACTION_LEVEL);
        }
        this.transactionIsolation = level;
    }

    @Override
    public Map<String, Class<?>> getTypeMap() {
        return this.typeMap;
    }

    @Override
    public void setTypeMap(final Map<String, Class<?>> typeMap) {
        this.typeMap = typeMap;
    }

    @Override
    public SQLWarning getWarnings() {
        return connectionInfo.getWarning();
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    @Override
    public boolean isReadOnly() {
        return this.readonly;
    }

    @Override
    public void setReadOnly(final boolean readOnly) {
        this.readonly = readOnly;
    }

    @Override
    public boolean isValid(final int timeout) {
        return !this.closed;
    }

    @Override
    public boolean isWrapperFor(final Class<?> iFace) {
        return Utils.isWrapperFor(this, iFace);
    }

    @Override
    public String nativeSQL(final String sql) {
        return sql;
    }

    @Override
    public CallableStatement prepareCall(final String sql) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        return this.prepareCall(sql);
    }

    @Override
    public PreparedStatement prepareStatement(final String sql) throws SQLException {
        return prepareStatement(sql, Statement.NO_GENERATED_KEYS);
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        return prepareStatement(sql, resultSetType, resultSetConcurrency, connectionInfo.getHoldability());
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException {
        final PreparedStatement statement = prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY, connectionInfo.getHoldability());
        ((ParadoxPreparedStatement) statement).setAutoGeneratedKeys(autoGeneratedKeys);
        return statement;
    }

    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        return this.prepareCall(sql);
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        final ParadoxPreparedStatement statement = new ParadoxPreparedStatement(this, sql, resultSetType,
                resultSetConcurrency, connectionInfo.getHoldability());
        this.statements.add(statement);
        return statement;
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final String[] columnNames) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void releaseSavepoint(final Savepoint savepoint) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void rollback() throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void rollback(final Savepoint savepoint) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void setClientInfo(final String name, final String value) throws SQLClientInfoException {
        this.connectionInfo.put(name, value);
    }

    @Override
    public void setNetworkTimeout(final Executor executor, final int milliseconds) {
        this.networkTimeout = milliseconds;
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public Savepoint setSavepoint(final String name) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public <T> T unwrap(final Class<T> iFace) throws SQLException {
        return Utils.unwrap(this, iFace);
    }

    /**
     * Gets the connection information.
     *
     * @return the connection information.
     */
    public ConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }
}
