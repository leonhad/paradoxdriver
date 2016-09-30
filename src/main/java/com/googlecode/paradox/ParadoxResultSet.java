/*
 * ParadoxResultSet.java
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox;

import com.googlecode.paradox.data.table.value.ClobDescriptor;
import com.googlecode.paradox.data.table.value.FieldValue;
import com.googlecode.paradox.metadata.ParadoxResultSetMetaData;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.rowset.ParadoxClob;
import com.googlecode.paradox.utils.SQLStates;
import com.googlecode.paradox.utils.Utils;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JDBC ResultSet implementation.
 *
 * @author Leonardo Alves da Costa
 * @version 1.1
 * @since 1.0
 */
public final class ParadoxResultSet implements ResultSet {

    /**
     * If this connection is invalid.
     */
    private static final String ERROR_INVALID_COLUMN = "Invalid column.";
    /**
     * {@link ResultSet} columns.
     */
    private final List<Column> columns;
    /**
     * The connection used in this {@link ResultSet}.
     */
    private final ParadoxConnection conn;
    /**
     * This {@link ResultSet} {@link Statement}.
     */
    private final ParadoxStatement statement;
    /**
     * The list of all {@link ResultSet} rows.
     */
    private final List<List<FieldValue>> values;
    /**
     * Clob fields mapping.
     */
    private Map<Integer, Clob> clobMap;
    /**
     * If this {@link ResultSet} is closed.
     */
    private boolean closed;
    /**
     * The amount of rows fetched.
     */
    private int fetchSize = 10;
    /**
     * Last got value.
     */
    private FieldValue lastValue = null;
    /**
     * Row position.
     */
    private int position = -1;

