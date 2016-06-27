/*
 * FieldNode.java
 *
 * 03/12/2009
 * Copyright (C) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.parser.nodes;

/**
 * Stores the field values.
 *
 * @author Leonardo Alves da Costa
 * @version 1.1
 * @since 1.0
 */
public class FieldNode extends SQLNode {

    /**
     * Field alias (after AS tokens).
     */
    private final String alias;

    /**
     * This field table name.
     */
    private final String tableName;

    /**
     * Stores field values (from select statements).
     *
     * @param tableName
     *            the table name.
     * @param fieldName
     *            the field name.
     * @param alias
     *            the field name alias.
     */
    public FieldNode(final String tableName, final String fieldName, final String alias) {
        super(fieldName);
        this.tableName = tableName;
        this.alias = alias;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAlias() {
        return alias;
    }

    /**
     * Gets the table name.
     * 
     * @return the table name.
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        if (tableName != null) {
            builder.append(tableName);
            builder.append(".");
        }
        builder.append(getName());
        if (!getName().equals(alias)) {
            builder.append(" AS ");
            builder.append(alias);
        }
        return builder.toString();
    }

}
