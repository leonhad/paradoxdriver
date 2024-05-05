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
package com.googlecode.paradox.rowset;

import com.googlecode.paradox.exceptions.ParadoxException;
import com.googlecode.paradox.results.Column;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Data navigation facility.
 *
 * @since 1.6.0
 */
public class DataNavigation implements AutoCloseable {

    private boolean closed;
    private List<? extends Object[]> values;
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

    /**
     * The column mapping.
     */
    private final int[] columns;

    /**
     * Creates a new instance.
     *
     * @param columns the column list.
     * @param values  the value list.
     */
    public DataNavigation(final List<Column> columns, final List<? extends Object[]> values) {
        this.columns = columns.stream().mapToInt(Column::getIndex).toArray();
        this.values = values;
    }

    /**
     * Gets the column value.
     *
     * @param columnIndex the column index.
     * @return the column value.
     * @throws SQLException in case of failures.
     */
    public Object getColumnValue(final int columnIndex) throws SQLException {
        verifyStatus();
        verifyRow();

        int currentIndex = -1;
        for (int loop = 0; loop < this.columns.length; loop++) {
            final int column = this.columns[loop];
            if (column == columnIndex) {
                currentIndex = loop;
                break;
            }
        }

        // Found a column?
        if (currentIndex == -1) {
            throw new ParadoxException(ParadoxException.Error.INVALID_COLUMN_INDEX, columnIndex);
        }

        this.lastValue = currentRow[currentIndex];
        return this.lastValue;
    }

    /**
     * Gets the fetch direction.
     *
     * @return the fetch direction.
     */
    public int getFetchDirection() throws SQLException {
        verifyStatus();
        return fetchDirection;
    }

    /**
     * Sets the fetch direction.
     *
     * @param fetchDirection the fetch direction.
     * @throws SQLException in case of failures.
     */
    public void setFetchDirection(int fetchDirection) throws SQLException {
        verifyStatus();

        if (fetchDirection != ResultSet.FETCH_FORWARD && fetchDirection != ResultSet.FETCH_REVERSE) {
            throw new ParadoxException(ParadoxException.Error.INVALID_FETCH_DIRECTION, fetchDirection);
        }

        this.fetchDirection = fetchDirection;
    }

    /**
     * Gets the last returned value.
     *
     * @return last returned value.
     * @throws SQLException in case of failures.
     */
    public Object getLastValue() throws SQLException {
        verifyStatus();
        return lastValue;
    }

    /**
     * Sets the absolute position.
     *
     * @param row the absolute row position.
     * @return <code>true</code> if success.
     * @throws SQLException in case of failures.
     */
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

    /**
     * Go to first row.
     *
     * @return <code>true</code> if success.
     * @throws SQLException in case of failures.
     */
    public boolean first() throws SQLException {
        verifyStatus();

        if (values.isEmpty()) {
            return false;
        }

        index = 0;
        updateCurrentRow();
        return true;
    }

    /**
     * Go to after the last one.
     *
     * @throws SQLException in case of failures.
     */
    public void afterLast() throws SQLException {
        verifyStatus();

        if (!values.isEmpty()) {
            index = values.size();
            updateCurrentRow();
        }
    }

    /**
     * Go to a row before the first.
     *
     * @throws SQLException in case of failures.
     */
    public void beforeFirst() throws SQLException {
        verifyStatus();

        index = -1;
        updateCurrentRow();
    }

    /**
     * If the current row is after the last one.
     *
     * @return <code>true</code> if the current row is after the last one.
     * @throws SQLException in case of failures.
     */
    public boolean isAfterLast() throws SQLException {
        verifyStatus();

        return index >= values.size() || values.isEmpty();
    }

    /**
     * If the current row is before the first one.
     *
     * @return <code>true</code> if the current row is before the first one.
     * @throws SQLException in case of failures.
     */
    public boolean isBeforeFirst() throws SQLException {
        verifyStatus();

        return index < 0;
    }

    /**
     * Gets the current row index.
     *
     * @return the current row index.
     * @throws SQLException in case of failures.
     */
    public int getRow() throws SQLException {
        verifyStatus();

        if (index == -1 || index == values.size()) {
            return 0;
        }

        return index + 1;
    }

    /**
     * If the current row is first.
     *
     * @return <code>true</code> if the current row is first.
     * @throws SQLException in case of failures.
     */
    public boolean isFirst() throws SQLException {
        verifyStatus();

        return index == 0;
    }

    /**
     * If the current row is the last one.
     *
     * @return <code>true</code> if the current row is the last one.
     * @throws SQLException in case of failures.
     */
    public boolean isLast() throws SQLException {
        verifyStatus();

        return index == values.size() - 1;
    }

    /**
     * Go to last row.
     *
     * @return <code>true</code> if success.
     * @throws SQLException in case of failures.
     */
    public boolean last() throws SQLException {
        verifyStatus();

        if (values.isEmpty()) {
            return false;
        }

        index = values.size() - 1;
        updateCurrentRow();
        return true;
    }

    /**
     * Go to position relative to the current.
     *
     * @param rows the position relative to the current.
     * @return <code>true</code> in case of success.
     * @throws SQLException in case of failures.
     */
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
        this.currentRow = null;
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

    /**
     * Go to the next row.
     *
     * @return <code>true</code> in case of failures.
     */
    public boolean next() {
        if (fetchDirection == ResultSet.FETCH_FORWARD) {
            return moveNext();
        } else {
            return movePrevious();
        }
    }

    /**
     * Go to previous row.
     *
     * @return in case of failures.
     */
    public boolean previous() {
        if (fetchDirection == ResultSet.FETCH_FORWARD) {
            return movePrevious();
        } else {
            return moveNext();
        }
    }

    /**
     * Go to next row (forward mode only).
     *
     * @return <code>true</code> in case of success.
     */
    private boolean moveNext() {
        if (index < values.size()) {
            index++;
        }

        updateCurrentRow();
        return index != values.size();
    }

    /**
     * Go to previous row (forward mode only).
     *
     * @return <code>true</code> in case of success.
     */
    private boolean movePrevious() {
        if (index != -1) {
            index--;
        }

        updateCurrentRow();
        return index != -1;
    }

    /**
     * If the navigation is already closed.
     *
     * @return in case of failures.
     */
    public boolean isClosed() {
        return closed;
    }

    @Override
    public String toString() {
        return "rows: " + values.size() + " current row: " + index;
    }
}
