package org.paradox;

import org.paradox.metadata.ParadoxDatabaseMetaData;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
import org.paradox.utils.SQLStates;

/**
 * Conexão JDBC com o PARADOX.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 14/03/2009
 */
public class ParadoxConnection implements Connection {

    //private final ArrayList<ParadoxTable> tables = new ArrayList<ParadoxTable>();
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
    private ArrayList<Statement> statements = new ArrayList<Statement>();
    private int transactionIsolation = Connection.TRANSACTION_NONE;
    private FileOutputStream lock;

    public ParadoxConnection(final File dir, final String url) throws SQLException {
        this.url = url;
        this.dir = dir;

        try {
            if (!dir.exists() && !dir.isDirectory()) {
                throw new SQLException("Directory not found.", SQLStates.DIR_NOT_FOUND);
            }
            final File lockFile = new File(dir.getAbsolutePath() + File.separator + "db.lock");
            lock = new FileOutputStream(lockFile);

            catalog = dir.getName();
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
    public CallableStatement prepareCall(String sql) throws SQLException {
        throw new SQLException("No Callable Statement");
    }

    @Override
    public String nativeSQL(final String sql) throws SQLException {
        return sql;
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
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
     * @throws java.sql.SQLException Caso haja erro ao desalocar os recursos
     */
    @Override
    public void close() throws SQLException {
        for (final Statement stmt : statements) {
            stmt.close();
        }
        statements.clear();

        if (lock != null) {
            try {
                lock.close();
            } catch (IOException ex) {
                throw new SQLException("Error unlocking database.", SQLStates.INVALID_STATE, ex);
            }
            lock = null;
        }

        closed = true;
    }

    /**
     * Unlock the database
     * 
     * @throws Throwable in dealocation falt
     */
    @Override
    @SuppressWarnings("FinalizeDeclaration")
    public void finalize() throws Throwable {
        if (lock != null) {
            lock.close();
            lock = null;
        }
        super.finalize();
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
    public void setReadOnly(boolean readOnly) throws SQLException {
        this.readonly = readOnly;
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

    public String getSchema() throws SQLException {
        return "APP";
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

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return warnings;
    }

    @Override
    public void clearWarnings() throws SQLException {
        warnings = null;
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return prepareCall(sql);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return typeMap;
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> typeMap) throws SQLException {
        this.typeMap = typeMap;
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
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
    public Savepoint setSavepoint(String name) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return prepareCall(sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return prepareStatement(sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
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
    public boolean isValid(int timeout) throws SQLException {
        return !closed;
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
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
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return null;
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (isWrapperFor(iface)) {
            return (T) this;
        }
        throw new SQLException("Type not found.", SQLStates.TYPE_NOT_FOUND);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return getClass().isAssignableFrom(iface);
    }

    public File getDir() {
        return dir;
    }
}
