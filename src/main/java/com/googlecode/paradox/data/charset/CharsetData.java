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
package com.googlecode.paradox.data.charset;

import java.nio.charset.Charset;

/**
 * Charset structured data.
 *
 * @since 1.6.2
 */
public class CharsetData {

    /**
     * The sort order.
     */
    private final byte sortOrder;

    /**
     * The code page.
     */
    private final int codePage;

    /**
     * The sort order ID.
     */
    private final String sortOrderId;

    /**
     * The encoding name.
     */
    private final String name;

    /**
     * The Java charset.
     */
    private final Charset charset;

    /**
     * Creates a new instance.
     *
     * @param sortOrder   the sort order.
     * @param codePage    the code page.
     * @param sortOrderId the sort order ID.
     * @param name        the encoding name
     * @param charset     the Java charset.
     */
    public CharsetData(int sortOrder, int codePage, String sortOrderId, String name, Charset charset) {
        this.codePage = codePage;
        this.sortOrder = (byte) sortOrder;
        this.name = name;
        this.sortOrderId = sortOrderId;
        this.charset = charset;
    }

    /**
     * Gets the sort order.
     *
     * @return the sort order.
     */
    public byte getSortOrder() {
        return sortOrder;
    }

    /**
     * Gets the code page.
     *
     * @return the code page.
     */
    public int getCodePage() {
        return codePage;
    }

    /**
     * Gets the sort order ID.
     *
     * @return the sort order ID.
     */
    public String getSortOrderId() {
        return sortOrderId;
    }

    /**
     * Gets the encoding name.
     *
     * @return the encoding name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the Java charset.
     *
     * @return the Java charset.
     */
    public Charset getCharset() {
        return charset;
    }
}
