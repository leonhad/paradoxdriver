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

            ParadoxValidation data = loadHeader(buffer, connectionInfo, table);
            loadFooter(buffer, data, connectionInfo, table);

            // Validations
            buffer.position(0x35);
            for (int i = 0; i < data.getCount(); i++) {
                int start = buffer.position();

                ValidationField validationField = data.getFields()[buffer.get() & 0xFF];
                int maskSize = buffer.get() & 0x0F;

                Field field = Arrays.stream(table.getFields())
                        .filter(x -> x.getName().equalsIgnoreCase(validationField.getName())).findFirst()
                        .orElseThrow(() -> new ParadoxException(ParadoxException.Error.INVALID_COLUMN, validationField.getName()));

                buffer.position(start + 0x0C);
                int minimumHint = buffer.getInt();
                int maximumHint = buffer.getInt();
                int defaultHint = buffer.getInt();
                int maskHint = buffer.getInt();

                if (minimumHint > 0) {
                    Object value = ParadoxFieldFactory.parse(table, buffer, field);
                    validationField.setMinimumValue(value);
                }

                if (maximumHint > 0) {
                    Object value = ParadoxFieldFactory.parse(table, buffer, field);
                    validationField.setMaximumValue(value);
                }

                if (defaultHint > 0) {
                    Object value = ParadoxFieldFactory.parse(table, buffer, field);
                    validationField.setDefaultValue(value);
                }

                // Is a mask validation?
                if (maskSize != 0 && maskHint != 0) {
                    final ByteBuffer maskBuffer = ByteBuffer.allocate(maskSize);
                    for (int s = 0; s < maskSize; s++) {
                        maskBuffer.put(buffer.get());
                    }

                    // string ending with zero
                    maskBuffer.flip();
                    maskBuffer.limit(maskSize - 1);

                    validationField.setMask(table.getCharset().decode(maskBuffer).toString());
                }
            }

            return data;
        } catch (final IllegalArgumentException | BufferUnderflowException | IOException e) {
            // Don't break in validation erros.
            connectionInfo.addWarning(e);
        }

        return null;
    }

    private static ParadoxValidation loadHeader(final ByteBuffer buffer, final ConnectionInfo connectionInfo, final Table table) throws SQLException {
        ParadoxValidation data = new ParadoxValidation();

        // Unknown
        byte unknown1 = buffer.get();

        data.setVersionId(buffer.get());
        data.setCount(buffer.get());

        buffer.position(0x09);
        data.setFooterOffset(buffer.getInt());

        return data;
    }

    private static void loadFooter(final ByteBuffer buffer, ParadoxValidation data, final ConnectionInfo connectionInfo, final Table table) throws SQLException {
        buffer.position(data.getFooterOffset());
        data.setFieldCount(buffer.getShort());

        int unknown2 = buffer.getInt();

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
