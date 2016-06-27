/*
 * ParadoxIndex.java
 *
 * 03/14/2009
 * Copyright (C) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.metadata;

import static java.nio.charset.Charset.forName;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores index data.
 * 
 * @author Leonardo Alves da Costa
 * @since 1.0
 * @version 1.1
 */
public class ParadoxIndex extends ParadoxDataFile {

    /**
     * Index charset.
     */
    private Charset charset = forName("Cp437");

    private List<Short> fieldsOrder;

    /**
     * Parent name.
     */
    private String parentName;

    /**
     * Field order ID.
     */
    private String sortOrderID;

    /**
     * Creates a new instance.
     *
     * @param file
     *            the file to read of.
     * @param name
     *            index name.
     */
    public ParadoxIndex(final File file, final String name) {
        super(file, name);
    }

    /**
     * Gets the charset.
     *
     * @return the charset.
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * Gets the file orders.
     *
     * @return the file orders.
     */
    public List<Short> getFieldsOrder() {
        return fieldsOrder;
    }

    /**
     * Gets the index order.
     * 
     * @return the index order.
     */
    public String getOrder() {
        switch (referentialIntegrity) {
        case 0:
        case 1:
        case 0x20:
        case 0x21:
            return "A";
        case 0x10:
        case 0x11:
        case 0x30:
            return "D";
        default:
            return "A";
        }
    }

    /**
     * Gets the parent name.
     * 
     * @return the parent name.
     */
    public String getParentName() {
        return parentName;
    }

    /**
     * Gets the primary key.
     * 
     * @return the primary key.
     */
    public List<ParadoxField> getPrimaryKeys() {
        final ArrayList<ParadoxField> ret = new ArrayList<>();
        for (int loop = 0; loop < primaryFieldCount; loop++) {
            ret.add(fields.get(loop));
        }
        return ret;
    }

    /**
     * Gets the sorter order id.
     * 
     * @return the sorter order id.
     */
    public String getSortOrderID() {
        return sortOrderID;
    }

    /**
     * If this table is valid.
     *
     * @return true if this table is valid.
     */
    @Override
    public boolean isValid() {
        switch (type) {
        case 3:
        case 5:
        case 6:
        case 8:
            return true;
        default:
            return false;
        }
    }

    /**
     * Sets the charset.
     * 
     * @param charset
     *            the charset to set.
     */
    public void setCharset(final Charset charset) {
        this.charset = charset;
    }

    /**
     * Sets the fields order.
     * 
     * @param fieldsOrder
     *            the fields order to set.
     */
    public void setFieldsOrder(final List<Short> fieldsOrder) {
        this.fieldsOrder = fieldsOrder;
    }

    /**
     * Sets the parent name.
     * 
     * @param parentName
     *            the parent name to set.
     */
    public void setParentName(final String parentName) {
        this.parentName = parentName;
    }

    /**
     * Sets the sort order ID.
     * 
     * @param sortOrderID
     *            the sort order ID to set.
     */
    public void setSortOrderID(final String sortOrderID) {
        this.sortOrderID = sortOrderID;
    }
}
