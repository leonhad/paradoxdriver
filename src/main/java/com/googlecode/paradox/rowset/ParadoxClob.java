/*
 * ParadoxClob.java 12/22/2014 Copyright (C) 2014 Leonardo Alves da Costa This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.rowset;

import com.googlecode.paradox.data.table.value.ClobDescriptor;
import com.googlecode.paradox.metadata.BlobTable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.sql.Clob;
import java.sql.SQLException;

/**
 * CLOB for paradox file (MB).
 *
 * @author Leonardo Alves da Costa
 * @author Andre Mikhaylov
 * @version 1.2
 * @since 1.2
 */
public final class ParadoxClob implements Clob {

    /**
     * The default clob charset.
     */
    private static final Charset DEFAULT_CHARSET = Charset.forName("cp1251");

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
    public ParadoxClob(final ClobDescriptor descriptor) {
        this.offset = -1;
        // If MB_Offset = 0 then the entire blob is contained in the leader.
        if (descriptor.getOffset() == 0) {
            if (descriptor.getLeader() != null) {
                this.value = descriptor.getLeaderAsStr().getBytes();
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

    /**
     * {@inheritDoc}.
     */
    @Override
    public InputStream getAsciiStream() throws SQLException {
        this.parse();
        this.isValid();
        return new ByteArrayInputStream(this.value);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Reader getCharacterStream() throws SQLException {
        this.parse();
        this.isValid();
        return new InputStreamReader(new ByteArrayInputStream(this.value), ParadoxClob.DEFAULT_CHARSET);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Reader getCharacterStream(final long pos, final long length) throws SQLException {
        this.parse();
        this.isValid();
        if ((pos < 1) || (pos > this.length)) {
            throw new SQLException("Invalid position in Clob object set");
        } else if (((pos - 1) + length) > this.length) {
            throw new SQLException("Invalid position and substring length");
        } else if (length <= 0) {
            throw new SQLException("Invalid length specified");
        }
        return new InputStreamReader(new ByteArrayInputStream(this.value, (int) pos - 1, (int) length),
                ParadoxClob.DEFAULT_CHARSET);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getSubString(final long pos, final int length) throws SQLException {
        this.parse();
        this.isValid();
        if ((pos < 1) || (pos > this.length)) {
            throw new SQLException("Invalid position '" + pos + "' in Clob object set");
        } else if (((pos - 1) + length) > this.length) {
            throw new SQLException("Invalid position and substring length");
        } else if (length <= 0) {
            throw new SQLException("Invalid length specified");
        }
        return new String(this.value, (int) pos - 1, length, ParadoxClob.DEFAULT_CHARSET);
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

    /**
     * {@inheritDoc}.
     */
    @Override
    public long position(final Clob search, final long start) {
        return 0;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public long position(final String search, final long start) {
        return 0;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public OutputStream setAsciiStream(final long pos) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Writer setCharacterStream(final long pos) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int setString(final long pos, final String str) {
        return 0;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int setString(final long pos, final String str, final int offset, final int len) {
        return 0;
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
            this.value = this.getSubString(1, (int) length).getBytes(ParadoxClob.DEFAULT_CHARSET);
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
