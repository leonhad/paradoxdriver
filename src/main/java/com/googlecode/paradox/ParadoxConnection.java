/*
 * ParadoxConnection.java 03/14/2009 Copyright (C) 2009 Leonardo Alves da Costa This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox;

import com.googlecode.paradox.metadata.ParadoxDatabaseMetaData;
import com.googlecode.paradox.utils.SQLStates;
import com.googlecode.paradox.utils.Utils;

import java.io.File;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * JDBC Paradox connection implementation.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.0
 */
public final class ParadoxConnection implements Connection {

    /**
     * Auto Commit flag.
     */
    private boolean autocommit = true;
    /**
     * Database catalog.
     */
    private final String catalog;
    /**
     * Connection properties info.
     */
    private Properties clientInfo = new Properties();
    /**
     * If this connection is closed.
     */
    private boolean closed;
    /**
     * Store the connection directory reference.
     */
    private final File dir;
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
    private String schema;
    /**
     * Stores the opened statements.
     */
    private final ArrayList<Statement> statements = new ArrayList<>();
    /**
     * Stores the transaction isolation mode.
     */
    private int transactionIsolation = Connection.TRANSACTION_NONE;
    /**
     * Stores the JDBC type mapping.
     */
    private Map<String, Class<?>> typeMap;
    /**
     * Driver URL.
     */
    private final String url;

    /**
     * Creates a new paradox connection.
     *
     * @param dir
     *            database directory.
     * @param url
     *            connect URL.
     * @throws SQLException
     *             in any connection fault.
     */
    public ParadoxConnection(final File dir, final String url) throws SQLException {
        this.url = url;
        this.dir = dir;

        if (!dir.exists() && !dir.isDirectory()) {
            throw new SQLException("Directory not found.", SQLStates.DIR_NOT_FOUND.getValue());
        }
        this.schema = dir.getName();

        if (dir.getParent() != null && !dir.getParent().isEmpty()) {
            this.catalog = dir.getParentFile().getName();
        } else {
            this.catalog = "APP";
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void abort(final Executor executor) throws SQLException {
        throw new SQLFeatureNotSupportedException();
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
    public void commit() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Array createArrayOf(final String typeName, final Object[] elements) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Blob createBlob() {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Clob createClob() {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public NClob createNClob() {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public SQLXML createSQLXML() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Statement createStatement() {
        final Statement stmt = new ParadoxStatement(this);
        this.statements.add(stmt);
        return stmt;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency) {
        return this.createStatement();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency,
            final int resultSetHoldability) {
        return this.createStatement();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Struct createStruct(final String typeName, final Object[] attributes) {
        return null;
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
    public String getCatalog() {
        return this.catalog;
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
    public String getClientInfo(final String name) {
        return this.clientInfo.getProperty(name);
    }

    /**
     * Gets the current directory.
     *
     * @return the current directory.
     */
    public File getDir() {
        return this.dir;
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
        return this.schema;
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
    public Map<String, Class<?>> getTypeMap() {
        return this.typeMap;
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
        throw new SQLException("No Callable Statement");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency)
            throws SQLException {
        return this.prepareCall(sql);
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
        throw new SQLException("No Prepared Statement");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException {
        return this.prepareStatement(sql);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency)
            throws SQLException {
        return this.prepareStatement(sql);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency,
            final int resultSetHoldability) throws SQLException {
        return this.prepareStatement(sql);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) throws SQLException {
        return this.prepareStatement(sql);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public PreparedStatement prepareStatement(final String sql, final String[] columnNames) throws SQLException {
        return this.prepareStatement(sql);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void releaseSavepoint(final Savepoint savepoint) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void rollback() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void rollback(final Savepoint savepoint) {
        throw new UnsupportedOperationException();
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
    public void setCatalog(final String catalog) throws SQLException {
        throw new SQLException("Change catalog not supported.", SQLStates.CHANGE_CATALOG_NOT_SUPPORTED.getValue());
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
    public void setClientInfo(final String name, final String value) {
        this.clientInfo.put(name, value);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setHoldability(final int holdability) throws SQLException {
        if ((holdability != ResultSet.HOLD_CURSORS_OVER_COMMIT) && (holdability != ResultSet.CLOSE_CURSORS_AT_COMMIT)) {
            throw new SQLException("Invalid parameter.", SQLStates.INVALID_PARAMETER.getValue());
        }
        this.holdability = holdability;
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
    public void setReadOnly(final boolean readOnly) {
        this.readonly = readOnly;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Savepoint setSavepoint() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Savepoint setSavepoint(final String name) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setSchema(final String schema) {
        this.schema = schema;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setTransactionIsolation(final int level) throws SQLException {
        if (Connection.TRANSACTION_NONE != level) {
            throw new SQLException("Invalid level.");
        }
        this.transactionIsolation = level;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setTypeMap(final Map<String, Class<?>> typeMap) {
        this.typeMap = typeMap;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public <T> T unwrap(final Class<T> iFace) throws SQLException {
        return Utils.unwrap(this, iFace);
    }
}
