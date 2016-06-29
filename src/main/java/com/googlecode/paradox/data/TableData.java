/*
 * TableData.java
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.data.table.value.ClobDescriptor;
import com.googlecode.paradox.data.table.value.FieldValue;
import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.metadata.ParadoxTable;
import com.googlecode.paradox.utils.Constants;
import com.googlecode.paradox.utils.DateUtils;
import com.googlecode.paradox.utils.SQLStates;
import com.googlecode.paradox.utils.Utils;
import com.googlecode.paradox.utils.filefilters.TableFilter;

/**
 * Utility class for loading table files.
 *
 * @author Leonardo Alves da Costa
 * @since 1.0
 * @version 1.1
 */
public final class TableData {

    /**
     * Utility class.
     */
    private TableData() {
        // Utility class.
    }

    /**
     * Fix the buffer position based on file version ID.
     * 
     * @param table
     *            the Paradox table.
     * @param buffer
     *            the buffer to fix.
     * @param fieldsSize
     *            the field list.
     */
    private static void fixTablePositionByVersion(final ParadoxTable table, final ByteBuffer buffer, final int fieldsSize) {
        if (table.getVersionId() > 4) {
            if (table.getVersionId() == 0xC) {
                buffer.position(0x78 + 261 + 4 + 6 * fieldsSize);
            } else {
                buffer.position(0x78 + 83 + 6 * fieldsSize);
            }
        } else {
            buffer.position(0x58 + 83 + 6 * fieldsSize);
        }
    }

    /**
     * List all database tables.
     *
     * @param conn
     *            the database connection.
     * @return all {@link ParadoxTable}.
     * @throws SQLException
     *             in case of failures.
     */
    public static List<ParadoxTable> listTables(final ParadoxConnection conn) throws SQLException {
        final ArrayList<ParadoxTable> tables = new ArrayList<>();
        final File[] fileList = conn.getDir().listFiles(new TableFilter());
        if (fileList != null) {
            for (final File file : fileList) {
                final ParadoxTable table = TableData.loadTableHeader(file);
                if (table.isValid()) {
                    tables.add(table);
                }
            }
        }
        return tables;
    }

    /**
     * Gets all tables within a pattern.
     *
     * @param conn
     *            the database connection.
     * @param pattern
     *            the pattern.
     * @return the tables filtered.
     * @throws SQLException
     *             in case of failures.
     */
    public static List<ParadoxTable> listTables(final ParadoxConnection conn, final String pattern) throws SQLException {
        final List<ParadoxTable> tables = new ArrayList<>();
        final File[] fileList = conn.getDir().listFiles(new TableFilter(Utils.removeDb(pattern)));
        if (fileList != null) {
            for (final File file : fileList) {
                final ParadoxTable table = TableData.loadTableHeader(file);
                if (table.isValid()) {
                    tables.add(table);
                }
            }
        }
        return tables;
    }

    /**
     * Load the table data from file.
     *
     * @param table
     *            the table to read.
     * @param fields
     *            the fields to read.
     * @return the row values.
     * @throws SQLException
     *             in case of failures.
     */
    public static List<List<FieldValue>> loadData(final ParadoxTable table, final Collection<ParadoxField> fields) throws SQLException {
        final List<List<FieldValue>> ret = new ArrayList<>();

        final int blockSize = table.getBlockSizeBytes();
        final int recordSize = table.getRecordSize();
        final int headerSize = table.getHeaderSize();
        final ByteBuffer buffer = ByteBuffer.allocate(blockSize);

        try (FileInputStream fs = new FileInputStream(table.getFile()); FileChannel channel = fs.getChannel()) {
            if (table.getUsedBlocks() == 0) {
                return ret;
            }
            long nextBlock = table.getFirstBlock();
            do {
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                channel.position(headerSize + (nextBlock - 1) * blockSize);

                buffer.clear();
                channel.read(buffer);
                buffer.flip();

                nextBlock = buffer.getShort();
                // The block number.
                buffer.getShort();

                final int addDataSize = buffer.getShort() & 0xFFFF;
                final int rowsInBlock = addDataSize / recordSize + 1;

                buffer.order(ByteOrder.BIG_ENDIAN);

                for (int loop = 0; loop < rowsInBlock; loop++) {
                    ret.add(readRow(table, fields, buffer));
                }
            } while (nextBlock != 0);
        } catch (final IOException e) {
            throw new SQLException(e.getMessage(), SQLStates.INVALID_IO.getValue(), e);
        }
        return ret;
    }

