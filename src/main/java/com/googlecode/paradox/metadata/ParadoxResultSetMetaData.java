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
package com.googlecode.paradox.metadata;

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.exceptions.ParadoxException;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.utils.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Paradox {@link java.sql.ResultSetMetaData}.
 *
 * @since 1.0
 */
public final class ParadoxResultSetMetaData implements java.sql.ResultSetMetaData {

    /**
     * The result columns.
     */
    private final List<Column> columns;

    /**
     * The connection information.
     */
    private final ConnectionInfo connectionInfo;

    /**
     * Creates a new {@link java.sql.ResultSetMetaData}.
     *
     * @param connectionInfo the connection information.
     * @param columns        columns in {@link ResultSet}.
     */
    public ParadoxResultSetMetaData(final ConnectionInfo connectionInfo, final List<Column> columns) {
        this.columns = Collections.unmodifiableList(columns);
        this.connectionInfo = connectionInfo;
    }

    @Override
    public String getCatalogName(final int column) throws SQLException {
        this.getColumn(column);
        return this.connectionInfo.getCatalog();
    }

    @Override
    public String getColumnClassName(final int column) throws SQLException {
        return ParadoxType.valueOf(this.getColumnType(column)).getJavaClass().getName();
    }

    @Override
    public int getColumnCount() {
        return (int) this.columns.stream().filter(c -> !c.isHidden()).count();
    }

    @Override
    public int getColumnDisplaySize(final int column) throws SQLException {
        final Column dto = this.getColumn(column);
        return dto.getField().getPrecision();
    }

    @Override
    public String getColumnLabel(final int column) throws SQLException {
        return getColumnName(column);
    }

    @Override
    public String getColumnName(final int column) throws SQLException {
        final Column dto = this.getColumn(column);
        return dto.getName();
    }

    @Override
    public int getColumnType(final int column) throws SQLException {
        final Column dto = this.getColumn(column);
        return dto.getType().getSQLType();
    }

    @Override
    public String getColumnTypeName(final int column) throws SQLException {
        final Column dto = this.getColumn(column);
        return dto.getType().name();
    }

    @Override
    public int getPrecision(final int column) throws SQLException {
        final Column dto = this.getColumn(column);
        return dto.getPrecision();
    }

    @Override
    public int getScale(final int column) throws SQLException {
        final Column dto = this.getColumn(column);
        return dto.getScale();
    }

    @Override
    public String getSchemaName(final int column) throws SQLException {
        this.getColumn(column);
        return this.connectionInfo.getCurrentSchema().name();
    }

    @Override
    public String getTableName(final int column) throws SQLException {
        final Column dto = this.getColumn(column);
        return dto.getTableName();
    }

    @Override
    public boolean isAutoIncrement(final int column) throws SQLException {
        final Column dto = this.getColumn(column);
        return dto.isAutoIncrement();
    }

    @Override
    public boolean isCaseSensitive(final int column) throws SQLException {
        this.getColumn(column);
        return false;
    }

    @Override
    public boolean isCurrency(final int column) throws SQLException {
        final Column dto = this.getColumn(column);
        return dto.isCurrency();
    }

    @Override
    public boolean isDefinitelyWritable(final int column) throws SQLException {
        return isWritable(column);
    }

    @Override
    public int isNullable(final int column) throws SQLException {
        final Column dto = this.getColumn(column);
        if (dto.isNullable()) {
            return java.sql.ResultSetMetaData.columnNullable;
        }

        return java.sql.ResultSetMetaData.columnNoNulls;
    }

    @Override
    public boolean isReadOnly(final int column) throws SQLException {
        final Column dto = this.getColumn(column);
        return dto.isReadOnly();
    }

    @Override
    public boolean isSearchable(final int column) throws SQLException {
        final Column dto = this.getColumn(column);
        return dto.isSearchable();
    }

    @Override
    public boolean isSigned(final int column) throws SQLException {
        final Column dto = this.getColumn(column);
        return dto.isSigned();
    }

    @Override
    public boolean isWrapperFor(final Class<?> iFace) {
        return Utils.isWrapperFor(this, iFace);
    }

    @Override
    public boolean isWritable(final int column) throws SQLException {
        final Column dto = this.getColumn(column);
        return dto.isWritable();
    }

    @Override
    public <T> T unwrap(final Class<T> face) throws SQLException {
        return Utils.unwrap(this, face);
    }

    /**
     * Get the column.
     *
     * @param column the column index.
     * @return the column.
     * @throws SQLException in case of invalid type.
     */
    private Column getColumn(final int column) throws SQLException {
        // Not hidden and by index.
        return this.columns.stream()
                .filter(c -> !c.isHidden())
                .filter(c -> c.getIndex() == column)
                .findFirst().orElseThrow(() -> new ParadoxException(ParadoxException.Error.INVALID_COLUMN, column));
    }
}
