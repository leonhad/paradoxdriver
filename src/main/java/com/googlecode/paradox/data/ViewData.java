/*
 * ViewData.java
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

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.metadata.ParadoxTable;
import com.googlecode.paradox.metadata.ParadoxView;
import com.googlecode.paradox.utils.SQLStates;
import com.googlecode.paradox.utils.filefilters.ViewFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Read view files (structure).
 *
 * @author Leonardo Alves da Costa
 * @since 1.0
 * @version 1.1
 */
public final class ViewData {
    
    /**
     * Default charset.
     */
    private static final Charset CHARSET = Charset.forName("Cp1250");

    /**
     * Utility class.
     */
    private ViewData() {
        // Utility class.
    }

    /**
     * Gets a field by name.
     *
     * @param table
     *            the fields table.
     * @param name
     *            the field name.
     * @return the {@link ParadoxField}.
     */
    private static ParadoxField getFieldByName(final ParadoxTable table, final String name) {
        ParadoxField originalField = null;
        for (final ParadoxField f : table.getFields()) {
            if (f.getName().equals(name)) {
                originalField = f;
                break;
            }
        }
        if (originalField == null) {
            originalField = new ParadoxField();
            originalField.setType((byte) 1);
        }
        return originalField;
    }

    /**
     * Get the {@link ParadoxTable} by name.
     *
     * @param conn
     *            the connection.
     * @param tableName
     *            the table name.
     * @return the {@link ParadoxTable}.
     * @throws SQLException
     *             if the table doesn't exist.
     */
    private static ParadoxTable getTable(final ParadoxConnection conn, final String tableName) throws SQLException {
        final List<ParadoxTable> tables = TableData.listTables(conn, tableName.trim());
        if (!tables.isEmpty()) {
            return tables.get(0);
        }
        throw new SQLException("Table " + tableName + " not found");
    }

    /**
     * Returns all connections view.
     *
     * @param conn
     *            the connection.
     * @return a list of all views.
     * @throws SQLException
     *             in case of failures.
     */
    public static List<ParadoxView> listViews(final ParadoxConnection conn) throws SQLException {
        return listViews(conn, null);
    }

    /**
     * Gets all view filtered by name.
     *
     * @param conn
     *            the database connection.
     * @param tableName
     *            the name filter.
     * @return all {@link ParadoxView} filtered by name.
     * @throws SQLException
     *             in case of reading errors.
     */
    public static List<ParadoxView> listViews(final ParadoxConnection conn, final String tableName)
            throws SQLException {
        final List<ParadoxView> views = new ArrayList<>();
        final File[] fileList = conn.getDir().listFiles(new ViewFilter(tableName));
        if (fileList != null) {
            for (final File file : fileList) {
                final ParadoxView view = ViewData.loadView(conn, file);
                if (view.isValid()) {
                    views.add(view);
                }
            }
        }
        return views;
    }

    /**
     * Gets a {@link ParadoxView} by {@link File}.
     *
     * @param conn
     *            the database connection.
     * @param file
     *            the {@link File} to read.
     * @return the {@link ParadoxView}.
     * @throws SQLException
     *             in case of reading errors.
     */
    private static ParadoxView loadView(final ParadoxConnection conn, final File file) throws SQLException {
        final ByteBuffer buffer = ByteBuffer.allocate(8192);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        final ParadoxView view = new ParadoxView(file, file.getName());

        try (FileInputStream fs = new FileInputStream(file); FileChannel channel = fs.getChannel()) {
            channel.read(buffer);
            buffer.flip();

            final BufferedReader reader = new BufferedReader(
                    new StringReader(ViewData.CHARSET.decode(buffer).toString()));

            if ("Query".equals(reader.readLine())) {
                return view;
            }

            // ANSWER
            String line = reader.readLine();
            if (line == null) {
                return view;
            }

            // Extra Line
            line = reader.readLine();
            if (line == null) {
                return view;
            }

            line = readLine(reader);

            // FIELDORDER
            if (line != null && line.startsWith("FIELDORDER: ")) {
                line = line.substring("FIELDORDER: ".length());

                final ArrayList<ParadoxField> fields = readFields(conn, reader);
                view.setFieldsOrder(fields);

                // Extra line
                line = reader.readLine();
                if (line == null) {
                    return view;
                }

                // New Line
                line = reader.readLine().trim();
            }

            // SORT
            if (line != null && line.startsWith("SORT: ")) {
                line = line.substring("SORT: ".length());

                final ArrayList<ParadoxField> fields = readFields(conn, reader);
                view.setFieldsSort(fields);

                // Extra Line
                line = reader.readLine();
                if (line == null) {
                    return view;
                }

                // New Line
                line = readLine(reader);
            }

            final ArrayList<ParadoxField> fields = new ArrayList<>();
            while (line != null && !"EndQuery".equals(line)) {
                // Fields
                final String[] flds = line.split("\\|");
                final String table = flds[0].trim();

                for (int loop = 1; loop < flds.length; loop++) {
                    final String name = flds[loop].trim();
                    final ParadoxField field = new ParadoxField();
                    final ParadoxField original = ViewData.getFieldByName(ViewData.getTable(conn, table), name);

                    field.setTableName(table);
                    field.setName(name);
                    field.setType(original.getType());
                    field.setSize(original.getSize());
                    fields.add(field);
                }
                line = reader.readLine();
                final String[] types = line.split("\\|");
                for (int loop = 1; loop < types.length; loop++) {
                    if (types[loop].trim().length() > 0) {
                        final ParadoxField field = fields.get(loop - 1);
                        ViewData.parseExpression(field, types[loop]);
                    }
                }

                // Extra Line
                line = reader.readLine();
                if (line == null) {
                    return view;
                }

                // New Line
                line = readLine(reader);
            }

            view.setFields(fields);
            view.setValid(true);
        } catch (final IOException e) {
            throw new SQLException(e.getMessage(), SQLStates.INVALID_IO, e);
        }
        return view;
    }

