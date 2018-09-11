/*
 * ViewData.java 03/14/2009 Copyright (C) 2009 Leonardo Alves da Costa This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.data;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.metadata.ParadoxDataFile;
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
import java.util.Locale;

/**
 * Read view files (structure).
 *
 * @author Leonardo Alves da Costa
 * @version 1.1
 * @since 1.0
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
     * Returns all connections view.
     *
     * @param conn the connection.
     * @return a list of all views.
     * @throws SQLException in case of failures.
     */
    public static List<ParadoxView> listViews(final ParadoxConnection conn) throws SQLException {
        return ViewData.listViews(conn, null);
    }

    /**
     * Gets all view filtered by name.
     *
     * @param conn      the database connection.
     * @param tableName the name filter.
     * @return all {@link ParadoxView} filtered by name.
     * @throws SQLException in case of reading errors.
     */
    public static List<ParadoxView> listViews(final ParadoxConnection conn, final String tableName) throws
            SQLException {
        final List<ParadoxView> views = new ArrayList<>();
        final File[] fileList = conn.getDir().listFiles(new ViewFilter(tableName));
        if (fileList != null) {
            for (final File file : fileList) {
                final ParadoxView view = ViewData.loadView(conn, file);
                views.add(view);
            }
        }
        return views;
    }

    /**
     * Fix the view extra line.
     *
     * @param reader the view reader.
     * @return the line fixed.
     * @throws IOException in case of reading errors.
     */
    private static String fixExtraLine(final BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if ((line != null) && line.trim().isEmpty()) {
            line = ViewData.readLine(reader);
        }
        return line;
    }

    /**
     * Get a field by its name.
     *
     * @param field the Paradox table.
     * @param name  the field name.
     * @return the Paradox field.
     */
    private static ParadoxField getField(final ParadoxDataFile field, final String name) {
        for (final ParadoxField f : field.getFields()) {
            if (f != null && f.getName().equalsIgnoreCase(name)) {
                return f;
            }
        }
        return null;
    }

    /**
     * Gets a field by name.
     *
     * @param table the fields table.
     * @param name  the field name.
     * @return the {@link ParadoxField}.
     */
    private static ParadoxField getFieldByName(final ParadoxTable table, final String name) {
        final ParadoxField originalField = ViewData.getField(table, name);
        if (originalField == null) {
            final ParadoxField newField = new ParadoxField();
            newField.setType((byte) 1);
            return newField;
        }
        return originalField;
    }

    /**
     * Get the {@link ParadoxTable} by name.
     *
     * @param conn      the connection.
     * @param tableName the table name.
     * @return the {@link ParadoxTable}.
     * @throws SQLException if the table doesn't exist.
     */
    private static ParadoxTable getTable(final ParadoxConnection conn, final String tableName) throws SQLException {
        final List<ParadoxTable> tables = TableData.listTables(conn, tableName.trim());
        if (!tables.isEmpty()) {
            return tables.get(0);
        }
        throw new SQLException("Table " + tableName + " not found");
    }

    /**
     * Gets a {@link ParadoxView} by {@link File}.
     *
     * @param conn the database connection.
     * @param file the {@link File} to read.
     * @return the {@link ParadoxView}.
     * @throws SQLException in case of reading errors.
     */
    private static ParadoxView loadView(final ParadoxConnection conn, final File file) throws SQLException {
        final ByteBuffer buffer = ByteBuffer.allocate(8192);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        final ParadoxView view = new ParadoxView(file, file.getName());

        try (FileInputStream fs = new FileInputStream(file); FileChannel channel = fs.getChannel()) {
            channel.read(buffer);
            buffer.flip();

            final BufferedReader reader =
                    new BufferedReader(new StringReader(ViewData.CHARSET.decode(buffer).toString()));

            if (!"Query".equals(reader.readLine())) {
                return view;
            }

            // ANSWER
            String line = reader.readLine();
            if (line == null) {
                return view;
            }

            // Extra Line
            line = ViewData.fixExtraLine(reader);

            // FIELDORDER.
            line = ViewData.parseFileOrder(conn, view, reader, line);

            // SORT.
            line = ViewData.parseSort(conn, view, reader, line);

            view.getFields().addAll(ViewData.parseFields(conn, reader, line));
        } catch (final IOException e) {
            throw new SQLException(e.getMessage(), SQLStates.INVALID_IO.getValue(), e);
        }
        return view;
    }

    /**
     * Parses check token.
     *
     * @param field   the field associated.
     * @param builder the builder reader.
     */
    private static void parseCheck(final ParadoxField field, final StringBuilder builder) {
        if (builder.indexOf("Check") == 0) {
            builder.delete(0, "Check".length() + 1);
            field.setChecked(true);
        }
    }

    /**
     * Parses the view fields.
     *
     * @param conn    the connection.
     * @param reader  the reader to load fields.
     * @param oldLine the old line.
     * @return the paradox field list.
     * @throws SQLException in case of parse errors.
     * @throws IOException  in case of I/O errors.
     */
    private static ArrayList<ParadoxField> parseFields(final ParadoxConnection conn, final BufferedReader reader,
            final String oldLine) throws SQLException, IOException {
        String line = oldLine;
        final ArrayList<ParadoxField> fieldList = new ArrayList<>();
        while ((line != null) && !"EndQuery".equals(line)) {
            // Fields
            final String[] fields = line.split("\\|");
            final String table = fields[0].trim();

            for (int loop = 1; loop < fields.length; loop++) {
                final String name = fields[loop].trim();
                final ParadoxField field = new ParadoxField();
                final ParadoxField original = ViewData.getFieldByName(ViewData.getTable(conn, table), name);

                field.setTableName(table);
                field.setName(name);
                field.setType(original.getType());
                field.setSize(original.getSize());
                fieldList.add(field);
            }
            line = reader.readLine();
            if (line == null) {
                break;
            }
            final String[] types = line.split("\\|");
            for (int loop = 1; loop < types.length; loop++) {
                final String type = types[loop].trim();
                if (type.length() > 0) {
                    final ParadoxField field = fieldList.get(loop - 1);
                    ViewData.parseExpression(field, type);
                }
            }

            // Extra Line
            line = ViewData.fixExtraLine(reader);
        }
        return fieldList;
    }

    /**
     * Parses the file order token.
     *
     * @param conn    the connection.
     * @param view    the paradox view.
     * @param reader  the reader to load order.
     * @param oldLine the old line to validate.
     * @return the current line.
     * @throws IOException  in case of I/O errors.
     * @throws SQLException in case of parse errors.
     */
    private static String parseFileOrder(final ParadoxConnection conn, final ParadoxView view,
            final BufferedReader reader, final String oldLine) throws IOException, SQLException {
        String line = oldLine;
        if ((line != null) && line.startsWith("FIELDORDER: ")) {
            final ArrayList<ParadoxField> fields = ViewData.readFields(conn, reader, line);
            final ArrayList<Short> fieldsOrder = new ArrayList<>(fields.size());
            for (final ParadoxField field : fields) {
                ParadoxField fieldByName = ViewData.getField(view, field.getName());
                if (fieldByName != null) {
                    fieldsOrder.add((short) fieldByName.getOrderNum());
                }
            }
            view.setFieldsOrder(fieldsOrder);

            // Extra line.
            line = ViewData.fixExtraLine(reader);
        }
        return line;
    }

    /**
     * Parses the table join names
     *
     * @param field   the field associated.
     * @param builder the build to read of.
     */
    private static void parseJoinName(final ParadoxField field, final StringBuilder builder) {
        if (builder.charAt(0) == '_') {
            final StringBuilder temp = new StringBuilder(builder.length());

            for (final char c : builder.toString().toCharArray()) {
                if ((c == ' ') || (c == ',')) {
                    break;
                }
                temp.append(c);
            }
            final String name = temp.toString();
            builder.delete(0, name.length());
            field.setJoinName(name);
        }
    }

    /**
     * Parses the sort token.
     *
     * @param conn    the connection.
     * @param view    the view.
     * @param reader  the reader to load sort order.
     * @param oldLine the old line to validate.
     * @return the new line.
     * @throws IOException  in case of I/O errors.
     * @throws SQLException in case of parse errors.
     */
    private static String parseSort(final ParadoxConnection conn, final ParadoxView view, final BufferedReader reader,
            final String oldLine) throws IOException, SQLException {
        String line = oldLine;
        if ((line != null) && line.startsWith("SORT: ")) {
            final ArrayList<ParadoxField> fields = ViewData.readFields(conn, reader, line);
            view.setFieldsSort(fields);

            // Extra Line.
            line = ViewData.fixExtraLine(reader);
        }
        return line;
    }

    /**
     * Read fields from buffer.
     *
     * @param conn   the database connection.
     * @param reader the buffer to read of.
     * @return the field list.
     * @throws IOException  in case of I/O errors.
     * @throws SQLException in case of syntax errors.
     */
    private static ArrayList<ParadoxField> readFields(final ParadoxConnection conn, final BufferedReader reader,
            final String firstLine) throws IOException, SQLException {

        final StringBuilder line = new StringBuilder(firstLine.substring(firstLine.indexOf(':') + 1));
        do {
            line.append(ViewData.readLine(reader));
        } while (line.toString().endsWith(","));

        ParadoxTable lastTable = null;
        final ArrayList<ParadoxField> fields = new ArrayList<>();
        final String[] cols = line.toString().split(",");
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
     * @param reader the buffer to read of.
     * @return a new line.
     * @throws IOException in case of I/O exception.
     */
    private static String readLine(final BufferedReader reader) throws IOException {
        final String line = reader.readLine();
        return line != null ? line.trim() : null;
    }

    /**
     * Parse a view Paradox expression.
     *
     * @param field      the expression field.
     * @param expression the expression to parse.
     */
    static void parseExpression(final ParadoxField field, final String expression) {
        final StringBuilder builder = new StringBuilder(expression.trim());

        ViewData.parseCheck(field, builder);
        if (builder.length() == 0) {
            return;
        }

        ViewData.parseJoinName(field, builder);
        final String typeTest = builder.toString().trim();
        if (typeTest.toUpperCase(Locale.US).startsWith("AS")) {
            field.setAlias(typeTest.substring(3).trim());
        } else {
            if (typeTest.charAt(0) == ',') {
                builder.delete(0, 1);
            }
            final int index = builder.toString().toUpperCase(Locale.US).lastIndexOf("AS");
            if (index > -1) {
                field.setExpression(builder.substring(0, index).trim());
                field.setAlias(builder.substring(index + 3).trim());
            } else {
                field.setExpression(builder.toString().trim());
            }
            if (field.getExpression().toUpperCase(Locale.US).startsWith("CALC")) {
                field.setChecked(true);
            }
        }
    }
}
