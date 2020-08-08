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
package com.googlecode.paradox.metadata;

import com.googlecode.paradox.results.ParadoxType;

import java.util.Objects;

public class Field {

    /**
     * Field name.
     */
    protected String name;

    /**
     * The field precision.
     */
    protected int precision;

    /**
     * The field size.
     */
    protected int size;

    /**
     * Paradox field type.
     */
    protected ParadoxType type;

    /**
     * The fields owner.
     */
    protected Table table;

    /**
     * Stores the field alias.
     */
    protected String alias;

    /**
     * Order of field in table/view. The first value is one.
     */
    protected int orderNum;

    /**
     * Creates a new instance.
     */
    public Field() {
        super();
    }

    /**
     * Gets the field alias.
     *
     * @return the field alias.
     */
    public String getAlias() {
        if (this.alias == null) {
            return this.name;
        }

        return this.alias;
    }

    /**
     * Sets the field alias.
     *
     * @param alias the alias to set.
     */
    public void setAlias(final String alias) {
        this.alias = alias;
    }

    /**
     * Gets the field name.
     *
     * @return the field name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the field name.
     *
     * @param name the name to set.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets the field size.
     *
     * @return the size.
     */
    public int getSize() {
        return this.size;
    }

    /**
     * Sets the field size.
     *
     * @param size the field size.
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Gets the field table.
     *
     * @return the field table.
     */
    public Table getTable() {
        return this.table;
    }

    /**
     * Sets the table reference.
     *
     * @param table the table reference.
     */
    public void setTable(final Table table) {
        this.table = table;
    }

    /**
     * Gets the field type.
     *
     * @return the field type.
     */
    public ParadoxType getType() {
        return this.type;
    }

    /**
     * Gets the field precision.
     *
     * @return the field precision.
     */
    public int getPrecision() {
        return precision;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Field field = (Field) o;
        return precision == field.precision &&
                size == field.size &&
                Objects.equals(name, field.name) &&
                type == field.type &&
                Objects.equals(table, field.table) &&
                Objects.equals(alias, field.alias);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, precision, size, type, table, alias);
    }

    /**
     * Gets the field auto increment status.
     *
     * @return true if this field is auto increment.
     */
    public boolean isAutoIncrement() {
        return this.type == ParadoxType.AUTO_INCREMENT;
    }

    /**
     * Gets the SQL field type.
     *
     * @return the SQL field type.
     */
    public int getSqlType() {
        return type.getSQLType();
    }

    /**
     * Gets the field order.
     *
     * @return the field order.
     */
    public int getOrderNum() {
        return this.orderNum;
    }
}
