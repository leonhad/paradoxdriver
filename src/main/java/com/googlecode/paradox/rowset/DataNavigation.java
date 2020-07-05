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

import com.googlecode.paradox.exceptions.ParadoxException;

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
            throw new ParadoxException(ParadoxException.Error.INVALID_COLUMN, Integer.toString(columnIndex), null);
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

        boolean ret;
        if (row >= 0) {
            if (row <= values.size()) {
                index = row - 1;
                ret = true;
            } else {
                index = values.size();
                ret = false;
            }
        } else {
            if (values.size() + row >= 0) {
                index = values.size() + row;
                ret = true;
            } else {
                index = -1;
                ret = false;
            }
        }

        updateCurrentRow();
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

        return index >= values.size() || values.size() == 0;
    }

    public boolean isBeforeFirst() throws SQLException {
        verifyStatus();

        return index < 0;
    }

    public int getRow() throws SQLException {
        verifyStatus();

        if (index == -1 || index == values.size()) {
            return 0;
        }

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
            } else {
                last();
            }
        } else {
            if (index + rows >= 0) {
                index += rows;
            } else {
                first();
            }
        }

        updateCurrentRow();
        return true;
    }

    @Override
    public void close() {
        this.closed = true;
        this.values = null;
    }

    private void verifyRow() throws SQLException {
        if (index == -1) {
            throw new ParadoxException(ParadoxException.Error.USE_NEXT_FIRST);
        } else if (index == values.size()) {
            throw new ParadoxException(ParadoxException.Error.NO_MORE_ROWS);
        }
    }

    private void verifyStatus() throws SQLException {
        if (this.closed) {
            throw new ParadoxException(ParadoxException.Error.RESULT_SET_CLOSED);
        }
    }

    private void updateCurrentRow() {
        if (index == -1 || index == values.size()) {
            this.currentRow = null;
        } else {
            this.currentRow = values.get(index);
        }
    }

    public boolean next() {
        if (fetchDirection == ResultSet.FETCH_FORWARD) {
            return moveNext();
        } else {
            return movePrevious();
        }
    }

    public boolean previous() {
        if (fetchDirection == ResultSet.FETCH_FORWARD) {
            return movePrevious();
        } else {
            return moveNext();
        }
    }

    private boolean moveNext() {
        if (index < values.size()) {
            index++;
        }

        updateCurrentRow();
        return index != values.size();
    }

    private boolean movePrevious() {
        if (index != -1) {
            index--;
        }

        updateCurrentRow();
        return index != -1;
    }

    public boolean isClosed() {
        return closed;
    }
}
