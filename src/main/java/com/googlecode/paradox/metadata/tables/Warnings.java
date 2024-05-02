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
package com.googlecode.paradox.metadata.tables;

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.metadata.Field;
import com.googlecode.paradox.metadata.Table;
import com.googlecode.paradox.metadata.TableType;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.utils.Constants;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Connection warnings.
 *
 * @version 1.2
 * @since 1.6.0
 */
public class Warnings implements Table {

    private static final Logger LOGGER = Logger.getLogger(Warnings.class.getName());

    /**
     * The current catalog.
     */
    private final String catalogName;

    /**
     * The connection information.
     */
    private final ConnectionInfo connectionInfo;

    private final Field catalog = new Field("catalog", 0, 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 1);
    private final Field reason = new Field("reason", 0, 0, Constants.MAX_STRING_SIZE, ParadoxType.VARCHAR, this, 2);
    private final Field sqlState = new Field("sql_state", 0, 0, 10, ParadoxType.VARCHAR, this, 3);
    private final Field vendorCode = new Field("vendor_code", 0, 0, 0, ParadoxType.INTEGER, this, 4);
    private final Field stackTrace = new Field("stackTrace", 0, 0, 0, ParadoxType.MEMO, this, 5);

    /**
     * Creates a new instance.
     *
     * @param connectionInfo the connection information.
     * @param catalogName    the catalog name.
     */
    public Warnings(final ConnectionInfo connectionInfo, final String catalogName) {
        this.catalogName = catalogName;
        this.connectionInfo = connectionInfo;
    }

    @Override
    public String getName() {
        return "pdx_warnings";
    }

    @Override
    public TableType type() {
        return TableType.SYSTEM_TABLE;
    }

    @Override
    public Field[] getFields() {
        return new Field[]{
                catalog,
                reason,
                sqlState,
                vendorCode,
                stackTrace
        };
    }

    @Override
    public String getSchemaName() {
        return ConnectionInfo.INFORMATION_SCHEMA;
    }

    @Override
    public int getRowCount() {
        int sum = 0;
        for (SQLWarning warning = connectionInfo.getWarning(); warning != null; warning = warning.getNextWarning()) {
            sum++;
        }

        return sum;
    }

    @Override
    public List<Object[]> load(final Field[] fields) {

        Map<Field, Function<SQLWarning, Object>> map = new HashMap<>();
        map.put(catalog, warning -> catalogName);
        map.put(reason, Throwable::getMessage);
        map.put(sqlState, SQLException::getSQLState);
        map.put(vendorCode, SQLException::getErrorCode);
        map.put(stackTrace, Warnings::printStack);

        final List<Object[]> ret = new ArrayList<>();
        for (SQLWarning warning = connectionInfo.getWarning(); warning != null; warning = warning.getNextWarning()) {
            final Object[] row = Table.getFieldValues(fields, map, warning);
            ret.add(row);
        }

        return ret;
    }

    private static String printStack(SQLWarning warning) {
        try (StringWriter sw = new StringWriter();
             PrintWriter pw = new PrintWriter(sw)) {
            warning.printStackTrace(pw);
            return sw.toString();
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }
}
