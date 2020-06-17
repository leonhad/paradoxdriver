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
package com.googlecode.paradox.data.table.value;

import com.googlecode.paradox.metadata.BlobTable;
import com.googlecode.paradox.rowset.ParadoxClob;
import com.googlecode.paradox.utils.Utils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.sql.SQLException;

/**
 * Describe a CLOB file.
 *
 * @author Leonardo Alves da Costa
 * @author Andre Mikhaylov
 * @author Michael Berry
 * @version 1.1
 * @since 1.2
 */
public final class ClobDescriptor extends BlobDescriptor {
    
    /**
     * The clob charset.
     */
    private final Charset charset;

    /**
     * Creates a new instance.
     *
     * @param file blob file reference.
     * @param charset the charset of this descriptor
     */
    public ClobDescriptor(final BlobTable file, final Charset charset) {
        super(file);
        this.charset = charset;
    }
    
    /**
     * Get the leader as a string, formatted by the charset of the descriptor.
     * @return the leader as a string.
     */
    public String getLeaderAsStr() {
        return Utils.parseString(ByteBuffer.wrap(getLeader()), charset);
    }

    /**
     * Retrieves the string that this descriptor represents from the MB file.
     * @return the full string associated with this descriptor.
     * @throws SQLException if something went wrong.
     */
    public String getClobString() throws SQLException {
        ParadoxClob clob = new ParadoxClob(this);
        try (InputStream is = clob.getAsciiStream()) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[2_048];
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            byte[] byteArray = buffer.toByteArray();
            return new String(byteArray, charset);
        } catch (IOException ex) {
            throw new SQLException("Unable to read field", ex);
        }
    }
}
