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

import com.googlecode.paradox.data.field.BCDField;
import com.googlecode.paradox.metadata.Field;
import com.googlecode.paradox.results.ParadoxType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Stores a field from a table.
 *
 * @since 1.0
 */
public final class ParadoxField extends Field {

    private static final List<ParadoxType> FIXED_PRECISION = Arrays.asList(
            ParadoxType.VARCHAR,
            ParadoxType.CHAR,
            ParadoxType.OLE,
            ParadoxType.BLOB,
            ParadoxType.MEMO,
            ParadoxType.FORMATTED_MEMO,
            ParadoxType.GRAPHIC,
            ParadoxType.BYTES
    );

    /**
     * Creates a new instance. it starts with {@link #getOrderNum()} with one.
     *
     * @param type the Paradox field type.
     */
    public ParadoxField(final ParadoxType type) {
        this(type, 1);
    }

    /**
     * Creates a new instance.
     *
     * @param type     the Paradox field type.
     * @param orderNum order number to start.
     */
    public ParadoxField(final ParadoxType type, final int orderNum) {
        this.type = type;
        this.orderNum = orderNum;
    }

    /**
     * Sets the field size.
     *
     * @param size the size to set.
     */
    @Override
    public void setSize(final int size) {
        this.realSize = size;
        if (type == ParadoxType.BCD) {
            this.precision = size;
            this.realSize = BCDField.BCD_SIZE;
            this.size = BCDField.MAX_DIGITS;
        } else if (FIXED_PRECISION.contains(type)) {
            this.precision = size;
            this.scale = 0;
            this.size = size;
        } else {
            this.precision = this.type.getPrecision();
            this.scale = this.type.getScale();
            this.size = size;
        }
    }

    @Override
    public String toString() {
        if (this.table == null) {
            return this.name;
        }

        return this.table.getName() + "." + this.name;
    }

    @Override
    public ParadoxTable getTable() {
        return (ParadoxTable) table;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ParadoxField that = (ParadoxField) o;
        return orderNum == that.orderNum &&
                size == that.size &&
                type == that.type &&
                Objects.equals(alias, that.alias) &&
                Objects.equals(name, that.name) &&
                Objects.equals(table, that.table);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alias, name, orderNum, size, table, type);
    }
}
