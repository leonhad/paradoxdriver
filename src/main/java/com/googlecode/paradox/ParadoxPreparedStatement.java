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

import com.googlecode.paradox.exceptions.*;
import com.googlecode.paradox.planner.plan.Plan;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.rowset.ValuesConverter;
import com.googlecode.paradox.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.sql.Date;
import java.sql.*;
import java.util.*;

/**
 * {@link PreparedStatement} implementation class.
 *
 * @version 1.8
 * @since 1.6.0
 */
@SuppressWarnings({"java:S1448", "java:S1200"})
class ParadoxPreparedStatement extends ParadoxStatement implements PreparedStatement {

    /**
     * Execution list.
     */
    protected final List<Object[]> executions = new ArrayList<>();

    /**
     * Execution parameter list.
     */
    protected final List<ParadoxType[]> executionTypes = new ArrayList<>();

    /**
     * Parameter list.
     */
    private final Object[] currentParameterValues;

    /**
     * Parameter type list.
     */
    private final ParadoxType[] currentParameterTypes;

    ParadoxPreparedStatement(final ParadoxConnection connection, final String sql, final int resultSetType,
                             final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        super(connection, resultSetType, resultSetConcurrency, resultSetHoldability);

        this.statements.add(connection.createPlan(sql));

        if (statements.size() > 1) {
            throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.USE_BATCH_OPERATION);
        }

