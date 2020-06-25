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
package com.googlecode.paradox.data.field;

import com.googlecode.paradox.data.FieldParser;
import com.googlecode.paradox.data.table.value.FieldValue;
import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.metadata.ParadoxTable;
import com.googlecode.paradox.results.ParadoxFieldType;
import com.googlecode.paradox.utils.SQLStates;
import com.googlecode.paradox.utils.filefilters.TableFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Parses memo fields.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.3
 */
public final class MemoField implements FieldParser {

    private static final FieldValue NULL = new FieldValue(ParadoxFieldType.MEMO.getSQLType());

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean match(final int type) {
        return type == ParadoxFieldType.MEMO.getType();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public FieldValue parse(final ParadoxTable table, final ByteBuffer buffer, final ParadoxField field) throws SQLException {
        final ByteBuffer value = ByteBuffer.allocate(field.getSize());
        for (int chars = 0; chars < field.getSize(); chars++) {
            value.put(buffer.get());
        }

        value.flip();

        buffer.order(ByteOrder.LITTLE_ENDIAN);
        int leader = field.getSize() - 10;
        value.position(leader);

        long beginIndex = buffer.getInt();
        long index = beginIndex & 0xFF;
        long offset = beginIndex & 0xffffff00;

        int size = buffer.getInt();
        int modifier = buffer.getShort();

        // All fields are 9, only graphics is 17.
        int hsize = 9;

        // Graphic field?
        if (false) {
            size -= 12;
            hsize = 17;
        }

        buffer.order(ByteOrder.BIG_ENDIAN);
        if (size <= 0) {
            return NULL;
        } else if (size <= leader) {
            byte[] str = Arrays.copyOf(value.array(), size);
            String strValue = new String(str, table.getCharset());
            return new FieldValue(strValue, ParadoxFieldType.MEMO.getSQLType());
        }

        // Find blob file.
        final File[] fileList = table.getFile().getParentFile().listFiles(new TableFilter(field.getConnection(),
                table.getName(), "mb"));
        if ((fileList == null) || (fileList.length == 0)) {
            throw new SQLException(String.format("Blob file not found for table '%s'", table.getName()),
                    SQLStates.LOAD_DATA.getValue());
        }
        if (fileList.length > 1) {
            throw new SQLException(String.format("Many blob files for table '%s'", table.getName()),
                    SQLStates.LOAD_DATA.getValue());
        }
        File blobFile = fileList[0];
        try (final FileInputStream fs = new FileInputStream(blobFile);
             final FileChannel channel = fs.getChannel()) {
            channel.position(offset);

            final ByteBuffer head = ByteBuffer.allocate(3);
            channel.read(head);
            head.flip();
            byte type = head.get();

            switch (type) {
                case 0x0:
                case 0x4:
                    throw new SQLException("Invalid MB header.");
                case 0x2:
                    System.out.println("2");
                    break;
                case 0x3:
                    System.out.println("3");
                    break;
                default:
                    throw new SQLException("Invalid BLOB header type " + type);
            }

            System.out.println();
        } catch (final IOException ex) {
            throw new SQLException(ex);
        }

        return new FieldValue("teste", ParadoxFieldType.MEMO.getSQLType());
    }
}
