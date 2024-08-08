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

package com.googlecode.paradox.data;

import com.googlecode.paradox.metadata.paradox.ParadoxTable;
import com.googlecode.paradox.results.ParadoxType;

/**
 * Validation field data.
 *
 * @since 1.6.2
 */
public class ValidationField {

    /**
     * The field position.
     */
    private int position;

    /**
     * The field name.
     */
    private String name;

    /**
     * The field picture.
     */
    private String picture;

    /**
     * Field default value.
     */
    private Object defaultValue;

    /**
     * Field minimum value.
     */
    private Object minimumValue;

    /**
     * Field maximum value.
     */
    private Object maximumValue;

    /**
     * Field required status.
     */
    private boolean required;

    /**
     * Field type.
     */
    private ParadoxType type;

    /**
     * Field size.
     */
    private int fieldSize;

    /**
     * Look up destination table.
     */
    private String referencedTableName;

    private ParadoxTable referencedTable;

    /**
     * Lookup all fields status.
     */
    private boolean lookupAllFields;

    /**
     * Lookup help status.
     */
    private boolean lookupHelp;

    /**
     * Creates a new instance.
     */
    public ValidationField() {
        super();
    }

    /**
     * Gets the field position.
     *
     * @return the field position.
     */
    public int getPosition() {
        return position;
    }

    /**
     * Sets the field position.
     *
     * @param position the field position.
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Gets the field name.
     *
     * @return the field name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the field name.
     *
     * @param name the field name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the field picture.
     *
     * @return the field picture.
     */
    public String getPicture() {
        return picture;
    }

    /**
     * Sets the field picture.
     *
     * @param picture the field picture.
     */
    public void setPicture(String picture) {
        this.picture = picture;
    }

    /**
     * Gets the field default value.
     *
     * @return the field default value.
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets the field default value.
     *
     * @param defaultValue the field default value.
     */
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Gets the field type.
     *
     * @return the field type.
     */
    public ParadoxType getType() {
        return type;
    }

    /**
     * Sets the field type.
     *
     * @param type the field type.
     */
    public void setType(ParadoxType type) {
        this.type = type;
    }

    /**
     * Gets the field size.
     *
     * @return the field size.
     */
    public int getFieldSize() {
        return fieldSize;
    }

    /**
     * Sets the field size.
     *
     * @param fieldSize the field size.
     */
    public void setFieldSize(int fieldSize) {
        this.fieldSize = fieldSize;
    }

    /**
     * Gets the field minimum value.
     *
     * @return the field minimum value.
     */
    public Object getMinimumValue() {
        return minimumValue;
    }

    /**
     * Sets the field minimum value.
     *
     * @param minimumValue the field minimum value.
     */
    public void setMinimumValue(Object minimumValue) {
        this.minimumValue = minimumValue;
    }

    /**
     * Gets the field maximum value.
     *
     * @return the field maximum value.
     */
    public Object getMaximumValue() {
        return maximumValue;
    }

    /**
     * Sets the field maximum value.
     *
     * @param maximumValue the field maximum value.
     */
    public void setMaximumValue(Object maximumValue) {
        this.maximumValue = maximumValue;
    }

    /**
     * Gets the table lookup destination table.
     *
     * @return the table lookup destination table.
     */
    public String getReferencedTableName() {
        return referencedTableName;
    }

    /**
     * Sets the table lookup destination table.
     *
     * @param referencedTableName the table lookup destination table.
     */
    public void setReferencedTableName(String referencedTableName) {
        this.referencedTableName = referencedTableName;
    }

    /**
     * Gets the lookup all fields status.
     *
     * @return the lookup all fields status.
     */
    public boolean isLookupAllFields() {
        return lookupAllFields;
    }

    /**
     * Sets the lookup all fields status.
     *
     * @param lookupAllFields the lookup all fields status.
     */
    public void setLookupAllFields(boolean lookupAllFields) {
        this.lookupAllFields = lookupAllFields;
    }

    /**
     * Gets the lookup help status.
     *
     * @return the lookup help status.
     */
    public boolean isLookupHelp() {
        return lookupHelp;
    }

    /**
     * Sets the lookup help status.
     *
     * @param lookupHelp the lookup help status.
     */
    public void setLookupHelp(boolean lookupHelp) {
        this.lookupHelp = lookupHelp;
    }

    /**
     * Gets the required status.
     *
     * @return the required status.
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Sets the required status.
     *
     * @param required the required status.
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

    public ParadoxTable getReferencedTable() {
        return referencedTable;
    }

    public void setReferencedTable(ParadoxTable referencedTable) {
        this.referencedTable = referencedTable;
    }
}
