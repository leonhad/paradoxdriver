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
package com.googlecode.paradox.planner.nodes;

import com.googlecode.paradox.metadata.Table;
import com.googlecode.paradox.parser.ScannerPosition;
import com.googlecode.paradox.parser.nodes.SQLNode;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * Stores the field values.
 *
 * @since 1.0
 */
public class FieldNode extends SQLNode {

    /**
     * This field table name.
     */
    protected final String tableName;

    /**
     * Field index used in filter values.
     */
    private int index = -1;

    /**
     * The table associated to this field.
     */
    private Table table;

    /**
     * Stores field values (from select statements).
     *
     * @param tableName the table name.
     * @param fieldName the field name.
     * @param position  the current Scanner position.
     */
    public FieldNode(final String tableName, final String fieldName, final ScannerPosition position) {
        super(fieldName, position);
        this.tableName = tableName;
    }

    /**
     * Gets the table name.
     *
     * @return the table name.
     */
    public String getTableName() {
        return this.tableName;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        if (this.tableName != null) {
            builder.append(this.tableName);
            builder.append('.');
        }
        builder.append(this.name);

        if ((this.alias != null) && !this.getName().equals(this.alias)) {
            builder.append(" AS ");
            builder.append(this.alias);
        }

        if (index != -1) {
            builder.append("[");
            builder.append(index);
            builder.append("]");
        }
        return builder.toString();
    }

    /**
     * Gets the index.
     *
     * @return the index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Sets the index.
     *
     * @param index the index.
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Gets the table.
     *
     * @return the table.
     */
    public Table getTable() {
        return table;
    }

    /**
     * Sets the table.
     *
     * @param table the table.
     */
    public void setTable(final Table table) {
        this.table = table;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FieldNode fieldNode = (FieldNode) o;
        return Objects.equals(tableName, fieldNode.tableName) &&
                Objects.equals(alias, fieldNode.alias) &&
                Objects.equals(name, fieldNode.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableName, alias, name);
    }

    @Override
    public Set<FieldNode> getClauseFields() {
        return Collections.singleton(this);
    }
}
