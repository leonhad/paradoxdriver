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

import java.util.function.Supplier;

public class SoftIndex implements Index {

    private final String name;

    private final boolean unique;

    private final Field[] fields;

    private final Supplier<Integer> total;

    public SoftIndex(final String name, final boolean unique, final Field[] fields, final Supplier<Integer> total) {
        this.name = name;
        this.unique = unique;
        this.fields = fields;
        this.total = total;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isUnique() {
        return unique;
    }

    @Override
    public String getOrder() {
        return "A";
    }

    @Override
    public Field[] getFields() {
        return fields;
    }

    @Override
    public int getRowCount() {
        return total.get();
    }

    @Override
    public int getTotalBlocks() {
        return 0;
    }
}
