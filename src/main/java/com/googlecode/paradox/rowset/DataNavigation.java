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
package com.googlecode.paradox.rowset;

import com.googlecode.paradox.utils.SQLStates;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Data navigation facility.
 *
 * @version 1.0
 * @since 1.6.0
 */
public class DataNavigation implements AutoCloseable {

    private boolean closed;
    private List<Object[]> values;
    private Object[] currentRow;
    /**
     * Last got value.
     */
    private Object lastValue;
    /**
     * Current index.
     */
    private int index = -1;
    /**
     * ResultSet fetch direction.
     */
    private int fetchDirection = ResultSet.FETCH_FORWARD;

    public DataNavigation(final List<Object[]> values) {
        this.values = values;
    }

    public Object getColumnValue(final int columnIndex) throws SQLException {
        verifyStatus();
        verifyRow();

        if (columnIndex > currentRow.length) {
            throw new SQLException("Invalid column index: " + columnIndex, SQLStates.INVALID_COLUMN.getValue());
        }

        this.lastValue = currentRow[columnIndex - 1];
        return this.lastValue;
    }

    public int getFetchDirection() throws SQLException {
        verifyStatus();
        return fetchDirection;
    }

    public void setFetchDirection(int fetchDirection) throws SQLException {
        verifyStatus();

        if (fetchDirection != ResultSet.FETCH_FORWARD && fetchDirection != ResultSet.FETCH_REVERSE) {
            throw new SQLException("Unsupported fetch direction: " + fetchDirection);
        }

        this.fetchDirection = fetchDirection;
    }

    public Object getLastValue() throws SQLException {
        verifyStatus();
        return lastValue;
    }

    public boolean absolute(final int row) throws SQLException {
        verifyStatus();

        boolean ret = false;
        if (row > 0) {
            if (row <= values.size()) {
                index = row - 1;
                ret = true;
                updateCurrentRow();
            }
        } else {
            if (values.size() + row >= 0) {
                index = values.size() + row;
                ret = true;
                updateCurrentRow();
            }
        }

        return ret;
    }

    public boolean first() throws SQLException {
        verifyStatus();

        if (values.isEmpty()) {
            return false;
        }

        index = 0;
        updateCurrentRow();
        return true;
    }

    public void afterLast() throws SQLException {
        verifyStatus();

        if (!values.isEmpty()) {
            index = values.size();
            updateCurrentRow();
        }
    }

    public void beforeFirst() throws SQLException {
        verifyStatus();

        index = -1;
        updateCurrentRow();
    }

    public boolean isAfterLast() throws SQLException {
        verifyStatus();

        return index >= values.size();
    }

    public boolean isBeforeFirst() throws SQLException {
        verifyStatus();

        return index < 0;
    }

    public int getRow() throws SQLException {
        verifyStatus();

        return index + 1;
    }

    public boolean isFirst() throws SQLException {
        verifyStatus();

        return index == 0;
    }

    public boolean isLast() throws SQLException {
        verifyStatus();

        return index == values.size() - 1;
    }

    public boolean last() throws SQLException {
        verifyStatus();

        if (values.isEmpty()) {
            return false;
        }

        index = values.size() - 1;
        updateCurrentRow();
        return true;
    }

    public boolean relative(final int rows) throws SQLException {
        verifyStatus();

        if (rows > 0) {
            if (index + rows < this.values.size()) {
                index += rows;
                updateCurrentRow();
                return true;
            }
        } else {
            if (index + rows >= 0) {
                index += rows;
                updateCurrentRow();
                return true;
            }
        }

        return false;
    }

    @Override
    public void close() {
        this.closed = true;
        this.values = null;
    }

    private void verifyRow() throws SQLException {
        if (index == -1) {
            throw new SQLException("Call ResultSet.next() first.", SQLStates.INVALID_ROW.getValue());
        } else if (index == values.size()) {
            throw new SQLException("There is no more rows to read.", SQLStates.INVALID_ROW.getValue());
        }
    }

    private void verifyStatus() throws SQLException {
        if (this.closed) {
            throw new SQLException("Closed result set.", SQLStates.RESULTSET_CLOSED.getValue());
        }
    }

    private void updateCurrentRow() {
        if (index == -1 || index == values.size()) {
            this.currentRow = null;
        } else {
            this.currentRow = values.get(index);
        }
    }

    public boolean next() throws SQLException {
        if (hasNext()) {
            if (fetchDirection == ResultSet.FETCH_FORWARD) {
                moveNext();
            } else {
                movePrevious();
            }

            return true;
        }

        return false;
    }

    public boolean previous() throws SQLException {
        if (hasNext()) {
            if (fetchDirection == ResultSet.FETCH_FORWARD) {
                movePrevious();
            } else {
                moveNext();
            }

            return true;
        }

        return false;
    }

    private void moveNext() {
        index++;
        updateCurrentRow();
    }

    private void movePrevious() {
        index--;
        updateCurrentRow();
    }

    private boolean hasNext() throws SQLException {
        verifyStatus();

        if (fetchDirection == ResultSet.FETCH_FORWARD) {
            return index + 1 < values.size();
        } else {
            return index - 1 >= 0;
        }
    }

    public boolean isClosed() {
        return closed;
    }
}