    /**
     * Gets the table header from a file.
     *
     * @param file
     *            the {@link File} to read.
     * @return the {@link ParadoxTable}.
     * @throws SQLException
     *             in case of reading errors.
     */
    private static ParadoxTable loadTableHeader(final File file) throws SQLException {
        final ParadoxTable table = new ParadoxTable(file, file.getName());
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        try (FileInputStream fs = new FileInputStream(file); FileChannel channel = fs.getChannel()) {
            channel.read(buffer);
            buffer.flip();

            table.setRecordSize(buffer.getShort());
            table.setHeaderSize(buffer.getShort());
            table.setType(buffer.get());
            table.setBlockSize(buffer.get());
            table.setRowCount(buffer.getInt());
            table.setUsedBlocks(buffer.getShort());
            table.setTotalBlocks(buffer.getShort());
            table.setFirstBlock(buffer.getShort());
            table.setLastBlock(buffer.getShort());

            buffer.position(0x21);
            table.setFieldCount(buffer.getShort());
            table.setPrimaryFieldCount(buffer.getShort());

            buffer.position(0x38);
            table.setWriteProtected(buffer.get());
            table.setVersionId(buffer.get());

            buffer.position(0x49);
            table.setAutoIncrementValue(buffer.getInt());
            table.setFirstFreeBlock(buffer.getShort());

            buffer.position(0x55);
            table.setReferentialIntegrity(buffer.get());

            parseTableVersionID(table, buffer);

            final List<ParadoxField> fields = parseTableFields(table, buffer);

            // Restart the buffer with all table header
            channel.position(0);
            buffer = ByteBuffer.allocate(table.getHeaderSize());
            channel.read(buffer);

            fixTablePositionByVersion(table, buffer, fields.size());

            parseTableFieldsName(table, buffer, fields);

            parseTableFieldsOrder(table, buffer);
        } catch (final IOException e) {
            throw new SQLException(e.getMessage(), SQLStates.INVALID_IO.getValue(), e);
        }
        return table;
    }

    /**
     * Parses the auto increment field.
     * 
     * @param buffer
     *            the buffer to read of.
     * @return the field value.
     */
    private static FieldValue parseAutoIncrement(final ByteBuffer buffer) {
        FieldValue fieldValue;
        final int v = buffer.getInt() & 0x0FFFFFFF;
        fieldValue = new FieldValue(v, Types.INTEGER);
        return fieldValue;
    }

    /**
     * Parses a boolean value.
     * 
     * @param buffer
     *            the buffer to read of.
     * @return the field value.
     * @throws SQLException
     *             in case of parse errors.
     */
    private static FieldValue parseBoolean(final ByteBuffer buffer) throws SQLException {
        final byte v = buffer.get();
        if (v == 0) {
            return new FieldValue(Types.BOOLEAN);
        } else if (v == -127) {
            return new FieldValue(Boolean.TRUE, Types.BOOLEAN);
        } else if (v == -128) {
            return new FieldValue(Boolean.FALSE, Types.BOOLEAN);
        } else {
            throw new SQLException("Invalid value " + v + ".");
        }
    }

    /**
     * Parses the date value.
     * 
     * @param buffer
     *            the buffer to read of.
     * @return the field value.
     */
    private static FieldValue parseDate(final ByteBuffer buffer) {
        FieldValue fieldValue;
        final int a1 = 0x000000FF & buffer.get();
        final int a2 = 0x000000FF & buffer.get();
        final int a3 = 0x000000FF & buffer.get();
        final int a4 = 0x000000FF & buffer.get();
        final long days = (a1 << 24 | a2 << 16 | a3 << 8 | a4) & 0x0FFFFFFFL;

        if ((a1 & 0xB0) != 0) {
            final Date date = DateUtils.sdnToGregorian(days + 1721425);
            fieldValue = new FieldValue(date, Types.DATE);
        } else {
            fieldValue = new FieldValue(Types.DATE);
        }
        return fieldValue;
    }

    /**
     * Parses the integer value.
     * 
     * @param buffer
     *            the buffer to read of.
     * @return the field value.
     */
    private static FieldValue parseInt(final ByteBuffer buffer) {
        FieldValue fieldValue;
        final int v = buffer.getInt() & 0x7FFF;
        fieldValue = new FieldValue(v, Types.INTEGER);
        return fieldValue;
    }

