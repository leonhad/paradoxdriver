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

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.data.filefilters.ViewFilter;
import com.googlecode.paradox.exceptions.ParadoxDataException;
import com.googlecode.paradox.metadata.ParadoxDataFile;
import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.metadata.ParadoxTable;
import com.googlecode.paradox.metadata.ParadoxView;
import com.googlecode.paradox.utils.Constants;
import com.googlecode.paradox.utils.Utils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Read view files (structure).
 *
 * @version 1.3
 * @since 1.0
 */
public final class ViewData {

    /**
     * Default charset.
     */
    private static final Charset CHARSET = StandardCharsets.ISO_8859_1;

    private static final Pattern PATTERN_COL = Pattern.compile("->");

    /**
     * Utility class.
     */
    private ViewData() {
        // Utility class.
    }

    /**
     * Returns all connections view.
     *
     * @param currentSchema the current schema file.
     * @param connection    the database connection.
     * @return a list of all views.
     * @throws SQLException in case of failures.
     */
    public static List<ParadoxView> listViews(final File currentSchema, final ParadoxConnection connection) throws
            SQLException {
        return ViewData.listViews(currentSchema, null, connection);
    }

    /**
     * Gets all view filtered by name.
     *
     * @param currentSchema the current schema file.
     * @param viewName      the name filter.
     * @param connection    the database connection.
     * @return all {@link ParadoxView} filtered by name.
     * @throws SQLException in case of reading errors.
     */
    public static List<ParadoxView> listViews(final File currentSchema, final String viewName,
                                              final ParadoxConnection connection) throws SQLException {
        final List<ParadoxView> views = new ArrayList<>();
        final File[] fileList = currentSchema.listFiles(new ViewFilter(connection, viewName));
        if (fileList != null) {
            for (final File file : fileList) {
                final ParadoxView view = ViewData.loadView(file, currentSchema, connection);
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
     * @param dataFile the Paradox table.
     * @param name     the field name.
     * @return the Paradox field.
     */
    private static ParadoxField getField(final ParadoxDataFile dataFile, final String name) {
        for (final ParadoxField f : dataFile.getFields()) {
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
            return new ParadoxField(table.getConnection(), (byte) 0x1);
        }
        return originalField;
    }

    /**
     * Get the {@link ParadoxTable} by name.
     *
     * @param tableName     the table name.
     * @param currentSchema the current schema file.
     * @param connection    the database connection.
     * @return the {@link ParadoxTable}.
     * @throws SQLException if the table doesn't exist.
     */
    private static ParadoxTable getTable(final String tableName, final File currentSchema,
                                         final ParadoxConnection connection) throws SQLException {
        final List<ParadoxTable> tables = TableData.listTables(currentSchema, tableName.trim(), connection);
        if (!tables.isEmpty()) {
            return tables.get(0);
        }

        throw new ParadoxDataException(ParadoxDataException.Error.TABLE_NOT_FOUND);
    }

    /**
     * Gets a {@link ParadoxView} by {@link File}.
     *
     * @param file          the {@link File} to read.
     * @param currentSchema the current schema file.
     * @param connection    the database connection.
     * @return the {@link ParadoxView}.
     * @throws SQLException in case of reading errors.
     */
    private static ParadoxView loadView(final File file, final File currentSchema,
                                        final ParadoxConnection connection) throws SQLException {
        final ByteBuffer buffer = ByteBuffer.allocate(Constants.MAX_BUFFER_SIZE);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        final ParadoxView view = new ParadoxView(file, Utils.removeSuffix(file.getName(), "QBE"), connection);

        try (final FileInputStream fs = new FileInputStream(file); final FileChannel channel = fs.getChannel()) {
            channel.read(buffer);
            buffer.flip();

            try (final BufferedReader reader =
                         new BufferedReader(new StringReader(ViewData.CHARSET.decode(buffer).toString()))) {

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

                // FIELD ORDER.
                line = ViewData.parseFileOrder(view, reader, line, currentSchema, connection);

                // SORT.
                line = ViewData.parseSort(view, reader, line, currentSchema, connection);

                view.setFields(ViewData.parseFields(reader, line, currentSchema, connection));
            }
        } catch (final IOException e) {
            throw new ParadoxDataException(ParadoxDataException.Error.ERROR_LOADING_DATA, e);
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
     * @param reader        the reader to load fields.
     * @param oldLine       the old line.
     * @param currentSchema the current schema file.
     * @param connection    the database connection.
     * @return the paradox field list.
     * @throws SQLException in case of parse errors.
     * @throws IOException  in case of I/O errors.
     */
    private static ParadoxField[] parseFields(final BufferedReader reader, final String oldLine,
                                              final File currentSchema, final ParadoxConnection connection)
            throws SQLException, IOException {
        String line = oldLine;
        final ArrayList<ParadoxField> fieldList = new ArrayList<>();
        while ((line != null) && !"EndQuery".equals(line)) {
            // Fields
            final String[] fields = line.split("\\|");
            final String tableName = fields[0].trim();

            for (int loop = 1; loop < fields.length; loop++) {
                final String name = fields[loop].trim();
                final ParadoxTable table = ViewData.getTable(tableName, currentSchema, connection);
                final ParadoxField original = ViewData.getFieldByName(table, name);
                final ParadoxField field = new ParadoxField(connection, original.getType());
                field.setTable(table);
                field.setName(name);
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
        return fieldList.toArray(new ParadoxField[0]);
    }

    /**
     * Parses the file order token.
     *
     * @param view          the paradox view.
     * @param reader        the reader to load order.
     * @param oldLine       the old line to validate.
     * @param currentSchema the current schema file.
     * @param connection    the database connection.
     * @return the current line.
     * @throws IOException  in case of I/O errors.
     * @throws SQLException in case of parse errors.
     */
    private static String parseFileOrder(final ParadoxView view, final BufferedReader reader, final String oldLine,
                                         final File currentSchema, final ParadoxConnection connection)
            throws IOException, SQLException {
        String line = oldLine;
        if ((line != null) && line.startsWith("FIELDORDER: ")) {
            view.setFields(ViewData.readFields(reader, line, currentSchema, connection));
            final short[] fieldsOrder = new short[view.getFields().length];
            int index = 0;
            for (final ParadoxField field : view.getFields()) {
                ParadoxField fieldByName = ViewData.getField(view, field.getName());
                if (fieldByName != null) {
                    fieldsOrder[index] = (short) fieldByName.getOrderNum();
                    index++;
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
     * @param view       the view.
     * @param reader     the reader to load sort order.
     * @param oldLine    the old line to validate.
     * @param connection the database connection.
     * @return the new line.
     * @throws IOException  in case of I/O errors.
     * @throws SQLException in case of parse errors.
     */
    private static String parseSort(final ParadoxView view, final BufferedReader reader, final String oldLine,
                                    final File currentSchema, final ParadoxConnection connection) throws IOException,
            SQLException {
        String line = oldLine;
        if ((line != null) && line.startsWith("SORT: ")) {
            view.setFieldsSort(ViewData.readFields(reader, line, currentSchema, connection));

            // Extra Line.
            line = ViewData.fixExtraLine(reader);
        }
        return line;
    }

    /**
     * Read fields from buffer.
     *
     * @param reader        the buffer to read of.
     * @param currentSchema the current schema file.
     * @param connection    the database connection.
     * @return the field list.
     * @throws IOException  in case of I/O errors.
     * @throws SQLException in case of syntax errors.
     */
    private static ParadoxField[] readFields(final BufferedReader reader, final String firstLine,
                                             final File currentSchema, final ParadoxConnection connection)
            throws IOException, SQLException {

        final StringBuilder line = new StringBuilder(firstLine.substring(firstLine.indexOf(':') + 1));
        do {
            line.append(ViewData.readLine(reader));
        } while (line.toString().endsWith(","));

        ParadoxTable lastTable = null;
        final ArrayList<ParadoxField> fields = new ArrayList<>();
        final String[] cols = line.toString().split(",");
        for (final String col : cols) {
            final String[] i = PATTERN_COL.split(col);

            String name;
            if (i.length <= 1) {
                if (lastTable == null) {
                    throw new ParadoxDataException(ParadoxDataException.Error.TABLE_NOT_FOUND);
                }
                continue;
            } else {
                lastTable = ViewData.getTable(i[0], currentSchema, connection);
                name = i[1].substring(1, i[1].length() - 1);
            }
            fields.add(ViewData.getFieldByName(lastTable, name));
        }

        return fields.toArray(new ParadoxField[0]);
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
        if (line != null) {
            return line.trim();
        }

        return null;
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
        if (typeTest.toUpperCase(field.getConnection().getLocale()).startsWith("AS")) {
            field.setAlias(typeTest.substring(3).trim());
        } else {
            if (typeTest.charAt(0) == ',') {
                builder.delete(0, 1);
            }
            final int index = builder.toString().toUpperCase(field.getConnection().getLocale()).lastIndexOf("AS");
            if (index > -1) {
                field.setExpression(builder.substring(0, index).trim());
                field.setAlias(builder.substring(index + 3).trim());
            } else {
                field.setExpression(builder.toString().trim());
            }
            if (field.getExpression().toUpperCase(field.getConnection().getLocale()).startsWith("CALC")) {
                field.setChecked(true);
            }
        }
    }
}
