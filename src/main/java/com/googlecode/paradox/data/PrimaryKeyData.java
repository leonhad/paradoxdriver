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

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.metadata.ParadoxPK;
import com.googlecode.paradox.metadata.ParadoxTable;
import com.googlecode.paradox.utils.filefilters.PrimaryKeyFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.sql.SQLException;

import static com.googlecode.paradox.utils.Utils.flip;
import static com.googlecode.paradox.utils.Utils.position;

/**
 * Reads primary key data fields.
 *
 * @author Leonardo Alves da Costa
 * @version 1.1
 * @since 1.0
 */
public final class PrimaryKeyData extends AbstractParadoxData {

    /**
     * Utility class.
     */
    private PrimaryKeyData() {
        // Utility class.
    }

    /**
     * Gets the primary keys from the database file.
     *
     * @param currentSchema the current schema file.
     * @param table         the tables primary key.
     * @param connection    the database connection.
     * @return the primary keys.
     * @throws SQLException in case of load failures.
     */
    public static ParadoxPK getPrimaryKey(final File currentSchema, final ParadoxTable table,
            final ParadoxConnection connection) throws SQLException {
        final String name = table.getName() + ".PX";

        final File[] fileList = currentSchema.listFiles(new PrimaryKeyFilter(name));
        if ((fileList != null) && (fileList.length > 0)) {
            try {
                return PrimaryKeyData.loadPKHeader(fileList[0], connection);
            } catch (final IOException ex) {
                throw new SQLException("Error loading Paradox tables.", ex);
            }
        }
        return null;
    }

    /**
     * Gets the {@link ParadoxPK} from a PK file.
     *
     * @param file       the file to read.
     * @param connection the database connection.
     * @return the {@link ParadoxPK}.
     * @throws IOException in case of I/O exceptions.
     */
    private static ParadoxPK loadPKHeader(final File file, final ParadoxConnection connection) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(2048);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        final ParadoxPK pk = new ParadoxPK(connection);

        try (final FileInputStream fs = new FileInputStream(file); FileChannel channel = fs.getChannel()) {
            channel.read(buffer);
            flip(buffer);

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

            position(buffer, 0x15);
            pk.setIndexFieldNumber(buffer.get());

            position(buffer, 0x21);
            pk.setFieldCount(buffer.get());

            position(buffer, 0x38);
            pk.setWriteProtected(buffer.get());
            pk.setVersionId(buffer.get());
        }
        return pk;
    }
}
