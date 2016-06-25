/*
 * ParadoxConnection.java
 *
 * 03/14/2009
 * Copyright (C) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
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

import com.googlecode.paradox.metadata.ParadoxDatabaseMetaData;
import com.googlecode.paradox.utils.SQLStates;
import com.googlecode.paradox.utils.Utils;

/**
 * JDBC Paradox connection implementation.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.0
 */
public class ParadoxConnection implements Connection {

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
    private boolean closed = false;

    /**
     * Store the connection directory reference.
     */
    private final File dir;

    /**
     * Ajusta a capacidade da conexão de manter os cursores
     */
    private int holdability = ResultSet.CLOSE_CURSORS_AT_COMMIT;

    /**
     * Show debug message.
     */
    private final boolean isDebugMode = true;

    /**
     * Holds the file lock used in transactions.
     */
    private FileLock lock;

    private RandomAccessFile lockFile;

    /**
     * Default timeout
     */
    private int networkTimeout = 0;

    /**
     * Se esta conexão é somente leitura
     */
    private boolean readonly = true;

    /**
     * Selected Schema
     */
    private String schema = "APP";

    /**
     * Holds the opened statements.
     */
    private final ArrayList<Statement> statements = new ArrayList<Statement>();

    /**
     * Store the transaction isolation mode.
     */
    private int transactionIsolation = Connection.TRANSACTION_NONE;

    /**
     * Holds the JDBC type mapping.
     */
    private Map<String, Class<?>> typeMap;

    /**
     * Driver URL
     */
    private final String url;

    /**
     * Warnings
     */
    private SQLWarning warnings = null;

    /**
     * Creates a new paradox connection.
     *
     * @param dir database directory.
     * @param url connect URL.
     * @param info connection properties.
     * @throws SQLException in any connection fault.
     */
    public ParadoxConnection(final File dir, final String url, final Properties info) throws SQLException {
        this.url = url;
        this.dir = dir;

        if (info != null && info.size() > 0) {
            throw new SQLFeatureNotSupportedException("Change properties is not supported yet.");
        }

        try {
            if (!dir.exists() && !dir.isDirectory()) {
                throw new SQLException("Directory not found.", SQLStates.DIR_NOT_FOUND);
            }
            lockFile = new RandomAccessFile(new File(dir.getAbsolutePath() + File.separator + "db.lock"), "rw");
            final FileChannel channel = lockFile.getChannel();
            lock = channel.tryLock();
            if (lock == null || !lock.isValid() || lock.isShared()) {
                throw new SQLException("Database is locked");
            }
            catalog = dir.getName();
        } catch (final FileNotFoundException e) {
            throw new SQLException(e);
        } catch (final IOException e) {
            throw new SQLException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void abort(final Executor executor) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearWarnings() throws SQLException {
        warnings = null;
    }

    /**
     * Fecha a conexão com o PARADOX
     *
     * @throws java.sql.SQLException
     *             Caso haja erro ao desalocar os recursos
     */
    @Override
    public void close() throws SQLException {
        for (final Statement stmt : statements) {
            stmt.close();
        }
        statements.clear();

        if (lock != null) {
            try {
                lock.release();
            } catch (final IOException ex) {
                throw new SQLException("Error unlocking database.", SQLStates.INVALID_STATE, ex);
            }
            lock = null;
        }
        if (lockFile != null) {
            try {
                lockFile.close();
            } catch (final IOException ex) {
                throw new SQLException("Can't release lock file.", SQLStates.INVALID_STATE, ex);
            }
        }
        closed = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void commit() throws SQLException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Array createArrayOf(final String typeName, final Object[] elements) throws SQLException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Blob createBlob() throws SQLException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Clob createClob() throws SQLException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NClob createNClob() throws SQLException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SQLXML createSQLXML() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Statement createStatement() throws SQLException {
        final Statement stmt = new ParadoxStatement(this);
        statements.add(stmt);
        return stmt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency) throws SQLException {
        return createStatement();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency,
            final int resultSetHoldability) throws SQLException {
        return createStatement();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Struct createStruct(final String typeName, final Object[] attributes) throws SQLException {
        return null;
    }

    public void debug(final String message) {
        if (isDebugMode) {
            System.out.println(message);
        }
    }

    public void debug(final String format, final Object... params) {
        this.debug(String.format(format, params));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getAutoCommit() throws SQLException {
        return autocommit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCatalog() throws SQLException {
        return catalog;
    }

    public String getCatalogName() {
        return catalog;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Properties getClientInfo() throws SQLException {
        return clientInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClientInfo(final String name) throws SQLException {
        return clientInfo.getProperty(name);
    }

    public File getDir() {
        return dir;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getHoldability() throws SQLException {
        return holdability;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return new ParadoxDatabaseMetaData(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNetworkTimeout() throws SQLException {
        return networkTimeout;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSchema() throws SQLException {
        return schema;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTransactionIsolation() throws SQLException {
        return transactionIsolation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return typeMap;
    }

    public String getUrl() {
        return url;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SQLWarning getWarnings() throws SQLException {
        return warnings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isClosed() throws SQLException {
        return closed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isReadOnly() throws SQLException {
        return readonly;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(final int timeout) throws SQLException {
        return !closed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return Utils.isWrapperFor(this, iface);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String nativeSQL(final String sql) throws SQLException {
        return sql;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CallableStatement prepareCall(final String sql) throws SQLException {
        throw new SQLException("No Callable Statement");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency)
            throws SQLException {
        return prepareCall(sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency,
            final int resultSetHoldability) throws SQLException {
        return prepareCall(sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PreparedStatement prepareStatement(final String sql) throws SQLException {
        throw new SQLException("No Prepared Statement");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException {
        return prepareStatement(sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency)
            throws SQLException {
        return prepareStatement(sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency,
            final int resultSetHoldability) throws SQLException {
        return prepareStatement(sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) throws SQLException {
        return prepareStatement(sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PreparedStatement prepareStatement(final String sql, final String[] columnNames) throws SQLException {
        return prepareStatement(sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rollback() throws SQLException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rollback(final Savepoint savepoint) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAutoCommit(final boolean autoCommit) throws SQLException {
        autocommit = autoCommit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCatalog(final String catalog) throws SQLException {
        throw new SQLException("Change catalog not supported.", SQLStates.CHANGE_CATALOG_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setClientInfo(final Properties clientInfo) throws SQLClientInfoException {
        this.clientInfo = clientInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setClientInfo(final String name, final String value) throws SQLClientInfoException {
        clientInfo.put(name, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHoldability(final int holdability) throws SQLException {
        if (holdability != ResultSet.HOLD_CURSORS_OVER_COMMIT && holdability != ResultSet.CLOSE_CURSORS_AT_COMMIT) {
            throw new SQLException("Invalid parameter.", SQLStates.INVALID_PARAMETER);
        }
        this.holdability = holdability;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNetworkTimeout(final Executor executor, final int milliseconds) throws SQLException {
        networkTimeout = milliseconds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReadOnly(final boolean readOnly) throws SQLException {
        readonly = readOnly;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Savepoint setSavepoint() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Savepoint setSavepoint(final String name) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSchema(final String schema) throws SQLException {
        this.schema = schema;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTransactionIsolation(final int level) throws SQLException {
        if (Connection.TRANSACTION_NONE != level) {
            throw new SQLException("Invalid level.");
        }
        transactionIsolation = level;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTypeMap(final Map<String, Class<?>> typeMap) throws SQLException {
        this.typeMap = typeMap;
    }

    public void setWarnings(final SQLWarning warning) {
        warnings = warning;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        return Utils.unwrap(this, iface);
    }
}