    /**
     * Parse a view Paradox expression.
     *
     * @param field
     *            the expression field.
     * @param expression
     *            the expression to parse.
     */
    public static void parseExpression(final ParadoxField field, final String expression) {
        final StringBuilder builder = new StringBuilder(expression.trim());

        if (builder.indexOf("Check") == 0) {
            builder.delete(0, "Check".length() + 1);
            field.setChecked(true);
        }
        if (builder.length() == 0) {
            return;
        }

        if (builder.charAt(0) == '_') {
            final StringBuilder temp = new StringBuilder(builder.length());

            for (final char c : builder.toString().toCharArray()) {
                if (c == ' ' || c == ',') {
                    break;
                }
                temp.append(c);
            }
            final String name = temp.toString();
            builder.delete(0, name.length());
            field.setJoinName(name);
        }
        final String typeTest = builder.toString().trim();
        if (typeTest.toUpperCase().startsWith("AS")) {
            field.setAlias(typeTest.substring(3).trim());
        } else {
            if (typeTest.startsWith(",")) {
                builder.delete(0, 1);
            }
            final int index = builder.toString().toUpperCase().lastIndexOf("AS");
            if (index != -1) {
                field.setExpression(builder.substring(0, index).trim());
                field.setAlias(builder.substring(index + 3).trim());
            } else {
                field.setExpression(builder.toString().trim());
            }
            if (field.getExpression().toUpperCase().startsWith("CALC")) {
                field.setChecked(true);
            }
        }
    }

    /**
     * Read fields from buffer.
     *
     * @param conn
     *            the database connection.
     * @param reader
     *            the buffer to read of.
     * @return the field list.
     * @throws IOException
     *             in case of I/O errors.
     * @throws SQLException
     *             in case of syntax errors.
     */
    private static ArrayList<ParadoxField> readFields(final ParadoxConnection conn, final BufferedReader reader)
            throws IOException, SQLException {
        
        final StringBuilder line = new StringBuilder();
        do {
            line.append(readLine(reader));
        } while (line.toString().endsWith(","));

        ParadoxTable lastTable = null;
        final ArrayList<ParadoxField> fields = new ArrayList<>();
        final String[] cols = line.toString().split("\\,");
        for (final String col : cols) {
            final String[] i = col.split("->");
            final ParadoxField field = new ParadoxField();

            if (i.length < 2) {
                if (lastTable == null) {
                    throw new SQLException("Invalid table.");
                }
                continue;
            } else {
                lastTable = ViewData.getTable(conn, i[0]);
                field.setName(i[1].substring(1, i[1].length() - 1));
            }
            final ParadoxField originalField = ViewData.getFieldByName(lastTable, field.getName());

            field.setType(originalField.getType());
            field.setSize(originalField.getSize());
            fields.add(field);
        }
        return fields;
    }

    /**
     * Read a line from buffer.
     * 
     * @param reader
     *            the buffer to read of.
     * @return a new line.
     * @throws IOException
     *             in case of I/O exception.
     */
    private static String readLine(final BufferedReader reader) throws IOException {
        final String line = reader.readLine();
        return line != null ? line.trim() : null;
    }
}
