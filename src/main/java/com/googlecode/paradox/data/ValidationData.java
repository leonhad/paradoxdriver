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
import com.googlecode.paradox.data.charset.CharsetUtil;
import com.googlecode.paradox.data.filefilters.ValidationFilter;
import com.googlecode.paradox.exceptions.ParadoxException;
import com.googlecode.paradox.metadata.Field;
import com.googlecode.paradox.metadata.paradox.ParadoxReferentialIntegrity;
import com.googlecode.paradox.metadata.paradox.ParadoxTable;
import com.googlecode.paradox.metadata.paradox.ParadoxValidation;
import com.googlecode.paradox.results.ParadoxType;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

            ParadoxValidation data = new ParadoxValidation();
            loadHeader(buffer, data);

            if (data.getVersionId() < 0x09) {
                // Unsupported file version.
                return null;
            } else if (table.getBlockChangeCount() != data.getTableChangeCount()) {
                throw new SQLException("The validation file is outdated");
            }

            loadFooter(buffer, data, table);
            loadValidations(buffer, data, table, file.getName());
            loadReferentialIntegrity(buffer, data, table);

            return data;
        } catch (final Exception e) {
            // Don't break in validation erros.
            connectionInfo.addWarning(e);
        }

        return null;
    }

    private static void loadValidations(ByteBuffer buffer, ParadoxValidation data, ParadoxTable table, String fileName) throws SQLException {
        if (data.getCount() == 0) {
            return;
        }

        buffer.position(0x35);
        for (int i = 0; i < data.getCount(); i++) {
            int start = buffer.position();

            int fieldPosition = buffer.get() & 0xFF;

            if (fieldPosition > data.getFieldCount()) {
                throw new ParadoxException(ParadoxException.Error.INVALID_COLUMN_INDEX_FILE, fieldPosition, fileName);
            }
            ValidationField validationField = data.getFields()[fieldPosition];

            Field field = Arrays.stream(table.getFields())
                    .filter(x -> x.getName().equalsIgnoreCase(validationField.getName())).findFirst()
                    .orElseThrow(() -> new ParadoxException(ParadoxException.Error.INVALID_COLUMN_FILE, validationField.getName(), fileName));

            int pictureSize = buffer.get() & 0xFF;
            int required = buffer.get() & 0xFF;
            validationField.setRequired(required == 1);

            int tableLookupAttribute = buffer.get() & 0xFF;

            int tableLookupHint = buffer.getInt();

            buffer.position(start + 0x0C);
            int minimumHint = buffer.getInt();
            int maximumHint = buffer.getInt();
            int defaultHint = buffer.getInt();
            int pictureHint = buffer.getInt();

            loadTableLookup(buffer, table, tableLookupHint, validationField, tableLookupAttribute);

            validationField.setMinimumValue(loadValue(buffer, table, field, minimumHint));
            validationField.setMaximumValue(loadValue(buffer, table, field, maximumHint));
            validationField.setDefaultValue(loadValue(buffer, table, field, defaultHint));

            loadPicture(buffer, table, pictureSize, pictureHint, validationField);
        }
    }

    private static void loadReferentialIntegrity(ByteBuffer buffer, ParadoxValidation data, ParadoxTable table) {
        if (data.getReferentialIntegrityOffset() == 0) {
            return;
        }

        buffer.position(data.getReferentialIntegrityOffset());
        int count = buffer.getShort() & 0xFFFF;

        List<ParadoxReferentialIntegrity> references = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ParadoxReferentialIntegrity reference = new ParadoxReferentialIntegrity();
            int startPosition = buffer.position();

            reference.setName(loadString(buffer, table, 0x40));

            // Ignoring non used fields.
            buffer.position(startPosition + 0xD4);

            reference.setDestinationTableName(loadString(buffer, table, 0x72));

            // Ignoring non used fields.
            buffer.position(startPosition + 0x19A);
            reference.setCascade((buffer.getInt() & 0xFFFFFF) == 1);

            int fieldCount = buffer.getShort() & 0xFFFF;
            reference.setFields(parseFields(buffer, fieldCount));

            buffer.position(startPosition + 0x1C0);
            reference.setDestinationFields(parseFields(buffer, fieldCount));

            // Skip to the end position for next reference.
            buffer.position(startPosition + 0x1E0);

            references.add(reference);
        }

        data.setReferentialIntegrity(references.toArray(new ParadoxReferentialIntegrity[0]));
    }

    private static int[] parseFields(ByteBuffer buffer, int fieldCount) {
        int[] fields = new int[fieldCount];
        for (int loop = 0; loop < fieldCount; loop++) {
            fields[loop] = buffer.getShort() & 0xFFFF;
        }

        return fields;
    }

    private static void loadTableLookup(ByteBuffer buffer, ParadoxTable table, int tableLookupHint, ValidationField validationField, int tableLookupAttribute) {
        if (tableLookupHint != 0) {
            validationField.setReferencedTableName(loadString(buffer, table, 0x1A));
            validationField.setLookupAllFields((tableLookupAttribute & 0b01) > 0);
            validationField.setLookupHelp((tableLookupAttribute & 0b10) > 0);

            // Skip next pointer values
            buffer.position(buffer.position() + 0x36);
        }
    }

    private static String loadString(ByteBuffer buffer, ParadoxTable table, int size) {
        int originalPosition = buffer.position();

        final ByteBuffer stringBuffer = ByteBuffer.allocate(size);
        for (int s = 0; s < size; s++) {
            byte value = buffer.get();
            if (value == 0) {
                break;
            }

            stringBuffer.put(value);
        }

        stringBuffer.limit(size);
        stringBuffer.flip();

        String ret = CharsetUtil.translate(table, stringBuffer);
        buffer.position(originalPosition + size);
        return ret;
    }

    private static Object loadValue(ByteBuffer buffer, ParadoxTable table, Field field, int hint) throws SQLException {
        if (hint != 0) {
            return ParadoxFieldFactory.parse(table, buffer, field);
        }

        return null;
    }

    private static void loadPicture(ByteBuffer buffer, ParadoxTable table, int pictureSize, int pictureHint, ValidationField validationField) {
        if (pictureSize != 0 && pictureHint != 0) {
            final ByteBuffer pictureBuffer = ByteBuffer.allocate(pictureSize);
            for (int s = 0; s < pictureSize; s++) {
                pictureBuffer.put(buffer.get());
            }

            // string ending with zero
            pictureBuffer.flip();
            pictureBuffer.limit(pictureSize - 1);

            validationField.setPicture(CharsetUtil.translate(table, pictureBuffer));
        }
    }

    private static void loadHeader(final ByteBuffer buffer, ParadoxValidation data) {
        data.setTableChangeCount(buffer.get());
        data.setVersionId(buffer.get());
        data.setCount(buffer.get());

        buffer.position(0x09);
        data.setFooterOffset(buffer.getInt());
        data.setReferentialIntegrityOffset(buffer.getInt());
    }

    private static void loadFooter(final ByteBuffer buffer, ParadoxValidation data, final ParadoxTable table) {
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

        data.setOriginalTableName(CharsetUtil.translate(table, originalTableName));
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
            String fieldName = CharsetUtil.translate(table, name);

            ValidationField field = new ValidationField();
            field.setName(fieldName);
            field.setPosition(fieldOrder[i]);
            fields[i] = field;
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
