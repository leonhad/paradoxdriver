package com.googlecode.paradox.metadata;

import java.sql.Date;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.util.ArrayList;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.utils.SQLStates;

/**
 * Paradox Result Set Meta Data
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 03/12/2009
 */
public class ParadoxResultSetMetaData implements ResultSetMetaData {

    private final ArrayList<Column> columns;
    private final ParadoxConnection conn;

    public ParadoxResultSetMetaData(final ParadoxConnection conn, final ArrayList<Column> columns) {
        this.columns = columns;
        this.conn = conn;
    }

    private Column getColumn(final int column) throws SQLException {
        if (column < 1 || column > columns.size()) {
            throw new SQLException("Invalid column: " + column, SQLStates.INVALID_COLUMN);
        }
        return columns.get(column - 1);
    }

    @Override
    public int getColumnCount() throws SQLException {
        return columns.size();
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        final Column dto = getColumn(column);
        return dto.isAutoIncrement();
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        getColumn(column);
        return false;
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {
        final Column dto = getColumn(column);
        return dto.isSearchable();
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        final Column dto = getColumn(column);
        return dto.isCurrency();
    }

    @Override
    public int isNullable(int column) throws SQLException {
        final Column dto = getColumn(column);
        return dto.isNullable() ? ResultSetMetaData.columnNullable : ResultSetMetaData.columnNoNulls;
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        final Column dto = getColumn(column);
        return dto.isSigned();
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        final Column dto = getColumn(column);
        return dto.getMaxSize();
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        final Column dto = getColumn(column);
        return dto.getName();
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        final Column dto = getColumn(column);
        return dto.getName();
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        getColumn(column);
        return conn.getSchema();
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        final Column dto = getColumn(column);
        return dto.getPrecision();
    }

    @Override
    public int getScale(int column) throws SQLException {
        final Column dto = getColumn(column);
        return dto.getScale();
    }

    @Override
    public String getTableName(int column) throws SQLException {
        final Column dto = getColumn(column);
        return dto.getTableName();
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        getColumn(column);
        return conn.getCatalog();
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        final Column dto = getColumn(column);
        return dto.getType();
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        final Column dto = getColumn(column);
        return Column.getTypeName(dto.getType());
    }
 
    @Override
    public boolean isReadOnly(int column) throws SQLException {
        final Column dto = getColumn(column);
        return dto.isReadOnly();
    }

    @Override
    public boolean isWritable(int column) throws SQLException {
        final Column dto = getColumn(column);
        return dto.isWriteable();
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        final Column dto = getColumn(column);
        return dto.isWriteable();
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {
        int type = getColumnType(column);
        switch (type) {
            case Types.CHAR:
            case Types.VARCHAR:
                return String.class.getName();
            case Types.INTEGER:
                return Integer.class.getName();
            case Types.DATE:
                return Date.class.getName();
            case Types.TIME:
                return Time.class.getName();
            case Types.DOUBLE:
                return Double.class.getName();
            case Types.NUMERIC:
                return Double.class.getName();
            case Types.BOOLEAN:
                return Boolean.class.getName();
            default:
                throw new SQLException("Type Unknown", SQLStates.TYPE_NOT_FOUND);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        if (isWrapperFor(iface)) {
            return (T) this;
        }
        throw new SQLException("Type not found.", SQLStates.TYPE_NOT_FOUND);
    }

    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return getClass().isAssignableFrom(iface);
    }
}
