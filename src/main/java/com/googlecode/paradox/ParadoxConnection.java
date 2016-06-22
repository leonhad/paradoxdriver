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
 * Conexão JDBC com o PARADOX.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.0
 */
public class ParadoxConnection implements Connection {

    /**
     * Se esta conexão está fechada
     */
    private boolean closed = false;
    /**
     * Se esta conexão é somente leitura
     */
    private boolean readonly = true;
    /**
     * Auto Commit
     */
    private boolean autocommit = true;
    /**
     * Warnings
     */
    private SQLWarning warnings = null;
    /**
     * Catálogo
     */
    private final String catalog;
    /**
     * Informações do cliente
     */
    private Properties clientInfo = new Properties();
    /**
     * Ajusta a capacidade da conexão de manter os cursores
     */
    private int holdability = ResultSet.CLOSE_CURSORS_AT_COMMIT;
    private Map<String, Class<?>> typeMap;
    /**
     * Driver URL
     */
    private final String url;
    private final File dir;
    private final ArrayList<Statement> statements = new ArrayList<Statement>();
    private int transactionIsolation = Connection.TRANSACTION_NONE;
    private FileLock lock;
    private RandomAccessFile lockFile;

    /**
     * Default timeout
     */
    private int networkTimeout = 0;
    /**
     * Selected Schema
     */
    private String schema = "APP";

    /**
     * Show debug message.
     */
    private final boolean isDebugMode = true;

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

    public String getCatalogName() {
        return catalog;
    }

    @Override
    public Statement createStatement() throws SQLException {
        final Statement stmt = new ParadoxStatement(this);
        statements.add(stmt);
        return stmt;
    }

    @Override
    public PreparedStatement prepareStatement(final String sql) throws SQLException {
        throw new SQLException("No Prepared Statement");
    }

    @Override
    public CallableStatement prepareCall(final String sql) throws SQLException {
        throw new SQLException("No Callable Statement");
    }

    @Override
    public String nativeSQL(final String sql) throws SQLException {
        return sql;
    }

    @Override
    public void setAutoCommit(final boolean autoCommit) throws SQLException {
        autocommit = autoCommit;
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return autocommit;
    }

    @Override
    public void commit() throws SQLException {
    }

    @Override
    public void rollback() throws SQLException {
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

    @Override
    public boolean isClosed() throws SQLException {
        return closed;
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return new ParadoxDatabaseMetaData(this);
    }

    @Override
    public void setReadOnly(final boolean readOnly) throws SQLException {
        readonly = readOnly;
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return readonly;
    }

    @Override
    public void setCatalog(final String catalog) throws SQLException {
        throw new SQLException("Change catalog not supported.", SQLStates.CHANGE_CATALOG_NOT_SUPPORTED);
    }

    @Override
    public String getCatalog() throws SQLException {
        return catalog;
    }

    @Override
    public String getSchema() throws SQLException {
        return schema;
    }

    @Override
    public void setTransactionIsolation(final int level) throws SQLException {
        if (Connection.TRANSACTION_NONE != level) {
            throw new SQLException("Invalid level.");
        }
        transactionIsolation = level;
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return transactionIsolation;
    }

    public void setWarnings(final SQLWarning warning) {
        warnings = warning;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return warnings;
    }

    @Override
    public void clearWarnings() throws SQLException {
        warnings = null;
    }

    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency) throws SQLException {
        return createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency)
            throws SQLException {
        return prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency)
            throws SQLException {
        return prepareCall(sql);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return typeMap;
    }

    @Override
    public void setTypeMap(final Map<String, Class<?>> typeMap) throws SQLException {
        this.typeMap = typeMap;
    }

    @Override
    public void setHoldability(final int holdability) throws SQLException {
        if (holdability != ResultSet.HOLD_CURSORS_OVER_COMMIT && holdability != ResultSet.CLOSE_CURSORS_AT_COMMIT) {
            throw new SQLException("Invalid parameter.", SQLStates.INVALID_PARAMETER);
        }
        this.holdability = holdability;
    }

    @Override
    public int getHoldability() throws SQLException {
        return holdability;
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public Savepoint setSavepoint(final String name) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void rollback(final Savepoint savepoint) throws SQLException {
    }

    @Override
    public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
    }

    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency,
            final int resultSetHoldability) throws SQLException {
        return createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency,
            final int resultSetHoldability) throws SQLException {
        return prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency,
            final int resultSetHoldability) throws SQLException {
        return prepareCall(sql);
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException {
        return prepareStatement(sql);
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) throws SQLException {
        return prepareStatement(sql);
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final String[] columnNames) throws SQLException {
        return prepareStatement(sql);
    }

    @Override
    public Clob createClob() throws SQLException {
        return null;
    }

    @Override
    public Blob createBlob() throws SQLException {
        return null;
    }

    @Override
    public NClob createNClob() throws SQLException {
        return null;
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public boolean isValid(final int timeout) throws SQLException {
        return !closed;
    }

    @Override
    public void setClientInfo(final String name, final String value) throws SQLClientInfoException {
        clientInfo.put(name, value);
    }

    public String getUrl() {
        return url;
    }

    @Override
    public void setClientInfo(final Properties clientInfo) throws SQLClientInfoException {
        this.clientInfo = clientInfo;
    }

    @Override
    public String getClientInfo(final String name) throws SQLException {
        return clientInfo.getProperty(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return clientInfo;
    }

    @Override
    public Array createArrayOf(final String typeName, final Object[] elements) throws SQLException {
        return null;
    }

    @Override
    public Struct createStruct(final String typeName, final Object[] attributes) throws SQLException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        return Utils.unwrap(this, iface);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return Utils.isWrapperFor(this, iface);
    }

    public File getDir() {
        return dir;
    }

    @Override
    public void setSchema(final String schema) throws SQLException {
        this.schema = schema;
    }

    @Override
    public void abort(final Executor executor) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void setNetworkTimeout(final Executor executor, final int milliseconds) throws SQLException {
        networkTimeout = milliseconds;
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return networkTimeout;
    }

    public void debug(final String message) {
        if (isDebugMode) {
            System.out.println(message);
        }
    }

    public void debug(final String format, final Object... params) {
        this.debug(String.format(format, params));
    }
}
