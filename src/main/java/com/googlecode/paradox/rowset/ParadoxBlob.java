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
import com.googlecode.paradox.exceptions.ParadoxNotSupportedException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * BLOB for paradox file (MB).
 *
 * @version 1.2
 * @since 1.5.0
 */
public final class ParadoxBlob implements Blob {

    /**
     * Empty value used in free method.
     */
    private static final byte[] EMPTY_BLOB = new byte[0];

    /**
     * The clob data.
     */
    private byte[] value;

    /**
     * Create a new instance.
     *
     * @param value the blob value.
     */
    public ParadoxBlob(final byte[] value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void free() {
        this.value = EMPTY_BLOB;
    }

    @Override
    public InputStream getBinaryStream(long pos, long length) {
        return new ByteArrayInputStream(getBytes(pos, (int) length));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public long length() {
        return this.value.length;
    }

    @Override
    public byte[] getBytes(long pos, int length) {
        return Arrays.copyOfRange(this.value, (int) pos - 1, length);
    }

    @Override
    public InputStream getBinaryStream() {
        return new ByteArrayInputStream(this.value);
    }

    @Override
    public long position(byte[] pattern, long start) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public long position(Blob pattern, long start) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public int setBytes(long pos, byte[] bytes) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public int setBytes(long pos, byte[] bytes, int offset, int len) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    @Override
    public OutputStream setBinaryStream(long pos) throws ParadoxNotSupportedException {
        throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.OPERATION_NOT_SUPPORTED);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void truncate(final long length) throws SQLException {
        if (length > this.value.length) {
            throw new ParadoxException(ParadoxException.Error.INVALID_LENGTH_SPECIFIED);
        }
        if (length == 0) {
            this.value = EMPTY_BLOB;
        } else {
            this.value = Arrays.copyOf(this.value, (int) length);
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
        ParadoxBlob that = (ParadoxBlob) o;
        return Arrays.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }
}
