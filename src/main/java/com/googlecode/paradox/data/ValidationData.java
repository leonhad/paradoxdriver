/*
 * Copyright (c) 2009 Leonardo Alves da Costa
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
import com.googlecode.paradox.data.filefilters.ValidationFilter;
import com.googlecode.paradox.exceptions.ParadoxException;
import com.googlecode.paradox.metadata.Field;
import com.googlecode.paradox.metadata.Table;
import com.googlecode.paradox.metadata.paradox.ParadoxTable;
import com.googlecode.paradox.metadata.paradox.ParadoxValidation;
import com.googlecode.paradox.results.ParadoxType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Utility class for loading validation files.
 *
 * @since 1.0
 */
public class ValidationData {

    private ValidationData() {
        super();
    }

    /**
     * Gets all tables within a pattern.
     *
     * @param schema         the schema directory.
     * @param table          the original table.
     * @param connectionInfo the connection information.
     * @return the tables filtered.
     */
    public static ParadoxValidation listValidation(final File schema, final ParadoxTable table, final ConnectionInfo connectionInfo) {
        final File[] fileList = schema.listFiles(new ValidationFilter(connectionInfo.getLocale(), table.getName()));

        if (fileList != null) {
            if (fileList.length == 1) {
                try {
                    return load(fileList[0], connectionInfo, table);
                } catch (final SQLException e) {
                    connectionInfo.addWarning(e);
                }
            } else if (fileList.length > 1) {
                connectionInfo.addWarning("Invalid validation list in table " + table.getName());
                return null;
            }
        }

        return null;
    }

    /**
     * Gets the header from a file.
     *
     * @param file           the {@link File} to read.
     * @param connectionInfo the connection information.
     * @return the data file.
     * @throws SQLException in case of reading errors.
     */
    private static ParadoxValidation load(final File file, final ConnectionInfo connectionInfo, final ParadoxTable table) throws SQLException {
        try (FileInputStream fs = new FileInputStream(file); FileChannel channel = fs.getChannel()) {
            final ByteBuffer buffer = ByteBuffer.allocate((int) channel.size());
            buffer.order(ByteOrder.LITTLE_ENDIAN);

            channel.read(buffer);
            buffer.flip();

            ParadoxValidation data = loadHeader(buffer);
            loadFooter(buffer, data, table);

            if (data.getVersionId() < 0x09) {
                // Unsupported file version.
                return null;
            }

            // Validations
            buffer.position(0x35);
            for (int i = 0; i < data.getCount(); i++) {
                int start = buffer.position();

                ValidationField validationField = data.getFields()[buffer.get() & 0xFF];
                Field field = Arrays.stream(table.getFields())
                        .filter(x -> x.getName().equalsIgnoreCase(validationField.getName())).findFirst()
                        .orElseThrow(() -> new ParadoxException(ParadoxException.Error.INVALID_COLUMN, validationField.getName()));

                int pictureSize = buffer.get() & 0x0F;

                int referentialIntegrityAttribute = buffer.get() & 0x0F;

                int tableLookupAttribute = buffer.get() & 0x0F;

                int tableLookupHint = buffer.getInt();

                buffer.position(start + 0x0C);
                int minimumHint = buffer.getInt();
                int maximumHint = buffer.getInt();
                int defaultHint = buffer.getInt();
                int pictureHint = buffer.getInt();

                if (tableLookupHint != 0) {
                    int pos = buffer.position();
                    final ByteBuffer destinationBuffer = ByteBuffer.allocate(0x1A);
                    for (int s = 0; s < 0x1A; s++) {
                        byte read = buffer.get();
                        if (read == 0) {
                            break;
                        }
                        destinationBuffer.put(read);
                    }
                    destinationBuffer.flip();

                    validationField.setDestinationTable(table.getCharset().decode(destinationBuffer).toString());
                    validationField.setLookupAllFields((tableLookupAttribute & 0b01) > 0);
                    validationField.setLookupHelp((tableLookupAttribute & 0b10) > 0);

                    buffer.position(pos + 0x1A + 0x36);
                }

                if (minimumHint != 0) {
                    Object value = ParadoxFieldFactory.parse(table, buffer, field);
                    validationField.setMinimumValue(value);
                }

                if (maximumHint != 0) {
                    Object value = ParadoxFieldFactory.parse(table, buffer, field);
                    validationField.setMaximumValue(value);
                }

                if (defaultHint != 0) {
                    Object value = ParadoxFieldFactory.parse(table, buffer, field);
                    validationField.setDefaultValue(value);
                }

                // Is a mask validation?
                if (pictureSize != 0 && pictureHint != 0) {
                    final ByteBuffer pictureBuffer = ByteBuffer.allocate(pictureSize);
                    for (int s = 0; s < pictureSize; s++) {
                        pictureBuffer.put(buffer.get());
                    }

                    // string ending with zero
                    pictureBuffer.flip();
                    pictureBuffer.limit(pictureSize - 1);

                    validationField.setPicture(table.getCharset().decode(pictureBuffer).toString());
                }
            }

            return data;
        } catch (final IllegalArgumentException | BufferUnderflowException | IOException e) {
            // Don't break in validation erros.
            connectionInfo.addWarning(e);
        }

        return null;
    }

    private static ParadoxValidation loadHeader(final ByteBuffer buffer) {
        ParadoxValidation data = new ParadoxValidation();

        // Unknown
        buffer.get();

        data.setVersionId(buffer.get());
        data.setCount(buffer.get());

        buffer.position(0x09);
        data.setFooterOffset(buffer.getInt());

        return data;
    }

    private static void loadFooter(final ByteBuffer buffer, ParadoxValidation data, final Table table) {
        buffer.position(data.getFooterOffset());
        data.setFieldCount(buffer.getShort());

        // Unknown
        buffer.getInt();

        int[] fieldOrder = new int[data.getFieldCount()];
        for (int i = 0; i < data.getFieldCount(); i++) {
            int campo = buffer.getShort() & 0xFFFF;
            fieldOrder[i] = campo;
        }

        int fieldTypePos = buffer.position();
        // Two bytes per field.
        buffer.position(buffer.position() + data.getFieldCount() * 2);

        // Original table name
        int position = buffer.position();
        final ByteBuffer originalTableName = ByteBuffer.allocate(0x4F);
        byte c;
        while ((c = buffer.get()) != 0 && buffer.position() < position + originalTableName.capacity()) {
            originalTableName.put(c);
        }
        originalTableName.flip();

        data.setOriginalTableName(table.getCharset().decode(originalTableName).toString());
        buffer.position(position + 0x4F);

        // Field name and position
        ValidationField[] fields = new ValidationField[data.getFieldCount()];
        final ByteBuffer name = ByteBuffer.allocate(261);
        for (int i = 0; i < data.getFieldCount(); i++) {
            name.clear();

            while ((c = buffer.get()) != 0) {
                name.put(c);
            }

            name.flip();
            String fieldName = table.getCharset().decode(name).toString();
            final int order = fieldOrder[i] - 1;

            ValidationField field = new ValidationField();
            field.setName(fieldName);
            field.setPosition(order);
            fields[order] = field;
        }
        data.setFields(fields);

        // Field Type
        buffer.position(fieldTypePos);
        for (int i = 0; i < data.getFieldCount(); i++) {
            int fieldType = buffer.get() & 0xFF;
            int valueCount = buffer.get() & 0xFF;

            ValidationField field = data.getFields()[i];
            field.setType(ParadoxType.valueOfVendor(fieldType));
            field.setFieldSize(valueCount);
        }
    }

}
