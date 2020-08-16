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
package com.googlecode.paradox.metadata.paradox;

import com.googlecode.paradox.data.field.BCDField;
import com.googlecode.paradox.metadata.Field;
import com.googlecode.paradox.results.ParadoxType;

import java.sql.Types;
import java.util.Objects;

/**
 * Stores a field from a table.
 *
 * @version 1.4
 * @since 1.0
 */
public final class ParadoxField extends Field {

    /**
     * Default number precision.
     */
    private static final int NUMBER_PRECISION = 3;

    /**
     * Default currency precision.
     */
    private static final int CURRENCY_PRECISION = 2;

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
        int sqlType = this.getSqlType();
        if (type == ParadoxType.CURRENCY) {
            this.precision = CURRENCY_PRECISION;
            this.size = size;
        } else if (type == ParadoxType.BCD) {
            this.realSize = BCDField.BCD_SIZE;
            this.precision = size;
            this.size = BCDField.MAX_DIGITS;
        } else if (sqlType == Types.NUMERIC) {
            this.precision = NUMBER_PRECISION;
            this.size = size;
        } else {
            this.size = size;
        }
    }

    /**
     * {@inheritDoc}.
     */
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
