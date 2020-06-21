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

import com.googlecode.paradox.data.table.value.BlobDescriptor;
import com.googlecode.paradox.metadata.BlobTable;

import java.io.*;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * BLOB for paradox file (MB).
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.5.0
 */
public final class ParadoxBlob implements Blob {

    /**
     * The clob table.
     */
    private BlobTable blob;

    /**
     * The clob length.
     */
    private long length;

    /**
     * The clob offset.
     */
    private long offset;

    /**
     * If this clob is already parsed.
     */
    private boolean parsed;

    /**
     * The clob data.
     */
    private byte[] value;

    /**
     * Create a new instance.
     *
     * @param descriptor the blob descriptor.
     */
    public ParadoxBlob(final BlobDescriptor descriptor) {
        this.offset = -1;
        // If MB_Offset = 0 then the entire blob is contained in the leader.
        if (descriptor.getOffset() == 0) {
            if (descriptor.getLeader() != null) {
                this.value = descriptor.getLeader();
                this.length = this.value.length;
            }
            this.parsed = true;
        } else {
            this.offset = descriptor.getOffset();
            this.blob = descriptor.getFile();
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void free() throws SQLException {
        if (this.blob != null) {
            this.blob.close();
        }
    }

    @Override
    public InputStream getBinaryStream(long pos, long length) throws SQLException {
        this.parse();
        this.isValid();
        return new ByteArrayInputStream(this.value);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public long length() throws SQLException {
        this.parse();
        this.isValid();
        return this.length;
    }

    @Override
    public byte[] getBytes(long pos, int length) throws SQLException {
        this.parse();
        this.isValid();
        return this.value;
    }

    @Override
    public InputStream getBinaryStream() throws SQLException {
        this.parse();
        this.isValid();
        return new ByteArrayInputStream(this.value);
    }

    @Override
    public long position(byte[] pattern, long start) throws SQLException {
        return 0;
    }

    @Override
    public long position(Blob pattern, long start) throws SQLException {
        return 0;
    }

    @Override
    public int setBytes(long pos, byte[] bytes) throws SQLException {
        return 0;
    }

    @Override
    public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
        return 0;
    }

    @Override
    public OutputStream setBinaryStream(long pos) throws SQLException {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void truncate(final long length) throws SQLException {
        this.parse();
        this.isValid();
        if (length > this.length) {
            throw new SQLException("Length more than what can be truncated");
        }
        if (length == 0) {
            this.value = new byte[]{};
        } else {
            this.value = Arrays.copyOf(this.value, (int) length);
        }
        this.length = this.value.length;
    }

    /**
     * Check for the blob validate.
     *
     * @throws SQLException in case of invalid descriptor.
     */
    private void isValid() throws SQLException {
        if (!this.parsed && (this.blob == null)) {
            throw new SQLException("Invalid CLOB descriptor.");
        }
    }

    /**
     * Parse the blob.
     *
     * @throws SQLException in case of parse errors.
     */
    private void parse() throws SQLException {
        if (!this.parsed) {
            this.value = this.blob.read(this.offset);
            this.parsed = this.blob.isParsed();
            this.length = this.value.length;
        }
    }
}