    /**
     * Parses a long value.
     * 
     * @param buffer
     *            the buffer to read of.
     * @return the field value.
     */
    private static FieldValue parseLong(final ByteBuffer buffer) {
        FieldValue fieldValue;
        long l = buffer.getInt();
        l &= 0x7FFFFFFF;
        fieldValue = new FieldValue(l, Types.BIGINT);
        return fieldValue;
    }

    /**
     * Parses the memo field.
     * 
     * @param table
     *            the tables memo.
     * @param buffer
     *            the buffer to read of.
     * @param field
     *            the memo field.
     * @return the field value.
     */
    private static FieldValue parseMemo(final ParadoxTable table, final ByteBuffer buffer, final ParadoxField field) {
        FieldValue fieldValue;
        final ByteBuffer value = ByteBuffer.allocate(field.getSize());
        Arrays.fill(value.array(), (byte) 0);

        for (int chars = 0; chars < field.getSize(); chars++) {
            value.put(buffer.get());
        }
        value.flip();

        buffer.order(ByteOrder.LITTLE_ENDIAN);
        final long offset = buffer.getInt();
        final long length = buffer.getInt();
        final short modifier = buffer.getShort();
        buffer.order(ByteOrder.BIG_ENDIAN);

        final ClobDescriptor descriptor = new ClobDescriptor(table.getBlobTable());
        descriptor.setCharset(table.getCharset());
        descriptor.setLeader(TableData.parseString(value, table.getCharset()));
        descriptor.setLength(length);
        descriptor.setOffset(offset);
        descriptor.setModifier(modifier);

        fieldValue = new FieldValue(descriptor, Types.CLOB);
        return fieldValue;
    }

    /**
     * Parses the number value.
     * 
     * @param buffer
     *            the buffer to read of.
     * @return the field value.
     */
    private static FieldValue parseNumber(final ByteBuffer buffer) {
        FieldValue fieldValue;
        final double v = buffer.getDouble() * -1;
        if (Double.compare(Double.NEGATIVE_INFINITY, 1 / v) == 0) {
            fieldValue = new FieldValue(Types.DOUBLE);
        } else {
            fieldValue = new FieldValue(v, Types.DOUBLE);
        }
        return fieldValue;
    }

    /**
     * Convert the Paradox VARCHAR to {@link String}. The paradox fill the entire buffer with zeros at end of VARCHAR
     * literals.
     *
     * @param buffer
     *            VARCHAR Buffer to convert.
     * @param charset
     *            Table charset.
     * @return a formatted {@link String}.
     */
    private static String parseString(final ByteBuffer buffer, final Charset charset) {
        final byte[] value = buffer.array();
        int length = value.length;
        for (; length > 0; length--) {
            // array value starts with zero, not 1
            if (value[length - 1] != 0) {
                break;
            }
        }
        buffer.flip();
        buffer.limit(length);
        return charset.decode(buffer).toString();
    }

    /**
     * Read fields attributes.
     * 
     * @param table
     *            the Paradox table.
     * @param buffer
     *            the buffer to read of.
     * @return the Paradox field list.
     * @throws SQLException
     *             in case of parse errors.
     */
    private static List<ParadoxField> parseTableFields(final ParadoxTable table, final ByteBuffer buffer) throws SQLException {
        final List<ParadoxField> fields = new ArrayList<>();
        for (int loop = 0; loop < table.getFieldCount(); loop++) {
            final ParadoxField field = new ParadoxField(loop + 1);
            field.setType(buffer.get());
            field.setSize((short) (buffer.get() & 0xff));
            field.setTableName(table.getName());
            field.setTable(table);
            fields.add(field);
        }
        return fields;
    }

    /**
     * Parse the Paradox fields name.
     * 
     * @param table
     *            the Paradox table.
     * @param buffer
     *            the buffer to read of.
     * @param fields
     *            the field list.
     */
    private static void parseTableFieldsName(final ParadoxTable table, final ByteBuffer buffer, final List<ParadoxField> fields) {
        for (int loop = 0; loop < table.getFieldCount(); loop++) {
            final ByteBuffer name = ByteBuffer.allocate(261);

            while (true) {
                final byte c = buffer.get();
                if (c == 0) {
                    break;
                }
                name.put(c);
            }
            name.flip();
            fields.get(loop).setName(table.getCharset().decode(name).toString());
        }
        table.setFields(fields);
    }

