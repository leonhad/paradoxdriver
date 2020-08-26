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
import com.googlecode.paradox.exceptions.DataError;
import com.googlecode.paradox.exceptions.ParadoxDataException;
import com.googlecode.paradox.metadata.Table;
import com.googlecode.paradox.metadata.paradox.*;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.utils.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles the paradox files (structure).
 *
 * @version 1.3
 * @since 1.4.0
 */
@SuppressWarnings({"i18n-java:V1008", "java:S109", "i18n-java:V1004"})
public class ParadoxData {

    /**
     * Minimum paradox file version.
     */
    protected static final int MINIMUM_VERSION = 4;
    private static final int CHARSET_DEFAULT = 437;
    private static final Charset CP437 = Charset.forName("cp437");
    private static final Map<Integer, Charset> CHARSET_TABLE = new HashMap<>();

    static {
        CHARSET_TABLE.put(437, CP437);
        CHARSET_TABLE.put(850, Charset.forName("cp850"));
        CHARSET_TABLE.put(852, Charset.forName("cp852"));
        CHARSET_TABLE.put(861, Charset.forName("cp861"));
        CHARSET_TABLE.put(862, Charset.forName("cp862"));
        CHARSET_TABLE.put(863, Charset.forName("cp863"));
        CHARSET_TABLE.put(865, Charset.forName("cp865"));
        CHARSET_TABLE.put(866, Charset.forName("cp866"));
        CHARSET_TABLE.put(867, Charset.forName("cp862"));
        CHARSET_TABLE.put(932, Charset.forName("windows-31j"));
        CHARSET_TABLE.put(936, Charset.forName("cp936"));
        CHARSET_TABLE.put(0x4e3, Charset.forName("cp1251"));
        CHARSET_TABLE.put(0x4e4, Charset.forName("cp1252"));
    }

    /**
     * Creates a new instance.
     */
    protected ParadoxData() {
        // Unused.
    }

    protected static void checkDBEncryption(final ByteBuffer buffer, final ParadoxDataFile dataFile, int blockSize,
                                            long blockNumber) {
        if (dataFile.isEncrypted()) {
            byte[] b = buffer.array();
            EncryptedData.decryptDBBlock(b, dataFile.getEncryptedData(), blockSize, blockNumber);
        }
    }

    /**
     * Parse and handle the version ID.
     *
     * @param buffer         the buffer to parse.
     * @param dataFile       the paradox index.
     * @param connectionInfo the connection information.
     */
    protected static void parseVersionID(final ByteBuffer buffer, final ParadoxDataFile dataFile,
                                         final ConnectionInfo connectionInfo) {
        if (dataFile.getVersionId() > ParadoxData.MINIMUM_VERSION) {
            // Set the charset.
            buffer.position(0x6A);
            int cp = buffer.getShort();

            // Force charset if have one.
            if (dataFile.getCharset() == null) {
                dataFile.setCharset(CHARSET_TABLE.getOrDefault(cp, CP437));
                if (CHARSET_TABLE.get(cp) == null) {
                    connectionInfo.addWarning("Charset " + cp + " not found.");
                }
            }

            buffer.position(0x78);
        } else {
            buffer.position(0x58);

            if (dataFile.getCharset() == null) {
                dataFile.setCharset(CHARSET_TABLE.get(CHARSET_DEFAULT));
            }
        }
    }

    private static ParadoxDataFile newInstance(final File file, final byte type, final ConnectionInfo connectionInfo)
            throws ParadoxDataException {
        switch (type) {
            case 0x00:
                // Indexed table.
            case 0x02:
                // Non indexed table.
                return new ParadoxTable(file, connectionInfo);
            case 0x01:
                return new ParadoxPK(file, connectionInfo);
            case 0x03:
                // Non-incrementing secondary index .Xnn file.
            case 0x04:
                // Secondary index .Ynn file (inc or non-inc).
            case 0x05:
                // Incrementing secondary index .Xnn file.
            case 0x06:
                // Non-incrementing secondary index .XGn file.
            case 0x07:
                // Secondary index .YGn file (inc or non inc).
            case 0x08:
                // Incrementing secondary index .XGn file.
                return new ParadoxIndex(file, connectionInfo);
            default:
                throw new ParadoxDataException(DataError.UNSUPPORTED_FILE_TYPE, type);
        }
    }

