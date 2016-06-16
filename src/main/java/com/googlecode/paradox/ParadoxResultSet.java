package com.googlecode.paradox;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
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
import java.util.*;

import com.googlecode.paradox.data.table.value.ClobDescriptor;
import com.googlecode.paradox.data.table.value.FieldValue;
import com.googlecode.paradox.metadata.ParadoxResultSetMetaData;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.rowset.ParadoxClob;
import com.googlecode.paradox.utils.SQLStates;

/**
 * 
 * @author Leonardo Alves da Costa
 * @since 14/03/2009
 * @version 1.1
 */
public class ParadoxResultSet implements ResultSet {

	private final ParadoxStatement statement;
	private boolean closed = false;
	private SQLWarning warnings = null;
	private final List<List<FieldValue>> values;
	private int position = -1;
	private final List<Column> columns;
	private int fetchSize = 10;
	private final ParadoxConnection conn;
	private FieldValue lastValue = null;
    private Map<Integer, Clob> clobs;

	public ParadoxResultSet(final ParadoxConnection conn, final ParadoxStatement statement,
							final List<List<FieldValue>> values,
							final List<Column> columns) {
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

	private void verifyRow() throws SQLException {
		if (!hasNext()) {
			throw new SQLDataException("Result do not have more rows.", SQLStates.INVALID_ROW);
		} else if (closed) {
			throw new SQLException("Closed result set.", SQLStates.RESULTSET_CLOSED);
		}
	}

	private boolean hasNext() {
		if (values == null) return false;
		return position < values.size();
	}
	
	private void clearClobs() {
		if (clobs != null) {
			clobs.clear();
		}
	}

	@Override
	public boolean next() throws SQLException {
		if (hasNext()) {
			position++;
			clearClobs();
			return true;
		}
		return false;
	}

	@Override
	public void close() throws SQLException {
		if (columns != null) columns.clear();
		if (values != null) values.clear();
        if (clobs != null) {
            for(Clob clob: clobs.values()) {
                clob.free();
            }
            clearClobs();
        }
		closed = true;
	}

	@Override
	public boolean wasNull() throws SQLException {
		if (closed) {
			throw new SQLException("Closed result set.", SQLStates.RESULTSET_CLOSED);
		}
		return lastValue.isNull();
	}

	@Override
	public String getString(final int columnIndex) throws SQLException {
		verifyRow();

		final List<FieldValue> row = values.get(position);
		if (columnIndex > row.size()) {
			throw new SQLException("Invalid column.", SQLStates.INVALID_COLUMN);
		}
		lastValue = row.get(columnIndex-1);
		if (lastValue != null && lastValue.getValue() != null) {
			return lastValue.getValue().toString();
		}

		return null;
	}

	@Override
	public boolean getBoolean(final int columnIndex) throws SQLException {
		verifyRow();

		final List<FieldValue> row = values.get(position);
		if (columnIndex > row.size()) {
			throw new SQLException("Invalid column.", SQLStates.INVALID_COLUMN);
		}
		lastValue = row.get(columnIndex - 1);
		return lastValue.isNull() ? false : lastValue.getBoolean().booleanValue();
	}

	@Override
	public byte getByte(final int columnIndex) throws SQLException {
		verifyRow();

		final List<FieldValue> row = values.get(position);
		if (columnIndex > row.size()) {
			throw new SQLException("Invalid column.", SQLStates.INVALID_COLUMN);
		}
		lastValue = row.get(columnIndex - 1);
		return lastValue.isNull() ? (byte) 0 : lastValue.getNumber().byteValue();
	}

	@Override
	public short getShort(final int columnIndex) throws SQLException {
		verifyRow();

		final List<FieldValue> row = values.get(position);
		if (columnIndex > row.size()) {
			throw new SQLException("Invalid column.", SQLStates.INVALID_COLUMN);
		}
		lastValue = row.get(columnIndex - 1);
		return lastValue.isNull() ? (short) 0 : lastValue.getNumber().shortValue();
	}

	@Override
	public int getInt(final int columnIndex) throws SQLException {
		verifyRow();

		final List<FieldValue> row = values.get(position);
		if (columnIndex > row.size()) {
			throw new SQLException("Invalid column.", SQLStates.INVALID_COLUMN);
		}
		lastValue = row.get(columnIndex - 1);
		return lastValue.isNull() ? 0 : lastValue.getNumber().intValue();
	}

	@Override
	public long getLong(final int columnIndex) throws SQLException {
		verifyRow();

		final List<FieldValue> row = values.get(position);
		if (columnIndex > row.size()) {
			throw new SQLException("Invalid column.", SQLStates.INVALID_COLUMN);
		}
		lastValue = row.get(columnIndex - 1);
		return lastValue.isNull() ? 0l : lastValue.getNumber().longValue();
	}

	@Override
	public float getFloat(final int columnIndex) throws SQLException {
		verifyRow();

		final List<FieldValue> row = values.get(position);
		if (columnIndex > row.size()) {
			throw new SQLException("Invalid column.", SQLStates.INVALID_COLUMN);
		}
		lastValue = row.get(columnIndex - 1);
		return lastValue.isNull() ? 0f : lastValue.getNumber().floatValue();
	}

	@Override
	public double getDouble(final int columnIndex) throws SQLException {
		verifyRow();

		final List<FieldValue> row = values.get(position);
		if (columnIndex > row.size()) {
			throw new SQLException("Invalid column.", SQLStates.INVALID_COLUMN);
		}
		lastValue = row.get(columnIndex - 1);
		return lastValue.isNull() ? 0d : lastValue.getNumber().doubleValue();
	}

	@Deprecated
	@Override
	public BigDecimal getBigDecimal(final int columnIndex, final int scale) throws SQLException {
		verifyRow();

		final BigDecimal value = getBigDecimal(columnIndex);
		if (value != null) {
			return value.setScale(scale);
		}
		return null;
	}

	@Override
	public byte[] getBytes(final int columnIndex) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public Date getDate(final int columnIndex) throws SQLException {
		verifyRow();

		final List<FieldValue> row = values.get(position);
		if (columnIndex > row.size()) {
			throw new SQLException("Invalid column.", SQLStates.INVALID_COLUMN);
		}
		lastValue = row.get(columnIndex - 1);
		return lastValue.getDate();
	}

	@Override
	public Time getTime(final int columnIndex) throws SQLException {
		verifyRow();

		final List<FieldValue> row = values.get(position);
		if (columnIndex > row.size()) {
			throw new SQLException("Invalid column.", SQLStates.INVALID_COLUMN);
		}
		lastValue = row.get(columnIndex - 1);
		return lastValue.getTime();
	}

	@Override
	public Timestamp getTimestamp(final int columnIndex) throws SQLException {
		return null;
	}

	@Override
	public InputStream getAsciiStream(final int columnIndex) throws SQLException {
		return null;
	}

	@Deprecated
	@Override
	public InputStream getUnicodeStream(final int columnIndex) throws SQLException {
		return null;
	}

	@Override
	public InputStream getBinaryStream(final int columnIndex) throws SQLException {
		return null;
	}

	@Override
	public int findColumn(final String columnLabel) throws SQLException {
		for (final Column column : columns) {
			if (column.getName().equalsIgnoreCase(columnLabel)) {
				return column.getIndex() + 1;
			}
		}
		throw new SQLException("Invalid column: " + columnLabel, SQLStates.INVALID_COLUMN);
	}

	@Override
	public String getString(final String columnLabel) throws SQLException {
		return getString(findColumn(columnLabel));
	}

	@Override
	public boolean getBoolean(final String columnLabel) throws SQLException {
		return getBoolean(findColumn(columnLabel));
	}

	@Override
	public byte getByte(final String columnLabel) throws SQLException {
		return getByte(findColumn(columnLabel));
	}

	@Override
	public short getShort(final String columnLabel) throws SQLException {
		return getShort(findColumn(columnLabel));
	}

	@Override
	public int getInt(final String columnLabel) throws SQLException {
		return getInt(findColumn(columnLabel));
	}

	@Override
	public long getLong(final String columnLabel) throws SQLException {
		return getLong(findColumn(columnLabel));
	}

	@Override
	public float getFloat(final String columnLabel) throws SQLException {
		return getFloat(findColumn(columnLabel));
	}

	@Override
	public double getDouble(final String columnLabel) throws SQLException {
		return getDouble(findColumn(columnLabel));
	}

	@Deprecated
	@Override
	public BigDecimal getBigDecimal(final String columnLabel, final int scale) throws SQLException {
		return getBigDecimal(findColumn(columnLabel), scale);
	}

	@Override
	public byte[] getBytes(final String columnLabel) throws SQLException {
		return getBytes(findColumn(columnLabel));
	}

	@Override
	public Date getDate(final String columnLabel) throws SQLException {
		return getDate(findColumn(columnLabel));
	}

	@Override
	public Time getTime(final String columnLabel) throws SQLException {
		return getTime(findColumn(columnLabel));
	}

	@Override
	public Timestamp getTimestamp(final String columnLabel) throws SQLException {
		return getTimestamp(findColumn(columnLabel));
	}

	@Override
	public InputStream getAsciiStream(final String columnLabel) throws SQLException {
		return getAsciiStream(findColumn(columnLabel));
	}

	@Deprecated
	@Override
	public InputStream getUnicodeStream(final String columnLabel) throws SQLException {
		return getUnicodeStream(findColumn(columnLabel));
	}

	@Override
	public InputStream getBinaryStream(final String columnLabel) throws SQLException {
		return getBinaryStream(findColumn(columnLabel));
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
	public String getCursorName() throws SQLException {
		return statement != null ? statement.cursorName : "NO_NAME";
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return new ParadoxResultSetMetaData(conn, columns);
	}

	@Override
	public Object getObject(final int columnIndex) throws SQLException {
		verifyRow();

		final List<FieldValue> row = values.get(position);
		if (columnIndex > row.size()) {
			throw new SQLException("Invalid column.", SQLStates.INVALID_COLUMN);
		}
		lastValue = row.get(columnIndex - 1);
		return lastValue.getValue();
	}

	@Override
	public Object getObject(final String columnLabel) throws SQLException {
		return getObject(findColumn(columnLabel));
	}

	@Override
	public Reader getCharacterStream(final int columnIndex) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public Reader getCharacterStream(final String columnLabel) throws SQLException {
		return getCharacterStream(findColumn(columnLabel));
	}

	@Override
	public BigDecimal getBigDecimal(final int columnIndex) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public BigDecimal getBigDecimal(final String columnLabel) throws SQLException {
		return getBigDecimal(findColumn(columnLabel));
	}

	@Override
	public boolean isBeforeFirst() throws SQLException {
		return position == -1;
	}

	@Override
	public boolean isAfterLast() throws SQLException {
		return position > values.size();
	}

	@Override
	public boolean isFirst() throws SQLException {
		return position + 1 == 0;
	}

	@Override
	public boolean isLast() throws SQLException {
		return position + 1 == values.size();
	}

	@Override
	public void beforeFirst() throws SQLException {
		position = -1;
	}

	@Override
	public void afterLast() throws SQLException {
		position = values.size();
	}

	@Override
	public boolean first() throws SQLException {
		if (values.isEmpty()) {
			return false;
		}
		position = 0;
		clearClobs();
		return true;
	}

	@Override
	public boolean last() throws SQLException {
		if (values.isEmpty()) {
			return false;
		}
		position = values.size() - 1;
		clearClobs();
		return true;
	}

	@Override
	public int getRow() throws SQLException {
		return position + 1;
	}

	@Override
	public boolean absolute(final int row) throws SQLException {
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

	@Override
	public boolean relative(final int rows) throws SQLException {
		return false;
	}

	@Override
	public boolean previous() throws SQLException {
		if (position > -1) {
			position--;
			clearClobs();
			return true;
		}
		return false;
	}

	@Override
	public void setFetchDirection(final int direction) throws SQLException {
		throw new SQLException("No fetch direction");
	}

	@Override
	public int getFetchDirection() throws SQLException {
		throw new SQLException("No fetch direction");
	}

	@Override
	public void setFetchSize(final int rows) throws SQLException {
		fetchSize = rows;
	}

	@Override
	public int getFetchSize() throws SQLException {
		return fetchSize;
	}

	@Override
	public int getType() throws SQLException {
		throw new SQLException("getType");
	}

	@Override
	public int getConcurrency() throws SQLException {
		return ResultSet.CONCUR_READ_ONLY;
	}

	@Override
	public boolean rowUpdated() throws SQLException {
		return false;
	}

	@Override
	public boolean rowInserted() throws SQLException {
		return false;
	}

	@Override
	public boolean rowDeleted() throws SQLException {
		return false;
	}

	@Override
	public void updateNull(final int columnIndex) throws SQLException {
	}

	@Override
	public void updateBoolean(final int columnIndex, final boolean x) throws SQLException {
	}

	@Override
	public void updateByte(final int columnIndex, final byte x) throws SQLException {
	}

	@Override
	public void updateShort(final int columnIndex, final short x) throws SQLException {
	}

	@Override
	public void updateInt(final int columnIndex, final int x) throws SQLException {
	}

	@Override
	public void updateLong(final int columnIndex, final long x) throws SQLException {
	}

	@Override
	public void updateFloat(final int columnIndex, final float x) throws SQLException {
	}

	@Override
	public void updateDouble(final int columnIndex, final double x) throws SQLException {
	}

	@Override
	public void updateBigDecimal(final int columnIndex, final BigDecimal x) throws SQLException {
	}

	@Override
	public void updateString(final int columnIndex, final String x) throws SQLException {
	}

	@Override
	public void updateBytes(final int columnIndex, final byte[] x) throws SQLException {
	}

	@Override
	public void updateDate(final int columnIndex, final Date x) throws SQLException {
	}

	@Override
	public void updateTime(final int columnIndex, final Time x) throws SQLException {
	}

	@Override
	public void updateTimestamp(final int columnIndex, final Timestamp x) throws SQLException {
	}

	@Override
	public void updateAsciiStream(final int columnIndex, final InputStream x, final int length) throws SQLException {
	}

	@Override
	public void updateBinaryStream(final int columnIndex, final InputStream x, final int length) throws SQLException {
	}

	@Override
	public void updateCharacterStream(final int columnIndex, final Reader x, final int length) throws SQLException {
	}

	@Override
	public void updateObject(final int columnIndex, final Object x, final int scaleOrLength) throws SQLException {
	}

	@Override
	public void updateObject(final int columnIndex, final Object x) throws SQLException {
	}

	@Override
	public void updateNull(final String columnLabel) throws SQLException {
	}

	@Override
	public void updateBoolean(final String columnLabel, final boolean x) throws SQLException {
	}

	@Override
	public void updateByte(final String columnLabel, final byte x) throws SQLException {
	}

	@Override
	public void updateShort(final String columnLabel, final short x) throws SQLException {
	}

	@Override
	public void updateInt(final String columnLabel, final int x) throws SQLException {
	}

	@Override
	public void updateLong(final String columnLabel, final long x) throws SQLException {
	}

	@Override
	public void updateFloat(final String columnLabel, final float x) throws SQLException {
	}

	@Override
	public void updateDouble(final String columnLabel, final double x) throws SQLException {
	}

	@Override
	public void updateBigDecimal(final String columnLabel, final BigDecimal x) throws SQLException {
	}

	@Override
	public void updateString(final String columnLabel, final String x) throws SQLException {
	}

	@Override
	public void updateBytes(final String columnLabel, final byte[] x) throws SQLException {
	}

	@Override
	public void updateDate(final String columnLabel, final Date x) throws SQLException {
	}

	@Override
	public void updateTime(final String columnLabel, final Time x) throws SQLException {
	}

	@Override
	public void updateTimestamp(final String columnLabel, final Timestamp x) throws SQLException {
	}

	@Override
	public void updateAsciiStream(final String columnLabel, final InputStream x, final int length) throws SQLException {
	}

	@Override
	public void updateBinaryStream(final String columnLabel, final InputStream x, final int length) throws SQLException {
	}

	@Override
	public void updateCharacterStream(final String columnLabel, final Reader reader, final int length) throws SQLException {
	}

	@Override
	public void updateObject(final String columnLabel, final Object x, final int scaleOrLength) throws SQLException {
	}

	@Override
	public void updateObject(final String columnLabel, final Object x) throws SQLException {
	}

	@Override
	public void insertRow() throws SQLException {
	}

	@Override
	public void updateRow() throws SQLException {
	}

	@Override
	public void deleteRow() throws SQLException {
	}

	@Override
	public void refreshRow() throws SQLException {
	}

	@Override
	public void cancelRowUpdates() throws SQLException {
	}

	@Override
	public void moveToInsertRow() throws SQLException {
	}

	@Override
	public void moveToCurrentRow() throws SQLException {
	}

	@Override
	public Statement getStatement() throws SQLException {
		return statement;
	}

	@Override
	public Object getObject(final int columnIndex, final Map<String, Class<?>> map) throws SQLException {
		return getObject(columnIndex);
	}

	@Override
	public Ref getRef(final int columnIndex) throws SQLException {
		return null;
	}

	@Override
	public Blob getBlob(final int columnIndex) throws SQLException {
		return null;
	}

	@Override
	public Array getArray(final int columnIndex) throws SQLException {
		return null;
	}

	@Override
	public Object getObject(final String columnLabel, final Map<String, Class<?>> map) throws SQLException {
		return getObject(columnLabel);
	}

	@Override
	public Ref getRef(final String columnLabel) throws SQLException {
		return null;
	}

	@Override
	public Blob getBlob(final String columnLabel) throws SQLException {
		return null;
	}

	@Override
	public Clob getClob(final String columnLabel) throws SQLException {
		return getClob(findColumn(columnLabel));
	}

	@Override
	public Clob getClob(final int columnIndex) throws SQLException {
		if (clobs == null) {
			clobs = new HashMap(1);
		}
		if (clobs.containsKey(columnIndex)) {
			return clobs.get(columnIndex);
		}
		Object val = this.getObject(columnIndex);
		if (val != null) {
			if (val instanceof ClobDescriptor) {
                ParadoxClob clob = new ParadoxClob((ClobDescriptor)val);
                clobs.put(columnIndex, clob);
				return clob;
			} else {
				throw new SQLException("Filed isn't clob type", SQLStates.INVALID_FIELD_VALUE);
			}
		}
		return null;
	}

	@Override
	public Array getArray(final String columnLabel) throws SQLException {
		return null;
	}

	@Override
	public Date getDate(final int columnIndex, final Calendar cal) throws SQLException {
		return getDate(columnIndex);
	}

	@Override
	public Date getDate(final String columnLabel, final Calendar cal) throws SQLException {
		return getDate(columnLabel);
	}

	@Override
	public Time getTime(final int columnIndex, final Calendar cal) throws SQLException {
		return getTime(columnIndex);
	}

	@Override
	public Time getTime(final String columnLabel, final Calendar cal) throws SQLException {
		return getTime(columnLabel);
	}

	@Override
	public Timestamp getTimestamp(final int columnIndex, final Calendar cal) throws SQLException {
		return getTimestamp(columnIndex);
	}

	@Override
	public Timestamp getTimestamp(final String columnLabel, final Calendar cal) throws SQLException {
		return getTimestamp(columnLabel);
	}

	@Override
	public URL getURL(final int columnIndex) throws SQLException {
		return null;
	}

	@Override
	public URL getURL(final String columnLabel) throws SQLException {
		return null;
	}

	@Override
	public void updateRef(final int columnIndex, final Ref x) throws SQLException {
	}

	@Override
	public void updateRef(final String columnLabel, final Ref x) throws SQLException {
	}

	@Override
	public void updateBlob(final int columnIndex, final Blob x) throws SQLException {
	}

	@Override
	public void updateBlob(final String columnLabel, final Blob x) throws SQLException {
	}

	@Override
	public void updateClob(final int columnIndex, final Clob x) throws SQLException {
	}

	@Override
	public void updateClob(final String columnLabel, final Clob x) throws SQLException {
	}

	@Override
	public void updateArray(final int columnIndex, final Array x) throws SQLException {
	}

	@Override
	public void updateArray(final String columnLabel, final Array x) throws SQLException {
	}

	@Override
	public RowId getRowId(final int columnIndex) throws SQLException {
		return null;
	}

	@Override
	public RowId getRowId(final String columnLabel) throws SQLException {
		return null;
	}

	@Override
	public void updateRowId(final int columnIndex, final RowId x) throws SQLException {
	}

	@Override
	public void updateRowId(final String columnLabel, final RowId x) throws SQLException {
	}

	@Override
	public int getHoldability() throws SQLException {
		return conn.getHoldability();
	}

	@Override
	public boolean isClosed() throws SQLException {
		return closed;
	}

	@Override
	public void updateNString(final int columnIndex, final String nString) throws SQLException {
	}

	@Override
	public void updateNString(final String columnLabel, final String nString) throws SQLException {
	}

	@Override
	public void updateNClob(final int columnIndex, final NClob nClob) throws SQLException {
	}

	@Override
	public void updateNClob(final String columnLabel, final NClob nClob) throws SQLException {
	}

	@Override
	public NClob getNClob(final int columnIndex) throws SQLException {
		return null;
	}

	@Override
	public NClob getNClob(final String columnLabel) throws SQLException {
		return null;
	}

	@Override
	public SQLXML getSQLXML(final int columnIndex) throws SQLException {
		return null;
	}

	@Override
	public SQLXML getSQLXML(final String columnLabel) throws SQLException {
		return null;
	}

	@Override
	public void updateSQLXML(final int columnIndex, final SQLXML xmlObject) throws SQLException {
	}

	@Override
	public void updateSQLXML(final String columnLabel, final SQLXML xmlObject) throws SQLException {
	}

	@Override
	public String getNString(final int columnIndex) throws SQLException {
		return null;
	}

	@Override
	public String getNString(final String columnLabel) throws SQLException {
		return null;
	}

	@Override
	public Reader getNCharacterStream(final int columnIndex) throws SQLException {
		return null;
	}

	@Override
	public Reader getNCharacterStream(final String columnLabel) throws SQLException {
		return null;
	}

	@Override
	public void updateNCharacterStream(final int columnIndex, final Reader x, final long length) throws SQLException {
	}

	@Override
	public void updateNCharacterStream(final String columnLabel, final Reader reader, final long length) throws SQLException {
	}

	@Override
	public void updateAsciiStream(final int columnIndex, final InputStream x, final long length) throws SQLException {
	}

	@Override
	public void updateBinaryStream(final int columnIndex, final InputStream x, final long length) throws SQLException {
	}

	@Override
	public void updateCharacterStream(final int columnIndex, final Reader x, final long length) throws SQLException {
	}

	@Override
	public void updateAsciiStream(final String columnLabel, final InputStream x, final long length) throws SQLException {
	}

	@Override
	public void updateBinaryStream(final String columnLabel, final InputStream x, final long length) throws SQLException {
	}

	@Override
	public void updateCharacterStream(final String columnLabel, final Reader reader, final long length) throws SQLException {
	}

	@Override
	public void updateBlob(final int columnIndex, final InputStream inputStream, final long length) throws SQLException {
	}

	@Override
	public void updateBlob(final String columnLabel, final InputStream inputStream, final long length) throws SQLException {
	}

	@Override
	public void updateClob(final int columnIndex, final Reader reader, final long length) throws SQLException {
	}

	@Override
	public void updateClob(final String columnLabel, final Reader reader, final long length) throws SQLException {
	}

	@Override
	public void updateNClob(final int columnIndex, final Reader reader, final long length) throws SQLException {
	}

	@Override
	public void updateNClob(final String columnLabel, final Reader reader, final long length) throws SQLException {
	}

	@Override
	public void updateNCharacterStream(final int columnIndex, final Reader x) throws SQLException {
	}

	@Override
	public void updateNCharacterStream(final String columnLabel, final Reader reader) throws SQLException {
	}

	@Override
	public void updateAsciiStream(final int columnIndex, final InputStream x) throws SQLException {
	}

	@Override
	public void updateBinaryStream(final int columnIndex, final InputStream x) throws SQLException {
	}

	@Override
	public void updateCharacterStream(final int columnIndex, final Reader x) throws SQLException {
	}

	@Override
	public void updateAsciiStream(final String columnLabel, final InputStream x) throws SQLException {
	}

	@Override
	public void updateBinaryStream(final String columnLabel, final InputStream x) throws SQLException {
	}

	@Override
	public void updateCharacterStream(final String columnLabel, final Reader reader) throws SQLException {
	}

	@Override
	public void updateBlob(final int columnIndex, final InputStream inputStream) throws SQLException {
	}

	@Override
	public void updateBlob(final String columnLabel, final InputStream inputStream) throws SQLException {
	}

	@Override
	public void updateClob(final int columnIndex, final Reader reader) throws SQLException {
	}

	@Override
	public void updateClob(final String columnLabel, final Reader reader) throws SQLException {
	}

	@Override
	public void updateNClob(final int columnIndex, final Reader reader) throws SQLException {
	}

	@Override
	public void updateNClob(final String columnLabel, final Reader reader) throws SQLException {
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T unwrap(final Class<T> iface) throws SQLException {
		if (isWrapperFor(iface)) {
			return (T) this;
		}
		throw new SQLException("Tipo não encontrado.", SQLStates.TYPE_NOT_FOUND);
	}

	@Override
	public boolean isWrapperFor(final Class<?> iface) throws SQLException {
		return getClass().isAssignableFrom(iface);
	}

	public <T> T getObject(final int columnIndex, final Class<T> type) throws SQLException {
		throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose Tools | Templates.
	}

	public <T> T getObject(final String columnLabel, final Class<T> type) throws SQLException {
		throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose Tools | Templates.
	}

	public List<List<FieldValue>> getValues() {
		return values;
	}

}