    /**
     * Parse the fields order.
     * 
     * @param table
     *            the Paradox table.
     * @param buffer
     *            the buffer to read of.
     */
    private static void parseTableFieldsOrder(final ParadoxTable table, final ByteBuffer buffer) {
        final List<Short> fieldsOrder = new ArrayList<>();
        for (int loop = 0; loop < table.getFieldCount(); loop++) {
            fieldsOrder.add(buffer.getShort());
        }
        table.setFieldsOrder(fieldsOrder);
    }

    /**
     * Parse the table version ID.
     * 
     * @param table
     *            the Paradox table.
     * @param buffer
     *            the buffer to read of.
     */
    private static void parseTableVersionID(final ParadoxTable table, final ByteBuffer buffer) {
        if (table.getVersionId() > 4) {
            // Set the charset
            buffer.position(0x6A);
            table.setCharset(Charset.forName("cp" + buffer.getShort()));

            buffer.position(0x78);
        } else {
            buffer.position(0x58);
        }
    }

    /**
     * Parses the time value.
     * 
     * @param buffer
     *            the buffer to read of.
     * @return the field value.
     */
    private static FieldValue parseTime(final ByteBuffer buffer) {
        FieldValue fieldValue;
        final int a1 = 0x000000FF & buffer.get();
        final int a2 = 0x000000FF & buffer.get();
        final int a3 = 0x000000FF & buffer.get();
        final int a4 = 0x000000FF & buffer.get();
        final long timeInMillis = (a1 << 24 | a2 << 16 | a3 << 8 | a4) & 0x0FFFFFFFL;

        if ((a1 & 0xB0) != 0) {
            final Calendar calendar = new GregorianCalendar(1, 0, 0);
            calendar.add(Calendar.MILLISECOND, (int) timeInMillis);
            final Time time = new Time(calendar.getTimeInMillis());
            fieldValue = new FieldValue(time, Types.TIME);
        } else {
            fieldValue = new FieldValue(Types.TIME);
        }
        return fieldValue;
    }

    /**
     * Parse the VARCHAR value.
     * 
     * @param table
     *            the field table.
     * @param buffer
     *            the buffer to read of.
     * @param field
     *            the field to read.
     * @return the field value.
     */
    private static FieldValue parseVarchar(final ParadoxTable table, final ByteBuffer buffer, final ParadoxField field) {
        final ByteBuffer valueString = ByteBuffer.allocate(Constants.MAX_STRING_SIZE);

        FieldValue fieldValue;
        // reset buffer to zeros
        Arrays.fill(valueString.array(), (byte) 0);

        for (int chars = 0; chars < field.getSize(); chars++) {
            valueString.put(buffer.get());
        }
        fieldValue = new FieldValue(TableData.parseString(valueString, table.getCharset()), Types.VARCHAR);
        return fieldValue;
    }

    /**
     * Read a entire row.
     * 
     * @param table
     *            the table to read of.
     * @param fields
     *            the fields to read.
     * @param buffer
     *            the buffer to read of.
     * @return the row.
     * @throws SQLException
     *             in case of parse errors.
     */
    private static List<FieldValue> readRow(final ParadoxTable table, final Collection<ParadoxField> fields, final ByteBuffer buffer) throws SQLException {
        final List<FieldValue> row = new ArrayList<>();

        for (final ParadoxField field : table.getFields()) {
            FieldValue fieldValue;

            switch (field.getType()) {
            case 1:
                // VARCHAR type
                fieldValue = parseVarchar(table, buffer, field);
                break;
            case 2:
                // DATE type
                fieldValue = parseDate(buffer);
                break;
            case 3:
                fieldValue = parseInt(buffer);
                break;
            case 4:
                fieldValue = parseLong(buffer);
                break;
            case 5: // Currency
            case 6:
                // Number
                fieldValue = parseNumber(buffer);
                break;
            case 9:
                // Logical
                fieldValue = parseBoolean(buffer);
                break;
            case 0xC:
                // Memo type
                fieldValue = parseMemo(table, buffer, field);
                break;
            case 0x14:
                fieldValue = parseTime(buffer);
                break;
            case 0x16:
                // Autoincrement
                fieldValue = parseAutoIncrement(buffer);
                break;
            case 0xD:
            default:
                throw new SQLException("Field type unsupported.", SQLStates.TYPE_NOT_FOUND.getValue());
            }
            // Field filter
            if (fields.contains(field) && fieldValue != null) {
                fieldValue.setField(field);
                row.add(fieldValue);
            }
        }
        return row;
    }
}
