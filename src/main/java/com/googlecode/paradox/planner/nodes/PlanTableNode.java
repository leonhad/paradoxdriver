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

import com.googlecode.paradox.metadata.ParadoxTable;

/**
 * Stores the execution plan table node.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
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
     * @param alias
     *            the plan alias to set.
     */
    public void setAlias(final String alias) {
        this.alias = alias;
    }
    
    /**
     * Sets the plan table.
     *
     * @param table
     *            the plan table to set.
     */
    public void setTable(final ParadoxTable table) {
        this.table = table;
    }
    
}
