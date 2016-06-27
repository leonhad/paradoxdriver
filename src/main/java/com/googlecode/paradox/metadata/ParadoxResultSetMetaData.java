/*
 * ParadoxResultSetMetaData.java
 *
 * 03/12/2009
 * Copyright (C) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.metadata;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.util.List;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.utils.SQLStates;
import com.googlecode.paradox.utils.Utils;

/**
 * Paradox Result Set Meta Data
 *
 * @author Leonardo Alves da Costa
 * @version 1.2
 * @since 1.0
 */
public class ParadoxResultSetMetaData implements ResultSetMetaData {

    private final List<Column> columns;
    private final ParadoxConnection conn;

    /**
     * Creates a new {@link ResultSetMetaData}.
     *
     * @param conn the Paradox connection.
     * @param columns columns in {@link ResultSet}.
     */
    public ParadoxResultSetMetaData(final ParadoxConnection conn, final List<Column> columns) {
        this.columns = columns;
        this.conn = conn;
    }

    @Override
    public String getCatalogName(final int column) throws SQLException {
        getColumn(column);
        return conn.getCatalog();
    }

    private Column getColumn(final int column) throws SQLException {
        if (column < 1 || column > columns.size()) {
            throw new SQLException("Invalid column: " + column, SQLStates.INVALID_COLUMN);
        }
        return columns.get(column - 1);
    }

    @Override
    public String getColumnClassName(final int column) throws SQLException {
        final int type = getColumnType(column);
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
    public int getColumnCount() throws SQLException {
        return columns.size();
    }

    @Override
    public int getColumnDisplaySize(final int column) throws SQLException {
        final Column dto = getColumn(column);
        return dto.getMaxSize();
    }

    @Override
    public String getColumnLabel(final int column) throws SQLException {
        final Column dto = getColumn(column);
        return dto.getName();
    }

    @Override
    public String getColumnName(final int column) throws SQLException {
        final Column dto = getColumn(column);
        return dto.getName();
    }

    @Override
    public int getColumnType(final int column) throws SQLException {
        final Column dto = getColumn(column);
        return dto.getType();
    }

    @Override
    public String getColumnTypeName(final int column) throws SQLException {
        final Column dto = getColumn(column);
        return Column.getTypeName(dto.getType());
    }

    @Override
    public int getPrecision(final int column) throws SQLException {
        final Column dto = getColumn(column);
        return dto.getPrecision();
    }

    @Override
    public int getScale(final int column) throws SQLException {
        final Column dto = getColumn(column);
        return dto.getScale();
    }

    @Override
    public String getSchemaName(final int column) throws SQLException {
        getColumn(column);
        return conn.getSchema();
    }

    @Override
    public String getTableName(final int column) throws SQLException {
        final Column dto = getColumn(column);
        return dto.getTableName();
    }

    @Override
    public boolean isAutoIncrement(final int column) throws SQLException {
        final Column dto = getColumn(column);
        return dto.isAutoIncrement();
    }

    @Override
    public boolean isCaseSensitive(final int column) throws SQLException {
        getColumn(column);
        return false;
    }

    @Override
    public boolean isCurrency(final int column) throws SQLException {
        final Column dto = getColumn(column);
        return dto.isCurrency();
    }

    @Override
    public boolean isDefinitelyWritable(final int column) throws SQLException {
        final Column dto = getColumn(column);
        return dto.isWriteable();
    }

    @Override
    public int isNullable(final int column) throws SQLException {
        final Column dto = getColumn(column);
        return dto.isNullable() ? ResultSetMetaData.columnNullable : ResultSetMetaData.columnNoNulls;
    }

    @Override
    public boolean isReadOnly(final int column) throws SQLException {
        final Column dto = getColumn(column);
        return dto.isReadOnly();
    }

    @Override
    public boolean isSearchable(final int column) throws SQLException {
        final Column dto = getColumn(column);
        return dto.isSearchable();
    }

    @Override
    public boolean isSigned(final int column) throws SQLException {
        final Column dto = getColumn(column);
        return dto.isSigned();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return Utils.isWrapperFor(this, iface);
    }

    @Override
    public boolean isWritable(final int column) throws SQLException {
        final Column dto = getColumn(column);
        return dto.isWriteable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        return Utils.unwrap(this, iface);
    }
}
