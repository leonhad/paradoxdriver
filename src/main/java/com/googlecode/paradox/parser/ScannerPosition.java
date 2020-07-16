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
package com.googlecode.paradox.parser;

/**
 * Stores the scanner position.
 *
 * @version 1.1
 * @since 1.6.0
 */
public class ScannerPosition {

    /**
     * The SQL current column.
     */
    private int column = 1;

    /**
     * The SQL current line.
     */
    private int line = 1;

    /**
     * Creates a new instance.
     */
    public ScannerPosition() {
        super();
    }

    public void add(final char c) {
        if (c == '\n') {
            column = 1;
            line++;
        } else {
            column++;
        }
    }

    /**
     * Gets the current column.
     *
     * @return the current column.
     */
    public int getColumn() {
        return column;
    }

    /**
     * Gets the current line.
     *
     * @return the current line.
     */
    public int getLine() {
        return line;
    }

    public String getLocation() {
        return String.format("line %d, column %d", line, column);
    }

    /**
     * Gets the last position (column - 1).
     *
     * @return the last position (column - 1).
     */
    public ScannerPosition lastPosition() {
        final ScannerPosition last = new ScannerPosition();
        last.column = column - 1;
        last.line = line;
        return last;
    }
}
