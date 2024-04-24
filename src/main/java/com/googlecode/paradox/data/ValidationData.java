package com.googlecode.paradox.data;

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.data.filefilters.ValidationFilter;
import com.googlecode.paradox.exceptions.DataError;
import com.googlecode.paradox.exceptions.ParadoxDataException;
import com.googlecode.paradox.metadata.Table;
import com.googlecode.paradox.metadata.paradox.ParadoxValidation;
import com.googlecode.paradox.utils.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
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

    /**
     * Gets all tables within a pattern.
     *
     * @param schema         the schema directory.
     * @param table          the original table.
     * @param connectionInfo the connection information.
     * @return the tables filtered.
     */
    public static List<ParadoxValidation> listValidations(final File schema, final Table table, final ConnectionInfo connectionInfo) {
        final List<ParadoxValidation> tables = new ArrayList<>();
        final File[] fileList = schema.listFiles(new ValidationFilter(connectionInfo.getLocale(), table.getName()));

        if (fileList != null) {
            Arrays.sort(fileList);
            for (final File file : fileList) {
                try {
                    final ParadoxValidation validation = loadHeader(file, connectionInfo, table);
//                    Arrays.stream(validation.getFields()).forEach(field -> field.setTable(validation));
//                    validation.loadIndexes();
                    tables.add(validation);
                } catch (final SQLException e) {
                    connectionInfo.addWarning(e);
                }
            }
        }

        return tables;
    }

    /**
     * Gets the header from a file.
     *
     * @param file           the {@link File} to read.
     * @param connectionInfo the connection information.
     * @return the data file.
     * @throws SQLException in case of reading errors.
     */
    private static ParadoxValidation loadHeader(final File file, final ConnectionInfo connectionInfo, final Table table)
            throws SQLException {
        final ByteBuffer buffer = ByteBuffer.allocate(Constants.MAX_BUFFER_SIZE);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        try (FileInputStream fs = new FileInputStream(file); FileChannel channel = fs.getChannel()) {
            channel.read(buffer);
            buffer.flip();

            ParadoxValidation data = new ParadoxValidation();

            // Unknown
            byte unknown1 = buffer.get();

            data.setVersionId(buffer.get());
            data.setCount(buffer.get());

            buffer.position(0x09);
            int offset = buffer.getShort() & 0xFFFF;

            // Validations
            buffer.position(0x34);
            for (int i = 0; i < data.getCount(); i++) {
                buffer.order(ByteOrder.BIG_ENDIAN);
                int fieldPos = buffer.getShort() & 0xFFFF;
                buffer.order(ByteOrder.LITTLE_ENDIAN);

                int maskSize = buffer.get() & 0x0F;

                // Is a mask validation?
                if (maskSize != 0) {
                    buffer.position(buffer.position() + 0x16);

                    int unknown2 = buffer.getInt();

                    final ByteBuffer maskBuffer = ByteBuffer.allocate(maskSize);
                    for (int s = 1; s < maskSize; s++) {
                        maskBuffer.put(buffer.get());
                    }
                    maskBuffer.flip();

                    String mask = table.getCharset().decode(maskBuffer).toString();
                    System.out.println(String.format("pos %d mask size %d mask %s", fieldPos, maskSize, mask));
                }

            }

            buffer.position(offset);
            data.setFieldCount(buffer.getShort());

            int unknown2 = buffer.getInt();

            int[] fieldOrder = new int[data.getFieldCount()];
            for (int i = 0; i < data.getFieldCount(); i++) {
                int campo = buffer.getShort() & 0xFFFF;
                fieldOrder[i] = campo;
            }

            // Unknown field data
            for (int i = 0; i < data.getFieldCount(); i++) {
                int campo = buffer.getShort() & 0xFFFF;
            }

            int position = buffer.position();
            final ByteBuffer originalTableName = ByteBuffer.allocate(0x4F);
            byte c;
            while ((c = buffer.get()) != 0 && buffer.position() < position + originalTableName.capacity()) {
                originalTableName.put(c);
            }
            originalTableName.flip();

            data.setOriginalTableName(table.getCharset().decode(originalTableName).toString());
            buffer.position(position + 0x4F);

            String[] fields = new String[data.getFieldCount()];
            final ByteBuffer name = ByteBuffer.allocate(261);
            for (int i = 0; i < data.getFieldCount(); i++) {
                name.clear();

                //       byte c;
                while ((c = buffer.get()) != 0) {
                    name.put(c);
                }

                name.flip();
                fields[fieldOrder[i] - 1] = table.getCharset().decode(name).toString();
            }

            data.setFieldNames(fields);

            return data;
        } catch (final BufferUnderflowException | IOException e) {
            throw new ParadoxDataException(DataError.ERROR_LOADING_DATA, e);
        }

    }

}
