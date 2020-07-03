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

import com.googlecode.paradox.metadata.ParadoxResultSetMetaData;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.rowset.DataNavigation;
import com.googlecode.paradox.rowset.ParadoxBlob;
import com.googlecode.paradox.rowset.ParadoxClob;
import com.googlecode.paradox.rowset.ValuesConverter;
import com.googlecode.paradox.utils.SQLStates;
import com.googlecode.paradox.utils.Utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
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
 * @version 1.3
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
     * The connection used in this {@link ResultSet}.
     */
    private final ParadoxConnection conn;
    /**
     * The amount of rows fetched.
     */
    private int fetchSize = ParadoxResultSet.FETCH_SIZE;
    /**
     * This {@link ResultSet} {@link Statement}.
     */
    private final ParadoxStatement statement;

    private final DataNavigation dataNavigation;

    /**
     * Creates a new {@link ResultSet}.
     *
     * @param conn      the database connection.
     * @param statement the {@link Statement} for this {@link ResultSet}.
     * @param values    row and column values.
     * @param columns   the columns name.
     */
    public ParadoxResultSet(final ParadoxConnection conn, final ParadoxStatement statement,
                            final List<Object[]> values, final List<Column> columns) {
        this.statement = statement;
        this.columns = columns;
        this.conn = conn;
        this.dataNavigation = new DataNavigation(values);

        // Fill column indexes
        for (int loop = 0; loop < columns.size(); loop++) {
            if (columns.get(loop) != null) {
                columns.get(loop).setIndex(loop);
            }
        }
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
    public void cancelRowUpdates() {
        throw new UnsupportedOperationException();
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
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void deleteRow() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int findColumn(final String columnLabel) throws SQLException {
        for (final Column column : this.columns) {
            if (column.getName().equalsIgnoreCase(columnLabel)) {
                return column.getIndex() + 1;
            }
        }

        throw new SQLException("Invalid column: " + columnLabel, SQLStates.INVALID_COLUMN.getValue());
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
        final String val = ValuesConverter.getString(dataNavigation.getColumnValue(columnIndex));
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
        return ValuesConverter.getBigDecimal(dataNavigation.getColumnValue(columnIndex));
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
        final byte[] val = ValuesConverter.getByteArray(dataNavigation.getColumnValue(columnIndex));
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
        final byte[] val = ValuesConverter.getByteArray(dataNavigation.getColumnValue(columnIndex));
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
        return ValuesConverter.getBoolean(dataNavigation.getColumnValue(columnIndex));
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
        return ValuesConverter.getByte(dataNavigation.getColumnValue(columnIndex));
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
        return ValuesConverter.getByteArray(dataNavigation.getColumnValue(columnIndex));
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
        final String val = ValuesConverter.getString(dataNavigation.getColumnValue(columnIndex));
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
        final String val = ValuesConverter.getString(dataNavigation.getColumnValue(columnIndex));
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
        return ResultSet.CONCUR_READ_ONLY;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getCursorName() {
        if (this.statement != null) {
            return this.statement.getCursorName();
        }

        return "NO_NAME";
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Date getDate(final int columnIndex) throws SQLException {
        return ValuesConverter.getDate(dataNavigation.getColumnValue(columnIndex));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Date getDate(final int columnIndex, final Calendar cal) throws SQLException {
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
        return this.getDate(columnLabel);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public double getDouble(final int columnIndex) throws SQLException {
        return ValuesConverter.getDouble(dataNavigation.getColumnValue(columnIndex));
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
    public int getFetchSize() {
        return this.fetchSize;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public float getFloat(final int columnIndex) throws SQLException {
        return ValuesConverter.getFloat(dataNavigation.getColumnValue(columnIndex));
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
        return this.conn.getHoldability();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getInt(final int columnIndex) throws SQLException {
        return ValuesConverter.getInteger(dataNavigation.getColumnValue(columnIndex));
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
        return ValuesConverter.getLong(dataNavigation.getColumnValue(columnIndex));
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
    public ResultSetMetaData getMetaData() {
        return new ParadoxResultSetMetaData(this.conn, this.columns);
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
    @SuppressWarnings("unchecked")
    public <T> T getObject(final int columnIndex, final Class<T> type) throws SQLException {
        return ValuesConverter.convert(getObject(columnIndex), type);
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
    @SuppressWarnings("unchecked")
    public <T> T getObject(final String columnLabel, final Class<T> type) throws SQLException {
        try {
            return getObject(this.findColumn(columnLabel), type);
        } catch (final IllegalArgumentException e) {
            throw new SQLException("Error in value conversion to " + type, e);
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object getObject(final String columnLabel, final Map<String, Class<?>> map) throws SQLException {
        return this.getObject(columnLabel, map);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Ref getRef(final int columnIndex) {
        // FIXME review this method.
        return null;
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
    public RowId getRowId(final int columnIndex) {
        // FIXME review this method.
        return null;
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
        return ValuesConverter.getShort(dataNavigation.getColumnValue(columnIndex));
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
        return this.statement;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getString(final int columnIndex) throws SQLException {
        return ValuesConverter.getString(dataNavigation.getColumnValue(columnIndex));
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
        return ValuesConverter.getTime(dataNavigation.getColumnValue(columnIndex));
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
        return this.getTime(columnLabel);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Timestamp getTimestamp(final int columnIndex) throws SQLException {
        return ValuesConverter.getTimestamp(dataNavigation.getColumnValue(columnIndex));
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
        return this.getTimestamp(columnLabel);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getType() {
        return ResultSet.TYPE_SCROLL_INSENSITIVE;
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
    public URL getURL(final int columnIndex) {
        // FIXME review this method.
        return null;
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
    public void insertRow() {
        throw new UnsupportedOperationException();
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
    public boolean next() throws SQLException {
        return dataNavigation.next();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean previous() throws SQLException {
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
    public void setFetchSize(final int rows) {
        this.fetchSize = rows;
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
    public void updateArray(final int columnIndex, final Array x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateArray(final String columnLabel, final Array x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateAsciiStream(final int columnIndex, final InputStream x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateAsciiStream(final int columnIndex, final InputStream x, final int length) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateAsciiStream(final int columnIndex, final InputStream x, final long length) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateAsciiStream(final String columnLabel, final InputStream x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateAsciiStream(final String columnLabel, final InputStream x, final int length) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateAsciiStream(final String columnLabel, final InputStream x, final long length) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBigDecimal(final int columnIndex, final BigDecimal x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBigDecimal(final String columnLabel, final BigDecimal x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBinaryStream(final int columnIndex, final InputStream x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBinaryStream(final int columnIndex, final InputStream x, final int length) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBinaryStream(final int columnIndex, final InputStream x, final long length) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBinaryStream(final String columnLabel, final InputStream x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBinaryStream(final String columnLabel, final InputStream x, final int length) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBinaryStream(final String columnLabel, final InputStream x, final long length) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBlob(final int columnIndex, final Blob x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBlob(final int columnIndex, final InputStream inputStream) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBlob(final int columnIndex, final InputStream inputStream, final long length) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBlob(final String columnLabel, final Blob x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBlob(final String columnLabel, final InputStream inputStream) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBlob(final String columnLabel, final InputStream inputStream, final long length) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBoolean(final int columnIndex, final boolean x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBoolean(final String columnLabel, final boolean x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateByte(final int columnIndex, final byte x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateByte(final String columnLabel, final byte x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBytes(final int columnIndex, final byte[] x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateBytes(final String columnLabel, final byte[] x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateCharacterStream(final int columnIndex, final Reader x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateCharacterStream(final int columnIndex, final Reader x, final int length) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateCharacterStream(final int columnIndex, final Reader x, final long length) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateCharacterStream(final String columnLabel, final Reader reader) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateCharacterStream(final String columnLabel, final Reader reader, final int length) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateCharacterStream(final String columnLabel, final Reader reader, final long length) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateClob(final int columnIndex, final Clob x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateClob(final int columnIndex, final Reader reader) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateClob(final int columnIndex, final Reader reader, final long length) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateClob(final String columnLabel, final Clob x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateClob(final String columnLabel, final Reader reader) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateClob(final String columnLabel, final Reader reader, final long length) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateDate(final int columnIndex, final Date x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateDate(final String columnLabel, final Date x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateDouble(final int columnIndex, final double x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateDouble(final String columnLabel, final double x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateFloat(final int columnIndex, final float x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateFloat(final String columnLabel, final float x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateInt(final int columnIndex, final int x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateInt(final String columnLabel, final int x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateLong(final int columnIndex, final long x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateLong(final String columnLabel, final long x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateNCharacterStream(final int columnIndex, final Reader x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateNCharacterStream(final int columnIndex, final Reader x, final long length) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateNCharacterStream(final String columnLabel, final Reader reader) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateNCharacterStream(final String columnLabel, final Reader reader, final long length) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateNClob(final int columnIndex, final NClob nClob) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateNClob(final int columnIndex, final Reader reader) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateNClob(final int columnIndex, final Reader reader, final long length) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateNClob(final String columnLabel, final NClob nClob) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateNClob(final String columnLabel, final Reader reader) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateNClob(final String columnLabel, final Reader reader, final long length) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateNString(final int columnIndex, final String nString) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateNString(final String columnLabel, final String nString) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateNull(final int columnIndex) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateNull(final String columnLabel) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateObject(final int columnIndex, final Object x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateObject(final int columnIndex, final Object x, final int scaleOrLength) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateObject(final String columnLabel, final Object x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateObject(final String columnLabel, final Object x, final int scaleOrLength) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateRef(final int columnIndex, final Ref x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateRef(final String columnLabel, final Ref x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateRow() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateRowId(final int columnIndex, final RowId x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateRowId(final String columnLabel, final RowId x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateShort(final int columnIndex, final short x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateShort(final String columnLabel, final short x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateSQLXML(final int columnIndex, final SQLXML xmlObject) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateSQLXML(final String columnLabel, final SQLXML xmlObject) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateString(final int columnIndex, final String x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateString(final String columnLabel, final String x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateTime(final int columnIndex, final Time x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateTime(final String columnLabel, final Time x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateTimestamp(final int columnIndex, final Timestamp x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateTimestamp(final String columnLabel, final Timestamp x) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean wasNull() throws SQLException {
        return dataNavigation.getLastValue() == null;
    }
}
