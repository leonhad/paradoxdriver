package com.googlecode.paradox.data;

import static java.nio.ByteBuffer.allocate;
import static java.nio.charset.Charset.forName;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.ArrayList;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.metadata.ParadoxIndex;
import com.googlecode.paradox.utils.filefilters.SecondaryIndexFilter;


public class IndexData {

    public static ArrayList<ParadoxIndex> listIndexes(final ParadoxConnection conn, final String tableName) throws SQLException {
        final ArrayList<ParadoxIndex> indexes = new ArrayList<ParadoxIndex>();
        final String indexNamePattern = tableName.substring(0, tableName.lastIndexOf('.')) + ".X??";
        final File[] fileList = conn.getDir().listFiles(new SecondaryIndexFilter(indexNamePattern));
        for (final File file : fileList) {
            final ParadoxIndex index;
            try {
                index = loadIndexHeader(file);
            } catch (final IOException ex) {
                throw new SQLException("Error loading Paradox index.", ex);
            }
            if (index.isValid()) {
                indexes.add(index);
            }
        }
        return indexes;
    }

    private static ParadoxIndex loadIndexHeader(final File file) throws IOException, SQLException {
        final ByteBuffer buffer = allocate(2048);
        
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        FileChannel channel = null;
        final FileInputStream fs = new FileInputStream(file);
        final ParadoxIndex index = new ParadoxIndex(file, file.getName());

        try {
            channel = fs.getChannel();
            channel.read(buffer);
            buffer.flip();

            index.setRecordSize(buffer.getShort());
            index.setHeaderSize(buffer.getShort());
            index.setType(buffer.get());
            index.setBlockSize(buffer.get());
            index.setRowCount(buffer.getInt());
            index.setUsedBlocks(buffer.getShort());
            index.setTotalBlocks(buffer.getShort());
            index.setFirstBlock(buffer.getShort());
            index.setLastBlock(buffer.getShort());

            buffer.position(0x21);
            index.setFieldCount(buffer.getShort());
            index.setPrimaryFieldCount(buffer.getShort());

            buffer.position(0x38);
            index.setWriteProtected(buffer.get());
            index.setVersionId(buffer.get());

            buffer.position(0x49);
            index.setAutoIncrementValue(buffer.getInt());
            index.setFirstFreeBlock(buffer.getShort());

            buffer.position(0x55);
            index.setReferencialIntegrity(buffer.get());

            if (index.getVersionId() > 4) {
                // Set the charset
                buffer.position(0x6A);
                index.setCharset(forName("cp" + buffer.getShort()));

                buffer.position(0x78);
            } else {
                buffer.position(0x58);
            }

            final ArrayList<ParadoxField> fields = new ArrayList<ParadoxField>();
            for (int loop = 0; loop < index.getFieldCount(); loop++) {
                final ParadoxField field = new ParadoxField(loop+1);
                field.setType(buffer.get());
                field.setSize(buffer.get());
                fields.add(field);
            }

            if (index.getVersionId() > 4) {
                if (index.getVersionId() == 0xC) {
                    buffer.position(0x78 + 261 + 4 + 6 * fields.size());
                } else {
                    buffer.position(0x78 + 83 + 6 * fields.size());
                }
            } else {
                buffer.position(0x58 + 83 + 6 * fields.size());
            }

            for (int loop = 0; loop < index.getFieldCount(); loop++) {
                final ByteBuffer name = allocate(261);

                while (true) {
                    final byte c = buffer.get();
                    if (c == 0) {
                        break;
                    }
                    name.put(c);
                }
                name.flip();
                fields.get(loop).setName(index.getCharset().decode(name).toString());
            }
            index.setFields(fields);

            final ArrayList<Short> fieldsOrder = new ArrayList<Short>();
            for (int loop = 0; loop < index.getFieldCount(); loop++) {
                fieldsOrder.add(buffer.getShort());
            }
            index.setFieldsOrder(fieldsOrder);

            // Sort Order ID
            final ByteBuffer sortOrderID = allocate(26);
            while (true) {
                final byte c = buffer.get();
                if (c == 0) {
                    break;
                }
                sortOrderID.put(c);
            }
            sortOrderID.flip();
            index.setSortOrderID(index.getCharset().decode(sortOrderID).toString());

            // Index name
            final ByteBuffer name = allocate(26);
            while (true) {
                final byte c = buffer.get();
                if (c == 0) {
                    break;
                }
                name.put(c);
            }
            name.flip();
            String tempName = index.getCharset().decode(name).toString();
            if (tempName.length() != 0) {
                index.setName(tempName);
            }

        } finally {
            if (channel != null) {
                channel.close();
            }
            fs.close();
        }
        return index;
    }

    private IndexData() {
    }
}
