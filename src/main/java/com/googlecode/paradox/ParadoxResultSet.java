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

import com.googlecode.paradox.exceptions.ParadoxException;
import com.googlecode.paradox.exceptions.ParadoxNotSupportedException;
import com.googlecode.paradox.metadata.ResultSetMetaData;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.rowset.DataNavigation;
import com.googlecode.paradox.rowset.ParadoxBlob;
import com.googlecode.paradox.rowset.ParadoxClob;
import com.googlecode.paradox.rowset.ValuesConverter;
import com.googlecode.paradox.utils.Utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * JDBC ResultSet implementation.
 *
 * @version 1.6
 * @since 1.0
 */
public final class ParadoxResultSet implements ResultSet {

    /**
     * Default fetch size.
     */
    private static final int FETCH_SIZE = 10;
    /**
     * {@link ResultSet} columns.
     */
    private final List<Column> columns;
    /**
     * This {@link ResultSet} {@link Statement}.
     */
    private final WeakReference<Statement> statement;
    /**
     * Facade to navigate in data values.
     */
    private final DataNavigation dataNavigation;
    /**
     * The connection information.
     */
    private ConnectionInfo connectionInfo;
    /**
     * The amount of rows fetched.
     */
    private int fetchSize = ParadoxResultSet.FETCH_SIZE;
    /**
     * Result set type.
     */
    private int type = ResultSet.TYPE_SCROLL_INSENSITIVE;
    /**
     * Concurrency type.
     */
    private int concurrency = ResultSet.CONCUR_READ_ONLY;

