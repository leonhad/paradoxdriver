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

/**
 * A generic index.
 *
 * @version 1.0
 * @since 1.6.0
 */
public interface Index {

    /**
     * Gets the index name.
     *
     * @return the index name.
     */
    String getName();

    /**
     * Gets if this index is unique.
     *
     * @return <code>true</code> if this index is unique.
     */
    boolean isUnique();

    /**
     * FIXME Add order type.
     * Gets the index order type.
     *
     * @return the index order type.
     */
    String getOrder();

    /**
     * Gets the field list.
     *
     * @return the field list.
     */
    Field[] getFields();

    /**
     * Gets the row count.
     *
     * @return the row count.
     */
    int getRowCount();

    /**
     * Gets the file total blocks.
     *
     * @return the file total blocks.
     */
    int getTotalBlocks();

    /**
     * Gets the index type.
     *
     * @return the index type.
     */
    default IndexType type() {
        if (isUnique()) {
            return IndexType.UNIQUE;
        }

        return IndexType.INDEX;
    }

    /**
     * Gets the index definition.
     *
     * @return the index definition.
     */
    default String definition() {
        return "";
    }
}
