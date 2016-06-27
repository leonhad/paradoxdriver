/*
 * PrimaryKeyData.java
 *
 * 03/14/2009
 * Copyright (C) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.data;

import static java.nio.ByteBuffer.allocate;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Reads primary key data fields.
 *
 * @author Leonardo Alves da Costa
 * @since 1.0
 * @version 1.1
 */
public final class PrimaryKeyData {
    
    /**
     * Utility class.
     */
    private PrimaryKeyData() {
        // Utility class.
    }

    /**
     * Gets the primary keys from the database file.
     *
     * @param conn
     *            the database connection.
     * @param table
     *            the tables primary key.
     * @return the primary keys.
     * @throws SQLException
     *             in case of load failures.
     */
    public static ParadoxPK getPrimaryKey(final ParadoxConnection conn, final ParadoxTable table) throws SQLException {
        final String name = table.getName() + ".PX";

        final File[] fileList = conn.getDir().listFiles(new PrimaryKeyFilter(name));
        if (fileList != null) {
            for (final File file : fileList) {
                final ParadoxPK key;
                try {
                    key = loadPKHeader(file);
                } catch (final IOException ex) {
                    throw new SQLException("Error loading Paradox tables.", ex);
                }
                if (key.isValid()) {
                    return key;
                }
            }
        }
        return null;
    }

    /**
     * Gets all primary keys from the database.
     *
     * @param conn
     *            the database connection.
     * @return a list of primary keys.
     * @throws SQLException
     *             in case of load failures.
     */
    public static List<ParadoxPK> listPrimaryKeys(final ParadoxConnection conn) throws SQLException {
        final ArrayList<ParadoxPK> keys = new ArrayList<>();
        final File[] fileList = conn.getDir().listFiles(new PrimaryKeyFilter());
        if (fileList != null) {
            for (final File file : fileList) {
                final ParadoxPK key;
                try {
                    key = loadPKHeader(file);
                } catch (final IOException ex) {
                    throw new SQLException("Error loading Paradox tables.", ex);
                }
                if (key.isValid()) {
                    keys.add(key);
                }
            }
        }
        return keys;
    }

    /**
     * Gets the {@link ParadoxPK} from a PK file.
     *
     * @param file
     *            the file to read.
     * @return the {@link ParadoxPK}.
     * @throws IOException
     *             in case of I/O exceptions.
     */
    private static ParadoxPK loadPKHeader(final File file) throws IOException {
        final ByteBuffer buffer = allocate(2048);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        FileChannel channel = null;
        final FileInputStream fs = new FileInputStream(file);
        final ParadoxPK pk = new ParadoxPK();

        try {
            channel = fs.getChannel();
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

            buffer.position(0x38);
            pk.setWriteProtected(buffer.get());
            pk.setVersionId(buffer.get());
        } finally {
            if (channel != null) {
                channel.close();
            }
            fs.close();
        }
        return pk;
    }
}