    /**
     * Creates a new {@link ResultSet}.
     *
     * @param connectionInfo the connection information.
     * @param statement      the {@link Statement} for this {@link ResultSet}.
     * @param values         row and column values.
     * @param columns        the columns name.
     */
    public ParadoxResultSet(final ConnectionInfo connectionInfo, final Statement statement,
                            final List<? extends Object[]> values, final List<Column> columns) {
        this.statement = new WeakReference<>(statement);
        this.columns = columns;
        this.connectionInfo = connectionInfo;

        // Fix column indexes.
        int index = 1;
        for (final Column column : this.columns) {
            if (!column.isHidden()) {
                column.setIndex(index);
                index++;
            }
        }

        this.dataNavigation = new DataNavigation(columns, values);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean absolute(final int row) throws SQLException {
        return dataNavigation.absolute(row);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void afterLast() throws SQLException {
        this.dataNavigation.afterLast();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void beforeFirst() throws SQLException {
        this.dataNavigation.beforeFirst();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void cancelRowUpdates() throws ParadoxNotSupportedException {
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
    public void close() {
        this.dataNavigation.close();
        this.connectionInfo = null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void deleteRow() throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int findColumn(final String columnLabel) throws SQLException {
        for (final Column column : this.columns) {
            if (column.getName().equalsIgnoreCase(columnLabel)) {
                return column.getIndex();
            }
        }

        throw new ParadoxException(ParadoxException.Error.INVALID_COLUMN, columnLabel);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean first() throws SQLException {
        return this.dataNavigation.first();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Array getArray(final int columnIndex) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Array getArray(final String columnLabel) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public InputStream getAsciiStream(final int columnIndex) throws SQLException {
        final String val = ValuesConverter.getString(dataNavigation.getColumnValue(columnIndex), connectionInfo);
        if (val != null) {
            new ByteArrayInputStream(val.getBytes(StandardCharsets.UTF_8));
        }

        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public InputStream getAsciiStream(final String columnLabel) throws SQLException {
        return this.getAsciiStream(this.findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public BigDecimal getBigDecimal(final int columnIndex) throws SQLException {
        return ValuesConverter.getBigDecimal(dataNavigation.getColumnValue(columnIndex), connectionInfo);
    }

    /**
     * {@inheritDoc}.
     *
     * @deprecated this method is only used for JDBC compatibility.
     */
    @SuppressWarnings("squid:S1133")
    @Deprecated
    @Override
    public BigDecimal getBigDecimal(final int columnIndex, final int scale) throws SQLException {
        final BigDecimal value = this.getBigDecimal(columnIndex);
        return value.setScale(scale, RoundingMode.HALF_DOWN);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public BigDecimal getBigDecimal(final String columnLabel) throws SQLException {
        return this.getBigDecimal(this.findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     *
     * @deprecated this method is only used for JDBC compatibility.
     */
    @SuppressWarnings("squid:S1133")
    @Deprecated
    @Override
    public BigDecimal getBigDecimal(final String columnLabel, final int scale) throws SQLException {
        return this.getBigDecimal(this.findColumn(columnLabel), scale);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public InputStream getBinaryStream(final int columnIndex) throws SQLException {
        final byte[] val = ValuesConverter.getByteArray(dataNavigation.getColumnValue(columnIndex), connectionInfo);
        if (val != null) {
            return new ByteArrayInputStream(val);
        }

        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public InputStream getBinaryStream(final String columnLabel) throws SQLException {
        return this.getBinaryStream(this.findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Blob getBlob(final int columnIndex) throws SQLException {
        final byte[] val = ValuesConverter.getByteArray(dataNavigation.getColumnValue(columnIndex), connectionInfo);
        if (val != null) {
            return new ParadoxBlob(val);
        }

        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Blob getBlob(final String columnLabel) throws SQLException {
        return getBlob(this.findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean getBoolean(final int columnIndex) throws SQLException {
        Boolean ret = ValuesConverter.getBoolean(dataNavigation.getColumnValue(columnIndex), connectionInfo);
        if (ret != null) {
            return ret;
        }

        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean getBoolean(final String columnLabel) throws SQLException {
        return this.getBoolean(this.findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public byte getByte(final int columnIndex) throws SQLException {
        Byte ret = ValuesConverter.getByte(dataNavigation.getColumnValue(columnIndex), connectionInfo);
        if (ret != null) {
            return ret;
        }

        return 0;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public byte getByte(final String columnLabel) throws SQLException {
        return this.getByte(this.findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public byte[] getBytes(final int columnIndex) throws SQLException {
        return ValuesConverter.getByteArray(dataNavigation.getColumnValue(columnIndex), connectionInfo);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public byte[] getBytes(final String columnLabel) throws SQLException {
        return this.getBytes(this.findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Reader getCharacterStream(final int columnIndex) throws SQLException {
        final String val = ValuesConverter.getString(dataNavigation.getColumnValue(columnIndex), connectionInfo);
        if (val != null) {
            return new StringReader(val);
        }
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Reader getCharacterStream(final String columnLabel) throws SQLException {
        return this.getCharacterStream(this.findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Clob getClob(final int columnIndex) throws SQLException {
        final String val = ValuesConverter.getString(dataNavigation.getColumnValue(columnIndex), connectionInfo);
        if (val != null) {
            return new ParadoxClob(val);
        }

        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Clob getClob(final String columnLabel) throws SQLException {
        return this.getClob(this.findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getConcurrency() {
        return concurrency;
    }

    /**
     * Sets this concurrency type.
     *
     * @param concurrency the concurrency type.
     */
    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getCursorName() {
        return "NO_NAME";
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Date getDate(final int columnIndex) throws SQLException {
        return ValuesConverter.getDate(dataNavigation.getColumnValue(columnIndex), connectionInfo);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Date getDate(final int columnIndex, final Calendar c) throws SQLException {
        return this.getDate(columnIndex);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Date getDate(final String columnLabel) throws SQLException {
        return this.getDate(this.findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Date getDate(final String columnLabel, final Calendar cal) throws SQLException {
        return this.getDate(this.findColumn(columnLabel), cal);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public double getDouble(final int columnIndex) throws SQLException {
        Double ret = ValuesConverter.getDouble(dataNavigation.getColumnValue(columnIndex), connectionInfo);
        if (ret != null) {
            return ret;
        }

        return 0.0;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public double getDouble(final String columnLabel) throws SQLException {
        return this.getDouble(this.findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getFetchDirection() throws SQLException {
        return dataNavigation.getFetchDirection();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setFetchDirection(final int direction) throws SQLException {
        this.dataNavigation.setFetchDirection(direction);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getFetchSize() {
        return this.fetchSize;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setFetchSize(final int rows) {
        this.fetchSize = rows;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public float getFloat(final int columnIndex) throws SQLException {
        Float ret = ValuesConverter.getFloat(dataNavigation.getColumnValue(columnIndex), connectionInfo);
        if (ret != null) {
            return ret;
        }

        return 0.0F;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public float getFloat(final String columnLabel) throws SQLException {
        return this.getFloat(this.findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getHoldability() {
        return this.connectionInfo.getHoldability();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getInt(final int columnIndex) throws SQLException {
        Integer ret = ValuesConverter.getInteger(dataNavigation.getColumnValue(columnIndex), connectionInfo);
        if (ret != null) {
            return ret;
        }

        return 0;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getInt(final String columnLabel) throws SQLException {
        return this.getInt(this.findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public long getLong(final int columnIndex) throws SQLException {
        Long ret = ValuesConverter.getLong(dataNavigation.getColumnValue(columnIndex), connectionInfo);
        if (ret != null) {
            return ret;
        }

        return 0;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public long getLong(final String columnLabel) throws SQLException {
        return this.getLong(this.findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public java.sql.ResultSetMetaData getMetaData() {
        return new ResultSetMetaData(this.connectionInfo, this.columns);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Reader getNCharacterStream(final int columnIndex) throws SQLException {
        return getCharacterStream(columnIndex);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Reader getNCharacterStream(final String columnLabel) throws SQLException {
        return getCharacterStream(columnLabel);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public NClob getNClob(final int columnIndex) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public NClob getNClob(final String columnLabel) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getNString(final int columnIndex) throws SQLException {
        return getString(columnIndex);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getNString(final String columnLabel) throws SQLException {
        return getString(columnLabel);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object getObject(final int columnIndex) throws SQLException {
        return dataNavigation.getColumnValue(columnIndex);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public <T> T getObject(final int columnIndex, final Class<T> type) throws SQLException {
        return ValuesConverter.convert(getObject(columnIndex), type, connectionInfo);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object getObject(final int columnIndex, final Map<String, Class<?>> map) throws SQLException {
        return getObject(columnIndex);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object getObject(final String columnLabel) throws SQLException {
        return this.getObject(this.findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public <T> T getObject(final String columnLabel, final Class<T> type) throws SQLException {
        return getObject(this.findColumn(columnLabel), type);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object getObject(final String columnLabel, final Map<String, Class<?>> map) throws SQLException {
        return this.getObject(this.findColumn(columnLabel), map);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Ref getRef(final int columnIndex) throws SQLFeatureNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Ref getRef(final String columnLabel) throws SQLException {
        return getRef(this.findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getRow() throws SQLException {
        return dataNavigation.getRow();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public RowId getRowId(final int columnIndex) throws SQLFeatureNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public RowId getRowId(final String columnLabel) throws SQLException {
        return getRowId(this.findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public short getShort(final int columnIndex) throws SQLException {
        Short ret = ValuesConverter.getShort(dataNavigation.getColumnValue(columnIndex), connectionInfo);
        if (ret != null) {
            return ret;
        }

        return 0;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public short getShort(final String columnLabel) throws SQLException {
        return this.getShort(this.findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public SQLXML getSQLXML(final int columnIndex) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public SQLXML getSQLXML(final String columnLabel) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Statement getStatement() {
        if (statement != null) {
            return this.statement.get();
        }

        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getString(final int columnIndex) throws SQLException {
        return ValuesConverter.getString(dataNavigation.getColumnValue(columnIndex), connectionInfo);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getString(final String columnLabel) throws SQLException {
        return this.getString(this.findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Time getTime(final int columnIndex) throws SQLException {
        return ValuesConverter.getTime(dataNavigation.getColumnValue(columnIndex), connectionInfo);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Time getTime(final int columnIndex, final Calendar cal) throws SQLException {
        return this.getTime(columnIndex);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Time getTime(final String columnLabel) throws SQLException {
        return this.getTime(this.findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Time getTime(final String columnLabel, final Calendar cal) throws SQLException {
        return this.getTime(this.findColumn(columnLabel), cal);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Timestamp getTimestamp(final int columnIndex) throws SQLException {
        return ValuesConverter.getTimestamp(dataNavigation.getColumnValue(columnIndex), connectionInfo);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Timestamp getTimestamp(final int columnIndex, final Calendar cal) throws SQLException {
        return this.getTimestamp(columnIndex);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Timestamp getTimestamp(final String columnLabel) throws SQLException {
        return this.getTimestamp(this.findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Timestamp getTimestamp(final String columnLabel, final Calendar cal) throws SQLException {
        return this.getTimestamp(this.findColumn(columnLabel), cal);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getType() {
        return type;
    }

    /**
     * Sets this type.
     *
     * @param type the ResultSet type.
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * {@inheritDoc}.
     *
     * @deprecated use {@link #getAsciiStream(String)} method.
     */
    @SuppressWarnings("squid:S1133")
    @Deprecated
    @Override
    public InputStream getUnicodeStream(final String columnLabel) throws SQLException {
        return this.getAsciiStream(this.findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public URL getURL(final int columnIndex) throws SQLFeatureNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public URL getURL(final String columnLabel) throws SQLException {
        return getURL(this.findColumn(columnLabel));
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
    public void insertRow() throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean isAfterLast() throws SQLException {
        return dataNavigation.isAfterLast();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean isBeforeFirst() throws SQLException {
        return dataNavigation.isBeforeFirst();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean isClosed() {
        return dataNavigation.isClosed();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean isFirst() throws SQLException {
        return dataNavigation.isFirst();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean isLast() throws SQLException {
        return dataNavigation.isLast();
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
    public boolean last() throws SQLException {
        return dataNavigation.last();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void moveToCurrentRow() {
        // Do nothing.
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void moveToInsertRow() {
        // Do nothing.
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean next() {
        return dataNavigation.next();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean previous() {
        return dataNavigation.previous();
    }

    /**
     * {@inheritDoc}.
     *
     * @deprecated use {@link #getAsciiStream(int)} method.
     */
    @SuppressWarnings("squid:S1133")
    @Deprecated
    @Override
    public InputStream getUnicodeStream(final int columnIndex) throws SQLException {
        return getAsciiStream(columnIndex);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean relative(final int rows) throws SQLException {
        return dataNavigation.relative(rows);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean rowDeleted() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean rowInserted() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean rowUpdated() {
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void refreshRow() {
        // Nothing to do.
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public <T> T unwrap(final Class<T> iFace) throws SQLException {
        return Utils.unwrap(this, iFace);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateArray(final int columnIndex, final Array x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateArray(final String columnLabel, final Array x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateAsciiStream(final int columnIndex, final InputStream x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateAsciiStream(final int columnIndex, final InputStream x, final int length)
            throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateAsciiStream(final int columnIndex, final InputStream x, final long length)
            throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateAsciiStream(final String columnLabel, final InputStream x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateAsciiStream(final String columnLabel, final InputStream x, final int length)
            throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateAsciiStream(final String columnLabel, final InputStream x, final long length)
            throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBigDecimal(final int columnIndex, final BigDecimal x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBigDecimal(final String columnLabel, final BigDecimal x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBinaryStream(final int columnIndex, final InputStream x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBinaryStream(final int columnIndex, final InputStream x, final int length)
            throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBinaryStream(final int columnIndex, final InputStream x, final long length)
            throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBinaryStream(final String columnLabel, final InputStream x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBinaryStream(final String columnLabel, final InputStream x, final int length)
            throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBinaryStream(final String columnLabel, final InputStream x, final long length)
            throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBlob(final int columnIndex, final Blob x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBlob(final int columnIndex, final InputStream inputStream) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBlob(final int columnIndex, final InputStream inputStream, final long length)
            throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBlob(final String columnLabel, final Blob x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBlob(final String columnLabel, final InputStream inputStream)
            throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBlob(final String columnLabel, final InputStream inputStream, final long length)
            throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBoolean(final int columnIndex, final boolean x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBoolean(final String columnLabel, final boolean x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateByte(final int columnIndex, final byte x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateByte(final String columnLabel, final byte x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBytes(final int columnIndex, final byte[] x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBytes(final String columnLabel, final byte[] x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateCharacterStream(final int columnIndex, final Reader x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateCharacterStream(final int columnIndex, final Reader x, final int length)
            throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateCharacterStream(final int columnIndex, final Reader x, final long length)
            throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateCharacterStream(final String columnLabel, final Reader reader)
            throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateCharacterStream(final String columnLabel, final Reader reader, final int length)
            throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateCharacterStream(final String columnLabel, final Reader reader, final long length)
            throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateClob(final int columnIndex, final Clob x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateClob(final int columnIndex, final Reader reader) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateClob(final int columnIndex, final Reader reader, final long length)
            throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateClob(final String columnLabel, final Clob x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateClob(final String columnLabel, final Reader reader) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateClob(final String columnLabel, final Reader reader, final long length)
            throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateDate(final int columnIndex, final Date x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateDate(final String columnLabel, final Date x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateDouble(final int columnIndex, final double x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateDouble(final String columnLabel, final double x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateFloat(final int columnIndex, final float x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateFloat(final String columnLabel, final float x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateInt(final int columnIndex, final int x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateInt(final String columnLabel, final int x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateLong(final int columnIndex, final long x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateLong(final String columnLabel, final long x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateNCharacterStream(final int columnIndex, final Reader x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateNCharacterStream(final int columnIndex, final Reader x, final long length)
            throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateNCharacterStream(final String columnLabel, final Reader reader)
            throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateNCharacterStream(final String columnLabel, final Reader reader, final long length)
            throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateNClob(final int columnIndex, final NClob nClob) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateNClob(final int columnIndex, final Reader reader) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateNClob(final int columnIndex, final Reader reader, final long length)
            throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateNClob(final String columnLabel, final NClob nClob) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateNClob(final String columnLabel, final Reader reader) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateNClob(final String columnLabel, final Reader reader, final long length)
            throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateNString(final int columnIndex, final String nString) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateNString(final String columnLabel, final String nString) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateNull(final int columnIndex) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateNull(final String columnLabel) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateObject(final int columnIndex, final Object x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateObject(final int columnIndex, final Object x, final int scaleOrLength)
            throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateObject(final String columnLabel, final Object x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateObject(final String columnLabel, final Object x, final int scaleOrLength)
            throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateRef(final int columnIndex, final Ref x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateRef(final String columnLabel, final Ref x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateRow() throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateRowId(final int columnIndex, final RowId x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateRowId(final String columnLabel, final RowId x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateShort(final int columnIndex, final short x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateShort(final String columnLabel, final short x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateSQLXML(final int columnIndex, final SQLXML xmlObject) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateSQLXML(final String columnLabel, final SQLXML xmlObject) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateString(final int columnIndex, final String x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateString(final String columnLabel, final String x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateTime(final int columnIndex, final Time x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateTime(final String columnLabel, final Time x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateTimestamp(final int columnIndex, final Timestamp x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateTimestamp(final String columnLabel, final Timestamp x) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean wasNull() throws SQLException {
        return dataNavigation.getLastValue() == null;
    }

    @Override
    public String toString() {
        return "Columns: " + columns.size() + " " + dataNavigation;
    }
}
