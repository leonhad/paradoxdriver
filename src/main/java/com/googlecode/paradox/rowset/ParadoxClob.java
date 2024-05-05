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

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Objects;

/**
 * CLOB for paradox file (MB).
 *
 * @since 1.2
 */
public final class ParadoxClob implements Clob {

    /**
     * The clob data.
     */
    private String value;

    /**
     * Create a new instance.
     *
     * @param value the clob value.
     */
    public ParadoxClob(final String value) {
        this.value = value;
    }

    @Override
    public void free() {
        // Unused
    }

    @Override
    public InputStream getAsciiStream() {
        if (this.value != null) {
            return new ByteArrayInputStream(this.value.getBytes(StandardCharsets.UTF_8));
        }

        return null;
    }

    @Override
    public Reader getCharacterStream() {
        return new StringReader(value);
    }

    @Override
    public Reader getCharacterStream(final long pos, final long length) throws SQLException {
        if (pos < 1) {
            throw new ParadoxException(ParadoxException.Error.INVALID_POSITION_SPECIFIED);
        } else if (length < 0) {
            throw new ParadoxException(ParadoxException.Error.INVALID_LENGTH_SPECIFIED);
        }

        final int endPos = (int) (pos - 1 + length);

        if (pos > this.value.length()) {
            return new StringReader("");
        } else if (endPos > this.value.length()) {
            return new StringReader(this.value.substring((int) pos - 1));
        }

        return new StringReader(this.value.substring((int) pos - 1, endPos));
    }

    @Override
    public String getSubString(final long pos, final int length) throws SQLException {
        if (pos < 1) {
            throw new ParadoxException(ParadoxException.Error.INVALID_POSITION_SPECIFIED);
        } else if (length < 0) {
            throw new ParadoxException(ParadoxException.Error.INVALID_LENGTH_SPECIFIED);
        }

        final int endPos = (int) (pos - 1 + length);

        if (pos > this.value.length()) {
            return "";
        } else if (endPos > this.value.length()) {
            return this.value.substring((int) pos - 1);
        }

        return this.value.substring((int) pos - 1, endPos);
    }

    @Override
    public long length() {
        return value.length();
    }

    @Override
    public long position(final Clob search, final long start) {
        return 0;
    }

    @Override
    public long position(final String search, final long start) {
        return 0;
    }

    @Override
    public OutputStream setAsciiStream(final long pos) {
        return null;
    }

    @Override
    public Writer setCharacterStream(final long pos) {
        return null;
    }

    @Override
    public int setString(final long pos, final String str) {
        return 0;
    }

    @Override
    public int setString(final long pos, final String str, final int offset, final int len) {
        return 0;
    }

    @Override
    public void truncate(final long length) throws SQLException {
        if (length > this.value.length()) {
            throw new ParadoxException(ParadoxException.Error.INVALID_LENGTH_SPECIFIED);
        }
        if (length == 0) {
            this.value = "";
        } else {
            this.value = this.value.substring(0, (int) length);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ParadoxClob that = (ParadoxClob) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
