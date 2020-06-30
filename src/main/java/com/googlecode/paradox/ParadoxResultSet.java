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
import com.googlecode.paradox.rowset.ParadoxBlob;
import com.googlecode.paradox.rowset.ParadoxClob;
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
 * @author Leonardo Alves da Costa
 * @version 1.2
 * @since 1.0
 */
public final class ParadoxResultSet implements ResultSet {

    /**
     * If this connection is invalid.
     */
    private static final String ERROR_INVALID_COLUMN = "Invalid column.";
    /**
     * Default fetch size.
     */
    private static final int FETCH_SIZE = 10;
    /**
     * If this {@link ResultSet} is closed.
     */
    private boolean closed;
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
     * Last got value.
     */
    private Object lastValue;
    /**
     * Row position.
     */
    private int position = -1;
    /**
     * This {@link ResultSet} {@link Statement}.
     */
    private final ParadoxStatement statement;
    /**
     * The list of all {@link ResultSet} rows.
     */
    private final List<Object[]> values;

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
        this.values = values;
        this.columns = columns;
        this.conn = conn;

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
    public boolean absolute(final int row) {
        if (row < 0) {
            if ((row + this.values.size()) < 0) {
                return false;
            }
            this.position = this.values.size() + row;
        } else {
            if (row > this.values.size()) {
                return false;
            }
            this.position = row - 1;
        }
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void afterLast() {
        this.position = this.values.size();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void beforeFirst() {
        this.position = -1;
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
        this.closed = true;
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
    public boolean first() {
        if (this.values.isEmpty()) {
            return false;
        }
        this.position = 0;
        return true;
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
        final Object val = this.getObject(columnIndex);
        if (val != null) {
            if (val instanceof String) {
                new ByteArrayInputStream(((String) val).getBytes(StandardCharsets.UTF_8));
            } else {
                throw new SQLException("Filed isn't clob type", SQLStates.INVALID_FIELD_VALUE.getValue());
            }
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
        return BigDecimal.valueOf(getDouble(columnIndex));
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
        this.verifyRow();

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
        final Object val = this.getObject(columnIndex);
        if (val != null) {
            if (val instanceof byte[]) {
                return new ByteArrayInputStream((byte[]) val);
            } else {
                throw new SQLException("Filed is not a blob type", SQLStates.INVALID_FIELD_VALUE.getValue());
            }
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
        final Object val = this.getObject(columnIndex);
        if (val != null) {
            if (val instanceof byte[]) {
                return new ParadoxBlob((byte[]) val);
            } else {
                throw new SQLException("Filed is not a clob type", SQLStates.INVALID_FIELD_VALUE.getValue());
            }
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
        this.verifyRow();

        final Object[] row = this.values.get(this.position);
        if (columnIndex > row.length) {
            throw new SQLException(ParadoxResultSet.ERROR_INVALID_COLUMN, SQLStates.INVALID_COLUMN.getValue());
        }
        this.lastValue = row[columnIndex - 1];
        if (this.lastValue != null) {
            return (Boolean) this.lastValue;
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
        this.verifyRow();

        final Object[] row = this.values.get(this.position);
        if (columnIndex > row.length) {
            throw new SQLException(ParadoxResultSet.ERROR_INVALID_COLUMN, SQLStates.INVALID_COLUMN.getValue());
        }
        this.lastValue = row[columnIndex - 1];
        if (this.lastValue == null) {
            return (byte) 0;
        }
        return ((Number) this.lastValue).byteValue();
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
        final Object val = this.getObject(columnIndex);
        if (val != null) {
            if (val instanceof byte[]) {
                return (byte[]) val;
            } else {
                throw new SQLException("Filed is not a byte array.", SQLStates.INVALID_FIELD_VALUE.getValue());
            }
        }
        return null;
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
        final Object val = this.getObject(columnIndex);
        if (val != null) {
            return new StringReader(val.toString());
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
        final Object val = this.getObject(columnIndex);
        if (val != null) {
            if (val instanceof String) {
                return new ParadoxClob((String) val);
            } else {
                throw new SQLException("Filed isn't clob type", SQLStates.INVALID_FIELD_VALUE.getValue());
            }
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
        this.verifyRow();

        final Object[] row = this.values.get(this.position);
        if (columnIndex > row.length) {
            throw new SQLException(ParadoxResultSet.ERROR_INVALID_COLUMN, SQLStates.INVALID_COLUMN.getValue());
        }
        this.lastValue = row[columnIndex - 1];
        return (Date) this.lastValue;
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
        this.verifyRow();

        final Object[] row = this.values.get(this.position);
        if (columnIndex > row.length) {
            throw new SQLException(ParadoxResultSet.ERROR_INVALID_COLUMN, SQLStates.INVALID_COLUMN.getValue());
        }
        this.lastValue = row[columnIndex - 1];
        if (this.lastValue == null) {
            return 0D;
        }
        return ((Number) this.lastValue).doubleValue();
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
        return ResultSet.FETCH_FORWARD;
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
        this.verifyRow();

        final Object[] row = this.values.get(this.position);
        if (columnIndex > row.length) {
            throw new SQLException(ParadoxResultSet.ERROR_INVALID_COLUMN, SQLStates.INVALID_COLUMN.getValue());
        }
        this.lastValue = row[columnIndex - 1];
        if (this.lastValue == null) {
            return 0F;
        }
        return ((Number) this.lastValue).floatValue();
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
        this.verifyRow();

        final Object[] row = this.values.get(this.position);
        if (columnIndex > row.length) {
            throw new SQLException(ParadoxResultSet.ERROR_INVALID_COLUMN, SQLStates.INVALID_COLUMN.getValue());
        }
        this.lastValue = row[columnIndex - 1];
        if (this.lastValue == null) {
            return 0;
        }
        return ((Number) this.lastValue).intValue();
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
        this.verifyRow();

        final Object[] row = this.values.get(this.position);
        if (columnIndex > row.length) {
            throw new SQLException(ParadoxResultSet.ERROR_INVALID_COLUMN, SQLStates.INVALID_COLUMN.getValue());
        }
        this.lastValue = row[columnIndex - 1];
        if (this.lastValue == null) {
            return 0L;
        }
        return ((Number) this.lastValue).longValue();
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
    public Reader getNCharacterStream(final int columnIndex) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Reader getNCharacterStream(final String columnLabel) {
        return null;
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
    public String getNString(final int columnIndex) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getNString(final String columnLabel) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object getObject(final int columnIndex) throws SQLException {
        this.verifyRow();

        final Object[] row = this.values.get(this.position);
        if (columnIndex > row.length) {
            throw new SQLException(ParadoxResultSet.ERROR_INVALID_COLUMN, SQLStates.INVALID_COLUMN.getValue());
        }
        this.lastValue = row[columnIndex - 1];
        return this.lastValue;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getObject(final int columnIndex, final Class<T> type) throws SQLException {
        return (T) getObject(columnIndex);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object getObject(final int columnIndex, final Map<String, Class<?>> map) throws SQLException {
        return this.getObject(columnIndex);
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
        return (T) getObject(columnLabel);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object getObject(final String columnLabel, final Map<String, Class<?>> map) throws SQLException {
        return this.getObject(columnLabel);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Ref getRef(final int columnIndex) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Ref getRef(final String columnLabel) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getRow() {
        return this.position + 1;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public RowId getRowId(final int columnIndex) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public RowId getRowId(final String columnLabel) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public short getShort(final int columnIndex) throws SQLException {
        this.verifyRow();

        final Object[] row = this.values.get(this.position);
        if (columnIndex > row.length) {
            throw new SQLException(ParadoxResultSet.ERROR_INVALID_COLUMN, SQLStates.INVALID_COLUMN.getValue());
        }
        this.lastValue = row[columnIndex - 1];
        if (this.lastValue == null) {
            return (short) 0;
        }
        return ((Number) this.lastValue).shortValue();
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
        this.verifyRow();

        final Object[] row = this.values.get(this.position);
        if (columnIndex > row.length) {
            throw new SQLException(ParadoxResultSet.ERROR_INVALID_COLUMN, SQLStates.INVALID_COLUMN.getValue());
        }
        this.lastValue = row[columnIndex - 1];
        if (this.lastValue != null) {
            return this.lastValue.toString();
        }

        return null;
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
        this.verifyRow();

        final Object[] row = this.values.get(this.position);
        if (columnIndex > row.length) {
            throw new SQLException(ParadoxResultSet.ERROR_INVALID_COLUMN, SQLStates.INVALID_COLUMN.getValue());
        }
        this.lastValue = row[columnIndex - 1];
        return (Time) this.lastValue;
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
        this.verifyRow();

        final Object[] row = this.values.get(this.position);
        if (columnIndex > row.length) {
            throw new SQLException(ParadoxResultSet.ERROR_INVALID_COLUMN, SQLStates.INVALID_COLUMN.getValue());
        }
        this.lastValue = row[columnIndex - 1];
        return (Timestamp) this.lastValue;
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
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setFetchDirection(final int direction) throws SQLException {
        throw new SQLException("No fetch direction supported yet.");
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
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public URL getURL(final String columnLabel) {
        return null;
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
    public boolean isAfterLast() {
        return this.position >= this.values.size();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean isBeforeFirst() {
        return this.position == -1;
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
    public boolean isFirst() {
        return (this.position + 1) == 0;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean isLast() {
        return (this.position + 1) == this.values.size();
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
    public boolean last() {
        if (this.values.isEmpty()) {
            return false;
        }
        this.position = this.values.size() - 1;
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void moveToCurrentRow() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void moveToInsertRow() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean next() {
        this.position++;
        return this.hasNext();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean previous() {
        if (this.position > -1) {
            this.position--;
            return true;
        }
        return false;
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
    public boolean relative(final int rows) {
        return false;
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
        if (this.closed) {
            throw new SQLException("Closed result set.", SQLStates.RESULTSET_CLOSED.getValue());
        }
        return this.lastValue == null;
    }

    private boolean hasNext() {
        return (this.values != null) && (this.position < this.values.size());
    }

    /**
     * Verify it there is more rows.
     *
     * @throws SQLException in case of errors.
     */
    private void verifyRow() throws SQLException {
        if (!this.hasNext()) {
            throw new SQLDataException("Result do not have more rows.", SQLStates.INVALID_ROW.getValue());
        } else if (this.closed) {
            throw new SQLException("Closed result set.", SQLStates.RESULTSET_CLOSED.getValue());
        }
    }
}