    /**
     * Gets the header from a file.
     *
     * @param file           the {@link File} to read.
     * @param connectionInfo the connection information.
     * @param <T>            the desired data type.
     * @return the data file.
     * @throws SQLException in case of reading errors.
     */
    protected static <T extends ParadoxDataFile> T loadHeader(final File file, final ConnectionInfo connectionInfo)
            throws SQLException {
        final ByteBuffer buffer = ByteBuffer.allocate(Constants.MAX_BUFFER_SIZE);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        try (FileInputStream fs = new FileInputStream(file); FileChannel channel = fs.getChannel()) {
            channel.read(buffer);
            buffer.flip();

            int recordSize = buffer.getShort() & 0xFFFF;
            int headerSize = buffer.getShort() & 0xFFFF;
            byte type = buffer.get();

            @SuppressWarnings("unchecked")
            T data = (T) newInstance(file, type, connectionInfo);
            data.setRecordSize(recordSize);
            data.setHeaderSize(headerSize);
            data.setType(type);
            data.setBlockSize(buffer.get());
            data.setRowCount(buffer.getInt());
            data.setUsedBlocks(buffer.getShort());
            data.setTotalBlocks(buffer.getShort());
            data.setFirstBlock(buffer.getShort());
            data.setLastBlock(buffer.getShort());

            buffer.position(0x21);
            data.setFieldCount(buffer.getShort());
            data.setPrimaryFieldCount(buffer.getShort());

            // Check for encrypted file.
            buffer.position(0x25);
            long value = buffer.getInt();

            buffer.position(0x38);
            data.setWriteProtected(buffer.get() != 0);
            data.setVersionId(buffer.get());

            // Paradox version 4.x and up.
            if (value == 0xFF00_FF00 && data.getVersionId() > Constants.PARADOX_VERSION_4) {
                buffer.position(0x5c);
                value = buffer.getInt();
            }

            data.setEncryptedData(value);

            buffer.position(0x49);
            data.setAutoIncrementValue(buffer.getInt());
            data.setFirstFreeBlock(buffer.getShort());

            buffer.position(0x55);
            data.setReferentialIntegrity(buffer.get());

            parseVersionID(buffer, data, connectionInfo);

            final ParadoxField[] fields = parseTableFields(data, buffer);

            // Only for DB files and Xnn files.
            if (data instanceof ParadoxTable || data instanceof ParadoxIndex) {
                fixPositionByVersion(data, buffer, fields.length);
                parseFieldsName(data, buffer, fields);

                parseTableFieldsOrder(data, buffer);
            }

            if (data instanceof ParadoxIndex) {
                parseSortID(buffer, data);
                parseIndexName(buffer, data);
            }

            return data;
        } catch (final BufferUnderflowException | IOException e) {
            throw new ParadoxDataException(DataError.ERROR_LOADING_DATA, e);
        }

    }

    /**
     * Fix the buffer position based on file version ID.
     *
     * @param dataFile   the Paradox data file.
     * @param buffer     the buffer to fix.
     * @param fieldsSize the field list.
     */
    private static void fixPositionByVersion(final ParadoxDataFile dataFile, final ByteBuffer buffer,
                                             final int fieldsSize) {
        // DB and Xnn files.
        if (dataFile.getVersionId() > Constants.PARADOX_VERSION_4) {
            if (dataFile.getVersionId() == 0xC) {
                buffer.position(0x78 + 261 + 4 + (6 * fieldsSize));
            } else {
                buffer.position(0x78 + 83 + (6 * fieldsSize));
            }
        } else {
            buffer.position(0x58 + 83 + (6 * fieldsSize));
        }
    }

    /**
     * Read fields attributes.
     *
     * @param dataFile the Paradox data file.
     * @param buffer   the buffer to read of.
     * @return the Paradox field list.
     */
    private static ParadoxField[] parseTableFields(final ParadoxDataFile dataFile, final ByteBuffer buffer) {
        final ParadoxField[] fields = new ParadoxField[dataFile.getFieldCount()];
        for (int loop = 0; loop < dataFile.getFieldCount(); loop++) {
            final ParadoxField field = new ParadoxField(ParadoxType.valueOfVendor(buffer.get()), loop + 1);
            field.setSize(buffer.get() & 0xFF);
            if (dataFile instanceof Table) {
                field.setTable((Table) dataFile);
            }

            fields[loop] = field;
        }

        return fields;
    }

    /**
     * Parse the Paradox fields name.
     *
     * @param dataFile the Paradox data file.
     * @param buffer   the buffer to read of.
     * @param fields   the field list.
     */
    private static void parseFieldsName(final ParadoxDataFile dataFile, final ByteBuffer buffer,
                                        final ParadoxField[] fields) {
        final ByteBuffer name = ByteBuffer.allocate(261);
        for (int loop = 0; loop < dataFile.getFieldCount(); loop++) {
            name.clear();

            byte c;
            while ((c = buffer.get()) != 0) {
                name.put(c);
            }

            name.flip();
            fields[loop].setName(dataFile.getCharset().decode(name).toString());
        }

        dataFile.setFields(fields);
    }

    /**
     * Parse the fields order.
     *
     * @param dataFile the Paradox data file.
     * @param buffer   the buffer to read of.
     */
    private static void parseTableFieldsOrder(final ParadoxDataFile dataFile, final ByteBuffer buffer) {
        final short[] fieldsOrder = new short[dataFile.getFieldCount()];
        for (int loop = 0; loop < dataFile.getFieldCount(); loop++) {
            fieldsOrder[loop] = buffer.getShort();
        }

        dataFile.setFieldsOrder(fieldsOrder);
    }

    /**
     * Parse the sort order ID.
     *
     * @param buffer the buffer to parse.
     * @param index  the paradox index.
     */
    private static void parseSortID(final ByteBuffer buffer, final ParadoxDataFile index) {
        final ByteBuffer sortOrderID = ByteBuffer.allocate(26);
        while (true) {
            final byte c = buffer.get();
            if (c == 0) {
                break;
            }
            sortOrderID.put(c);
        }

        sortOrderID.flip();
        index.setSortOrderID(index.getCharset().decode(sortOrderID).toString());
    }

    /**
     * Parse the data file name.
     *
     * @param buffer the buffer to parse.
     * @param index  the paradox index.
     */
    private static void parseIndexName(final ByteBuffer buffer, final ParadoxDataFile index) {
        final ByteBuffer name = ByteBuffer.allocate(26);
        while (true) {
            final byte c = buffer.get();
            if (c == 0) {
                break;
            }
            name.put(c);
        }

        name.flip();
        final String tempName = index.getCharset().decode(name).toString();
        if (tempName.length() != 0) {
            index.setName(tempName);
        }
    }
}
