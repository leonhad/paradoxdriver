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

public class CharsetData {

    private final byte sortOrder;

    private final int codePage;

    private final String sortOrderId;

    private final String name;

    private final Charset charset;

    public CharsetData(int sortOrder, int codePage, String sortOrderId, String name, Charset charset) {
        this.codePage = codePage;
        this.sortOrder = (byte) sortOrder;
        this.name = name;
        this.sortOrderId = sortOrderId;
        this.charset = charset;
    }

    public byte getSortOrder() {
        return sortOrder;
    }

    public int getCodePage() {
        return codePage;
    }

    public String getSortOrderId() {
        return sortOrderId;
    }

    public String getName() {
        return name;
    }

    public Charset getCharset() {
        return charset;
    }
}
