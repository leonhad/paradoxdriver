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

import com.googlecode.paradox.results.ParadoxType;

public class ValidationField {

    private int position;

    private String name;

    private String picture;

    private Object defaultValue;

    private Object minimumValue;

    private Object maximumValue;

    private ParadoxType type;

    private int fieldSize;

    private String destinationTable;

    private boolean lookupAllFields;

    private boolean lookupHelp;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public ParadoxType getType() {
        return type;
    }

    public void setType(ParadoxType type) {
        this.type = type;
    }

    public int getFieldSize() {
        return fieldSize;
    }

    public void setFieldSize(int fieldSize) {
        this.fieldSize = fieldSize;
    }

    public Object getMinimumValue() {
        return minimumValue;
    }

    public void setMinimumValue(Object minimumValue) {
        this.minimumValue = minimumValue;
    }

    public Object getMaximumValue() {
        return maximumValue;
    }

    public void setMaximumValue(Object maximumValue) {
        this.maximumValue = maximumValue;
    }

    public String getDestinationTable() {
        return destinationTable;
    }

    public void setDestinationTable(String destinationTable) {
        this.destinationTable = destinationTable;
    }

    public boolean isLookupAllFields() {
        return lookupAllFields;
    }

    public void setLookupAllFields(boolean lookupAllFields) {
        this.lookupAllFields = lookupAllFields;
    }

    public boolean isLookupHelp() {
        return lookupHelp;
    }

    public void setLookupHelp(boolean lookupHelp) {
        this.lookupHelp = lookupHelp;
    }
}
