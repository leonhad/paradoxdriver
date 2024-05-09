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
package com.googlecode.paradox.metadata.paradox;

/**
 * The paradox referential integrity data.
 *
 * @since 1.6.2
 */
public class ParadoxReferentialIntegrity {

    /**
     * The referential integrity name.
     */
    private String name;

    /**
     * The destination table.
     */
    private String destinationTableName;

    private ParadoxTable destinationTable;

    /**
     * If the constraints is in cascade mode.
     */
    private boolean cascade;

    /**
     * The origin fields.
     */
    private int[] fields;

    /**
     * The destination fields.
     */
    private int[] destinationFields;

    /**
     * Creates a new instance.
     */
    public ParadoxReferentialIntegrity() {
        super();
    }

    /**
     * Gets the referential integrity name.
     *
     * @return the referential integrity name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the referential integrity name.
     *
     * @param name the referential integrity name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the destination table.
     *
     * @return the destination table.
     */
    public String getDestinationTableName() {
        return destinationTableName;
    }

    /**
     * Sets the destination table.
     *
     * @param destinationTableName the destination table.
     */
    public void setDestinationTableName(String destinationTableName) {
        this.destinationTableName = destinationTableName;
    }

    /**
     * Gets if the cascade mode is enabled.
     *
     * @return if the cascade mode is enabled.
     */
    public boolean isCascade() {
        return cascade;
    }

    /**
     * Sets the cascade mode.
     *
     * @param cascade the cascade mode
     */
    public void setCascade(boolean cascade) {
        this.cascade = cascade;
    }

    /**
     * Gets the origin field list.
     *
     * @return the origin field list.
     */
    public int[] getFields() {
        return fields;
    }

    /**
     * Sets the origin field list.
     *
     * @param fields the origin field list.
     */
    public void setFields(int[] fields) {
        this.fields = fields;
    }

    /**
     * Gets the destination fields.
     *
     * @return the destination fields.
     */
    public int[] getDestinationFields() {
        return destinationFields;
    }

    /**
     * Sets the destination fields.
     *
     * @param destinationFields the destination fields.
     */
    public void setDestinationFields(int[] destinationFields) {
        this.destinationFields = destinationFields;
    }

    public ParadoxTable getDestinationTable() {
        return destinationTable;
    }

    public void setDestinationTable(ParadoxTable destinationTable) {
        this.destinationTable = destinationTable;
    }
}
