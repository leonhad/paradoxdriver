package org.paradox.data;

import org.paradox.ParadoxConnection;
import org.paradox.utils.filefilters.PrimaryKeyFilter;
import org.paradox.metadata.ParadoxPK;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import static java.nio.ByteBuffer.allocate;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import org.paradox.metadata.ParadoxTable;


public class PrimaryKeyData {
    public static ParadoxPK getPrimaryKey(final ParadoxConnection conn, final ParadoxTable table) throws SQLException {
        final String name = table.getName().substring(0, table.getName().length() - 3) + ".PX";

        final File[] fileList = conn.getDir().listFiles(new PrimaryKeyFilter(name));
        for (final File file : fileList) {
            final ParadoxPK key;
            try {
                key = loadPKHeader(conn, file);
            } catch (final IOException ex) {
                throw new SQLException("Error loading Paradox tables.", ex);
            }
            if (key.isValid()) {
                return key;
            }
        }
        return null;
    }

    public static ArrayList<ParadoxPK> listPrimaryKeys(final ParadoxConnection conn) throws SQLException {
        final ArrayList<ParadoxPK> keys = new ArrayList<ParadoxPK>();
        final File[] fileList = conn.getDir().listFiles(new PrimaryKeyFilter());
        for (final File file : fileList) {
            final ParadoxPK key;
            try {
                key = loadPKHeader(conn, file);
            } catch (final IOException ex) {
                throw new SQLException("Error loading Paradox tables.", ex);
            }
            if (key.isValid()) {
                keys.add(key);
            }
        }
        return keys;
    }

    private static ParadoxPK loadPKHeader(final ParadoxConnection conn, final File file) throws IOException {
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

    private PrimaryKeyData() {
    }
}
