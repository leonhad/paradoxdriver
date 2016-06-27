/*
 * ParadoxPK.java
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
package com.googlecode.paradox.metadata;

/**
 * Stores a primary key definition..
 *
 * @author Leonardo Alves da Costa
 * @since 1.0
 * @version 1.1
 */
public class ParadoxPK extends ParadoxDataFile {

    /**
     * The index field order.
     */
    private int indexFieldNumber;

    /**
     * Creates a new instance.
     */
    public ParadoxPK() {
        super(null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof ParadoxPK) {
            return getName().equals(((ParadoxPK) obj).getName());
        }
        return false;
    }

    /**
     * Gets the index field number.
     * 
     * @return the index field number.
     */
    public int getIndexFieldNumber() {
        return indexFieldNumber;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid() {
        return true;
    }

    /**
     * Sets the index field number.
     * 
     * @param indexFieldNumber
     *            the index field number.
     */
    public void setIndexFieldNumber(final int indexFieldNumber) {
        this.indexFieldNumber = indexFieldNumber;
    }
}
