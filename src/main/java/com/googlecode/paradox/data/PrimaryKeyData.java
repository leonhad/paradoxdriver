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
package com.googlecode.paradox.data;

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.data.filefilters.PrimaryKeyFilter;
import com.googlecode.paradox.exceptions.ParadoxDataException;
import com.googlecode.paradox.metadata.paradox.ParadoxDataFile;
import com.googlecode.paradox.metadata.paradox.ParadoxPK;
import com.googlecode.paradox.utils.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.sql.SQLException;

/**
 * Reads primary key data fields.
 *
 * @version 1.3
 * @since 1.0
 */
public final class PrimaryKeyData extends ParadoxData {

    /**
     * Utility class.
     */
    private PrimaryKeyData() {
        // Utility class.
    }

    /**
     * Gets the primary keys from the database file.
     *
     * @param currentSchema  the current schema file.
     * @param table          the tables primary key.
     * @param connectionInfo the connection information.
     * @return the primary keys.
     * @throws SQLException in case of load failures.
     */
    public static ParadoxPK getPrimaryKey(final File currentSchema, final ParadoxDataFile table,
                                          final ConnectionInfo connectionInfo) throws SQLException {
        final String name = table.getName() + ".PX";

        final File[] fileList = currentSchema.listFiles(new PrimaryKeyFilter(connectionInfo.getLocale(), name));
        if ((fileList != null) && (fileList.length > 0)) {
            try {
                return PrimaryKeyData.loadPKHeader(fileList[0], connectionInfo);
            } catch (final IOException ex) {
                throw new ParadoxDataException(ParadoxDataException.Error.ERROR_LOADING_DATA, ex);
            }
        }
        return null;
    }

    /**
     * Gets the {@link ParadoxPK} from a PK file.
     *
     * @param file           the file to read.
     * @param connectionInfo the connection information.
     * @return the {@link ParadoxPK}.
     * @throws IOException in case of I/O exceptions.
     */
    private static ParadoxPK loadPKHeader(final File file, final ConnectionInfo connectionInfo) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(Constants.MAX_BUFFER_SIZE);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        final ParadoxPK pk = new ParadoxPK(connectionInfo);

        try (final FileInputStream fs = new FileInputStream(file); final FileChannel channel = fs.getChannel()) {
            channel.read(buffer);
            buffer.flip();

            pk.setName(file.getName());
            pk.setRecordSize(buffer.getShort());
            pk.setHeaderSize(buffer.getShort());
            pk.setType(buffer.get());
            pk.setBlockSize(buffer.get());
            pk.setRowCount(buffer.getInt());
            pk.setUsedBlocks(buffer.getShort());
            pk.setTotalBlocks(buffer.getShort());
            pk.setFirstBlock(buffer.getShort());
            pk.setLastBlock(buffer.getShort());

            buffer.position(0x15);
            pk.setIndexFieldNumber(buffer.get());

            buffer.position(0x21);
            pk.setFieldCount(buffer.get());

            buffer.position(0x38);
            pk.setWriteProtected(buffer.get());
            pk.setVersionId(buffer.get());
        }
        return pk;
    }
}