        currentParameterValues = new Object[statements.get(0).getParameterCount()];
        currentParameterTypes = new ParadoxType[statements.get(0).getParameterCount()];
    }

    public void setAutoGeneratedKeys(int autoGeneratedKeys) {
        this.autoGeneratedKeys = autoGeneratedKeys;
    }

    @Override
    protected int[] executeStatements() throws SQLException {
        final ArrayList<Integer> ret = new ArrayList<>();
        // One for statement.
        for (final Plan<?, ?> plan : statements) {
            // One for parameters.
            for (int i = 0; i < executions.size(); i++) {
                final Object[] params = executions.get(i);
                final ParadoxType[] types = executionTypes.get(i);
                try {
                    ret.addAll(executeStatement(plan, params, types));
                } catch (@SuppressWarnings("java:S1166") final InternalException e) {
                    throw e.getCause();
                }
            }
        }

        return ret.stream().mapToInt(Integer::intValue).toArray();
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        executions.add(currentParameterValues);
        executionTypes.add(currentParameterTypes);

        executeStatements();

        resultSetIndex = 0;
        return getResultSet();
    }

    @Override
    public int executeUpdate() throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        checkIndex(parameterIndex);
        currentParameterValues[parameterIndex - 1] = null;
        currentParameterTypes[parameterIndex - 1] = ParadoxType.valueOf(sqlType);
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        checkIndex(parameterIndex);
        currentParameterValues[parameterIndex - 1] = x;
        currentParameterTypes[parameterIndex - 1] = ParadoxType.BOOLEAN;
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        checkIndex(parameterIndex);
        currentParameterValues[parameterIndex - 1] = x;
        currentParameterTypes[parameterIndex - 1] = ParadoxType.INTEGER;
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        checkIndex(parameterIndex);
        currentParameterValues[parameterIndex - 1] = x;
        currentParameterTypes[parameterIndex - 1] = ParadoxType.INTEGER;
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        checkIndex(parameterIndex);
        currentParameterValues[parameterIndex - 1] = x;
        currentParameterTypes[parameterIndex - 1] = ParadoxType.INTEGER;
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        checkIndex(parameterIndex);
        currentParameterValues[parameterIndex - 1] = x;
        currentParameterTypes[parameterIndex - 1] = ParadoxType.NUMBER;
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        checkIndex(parameterIndex);
        currentParameterValues[parameterIndex - 1] = x;
        currentParameterTypes[parameterIndex - 1] = ParadoxType.NUMBER;
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        checkIndex(parameterIndex);
        currentParameterValues[parameterIndex - 1] = x;
        currentParameterTypes[parameterIndex - 1] = ParadoxType.NUMBER;
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        checkIndex(parameterIndex);
        currentParameterValues[parameterIndex - 1] = x;
        currentParameterTypes[parameterIndex - 1] = ParadoxType.NUMBER;
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        checkIndex(parameterIndex);
        currentParameterValues[parameterIndex - 1] = x;
        currentParameterTypes[parameterIndex - 1] = ParadoxType.VARCHAR;
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        checkIndex(parameterIndex);
        currentParameterValues[parameterIndex - 1] = x;
        currentParameterTypes[parameterIndex - 1] = ParadoxType.BLOB;
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        checkIndex(parameterIndex);
        currentParameterValues[parameterIndex - 1] = x;
        currentParameterTypes[parameterIndex - 1] = ParadoxType.DATE;
    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        checkIndex(parameterIndex);
        currentParameterValues[parameterIndex - 1] = x;
        currentParameterTypes[parameterIndex - 1] = ParadoxType.TIME;
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        checkIndex(parameterIndex);
        currentParameterValues[parameterIndex - 1] = x;
        currentParameterTypes[parameterIndex - 1] = ParadoxType.TIMESTAMP;
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        checkIndex(parameterIndex);
        currentParameterValues[parameterIndex - 1] = x;
        currentParameterTypes[parameterIndex - 1] = ParadoxType.MEMO;
    }

    /**
     * @deprecated This method exists only to keep compatibility.
     */
    @Deprecated
    @Override
    @SuppressWarnings("java:S1133")
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void clearParameters() {
        Arrays.fill(currentParameterValues, null);
    }

    @Override
    public void setObject(int parameterIndex, Object x, int type) throws SQLException {
        checkIndex(parameterIndex);
        currentParameterValues[parameterIndex - 1] = x;
        currentParameterTypes[parameterIndex - 1] = ParadoxType.valueOf(type);
    }

    @Override
    public void setObject(int parameterIndex, Object instance) throws SQLException {
        checkIndex(parameterIndex);
        currentParameterValues[parameterIndex - 1] = instance;

        if (instance != null) {
            currentParameterTypes[parameterIndex - 1] = ParadoxType.valueOf(instance.getClass());
        } else {
            currentParameterTypes[parameterIndex - 1] = ParadoxType.NULL;
        }
    }

    @Override
    public boolean execute() throws SQLException {
        return executeQuery() != null;
    }

    @Override
    public void addBatch() {
        executions.add(currentParameterValues);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        checkIndex(parameterIndex);
        currentParameterValues[parameterIndex - 1] = x;
        currentParameterTypes[parameterIndex - 1] = ParadoxType.BLOB;
    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        checkIndex(parameterIndex);
        currentParameterValues[parameterIndex - 1] = ParadoxType.MEMO;
    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        @SuppressWarnings("java:S2095") final ResultSet current = getResultSet();
        return current.getMetaData();
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        setNull(parameterIndex, sqlType);
    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void setObject(final int parameterIndex, final Object x, final int sqlType, final int scaleOrLength)
            throws SQLException {
        checkIndex(parameterIndex);

        Object value = x;

        if (x instanceof InputStream) {
            value = ValuesConverter.getBytes((InputStream) x, scaleOrLength);
        } else if (x instanceof Reader) {
            try {
                final char[] chars = new char[scaleOrLength];
                if (((Reader) x).read(chars) != scaleOrLength) {
                    throw new ParadoxDataException(DataError.INVALID_CONVERSION, x);
                }
                value = new String(chars);
            } catch (final IOException e) {
                throw new ParadoxDataException(DataError.INVALID_CONVERSION, e, x);
            }
        } else if (sqlType == Types.NUMERIC || sqlType == Types.DECIMAL) {
            value = ValuesConverter.getBigDecimal(x, connectionInfo).setScale(scaleOrLength, RoundingMode.DOWN);
        }

        currentParameterValues[parameterIndex - 1] = value;
        currentParameterTypes[parameterIndex - 1] = ParadoxType.valueOf(sqlType);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void setAsciiStream(final int parameterIndex, final InputStream x) throws SQLException {
        setBinaryStream(parameterIndex, x);
    }

    @Override
    public void setBinaryStream(final int parameterIndex, final InputStream x) throws SQLException {
        checkIndex(parameterIndex);
        currentParameterValues[parameterIndex - 1] = x;
        currentParameterTypes[parameterIndex - 1] = ParadoxType.BLOB;
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void addBatch(final String sql) throws SQLException {
        if (connection == null || connection.get() == null) {
            throw new ParadoxConnectionException(ParadoxConnectionException.Error.NOT_CONNECTED);
        }

        final Plan<?, ?> plan = Objects.requireNonNull(connection.get()).createPlan(sql);

        if (plan.getParameterCount() != currentParameterValues.length) {
            throw new ParadoxSyntaxErrorException(SyntaxError.INCONSISTENT_PARAMETER_LIST);
        }

        this.statements.add(plan);
    }

    @Override
    public void clearBatch() {
        this.executions.clear();
        while (this.statements.size() > 1) {
            this.statements.remove(1);
        }
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public <T> T unwrap(Class<T> iFace) throws SQLException {
        return Utils.unwrap(this, iFace);
    }

    @Override
    public boolean isWrapperFor(Class<?> iFace) {
        return Utils.isWrapperFor(this, iFace);
    }

    private void checkIndex(final int index) throws ParadoxException {
        if (index < 1 || index > currentParameterValues.length) {
            throw new ParadoxException(ParadoxException.Error.INVALID_COLUMN_INDEX, Integer.toString(index), null);
        }
    }

    @Override
    public void close() throws SQLException {
        super.close();

        executions.clear();
        Arrays.fill(currentParameterValues, null);
    }
}