    /**
     * Creates a new {@link ResultSet}.
     *
     * @param conn
     *         the database connection.
     * @param statement
     *         the {@link Statement} for this {@link ResultSet}.
     * @param values
     *         row and column values.
     * @param columns
     *         the columns name.
     */
    public ParadoxResultSet(final ParadoxConnection conn, final ParadoxStatement statement, final
    List<List<FieldValue>> values, final List<Column> columns) {
        this.statement = statement;
        this.values = Collections.unmodifiableList(values);
        this.columns = Collections.unmodifiableList(columns);
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
            if (row + values.size() < 0) {
                return false;
            }
            position = values.size() + row;
        } else {
            if (row > values.size()) {
                return false;
            }
            position = row - 1;
        }
        return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void afterLast() {
        position = values.size();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void beforeFirst() {
        position = -1;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void cancelRowUpdates() {
        throw new UnsupportedOperationException();
    }

    /**
     * Clear the current clob.
     */
    private void clearClob() {
        if (clobMap != null) {
            clobMap.clear();
        }
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
        if (clobMap != null) {
            for (final Clob clob : clobMap.values()) {
                clob.free();
            }
            clearClob();
        }
        closed = true;
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
        for (final Column column : columns) {
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
        if (values.isEmpty()) {
            return false;
        }
        position = 0;
        clearClob();
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
    public InputStream getAsciiStream(final int columnIndex) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public InputStream getAsciiStream(final String columnLabel) throws SQLException {
        return getAsciiStream(findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public BigDecimal getBigDecimal(final int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    /**
     * {@inheritDoc}.
     *
     * @deprecated this method is only used for JDBC compatibility.
     */
    @Deprecated
    @SuppressWarnings("squid:S1133")
    @Override
    public BigDecimal getBigDecimal(final int columnIndex, final int scale) throws SQLException {
        verifyRow();

        final BigDecimal value = getBigDecimal(columnIndex);
        return value.setScale(scale, RoundingMode.HALF_DOWN);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public BigDecimal getBigDecimal(final String columnLabel) throws SQLException {
        return getBigDecimal(findColumn(columnLabel));
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
        return getBigDecimal(findColumn(columnLabel), scale);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public InputStream getBinaryStream(final int columnIndex) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public InputStream getBinaryStream(final String columnLabel) throws SQLException {
        return getBinaryStream(findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Blob getBlob(final int columnIndex) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Blob getBlob(final String columnLabel) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean getBoolean(final int columnIndex) throws SQLException {
        verifyRow();

        final List<FieldValue> row = values.get(position);
        if (columnIndex > row.size()) {
            throw new SQLException(ERROR_INVALID_COLUMN, SQLStates.INVALID_COLUMN.getValue());
        }
        lastValue = row.get(columnIndex - 1);
        if (!lastValue.isNull()) {
            return lastValue.getBoolean();
        }
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean getBoolean(final String columnLabel) throws SQLException {
        return getBoolean(findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public byte getByte(final int columnIndex) throws SQLException {
        verifyRow();

        final List<FieldValue> row = values.get(position);
        if (columnIndex > row.size()) {
            throw new SQLException(ERROR_INVALID_COLUMN, SQLStates.INVALID_COLUMN.getValue());
        }
        lastValue = row.get(columnIndex - 1);
        return lastValue.isNull() ? (byte) 0 : lastValue.getNumber().byteValue();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public byte getByte(final String columnLabel) throws SQLException {
        return getByte(findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public byte[] getBytes(final int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public byte[] getBytes(final String columnLabel) throws SQLException {
        return getBytes(findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Reader getCharacterStream(final int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Reader getCharacterStream(final String columnLabel) throws SQLException {
        return getCharacterStream(findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Clob getClob(final int columnIndex) throws SQLException {
        if (clobMap == null) {
            clobMap = new HashMap<>(1);
        }
        if (clobMap.containsKey(columnIndex)) {
            return clobMap.get(columnIndex);
        }
        final Object val = this.getObject(columnIndex);
        if (val != null) {
            if (val instanceof ClobDescriptor) {
                final ParadoxClob clob = new ParadoxClob((ClobDescriptor) val);
                clobMap.put(columnIndex, clob);
                return clob;
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
        return getClob(findColumn(columnLabel));
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
        return statement != null ? statement.cursorName : "NO_NAME";
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Date getDate(final int columnIndex) throws SQLException {
        verifyRow();

        final List<FieldValue> row = values.get(position);
        if (columnIndex > row.size()) {
            throw new SQLException(ERROR_INVALID_COLUMN, SQLStates.INVALID_COLUMN.getValue());
        }
        lastValue = row.get(columnIndex - 1);
        return lastValue.getDate();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Date getDate(final int columnIndex, final Calendar cal) throws SQLException {
        return getDate(columnIndex);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Date getDate(final String columnLabel) throws SQLException {
        return getDate(findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Date getDate(final String columnLabel, final Calendar cal) throws SQLException {
        return getDate(columnLabel);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public double getDouble(final int columnIndex) throws SQLException {
        verifyRow();

        final List<FieldValue> row = values.get(position);
        if (columnIndex > row.size()) {
            throw new SQLException(ERROR_INVALID_COLUMN, SQLStates.INVALID_COLUMN.getValue());
        }
        lastValue = row.get(columnIndex - 1);
        return lastValue.isNull() ? 0d : lastValue.getNumber().doubleValue();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public double getDouble(final String columnLabel) throws SQLException {
        return getDouble(findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getFetchDirection() throws SQLException {
        throw new SQLException("No fetch direction");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getFetchSize() {
        return fetchSize;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public float getFloat(final int columnIndex) throws SQLException {
        verifyRow();

        final List<FieldValue> row = values.get(position);
        if (columnIndex > row.size()) {
            throw new SQLException(ERROR_INVALID_COLUMN, SQLStates.INVALID_COLUMN.getValue());
        }
        lastValue = row.get(columnIndex - 1);
        return lastValue.isNull() ? 0f : lastValue.getNumber().floatValue();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public float getFloat(final String columnLabel) throws SQLException {
        return getFloat(findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getHoldability() {
        return conn.getHoldability();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getInt(final int columnIndex) throws SQLException {
        verifyRow();

        final List<FieldValue> row = values.get(position);
        if (columnIndex > row.size()) {
            throw new SQLException(ERROR_INVALID_COLUMN, SQLStates.INVALID_COLUMN.getValue());
        }
        lastValue = row.get(columnIndex - 1);
        return lastValue.isNull() ? 0 : lastValue.getNumber().intValue();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getInt(final String columnLabel) throws SQLException {
        return getInt(findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public long getLong(final int columnIndex) throws SQLException {
        verifyRow();

        final List<FieldValue> row = values.get(position);
        if (columnIndex > row.size()) {
            throw new SQLException(ERROR_INVALID_COLUMN, SQLStates.INVALID_COLUMN.getValue());
        }
        lastValue = row.get(columnIndex - 1);
        return lastValue.isNull() ? 0L : lastValue.getNumber().longValue();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public long getLong(final String columnLabel) throws SQLException {
        return getLong(findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ResultSetMetaData getMetaData() {
        return new ParadoxResultSetMetaData(conn, columns);
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
        verifyRow();

        final List<FieldValue> row = values.get(position);
        if (columnIndex > row.size()) {
            throw new SQLException(ERROR_INVALID_COLUMN, SQLStates.INVALID_COLUMN.getValue());
        }
        lastValue = row.get(columnIndex - 1);
        return lastValue.getValue();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public <T> T getObject(final int columnIndex, final Class<T> type) {
        throw new UnsupportedOperationException("Not supported yet.");
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
        return getObject(findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public <T> T getObject(final String columnLabel, final Class<T> type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object getObject(final String columnLabel, final Map<String, Class<?>> map) throws SQLException {
        return getObject(columnLabel);
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
        return position + 1;
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
        verifyRow();

        final List<FieldValue> row = values.get(position);
        if (columnIndex > row.size()) {
            throw new SQLException(ERROR_INVALID_COLUMN, SQLStates.INVALID_COLUMN.getValue());
        }
        lastValue = row.get(columnIndex - 1);
        return lastValue.isNull() ? (short) 0 : lastValue.getNumber().shortValue();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public short getShort(final String columnLabel) throws SQLException {
        return getShort(findColumn(columnLabel));
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
        return statement;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getString(final int columnIndex) throws SQLException {
        verifyRow();

        final List<FieldValue> row = values.get(position);
        if (columnIndex > row.size()) {
            throw new SQLException(ERROR_INVALID_COLUMN, SQLStates.INVALID_COLUMN.getValue());
        }
        lastValue = row.get(columnIndex - 1);
        if (lastValue != null && lastValue.getValue() != null) {
            return lastValue.getValue().toString();
        }

        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getString(final String columnLabel) throws SQLException {
        return getString(findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Time getTime(final int columnIndex) throws SQLException {
        verifyRow();

        final List<FieldValue> row = values.get(position);
        if (columnIndex > row.size()) {
            throw new SQLException(ERROR_INVALID_COLUMN, SQLStates.INVALID_COLUMN.getValue());
        }
        lastValue = row.get(columnIndex - 1);
        return lastValue.getTime();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Time getTime(final int columnIndex, final Calendar cal) throws SQLException {
        return getTime(columnIndex);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Time getTime(final String columnLabel) throws SQLException {
        return getTime(findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Time getTime(final String columnLabel, final Calendar cal) throws SQLException {
        return getTime(columnLabel);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Timestamp getTimestamp(final int columnIndex) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Timestamp getTimestamp(final int columnIndex, final Calendar cal) throws SQLException {
        return getTimestamp(columnIndex);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Timestamp getTimestamp(final String columnLabel) throws SQLException {
        return getTimestamp(findColumn(columnLabel));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Timestamp getTimestamp(final String columnLabel, final Calendar cal) throws SQLException {
        return getTimestamp(columnLabel);
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
     *
     * @deprecated this method is only used for JDBC compatibility.
     */
    @Deprecated
    @SuppressWarnings("squid:S1133")
    @Override
    public InputStream getUnicodeStream(final int columnIndex) {
        return null;
    }

    /**
     * {@inheritDoc}.
     *
     * @deprecated this method is only used for JDBC compatibility.
     */
    @Deprecated
    @SuppressWarnings("squid:S1133")
    @Override
    public InputStream getUnicodeStream(final String columnLabel) throws SQLException {
        return getUnicodeStream(findColumn(columnLabel));
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
     * Gets the all row values.
     *
     * @return the row values.
     */
    public List<List<FieldValue>> getValues() {
        return values;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public SQLWarning getWarnings() {
        return null;
    }

    private boolean hasNext() {
        return values != null && position < values.size();
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
        return position >= values.size();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean isBeforeFirst() {
        return position == -1;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean isClosed() {
        return closed;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean isFirst() {
        return position + 1 == 0;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean isLast() {
        return position + 1 == values.size();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean isWrapperFor(final Class<?> iFace) throws SQLException {
        return Utils.isWrapperFor(this, iFace);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean last() {
        if (values.isEmpty()) {
            return false;
        }
        position = values.size() - 1;
        clearClob();
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
        position++;
        if (hasNext()) {
            clearClob();
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean previous() {
        if (position > -1) {
            position--;
            clearClob();
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void refreshRow() {
        throw new UnsupportedOperationException();
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
    public void setFetchDirection(final int direction) throws SQLException {
        throw new SQLException("No fetch direction");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setFetchSize(final int rows) {
        fetchSize = rows;
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
    public void updateBlob(final int columnIndex, final InputStream inputStream) throws SQLException {
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

    private void verifyRow() throws SQLException {
        if (!hasNext()) {
            throw new SQLDataException("Result do not have more rows.", SQLStates.INVALID_ROW.getValue());
        } else if (closed) {
            throw new SQLException("Closed result set.", SQLStates.RESULTSET_CLOSED.getValue());
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean wasNull() throws SQLException {
        if (closed) {
            throw new SQLException("Closed result set.", SQLStates.RESULTSET_CLOSED.getValue());
        }
        return lastValue.isNull();
    }
}
