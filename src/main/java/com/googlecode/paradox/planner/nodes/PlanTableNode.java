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
package com.googlecode.paradox.planner.nodes;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.data.TableData;
import com.googlecode.paradox.metadata.ParadoxTable;
import com.googlecode.paradox.parser.nodes.TableNode;
import com.googlecode.paradox.utils.SQLStates;

import java.sql.SQLException;

/**
 * Stores the execution plan table node.
 *
 * @author Leonardo Alves da Costa
 * @version 1.1
 * @since 1.1
 */
public final class PlanTableNode {

    /**
     * The plan alias.
     */
    private String alias;

    /**
     * The plan table.
     */
    private ParadoxTable table;

    /**
     * Creates a new instance.
     */
    public PlanTableNode() {
        super();
    }

    /**
     * Gets the plan alias.
     *
     * @return the plan alias.
     */
    public String getAlias() {
        return this.alias;
    }

    /**
     * Gets the table plan.
     *
     * @return the table plan.
     */
    public ParadoxTable getTable() {
        return this.table;
    }

    /**
     * Sets the plan alias.
     *
     * @param alias the plan alias to set.
     */
    public void setAlias(final String alias) {
        this.alias = alias;
    }

    /**
     * Sets the plan table.
     *
     * @param connection the Paradox connection.
     * @param table      the table data to use.
     * @throws SQLException in case of failures.
     */
    public void setTable(final ParadoxConnection connection, final TableNode table)
            throws SQLException {
        String schemaName = table.getSchemaName();
        if (schemaName == null) {
            schemaName = connection.getSchema();
        }

        final String tableName = table.getName();
        for (final ParadoxTable paradoxTable : TableData.listTables(schemaName, connection)) {
            if (schemaName.equalsIgnoreCase(paradoxTable.getSchemaName())
                    && tableName.equalsIgnoreCase(paradoxTable.getName())) {
                this.table = paradoxTable;
                break;
            }
        }

        if (this.table == null) {
            throw new SQLException("Table name " + table.getName() + " not found.", SQLStates.INVALID_SQL.getValue());
        }

        this.alias = table.getAlias();
    }

    /**
     * Return if the name is referencing this table.
     *
     * @param aliasOrName the alias or table name.
     * @return <code>true</code> if is this table.
     */
    public boolean isThis(final String aliasOrName) {
        if (aliasOrName == null) {
            return false;
        }

        return aliasOrName.equalsIgnoreCase(table.getName()) || aliasOrName.equalsIgnoreCase(alias);
    }
}
