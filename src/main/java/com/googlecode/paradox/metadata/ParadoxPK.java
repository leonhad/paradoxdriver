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

import com.googlecode.paradox.ParadoxConnection;

/**
 * Stores a primary key definition..
 *
 * @author Leonardo Alves da Costa
 * @version 1.1
 * @since 1.0
 */
public final class ParadoxPK extends ParadoxDataFile {

    /**
     * The index field order.
     */
    private int indexFieldNumber;

    /**
     * Creates a new instance.
     *
     * @param connection the database connection.
     */
    public ParadoxPK(final ParadoxConnection connection) {
        super(null, null, connection);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean equals(final Object obj) {
        return (obj == this) || ((obj != null) && (this.getClass() == obj.getClass())
                && this.getName().equals(((ParadoxPK) obj).getName()));
    }

    /**
     * Gets the index field number.
     *
     * @return the index field number.
     */
    public int getIndexFieldNumber() {
        return this.indexFieldNumber;
    }

    /**
     * Sets the index field number.
     *
     * @param indexFieldNumber the index field number.
     */
    public void setIndexFieldNumber(final int indexFieldNumber) {
        this.indexFieldNumber = indexFieldNumber;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }
}
