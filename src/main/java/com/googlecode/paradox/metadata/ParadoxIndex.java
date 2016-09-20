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

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.Charset.forName;

/**
 * Stores index data.
 *
 * @author Leonardo Alves da Costa
 * @version 1.1
 * @since 1.0
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
     *         the file to read of.
     * @param name
     *         index name.
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
     * Sets the charset.
     *
     * @param charset
     *         the charset to set.
     */
    public void setCharset(final Charset charset) {
        this.charset = charset;
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
     * Sets the fields order.
     *
     * @param fieldsOrder
     *         the fields order to set.
     */
    public void setFieldsOrder(final List<Short> fieldsOrder) {
        this.fieldsOrder = fieldsOrder;
    }

    /**
     * Gets the index order.
     *
     * @return the index order.
     */
    public String getOrder() {
        if (referentialIntegrity == 0x10 || referentialIntegrity == 0x11 || referentialIntegrity == 0x30) {
            return "D";
        }
        return "A";
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
     * Sets the parent name.
     *
     * @param parentName
     *         the parent name to set.
     */
    public void setParentName(final String parentName) {
        this.parentName = parentName;
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
     * Sets the sort order ID.
     *
     * @param sortOrderID
     *         the sort order ID to set.
     */
    public void setSortOrderID(final String sortOrderID) {
        this.sortOrderID = sortOrderID;
    }
}
