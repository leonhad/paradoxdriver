/*
 */
package org.paradox;

import org.paradox.metadata.ParadoxResultSetMetaData;
import org.paradox.results.ColumnDTO;
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
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import org.paradox.data.table.value.AbstractFieldValue;
import org.paradox.data.table.value.AbstractNumericValue;
import org.paradox.data.table.value.BooleanValue;
import org.paradox.data.table.value.DateValue;
import org.paradox.data.table.value.TimeValue;
import org.paradox.utils.SQLStates;

/**
 *
 * @author Leonardo
 */
public class ParadoxResultSet implements ResultSet {

    private final ParadoxStatement statement;
    private boolean closed = false;
    private SQLWarning warnings = null;
    private final ArrayList<ArrayList<AbstractFieldValue>> values;
    private int position = -1;
    private final ArrayList<ColumnDTO> columns;
    private int fetchSize = 10;
    private final ParadoxConnection conn;
    private AbstractFieldValue lastValue = null;

    public ParadoxResultSet(final ParadoxConnection conn, final ParadoxStatement statement, final ArrayList<ArrayList<AbstractFieldValue>> values, final ArrayList<ColumnDTO> columns) {
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
            throw new SQLException("Result do not have more rows.", SQLStates.INVALID_ROW);
        } else if (closed) {
            throw new SQLException("Closed resultset.", SQLStates.RESULTSET_CLOSED);
        }
    }

    private boolean hasNext() {
        return position < values.size();
    }

    @Override
    public boolean next() throws SQLException {
        position++;
        return hasNext();
    }

    @Override
    public void close() throws SQLException {
        values.clear();
        closed = true;
    }

    @Override
    public boolean wasNull() throws SQLException {
        if (closed) {
            throw new SQLException("Closed resultset.", SQLStates.RESULTSET_CLOSED);
        }
        return lastValue == null;
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        verifyRow();

        final ArrayList<AbstractFieldValue> row = values.get(position);
        if (columnIndex > row.size()) {
            throw new SQLException("Invalid column.", SQLStates.INVALID_COLUMN);
        }
        lastValue = row.get(columnIndex - 1);
        if (lastValue.getValue() != null) {
            return lastValue.getValue().toString();
        }
        return null;
    }

    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        verifyRow();

        final ArrayList<AbstractFieldValue> row = values.get(position);
        if (columnIndex > row.size()) {
            throw new SQLException("Invalid column.", SQLStates.INVALID_COLUMN);
        }
        lastValue = row.get(columnIndex - 1);
        if (lastValue instanceof BooleanValue) {
            return ((BooleanValue) lastValue).getBoolean();
        }
        throw new SQLException("Invalid field value.", SQLStates.INVALID_FIELD_VALUE);
    }

    @Override
    public byte getByte(int columnIndex) throws SQLException {
        verifyRow();

        final ArrayList<AbstractFieldValue> row = values.get(position);
        if (columnIndex > row.size()) {
            throw new SQLException("Invalid column.", SQLStates.INVALID_COLUMN);
        }
        lastValue = row.get(columnIndex - 1);
        if (lastValue instanceof AbstractNumericValue) {
            return ((AbstractNumericValue) lastValue).getByte();
        }
        throw new SQLException("Invalid field value.", SQLStates.INVALID_FIELD_VALUE);
    }

    @Override
    public short getShort(int columnIndex) throws SQLException {
        verifyRow();

        final ArrayList<AbstractFieldValue> row = values.get(position);
        if (columnIndex > row.size()) {
            throw new SQLException("Invalid column.", SQLStates.INVALID_COLUMN);
        }
        lastValue = row.get(columnIndex - 1);
        if (lastValue instanceof AbstractNumericValue) {
            return ((AbstractNumericValue) lastValue).getShort();
        }
        throw new SQLException("Invalid field value.", SQLStates.INVALID_FIELD_VALUE);
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        verifyRow();

        final ArrayList<AbstractFieldValue> row = values.get(position);
        if (columnIndex > row.size()) {
            throw new SQLException("Invalid column.", SQLStates.INVALID_COLUMN);
        }
        lastValue = row.get(columnIndex - 1);
        if (lastValue instanceof AbstractNumericValue) {
            return ((AbstractNumericValue) lastValue).getInt();
        }
        throw new SQLException("Column " + columnIndex + " Invalid field value.", SQLStates.INVALID_FIELD_VALUE);
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {
        verifyRow();

        final ArrayList<AbstractFieldValue> row = values.get(position);
        if (columnIndex > row.size()) {
            throw new SQLException("Invalid column.", SQLStates.INVALID_COLUMN);
        }
        lastValue = row.get(columnIndex - 1);
        if (lastValue instanceof AbstractNumericValue) {
            return ((AbstractNumericValue) lastValue).getLong();
        }
        throw new SQLException("Invalid field value.", SQLStates.INVALID_FIELD_VALUE);
    }

    @Override
    public float getFloat(int columnIndex) throws SQLException {
        verifyRow();

        final ArrayList<AbstractFieldValue> row = values.get(position);
        if (columnIndex > row.size()) {
            throw new SQLException("Invalid column.", SQLStates.INVALID_COLUMN);
        }
        lastValue = row.get(columnIndex - 1);
        if (lastValue instanceof AbstractNumericValue) {
            return ((AbstractNumericValue) lastValue).getFloat();
        }
        throw new SQLException("Invalid field value.", SQLStates.INVALID_FIELD_VALUE);
    }

    @Override
    public double getDouble(int columnIndex) throws SQLException {
        verifyRow();

        final ArrayList<AbstractFieldValue> row = values.get(position);
        if (columnIndex > row.size()) {
            throw new SQLException("Invalid column.", SQLStates.INVALID_COLUMN);
        }
        lastValue = row.get(columnIndex - 1);
        if (lastValue instanceof AbstractNumericValue) {
            return ((AbstractNumericValue) lastValue).getDouble();
        }
        throw new SQLException("Invalid field value.", SQLStates.INVALID_FIELD_VALUE);
    }

    @Deprecated
    @Override
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        verifyRow();

        final BigDecimal value = getBigDecimal(columnIndex);
        if (value != null) {
            return value.setScale(scale);
        }
        return null;
    }

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Date getDate(int columnIndex) throws SQLException {
        verifyRow();

        final ArrayList<AbstractFieldValue> row = values.get(position);
        if (columnIndex > row.size()) {
            throw new SQLException("Invalid column.", SQLStates.INVALID_COLUMN);
        }
        lastValue = row.get(columnIndex - 1);
        if (lastValue instanceof DateValue) {
            return ((DateValue) lastValue).getValue();
        }
        throw new SQLException("Column " + columnIndex + " Invalid field value.", SQLStates.INVALID_FIELD_VALUE);
    }

    @Override
    public Time getTime(int columnIndex) throws SQLException {
        verifyRow();

        final ArrayList<AbstractFieldValue> row = values.get(position);
        if (columnIndex > row.size()) {
            throw new SQLException("Invalid column.", SQLStates.INVALID_COLUMN);
        }
        lastValue = row.get(columnIndex - 1);
        if (lastValue instanceof TimeValue) {
            return ((TimeValue) lastValue).getValue();
        }
        throw new SQLException("Column " + columnIndex + " Invalid field value.", SQLStates.INVALID_FIELD_VALUE);
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        return null;
    }

    @Deprecated
    @Override
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public int findColumn(final String columnLabel) throws SQLException {
        for (final ColumnDTO column : columns) {
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
    public boolean getBoolean(String columnLabel) throws SQLException {
        return getBoolean(findColumn(columnLabel));
    }

    @Override
    public byte getByte(String columnLabel) throws SQLException {
        return getByte(findColumn(columnLabel));
    }

    @Override
    public short getShort(String columnLabel) throws SQLException {
        return getShort(findColumn(columnLabel));
    }

    @Override
    public int getInt(String columnLabel) throws SQLException {
        return getInt(findColumn(columnLabel));
    }

    @Override
    public long getLong(String columnLabel) throws SQLException {
        return getLong(findColumn(columnLabel));
    }

    @Override
    public float getFloat(String columnLabel) throws SQLException {
        return getFloat(findColumn(columnLabel));
    }

    @Override
    public double getDouble(String columnLabel) throws SQLException {
        return getDouble(findColumn(columnLabel));
    }

    @Deprecated
    @Override
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        return getBigDecimal(findColumn(columnLabel), scale);
    }

    @Override
    public byte[] getBytes(String columnLabel) throws SQLException {
        return getBytes(findColumn(columnLabel));
    }

    @Override
    public Date getDate(String columnLabel) throws SQLException {
        return getDate(findColumn(columnLabel));
    }

    @Override
    public Time getTime(String columnLabel) throws SQLException {
        return getTime(findColumn(columnLabel));
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        return getTimestamp(findColumn(columnLabel));
    }

    @Override
    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        return getAsciiStream(findColumn(columnLabel));
    }

    @Deprecated
    @Override
    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        return getUnicodeStream(findColumn(columnLabel));
    }

    @Override
    public InputStream getBinaryStream(String columnLabel) throws SQLException {
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
    public Object getObject(int columnIndex) throws SQLException {
        // FIXME verificar o tipo
        verifyRow();

        final ArrayList<AbstractFieldValue> row = values.get(position);
        if (columnIndex > row.size()) {
            throw new SQLException("Invalid column.", SQLStates.INVALID_COLUMN);
        }
        lastValue = row.get(columnIndex - 1);
        return lastValue.getValue();
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        return getObject(findColumn(columnLabel));
    }

    @Override
    public Reader getCharacterStream(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Reader getCharacterStream(String columnLabel) throws SQLException {
        return getCharacterStream(findColumn(columnLabel));
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
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
        return true;
    }

    @Override
    public boolean last() throws SQLException {
        if (values.isEmpty()) {
            return false;
        }
        position = values.size() - 1;
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
    public boolean relative(int rows) throws SQLException {
        return false;
    }

    @Override
    public boolean previous() throws SQLException {
        if (position > -1) {
            position--;
            return true;
        }
        return false;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        throw new SQLException("No fetch direction");
    }

    @Override
    public int getFetchDirection() throws SQLException {
        throw new SQLException("No fetch direction");
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
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
    public void updateNull(int columnIndex) throws SQLException {
    }

    @Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
    }

    @Override
    public void updateByte(int columnIndex, byte x) throws SQLException {
    }

    @Override
    public void updateShort(int columnIndex, short x) throws SQLException {
    }

    @Override
    public void updateInt(int columnIndex, int x) throws SQLException {
    }

    @Override
    public void updateLong(int columnIndex, long x) throws SQLException {
    }

    @Override
    public void updateFloat(int columnIndex, float x) throws SQLException {
    }

    @Override
    public void updateDouble(int columnIndex, double x) throws SQLException {
    }

    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
    }

    @Override
    public void updateString(int columnIndex, String x) throws SQLException {
    }

    @Override
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
    }

    @Override
    public void updateDate(int columnIndex, Date x) throws SQLException {
    }

    @Override
    public void updateTime(int columnIndex, Time x) throws SQLException {
    }

    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
    }

    @Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
    }

    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {
    }

    @Override
    public void updateNull(String columnLabel) throws SQLException {
    }

    @Override
    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
    }

    @Override
    public void updateByte(String columnLabel, byte x) throws SQLException {
    }

    @Override
    public void updateShort(String columnLabel, short x) throws SQLException {
    }

    @Override
    public void updateInt(String columnLabel, int x) throws SQLException {
    }

    @Override
    public void updateLong(String columnLabel, long x) throws SQLException {
    }

    @Override
    public void updateFloat(String columnLabel, float x) throws SQLException {
    }

    @Override
    public void updateDouble(String columnLabel, double x) throws SQLException {
    }

    @Override
    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
    }

    @Override
    public void updateString(String columnLabel, String x) throws SQLException {
    }

    @Override
    public void updateBytes(String columnLabel, byte[] x) throws SQLException {
    }

    @Override
    public void updateDate(String columnLabel, Date x) throws SQLException {
    }

    @Override
    public void updateTime(String columnLabel, Time x) throws SQLException {
    }

    @Override
    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
    }

    @Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
    }

    @Override
    public void updateObject(String columnLabel, Object x) throws SQLException {
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
    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        return getObject(columnIndex);
    }

    @Override
    public Ref getRef(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Blob getBlob(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Clob getClob(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Array getArray(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        return getObject(columnLabel);
    }

    @Override
    public Ref getRef(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public Blob getBlob(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public Clob getClob(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public Array getArray(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        return getDate(columnIndex);
    }

    @Override
    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        return getDate(columnLabel);
    }

    @Override
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        return getTime(columnIndex);
    }

    @Override
    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        return getTime(columnLabel);
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        return getTimestamp(columnIndex);
    }

    @Override
    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        return getTimestamp(columnLabel);
    }

    @Override
    public URL getURL(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public URL getURL(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public void updateRef(int columnIndex, Ref x) throws SQLException {
    }

    @Override
    public void updateRef(String columnLabel, Ref x) throws SQLException {
    }

    @Override
    public void updateBlob(int columnIndex, Blob x) throws SQLException {
    }

    @Override
    public void updateBlob(String columnLabel, Blob x) throws SQLException {
    }

    @Override
    public void updateClob(int columnIndex, Clob x) throws SQLException {
    }

    @Override
    public void updateClob(String columnLabel, Clob x) throws SQLException {
    }

    @Override
    public void updateArray(int columnIndex, Array x) throws SQLException {
    }

    @Override
    public void updateArray(String columnLabel, Array x) throws SQLException {
    }

    @Override
    public RowId getRowId(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public RowId getRowId(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public void updateRowId(int columnIndex, RowId x) throws SQLException {
    }

    @Override
    public void updateRowId(String columnLabel, RowId x) throws SQLException {
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
    public void updateNString(int columnIndex, String nString) throws SQLException {
    }

    @Override
    public void updateNString(String columnLabel, String nString) throws SQLException {
    }

    @Override
    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
    }

    @Override
    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
    }

    @Override
    public NClob getNClob(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public NClob getNClob(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
    }

    @Override
    public String getNString(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public String getNString(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
    }

    @Override
    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
    }

    @Override
    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
    }

    @Override
    public void updateClob(int columnIndex, Reader reader) throws SQLException {
    }

    @Override
    public void updateClob(String columnLabel, Reader reader) throws SQLException {
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (isWrapperFor(iface)) {
            return (T) this;
        }
        throw new SQLException("Tipo não encontrado.", SQLStates.TYPE_NOT_FOUND);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return getClass().isAssignableFrom(iface);
    }

    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
